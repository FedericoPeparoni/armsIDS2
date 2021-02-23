package ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails;

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
public abstract class ARPaymentDetailsMapper  extends SimpleColumnMapper<ARPaymentDetails> {

    @Autowired
    CaabSageMapperHelper caabSageMapperHelper;

    // region @Mapping

    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "invoiceNumber", source = "billingLedger.invoiceNumber")
    @Mapping(target = "uploadedOn", ignore = true)
    public abstract ARPaymentDetails toARPaymentDetails(final TransactionPayment transactionPayment);

    // endregion @Mapping

    // region TransactionPayment @AfterMapping

    /**
     * Applied Amount, always positive.
     */
    @AfterMapping
    void toARPaymentDetailsAppliedAmount(final TransactionPayment source, @MappingTarget final ARPaymentDetails target) {
        // amounts for credits are always positive
        target.setAppliedAmount(CaabSageDecimalFormat.format(Math.abs(source.getAmount())));
    }

    /**
     * ReceiptNumber defined from TransactionPayment transaction.receiptNumber value.
     */
    @AfterMapping
    void toARPaymentHeaderReceiptNumber(final TransactionPayment source, @MappingTarget final ARPaymentDetails target) {

        if (source.getTransaction() != null && StringUtils.isNotBlank(source.getTransaction().getReceiptNumber()))
            target.setReceiptNumber(source.getTransaction().getReceiptNumber());
        else
            throw new NullPointerException("Transaction payment receipt number must be defined");
    }

    // endregion TransactionPayment @AfterMapping
}
