package ca.ids.abms.modules.selfcareportal.approvalrequests;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface SelfCarePortalApprovalRequestMapper {

    List<SelfCarePortalApprovalRequestViewModel> toViewModel(Iterable<SelfCarePortalApprovalRequest> requests);

    List<SelfCarePortalApprovalRequest> toModel(Iterable<SelfCarePortalApprovalRequestViewModel> requestsDto);

    SelfCarePortalApprovalRequestViewModel toViewModel(SelfCarePortalApprovalRequest requests);

    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    SelfCarePortalApprovalRequest toModel(SelfCarePortalApprovalRequestViewModel requestsDto);

    @Mapping(target = "account", source = "account.name")
    @Mapping(target = "user", source = "user.name")
    SelfCarePortalApprovalRequestCsvExportModel toCsvModel(SelfCarePortalApprovalRequest item);

    List<SelfCarePortalApprovalRequestCsvExportModel> toCsvModel(Iterable<SelfCarePortalApprovalRequest> items);
}
