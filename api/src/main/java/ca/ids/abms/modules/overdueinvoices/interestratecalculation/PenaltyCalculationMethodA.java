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
  Interest is calculated based on each payment amount

  Default interest is charged beginning the day after the invoice due date plus
  the default interest grace period and ends on the invoice payment date

  Punitive interest is charged beginning the day after the invoice due date plus
  the punitive interest grace period and ends on the invoice payment date

  Interest grace period is taken from the previous day of the first penalized payment
 */
@Service
@Transactional
public class PenaltyCalculationMethodA {

    private static final Logger LOG = LoggerFactory.getLogger(PenaltyCalculationMethodA.class);

    private final SystemConfigurationService systemConfigurationService;
    private final InterestRateService interestRateService;
    private final CurrencyService currencyService;
    private final TransactionRepository transactionRepository;
    private final PenaltyCalculationUtility penaltyCalculationUtility;

    public PenaltyCalculationMethodA(final SystemConfigurationService systemConfigurationService,
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

        if (!applyInterestPenaltyOn.equalsIgnoreCase("Invoice final payment")) {
            return null;
        }

        // calculate penalties on final payment date. The new interest invoice will include penalties
        LocalDate interestStartDate = penalizedInvoice.getPaymentDueDate().toLocalDate().plusDays(1);
        LocalDate interestEndDateIncluded = penalizedInvoice.getFinalPaymentDate().toLocalDate();
        String interestPenaltyInvoiceCurrency = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.INTEREST_PENALTY_INVOICE_CURRENCY);
        boolean ansp = invoiceCurrency.equals(currencyService.getANSPCurrency());

        Penalties penalties = calculatePenalties(interestStartDate, interestEndDateIncluded, ansp, penalizedInvoice, applyInterestPenaltyOn);

        double defaultPenalty = penalties.defaultPenalty;
        double punitivePenalty = penalties.punitivePenalty;

        if (defaultPenalty < 0) {
            defaultPenalty = 0d;
        }

