package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Repository
public interface FlightMovementRepository extends ABMSRepository<FlightMovement, Integer> {

    FlightMovement findByFlightId(String flightId);

    List <FlightMovement> findBySpatiaFplObjectIdOrderBySpatiaFplObjectIdDesc (Long catalogueFplObjectId);

    FlightMovement findTop1ByItem18RegNumOrderByIdDesc(String item18RegNum);

    Page<FlightMovement> findAllByStatus(Pageable pageable, FlightMovementStatus status);

    @Query(value="SELECT fm.* FROM flight_movements fm WHERE fm.flight_category_id = :flightCategoryId", nativeQuery = true)
    List<FlightMovement> findAllByFlightCategory(@Param("flightCategoryId") Integer flightCategoryId);

    Page<FlightMovement> findAllByResolutionErrorsContaining(String issue, Pageable pageable);

    @Query("SELECT fm FROM FlightMovement fm WHERE fm.id IN :ids ORDER BY fm.dateOfFlight, fm.depTime, fm.flightId, fm.id")
    List<FlightMovement> findAllByIdIn(@Param("ids") List<Integer> ids);

    @Query(value="SELECT fm.* FROM flight_movements fm WHERE fm.account = :accountID", nativeQuery = true)
    List<FlightMovement> findAllByAccount(@Param("accountID") Integer accountID);

    @Query(value="SELECT fm.*  FROM flight_movements fm INNER JOIN accounts a ON fm.account= a.id WHERE fm.account = :accountID AND fm.status = :status AND fm.date_of_flight >= :startInterval\\:\\:timestamp AND  fm.date_of_flight <= :endInterval\\:\\:timestamp ", nativeQuery = true)
    List<FlightMovement> findAllByAccountIntervalDate(@Param("accountID") Integer accountID, @Param("status")String status, @Param("startInterval") Date startInterval, @Param("endInterval") Date endInterval);

    @Query(value="SELECT fm.*  FROM flight_movements fm INNER JOIN accounts a ON fm.account=a.id WHERE a.iata_member=true AND fm.status = :status AND fm.billing_date >= :startInterval\\:\\:timestamp AND  fm.billing_date <= :endInterval\\:\\:timestamp ", nativeQuery = true)
    List<FlightMovement> findAllByAssociatedAccountIataByIntervalDate(@Param("status")String status, @Param("startInterval") Date startInterval, @Param("endInterval") Date endInterval);

    @Query(value=
            "select flm " +
            "from FlightMovement flm " +
            "where flm.flightId = :flightId and " +
            "flm.depAd = :depAd and " +
            "flm.dateOfFlight between :startDate and :endDate")
    List<FlightMovement> findAllByFlightIdAndDepAdAndDateOfFlight(
            Sort sort,
            @Param("flightId") String flightId,
            @Param("depAd") String depAd,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndDepAdAndDestAd(Sort sort, String flightId, LocalDateTime dateOfFlight, String depAd, String destAd);

    @Query("SELECT fm FROM FlightMovement fm WHERE " +
        "fm.flightId = :flightId AND " +
        "fm.depAd = :depAd AND (" +
        "(fm.dateOfFlight = :dateOfContact AND fm.depTime <= :timeOfContact) OR " +
        "(fm.dateOfFlight = :dateOfContactMinusDay AND fm.arrivalTime <> '' AND fm.depTime > fm.arrivalTime AND fm.arrivalTime >= :timeOfContact))")
    List<FlightMovement> findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
        @Param("flightId") final String flightId,
        @Param("depAd") final String depAd,
        @Param("dateOfContact") final LocalDateTime dateOfContact,
        @Param("dateOfContactMinusDay") final LocalDateTime dateOfContactMinusDay,
        @Param("timeOfContact") final String timeOfContact);

    /**
     * Find all flight movements with same flight id that overlap the provided departure time and date of flight.
     */
    @Query("SELECT fm FROM FlightMovement fm WHERE fm.flightId = :flightId AND (fm.deltaFlight = TRUE OR fm.thruFlight = TRUE) AND (" +
        "(fm.dateOfFlight = :dateOfFlight AND fm.depTime <= fm.arrivalTime AND fm.depTime <= :depTime AND fm.arrivalTime >= :depTime) OR " +
        "(fm.dateOfFlight = :dateOfFlight AND fm.depTime > fm.arrivalTime AND fm.depTime <= :depTime) OR " +
        "(fm.dateOfFlight = :dateOfFlightMinusDay AND fm.depTime > fm.arrivalTime AND fm.arrivalTime >= :depTime)) " +
        "ORDER BY fm.dateOfFlight DESC, fm.depTime DESC, fm.flightId, fm.id")
    List<FlightMovement> findAllOverlapByFlightIdAndDepTimeAndDateOfFlight(
        @Param("flightId") final String flightId,
        @Param("depTime") final String depTime,
        @Param("dateOfFlight") final LocalDateTime dateOfFlight,
        @Param("dateOfFlightMinusDay") final LocalDateTime dateOfFlightMinusDay);

