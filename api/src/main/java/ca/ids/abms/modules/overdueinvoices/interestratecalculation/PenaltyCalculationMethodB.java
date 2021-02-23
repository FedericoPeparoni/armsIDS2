package ca.ids.abms.modules.overdueinvoices.interestratecalculation;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.interestrates.InterestRate;
import ca.ids.abms.modules.interestrates.InterestRateService;
import ca.ids.abms.modules.interestrates.enumerate.AppliedRate;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionRepository;
import ca.ids.abms.modules.transactions.error.InterestInvoiceException;
import ca.ids.abms.modules.util.models.Calculation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

/*
  Interest is calculated based on declining balance

  Default interest is charged beginning the day after the invoice due date plus
  the default interest grace period and ends on the invoice payment date

  Punitive interest is charged beginning the day after the invoice due date plus
  the punitive interest grace period and ends on the invoice payment date

  Advance through dates Dx from startDate to endDate using the interest
  rate record valid for the date Dx, accumulating default/punitive interest on
  daily/monthly basis and incrementing date Dx either by 1 day (daily) or 1 month (monthly)
 */
@Service
@Transactional
public class PenaltyCalculationMethodB {

    private static final Logger LOG = LoggerFactory.getLogger(PenaltyCalculationMethodB.class);

    private final SystemConfigurationService systemConfigurationService;
    private final InterestRateService interestRateService;
    private final CurrencyService currencyService;
    private final TransactionRepository transactionRepository;
    private final PenaltyCalculationUtility penaltyCalculationUtility;

    public PenaltyCalculationMethodB(final SystemConfigurationService systemConfigurationService,
                                     final InterestRateService interestRateService,
                                     final CurrencyService currencyService,
                                     final TransactionRepository transactionRepository,
                                     final PenaltyCalculationUtility penaltyCalculationUtility) {
        this.systemConfigurationService = systemConfigurationService;
        this.interestRateService = interestRateService;
        this.currencyService = currencyService;
        this.transactionRepository = transactionRepository;
        this.penaltyCalculationUtility = penaltyCalculationUtility;
    }

    public InvoiceOverduePenalty createPenaltyByInterestRate(final BillingLedger penalizedInvoice,
                                                             final BillingLedger penaltyAddedToInvoice,
                                                             final LocalDate newPenaltyPeriodEndDate,
                                                             final Integer penaltyNumberOfMonths,
                                                             final Currency invoiceCurrency) throws InterestInvoiceException {

        String applyInterestPenaltyOn = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON);
        LocalDate interestStartDate, interestEndDateIncluded;
        String interestPenaltyInvoiceCurrency = null;

