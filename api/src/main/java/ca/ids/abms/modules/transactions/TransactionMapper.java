package ca.ids.abms.modules.transactions;

import ca.ids.abms.modules.transactionapprovals.TransactionApprovalsMapper;
import org.mapstruct.*;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = { TransactionApprovalsMapper.class })
public interface TransactionMapper {

    List<TransactionViewModel> toViewModel(Iterable<Transaction> transactions);

    TransactionViewModel toViewModel(Transaction transaction);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "receiptDocument", ignore = true)
    @Mapping(target = "receiptDocumentType", ignore = true)
    @Mapping(target = "receiptDocumentFileName", ignore = true)
    @Mapping(target = "approvalDocument", ignore = true)
    @Mapping(target = "supportingDocument", ignore = true)
    Transaction toModel(TransactionViewModel dto);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "currency", source = "currency.currencyCode")
    @Mapping(target = "transactionType", source = "transactionType.name")
    @Mapping(target = "bankAccountName", ignore = true)
    @Mapping(target = "amount", ignore = true)
    TransactionCsvExportModel toCsvModel(Transaction transaction);

    List<TransactionCsvExportModel> toCsvModel(Iterable<Transaction> transactions);

    @AfterMapping
    default void getBankAccountName(final Transaction transaction, @MappingTarget final TransactionCsvExportModel target) {
        if (transaction.getBankAccountName() != null && !transaction.getBankAccountName().isEmpty()) {
            target.setBankAccountName(transaction.getBankAccountName() + " (" + transaction.getBankAccountNumber() + ")");
        }
    }

    @AfterMapping
    default void getAmount(final Transaction transaction, @MappingTarget final TransactionCsvExportModel target) {
        target.setAmount(Math.abs(transaction.getAmount()));
    }

    @AfterMapping
    default void toViewModelPaymentsExported(final Transaction source, @MappingTarget final TransactionViewModel target) {
        boolean isExported = true;

        if (source.getTransactionPayments() != null) {
            for (TransactionPayment payment : source.getTransactionPayments()) {
                if (!payment.getExported())
                    isExported = false;
            }
        }

        target.setPaymentsExported(isExported);
    }

    @AfterMapping
    default void toModelPaymentDefaults(final TransactionViewModel source, @MappingTarget final Transaction target) {
        if (target.getPaymentAmount() == null) target.setPaymentAmount(source.getAmount());
        if (target.getPaymentCurrency() == null) target.setPaymentCurrency(source.getCurrency());
        if (target.getPaymentExchangeRate() == null) target.setPaymentExchangeRate(1.0);
    }

    @AfterMapping
    default void toViewModelPaymentDefaults(final Transaction source, @MappingTarget final TransactionViewModel target) {
        if (target.getPaymentAmount() == null) target.setPaymentAmount(source.getAmount());
        if (target.getPaymentCurrency() == null) target.setPaymentCurrency(source.getCurrency());
        if (target.getPaymentExchangeRate() == null) target.setPaymentExchangeRate(1.0);
    }
}