    // returns a list of `flight_movements` that are linked to an invoice
    @Query(value="SELECT fm.* FROM flight_movements fm WHERE fm.enroute_invoice_id = :invoiceId OR fm.passenger_invoice_id = :invoiceId OR fm.other_invoice_id = :invoiceId", nativeQuery = true)
    List<FlightMovement> findAllByAssociatedBillingLedgerId(@Param("invoiceId") Integer invoiceId);

    @Query(value="SELECT fm FROM FlightMovement fm WHERE fm.enrouteInvoiceId = :invoiceId OR fm.passengerInvoiceId = :invoiceId OR fm.otherInvoiceId = :invoiceId")
    Page<FlightMovement> findAllByAssociatedBillingLedgerId(@Param("invoiceId") Integer invoiceId, Pageable pageable);

    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndDepTime(String flightId, LocalDateTime dateOfFlight, String depTime);

    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndFlightCategoryTypeIn(Sort sort, String flightId, LocalDateTime dateOfFlight, List<FlightmovementCategoryType> movementCategoryTypes);

    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(Sort sort, String flightId, LocalDateTime dateOfFlight, String depTime, String depAd);

    @Query(value="SELECT fm.*  FROM flight_movements fm WHERE fm.flight_id = :flightId AND fm.dep_ad = :depAd  AND FM_DateTimeMatch(fm, :dateOfFlight, :depTime, :eet, :percentage, :minimum) ORDER BY fm.date_of_flight DESC, fm.dep_time DESC", nativeQuery = true)
    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(@Param("flightId")String flightId, @Param("depAd") String depAd, @Param("dateOfFlight") LocalDate dateOfFlight, @Param("depTime") String depTime, @Param("eet") String eet, @Param("percentage") int percentage, @Param("minimum") int minimum);

    @Query(value="SELECT fm.*  FROM flight_movements fm WHERE fm.flight_id = :flightId AND fm.date_of_flight\\:\\:timestamp = :dateOfFlight\\:\\:timestamp ORDER BY fm.date_of_flight DESC", nativeQuery = true)
    List<FlightMovement> findAllByFlightIdAndDateOfFlight(@Param("flightId")String flightId,@Param("dateOfFlight") LocalDateTime dateOfFlight);

    @Query(value="SELECT fm.*  FROM flight_movements fm WHERE fm.flight_id = :flightId AND fm.date_of_flight\\:\\:timestamp = :dateOfFlight\\:\\:timestamp AND fm.dep_time = :depTime ORDER BY fm.date_of_flight DESC", nativeQuery = true)
    List<FlightMovement> findAllByFlightIdAndDateOfFlightAndDepTime(@Param("flightId")String flightId,@Param("dateOfFlight") Date dateOfFlight,@Param("depTime") String depTime);

    /**
     * Find flights that should be included in a general aviation invoice.
     */
    @Query(nativeQuery = true, value =
            "SELECT fm.* " +
            "FROM flight_movements fm " +
            "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE status IN (:statusList) " +
            "AND acc.id = :accountId " +
            "AND (fm.passenger_invoice_id is null or fm.other_invoice_id is null or (fm.enroute_invoice_id is null and acc.iata_member = false)) " +
            "ORDER BY fm.billing_date, fm.flight_id, fm.id " +
            "FOR UPDATE OF fm"
    )
    List <FlightMovement> findForGeneralAviationInvoiceByAccount (
            final @Param("accountId") Integer accountId,
            final @Param("statusList") Collection <String> statusList);

    /**
     * Find flights that should be included in a general aviation invoice.
     */
    @Query(nativeQuery = true, value =
            "SELECT fm.* " +
           "FROM flight_movements fm " +
           "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE status IN (:statusList)  " +
            "AND fm.id in (:flightIdList) " +
            "AND acc.id = :accountId " +
            "AND (fm.passenger_invoice_id is null or fm.other_invoice_id is null or (fm.enroute_invoice_id is null and acc.iata_member = false)) " +
            "ORDER BY fm.billing_date, fm.flight_id, fm.id " +
            "FOR UPDATE OF fm"
    )
    List <FlightMovement> findForGeneralAviationInvoiceByAccount (
            final @Param("accountId") Integer accountId,
            final @Param("statusList") Collection <String> statusList,
            final @Param("flightIdList") Collection <Integer> flightIdList);

    /**
     * Find ALL flights that should be included in a general aviation invoice
     * Flights should belong to the given account
     * Flights can be PENDING
     */
    @Query(value="SELECT fm FROM FlightMovement fm LEFT JOIN fm.account a "
            + "WHERE a.id = :accountId "
            + "AND (fm.status = 'PENDING'  OR fm.status = 'INCOMPLETE')"
            + "AND ((fm.enrouteInvoiceId is null AND (:iataSupport = false OR a.iataMember = false)) "
            + "    OR (fm.passengerInvoiceId is null) "
            + "    OR (fm.otherInvoiceId is null))")
    Page<FlightMovement> findAllForGeneralAviationInvoiceByAccount(
        final @Param("accountId") Integer accountId,
        final @Param("iataSupport") Boolean iataSupport,
        final Pageable pageable);

