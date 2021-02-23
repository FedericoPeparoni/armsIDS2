package ca.ids.abms.modules.accounts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccountEventMapRepository extends JpaRepository<AccountEventMap, Integer> {

    @Query(value="SELECT a.*  FROM account_event_map a WHERE a.account_id = :accountId", nativeQuery = true)
    List<AccountEventMap> findByAccountId(final @Param("accountId") int accountId);

    @Query(value="SELECT a.*  FROM account_event_map a WHERE a.account_id = :accountId AND a.notification_event_type_id = :notificationId", nativeQuery = true)
    AccountEventMap findByAccountAndNotification(final @Param("accountId") int accountId,
                                                 final @Param("notificationId") int notificationId);

}
