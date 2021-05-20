package ca.ids.abms.modules.accounts;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.enumerate.WhitelistState;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billings.BillingLedger;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountRepository extends ABMSRepository<Account, Integer> {

    /**
     * Fetches the Accounts that match the given IDs.
     *
     * @param accountIds a list of IDs to match
     * @return a list of matching accounts
     */
    List<Account> findByIdIn(List<Integer> accountIds);

    @Query(value="select a.* from accounts a join account_types at on a.account_type = at.id where at.name = :nameAccountType", nativeQuery = true)
    List<Account> findByAccountType(@Param("nameAccountType") String nameAccountType);
    
    @Query(value="select a.* from abms.accounts a where a.id = :id", nativeQuery = true)
    Account findAccountById(@Param("id") Integer id);
    
    @Query(value="select a from Account a join fetch a.billingLedgers join fetch a.aircraftRegistrations where a.id = :id")
    Account findAccountByIdwithBillingLedgerAndAircraft(@Param("id") Integer id);
       
    @Query(value="SELECT a.* FROM accounts a WHERE a.icao_code = ?1", nativeQuery = true)
    Account findByIcaoCode(String icaoCode);

    @Query("SELECT a FROM Account a WHERE a.icaoCode = ?1 OR a.iataCode = ?2")
    List<Account> findByIcaoOrIataCode(String icaoCode, String iataCode);

    @Query(value="SELECT a.*  FROM accounts a WHERE NOT cash_account", nativeQuery = true)
    List<Account> findAllExcludingCash();

    @Query(value="select a.* from accounts a join account_users_map aum on a.id = aum.account_id where aum.user_id = :userId", nativeQuery = true)
    List<Account> findByUserId (@Param("userId") Integer userId);

    @Query(value="SELECT a FROM Account a " +
                 "WHERE a.active = true " +
                 "AND a.cashAccount = false " +
                 "AND a.accountType.id = :accountTypeId " +
                 "ORDER BY a.name")
    List<Account> findActiveCreditMinimalReturnAviationByType(@Param("accountTypeId") Integer accountTypeId);

    @Query(value="SELECT a from Account a " +
                 "JOIN a.accountType act " +
                 "WHERE a.active = true " +
                 "AND a.cashAccount = false " +
                 "AND act.name in ('GeneralAviation','Airline','Charter','Unified Tax') " +
                 "ORDER BY a.name")
    List<Account> findAllActiveCreditMinimalReturnAviation();

    /*
       BUG 74598: This query should return only 1 record, otherwise we will get NonUniqueResultException.
       We should not use LIKE %. Instead, we should use exact match.
    */
    @Query(value="SELECT a.*  FROM accounts a WHERE a.opr_identifier = ?1", nativeQuery = true)
    Account findByOprIdentifier(String oprIdentifier);

    @Query(value="SELECT a.*  FROM accounts a WHERE a.name LIKE %?1 OR a.alias LIKE %?1 ", nativeQuery = true)
    Account findByNameOrAlias(String nameOrAlias);

    @Query(value="SELECT a.*  FROM accounts a WHERE a.name LIKE %?1 OR a.alias LIKE %?1 OR a.opr_identifier LIKE %?1 ", nativeQuery = true)
    Account findByNameOrAliasOrOperator(String nameOrAlias);

    // return a list of ALL accounts where invoices assigned to the account are overdue and not paid
    @Query(value="SELECT DISTINCT a.* FROM accounts a " +
        " JOIN billing_ledgers bl ON bl.account_id = a.id " +
        " WHERE bl.amount_owing > 0 " +
        " AND bl.payment_due_date\\:\\:date < NOW()\\:\\:date", nativeQuery = true)
    List<Account> findAllWithOverdueInvoices();

    Account findById(Integer id);

    @Query(value="SELECT id, event_type FROM notification_event_types WHERE customer_notification_indicator = true", nativeQuery = true)
    List<Object>getCustomerNotifications();

    @Query(value =
    		"select ac.* " +
    		"  from accounts ac " +
    		"  join account_users_map m on m.account_id = ac.id " +
    		"  group by ac.id " +
    		"  order by ac.name, ac.id " +
    		"", nativeQuery = true)
    List<Account> findAllSelfCareUserAccounts();

    @Query (nativeQuery = true, value =
            "SELECT name FROM accounts WHERE invoice_currency = :currencyId ORDER BY 1 LIMIT :maxRows")
    List <String> getTopNamesByInvoiceCurrency(@Param("currencyId") final int currencyId, @Param("maxRows") final int maxRows);
    
    @Query (nativeQuery = true, value =
            "SELECT count(*) FROM accounts WHERE invoice_currency = :currencyId")
    int countByCurrency(@Param("currencyId") final int currencyId);

    @Query (value = "SELECT COUNT(ac) FROM Account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllBySelfCareUser(@Param ("userId") final int userId);

    @Query (nativeQuery = true, value = "SELECT DISTINCT account_id FROM account_users_map")
    List<Integer> getAllAccountsFromAccountUsersMap();

    @Query (value = "SELECT a.name FROM Account a WHERE a.id != :id")
    List<String> findAllNames(@Param("id") final int id);

    @Query (value = "SELECT a.name FROM Account a")
    List<String> findAllNames();

    @Query (value = "SELECT a.name FROM Account a WHERE a.id = :id")
    String getAccountNameById(@Param("id") final int id);

    @Modifying
    @Query("UPDATE Account ac SET ac.whitelistLastActivityDateTime = :lastActivityDateTime where ac.id = :id")
    void updateWhitelistLastActivityDateTime(@Param("id") Integer id,
                                             @Param("lastActivityDateTime") LocalDateTime lastActivityDateTime);

    @Modifying
    @Query("UPDATE Account ac SET ac.whitelistState = :whitelistState where ac.id = :id")
    void updateWhitelistState(@Param("id") Integer id,
                              @Param("whitelistState") WhitelistState whitelistState);

    @Modifying
    @Query("UPDATE Account ac SET ac.whitelistInactivityNoticeSentFlag = :whitelistInactivityNoticeSentFlag where ac.id = :id")
    void updateWhitelistInactivityNoticeSentFlag(@Param("id") Integer id,
                                                 @Param("whitelistInactivityNoticeSentFlag") boolean whitelistInactivityNoticeSentFlag);

    @Modifying
    @Query("UPDATE Account ac SET ac.whitelistExpiryNoticeSentFlag = :whitelistExpiryNoticeSentFlag where ac.id = :id")
    void updateWhitelistExpiryNoticeSentFlag(@Param("id") Integer id,
                                             @Param("whitelistExpiryNoticeSentFlag") boolean whitelistExpiryNoticeSentFlag);

    @Query (nativeQuery = true,
        value = "SELECT a.* FROM accounts a " +
            "JOIN account_users_map aum ON a.id = aum.account_id " +
            "JOIN users u ON u.id = aum.user_id " +
            "WHERE a.cash_account = TRUE " +
            "AND NOW() BETWEEN (a.whitelist_last_activity_date_time + :inactivityPeriod \\:\\:interval) " +
            "AND (a.whitelist_last_activity_date_time + :expiryPeriod \\:\\:interval)")
    List<Account> findInactiveCashAccounts(@Param("inactivityPeriod") String inactivityPeriod,
                                           @Param("expiryPeriod") String expiryPeriod);

    @Query (nativeQuery = true,
        value = "SELECT a.* FROM accounts a " +
            "JOIN account_users_map aum ON a.id = aum.account_id " +
            "JOIN users u ON u.id = aum.user_id " +
            "WHERE a.cash_account = TRUE " +
            "AND (a.whitelist_last_activity_date_time + :expiryPeriod \\:\\:interval) < NOW()")
    List<Account> findExpiredCashAccounts(@Param("expiryPeriod") String expiryPeriod);
}