    /**
     * Find ALL flights that should be included in a general aviation invoice
     * Flights should belong to the given account and filtered by flightMovementCategory
     * Flights can be PENDING
     */
    @Query(value="SELECT fm FROM FlightMovement fm LEFT JOIN fm.account a "
            + "WHERE a.id = :accountId "
            + "AND (fm.flightmovementCategory.id = :flightMovementCategoryId) "
            + "AND (fm.status = 'PENDING'  OR fm.status = 'INCOMPLETE') "
            + "AND ((fm.enrouteInvoiceId is null AND (:iataSupport = false OR a.iataMember = false)) "
            + "    OR (fm.passengerInvoiceId is null) "
            + "    OR (fm.otherInvoiceId is null))")
    Page<FlightMovement> findAllForGeneralAviationInvoiceByAccountAndFlightMovementCategory(
        final @Param("accountId") Integer accountId,
        final @Param("flightMovementCategoryId") Integer flightMovementCategoryId,
        final @Param("iataSupport") Boolean iataSupport,
        final Pageable pageable);


    /**
     * Find ALL flights that should be included in a general aviation invoice
     * Flights should belong to the given account and billing centre
     * Flights can be PENDING
     */
    @Query(value="SELECT fm FROM FlightMovement fm LEFT JOIN fm.account a "
            + "WHERE a.id = :accountId "
            + "AND fm.billingCenter.id = :billingCenterId "
            + "AND (fm.status = 'PENDING'  OR fm.status = 'INCOMPLETE')"
            + "AND ((fm.enrouteInvoiceId is null AND (:iataSupport = false OR a.iataMember = false)) "
            + "    OR (fm.passengerInvoiceId is null) "
            + "    OR (fm.otherInvoiceId is null))")
    Page<FlightMovement> findAllForGeneralAviationInvoiceByAccountAndBC(
        final @Param("accountId") Integer accountId,
        final @Param ("billingCenterId") Integer billingCenterId,
        final @Param("iataSupport") Boolean iataSupport,
        final Pageable pageable);

    /**
     * Find ALL flights that should be included in a general aviation invoice
     * Flights should belong to the given account and billing centre
     * Flights can be PENDING
     */
    @Query(value="SELECT fm FROM FlightMovement fm LEFT JOIN fm.account a "
            + "WHERE a.id = :accountId "
            + "AND (fm.flightmovementCategory.id = :flightMovementCategoryId) "
            + "AND fm.billingCenter.id = :billingCenterId "
            + "AND (fm.status = 'PENDING'  OR fm.status = 'INCOMPLETE')"
            + "AND ((fm.enrouteInvoiceId is null AND (:iataSupport = false OR a.iataMember = false)) "
            + "    OR (fm.passengerInvoiceId is null) "
            + "    OR (fm.otherInvoiceId is null))")
    Page<FlightMovement> findAllForGeneralAviationInvoiceByAccountAndBCAndFlightMovementCategory(
        final @Param("accountId") Integer accountId,
        final @Param ("billingCenterId") Integer billingCenterId,
        final @Param("flightMovementCategoryId") Integer flightMovementCategoryId,
        final @Param("iataSupport") Boolean iataSupport,
        final Pageable pageable);
    
    @Query("SELECT fm FROM FlightMovement fm " +
            "WHERE fm.item18RegNum = :arrivalItem18RegNum " +
            "AND fm.arrivalAd = :arrivalDestAd " +
            "AND fm.dateOfFlight <= :arrivalDateOfFlight " +
            "AND fm.movementType IN ('REG_ARRIVAL', 'INT_ARRIVAL', 'DOMESTIC') " +
            "AND fm.status NOT IN ('CANCELED', 'DELETED', 'REJECTED', 'DECLINED') " +
            "ORDER BY fm.dateOfFlight DESC, fm.depTime DESC")
	List<FlightMovement> findPriorArrivals(
			@Param("arrivalItem18RegNum") String arrivalItem18RegNum,
			@Param("arrivalDestAd") String arrivalDestAd,
			@Param("arrivalDateOfFlight") LocalDateTime arrivalDateOfFlight);

    @Query(value = "SELECT fm.* FROM flight_movements fm " +
        "WHERE fm.item18_reg_num = :arrivalItem18RegNum " +
        "AND (fm.arrival_ad = :arrivalIdentity OR fm.item18_dest LIKE '%' || :arrivalIdentity || '%') " +
        "AND ((fm.date_of_flight = :arrivalDateOfFlight AND fm.dep_time < :arrivalDepTime) OR fm.date_of_flight < :arrivalDateOfFlight) " +
        "AND fm.status NOT IN ('CANCELED', 'DELETED', 'REJECTED', 'DECLINED') " +
        "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC " +
        "LIMIT 1", nativeQuery = true)
	FlightMovement findPriorArrivalByRegNum(
        final @Param("arrivalItem18RegNum") String arrivalItem18RegNum,
        final @Param("arrivalIdentity") String arrivalIdentity,
        final @Param("arrivalDateOfFlight") LocalDateTime arrivalDateOfFlight,
        final @Param("arrivalDepTime") String arrivalDepTime);
    
