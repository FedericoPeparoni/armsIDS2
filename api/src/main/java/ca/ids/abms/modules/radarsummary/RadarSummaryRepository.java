package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by c.talpa on 05/01/2017.
 */
public interface RadarSummaryRepository extends ABMSRepository<RadarSummary, Integer> {


    @Query (nativeQuery = true, value =
                "SELECT * " +
                "  FROM radar_summaries " +
                " WHERE flight_identifier = :flightId " +
                "   AND day_of_flight = :dayOfFlight " +
                "   AND dep_time = :depTime " +
                "   AND dep_ad = :depAd " +
                " ORDER BY day_of_flight DESC, dep_time DESC, id DESC " +
                " LIMIT 1")
    RadarSummary findByLogicalKeyExact (
            @Param("flightId") String flightId,
            @Param("depAd") String depAd,
            @Param("dayOfFlight") LocalDateTime dayOfFlight,
            @Param("depTime") String depTime);

    
    @Query (nativeQuery = true, value =
            "SELECT * " +
            "  FROM radar_summaries " +
            " WHERE flight_identifier = :flightId " +
            "   AND day_of_flight = :dayOfFlight " +
            "   AND dep_time = :depTime " +
            "   AND dep_ad = :depAd " +
            " ORDER BY day_of_flight DESC, dep_time DESC, id DESC, segment DESC ")
List<RadarSummary> findByLogicalKeyAll (
        @Param("flightId") String flightId,
        @Param("depAd") String depAd,
        @Param("dayOfFlight") LocalDateTime dayOfFlight,
        @Param("depTime") String depTime);
    
    @Query (nativeQuery = true, value =
            "SELECT * " +
            "  FROM radar_summaries " +
            " WHERE flight_identifier = :flightId " +
            "   AND day_of_flight = :dayOfFlight " +
            "   AND dep_time = :depTime " +
            "   AND dep_ad = :depAd " +
            "   AND segment = :segment " +
            " ORDER BY day_of_flight DESC, dep_time DESC, id DESC, segment DESC Limit 1")
RadarSummary findByLogicalKeySegment (
        @Param("flightId") String flightId,
        @Param("depAd") String depAd,
        @Param("dayOfFlight") LocalDateTime dayOfFlight,
        @Param("depTime") String depTime,
        @Param("segment") Integer segment);
   
  @Query (nativeQuery = true, value =
            "SELECT max(segment) " +
            "  FROM radar_summaries " +
            " WHERE flight_identifier = :flightId " +
            "   AND day_of_flight = :dayOfFlight " +
            "   AND dep_time = :depTime " +
            "   AND dep_ad = :depAd " )
  Integer getMaxSegmentNum(
        @Param("flightId") String flightId,
        @Param("depAd") String depAd,
        @Param("dayOfFlight") LocalDateTime dayOfFlight,
        @Param("depTime") String depTime);

    @Query (nativeQuery = true, value =
            "SELECT * " +
            "  FROM radar_summaries " +
                // flight_id must match
            " WHERE flight_identifier = :flightId " +
                // day_of_flight's date components must be between the date components
                // of the supplied from/to interval. This is not necessary because this
                // line is a superset of the more exact match expression that follows,
                // but this should make use of the unique index that includes day_of_flight
                // and trim down the result set in case there's a very large number of records
            "   AND day_of_flight BETWEEN " +
                    // timestamp truncated to date, then re-extended to timestamp at 00:00:00
                    "CAST (CAST (:fullDepartureDateTimeLower AS DATE) AS TIMESTAMP WITH TIME ZONE) AND " +
                    "CAST (CAST (:fullDepartureDateTimeUpper AS DATE) AS TIMESTAMP WITH TIME ZONE) " +
                // Check if flight's departure date/time falls within the given lower/upper interval. We construct
                // a full departure timestamp from "day_of_flight" and "dep_time" fields on the fly and compare
                // whether such values are within the supplied full date/time begin & end points
            "   AND " +
                    // This expression creates a single timestamp value out of "day_of_flight" and "dep_time"
                    //    <date> + <time with time zone> = <timestamp with time zone>
                    // the stored proc "try_parse_aviation_time_of_day" converts strings 'HHMM' to the actual
                    // time data type without causing an exception in case of parse errors 
                    "CAST (day_of_flight AS DATE) + try_parse_aviation_time_of_day (dep_time) " +
                    // ... departure timestamp has to be between he lower/upper timestamps given
                    "BETWEEN :fullDepartureDateTimeLower AND :fullDepartureDateTimeUpper" +
            "   AND dep_ad = :depAd " +
            " ORDER BY " +
                    // Order by the difference between the supplied departure time stamp and the
                    // record's full departure timestamp constructed on the fly as described above.
                    // We need this to take the first record from the result -- i.e., the one that is 
                    // closest to the date we are looking for.
                    // ABS() makes the difference bettwen dates absolute
                    // EXTRACT (EPOCH...) converts an interval type to seconds
            "        ABS (EXTRACT (EPOCH FROM (" +
                        "(CAST (day_of_flight AS DATE) + try_parse_aviation_time_of_day (dep_time)) - :fullDepartureDateTime" +
                     ")))" +
            " LIMIT 1")
    RadarSummary findByLogicalKeyFuzzy (
        @Param("flightId") String flightId,
        @Param("depAd") String depAd,
        @Param("fullDepartureDateTime") LocalDateTime fullDepartureDateTime, // full departure date/time
        @Param("fullDepartureDateTimeLower") LocalDateTime fullDepartureDateTimeLower,  // departure date/time minus a few minutes
        @Param("fullDepartureDateTimeUpper") LocalDateTime fullDepartureDateTimeUpper); // departure date/time plus a few minutes
    
}
