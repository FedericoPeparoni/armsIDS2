package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SelfCarePortalApprovalRequestRepository extends ABMSRepository<SelfCarePortalApprovalRequest, Integer> {

    @Query(value="SELECT a.id FROM SelfCarePortalApprovalRequest a WHERE a.objectId = :objectId AND a.status = 'open'")
    Integer findOpenApprovalRequestByObjectId(@Param("objectId") Integer objectId);


    @Query(value="SELECT distinct a.account FROM SelfCarePortalApprovalRequest a WHERE a.user.id = :userId AND a.requestDataset = 'flight schedule'")
    List<Account> getAccountFromApprovalRequestListForFlightSchedule(@Param("userId") Integer userId);
}