        // calculate penalties daily
        if (applyInterestPenaltyOn.equalsIgnoreCase("Daily")) {
            interestStartDate = LocalDate.now();
            interestEndDateIncluded = LocalDate.now();

            // calculate and apply penalties for an account while creating a new invoice
        } else if (applyInterestPenaltyOn.equalsIgnoreCase("Next invoice")) {
            if (penaltyAddedToInvoice == null) {
                return null; // we cannot calculate penalties with the "Next invoice" option without an invoice where penalty is added
            }

            interestStartDate = penalizedInvoice.getPaymentDueDate().toLocalDate().plusDays(1);
            interestEndDateIncluded = penaltyAddedToInvoice.getInvoiceDateOfIssue() != null
                ? penaltyAddedToInvoice.getInvoiceDateOfIssue().toLocalDate() : LocalDate.now();

            // calculate penalties on final payment date. The new interest invoice will include penalties
        } else {
            interestStartDate = penalizedInvoice.getPaymentDueDate().toLocalDate().plusDays(1);
            interestEndDateIncluded = penalizedInvoice.getFinalPaymentDate().toLocalDate();
            interestPenaltyInvoiceCurrency = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.INTEREST_PENALTY_INVOICE_CURRENCY);
        }

        InterestRate interestRate = interestRateService.getInterestRateByStartDate(interestStartDate);

        if (interestRate == null) {
            // there are should be interest rate
            final String details = String.format("No interest rate found on %s", interestStartDate);
            LOG.debug(details);
            throw new InterestInvoiceException(details);
        }

        // Default interest is charged beginning the day after the invoice due date plus
        // the default interest grace period and ends on the invoice payment date
        LocalDate defaultInterestStartDate = interestStartDate.plusDays(interestRate.getDefaultInterestGracePeriod());

        boolean ansp = invoiceCurrency.equals(currencyService.getANSPCurrency());

        double defaultPenalty = calculateDefaultInterest(defaultInterestStartDate, interestEndDateIncluded, ansp, penalizedInvoice);

        if (defaultPenalty < 0) {
            defaultPenalty = 0d;
        }

        // Punitive interest is charged beginning the day after the invoice due date plus
        // the punitive interest grace period and ends on the invoice payment date
        LocalDate punitiveInterestStartDate = interestStartDate.plusDays(interestRate.getPunitiveInterestGracePeriod());

        double punitivePenalty = 0d;

        // there no need to calculate punitive interest if punitiveInterestStartDate is the after as interestEndDateIncluded
        if (!punitiveInterestStartDate.isAfter(interestEndDateIncluded)) {
            punitivePenalty = calculatePunitiveInterest(punitiveInterestStartDate, interestEndDateIncluded, penalizedInvoice);

            if (punitivePenalty < 0) {
                punitivePenalty = 0d;
            }
        }

        if (interestPenaltyInvoiceCurrency != null) {
            defaultPenalty = penaltyCalculationUtility.convertToInterestPenaltyInvoiceCurrency(defaultPenalty, interestPenaltyInvoiceCurrency,
                penalizedInvoice.getInvoiceCurrency(), penalizedInvoice.getFinalPaymentDate());

            punitivePenalty = penaltyCalculationUtility.convertToInterestPenaltyInvoiceCurrency(punitivePenalty, interestPenaltyInvoiceCurrency,
                penalizedInvoice.getInvoiceCurrency(), penalizedInvoice.getFinalPaymentDate());
        }

        return penaltyCalculationUtility.createInvoiceOverduePenalty(penalizedInvoice, penaltyAddedToInvoice, newPenaltyPeriodEndDate,
            penaltyNumberOfMonths, defaultPenalty, punitivePenalty);
    }

    /**
     * Advance through dates Dx from startDate to endDate using the interest
     * rate record valid for the date Dx, accumulating default interest on
     * daily/monthly basis and incrementing date Dx either by 1 day (daily) or 1 month (monthly)
     *
     * If the invoice currency is ANSP, default national interest should be used, otherwise, default foreign interest
     *
     * @param startDate - start date of the penalty calculation (already with grace period for default interest)
     * @param endDate - end date of the penalty calculation (already with grace period for default interest)
     * @return defaultPenalty
     */
    private Double calculateDefaultInterest(final LocalDate startDate, final LocalDate endDate, final boolean ansp, final BillingLedger overdueInvoice) {
        double defaultPenalty = 0d;

        boolean needAnotherInterestRateRecord;
        LocalDate penaltiesStart = startDate;

        do {InterestRate interestRate = interestRateService.getInterestRateByStartDate(penaltiesStart);

            LocalDate interestRateEnd = interestRate.getEndDate();
            needAnotherInterestRateRecord = checkIfAnotherInterestRateNeeded(interestRateEnd, endDate);

            LocalDate end = !needAnotherInterestRateRecord ? endDate : interestRateEnd;

            Double percent = ansp ? interestRate.getDefaultNationalInterestAppliedPercentage() : interestRate.getDefaultForeignInterestAppliedPercentage();
            AppliedRate interestApplication = interestRate.getDefaultInterestApplication();
            defaultPenalty += calculatePenalty(interestApplication, percent, penaltiesStart, end, overdueInvoice);

            if (needAnotherInterestRateRecord) {
                penaltiesStart = getNextPenaltiesStartDate(interestApplication, end, startDate, endDate);
            }
        }
        while (needAnotherInterestRateRecord);

        return defaultPenalty;
    }

    /**
     * Advance through dates Dx from startDate to endDate using the interest
     * rate record valid for the date Dx, accumulating punitive interest on
     * daily/monthly basis and incrementing date Dx either by 1 day (daily) or 1 month (monthly)
     *
     * @param startDate - start date of the penalty calculation (already with grace period for punitive interest)
     * @param endDate - end date of the penalty calculation (already with grace period for punitive interest)
     * @return punitivePenalty
     */
    private double calculatePunitiveInterest(final LocalDate startDate, final LocalDate endDate, final BillingLedger overdueInvoice) {
        double punitivePenalty = 0d;

        boolean needAnotherInterestRateRecord;
        LocalDate penaltiesStart = startDate;

        do {InterestRate interestRate = interestRateService.getInterestRateByStartDate(penaltiesStart);

            LocalDate interestRateEnd = interestRate.getEndDate();
            needAnotherInterestRateRecord = checkIfAnotherInterestRateNeeded(interestRateEnd, endDate);

            LocalDate end = !needAnotherInterestRateRecord ? endDate : interestRateEnd;

            Double percent = interestRate.getPunitiveInterestAppliedPercentage();
            AppliedRate interestApplication = interestRate.getPunitiveInterestApplication();
            punitivePenalty += calculatePenalty(interestApplication, percent, penaltiesStart, end, overdueInvoice);

            if (needAnotherInterestRateRecord) {
                penaltiesStart = getNextPenaltiesStartDate(interestApplication, end, startDate, endDate);
            }
        }
        while (needAnotherInterestRateRecord);

        return punitivePenalty;
    }

    private LocalDate getNextPenaltiesStartDate(final AppliedRate interestApplication, final LocalDate interestRateEnd,
                                                final LocalDate penaltyStartDate, final LocalDate penaltyEndDate) {

        if (interestApplication.equals(AppliedRate.DAILY)) {
            return interestRateEnd.plusDays(1);
        } else {
            long months = MONTHS.between(penaltyStartDate, penaltyEndDate) + 1;
            return penaltyStartDate.plusMonths(months);
        }
    }

    private double calculatePenalty(final AppliedRate interestApplication, final Double percent,
                                    final LocalDate startDate, final LocalDate endDate, final BillingLedger overdueInvoice) {
        if (interestApplication.equals(AppliedRate.DAILY)) {
            return calculateDailyPenalties(percent, startDate, endDate, overdueInvoice);
        } else if (interestApplication.equals(AppliedRate.MONTHLY)) {
            return calculateMonthlyPenalties(percent, startDate, endDate, overdueInvoice);
        }
        return 0d;
    }

    private double calculateDailyPenalties(final Double percent,
                                           final LocalDate penaltyStartDate,
                                           final LocalDate penaltyEndDateIncluded,
                                           final BillingLedger overdueInvoice) {
        double penalties = 0d;
        long daysBetween;
        List<Transaction> invoiceTransactions = transactionRepository.getTransactionsByInvoiceId(overdueInvoice.getId());

        if (invoiceTransactions == null || invoiceTransactions.isEmpty()) {
            daysBetween = DAYS.between(penaltyStartDate, penaltyEndDateIncluded) + 1;
            penalties = Calculation.operation(overdueInvoice.getAmountOwing()*daysBetween,percent*0.01, Calculation.MathOperator.MULTIPLY,
                overdueInvoice.getInvoiceCurrency());
        } else {
            LocalDate fromDate = penaltyStartDate;
            double amountOwing = overdueInvoice.getInvoiceAmount();
            for (Transaction transaction: invoiceTransactions) {
                LocalDate transactionDate = transaction.getPaymentDate() != null ? transaction.getPaymentDate().toLocalDate()
                    : transaction.getTransactionDateTime().toLocalDate();
                double transactionAmount = transaction.getTransactionPayments() != null
                    ? transaction.getTransactionPayments().stream().filter(t -> t.getBillingLedger().equals(overdueInvoice)).mapToDouble(TransactionPayment::getAmount).sum()
                    : transaction.getAmount();
                if (transactionDate.isBefore(fromDate)) {
                    amountOwing += transactionAmount;
                } else if (penaltyEndDateIncluded.isAfter(transactionDate)) {
                    daysBetween = DAYS.between(fromDate, transactionDate);
                    penalties += Calculation.operation(amountOwing*daysBetween,percent*0.01, Calculation.MathOperator.MULTIPLY,
                        overdueInvoice.getInvoiceCurrency());
                    fromDate = transactionDate;
                    amountOwing += transactionAmount;
                } else {
                    daysBetween = DAYS.between(fromDate, penaltyEndDateIncluded) + 1;
                    penalties += Calculation.operation(amountOwing*daysBetween,percent*0.01, Calculation.MathOperator.MULTIPLY,
                        overdueInvoice.getInvoiceCurrency());
                    break;
                }
            }
        }
        return penalties;
    }

    private double calculateMonthlyPenalties(final Double percent,
                                             final LocalDate penaltyStartDate,
                                             final LocalDate penaltyEndDateIncluded,
                                             final BillingLedger overdueInvoice) {
        double penalties = 0d;
        long monthsBetween;
        List<Transaction> invoiceTransactions = transactionRepository.getTransactionsByInvoiceId(overdueInvoice.getId());

        if (invoiceTransactions == null || invoiceTransactions.isEmpty()) {
            monthsBetween = MONTHS.between(penaltyStartDate, penaltyEndDateIncluded) + 1;
            penalties = Calculation.operation(overdueInvoice.getAmountOwing()*monthsBetween,percent*0.01, Calculation.MathOperator.MULTIPLY,
                overdueInvoice.getInvoiceCurrency());
        } else {
            LocalDate fromDate = penaltyStartDate;
            double amountOwing = overdueInvoice.getInvoiceAmount();
            for (Transaction transaction: invoiceTransactions) {
                LocalDate transactionDate = transaction.getPaymentDate() != null ? transaction.getPaymentDate().toLocalDate()
                    : transaction.getTransactionDateTime().toLocalDate();
                double transactionAmount = transaction.getTransactionPayments() != null
                    ? transaction.getTransactionPayments().stream().filter(t -> t.getBillingLedger().equals(overdueInvoice)).mapToDouble(TransactionPayment::getAmount).sum()
                    : transaction.getAmount();
                if (transactionDate.isBefore(fromDate)) {
                    amountOwing += transactionAmount;
                } else {
                    penalties += Calculation.operation(amountOwing,percent*0.01, Calculation.MathOperator.MULTIPLY,
                        overdueInvoice.getInvoiceCurrency()) * 1;
                    fromDate = fromDate.plusMonths(1);
                    if (fromDate.isAfter(penaltyEndDateIncluded)) {
                        break;
                    }
                }
            }
        }

        return penalties;
    }

    private boolean checkIfAnotherInterestRateNeeded (final LocalDate interestRateEnd, final LocalDate penaltyEndDate){
        boolean needAnotherInterestRateRecord = false;

        if (interestRateEnd != null) {
            needAnotherInterestRateRecord = penaltyEndDate.isAfter(interestRateEnd);
        }
        return needAnotherInterestRateRecord;
    }
}
