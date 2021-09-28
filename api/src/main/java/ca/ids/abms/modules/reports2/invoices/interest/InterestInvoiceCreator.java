package ca.ids.abms.modules.reports2.invoices.interest;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.*;
import ca.ids.abms.modules.reports2.invoices.InvoiceReportUtility;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.util.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Create invoice data, PDF doc, ledger and transaction; one at a time
 */
@Component
public class InterestInvoiceCreator {

    private static final Logger LOG = LoggerFactory.getLogger (InterestInvoiceCreator.class);

    private final ReportHelper reportHelper;
    private final BillingLedgerService billingLedgerService;
    private final InterestInvoiceDocumentCreator interestInvoiceDocumentCreator;
    private final InvoiceSequenceNumberHelper.Generator invoiceSeqNumGen;
    private final LocalDateTime ldtNow;
    private final RoundingUtils roundingUtils;
    private final LocalDateTime endDateInclusive;
    private final BillingCenter billingCenter;
    private final InvoiceStateType initialLedgerState;
    private final User currentUser;
    private final DateTimeFormatter dateFormatter;
    private final Currency anspCurrency;
    private final Currency usdCurrency;
    private final Currency eurCurrency;
    private final CachedCurrencyConverter cachedCurrencyConverter;
    private final SystemConfigurationService systemConfigurationService;
    private final InvoiceReportUtility invoiceReportUtility;

    private final BillingOrgCode billingOrgCode;
    private final Boolean inverseCurrencyRate;

    InterestInvoiceCreator(final ReportHelper reportHelper,
                           final BillingLedgerService billingLedgerService,
                           final InterestInvoiceDocumentCreator interestInvoiceDocumentCreator,
                           final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                           final LocalDateTime ldtNow,
                           final CurrencyUtils currencyUtils,
                           final boolean approvalWorkflow,
                           final LocalDateTime endDateInclusive,
                           final RoundingUtils roundingUtils,
                           final SystemConfigurationService systemConfigurationService,
                           final LocalDateTime exchangeRateDate) {

        this.reportHelper = reportHelper;
        this.billingLedgerService = billingLedgerService;
        this.interestInvoiceDocumentCreator = interestInvoiceDocumentCreator;
        this.ldtNow = ldtNow;
        this.roundingUtils = roundingUtils;
        this.endDateInclusive = endDateInclusive;

        this.billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        this.initialLedgerState = reportHelper.getInitialLedgerState(approvalWorkflow);
        this.currentUser = reportHelper.getCurrentUser();
        this.invoiceSeqNumGen = invoiceSequenceNumberHelper.generator();
        this.dateFormatter = reportHelper.getDateFormat();
        this.cachedCurrencyConverter = new CachedCurrencyConverter (currencyUtils, exchangeRateDate);

        this.anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        this.usdCurrency = cachedCurrencyConverter.getUsdCurrency();
        this.eurCurrency = cachedCurrencyConverter.getEurCurrency();

        this.billingOrgCode = systemConfigurationService.getBillingOrgCode();
        this.inverseCurrencyRate = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVERSE_CURRENCY_RATE);
        this.systemConfigurationService = systemConfigurationService;

