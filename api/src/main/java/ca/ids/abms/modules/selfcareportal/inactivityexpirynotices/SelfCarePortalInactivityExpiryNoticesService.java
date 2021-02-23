package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.modules.common.services.AbmsCrudService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SelfCarePortalInactivityExpiryNoticesService extends AbmsCrudService<SelfCarePortalInactivityExpiryNotice, Integer> {

    private final SelfCarePortalInactivityExpiryNoticesRepository selfCarePortalInactivityExpiryNoticesRepository;

    public SelfCarePortalInactivityExpiryNoticesService(final SelfCarePortalInactivityExpiryNoticesRepository selfCarePortalInactivityExpiryNoticesRepository) {
        super(selfCarePortalInactivityExpiryNoticesRepository);
        this.selfCarePortalInactivityExpiryNoticesRepository = selfCarePortalInactivityExpiryNoticesRepository;
    }

    @Transactional(readOnly = true)
    public List<SelfCarePortalInactivityExpiryNotice> findAll() {
        return selfCarePortalInactivityExpiryNoticesRepository.findAll();
    }
}
