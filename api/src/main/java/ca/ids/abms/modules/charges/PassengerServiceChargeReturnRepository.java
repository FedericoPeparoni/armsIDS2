package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;


public interface PassengerServiceChargeReturnRepository extends ABMSRepository<PassengerServiceChargeReturn, Integer> {

    @Query(value = OrphanPassengerServiceChargeReturnFilter.QUERY, nativeQuery = true)
    List<Integer> getOrphanPassengerServiceChargeReturns ();

    List<PassengerServiceChargeReturn> findByFlightIdAndDayOfFlight(String flightId, LocalDateTime dayOfFlight);

    @Query(
        value = "SELECT count(*) FROM abms.flight_movements as f " +
                    " WHERE f.date_of_flight >= to_date( :start_date ,'YYYY-MM-dd') and f.date_of_flight <= to_date( :end_date ,'YYYY-MM-dd')" +
                    " AND NOT EXISTS (SELECT NULL" +
                    " FROM abms.passenger_service_charge_returns as p " +
                    " WHERE f.flight_id = p.flight_id " +
                    " AND to_char(f.date_of_flight, 'YYYY-MM-dd') = to_char(p.day_of_flight, 'YYYY-MM-dd'));",
        nativeQuery = true
    )
    Integer getMovementsWithoutPassengerCharges(@Param("start_date") String startDate, @Param("end_date") String endDate);

    @Query(
        value = "SELECT count(*) FROM abms.passenger_service_charge_returns as p " +
                    " WHERE p.day_of_flight >= to_date( :start_date ,'YYYY-MM-dd') and p.day_of_flight <= to_date( :end_date ,'YYYY-MM-dd')" +
                    " AND NOT EXISTS (SELECT NULL" +
                    " FROM abms.flight_movements as f " +
                    " WHERE f.flight_id = p.flight_id " +
                    " AND to_char(f.date_of_flight, 'YYYY-MM-dd') = to_char(p.day_of_flight, 'YYYY-MM-dd'));",
        nativeQuery = true
    )
    Integer getPassengerChargesWithoutMovements(@Param("start_date") String startDate, @Param("end_date") String endDate);

    @Query(
        value = "select" +
            " (sum((c.domestic_passenger_fee_adult*" +
            " (cast((select current_value from abms.system_configurations where item_name='Domestic passenger fee percentage') as double precision)/100))" +
            " *f.passengers_chargeable_domestic))" +
            " from abms.flight_movements as f " +
            " join abms.aerodromes as a on a.aerodrome_name = f.dep_ad" +
            " join abms.aerodrome_categories as c on a.aerodrome_category_id = c.id" +
            " where f.date_of_flight between to_date( :start_date , 'YYYY-MM-dd') and to_date( :end_date ,'YYYY-MM-dd')",
        nativeQuery = true
    )
    Double totalDomesticPassengerFee(@Param("start_date") String startDate, @Param("end_date") String endDate);

    @Query(
        value = "select" +
            " (sum((c.international_passenger_fee_adult*" +
            " (cast((select current_value from abms.system_configurations where item_name='International passenger fee percentage') as double precision)/100))" +
            " *f.passengers_chargeable_intern))" +
            " from abms.flight_movements as f " +
            " join abms.aerodromes as a on a.aerodrome_name = f.dep_ad" +
            " join abms.aerodrome_categories as c on a.aerodrome_category_id = c.id" +
            " where f.date_of_flight between to_date( :start_date , 'YYYY-MM-dd') and to_date( :end_date ,'YYYY-MM-dd')",
        nativeQuery = true
    )
    Double totalInternationalPassengerFee(@Param("start_date") String startDate, @Param("end_date") String endDate);

    @Query (value = "SELECT COUNT(ps) FROM PassengerServiceChargeReturn ps JOIN ps.account ac JOIN ac.accountUsers au WHERE au.id = :userId")
    long countAllPassengerServiceChargeReturnsForSelfCareUser(@Param ("userId") final int userId);

    List<PassengerServiceChargeReturn> findByFlightIdAndDayOfFlightAndDepartureTime(String flightId, LocalDateTime dayOfFlight, String departureTime);
}
