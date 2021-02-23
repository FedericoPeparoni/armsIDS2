package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.plugins.common.utilities.PluginMapperHelper;
import ca.ids.abms.plugins.kcaa.erp.modules.currency.KcaaErpCurrencyConstants;
import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;
import ca.ids.abms.modules.transactions.Transaction;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SalesHeaderMapper {

    // never set this currency in ERP database, blank is considered Kenya Shillings
    private static final String DEFAULT_CURRENCY_CODE = KcaaErpCurrencyConstants.BLANK_CURRENCY_CODE;

    @Autowired
    protected AccountExternalChargeCategoryService accountExternalChargeCategoryService;

    @Autowired
    protected ReportHelper reportHelper;

    @Autowired
    protected UserService userService;

    @Autowired
    protected PluginMapperHelper pluginMapperHelper;

    public abstract List<SalesHeader> toSalesHeader(List<BillingLedger> billingLedgers);

    @Mapping(target = "no", source = "invoiceNumber")
    @Mapping(target = "externalDocumentNo", source = "id")
    @Mapping(target = "invoiceType", constant = SalesHeaderConstants.INVOICE_TYPE)
    @Mapping(target = "documentType", constant = SalesHeaderConstants.DEBIT_DOCUMENT_TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesHeaderConstants.SHORTCUT_DIMENSION_CODE_1)
    public abstract SalesHeader toSalesHeader(BillingLedger billingLedger);

    @Mapping(target = "no", source = "receiptNumber")
    @Mapping(target = "externalDocumentNo", source = "id")
    @Mapping(target = "invoiceType", constant = SalesHeaderConstants.INVOICE_TYPE)
    @Mapping(target = "documentType", constant = SalesHeaderConstants.CREDIT_DOCUMENT_TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesHeaderConstants.SHORTCUT_DIMENSION_CODE_1)
    @Mapping(target = "transactionType", ignore = true)
    public abstract SalesHeader toSalesHeader(Transaction transaction);

    @AfterMapping
    void toSalesHeaderCurrency(final BillingLedger source, @MappingTarget SalesHeader target) {

        // leave bank if KES currency
        String currencyCode;
        Double currencyFactor;
        if (source.getInvoiceCurrency() != null &&
            source.getInvoiceCurrency().getCurrencyCode() != null &&
            !source.getInvoiceCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE) &&
            source.getInvoiceCurrency().getExternalAccountingSystemIdentifier() != null) {
            currencyCode = source.getInvoiceCurrency().getExternalAccountingSystemIdentifier();
            currencyFactor = source.getInvoiceExchange();
        } else {
            currencyCode = DefaultValue.STRING;
            currencyFactor = DefaultValue.DOUBLE;
        }
        target.setCurrencyCode(currencyCode);
        target.setCurrencyFactor(currencyFactor);
    }

    @AfterMapping
    void toSalesHeaderCurrency(final Transaction source, @MappingTarget SalesHeader target) {

        // leave bank if KES currency
        String currencyCode;
        Double currencyFactor;
        if (source.getCurrency() != null &&
            source.getCurrency().getCurrencyCode() != null &&
            !source.getCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE) &&
            source.getCurrency().getExternalAccountingSystemIdentifier() != null) {
            currencyCode = source.getCurrency().getExternalAccountingSystemIdentifier();
            currencyFactor = source.getExchangeRate();
        } else {
            currencyCode = DefaultValue.STRING;
            currencyFactor = DefaultValue.DOUBLE;
        }
        target.setCurrencyCode(currencyCode);
        target.setCurrencyFactor(currencyFactor);
    }

    @AfterMapping
    void toSalesHeaderCustomerNo(final BillingLedger source, @MappingTarget SalesHeader target) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = findCustomerNo(source.getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setSellToCustomerNo(customerNo);
        target.setBillToCustomerNo(customerNo);
    }

    @AfterMapping
    void toSalesHeaderCustomerNo(final Transaction source, @MappingTarget SalesHeader target) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = findCustomerNo(source.getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setSellToCustomerNo(customerNo);
        target.setBillToCustomerNo(customerNo);
    }

    @AfterMapping
    void toSalesHeaderDates(final BillingLedger source, @MappingTarget SalesHeader target) {

        // set dates without time values
        LocalDateTime defaultDate = DefaultValue.LOCAL_DATE_TIME.toLocalDate().atStartOfDay();

        // general header dates from invoice date of issue
        LocalDateTime invoiceDateOfIssue;
        if (source.getInvoiceDateOfIssue() != null)
            invoiceDateOfIssue = source.getInvoiceDateOfIssue().toLocalDate().atStartOfDay();
        else
            invoiceDateOfIssue = defaultDate;
        target.setOrderDate(invoiceDateOfIssue);
        target.setPostingDate(invoiceDateOfIssue);
        target.setShipmentDate(invoiceDateOfIssue);
        target.setDocumentDate(invoiceDateOfIssue);

        // due dates from payment due date
        LocalDateTime dueDate;
        if (source.getPaymentDueDate() != null)
            dueDate = source.getPaymentDueDate().toLocalDate().atStartOfDay();
        else
            dueDate = defaultDate;
        target.setDueDate(dueDate);
        target.setPrepaymentDueDate(dueDate);
    }

    @AfterMapping
    void toSalesHeaderDates(final Transaction source, @MappingTarget SalesHeader target) {

        // set dates without time values
        LocalDateTime transactionDateTime;
        if (source.getTransactionDateTime() != null)
            transactionDateTime = source.getTransactionDateTime().toLocalDate().atStartOfDay();
        else
            transactionDateTime = DefaultValue.LOCAL_DATE_TIME.toLocalDate().atStartOfDay();
        target.setOrderDate(transactionDateTime);
        target.setPostingDate(transactionDateTime);
        target.setShipmentDate(transactionDateTime);
        target.setDocumentDate(transactionDateTime);
    }

    @AfterMapping
    void toSalesHeaderPostingDescription(final BillingLedger source, @MappingTarget SalesHeader target) {

        // prefix description with account name if exists
        String description = "";
        if (source.getAccount() != null && source.getAccount().getName() != null
            && !source.getAccount().getName().isEmpty())
            description += source.getAccount().getName() + " - ";

        // invoice type dictates description
        target.setPostingDescription(description + pluginMapperHelper.billingLedgerDescriptionByInvoiceType(source));
    }

    @AfterMapping
    void toSalesHeaderPostingDescription(final Transaction source, @MappingTarget SalesHeader target) {

        StringJoiner description = new StringJoiner(" ");

        // prefix description with account name if exists
        if (source.getAccount() != null && source.getAccount().getName() != null
            && !source.getAccount().getName().isEmpty())
            description.add(source.getAccount().getName() + " -");

        description.add("Credit Memo");

        // append description with date of transaction
        if (source.getTransactionDateTime() != null && reportHelper != null)
            description.add("(" + reportHelper.formatDateUtc(source.getTransactionDateTime(), reportHelper.getDateFormat()) + ")");

        target.setPostingDescription(description.toString());
    }

    @AfterMapping
    void toSalesHeaderShortcutDimensionCode(final BillingLedger source, @MappingTarget SalesHeader target) {
        String code2;
        if (source.getUser() != null && source.getUser().getBillingCenter() != null &&
            source.getUser().getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            code2 = source.getUser().getBillingCenter().getExternalAccountingSystemIdentifier();
        else
            code2 = DefaultValue.STRING;
        target.setShortcutDimension2Code(code2);
    }

    @AfterMapping
    void toSalesHeaderShortcutDimensionCode(final Transaction source, @MappingTarget SalesHeader target) {
        String code2;
        User user = userService.getUserByLogin(source.getCreatedBy());
        if (user != null && user.getBillingCenter() != null &&
            user.getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            code2 = user.getBillingCenter().getExternalAccountingSystemIdentifier();
        else
            code2 = DefaultValue.STRING;
        target.setShortcutDimension2Code(code2);
    }

    /**
     * Get valid external system identifier from account that should be used as
     * the customer number.
     *
     * @param account account
     * @return external system identifier
     */
    private String findCustomerNo(final Account account) {

        // assert that account is not null and has an id value
        if (account == null || account.getId() == null)
            return null;

        // find all account external charge categories and return first
        // valid external system identifier value
        for (AccountExternalChargeCategory external : accountExternalChargeCategoryService.findByAccount(account.getId())) {
            String identifier = external.getExternalSystemIdentifier();
            if (identifier != null && !identifier.isEmpty())
                return identifier;
        }

        // return null if no valid external system identifiers found
        return null;
    }
}
