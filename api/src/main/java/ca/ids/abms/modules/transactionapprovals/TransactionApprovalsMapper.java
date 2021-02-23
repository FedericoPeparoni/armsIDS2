package ca.ids.abms.modules.transactionapprovals;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface TransactionApprovalsMapper {

    List<TransactionApprovalsViewModel> toViewModel(Iterable<TransactionApprovals> transactionApprovals);

    TransactionApprovalsViewModel toViewModel(TransactionApprovals transactionApprovals);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    TransactionApprovals toModel(TransactionApprovalsViewModel dto);

    TransactionApprovalsCsvExportModel toCsvModel(TransactionApprovals transactionApprovals);

    List<TransactionApprovalsCsvExportModel> toCsvModel(Iterable<TransactionApprovals> transactionApprovals);
}