    @Query(value = "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.item18_reg_num = :arrivalItem18RegNum " +
            "AND (fm.arrival_ad = :arrivalIdentity OR fm.item18_dest LIKE '%' || :arrivalIdentity || '%') " +
            "AND (fm.date_of_flight = :arrivalDateOfFlight AND fm.dep_time < :arrivalDepTime)  " +
            "AND fm.status NOT IN ('CANCELED', 'DELETED', 'REJECTED', 'DECLINED') " +
            "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC "
            , nativeQuery = true)
        List<FlightMovement> findPriorArrivalsByRegNum(
            final @Param("arrivalItem18RegNum") String arrivalItem18RegNum,
            final @Param("arrivalIdentity") String arrivalIdentity,
            final @Param("arrivalDateOfFlight") LocalDateTime arrivalDateOfFlight,
            final @Param("arrivalDepTime") String arrivalDepTime);
    
	@Query(value = "SELECT fm.id FROM flight_movements fm WHERE fm.item18_rmk like '%TRAINING%' AND fm.item18_reg_num = :regNumber AND "
        + "fm.date_of_flight\\:\\:date = :currentDate\\:\\:date AND fm.status in ('INCOMPLETE', 'PENDING', 'PAID', 'INVOICED') AND fm.id <> :exceptionId AND fm.enroute_charges > 0.0",
        nativeQuery = true)
	List<Integer> findAllPendingTrainingFlightsOfCurrentDay (@Param("regNumber") String regNumber, @Param("currentDate") LocalDateTime currentDate, @Param("exceptionId") Integer exceptionId);

	@Query(value = "SELECT fm.* FROM flight_movements fm WHERE fm.item18_reg_num = :departureItem18RegNum AND (fm.dep_ad = :departureAd OR (fm.dep_ad = 'ZZZZ' AND fm.item18_dep IS NOT NULL)) AND fm.date_of_flight\\:\\:date + fm.dep_time\\:\\:time >= :departureDateOfFlight\\:\\:date + :departureTime\\:\\:time AND fm.movement_type IN ('REG_DEPARTURE','INT_DEPARTURE','DOMESTIC') ORDER BY fm.date_of_flight ASC,fm.dep_time ASC", nativeQuery = true)
    List<FlightMovement> findSubsequentDepartures(
            @Param("departureDateOfFlight") Date departureDateOfFlight,
            @Param("departureItem18RegNum") String departureItem18RegNum,
            @Param("departureAd") String departureAd,
            @Param("departureTime") String departureTime);

    /**
     * The purpose of this query is to
     * verify that no other departure records between this arrival record and the departure record of providedFlightMovement
     * For performance optimization LIMIT is set to 1
     */
	@Query(value = "SELECT fm.* FROM flight_movements fm " +
        "WHERE fm.date_of_flight\\:\\:date + fm.dep_time\\:\\:time < :currentDepartureDateOfFlight\\:\\:date + :currentDepartureDepTime\\:\\:time " +
        "AND fm.date_of_flight\\:\\:date + fm.dep_time\\:\\:time > :priorArrivalDateOfFlight\\:\\:date + :priorArrivalArrivalTime\\:\\:time " +
        "AND fm.movement_type IN ('REG_DEPARTURE','INT_DEPARTURE','DOMESTIC') " +
        "AND fm.status NOT IN ('CANCELED', 'DELETED', 'REJECTED', 'DECLINED') " +
        "AND (fm.dep_ad = :currentDepartureDepAd " +
        "OR (fm.dep_ad = 'ZZZZ' AND fm.item18_dep IS NOT NULL)) " +
        "AND fm.item18_reg_num = :currentDepartureItem18RegNum " +
        "ORDER BY fm.date_of_flight DESC,fm.dep_time DESC", nativeQuery = true)
	List<FlightMovement> findAnyDepartureRecordsBetweenPriorArrivalAndCurrentDeparture(
			@Param("currentDepartureItem18RegNum") String currentDepartureItem18RegNum,
			@Param("currentDepartureDepAd") String currentDepartureDepAd,
			@Param("currentDepartureDateOfFlight") Date currentDepartureDateOfFlight,
			@Param("currentDepartureDepTime") String currentDepartureDepTime,
			@Param("priorArrivalDateOfFlight") Date priorArrivalDateOfFlight,
			@Param("priorArrivalArrivalTime") String priorArrivalArrivalTime);

