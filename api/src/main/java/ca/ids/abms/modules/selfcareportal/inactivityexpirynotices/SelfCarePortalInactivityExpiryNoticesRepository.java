package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
interface SelfCarePortalInactivityExpiryNoticesRepository extends ABMSRepository<SelfCarePortalInactivityExpiryNotice, Integer> {
}
