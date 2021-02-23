package ca.ids.abms.modules.selfcareportal.creditpayment;

import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
interface CreditPaymentTransactionMapper extends AbmsCrudMapper<CreditPaymentTransaction, CreditPaymentTransactionViewModel, CreditPaymentTransactionCsvExportModel> {

    List<CreditPaymentTransactionViewModel> toViewModel(List<CreditPaymentTransaction> creditPaymentTransactions);

    @Mapping(target = "account", source = "account.name")
    CreditPaymentTransactionCsvExportModel toCsvModel(CreditPaymentTransaction item);

    List<CreditPaymentTransactionCsvExportModel> toCsvModel(Iterable<CreditPaymentTransaction> items);
}