	@Query (nativeQuery = true, value =
	        "SELECT fm.* " +
	          "FROM flight_movements fm " +
	         "WHERE fm.enroute_invoice_id = :billingLedgerId or fm.other_invoice_id = :billingLedgerId or fm.passenger_invoice_id = :billingLedgerId")
	List <FlightMovement> findByBillingLedger (final @Param ("billingLedgerId") Integer billingLedgerId);

    @Query (nativeQuery = true, value =
        "SELECT fm.* " +
            "FROM flight_movements fm " +
            "WHERE EXTRACT(MONTH FROM fm.date_of_flight)  = :month and EXTRACT(year FROM fm.date_of_flight)  = :year " +
            " and fm.status in ('PENDING','INCOMPLETE') ")
    List <FlightMovement> findAllByMonthYear (final @Param ("month") Integer month, final @Param ("year") Integer year);

	/**
     * Find flights for aviation billing by start date, end date, billing center and iata status
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* " +
            "FROM flight_movements fm " +
            "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE (acc.iata_member = :iataStatus) " +
            "AND fm.billing_date >= :dateStart " +
            "AND fm.billing_date < :dateEnd " +
            "AND fm.status in ('PENDING','INCOMPLETE') " +
            "AND fm.billing_center_id = :billingCenterId " +
            "ORDER BY fm.date_of_flight, fm.dep_time, fm.flight_id, fm.id "
    )
    List <FlightMovement> findForAviationBillingByMonthYearIatastatus (final @Param ("dateStart") Date dateStart,
                                                                       final @Param ("dateEnd") Date dateEnd,
                                                                       final @Param ("iataStatus") Boolean iataStatus,
                                                                       final @Param ("billingCenterId") Integer billingCenterId);

    /**
     * Find flights for aviation billing by start date, end date and iata status
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* " +
            "FROM flight_movements fm " +
            "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE (acc.iata_member = :iataStatus) " +
            "AND fm.billing_date >= :dateStart " +
            "AND fm.billing_date < :dateEnd " +
            "AND fm.status in ('PENDING','INCOMPLETE') " +
            "ORDER BY fm.date_of_flight, fm.dep_time, fm.flight_id, fm.id "
    )
    List <FlightMovement> findForAviationBillingByMonthYearIatastatus (final @Param ("dateStart") Date dateStart,
                                                                       final @Param ("dateEnd") Date dateEnd,
                                                                       final @Param ("iataStatus") Boolean iataStatus);


    /**
     * Find flights for aviation billing by start date, end date, iata status, user billing center and the list of account ids
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* " +
            "FROM flight_movements fm " +
            "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE (acc.iata_member = :iataStatus) " +
            "AND fm.billing_date >= :dateStart " +
            "AND fm.billing_date < :dateEnd " +
            "AND acc.id in :accountList " +
            "AND fm.status in ('PENDING','INCOMPLETE') " +
            "AND fm.billing_center_id = :billingCenterId " +
            "ORDER BY fm.date_of_flight, fm.dep_time, fm.flight_id, fm.id "
    )
    List <FlightMovement> findForAviationBillingByMonthYearIatastatusAccountlist (final @Param("dateStart") Date dateStart,
                                                                                  final @Param("dateEnd") Date dateEnd,
                                                                                  final @Param ("iataStatus") Boolean iataStatus,
                                                                                  final @Param ("accountList") Collection<Integer> accountList,
                                                                                  final @Param ("billingCenterId") Integer billingCenterId);

    /**
     * Find flights for aviation billing by start date, end date, iata status and the list of account ids
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* " +
            "FROM flight_movements fm " +
            "LEFT JOIN accounts acc on acc.id = fm.account " +
            "WHERE (acc.iata_member = :iataStatus) " +
            "AND fm.billing_date >= :dateStart " +
            "AND fm.billing_date < :dateEnd " +
            "AND acc.id in :accountList " +
            "AND fm.status in ('PENDING','INCOMPLETE') " +
            "ORDER BY fm.date_of_flight, fm.dep_time, fm.flight_id, fm.id "
    )
    List <FlightMovement> findForAviationBillingByMonthYearIatastatusAccountlist (final @Param("dateStart") Date dateStart,
                                                                                  final @Param("dateEnd") Date dateEnd,
                                                                                  final @Param ("iataStatus") Boolean iataStatus,
                                                                                  final @Param ("accountList") Collection<Integer> accountList);

    /**
     * Find distinct departure and destination aerodromes
     */
    @Query(nativeQuery = true, value =
        "SELECT DISTINCT fm.dep_ad, fm.dest_ad " +
            "FROM flight_movements fm " +
            "ORDER BY fm.dep_ad "
    )
    List<Object> findDistinctRoutes ();

    /**
     * Find distinct flight levels
     */
    @Query(nativeQuery = true, value =
        "SELECT distinct fm.flight_level " +
        "FROM flight_movements fm " +
        "WHERE fm.flight_level is not null and fm.flight_level !~ '^[ \t\r\n]*$' " +
        "ORDER BY 1 ASC"
    )
    List<Object> findDistinctFlightLevels ();

