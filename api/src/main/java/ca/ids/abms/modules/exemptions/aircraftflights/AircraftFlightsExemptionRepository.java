package ca.ids.abms.modules.exemptions.aircraftflights;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AircraftFlightsExemptionRepository extends ABMSRepository<AircraftFlightsExemption, Integer> {

    @Query(value =
    "select * from exempt_aircraft_flights eaf "+
    "where eaf.flight_id=:flightId and eaf.aircraft_registration=:aircraftRegistration "+
    "and (eaf.exemption_start_date<=:dateOfFlight and eaf.exemption_end_date>=:dateOfFlight) order by eaf.created_at desc",nativeQuery = true)
    AircraftFlightsExemption findExemptionByAircraftRegistrationAndFlightId (final @Param("flightId") String flightId,
                                                     final @Param ("aircraftRegistration") String aircraftRegistration,
                                                     final @Param("dateOfFlight") LocalDateTime dateOfFlight);

    @Query(value =
    "select * from exempt_aircraft_flights eaf "+
    "where eaf.aircraft_registration=:aircraftRegistration "+
    "and (eaf.flight_id is null or eaf.flight_id = '') "+
    "and (eaf.exemption_start_date<=:dateOfFlight and eaf.exemption_end_date>=:dateOfFlight) order by eaf.created_at desc limit 1",nativeQuery = true)
    AircraftFlightsExemption findExemptionByAircraftRegistration (final @Param ("aircraftRegistration") String aircraftRegistration,
                                                     final @Param("dateOfFlight") LocalDateTime dateOfFlight);

    @Query(value =
    "select * from exempt_aircraft_flights eaf "+
    "where eaf.flight_id=:flightId "+
    "and (eaf.aircraft_registration is null or eaf.aircraft_registration = '') "+
    "and (eaf.exemption_start_date<=:dateOfFlight and eaf.exemption_end_date>=:dateOfFlight) order by eaf.created_at desc limit 1",nativeQuery = true)
    AircraftFlightsExemption findExemptionByFlightId (final @Param("flightId") String flightId,
                                                     final @Param("dateOfFlight") LocalDateTime dateOfFlight);

    @Query(value =
        "SELECT eaf FROM AircraftFlightsExemption eaf " +
            "WHERE eaf.aircraftRegistration = :aircraftRegistration " +
            "AND (eaf.flightId is NULL or eaf.flightId = '') " +
            "AND eaf.exemptionStartDate = :startDate")
    List<AircraftFlightsExemption> findExemptionsByAircraftRegistrationAndStartDate(final @Param ("aircraftRegistration") String aircraftRegistration,
                                                                                    final @Param("startDate") LocalDateTime startDate);

    @Query(value =
        "SELECT eaf FROM AircraftFlightsExemption eaf " +
            "WHERE eaf.aircraftRegistration = :aircraftRegistration " +
            "AND (eaf.flightId is NULL or eaf.flightId = '') " +
            "AND eaf.exemptionStartDate = :startDate " +
            "AND eaf.id != :id")
    List<AircraftFlightsExemption> findExemptionsByAircraftRegistrationAndStartDate(final @Param ("aircraftRegistration") String aircraftRegistration,
                                                                                    final @Param("startDate") LocalDateTime startDate,
                                                                                    final @Param("id") int id);

    @Query(value =
        "SELECT eaf FROM AircraftFlightsExemption eaf " +
            "WHERE eaf.flightId = :flightId " +
            "AND (eaf.aircraftRegistration is NULL or eaf.aircraftRegistration = '') " +
            "AND eaf.exemptionStartDate = :startDate")
    List<AircraftFlightsExemption> findExemptionsByFlightIdAndStartDate(final @Param ("flightId") String flightId,
                                                                        final @Param("startDate") LocalDateTime startDate);

    @Query(value =
        "SELECT eaf FROM AircraftFlightsExemption eaf " +
            "WHERE eaf.flightId = :flightId " +
            "AND (eaf.aircraftRegistration is NULL or eaf.aircraftRegistration = '') " +
            "AND eaf.exemptionStartDate = :startDate " +
            "AND eaf.id != :id")
    List<AircraftFlightsExemption> findExemptionsByFlightIdAndStartDate(final @Param ("flightId") String flightId,
                                                                        final @Param("startDate") LocalDateTime startDate,
                                                                        final @Param("id") int id);
}