        if (punitivePenalty < 0) {
            punitivePenalty = 0d;
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

    private Penalties calculatePenalties(final LocalDate interestStartDate,
                                         final LocalDate interestEndDateIncluded,
                                         final boolean ansp,
                                         final BillingLedger overdueInvoice,
                                         final String applyInterestPenaltyOn) throws InterestInvoiceException {

        Penalties penalties = new Penalties();
        List<Transaction> invoiceTransactions = transactionRepository.getTransactionsByInvoiceId(overdueInvoice.getId());

        if (invoiceTransactions == null || invoiceTransactions.isEmpty()) {
            // there are should be payments with "Invoice final payment" option
            final String details = String.format("No payments found for the invoice# %s", overdueInvoice.getId());
            LOG.debug(details);
            throw new InterestInvoiceException(details);
        }

        calculatePenaltiesWithTransactionPayments(invoiceTransactions, overdueInvoice, interestStartDate, ansp, penalties);

        return penalties;
    }

    private void calculatePenaltiesWithTransactionPayments(final List<Transaction> invoiceTransactions,
                                                           final BillingLedger overdueInvoice,
                                                           final LocalDate interestStartDate,
                                                           final boolean ansp,
                                                           Penalties penalties) throws InterestInvoiceException {

        // Default interest is charged beginning the day after the invoice due date
        // plus the default interest grace period and ends on the invoice payment date
        LocalDate defaultInterestStartDate = null;

        // Punitive interest is charged beginning the day after the invoice due date
        // plus the punitive interest grace period and ends on the invoice payment date
        LocalDate punitiveInterestStartDate = null;

        for (Transaction transaction: invoiceTransactions) {
            LocalDate transactionDate = transaction.getPaymentDate() != null ? transaction.getPaymentDate().toLocalDate()
                : transaction.getTransactionDateTime().toLocalDate();

            double transactionAmount = -(transaction.getTransactionPayments() != null
                ? transaction.getTransactionPayments().stream().filter(t -> t.getBillingLedger().equals(overdueInvoice)).mapToDouble(TransactionPayment::getAmount).sum()
                : transaction.getAmount());

            if (!transactionDate.isAfter(interestStartDate)) {
                continue;
            }

            InterestRate interestRate = interestRateService.getInterestRateByStartDate(transactionDate.minusDays(1));

            if (interestRate == null) {
                // there are should be interest rate
                final String details = String.format("No interest rate found on %s", transactionDate.minusDays(1));
                LOG.debug(details);
                throw new InterestInvoiceException(details);
            }

            // --Calculating default penalties--
            defaultInterestStartDate = defaultInterestStartDate == null ? interestStartDate.plusDays(interestRate.getDefaultInterestGracePeriod()) : defaultInterestStartDate;

            if (defaultInterestStartDate.isBefore(transactionDate)) {
                penalties.defaultPenalty += calculateDefaultPenalty(interestRate, defaultInterestStartDate, transactionDate,
                    ansp, transactionAmount, overdueInvoice.getInvoiceCurrency());
            }

            // --Calculating punitive penalties--
            punitiveInterestStartDate = punitiveInterestStartDate == null ? interestStartDate.plusDays(interestRate.getPunitiveInterestGracePeriod()) : punitiveInterestStartDate;

            if (punitiveInterestStartDate.isBefore(transactionDate)) {
                penalties.punitivePenalty += calculatePunitivePenalty(interestRate, punitiveInterestStartDate, transactionDate,
                    transactionAmount, overdueInvoice.getInvoiceCurrency());
            }
        }
    }

    private double calculateDefaultPenalty(final InterestRate interestRate,
                                           final LocalDate defaultInterestStartDate,
                                           final LocalDate interestEndDateIncluded,
                                           final boolean ansp,
                                           final double overdueAmount,
                                           final Currency overdueInvoiceCurrency) {

        double defaultPenalty = 0d;
        Double defaultPercent = ansp ? interestRate.getDefaultNationalInterestAppliedPercentage() : interestRate.getDefaultForeignInterestAppliedPercentage();
        AppliedRate defaultInterestApplication = interestRate.getDefaultInterestApplication();

        long defaultPeriodBetween = (defaultInterestApplication.equals(AppliedRate.DAILY)? DAYS.between(defaultInterestStartDate, interestEndDateIncluded) :
            MONTHS.between(defaultInterestStartDate, interestEndDateIncluded)) + 1;

        if (defaultPeriodBetween > 0)
            defaultPenalty = calculatePenaltyAmount(overdueAmount, defaultPeriodBetween, defaultPercent, overdueInvoiceCurrency);

        return defaultPenalty;
    }

    private double calculatePunitivePenalty(final InterestRate interestRate,
                                            final LocalDate punitiveInterestStartDate,
                                            final LocalDate interestEndDateIncluded,
                                            final double overdueAmount,
                                            final Currency overdueInvoiceCurrency) {

        double punitivePenalty = 0d;

        Double punitivePercent = interestRate.getPunitiveInterestAppliedPercentage();
        AppliedRate punitiveInterestApplication = interestRate.getPunitiveInterestApplication();

        long punitivePeriodBetween = (punitiveInterestApplication.equals(AppliedRate.DAILY)? DAYS.between(punitiveInterestStartDate, interestEndDateIncluded) :
            MONTHS.between(punitiveInterestStartDate, interestEndDateIncluded)) + 1;

        if (punitivePeriodBetween > 0)
            punitivePenalty = calculatePenaltyAmount(overdueAmount, punitivePeriodBetween, punitivePercent, overdueInvoiceCurrency);

        return punitivePenalty;
    }

    private double calculatePenaltyAmount(double overdueAmount, long period, double percent, Currency overdueInvoiceCurrency) {
        return Calculation.operation(overdueAmount * period,percent * 0.01, Calculation.MathOperator.MULTIPLY, overdueInvoiceCurrency);
    }

    public static class Penalties {
        double defaultPenalty;
        double punitivePenalty;

        public Penalties() {
            this.defaultPenalty = 0d;
            this.punitivePenalty = 0d;
        }
    }
}
