package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionTypeService;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.plugins.kcaa.erp.modules.currency.KcaaErpCurrencyConstants;
import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLine;
import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ReceiptHeaderMapper {

    // set blank values to this currency code, blank is considered Kenya Shillings
    private static final String DEFAULT_CURRENCY_CODE = KcaaErpCurrencyConstants.BLANK_CURRENCY_CODE;

    private static final TransactionPaymentMechanism DEFAULT_PAYMENT_MECHANISM = TransactionPaymentMechanism.credit;

    private static final String DEFAULT_REFERENCE_NUMBER = "N/A";

    private static final String TRANSACTION_TYPE = "credit";

    @Autowired
    protected AccountExternalChargeCategoryService accountExternalChargeCategoryService;

    @Autowired
    protected CurrencyService currencyService;

    @Autowired
    protected BillingLedgerService billingLedgerService;

    @Autowired
    private CurrencyExchangeRateService currencyExchangeRateService;

    @Autowired
    private TransactionTypeService transactionTypeService;

    public abstract List<Transaction> toTransaction(List<ReceiptHeader> receiptHeaders);

    @Mapping(target = "receiptNumber", source = "no")
    @Mapping(target = "transactionDateTime", source = "postingDate")
    @Mapping(target = "description", source = "postingDescription")
    @Mapping(target = "exported", constant = "false")
    public abstract Transaction toTransaction(ReceiptHeader receiptHeader);

    @AfterMapping
    void toTransactionAccount(final ReceiptHeader source, @MappingTarget Transaction target) {
        if (source.getReceiptLines() != null)
            target.setAccount(receiptLinesAccount(source.getReceiptLines()));
    }

    @AfterMapping
    void toTransactionAmountAndCurrency(final ReceiptHeader source, @MappingTarget Transaction target) {

        // find payement currency by external code or default if blank, blank is considered Kenya Shillings
        Currency paymentCurrency;
        if (source.getCurrencyCode() != null && !source.getCurrencyCode().isEmpty())
            paymentCurrency = currencyService.findByExternalAccountingSystemId(source.getCurrencyCode());
        else
            paymentCurrency = currencyService.findByCurrencyCode(DEFAULT_CURRENCY_CODE);

        // find local currency from receipt lines
        Currency localCurrency = receiptLinesCurrency(source.getReceiptLines());

        // cannot continue if no currency values
        if (paymentCurrency == null || localCurrency == null)
            return;

        target.setPaymentCurrency(paymentCurrency);
        target.setCurrency(localCurrency);

        // set exchange rate from currencies
        Double paymentExchangeRate;
        if (paymentCurrency.equals(localCurrency))
            paymentExchangeRate = 1d;
        else
            paymentExchangeRate = currencyExchangeRateService.getExchangeRate(
                paymentCurrency, localCurrency, target.getTransactionDateTime());

        // cannot continue if no payment exchange value
        if (paymentExchangeRate == 0d)
            return;

        target.setPaymentExchangeRate(paymentExchangeRate);

        // set amounts from receipt line amounts, convert with payment exchange rate
        Double paymentAmount = receiptLineAmount(source.getReceiptLines());
        Double localAmount = currencyExchangeRateService.getExchangeAmount(paymentAmount, paymentExchangeRate,
            localCurrency.getDecimalPlaces());

        // make sure payment amount decimal points are correct for currency
        paymentAmount = Calculation.truncate(paymentAmount, paymentCurrency.getDecimalPlaces());

        // set as negative values since they are always credit amounts
        target.setPaymentAmount(paymentAmount * -1);
        target.setAmount(localAmount * -1);
    }

    @AfterMapping
    void toTransactionBillingLedgerIds(final ReceiptHeader source, @MappingTarget Transaction target) {

        if (source.getReceiptLines() == null) {
            target.setBillingLedgerIds(new ArrayList<>());
            return;
        }

        List<Integer> billingLedgerIds = new ArrayList<>();
        for (ReceiptLine receiptLine : source.getReceiptLines()) {
            if (receiptLine.getInvoiceNo() == null || receiptLine.getInvoiceNo().isEmpty())
                continue;

            BillingLedger billingLedger = billingLedgerService.findByInvoiceNumber(receiptLine.getInvoiceNo());
            if (billingLedger != null && billingLedger.getId() != null)
                billingLedgerIds.add(billingLedger.getId());
        }

        target.setBillingLedgerIds(billingLedgerIds);
    }

    @AfterMapping
    void toTransactionPaymentMechanism(final ReceiptHeader source, @MappingTarget Transaction target) {

        // set mechanism and reference number based on chequeNo
        if (source.getChequeNo() != null && !source.getChequeNo().isEmpty()) {
            target.setPaymentMechanism(TransactionPaymentMechanism.cheque);
            target.setPaymentReferenceNumber(source.getChequeNo());
        } else {
            target.setPaymentMechanism(DEFAULT_PAYMENT_MECHANISM);
            target.setPaymentReferenceNumber(DEFAULT_REFERENCE_NUMBER);
        }
    }

    @AfterMapping
    void toTransactionType(final ReceiptHeader source, @MappingTarget Transaction target) {
        target.setTransactionType(transactionTypeService.findOneByName(TRANSACTION_TYPE));
    }

    @AfterMapping
    void toTransactionCreatedBy(final ReceiptHeader source, @MappingTarget Transaction target) {

        // attempt to set created by from cachier id
        // fallback to entered by if cachier id is not defined
        if (source.getCashierId() != null && !source.getCashierId().isEmpty())
            target.setCreatedBy(source.getCashierId());
        else if (source.getEnteredBy() != null && !source.getEnteredBy().isEmpty())
            target.setCreatedBy(source.getEnteredBy());

        // set created at only if casher date is defined and not default date
        if (source.getCasherDate() == null || source.getCasherDate().equals(DefaultValue.LOCAL_DATE_TIME))
            return;

        // add time from cachier time if defined
        LocalDateTime createdAt = source.getCasherDate();
        if (source.getCachierTime() != null)
            createdAt = createdAt.with(source.getCachierTime().toLocalTime());

        // set transaction created at datetime
        target.setCreatedAt(createdAt);
    }

    /**
     * Get first account found from list of receipt lines.
     *
     * @param receiptLines receipt lines to search
     * @return account
     */
    private Account receiptLinesAccount(final List<ReceiptLine> receiptLines) {
        if (receiptLines == null)
            return null;

        // loop through receipt lines until account found, only ever one distinct per receipt header's lines
        for (ReceiptLine receiptLine : receiptLines) {
            if (receiptLine == null || receiptLine.getAccountNo() == null || receiptLine.getAccountNo().isEmpty())
                continue;

            AccountExternalChargeCategory external = accountExternalChargeCategoryService
                .findOneByExternalSystemIdentifier(receiptLine.getAccountNo());
            if (external != null && external.getAccount() != null)
                return external.getAccount();
        }

        // nothing found if thi line reached
        return null;
    }

    /**
     * Get sum of amounts from list of receipt lines.
     *
     * @param receiptLines receipt lines to sum
     * @return total amount
     */
    private Double receiptLineAmount(final List<ReceiptLine> receiptLines) {
        if (receiptLines == null)
            return 0d;

        Double amount = 0d;
        for (ReceiptLine receiptLine : receiptLines) {
            amount += receiptLine.getAmount() != null ? receiptLine.getAmount() : 0d;
        }
        return amount;
    }

    /**
     * Get first currency found from list of receipt lines.
     *
     * @param receiptLines receipt lines to search
     * @return currency
     */
    private Currency receiptLinesCurrency(final List<ReceiptLine> receiptLines) {
        if (receiptLines == null)
            return null;

        // loop through receipt lines until currency found, only ever one distinct per receipt header's lines
        for (ReceiptLine receiptLine : receiptLines) {
            BillingLedger billingLedger = billingLedgerService.findByInvoiceNumber(receiptLine.getInvoiceNo());
            if (billingLedger != null && billingLedger.getInvoiceCurrency() != null)
                return billingLedger.getInvoiceCurrency();
        }

        // nothing found if this line reached
        return null;
    }
}
