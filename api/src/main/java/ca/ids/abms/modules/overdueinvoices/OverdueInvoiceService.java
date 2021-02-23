package ca.ids.abms.modules.overdueinvoices;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.billings.InvoiceOverduePenaltyRepository;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.util.models.Calculation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class OverdueInvoiceService {
    private static final Logger LOG = LoggerFactory.getLogger(OverdueInvoiceService.class);

    private final CurrencyService currencyService;
    private final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository;
    private final AccountRepository accountRepository;
    private final OverdueBaseService overdueBaseService;

    public OverdueInvoiceService(
        CurrencyService currencyService,
        InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
        AccountRepository accountRepository,
        OverdueBaseService overdueBaseService) {

        this.currencyService = currencyService;
        this.invoiceOverduePenaltyRepository = invoiceOverduePenaltyRepository;
        this.accountRepository = accountRepository;
        this.overdueBaseService = overdueBaseService;
    }

    public List<Account> findAllAccountsWithOverdueInvoices() {
        LOG.debug("Retrieving accounts with overdue invoices for overdue invoice processor.");
        return accountRepository.findAllWithOverdueInvoices();
    }

    public void removeAttachedPenalties(final Integer invoiceId, boolean penaltiesCheckedDaily) {
        if (invoiceId != null) {
            int detachedPenalties;
            if (penaltiesCheckedDaily) {
                detachedPenalties = invoiceOverduePenaltyRepository.removePenaltiesAttachedDaily(invoiceId);
            } else {
                detachedPenalties = invoiceOverduePenaltyRepository.removePenaltiesAppliedAtInvoiceCreationTime(invoiceId);
            }
            if (detachedPenalties > 0) {
                LOG.debug("Detached penalties {} for the rejected invoice with id {}", detachedPenalties, invoiceId);
            } else {
                LOG.debug("No penalties found to detach from the rejected invoice with id {}", invoiceId);
            }
        }
    }

    /**
     * In case of overdue invoice applied daily, the penalties have already been added to the balance. The invoice will
     * contain just a list of penalties applied before.
     *
     * @param billingLedger that will contain a reminder of penalties applied
     * @return billingLedger for chaining
     */
    public BillingLedger attachInvoiceOverduePenaltyDaily (BillingLedger billingLedger) {

        // validate if account credit can be applied
        if (!validBillingLedgerForPenalties(billingLedger))
            return billingLedger;

        // invoice state must be "published"
        if (billingLedger.getInvoiceStateType().equals(InvoiceStateType.PAID.toValue())
            || billingLedger.getInvoiceStateType().equals(InvoiceStateType.VOID.toValue())) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Invoice must be new, approved or published.",
                billingLedger.getId());
            return billingLedger;
        }

        // invoice currency must be valid
        Currency invoiceCurrencyFromDb = currencyService.findOne(billingLedger.getInvoiceCurrency().getId());
        if (invoiceCurrencyFromDb == null) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Invoice currency does not exist.",
                billingLedger.getId());
            return billingLedger;
        }

        List<InvoiceOverduePenalty> appliedPenaltiesToAttach = invoiceOverduePenaltyRepository.getAllPenaltiesToApply(
            billingLedger.getAccount().getId(), billingLedger.getInvoiceCurrency().getId());

        if (CollectionUtils.isEmpty(appliedPenaltiesToAttach)) {
            LOG.debug("For the account {} there aren't penalties to attach to the invoice {}",
                billingLedger.getAccount().getName(), billingLedger.getInvoiceNumber());
            return billingLedger;
        }
        double totalAmountOfAllPenaltiesAttached = 0.0d;
        for (final InvoiceOverduePenalty penalty : appliedPenaltiesToAttach) {
            penalty.setPenaltyAddedToInvoice(billingLedger);
            invoiceOverduePenaltyRepository.save(penalty);
            double penaltyAmount = penalty.getDefaultPenaltyAmount() + penalty.getPunitivePenaltyAmount();
            LOG.debug("Attached a penalty of {} to the invoice {}", penaltyAmount, billingLedger.getInvoiceNumber());
            if (penaltyAmount > 0) {
                totalAmountOfAllPenaltiesAttached += penaltyAmount;
            }
        }
        if (totalAmountOfAllPenaltiesAttached > 0) {
            //update invoice amount
            double updatedInvoiceAmount = Calculation.operation(
                billingLedger.getInvoiceAmount(),
                totalAmountOfAllPenaltiesAttached,
                Calculation.MathOperator.ADD,
                invoiceCurrencyFromDb
            );
            billingLedger.setInvoiceAmount(updatedInvoiceAmount);

            //update amountOwing
            double  updatedAmountOwing = Calculation.operation(
                billingLedger.getAmountOwing(),
                totalAmountOfAllPenaltiesAttached,
                Calculation.MathOperator.ADD,
                invoiceCurrencyFromDb
            );
            billingLedger.setAmountOwing(updatedAmountOwing);
            LOG.debug("The invoice {} with invoice_amount {}{} has been updated with amount_owing of {}{} (as the invoice_amount plus the total penalties of {}{})",
                billingLedger.getInvoiceNumber(), billingLedger.getInvoiceAmount(), invoiceCurrencyFromDb.getCurrencyCode(),
                updatedAmountOwing, invoiceCurrencyFromDb.getCurrencyCode(), totalAmountOfAllPenaltiesAttached,
                invoiceCurrencyFromDb.getCurrencyCode());
        } else {
            LOG.debug("No penalties to add to the invoice {} with invoice_amount {}{} and amount_owing of {}{}",
                billingLedger.getInvoiceNumber(), billingLedger.getInvoiceAmount(), invoiceCurrencyFromDb.getCurrencyCode(),
                billingLedger.getAmountOwing(), invoiceCurrencyFromDb.getCurrencyCode());
        }

        // return updated billing ledger for chaining
        return billingLedger;
    }

    /**
     * Calculate and apply penalty if applicable.
     * Penalty when applied will change invoice amount and amount owing of the billingLedger.
     *
     * @param billingLedger that will contain a reminder of penalties applied
     * return billingLedger for chaining
     */
    public BillingLedger applyInvoiceOverduePenalty(final BillingLedger billingLedger) {

        // validate if account credit can be applied
        if (!validBillingLedgerForPenalties(billingLedger))
            return billingLedger;

        // invoice state must be "published"
        if (!billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Invoice must be published.",
                billingLedger.getId());
            return billingLedger;
        }

        // invoice currency must be valid
        Currency invoiceCurrencyFromDb = currencyService.findOne(billingLedger.getInvoiceCurrency().getId());
        if (invoiceCurrencyFromDb == null) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Invoice currency does not exist.",
                billingLedger.getId());
            return billingLedger;
        }

        Double totalAmountOfAllPenaltiesCreated = calculateAndCreatePenaltiesForOverdueInvoices(billingLedger, invoiceCurrencyFromDb);

        if (totalAmountOfAllPenaltiesCreated > 0) {

            Double totalPenaltyAmountAppliedToInvoice = invoiceOverduePenaltyRepository.getTotalPenaltyAmountAppliedToInvoice(billingLedger.getId());
            if (totalPenaltyAmountAppliedToInvoice != null && totalPenaltyAmountAppliedToInvoice > 0) {

                // update invoice amount
                billingLedger.setInvoiceAmount(Calculation.operation(
                    billingLedger.getInvoiceAmount(),
                    totalPenaltyAmountAppliedToInvoice,
                    Calculation.MathOperator.ADD,
                    invoiceCurrencyFromDb
                ));

                // update amountOwing
                billingLedger.setAmountOwing(Calculation.operation(
                    billingLedger.getAmountOwing(),
                    totalPenaltyAmountAppliedToInvoice,
                    Calculation.MathOperator.ADD,
                    invoiceCurrencyFromDb
                ));
            }
        }

        // return updated billing ledger for chaining
        return billingLedger;
    }

    public Double calculateAndCreatePenaltiesForOverdueInvoices(BillingLedger billingLedger, Currency invoiceCurrency) {
        List<BillingLedger> existingOverdueInvoiceList = overdueBaseService.findExistingOverdueInvoices(
            billingLedger.getAccount(), invoiceCurrency);

        Double totalAmountOfAllPenaltiesCreated = 0.00;

        if (existingOverdueInvoiceList != null && !existingOverdueInvoiceList.isEmpty()) {

            //check if we can create a penalty for any existing overdue invoice
            for (BillingLedger existingOverdueInvoice : existingOverdueInvoiceList) { //start loop
                InvoiceOverduePenalty existingInvoiceOverduePenalty = invoiceOverduePenaltyRepository
                    .findMostRecentInvoiceOverduePenaltyByInvoiceId(existingOverdueInvoice.getId());

                if (existingInvoiceOverduePenalty == null) {
                    //CASE 1: InvoiceOverduePenalty does not exist, so we can create a new penalty

                    Double amountOfPenalty = createNewPenalty(
                        existingOverdueInvoice,
                        existingOverdueInvoice.getPaymentDueDate().toLocalDate(),
                        billingLedger
                    );

                    if (amountOfPenalty != null && amountOfPenalty > 0) {
                        totalAmountOfAllPenaltiesCreated += amountOfPenalty;
                    }

                } else {
                    //CASE 2: InvoiceOverduePenalty exists and penaltyPeriodEndDate < current date, so we can create penalty again
                    LocalDate lastPenaltyPeriodEndDate = existingInvoiceOverduePenalty.getPenaltyPeriodEndDate().toLocalDate();

                    if (lastPenaltyPeriodEndDate.isBefore(billingLedger.getCreatedAt().toLocalDate())) {

                        Double amountOfPenalty = createNewPenalty(
                            existingOverdueInvoice,
                            lastPenaltyPeriodEndDate,
                            billingLedger
                        );

                        if (amountOfPenalty != null && amountOfPenalty > 0) {
                            totalAmountOfAllPenaltiesCreated += amountOfPenalty;
                        }
                    }
                }
            } //end loop
        }
        return totalAmountOfAllPenaltiesCreated;
    }

    public Double createNewPenalty(BillingLedger existingOverdueInvoice, LocalDate dueDate, BillingLedger billingLedger) {
        Integer penaltyNumberOfMonth = OverdueBaseService.calculateNumberOfMonth(dueDate, billingLedger.getCreatedAt().toLocalDate()) + 1;

        LocalDate newPenaltyPeriodEndDate = dueDate.plusMonths(penaltyNumberOfMonth);

        final InvoiceOverduePenalty invoiceOverduePenalty = overdueBaseService.createInvoiceOverduePenalty(
            existingOverdueInvoice,
            billingLedger,
            newPenaltyPeriodEndDate,
            penaltyNumberOfMonth
        );
        return invoiceOverduePenalty != null ? (invoiceOverduePenalty.getDefaultPenaltyAmount() + invoiceOverduePenalty.getPunitivePenaltyAmount()) : 0.0d;
    }

    public InvoiceOverduePenalty createNewPenalty(BillingLedger existingOverdueInvoice, LocalDate finalPaymentDate) {
        Integer penaltyNumberOfMonth = OverdueBaseService.calculateNumberOfMonth(existingOverdueInvoice.getPaymentDueDate().toLocalDate(), finalPaymentDate) + 1;

        return overdueBaseService.createInvoiceOverduePenalty(existingOverdueInvoice,null, finalPaymentDate, penaltyNumberOfMonth);
    }

    /**
     * Validate if account credit can be applied to a billing ledger.
     */
    private Boolean validBillingLedgerForPenalties(final BillingLedger billingLedger) {

        Boolean result = true;

        // validate billing ledger is persisted by id value
        if (billingLedger == null || billingLedger.getId() == null) {
            LOG.warn("Cannot apply penalties to invoice as it does not exist.");
            result = false;
        }

        // validate account and currency
        else if (billingLedger.getInvoiceStateType() == null
            || billingLedger.getAccount() == null
            || billingLedger.getAccount().getId() == null
            || billingLedger.getInvoiceCurrency() == null
            || billingLedger.getInvoiceCurrency().getId() == null) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Invalid invoice data.", billingLedger.getId());
            result = false;
        }

        // invoice amount_owing should be greater than 0 and not overdue invoice type
        else if ((billingLedger.getAmountOwing() == null || billingLedger.getAmountOwing() <= 0)
            && !billingLedger.getInvoiceType().equalsIgnoreCase(InvoiceType.OVERDUE.toValue())) {
            LOG.warn("Cannot apply penalties to invoice '{}'. Amount owing should be greater than 0",
                billingLedger.getId());
            result = false;
        }

        return result;
    }
}
