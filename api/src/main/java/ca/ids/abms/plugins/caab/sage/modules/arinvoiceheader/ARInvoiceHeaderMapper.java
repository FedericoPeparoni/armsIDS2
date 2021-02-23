package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDateFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDecimalFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.mapper.column.SimpleColumnMapper;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    uses = { CaabSageDateFormat.class, CaabSageDecimalFormat.class},
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ARInvoiceHeaderMapper extends SimpleColumnMapper<ARInvoiceHeader> {

    @Autowired
    CaabSageMapperHelper caabSageMapperHelper;

    // region BillingLedger @Mapping

    /**
     * Billing Ledger mapping to ARInvoiceHeader.
     */
    @Mapping(target = "currency", source = "invoiceCurrency.currencyCode")
    @Mapping(target = "documentDate", source = "invoicePeriodOrDate")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "invoiceType", constant = ARInvoiceHeaderConstants.INVOICE_TYPE)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "poNumber", ignore = true)
    @Mapping(target = "specialInstructions", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    public abstract ARInvoiceHeader toARInvoiceHeader(final BillingLedger billingLedger);

    // endregion BillingLedger @Mapping

    // region Transaction @Mapping

    /**
     * Transaction mapping to ARInvoiceHeader.
     */
    @Mapping(target = "currency", source = "currency.currencyCode")
    @Mapping(target = "documentDate", source = "transactionDateTime")
    @Mapping(target = "documentDescription", source = "description")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "invoiceType", constant = ARInvoiceHeaderConstants.INVOICE_TYPE)
    @Mapping(target = "orderNumber", ignore = true)
    @Mapping(target = "poNumber", ignore = true)
    @Mapping(target = "specialInstructions", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    public abstract ARInvoiceHeader toARInvoiceHeader(final Transaction transaction);

    // endregion Transaction @Mapping

    // region BillingLedger @AfterMapping

    /**
     * CustomerCode and DocumentNumber, billing ledger account dictates code.
     */
    @AfterMapping
    void toARInvoiceHeaderCustomerCodeAndDocumentNumber(final BillingLedger source, @MappingTarget ARInvoiceHeader target) {

        // get customer code by billing ledger
        String customerCode = caabSageMapperHelper.getCustomerCode(source);

        // validate that customer code is not null or blank (whitespace)
        if (StringUtils.isBlank(customerCode)) caabSageMapperHelper.handleCustomerCodeError(source);

        // set customer code and document number
        target.setCustomerCode(customerCode);
        target.setDocumentNumber(caabSageMapperHelper.getDocumentNumber(source.getInvoiceNumber(), customerCode));
    }

    /**
     * Header description, billing ledger dictates description.
     */
    @AfterMapping
    void toARInvoiceHeaderDescription(final BillingLedger source, @MappingTarget ARInvoiceHeader target) {
        target.setDocumentDescription(caabSageMapperHelper.billingLedgerDescriptionByInvoiceType(source));
    }

    /**
     * Document type, billing ledger dictates document type.
     */
    @AfterMapping
    void toARInvoiceHeaderDocumentType(final BillingLedger source, @MappingTarget ARInvoiceHeader target) {
        // document type dependent on invoice type
        if (InvoiceType.DEBIT_NOTE.equals(InvoiceType.forValue(source.getInvoiceType()))) {
            target.setDocumentType(ARInvoiceHeaderConstants.DOCUMENT_TYPE_DEBIT_NOTE);
        } else {
            target.setDocumentType(ARInvoiceHeaderConstants.DOCUMENT_TYPE_INVOICE);
        }
    }

    // endregion BillingLedger @AfterMapping

    // region Transaction @AfterMapping

    /**
     * CustomerCode and DocumentNumber, transaction account dictates code.
     */
    @AfterMapping
    void toARInvoiceHeaderCustomerCodeAndDocumentNumber(final Transaction source, @MappingTarget ARInvoiceHeader target) {

        // get customer code by transaction
        String customerCode = caabSageMapperHelper.getCustomerCode(source);

        // validate that customer code is not null or blank (whitespace)
        if (StringUtils.isBlank(customerCode)) caabSageMapperHelper.handleCustomerCodeError(source);

        // set customer code and document number
        target.setCustomerCode(customerCode);
        target.setDocumentNumber(caabSageMapperHelper.getDocumentNumber(source.getReceiptNumber(), customerCode));
    }

    /**
     * Document type, transaction dictates document type.
     */
    @AfterMapping
    void toARInvoiceHeaderDocumentType(final Transaction source, @MappingTarget ARInvoiceHeader target) {
        // document type dependent on transaction type
        if (source.getTransactionType().isDebit()) {
            target.setDocumentType(ARInvoiceHeaderConstants.DOCUMENT_TYPE_DEBIT_NOTE);
        } else if (source.getTransactionType().isCredit()) {
            target.setDocumentType(ARInvoiceHeaderConstants.DOCUMENT_TYPE_CREDIT_NOTE);
        } else {
            target.setDocumentType(ARInvoiceHeaderConstants.DOCUMENT_TYPE_INVOICE);
        }
    }

    // endregion
}
