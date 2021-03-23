package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.db.ABMSRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public interface AircraftRegistrationRepository extends ABMSRepository<AircraftRegistration, Integer> {

    @Query(
        "select ar from AircraftRegistration ar where ( ?1 <= ar.registrationExpiryDate "
        + "and ?2 >= ar.registrationStartDate and ar.registrationNumber = ?3 )"
    )
    List<AircraftRegistration> getOverlapsDates(LocalDateTime startDate, LocalDateTime endDate, String registrationNumber);

    @Query(
        "select ar from AircraftRegistration ar where ( ?1 <= ar.registrationExpiryDate "
        + "and ?2 >= ar.registrationStartDate and ar.registrationNumber = ?3 and ar.id != ?4)"
    )
    List<AircraftRegistration> getOverlapsDates(LocalDateTime startDate, LocalDateTime endDate, String registrationNumber, Integer id);

    @Query(
            "select ar from AircraftRegistration ar where ar.registrationNumber = ?1 and ar.registrationExpiryDate >= ?2 and ar.registrationStartDate <= ?2"
        )
    List<AircraftRegistration> findAircraftRegistrationByRegistrationNumberAndCheckDate(String registrationNumber, LocalDateTime dateOfFlight);

    List<AircraftRegistration> findAircraftRegistrationByRegistrationNumber(String registrationNumber);

    @Query(value="select ar.* from " +
        "aircraft_registrations ar " +
        "where ar.aircraft_type_id=:aircraftTypeId ",
        nativeQuery = true)
    List<AircraftRegistration> findAircraftRegistrationByAircraftTypeId(@Param("aircraftTypeId") Integer aircraftTypeId);

    /**
     * Find aircraft registrations that belong to accounts, which have 1 or more users.
     */
    @Query (value = "SELECT ar FROM AircraftRegistration ar " +
                    "JOIN ar.account ac JOIN ac.accountUsers au " +
                    "WHERE lower(ar.registrationNumber) LIKE '%' || :searchFilter || '%' " +
                    "OR lower(ar.aircraftType.aircraftType) LIKE '%' || :searchFilter || '%' " +
                    "OR lower(ar.account.name) LIKE '%' || :searchFilter || '%'" +
                    "GROUP BY ar.id ORDER BY ar.registrationNumber")
    List<AircraftRegistration> findAllAircraftRegistrationForSelfCareAccounts(@Param("searchFilter") String searchFilter);

    @Query (value = "SELECT ar FROM AircraftRegistration ar " +
                    "JOIN ar.account ac JOIN ac.accountUsers au " +
                    "GROUP BY ar.id ORDER BY ar.registrationNumber")
    List<AircraftRegistration> findAllAircraftRegistrationForSelfCareAccounts();

    @Query (value = "SELECT ar FROM AircraftRegistration ar " +
                    "JOIN ar.account ac JOIN ac.accountUsers au " +
                    "WHERE au.id = :userId " +
                    "AND (lower(ar.registrationNumber) LIKE '%' || :searchFilter || '%' " +
                    "OR lower(ar.aircraftType.aircraftType) LIKE '%' || :searchFilter || '%' " +
                    "OR lower(ar.account.name) LIKE '%' || :searchFilter || '%') " +
                    "GROUP BY ar.id ORDER BY ar.registrationNumber")
    List<AircraftRegistration> findAircraftRegistrationForSelfCareUser(@Param("userId") Integer userId, @Param("searchFilter") String searchFilter);

    @Query (value = "SELECT ar FROM AircraftRegistration ar " +
                    "JOIN ar.account ac JOIN ac.accountUsers au " +
                    "WHERE au.id = :userId " +
                    "GROUP BY ar.id ORDER BY ar.registrationNumber")
    List<AircraftRegistration> findAircraftRegistrationForSelfCareUser(@Param("userId") Integer userId);

    @Query (value = "SELECT COUNT(ar) FROM AircraftRegistration ar JOIN ar.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllForSelfCareUser(@Param ("userId") final int userId);

    @Query (value = "SELECT COUNT(ar) FROM AircraftRegistration ar JOIN ar.account ac JOIN ac.accountUsers au")
    long countAllForSelfCareAccounts();
    
    @Modifying
    @Query("UPDATE AircraftRegistration SET coaIssueDate = :coa_issue_date, coaExpiryDate = :coa_expiry_date where id = :id")
    void updateAircraftRegistrationCOADatesById(@Param("id") Integer id,
                                             @Param("coa_issue_date") Timestamp coa_issue_date,
                                             @Param("coa_expiry_date") Timestamp coa_expiry_date); 
    
    @Modifying
    @Query("UPDATE AircraftRegistration SET coaIssueDate = :coa_issue_date, coaExpiryDate = :coa_expiry_date where id = :id")
    void updateAircraftRegistrationByIdAndDates(@Param("id") Integer id,
                                             @Param("coa_issue_date") LocalDateTime coa_issue_date,
                                             @Param("coa_expiry_date") LocalDateTime coa_expiry_date); 
    
}
