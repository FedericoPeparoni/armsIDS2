package ca.ids.abms.modules.overdueinvoices;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.overdueinvoices.interestratecalculation.PenaltyCalculationMethodA;
import ca.ids.abms.modules.overdueinvoices.interestratecalculation.PenaltyCalculationMethodB;
import ca.ids.abms.modules.overdueinvoices.interestratecalculation.PenaltyCalculationUtility;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.error.InterestInvoiceException;
import ca.ids.abms.modules.util.models.Calculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

@Service
@Transactional
public class OverdueBaseService {

    private static final Logger LOG = LoggerFactory.getLogger(OverdueBaseService.class);

    private final BillingLedgerRepository billingLedgerRepository;
    private final AccountRepository accountRepository;
    private final CurrencyService currencyService;
    private final PenaltyCalculationMethodA penaltyCalculationMethodA;
    private final PenaltyCalculationMethodB penaltyCalculationMethodB;
    private final PenaltyCalculationUtility penaltyCalculationUtility;
    private final SystemConfigurationService systemConfigurationService;

    public OverdueBaseService(final BillingLedgerRepository billingLedgerRepository,
                              final AccountRepository accountRepository,
                              final CurrencyService currencyService,
                              final PenaltyCalculationMethodA penaltyCalculationMethodA,
                              final PenaltyCalculationMethodB penaltyCalculationMethodB,
                              final PenaltyCalculationUtility penaltyCalculationUtility,
                              final SystemConfigurationService systemConfigurationService) {
        this.billingLedgerRepository = billingLedgerRepository;
        this.accountRepository = accountRepository;
        this.currencyService = currencyService;
        this.penaltyCalculationMethodA = penaltyCalculationMethodA;
        this.penaltyCalculationMethodB = penaltyCalculationMethodB;
        this.penaltyCalculationUtility = penaltyCalculationUtility;
        this.systemConfigurationService = systemConfigurationService;
    }

    List<BillingLedger> findExistingOverdueInvoices(final Account account) {
        return billingLedgerRepository.findOverdueInvoicesByAccountId(account.getId());
    }

    List<BillingLedger> findExistingOverdueInvoices(final Account account, final Currency currency) {
        return billingLedgerRepository.findOverdueInvoicesByAccountIdAndCurrencyId(account.getId(), currency.getId());
    }

    /**
     * create InvoiceOverduePenalty
     *
     * @param penalizedInvoice - existing overdue invoice - cannot be null
     * @param penaltyAddedToInvoice - billing ledger where penalty is added - can be null - used only when penalties are calculated on "Next invoice"
     * @param newPenaltyPeriodEndDate - cannot be null
     * @param penaltyNumberOfMonths - cannot be null
     * @return new penalties
     */
    InvoiceOverduePenalty createInvoiceOverduePenalty(
        final BillingLedger penalizedInvoice,
        final BillingLedger penaltyAddedToInvoice,
        final LocalDate newPenaltyPeriodEndDate,
        final Integer penaltyNumberOfMonths) {

        if (penalizedInvoice == null || newPenaltyPeriodEndDate == null || penaltyNumberOfMonths == null) {
            LOG.warn(
                "createInvoiceOverduePenalty: Can't create InvoiceOverduePenalty for penalizedInvoice {} due to missing input data",
                penalizedInvoice != null ? penalizedInvoice.getId() : ""
            );
            return null;
        }
        if (penaltyAddedToInvoice != null) {
            if (penalizedInvoice.getId().equals(penaltyAddedToInvoice.getId())) {
			/*
			We can't create penalty for the invoice and add it to the same invoice
			Normally, this case should never happen, because penalty for any existing
			overdue invoice is always added to amountOwing of a new invoice.
			penalizedInvoice is an overdue invoice and
			penaltyAddedToInvoice is a new invoice.
			*/
                return null;
            }

            //make sure the currencies are the same
            if (!penalizedInvoice.getInvoiceCurrency().getId().equals(penaltyAddedToInvoice.getInvoiceCurrency().getId())) {
                LOG.warn(
                    "createInvoiceOverduePenalty: Currency of penalizedInvoice {} is not equal the currency of penaltyAddedToInvoice {}",
                    penalizedInvoice.getId(),
                    penaltyAddedToInvoice.getId()
                );
                return null;
            }
        }
        Account account = accountRepository.getOne(penalizedInvoice.getAccount().getId());
        if (account == null) {
            return null;
        }

        Currency invoiceCurrencyFromDb = currencyService.findOne(penalizedInvoice.getInvoiceCurrency().getId());
        if (invoiceCurrencyFromDb == null) {
            return null;
        }

        Double penaltyRate = account.getMonthlyOverduePenaltyRate();
        if (penaltyRate <= 0.00) {
            // if an interest rate is not specified for the account,
            // then the interest rates defined in the interest rate table are applied as specified

            String applyInterestPenaltyOn = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON);
            try {
                if (applyInterestPenaltyOn.equalsIgnoreCase("Invoice final payment")) {
                    return penaltyCalculationMethodA.createPenaltyByInterestRate(penalizedInvoice, penaltyAddedToInvoice, newPenaltyPeriodEndDate, penaltyNumberOfMonths, invoiceCurrencyFromDb);
                } else {
                    return penaltyCalculationMethodB.createPenaltyByInterestRate(penalizedInvoice, penaltyAddedToInvoice, newPenaltyPeriodEndDate, penaltyNumberOfMonths, invoiceCurrencyFromDb);
                }
            } catch (InterestInvoiceException e) {
                LOG.debug("Cannot create penalty by interest rate because: {}", e.getMessage());
                return null;
            }
        }

        // if an interest rate is specified for the account,
        // then that interest rate is applied with the assumption
        // that it is a monthly rate and is being applied monthly
        return createPenaltyByAccountRate(penalizedInvoice, penaltyAddedToInvoice,
                                          newPenaltyPeriodEndDate, penaltyNumberOfMonths,
                                          invoiceCurrencyFromDb, penaltyRate);
    }

    private InvoiceOverduePenalty createPenaltyByAccountRate(final BillingLedger penalizedInvoice,
                                                             final BillingLedger penaltyAddedToInvoice,
                                                             final LocalDate newPenaltyPeriodEndDate,
                                                             final Integer penaltyNumberOfMonths,
                                                             final Currency invoiceCurrencyFromDb,
                                                             final Double penaltyRate){

        //we need to convert penaltyRate as: E.g. 2% = 2*0.01 = 0.02
        Double penaltyAmountForOneMonth = Calculation.operation(
            penalizedInvoice.getAmountOwing(),
            penaltyRate*0.01,
            Calculation.MathOperator.MULTIPLY,
            invoiceCurrencyFromDb
        );
        Double amountOfInvoiceOverduePenalty = Calculation.operation(
            penaltyAmountForOneMonth,
            Double.valueOf(penaltyNumberOfMonths),
            Calculation.MathOperator.MULTIPLY,
            invoiceCurrencyFromDb
        );

        return penaltyCalculationUtility.createInvoiceOverduePenalty(penalizedInvoice, penaltyAddedToInvoice, newPenaltyPeriodEndDate,
                                            penaltyNumberOfMonths, amountOfInvoiceOverduePenalty, 0d);
    }

    static int calculateNumberOfMonth(final LocalDate dateStart, final LocalDate dateEnd) {
        int result = 0;

        if (dateStart != null && dateEnd != null && dateEnd.isAfter(dateStart)) {
            result = Period.between(dateStart,dateEnd).getMonths();
        }

        return result;
    }
}