    /**
     * Find distinct registration numbers (item18_reg_num)
     */
    @Query(value =
        "SELECT distinct fm.item18RegNum FROM FlightMovement fm ORDER BY 1"
    )
    List<Object> findDistinctRegNum ();

    @Query(
        "SELECT fm " +
            "FROM FlightMovement fm " +
            "WHERE fm.approachCharges > :value " +
            "OR fm.aerodromeCharges > :value " +
            "OR fm.lateArrivalCharges > :value " +
            "OR fm.lateDepartureCharges > :value"
    )
    List<FlightMovement> findByAirNavigationChargesGreaterThan(@Param("value") Double value);

    List<FlightMovement> findByInternationalPassengerChargesGreaterThan(Double value);

    List<FlightMovement> findByDomesticPassengerChargesGreaterThan(Double value);

    /**
     * Find possible KCAA Somalia flight pairs
     * @param flightId;
     * @param dateOfFlight;
     * @param depTime;
     * @return List<FlightMovement>
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.flight_id = :flightId " +
                "AND fm.date_of_flight = :dateOfFlight " +
                "AND fm.dep_time >=  :depTime " +
            "ORDER BY fm.dep_time"
    )
    List<FlightMovement> findForKCAASomaliFlightPairsAfterFlight (
        final @Param("flightId") String flightId,
        final @Param("dateOfFlight") LocalDateTime dateOfFlight,
        final @Param("depTime") String depTime
    );
    
    /**
     * Find possible KCAA Somalia flight pairs
     * @param flightId;
     * @param dateOfFlight;
     * @param depTime;
     * @return List<FlightMovement>
     */
    @Query(nativeQuery = true, value =
        "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.flight_id = :flightId " +
                "AND fm.date_of_flight = :dateOfFlight " +
                "AND fm.dep_time <= :depTime " +
            "ORDER BY fm.dep_time desc"
    )
    List<FlightMovement> findForKCAASomaliFlightPairsBeforeFlight (
        final @Param("flightId") String flightId,
        final @Param("dateOfFlight") LocalDateTime dateOfFlight,
        final @Param("depTime") String depTime
    );

    
    /**
     * This method returns previous flight landing at the same aerodrome on the same day.
     *
     * @return list of FlightMovement entities
     */
    @Query(value = "SELECT fm.* FROM flight_movements fm WHERE " +
        "NOT (fm.flight_id = :flightId AND fm.date_of_flight = :dateOfFlight AND fm.dep_time = :depTime AND fm.dep_ad = :depAd) " +
        "AND fm.item18_reg_num = :regNum AND fm.account = :accountId AND fm.flight_type = :flightType AND fm.status NOT IN ('CANCELED', 'DELETED', 'REJECTED', 'DECLINED') " +
        "AND (" +
            "fm.arrival_charge_discounts LIKE :arrivalAd || '" + FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR + "%' " +
            "OR fm.arrival_charge_discounts LIKE '%" + FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_ENTRY_SEPERATOR + "' || :arrivalAd || '" +
                FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR + "%') " +
        "AND (" +
            "(fm.date_of_flight = :dateOfArrival AND fm.dep_time < :depTime AND (fm.arrival_time = '') IS NOT TRUE) " +
            "OR (fm.date_of_flight = :dateOfArrival AND fm.dep_time < :depTime AND fm.arrival_time <> '' AND fm.dep_time <= fm.arrival_time) " +
            "OR (fm.date_of_flight = :dateOfArrivalMinusDay AND fm.arrival_time <> '' AND fm.dep_time > fm.arrival_time)) " +
        "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC " +
        "LIMIT 1",
        nativeQuery = true)
    @SuppressWarnings("squid:S00107")
    FlightMovement findPriorArrivalCharge(
        final @Param("flightId") String flightId,
        final @Param("dateOfFlight") LocalDateTime dateOfFlight,
        final @Param("depTime") String depTime,
        final @Param("depAd") String depAd,
        final @Param("regNum") String regNum,
        final @Param("accountId") Integer accountId,
        final @Param("flightType") String flightType,
        final @Param("arrivalAd") String arrivalAd,
        final @Param("dateOfArrival") LocalDateTime dateOfArrival,
        final @Param("dateOfArrivalMinusDay") LocalDateTime dateOfArrivalMinusDay);

    /**
     * Find flights by time period for revenue projections
     * @param startInterval;
     * @param endInterval;
     * @return List<FlightMovement>
     */
    @Query(
        value =
            "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.movement_type <> 'OTHER' AND fm.movement_type <> 'OVERFLIGHT' " +
            "AND fm.date_of_flight >= :startInterval\\:\\:timestamp AND fm.date_of_flight <= :endInterval\\:\\:timestamp ",
        nativeQuery = true
    )
    List<FlightMovement> findAllByFlightTypeIntervalDate (
        final @Param("startInterval") Date startInterval,
        final @Param("endInterval") Date endInterval
    );

