package ca.ids.abms.modules.pendingtransactionapprovals;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PendingTransactionApprovalsMapper {

    List<PendingTransactionApprovalsViewModel> toViewModel(Iterable<PendingTransactionApprovals> pendingTransactionApprovals);

    PendingTransactionApprovalsViewModel toViewModel(PendingTransactionApprovals pendingTransactionApprovals);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    PendingTransactionApprovals toModel(PendingTransactionApprovalsViewModel dto);

    PendingTransactionApprovalsCsvExportModel toCsvModel(PendingTransactionApprovals pendingTransactionApprovals);

    List<PendingTransactionApprovalsCsvExportModel> toCsvModel(Iterable<PendingTransactionApprovals> pendingTransactionApprovals);
}
