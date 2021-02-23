package ca.ids.abms.modules.workflows;

import ca.ids.abms.modules.roles.RoleMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses={RoleMapper.class})
public interface ApprovalWorkflowMapper {

    List<ApprovalWorkflow> toModel (List<ApprovalWorkflowViewModel> dtoList);

    List<ApprovalWorkflowViewModel> toViewModel (Iterable<ApprovalWorkflow> entityList);

    @Mapping(target = "approvalUnderLevel", source = "approvalUnder.level")
    @Mapping(target = "approvalOverLevel", source = "approvalOver.level")
    @Mapping(target = "rejectedLevel", source = "rejected.level")
    ApprovalWorkflowViewModel toViewModel (ApprovalWorkflow entity);

    @Mapping(target = "approvalUnder", ignore = true)
    @Mapping(target = "approvalOver", ignore = true)
    @Mapping(target = "rejected", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    ApprovalWorkflow toModel (ApprovalWorkflowViewModel dto);

    @Mapping(target = "approvalGroup", source = "approvalGroup.name")
    @Mapping(target = "thresholdCurrency", source = "thresholdCurrency.currencyCode")
    @Mapping(target = "approvalUnderLevel", source = "approvalUnder.level")
    @Mapping(target = "approvalOverLevel", source = "approvalOver.level")
    @Mapping(target = "rejectedLevel", source = "rejected.level")
    ApprovalWorkflowCsvExportModel toCsvModel(ApprovalWorkflow item);

    List<ApprovalWorkflowCsvExportModel> toCsvModel(Iterable<ApprovalWorkflow> items);

}