    @Query(value=
    		"SELECT fm.*  FROM flight_movements fm " +
    		    "WHERE fm.flight_id = :flightId " +
    			"AND fm.date_of_flight = :dateOfFlight " +
    		    "AND source = :source " +
    			"ORDER BY fm.date_of_flight DESC", nativeQuery = true)
    List<FlightMovement> findThruFlightsByTimeInterval(
    		@Param("flightId") String flightId,
    		@Param("dateOfFlight") LocalDateTime dateOfFlight,
    		@Param("source") String source
            );


    @Query(value=
            "SELECT fm " +
            "  FROM FlightMovement fm, BillingLedger bl " +
            " WHERE bl.id IN (fm.enrouteInvoiceId, fm.passengerInvoiceId, fm.otherInvoiceId) " +
            "   AND bl.invoiceNumber = :invoiceNum " +
            "   AND fm.spatiaFplObjectId IS NULL " +
            "   AND fm.status IN ('PAID', 'INVOICED')"
    )
    Page<FlightMovement> findForCronosByInvoiceNum (final Pageable pageable, final @Param("invoiceNum") String invoiceNum);

    @Query(nativeQuery = true, value =
            "select trim (from estimated_elapsed_time) " +
            "  from flight_movements " +
            " where dep_ad = cast (:depAd as text) " +
            "   and dest_ad = cast (:destAd as text) " +
            "   and date_of_flight >= cast (:fromDate as timestamp with time zone) " +
            "   and date_of_flight < cast (:toDate as timestamp with time zone) " +
            "   and estimated_elapsed_time is not null and char_length (trim (from estimated_elapsed_time)) > 0 " +
            " order by  " +
            "     case " +
            "       when flight_id = cast (:flightId as text) and item18_reg_num = cast (:regNum as text) and aircraft_type = cast (:aircraftType as text) then " +
            "         4 " +
            "       when flight_id = cast (:flightId as text) and item18_reg_num = cast (:regNum as text) then " +
            "         3 " +
            "       when aircraft_type = cast (:aircraftType as text) then " +
            "         2 " +
            "       else " +
            "         1 " +
            "     end " +
            "   desc, " +
            "   date_of_flight desc, " +
            "   id desc " +
            " limit 1 "
    )
    String findHistoricalEstimatedElapsedTime(
        final @Param("depAd") String depAd,
        final @Param("destAd") String destAd,
        final @Param("flightId") String flightId,
        final @Param("regNum") String regNum,
        final @Param("aircraftType") String aircraftType,
        final @Param("fromDate") LocalDate fromDateInclusive,
        final @Param("toDate") LocalDate toDateExclusive);

    @SuppressWarnings("unused")
    default LocalDateTime previousDate(LocalDateTime dateOfFlight) {
        return dateOfFlight.minusDays(1);
    }

    /**
     * Cumulative flights are for the same date of flight
     * Not INVOICED
     */
    @Query(value = "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.account=:accountId " +
    		"AND fm.wake_turb =:wakeTurb " +
            "AND fm.date_of_flight =:dof\\:\\:timestamp " +
            "AND fm.status IN ('PENDING', 'INCOMPLETE', 'ACTIVE') " +
            "AND fm.flight_category_scope = 'IN'" +
            "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC", nativeQuery = true)
    List<FlightMovement> findAllCumulativeByAccountAndWakeTurbTypeDOF(
    		@Param("accountId") Integer accountId,
    		@Param("wakeTurb") String wakeTurb,
    		@Param("dof") LocalDateTime dof);

    /**
     * Cumulative flights are for the same billing date
     * Not INVOICED
     */
    @Query(value = "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.account=:accountId " +
    		"AND fm.wake_turb =:wakeTurb " +
            "AND fm.billing_date =:billingDate\\:\\:timestamp " +
            "AND fm.status IN ('PENDING', 'INCOMPLETE', 'ACTIVE') " +
            "AND fm.flight_category_scope = 'IN'" +
            "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC", nativeQuery = true)
    List<FlightMovement> findAllCumulativeByAccountAndWakeTurbTypeBD(
    		@Param("accountId") Integer accountId,
    		@Param("wakeTurb") String wakeTurb,
    		@Param("billingDate") LocalDateTime billingDate);

    @Modifying
    @Query(nativeQuery = true,value =
            "UPDATE flight_movements set status='PAID' where status='PENDING' and total_charges=0.0 and id in :flightIds"
    )
    int setZeroCostFlightsPaid(	@Param("flightIds") Collection<Integer> flightIds);

    @Modifying
    @Query(nativeQuery = true, value =
            "UPDATE flight_movements  set status = 'PAID' where status='PENDING' and total_charges=0.0 and date_of_flight\\:\\:timestamp < NOW() - INTERVAL '30 days'"
    )
    int setOldZeroCostFlightsPaid();

