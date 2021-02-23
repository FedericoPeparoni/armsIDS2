package ca.ids.abms.modules.overdueinvoices.interestratecalculation;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.billings.InvoiceOverduePenaltyRepository;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.transactions.error.InterestInvoiceException;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
public class PenaltyCalculationUtility {

    private static final Logger LOG = LoggerFactory.getLogger(PenaltyCalculationUtility.class);

    private final CurrencyUtils currencyUtils;
    private final SystemConfigurationService systemConfigurationService;
    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository;

    public PenaltyCalculationUtility(final CurrencyUtils currencyUtils,
                                     final SystemConfigurationService systemConfigurationService,
                                     final CurrencyExchangeRateService currencyExchangeRateService,
                                     final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository) {
        this.currencyUtils = currencyUtils;
        this.systemConfigurationService = systemConfigurationService;
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.invoiceOverduePenaltyRepository = invoiceOverduePenaltyRepository;
    }

    /**
     * Convert penalty amount for interest invoice according to sys config settings
     * @param penaltyAmount - penalty amount in invoice currency
     * @param interestPenaltyInvoiceCurrency - system configuration for interest invoice currency
     * @param invoiceCurrency - invoice currency
     * @return converted penalty to needed currency
     */
    Double convertToInterestPenaltyInvoiceCurrency(final Double penaltyAmount,
                                                   final String interestPenaltyInvoiceCurrency,
                                                   final Currency invoiceCurrency,
                                                   final LocalDateTime finalPaymentDate) throws InterestInvoiceException {

        Currency interestInvoiceCurrency;
        if (interestPenaltyInvoiceCurrency.equalsIgnoreCase("Original invoice") || finalPaymentDate == null) {
            return penaltyAmount;
        } else if (interestPenaltyInvoiceCurrency.equalsIgnoreCase("ANSP")) {
            interestInvoiceCurrency = currencyUtils.getAnspCurrency();
        } else {
            interestInvoiceCurrency = currencyUtils.getCurrencyUSD();
        }

        // define exchange rate date based on payment date
        LocalDateTime exchangeRateDate = finalPaymentDate;

        // EANA expects to use the previous day for exchange rates
        if (BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode())
            exchangeRateDate = exchangeRateDate.minusDays(1);

        // set exchange rate based on exchange rate date from payment to local currency
        double exchangeRate = currencyUtils.getExchangeRate(invoiceCurrency, interestInvoiceCurrency, exchangeRateDate);

        // validate exchange rate exists between currencies by checking for zero value result
        if (exchangeRate == 0d) {
            final String details = String.format("No exchange rate found between %s and %s on %s",
                invoiceCurrency.getCurrencyName(), interestInvoiceCurrency.getCurrencyName(), exchangeRateDate.toLocalDate());
            LOG.debug(details);
            throw new InterestInvoiceException(details);
        }

        return currencyExchangeRateService.getExchangeAmount(exchangeRate, penaltyAmount, interestInvoiceCurrency.getDecimalPlaces());
    }

    public InvoiceOverduePenalty createInvoiceOverduePenalty(final BillingLedger penalizedInvoice,
                                                             final BillingLedger penaltyAddedToInvoice,
                                                             final LocalDate penaltyPeriodEndDate,
                                                             final Integer penaltyNumberOfMonths,
                                                             final Double defaultPenaltyAmount,
                                                             final Double punitivePenaltyAmount){

        final InvoiceOverduePenalty newInvoiceOverduePenalty = new InvoiceOverduePenalty();

        newInvoiceOverduePenalty.setPenalizedInvoice(penalizedInvoice);
        newInvoiceOverduePenalty.setPenaltyAddedToInvoice(penaltyAddedToInvoice);
        newInvoiceOverduePenalty.setPenaltyAppliedDate(penaltyAddedToInvoice != null ? penaltyAddedToInvoice.getCreatedAt()
            : LocalDateTime.of(penaltyPeriodEndDate, LocalTime.of(0, 0)));
        newInvoiceOverduePenalty.setPenaltyPeriodEndDate(LocalDateTime.of(penaltyPeriodEndDate, LocalTime.of(0, 0)));
        newInvoiceOverduePenalty.setPenaltyNumberOfMonths(penaltyNumberOfMonths);
        newInvoiceOverduePenalty.setDefaultPenaltyAmount(defaultPenaltyAmount);
        newInvoiceOverduePenalty.setPunitivePenaltyAmount(punitivePenaltyAmount);

        LOG.debug("REST request to save InvoiceOverduePenalty : {}", newInvoiceOverduePenalty);

        return invoiceOverduePenaltyRepository.save(newInvoiceOverduePenalty);
    }
}
