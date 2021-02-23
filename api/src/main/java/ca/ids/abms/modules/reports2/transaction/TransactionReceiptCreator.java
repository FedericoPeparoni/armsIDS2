package ca.ids.abms.modules.reports2.transaction;

import static ca.ids.abms.util.MiscUtils.nvl;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.util.LocaleUtils;
import com.google.common.base.Preconditions;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.InvoiceReportUtility;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionPaymentRepository;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.transactions.TransactionType;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;

public class TransactionReceiptCreator {

    private static final Logger log = LoggerFactory.getLogger(TransactionReceiptCreator.class);

    private static final ReportFormat DFLT_FORMAT = ReportFormat.pdf;

    private final ReportHelper reportHelper;
    private final TransactionReceiptDocumentCreator transactionReceiptDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final TransactionPaymentRepository transactionPaymentRepository;
    private final BillingLedgerService billingLedgerService;
    private final RoundingUtils roundingUtils;
    private final SystemConfigurationService systemConfigurationService;
    private final CachedCurrencyConverter cachedCurrencyConverter;
    private final InvoiceReportUtility invoiceReportUtility;

    private final Currency anspCurrency;
    private final Currency usdCurrency;
    private final BillingOrgCode billingOrgCode;
    private final Boolean isOverduePenalties;