    @Query(nativeQuery = true, value = "SELECT fm.cruising_speed FROM flight_movements fm " +
        "WHERE fm.item18_reg_num = :item18RegNum AND fm.cruising_speed IS NOT NULL AND fm.cruising_speed <> ''" +
        "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC, fm.flight_id, fm.id " +
        "LIMIT 1")
    String findLatestCruisingSpeedByRegistrationNumber(@Param("item18RegNum") final String item18RegNum);

    @Query(nativeQuery = true, value = "SELECT fm.cruising_speed FROM flight_movements fm " +
        "WHERE fm.aircraft_type = :aircraftType AND fm.cruising_speed IS NOT NULL AND fm.cruising_speed <> ''" +
        "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC, fm.flight_id, fm.id " +
        "LIMIT 1")
    String findLatestCruisingSpeedByAircraftType(@Param("aircraftType") final String aircraftType);

    @Query(nativeQuery = true, value = "SELECT fm.* FROM flight_movements fm " +
            "WHERE fm.item18_reg_num=:item18RegNum\\:\\:text " +
            "AND fm.date_of_flight <=:dateOfFlight\\:\\:timestamp " +
    		" AND fm.aircraft_type IS NOT NULL AND fm.aircraft_type <> 'ZZZZ' " +
            "ORDER BY fm.date_of_flight DESC, fm.dep_time DESC " +
            "LIMIT 1")
    FlightMovement findLatestByItem18RegAircraftTypeKnown(@Param("item18RegNum") final String item18RegNum, @Param("dateOfFlight") final LocalDateTime dateOfFlight);

    @Query("SELECT fm FROM FlightMovement fm WHERE fm.dateOfFlight BETWEEN :startDate AND :endDate AND " +
        "fm.item18RegNum = :item18RegNum AND (fm.depAd = :aerodrome OR fm.destAd = :aerodrome) AND " +
        "fm.status != 'CANCELED' and fm.status != 'DELETED' and fm.status != 'DECLINED' ORDER BY fm.dateOfFlight, fm.depTime")
    List<FlightMovement> findAllByDateOfFlightAndRegNumAndAerodrome(@Param("startDate") final LocalDateTime startDate,
                                                                    @Param("endDate") final LocalDateTime endDate,
                                                                    @Param("item18RegNum") final String item18RegNum,
                                                                    @Param("aerodrome") final String aerodrome);

    @Query("SELECT fm.status FROM FlightMovement fm WHERE fm.id = :id")
    FlightMovementStatus findFlightMovementStatus(@Param("id") final Integer id);
    
    @Query(nativeQuery = true, value ="SELECT fm.* FROM flight_movements fm WHERE fm.id IN (:ids) FOR UPDATE")
    List<FlightMovement> findAllByIdInUpdateSkipLocked(@Param("ids") Collection<Integer> ids);

    @Query("SELECT fm FROM FlightMovement fm WHERE fm.account.id = :accountId " +
        "AND fm.dateOfFlight >= :whitelistingStartDate " +
        "AND fm.status IN ('PENDING', 'INCOMPLETE') " +
        "AND fm.id != :newFlightMovementId")
    List<FlightMovement> checkAllFlightMovementsByAccountForWhitelisting(@Param("newFlightMovementId") final int newFlightMovementId,
                                                                         @Param("accountId") final int accountId,
                                                                         @Param("whitelistingStartDate") final LocalDateTime whitelistingStartDate);

    @Query("SELECT fm FROM FlightMovement fm WHERE fm.account.id = :accountId " +
        "AND fm.dateOfFlight BETWEEN :whitelistingStartDate AND :today " +
        "AND fm.status = 'PENDING'" +
        "ORDER BY fm.dateOfFlight")
    List<FlightMovement> getAllFlightMovementsByAccountForWhitelistingRetroactivePayments(@Param("accountId") final int accountId,
                                                                                          @Param("whitelistingStartDate") final LocalDateTime whitelistingStartDate,
                                                                                          @Param("today") final LocalDateTime today);

    @Query(value = "SELECT DISTINCT fm.account FROM FlightMovement fm " +
        "JOIN fm.account a " +
        "WHERE a.cashAccount is TRUE " +
        "AND fm.dateOfFlight BETWEEN :flightMovementsFromDate AND :flightMovementsToDate " +
        "AND fm.status = 'PENDING' " +
        "ORDER BY fm.account.name")
    List<Account> findCashAccountsThatHaveWhitelistingFlights(@Param("flightMovementsFromDate") LocalDateTime flightMovementsFromDate,
                                                              @Param("flightMovementsToDate") LocalDateTime flightMovementsToDate);

    @Query (value = "SELECT COUNT(fm) FROM FlightMovement fm JOIN fm.account ac JOIN ac.accountUsers au")
    long countAllForSelfCareAccounts();

    @Query (value = "SELECT COUNT(fm) FROM FlightMovement fm JOIN fm.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllForSelfCareUser(@Param ("userId") final int userId);       
}
