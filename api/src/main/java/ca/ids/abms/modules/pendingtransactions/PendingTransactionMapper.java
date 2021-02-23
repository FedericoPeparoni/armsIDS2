package ca.ids.abms.modules.pendingtransactions;

import ca.ids.abms.modules.accounts.AccountMapper;
import ca.ids.abms.modules.billings.BillingLedgerMapper;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovalsMapper;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.workflows.ApprovalWorkflowMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses={AccountMapper.class, ApprovalWorkflowMapper.class, BillingLedgerMapper.class, PendingTransactionApprovalsMapper.class})
public interface PendingTransactionMapper {

    List<PendingTransactionViewModel> toViewModel(Iterable<PendingTransaction> transactions);

    PendingTransactionViewModel toViewModel(PendingTransaction transaction);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "approvalDocument", ignore = true)
    @Mapping(target = "supportingDocument", ignore = true)
    PendingTransaction toModel(PendingTransactionViewModel dto);

    List<PendingChargeAdjustmentViewModel> pendingChargesToViewModel(Iterable<PendingChargeAdjustment> pendingChargeAdjustments);

    @Mapping(target = "pendingTransaction", ignore = true)
    PendingChargeAdjustmentViewModel pendingChargesToViewModel(PendingChargeAdjustment pendingChargeAdjustments);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PendingChargeAdjustment pendingChargesToModel(PendingChargeAdjustmentViewModel dto);

    @Mapping(target = "localAmount", source = "amount")
    @Mapping(target = "localCurrency", source = "currency")
    @Mapping(target = "exchangeRateToUsd", source = "exchangeRate")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pendingChargeAdjustments", ignore = true)
    @Mapping(target = "currentApprovalLevel", ignore = true)
    @Mapping(target = "previousApprovalLevel", ignore = true)
    @Mapping(target = "relatedInvoices", ignore = true)
    @Mapping(target = "detailedInvoices", ignore = true)
    @Mapping(target = "canApprove", ignore = true)
    @Mapping(target = "canReject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "pendingTransactionApprovals", ignore = true)
    PendingTransaction transactionToPendingTransaction (Transaction transaction);

    @Mapping(source = "localAmount", target = "amount")
    @Mapping(source = "localCurrency", target = "currency")
    @Mapping(source = "exchangeRateToUsd", target = "exchangeRate")
    @Mapping(target = "exported", constant = "false")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "billingLedgerIds", ignore = true)
    @Mapping(target = "receiptDocument", ignore = true)
    @Mapping(target = "receiptDocumentType", ignore = true)
    @Mapping(target = "receiptDocumentFileName", ignore = true)
    @Mapping(target = "receiptNumber", ignore = true)
    @Mapping(target = "chargesAdjustment", ignore = true)
    @Mapping(target = "debitNoteNumber", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "transactionApprovals", ignore = true)
    @Mapping(target = "flightmovementCategory", ignore = true)
    @Mapping(target = "kraClerkName", ignore = true)
    @Mapping(target = "kraReceiptNumber", ignore = true)
    @Mapping(target = "transactionPayments", ignore = true)
    @Mapping(target = "targetCurrency", ignore = true)
    @Mapping(target = "bankAccountName", ignore = true)
    @Mapping(target = "bankAccountNumber", ignore = true)
    @Mapping(target = "bankAccountExternalAccountingSystemId", ignore = true)
    Transaction pendingTransactionToTransaction (PendingTransaction transaction);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "pendingTransaction", ignore = true)
    @Mapping(target = "invoiceType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PendingChargeAdjustment chargesToPendingCharges (ChargesAdjustment chargesAdjustment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "transaction", ignore = true)
    @Mapping(target = "billingLedger", ignore = true)
    @Mapping(target = "externalChargeCategoryName", ignore = true)
    ChargesAdjustment pendingChargesToCharges (PendingChargeAdjustment pendingChargeAdjustment);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "transactionType", source = "transactionType.name")
    @Mapping(target = "currentApprovalLevel", source = "currentApprovalLevel.approvalName")
    @Mapping(target = "previousApprovalLevel", source = "previousApprovalLevel.approvalName")
    PendingTransactionCsvExportModel toCsvModel(PendingTransaction pendingTransaction);

    List<PendingTransactionCsvExportModel> toCsvModel(Iterable<PendingTransaction> pendingTransactions);
}
