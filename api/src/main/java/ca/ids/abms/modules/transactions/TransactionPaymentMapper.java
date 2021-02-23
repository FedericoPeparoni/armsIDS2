package ca.ids.abms.modules.transactions;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TransactionPaymentMapper {

    @Mapping(target = "currency", source = "currency.currencyCode")
    @Mapping(target = "invoiceNumber", source = "billingLedger.invoiceNumber")
    TransactionPaymentCsvExportModel toCsvModel(TransactionPayment transactionPayment);

    List<TransactionPaymentCsvExportModel> toCsvModel(Iterable<TransactionPayment> transactionPayments);
}
