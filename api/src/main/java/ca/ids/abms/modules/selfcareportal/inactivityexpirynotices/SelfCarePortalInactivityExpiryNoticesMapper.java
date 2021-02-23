package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.modules.accounts.AccountMapper;
import ca.ids.abms.modules.common.mappers.AbmsCrudMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(uses = { AccountMapper.class })
public interface SelfCarePortalInactivityExpiryNoticesMapper  extends AbmsCrudMapper<SelfCarePortalInactivityExpiryNotice, SelfCarePortalInactivityExpiryNoticesViewModel,
    SelfCarePortalInactivityExpiryNoticesCsvExportModel> {

    List<SelfCarePortalInactivityExpiryNoticesViewModel> toViewModel(Iterable<SelfCarePortalInactivityExpiryNotice> items);

    List<SelfCarePortalInactivityExpiryNotice> toModel(Iterable<SelfCarePortalInactivityExpiryNoticesViewModel> itemsDto);

    SelfCarePortalInactivityExpiryNoticesViewModel toViewModel(SelfCarePortalInactivityExpiryNotice item);

    SelfCarePortalInactivityExpiryNotice toModel(SelfCarePortalInactivityExpiryNoticesViewModel itemDto);

    @Mapping(target = "account", source = "account.name")
    SelfCarePortalInactivityExpiryNoticesCsvExportModel toCsvModel(SelfCarePortalInactivityExpiryNotice item);

    List<SelfCarePortalInactivityExpiryNoticesCsvExportModel> toCsvModel(Iterable<SelfCarePortalInactivityExpiryNotice> items);
}