        this.invoiceReportUtility = new InvoiceReportUtility(reportHelper, billingLedgerService, null,
            systemConfigurationService, cachedCurrencyConverter, null, null);
    }

    /**
     * Create a POS invoice for the given account and line items
     */
    public InterestInvoice createInterestInvoice(final Account account, final ReportFormat reportFormat,
                                                 final double total, final double defaultPenalty,
                                                 final double punitivePenalty, final long overdueDays,
                                                 final double overdueInvoiceAmount, final String overdueInvoiceNumber) {
        return do_createInvoice (account, reportFormat, total, defaultPenalty, punitivePenalty, overdueDays, overdueInvoiceAmount, overdueInvoiceNumber);
    }

    // ------------------------ private ----------------------------------

    private InterestInvoice do_createInvoice (final Account account, final ReportFormat reportFormat,
                                              final double invoiceTotal, double defaultPenalty,
                                              final double punitivePenalty, final long overdueDays,
                                              final double overdueInvoiceAmount, final String overdueInvoiceNumber) {

        // interest penalty invoice currency is set in sys config "Interest penalty invoice currency"
        final Currency invoiceCurrency;
        String interestInvoiceCurrency = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.INTEREST_PENALTY_INVOICE_CURRENCY);

        if (interestInvoiceCurrency.equalsIgnoreCase("ANSP")) {
            invoiceCurrency = anspCurrency;
        } else if (interestInvoiceCurrency.equalsIgnoreCase("USD")) {
            invoiceCurrency = usdCurrency;
        } else {
            invoiceCurrency = account.getInvoiceCurrency();
        }

        final double exchangeRateToUsd = cachedCurrencyConverter.getExchangeRateToUsd (invoiceCurrency);
        final double exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);

        final String realInvoiceNumber = this.invoiceSeqNumGen.nextInvoiceSequenceNumber(InvoiceType.INTEREST);

        // Create billing ledger
        final BillingLedger billingLedger = do_createBillingLedger (account,
                                                                    invoiceTotal,
                                                                    realInvoiceNumber,
                                                                    exchangeRateToUsd,
                                                                    exchangeRateToAnsp,
                                                                    invoiceCurrency);

        // Create invoice
        final InterestInvoiceData invoiceData = this.do_createInvoiceData (account,
                                                                           billingLedger,
                                                                           exchangeRateToUsd,
                                                                           exchangeRateToAnsp,
                                                                           defaultPenalty,
                                                                           punitivePenalty,
                                                                           overdueDays,
                                                                           overdueInvoiceAmount,
                                                                           overdueInvoiceNumber);

        // Create PDF file
        final ReportDocument reportDocument = this.interestInvoiceDocumentCreator.create (invoiceData, reportFormat);

        // Save PDF file in billing ledger
        reportHelper.setReportDocument (billingLedger, reportDocument);

        // done
        final InterestInvoice invoice = new InterestInvoice (account, invoiceData, billingLedger, reportDocument);

        billingLedgerService.created(billingLedger);

        return invoice;
    }

    /**
     * Create raw invoice data; to be further formatted into PDF etc.
     */
    private InterestInvoiceData do_createInvoiceData (final Account account,
                                                      final BillingLedger billingLedger,
                                                      final Double exchangeRateToUsd,
                                                      final Double exchangeRateToAnsp,
                                                      final Double defaultPenalty,
                                                      final Double punitivePenalty,
                                                      final long overdueDays,
                                                      final double overdueInvoiceAmount,
                                                      final String overdueInvoiceNumber) {

        final InterestInvoiceData x = new InterestInvoiceData();
        Currency currency = billingLedger.getInvoiceCurrency();
        String numberFormat = "%,.2f";

        x.global = new InterestInvoiceData.Global();
        x.global.realInvoiceNumber = billingLedger.getInvoiceNumber();
        x.global.invoiceNumber = reportHelper.getDisplayInvoiceNumber(x.global.realInvoiceNumber, false);
        x.global.invoiceName = String.format ("%s %s%s", Translation.getLangByToken("Interest invoice"),
            x.global.invoiceNumber, "");
        x.global.invoiceIssueLocation = billingCenter.getName();
        x.global.invoiceDateStr = reportHelper.formatDateUtc (endDateInclusive, dateFormatter);
        x.global.invoiceDueDateStr = reportHelper.formatDateUtc(ldtNow.plusDays(account.getPaymentTerms()), dateFormatter);
        x.global.invoiceBillingPeriod = String.format("%s-%s", StringUtils.capitalize(endDateInclusive.getMonth().name().toLowerCase()), endDateInclusive.getYear());

        Locale localeES = new Locale ("es" , "ES");        
        x.global.invoiceBillingPeriodSpanish = String.format("%s-%s", StringUtils.capitalize(endDateInclusive.getMonth().getDisplayName(TextStyle.FULL, localeES).toLowerCase()), endDateInclusive.getYear());
                
        x.global.accountId = account.getId();
        x.global.accountName = account.getName();
        x.global.fromName = currentUser.getName();
        x.global.fromPosition = currentUser.getJobTitle();
        x.global.invoiceCurrencyCode = currency.getCurrencyCode();
        x.global.invoiceCurrencyAnspCode = anspCurrency.getCurrencyCode();
        x.global.invoiceCurrencyUsdCode = usdCurrency.getCurrencyCode();
        x.global.billingName = account.getAviationBillingContactPersonName();
        x.global.billingAddress = account.getAviationBillingMailingAddress();
        x.global.billingContactTel = account.getAviationBillingPhoneNumber();
        x.global.billingEmail = account.getAviationBillingEmailAddress();
        x.global.overdueInvoiceAmount = String.format(numberFormat, overdueInvoiceAmount);
        x.global.overdueInvoiceNumber = overdueInvoiceNumber;
        x.global.overdueInvoiceOverdueDays = overdueDays;
        x.global.invoiceCurrencyInWords = currency.getCurrencyName() != null ? String.format ("%s%s", currency.getCurrencyName(), "s") : "";

        x.global.defaultPenaltyAmountStr = String.format(numberFormat, defaultPenalty);
        x.global.punitivePenaltyAmountStr = String.format(numberFormat, punitivePenalty);

        x.overduePenaltyInvoices = new ArrayList<>();
        x.overduePenaltyInvoices.addAll(invoiceReportUtility.getOverduePenaltyInvoiceList(billingLedger));

        // calculate total outstanding amount from overdue invoice penalties
        Double totalOutstandingAmount = 0d;
        Double totalOutstandingAmountAnsp = 0d;
        for (OverduePenaltyInvoice overduePenaltyInvoice : x.overduePenaltyInvoices) {
            if (overduePenaltyInvoice == null)
                continue;
            totalOutstandingAmount += overduePenaltyInvoice.amountOwing;
            totalOutstandingAmountAnsp += overduePenaltyInvoice.amountOwingAnsp;
        }
        x.global.totalOutstandingAmount = String.format(numberFormat, totalOutstandingAmount);

        // set invoice total amounts
        Double totalAmount = billingLedger.getInvoiceAmount();
        Double totalAmountAnsp = cachedCurrencyConverter.toANSPCurrency(totalAmount, currency);
        Double totalAmountUsd = cachedCurrencyConverter.toUSDCurrency(totalAmount, currency);

        // update invoice data total amount and round appropriately if necessary
        x.global.totalAmount = roundingUtils.calculateSingleRoundedValue(totalAmount, currency, false);
        x.global.totalAmountStr = reportHelper.formatCurrency (x.global.totalAmount, currency);
        x.global.totalAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(x.global.totalAmount, currency);
        x.global.totalAmountAnsp = roundingUtils.calculateSingleRoundedValue(totalAmountAnsp, anspCurrency, false);
        x.global.totalAmountAnspStr = reportHelper.formatCurrency(x.global.totalAmountAnsp, anspCurrency);
        x.global.totalAmountUsd = roundingUtils.calculateSingleRoundedValue(totalAmountUsd, usdCurrency, false);
        x.global.totalAmountUsdStr = reportHelper.formatCurrency(x.global.totalAmountUsd, usdCurrency);

        // total amount in words: total amount spelled out in words using supported locales
        x.global.totalAmountStrInWords = reportHelper.formatAmountInFullText(
            x.global.totalAmount, LocaleUtils.ENGLISH);
        x.global.totalAmountStrInWordsSpanish = reportHelper.formatAmountInFullText(
            x.global.totalAmount, LocaleUtils.SPANISH);
        x.global.totalAmountStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.totalAmount, currency, LocaleUtils.ENGLISH);
        x.global.totalAmountStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.totalAmount, currency, LocaleUtils.SPANISH);

        // set invoice amount dues
        Double amountDue = billingLedger.getAmountOwing();
        Double amountDueAnsp = cachedCurrencyConverter.toANSPCurrency(amountDue, currency);
        Double amountDueUsd = cachedCurrencyConverter.toUSDCurrency(amountDue, currency);

        // KCAA must **DISPLAY** total outstanding amount in amount due
        // cannot be done within invoice template as sys config rounding is required
        if (billingOrgCode == BillingOrgCode.KCAA) {
            amountDue += totalOutstandingAmount;
            amountDueAnsp += totalOutstandingAmountAnsp;
            amountDueUsd += cachedCurrencyConverter.toUSDCurrency(totalOutstandingAmount, currency);
        }

        // update invoice data amount due and round appropriately if necessary
        x.global.amountDue = roundingUtils.calculateSingleRoundedValue(amountDue, currency, false);
        x.global.amountDueStr = reportHelper.formatCurrency(x.global.amountDue, currency);
        x.global.amountDueStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(x.global.amountDue, currency);
        x.global.amountDueAnsp = roundingUtils.calculateSingleRoundedValue(amountDueAnsp, anspCurrency, false);
        x.global.amountDueAnspStr = reportHelper.formatCurrency(x.global.amountDueAnsp, anspCurrency);
        x.global.amountDueUsd = roundingUtils.calculateSingleRoundedValue(amountDueUsd, usdCurrency, false);
        x.global.amountDueUsdStr = reportHelper.formatCurrency(x.global.amountDueUsd, usdCurrency);
        if (billingOrgCode == BillingOrgCode.INAC) {
            double owingInAlternativeCurr = cachedCurrencyConverter.convertCurrency(billingLedger.getAmountOwing(), currency, eurCurrency);
            x.global.amountDueAlt = roundingUtils.calculateSingleRoundedValue(owingInAlternativeCurr, eurCurrency, false);
            x.global.amountDueAltStr = reportHelper.formatCurrency(x.global.amountDueAlt, eurCurrency);
            x.global.amountDueAltStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (x.global.amountDueAlt, eurCurrency);
            x.global.invoiceCurrencyAltCode = eurCurrency.getCurrencyCode();
        }

        // amount due in words: amount due spelled out in words using supported locales
        x.global.amountDueStrInWords = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.ENGLISH);
        x.global.amountDueStrInWordsSpanish = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.SPANISH);
        x.global.amountDueStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.ENGLISH);
        x.global.amountDueStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.SPANISH);

        // deprecated because of misleading name - should use global.amountDueStrInWords
        x.global.totalAmountInWords = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.ENGLISH);
        // deprecated because of misleading name - should use global.amountDueStrInWordsWithCurrencySymbol
        x.global.totalAmountInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.ENGLISH);

        if (exchangeRateToAnsp == null || exchangeRateToUsd == null ) {
            LOG.debug("Bad request: exchange rate to ANSP or exchange rate to USD is not set");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Exchange rate to ANSP or exchange rate to USD is not set"));
        }

        if (inverseCurrencyRate) {
            x.global.exchageRate = String.format("%.5f", exchangeRateToAnsp);
            x.global.exchageRateUsd = String.format("%.5f", 1/exchangeRateToUsd);
        } else {
            x.global.exchageRate = String.format("%.5f", 1/exchangeRateToAnsp);
            x.global.exchageRateUsd = String.format("%.5f", exchangeRateToUsd);
        }

        return x;
    }

    /**
     * Create a billing ledger record
     */
    private BillingLedger do_createBillingLedger (final Account account,
                                                  final double invoiceTotal,
                                                  final String realInvoiceNumber,
                                                  final double exchangeRateToUsd,
                                                  final double exchangeRateToAnsp,
                                                  final Currency invoiceCurrency) {

        final LocalDateTime dueDate = ldtNow.plusDays (account.getPaymentTerms());

        // create billing ledger
        BillingLedger bl = new BillingLedger();
        bl.setAccount(account);
        bl.setBillingCenter(currentUser != null ? currentUser.getBillingCenter() : null);
        bl.setInvoicePeriodOrDate (endDateInclusive);
        bl.setInvoiceType(InvoiceType.INTEREST.toValue());
        bl.setInvoiceStateType (initialLedgerState.toValue());
        bl.setPaymentDueDate (dueDate);
        bl.setUser (currentUser);
        bl.setInvoiceAmount (invoiceTotal);
        bl.setInvoiceCurrency (invoiceCurrency);
        bl.setTargetCurrency (usdCurrency);
        bl.setInvoiceExchange (exchangeRateToUsd);
        bl.setInvoiceExchangeToAnsp (exchangeRateToAnsp);
        bl.setInvoiceDateOfIssue (ldtNow);
        bl.setExported (false);
        bl.setAmountOwing (invoiceTotal);
        bl.setInvoiceNumber (realInvoiceNumber);
        bl.setPaymentMode(account.getCashAccount() ? TransactionPaymentMechanism.cash.toString() : TransactionPaymentMechanism.credit.toString());

        // Document name, type and content (PDF) will be set later, penalties will be applied and invoice total
        // amounts rounded based on system configuration settings
        final BillingLedger billingLedger = billingLedgerService.createBillingLedgerAndTransaction(bl, false,false);

        LOG.info ("Created interest billing ledger record id={}, amountOwing={} {}", billingLedger.getId(),
                                                                                     billingLedger.getAmountOwing(),
                                                                                     invoiceCurrency.getCurrencyCode());

        return billingLedger;
    }
}