    public TransactionReceiptCreator(
        final ReportHelper reportHelper,
        final CurrencyUtils currencyUtils,
        final TransactionReceiptDocumentCreator transactionReceiptDocumentCreator,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final TransactionPaymentRepository transactionPaymentRepository,
        final BillingLedgerService billingLedgerService,
        final RoundingUtils roundingUtils,
        final SystemConfigurationService systemConfigurationService,
        final BankCodeService bankCodeService,
        final TransactionService transactionService,
        final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders,
        final LocalDateTime exchangeRateDate
     ) {
        this.reportHelper = reportHelper;
        this.transactionReceiptDocumentCreator = transactionReceiptDocumentCreator;
        this.systemConfigurationService = systemConfigurationService;
        this.transactionPaymentRepository = transactionPaymentRepository;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.billingLedgerService = billingLedgerService;
        this.roundingUtils = roundingUtils;

        this.cachedCurrencyConverter = new CachedCurrencyConverter(currencyUtils, exchangeRateDate);
        this.invoiceReportUtility = new InvoiceReportUtility(reportHelper, billingLedgerService, transactionService,
            systemConfigurationService, cachedCurrencyConverter, bankCodeService, aviationInvoiceChargeProviders);

        this.anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        this.usdCurrency = cachedCurrencyConverter.getUsdCurrency();
        this.billingOrgCode = systemConfigurationService.getBillingOrgCode();
        this.isOverduePenalties = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON).equalsIgnoreCase("Daily");
    }

    public ReportDocument createTransactionReceipt (final Transaction transaction, final TransactionReceiptData data, final ReportFormat format) {
        Preconditions.checkArgument (transaction.getReceiptDocument() == null, "can't replace existing receipt document in transaction");
        final ReportDocument doc = this.transactionReceiptDocumentCreator.create (data, format);
        transaction.setReceiptDocument (doc.data());
        transaction.setReceiptDocumentType (doc.contentType());
        transaction.setReceiptDocumentFileName (doc.fileName());
        transaction.setReceiptNumber (data.global.transactionNumber);
        return doc;
    }

    public ReportDocument createCreditDebitNoteDocument (final Transaction transaction, final CreditDebitNoteData data, final ReportFormat format) {
        Preconditions.checkArgument (transaction.getReceiptDocument() == null, "can't replace existing receipt document in transaction");
        final ReportDocument doc = this.transactionReceiptDocumentCreator.create (data, format);
        transaction.setReceiptDocument (doc.data());
        transaction.setReceiptDocumentType (doc.contentType());
        transaction.setReceiptDocumentFileName (doc.fileName());
        return doc;
    }

    public ReportDocument createTransactionReceipt (final Transaction transaction) {
        return createTransactionReceipt (transaction, ReportFormat.pdf, false);
    }

    public ReportDocument createTransactionDebitNote (final Transaction transaction) {
        final BillingLedger debitNote = this.createBillingLedgerFromTransactionForDebitNote (transaction);
        final ReportDocument document = createCreditDebitNoteData(transaction, transaction.getChargesAdjustment(), transaction.getBillingLedgerIds(), debitNote);
        reportHelper.setReportDocument(debitNote, document);

        // event trigger to indicate that a billing ledger was created
        billingLedgerService.created(debitNote);
        return document;
    }

    public ReportDocument createTransactionCreditNote (final Transaction transaction,
                                                       final Collection<ChargesAdjustment> chargesAdjustments,
                                                       final List<Integer> invoiceIds) {
        return createCreditDebitNoteData(transaction, chargesAdjustments, invoiceIds, null);
    }

    private ReportDocument createCreditDebitNoteData (final Transaction transaction, final Collection<ChargesAdjustment> charges, final List<Integer> invoiceIds, final BillingLedger debitNote) {
        final CreditDebitNoteData data = this.createCreditDebitNoteData(transaction, charges, ReportFormat.pdf, false, invoiceIds, debitNote);
        return this.createCreditDebitNoteDocument (transaction, data, ReportFormat.pdf);
    }

    public ReportDocument createTransactionReceipt (final Transaction transaction, final ReportFormat format, final boolean preview) {
        final TransactionReceiptData data = this.createTransactionReceiptData(transaction, format, preview);
        final ReportDocument doc = this.createTransactionReceipt (transaction, data, format);
        return doc;
    }

    public CreditDebitNoteData createCreditDebitNoteData (final Transaction transaction,
                                                          final Collection<ChargesAdjustment> adjustments,
                                                          final ReportFormat format, final boolean preview,
                                                          final List<Integer> invoiceIds,
                                                          final BillingLedger debitNoteSaved) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final BillingCenter billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        final ReportFormat reportFormat = nvl (format, DFLT_FORMAT);
        final User currentUser = reportHelper.getCurrentUser();
        final DateTimeFormatter dateFormatter = reportHelper.getDateFormat();
        Account account = null;
        BillingLedger billingLedgerToAdjust = null;

        final List <TransactionPayment> payments = transactionPaymentRepository.findByTransactionId(transaction.getId());
        if (CollectionUtils.isNotEmpty(invoiceIds)) {
            billingLedgerToAdjust = reportHelper.getBillingLedger(invoiceIds.get(0));
        }

        final List <BillingLedger> billingLedgerList = reportHelper.getTransactionBillingLedgers (invoiceIds);
        if (billingLedgerList!=null && !billingLedgerList.isEmpty()) {
            account = reportHelper.tryGetUniqueAccount (billingLedgerList);
        }

        final Account transactionAccount = transaction.getAccount();
        if (transaction.getReceiptNumber() == null) {
            transaction.setReceiptNumber (invoiceSequenceNumberHelper.generator().nextReceiptSequenceNumber(transaction.getPaymentMechanism()));
        }
        if (account != null && !account.equals(transactionAccount)) {
            final String msg = String.format (
                "one or more invoices belong to account" +
                " #%d[%s], " +
                "which doesn't match transaction account" +
                " #%d[%s], ",
                account.getId(), account.getName(),
                transactionAccount.getId(), transactionAccount.getName());
            throw new RuntimeException (msg);
        }
        if (transaction.getTransactionType().getName().equals("debit")) {
        	if(debitNoteSaved != null && StringUtils.isNotBlank(debitNoteSaved.getInvoiceNumber())) {
        		transaction.setDebitNoteNumber(debitNoteSaved.getInvoiceNumber());
        	} else {
        		transaction.setDebitNoteNumber(invoiceSequenceNumberHelper.generator().nextInvoiceSequenceNumber(InvoiceType.DEBIT_NOTE));
        	}
        }

        return this.createCreditDebitNoteData (
            ldtNow,
            billingCenter,
            transaction.getCurrency(),
            transactionAccount,
            currentUser,
            dateFormatter,
            billingLedgerToAdjust,
            debitNoteSaved,
            payments,
            transaction,
            adjustments,
            preview);
    }

    public TransactionReceiptData createTransactionReceiptData (final Transaction transaction, final ReportFormat format, final boolean preview) {
        final ZonedDateTime zdtNow = ZonedDateTime.now (ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdtNow.toLocalDateTime();
        final DateTimeFormatter dateFormatter = reportHelper.getDateFormat();

        Account account = null;
        final List <BillingLedger> billingLedgerList = reportHelper.getTransactionBillingLedgers (transaction);
        if (billingLedgerList!=null && !billingLedgerList.isEmpty()) {
        	account = reportHelper.tryGetUniqueAccount (billingLedgerList);
        }

        final Account transactionAccount = transaction.getAccount();
        if (transaction.getReceiptNumber() == null) {
            transaction.setReceiptNumber (invoiceSequenceNumberHelper.generator().nextReceiptSequenceNumber(transaction.getPaymentMechanism()));
        }
        if (account != null && !account.equals(transactionAccount)) {
            final String msg = String.format (
                "one or more invoices belong to account" +
                " #%d[%s], " +
                "which doesn't match transaction account" +
                " #%d[%s], ",
                    account.getId(), account.getName(),
                    transactionAccount.getId(), transactionAccount.getName());
            throw new RuntimeException (msg);
        }

        // attempt to set created by user from current user if exists
        // else attempt to set from transaction created by field
        User createdBy = reportHelper.getCurrentUser();
        if (createdBy == null && transaction.getCreatedBy() != null && !transaction.getCreatedBy().isEmpty()) {
            createdBy = reportHelper.getUserByLogin(transaction.getCreatedBy());
        }

        // attempt to get billing center from created by user
        BillingCenter billingCenter = createdBy == null ? null
            : createdBy.getBillingCenter();

        // if user's billing center could not be found, default to HQ
        if (billingCenter == null) {
            billingCenter = reportHelper.getHQBillingCenter();
        }

        return this.createTransactionReceiptData (
                ldtNow,
                billingCenter,
                transactionAccount,
                createdBy,
                dateFormatter,
                billingLedgerList,
                transaction,
               // reportFormat,
                preview);
    }

    /**
     * Create transaction receipt data
     */
    public TransactionReceiptData createTransactionReceiptData (
            final LocalDateTime ldtNow,
            final BillingCenter billingCenter,
            final Account account,
            final User currentUser,
            final DateTimeFormatter dateFormatter,
            final List <BillingLedger> billingLedgerList,
            final Transaction transaction,
           // final ReportFormat reportFormat,
            final boolean preview) {
        final TransactionReceiptData data = new TransactionReceiptData();
        final Currency transactionCurrency = transaction.getCurrency();
        final Currency transactionPaymentCurrency = transaction.getPaymentCurrency();
        final String transactionRealNumber = transaction.getReceiptNumber();
        Preconditions.checkNotNull(transactionRealNumber, "transaction receipt number is not set");
        final String transactionDisplayNumber = reportHelper.getDisplayReceiptNumber (transactionRealNumber, preview);
        final double transactionPaymentExchangeRate = transaction.getPaymentExchangeRate();

        Preconditions.checkNotNull (transaction.getTransactionType());
        final TransactionType transactionType = transaction.getTransactionType();

        data.global = new TransactionReceiptData.Global();

        data.global.isDebit = transactionType.isDebit();

        data.global.receiptName = String.format ("receipt-%s", transactionRealNumber);
        data.global.transactionIssueLocation = billingCenter != null ? billingCenter.getName() : null;
        data.global.transactionRealNumber = transactionRealNumber;
        data.global.transactionNumber = transactionDisplayNumber;
        data.global.transactionPayerName = account.getName();
        data.global.transactionDescription = transaction.getDescription();
        data.global.kraClerkName = transaction.getKraClerkName();
        data.global.kraReceiptNumber = transaction.getKraReceiptNumber();

        String invoiceType = null;

        if (!billingLedgerList.isEmpty()) {
            invoiceType = billingLedgerList.get(0).getInvoiceType();
        }

        Boolean aviation = InvoiceType.AVIATION_IATA.toValue().equalsIgnoreCase(invoiceType) || InvoiceType.AVIATION_NONIATA.toValue().equalsIgnoreCase(invoiceType);

        data.global.localAmount = roundingUtils.calculateSingleRoundedValue(Math.abs(transaction.getAmount()), transactionCurrency, aviation);
        data.global.localAmountStr = reportHelper.formatCurrency(data.global.localAmount, transactionCurrency);
        data.global.localAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(data.global.localAmount, transactionCurrency);

        data.global.paymentAmount = roundingUtils.calculateSingleRoundedValue(Math.abs(transaction.getPaymentAmount()), transactionPaymentCurrency, aviation);
        data.global.paymentAmountStr = reportHelper.formatCurrency(data.global.paymentAmount, transactionPaymentCurrency);

        LocalDateTime paymentDate = transaction.getPaymentDate() != null ? transaction.getPaymentDate() : transaction.getTransactionDateTime();
        data.global.paymentDateStr = reportHelper.formatDateUtc (paymentDate, dateFormatter);

        if (billingOrgCode == BillingOrgCode.KCAA) {
            String paymentText = reportHelper.formatAmountInFullTextWithCurrencyCode(
                data.global.paymentAmount, transactionPaymentCurrency, true, LocaleUtils.ENGLISH);
            if (!transactionPaymentCurrency.getCurrencyCode().equals(transactionCurrency.getCurrencyCode()))
                paymentText = paymentText + ", " + reportHelper.formatAmountInFullTextWithCurrencyCode(
                    data.global.localAmount, transactionCurrency, true, LocaleUtils.ENGLISH);
            data.global.paymentAmountStrWithCurrencySymbol = paymentText;
        } else {
            data.global.paymentAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(
                data.global.paymentAmount, transactionPaymentCurrency);
        }

        SystemConfiguration showInverse = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INVERSE_CURRENCY_RATE);
        if (showInverse == null || showInverse.getCurrentValue() == null || showInverse.getCurrentValue().equals("f")) {
            data.global.transactionPaymentExchangeRate = transactionPaymentExchangeRate;
            data.global.transactionPaymentExchangeRateStr = reportHelper.formatDouble(Calculation.truncate(transactionPaymentExchangeRate, 5), 5);
        } else {
            data.global.transactionPaymentExchangeRate = 1 / transactionPaymentExchangeRate;
            data.global.transactionPaymentExchangeRateStr = reportHelper.formatDouble(Calculation.truncate((1/transactionPaymentExchangeRate), 5), 5);
        }

        data.global.transactionReferenceNumber = transaction.getPaymentReferenceNumber();
        data.global.transactionDateStr = reportHelper.formatDateUtc (ldtNow, dateFormatter);

        data.global.bankName = null;
        data.global.bankBranch = null;
        data.global.bankAccount = null;

        data.global.transactionOfficerName = currentUser != null ? currentUser.getName() : null;
        data.global.transactionOfficerPosition = currentUser != null ? currentUser.getJobTitle() : null;

        data.global.kraClerkName = transaction.getKraClerkName();
        data.global.kraReceiptNumber = transaction.getKraReceiptNumber();

        // payments info
        List<TransactionPayment> transactionPayments = transactionPaymentRepository.getAllPaymentsByTransactionId(transaction.getId());

        data.paymentInfoList = transactionPayments.stream().map (transactionPayment->{

            final double paymentExchangeRate = data.global.transactionPaymentExchangeRate;

            final TransactionReceiptData.PaymentInfo  xx = new TransactionReceiptData.PaymentInfo();
            xx.billingLedgerId = transactionPayment.getBillingLedger().getId();
            xx.invoiceNumber = transactionPayment.getBillingLedger().getInvoiceNumber();
            xx.invoiceDateStr = reportHelper.formatDateUtc (transactionPayment.getBillingLedger().getInvoiceDateOfIssue(), dateFormatter);

            Currency paymentCurrency = transactionPayment.getCurrency();
            xx.localAmount = roundingUtils.calculateSingleRoundedValue(Math.abs(transactionPayment.getAmount()), paymentCurrency, aviation);
            xx.localAmountStr = reportHelper.formatCurrency (xx.localAmount, paymentCurrency);
            xx.localAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (xx.localAmount, paymentCurrency);

            Double paymentAmount = cachedCurrencyConverter.convertCurrency(transactionPayment.getAmount(), paymentCurrency, transactionPaymentCurrency);
            xx.paymentAmount = roundingUtils.calculateSingleRoundedValue(Math.abs(paymentAmount), transactionPaymentCurrency, aviation);
            xx.paymentAmountStr = reportHelper.formatCurrency (xx.paymentAmount, transactionPaymentCurrency);
            xx.paymentAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (xx.paymentAmount, transactionPaymentCurrency);

            xx.paymentExchangeRate = paymentExchangeRate;
            xx.paymentExchangeRateStr = reportHelper.formatDouble(Calculation.truncate(paymentExchangeRate, 5), 5);

            return xx;
        }).collect (Collectors.toList());

        data.global.localCurrencyCode = transactionCurrency.getCurrencyCode();
        data.global.paymentCurrencyCode = transactionPaymentCurrency.getCurrencyCode();

        return data;
    }

    /**
     * Create transaction receipt data
     */
    public CreditDebitNoteData createCreditDebitNoteData (
        final LocalDateTime ldtNow,
        final BillingCenter billingCenter,
        final Currency currency,
        final Account account,
        final User currentUser,
        final DateTimeFormatter dateFormatter,
        final BillingLedger invoiceToAdjust,
        final BillingLedger debitNote,
        final List <TransactionPayment> paymentList,
        final Transaction transaction,
        final Collection<ChargesAdjustment> adjustments,
        final boolean preview)
    {
        final CreditDebitNoteData data = new CreditDebitNoteData();

        LocalDateTime createdAt = transaction.getCreatedAt();
        if (createdAt == null) {
            createdAt = ldtNow;
        }

        data.global = new CreditDebitNoteData.Global();
        if (invoiceToAdjust == null) {
            throw new CustomParametrizedException(
                "At least one billing ledger has to be provided to create a debit or credit note",
                "billingLedgerIds");
        }
        final InvoiceType invoiceType = InvoiceType.forValue(invoiceToAdjust.getInvoiceType());
        switch (invoiceType) {
            case AVIATION_IATA:
            case AVIATION_NONIATA:
                data.global.isAviation = true;
                break;
            default:
                data.global.isAviation = false;
        }
        data.global.invoiceRefNumber = invoiceToAdjust.getInvoiceNumber();
        data.global.invoiceRefDateStr = reportHelper.formatDateUtc (invoiceToAdjust.getInvoiceDateOfIssue(), dateFormatter);
        final Currency transactionCurrency = transaction.getCurrency();
        final String transactionRealNumber = transaction.getReceiptNumber();

        Preconditions.checkNotNull(transactionRealNumber, "transaction receipt number is not set");
        final String transactionDisplayNumber = reportHelper.getDisplayReceiptNumber (transactionRealNumber, preview);

        Preconditions.checkNotNull (transaction.getTransactionType());
        final TransactionType transactionType = transaction.getTransactionType();

        data.global.isDebit = transactionType.isDebit();
        if (data.global.isDebit && transaction.getDebitNoteNumber() != null) {
            data.global.transactionNumber = transaction.getDebitNoteNumber();
        } else {
            data.global.transactionNumber = transactionDisplayNumber;
        }

        data.global.creditAmount = transactionType.isDebit()
            ? Calculation.truncate (transaction.getAmount(), transactionCurrency.getDecimalPlaces())
            // Credit reduces an amount while debit adds an amount
            : - Calculation.truncate (transaction.getAmount(), transactionCurrency.getDecimalPlaces());
        data.global.creditAmountStr = reportHelper.formatCurrency (data.global.creditAmount, transactionCurrency);
        data.global.creditAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (data.global.creditAmount, transactionCurrency);

        data.additionalCharges = new ArrayList<>();
        data.overduePenaltyInvoices = new ArrayList<>();

        double itemsTotalAmount = 0.0d;
        double itemsTotalAnspAmount = 0.0d;
        if (debitNote != null) {
            data.additionalCharges.addAll(invoiceReportUtility.getAppliedPenaltyAsAdditionalChargeList(debitNote));
            data.overduePenaltyInvoices.addAll(invoiceReportUtility.getOverduePenaltyInvoiceList(debitNote));
            if (isOverduePenalties) {
                for (final AdditionalCharge additionalCharge : data.additionalCharges) {
                    itemsTotalAmount += additionalCharge.amount;
                    itemsTotalAnspAmount += additionalCharge.amountAnsp;
                }
            }
        }

        double totalAmount = transaction.getAmount();
        if (CollectionUtils.isNotEmpty(paymentList) && transaction.getPaymentMechanism() != TransactionPaymentMechanism.adjustment) {
            data.affectedInvoiceList = new ArrayList<>(paymentList.size());
            for (final TransactionPayment payment : paymentList) {
                if (payment.getBillingLedger() != null) {
                    final CreditDebitNoteData.AffectedInvoice item = new CreditDebitNoteData.AffectedInvoice();
                    item.id = payment.getBillingLedger().getId();
                    item.affectedInvoiceNumber = payment.getBillingLedger().getInvoiceNumber();
                    item.affectedInvoiceAmount = reportHelper.formatCurrency(payment.getAmount(), payment.getCurrency());
                    data.affectedInvoiceList.add(item);
                    totalAmount -= payment.getAmount();
                }
            }
            data.global.totalAmount = Calculation.truncate (totalAmount, transactionCurrency.getDecimalPlaces());
            data.global.totalAmountStr = reportHelper.formatCurrency (data.global.totalAmount, transactionCurrency);
            data.global.totalAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (data.global.totalAmount, transactionCurrency);

            Double convertedTotalCharges = cachedCurrencyConverter.convertToANSP(data.global.totalAmount, currency);
            data.global.totalAmountAnsp = roundingUtils.calculateSingleRoundedValue(convertedTotalCharges, anspCurrency, true);
            data.global.totalAmountAnspStr = reportHelper.formatCurrency(data.global.totalAmountAnsp, anspCurrency);
        } else {
            data.global.totalAmount = data.global.creditAmount;
            data.global.totalAmountStr = data.global.creditAmountStr;
            data.global.totalAmountStrWithCurrencySymbol = data.global.creditAmountStrWithCurrencySymbol;

            Double convertedTotalCharges = cachedCurrencyConverter.toANSPCurrency(data.global.totalAmount, currency);
            data.global.totalAmountAnsp = roundingUtils.calculateSingleRoundedValue(convertedTotalCharges, anspCurrency, true);
            data.global.totalAmountAnspStr = reportHelper.formatCurrency(data.global.totalAmountAnsp, anspCurrency);
        }
        if (currency.getCurrencyCode().equals(anspCurrency.getCurrencyCode())) {
            data.global.amountDue = Calculation.truncate (data.global.totalAmount + itemsTotalAnspAmount, anspCurrency.getDecimalPlaces());
            data.global.amountDueStr = reportHelper.formatCurrency(data.global.amountDue, anspCurrency);
            data.global.amountDueStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (data.global.amountDue, anspCurrency);

            Double amountDueInANSP = roundingUtils.calculateSingleRoundedValue(data.global.amountDue, anspCurrency, data.global.isAviation);
            data.global.amountDueInANSP = reportHelper.formatCurrency(amountDueInANSP, anspCurrency);
        } else {
            data.global.amountDue = Calculation.truncate (data.global.totalAmount + itemsTotalAmount, currency.getDecimalPlaces());
            data.global.amountDueStr = reportHelper.formatCurrency(data.global.amountDue, currency);
            data.global.amountDueStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (data.global.amountDue, currency);

            Double amountDueInANSP = roundingUtils.calculateSingleRoundedValue(cachedCurrencyConverter.toANSPCurrency(data.global.amountDue, currency), anspCurrency, data.global.isAviation);
            data.global.amountDueInANSP = reportHelper.formatCurrency(amountDueInANSP, anspCurrency);
        }

        if (billingOrgCode == BillingOrgCode.KCAA) {
            data.global.amountDueStr = reportHelper.formatCurrency(roundingUtils.calculateSingleRoundedValue(data.global.amountDue, currency, data.global.isAviation), currency);
            if (currency.getCurrencyCode().equals(anspCurrency.getCurrencyCode())) {
                Double totalAmountInUSD = roundingUtils.calculateSingleRoundedValue(cachedCurrencyConverter.toUSDCurrency(data.global.totalAmount, currency), usdCurrency, data.global.isAviation);
                data.global.totalAmountInUSD = reportHelper.formatCurrency(totalAmountInUSD, usdCurrency);

                Double amountDueInUSD = roundingUtils.calculateSingleRoundedValue(cachedCurrencyConverter.toUSDCurrency(data.global.amountDue, currency), usdCurrency, data.global.isAviation);
                data.global.amountDueInUSD = reportHelper.formatCurrency(amountDueInUSD, usdCurrency);
            }
        }

        // Transaction type specific end

        data.global.realTransactionNumber = transactionRealNumber;

        data.global.transactionName = String.format (transactionType.isDebit() ? "debit-note-%s" : "credit-note-%s", data.global.transactionNumber);
        data.global.transactionIssueLocation = billingCenter.getName();
        data.global.transactionDateStr = reportHelper.formatDateUtc (ldtNow, dateFormatter);
        data.global.accountId = account.getId();
        data.global.accountName = account.getName();
        data.global.accountAlias = account.getAlias();
        data.global.fromName = currentUser.getName();
        data.global.fromPosition = currentUser.getJobTitle();
        data.global.invoiceCurrencyCode = transactionCurrency.getCurrencyCode();
        data.global.transactionDateStr = reportHelper.formatDateUtc (createdAt, dateFormatter);
        data.global.billingName = account.getAviationBillingContactPersonName();
        data.global.billingAddress = account.getAviationBillingMailingAddress();
        data.global.billingContactTel = account.getAviationBillingPhoneNumber();
        data.lineItemList = adjustments.stream().map (adjustment -> {

            final CreditDebitNoteData.LineItem item = new CreditDebitNoteData.LineItem();
            item.id = adjustment.getId();
            item.dateStr = reportHelper.formatDateUtc (adjustment.getDate() == null ? ldtNow : adjustment.getDate(),
                dateFormatter);
            item.flightId = adjustment.getFlightId();
            item.aerodrome = adjustment.getAerodrome();
            item.chargeDescription = adjustment.getChargeDescription();
            item.chargeDescriptionSpanish = Translation.getLangByToken(item.chargeDescription, LocaleUtils.SPANISH);
            item.amount = adjustment.getChargeAmount();
            item.amountStr = reportHelper.formatCurrency (Math.abs(item.amount), currency);
            item.amountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (Math.abs(item.amount), currency);

            Double convertedTotalCharges = cachedCurrencyConverter.convertToANSP(item.amount, currency);
            item.amountAnsp = roundingUtils.calculateSingleRoundedValue(convertedTotalCharges, anspCurrency, true);
            item.amountAnspStr = reportHelper.formatCurrency(item.amountAnsp, anspCurrency);

            return item;
        }).collect (Collectors.toList());
       return data;
    }

    /**
     * Create a billing ledger record and return the invoiceNumber
     */
    public BillingLedger createBillingLedgerFromTransactionForDebitNote (final Transaction transaction) {
        final Account account = transaction.getAccount();
        final Currency invoiceCurrency = transaction.getCurrency();
        final LocalDateTime invoicePeriodOrDate = transaction.getTransactionDateTime();
        final LocalDateTime dueDate = invoicePeriodOrDate.plusDays (account.getPaymentTerms());

        // get current user using security context
        User user = reportHelper.getCurrentUser();

        // create billing ledger
        final BillingLedger bl = new BillingLedger();
        bl.setAccount(account);
        bl.setBillingCenter(user != null ? user.getBillingCenter() : null);
        bl.setInvoicePeriodOrDate (invoicePeriodOrDate);
        bl.setInvoiceType(InvoiceType.DEBIT_NOTE.toValue());
        bl.setInvoiceStateType (reportHelper.getInitialLedgerState(true).toValue());
        bl.setPaymentDueDate (dueDate);
        bl.setUser(user);
        bl.setInvoiceAmount (transaction.getAmount());
        bl.setInvoiceCurrency (invoiceCurrency);
        bl.setTargetCurrency (usdCurrency);
        bl.setInvoiceExchange (transaction.getExchangeRate());
        bl.setInvoiceExchangeToAnsp (transaction.getExchangeRateToAnsp());
        bl.setInvoiceDateOfIssue (invoicePeriodOrDate);
        bl.setExported (false);
        bl.setAmountOwing (transaction.getAmount());
        bl.setInvoiceNumber(transaction.getDebitNoteNumber() != null ? transaction.getDebitNoteNumber() : transaction.getReceiptNumber());
        bl.setTransactionDescription(transaction.getDescription());
        if (account.getCashAccount()) {
            bl.setPaymentMode(TransactionPaymentMechanism.cash.toString());
        } else {
            bl.setPaymentMode(TransactionPaymentMechanism.credit.toString());
        }

        /* Transfer the debit note document from the transaction to the invoice */
        bl.setInvoiceDocument(transaction.getReceiptDocument());
        bl.setInvoiceDocumentType(transaction.getReceiptDocumentType());
        bl.setInvoiceFileName(transaction.getReceiptDocumentFileName());
        bl.setChargesAdjustment(transaction.getChargesAdjustment());
        transaction.setReceiptDocument(null);
        transaction.setReceiptDocumentType(null);
        transaction.setReceiptDocumentFileName(null);

        final BillingLedger billingLedger = billingLedgerService.createBillingLedgerAndTransaction(bl,true,false);

        List<Integer> invoicesId = transaction.getBillingLedgerIds();
        if (invoicesId == null) {
            invoicesId = new ArrayList<>();
            transaction.setBillingLedgerIds(invoicesId);
        }
        invoicesId.add(billingLedger.getId());

        log.info ("Created billing ledger for debit note, record id={}, amount={} {}",
            billingLedger.getId(), billingLedger.getInvoiceAmount(), billingLedger.getInvoiceCurrency().getCurrencyCode());

        // event trigger to indicate that a billing ledger was created
        billingLedgerService.created(billingLedger);
        return billingLedger;
    }
}
