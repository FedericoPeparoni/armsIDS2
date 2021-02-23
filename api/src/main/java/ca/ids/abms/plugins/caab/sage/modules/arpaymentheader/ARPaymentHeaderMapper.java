package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

import ca.ids.abms.modules.bankcode.BankCode;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDateFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDecimalFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.mapper.column.SimpleColumnMapper;
import org.apache.commons.lang.StringUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    uses = { CaabSageDateFormat.class, CaabSageDecimalFormat.class },
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ARPaymentHeaderMapper extends SimpleColumnMapper<ARPaymentHeader> {

    @Autowired
    CaabSageMapperHelper caabSageMapperHelper;

    // region TransactionPayment @Mapping

    @Mapping(target = "currency", source = "currency.currencyCode")
    @Mapping(target = "documentDate", source = "transaction.transactionDateTime")
    @Mapping(target = "entryDescription", source = "transaction.description")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "invoiceNumber", source = "billingLedger.invoiceNumber")
    @Mapping(target = "uploadedOn", ignore = true)
    public abstract ARPaymentHeader toARPaymentHeader(final TransactionPayment transactionPayment);

    // endregion TransactionPayment @Mapping

    // region TransactionPayment @AfterMapping

    /**
     * Each billing centre has three banking codes – one each for BWD, USD and ZAR
     */
    @AfterMapping
    void toARPaymentHeaderBankCode(final TransactionPayment source, @MappingTarget final ARPaymentHeader target) {
        BankCode bankCode = caabSageMapperHelper.getBankCode(source.getTransaction());
        target.setBankCode(bankCode.getCode());
    }

    /**
     * Customer code for payment header, cannot be null.
     */
    @AfterMapping
    void toARPaymentHeaderCustomerCode(final TransactionPayment source, @MappingTarget final ARPaymentHeader target) {

        // get customer code by transaction payment
        String customerCode = caabSageMapperHelper.getCustomerCode(source);

        // validate that customer code is not null or blank (whitespace)
        if (StringUtils.isNotBlank(customerCode))
            target.setCustomerCode(customerCode);
        else
            caabSageMapperHelper.handleCustomerCodeError(source);
    }

    /**
     * Document total, always positive.
     */
    @AfterMapping
    void toARPaymentHeaderDocumentTotal(final TransactionPayment source, @MappingTarget final ARPaymentHeader target) {
        // amounts for credits are always positive
        target.setDocumentTotal(CaabSageDecimalFormat.format(Math.abs(source.getAmount())));
    }

    /**
     * DocumentType defined from TransactionPayment isAccountCredit value.
     */
    @AfterMapping
    void toARPaymentHeaderDocumentType(final TransactionPayment source, @MappingTarget final ARPaymentHeader target) {
        // defined by isAccountCredit, prepayment is use of account credit
        target.setDocumentType(source.getIsAccountCredit()
            ? ARPaymentHeaderConstants.DOCUMENT_TYPE_PREPAYMENT
            : ARPaymentHeaderConstants.DOCUMENT_TYPE_RECEIPT);
    }

    /**
     * ReceiptNumber defined from TransactionPayment transaction.receiptNumber value.
     */
    @AfterMapping
    void toARPaymentHeaderReceiptNumber(final TransactionPayment source, @MappingTarget final ARPaymentHeader target) {

        if (source.getTransaction() != null && StringUtils.isNotBlank(source.getTransaction().getReceiptNumber()))
            target.setReceiptNumber(source.getTransaction().getReceiptNumber());
        else
            throw new NullPointerException("Transaction payment receipt number must be defined");
    }

    // endregion TransactionPayment @AfterMapping
}
