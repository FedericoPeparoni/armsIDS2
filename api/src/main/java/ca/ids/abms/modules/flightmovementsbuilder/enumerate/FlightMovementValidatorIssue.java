package ca.ids.abms.modules.flightmovementsbuilder.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by c.talpa on 07/12/2016.
 *
 * This enum includes
 * validation errors from (4.5.20 Aviation Billing Engine (CORE))
 * and also the resolution errors (3.6.8	Flight Movements (CORE)).
 * These resolution errors are identical to the validation errors,
 * so there is no need to maintain 2 separate enums.
 * The resolution errors list from requirements contains "no_errors" value.
 * There is no need to add this element,
 * becuase if no flight movement errors were found during validation, then resolution_errors field in DB will be NULL or empty.
 */
public enum FlightMovementValidatorIssue {

    ALL("ALL"),
    MISSING_MTOW("MISSING_MTOW"),
    NO_ASSOCIATED_ACCOUNT("NO_ASSOCIATED_ACCOUNT"),
    UNKNOWN_AIRCRAFT_TYPE("UNKNOWN_AIRCRAFT_TYPE"),
    UNKNOWN_STATUS("UNKNOWN_STATUS"),
    ZERO_LENGTH_BILLABLE_TRACK("ZERO_LENGTH_BILLABLE_TRACK"),
    MISSING_PARKING_TIME("MISSING_PARKING_TIME"),
    ATC_MOVEMENT_LOG_CROSSCHECK_WAS_NOT_COMPLETED("ATC_MOVEMENT_LOG_CROSSCHECK_WAS_NOT_COMPLETED"),
    TOWER_AIRCRAFT_PASSENGER("TOWER_AIRCRAFT_PASSENGER"),
    MISSING_PASSENGER_SERVICE_CHARGE_RETURN("MISSING_PASSENGER_SERVICE_CHARGE_RETURN"),
    PASSENGER_MANIFEST_SUMMARY("PASSENGER_MANIFEST_SUMMARY"),
    RADAR_SUMMARY_CROSSCHECK("RADAR_SUMMARY_CROSSCHECK"),
    RADAR_SUMMARY_MISSING("RADAR_SUMMARY_MISSING"),
    TOWER_LOG_MISSING("TOWER_LOG_MISSING"),
    ATC_LOG_MISSING("ATC_LOG_MISSING"),
    FLIGHT_PLAN_MISSING("FLIGHT_PLAN_MISSING"),
    MISSING_PASSENGER_DOMESTIC_DATA("MISSING_PASSENGER_DOMESTIC_DATA"),
    MISSING_PASSENGER_INTERNATIONAL_DATA("MISSING_PASSENGER_INTERNATIONAL_DATA"),
    UNKNOWN_DEP_AD("UNKNOWN_DEPARTURE_AERODROME"),
    UNKNOWN_DEST_AD("UNKNOWN_DESTINATION_AERODROME"),
    MISSING_USD_EXCHANGE_RATE("MISSING_USD_EXCHANGE_RATE"),
    EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT("EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT"),
    MISSING_ANSP_EXCHANGE_RATE("MISSING_ANSP_EXCHANGE_RATE"),
    MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE("MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE"),
    INVALID_TIME_FORMAT("INVALID_TIME_FORMAT"),
    INVALID_FLIGHT_LEVEL("INVALID_FLIGHT_LEVEL");


    private static Map<String, FlightMovementValidatorIssue> values = new HashMap<>(21);
    static {
        values.put("ALL", ALL);
        values.put("MISSING_MTOW", MISSING_MTOW);
        values.put("NO_ASSOCIATED_ACCOUNT", NO_ASSOCIATED_ACCOUNT);
        values.put("UNKNOWN_AIRCRAFT_TYPE", UNKNOWN_AIRCRAFT_TYPE);
        values.put("UNKNOWN_STATUS", UNKNOWN_STATUS);
        values.put("ZERO_LENGTH_BILLABLE_TRACK", ZERO_LENGTH_BILLABLE_TRACK);
        values.put("MISSING_PARKING_TIME", MISSING_PARKING_TIME);
        values.put("ATC_MOVEMENT_LOG_CROSSCHECK_WAS_NOT_COMPLETED", ATC_MOVEMENT_LOG_CROSSCHECK_WAS_NOT_COMPLETED);
        values.put("TOWER_AIRCRAFT_PASSENGER", TOWER_AIRCRAFT_PASSENGER);
        values.put("MISSING_PASSENGER_SERVICE_CHARGE_RETURN", MISSING_PASSENGER_SERVICE_CHARGE_RETURN);
        values.put("PASSENGER_MANIFEST_SUMMARY", PASSENGER_MANIFEST_SUMMARY);
        values.put("RADAR_SUMMARY_CROSSCHECK", RADAR_SUMMARY_CROSSCHECK);
        values.put("RADAR_SUMMARY_MISSING", RADAR_SUMMARY_MISSING);
        values.put("TOWER_LOG_MISSING", TOWER_LOG_MISSING);
        values.put("ATC_LOG_MISSING", ATC_LOG_MISSING);
        values.put("FLIGHT_PLAN_MISSING", FLIGHT_PLAN_MISSING);
        values.put("MISSING_PASSENGER_DOMESTIC_DATA", MISSING_PASSENGER_DOMESTIC_DATA);
        values.put("MISSING_PASSENGER_INTERNATIONAL_DATA", MISSING_PASSENGER_INTERNATIONAL_DATA);
        values.put("UNKNOWN_DEPARTURE_AERODROME", UNKNOWN_DEP_AD);
        values.put("UNKNOWN_DESTINATION_AERODROME", UNKNOWN_DEST_AD);
        values.put("MISSING_USD_EXCHANGE_RATE", MISSING_USD_EXCHANGE_RATE);
        values.put("MISSING_ANSP_EXCHANGE_RATE", MISSING_ANSP_EXCHANGE_RATE);
        values.put("EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT", EXPIRED_OR_MISSING_COA_FOR_SMALL_AIRCRAFT);
        values.put("MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE", MISSING_ENROUTE_FORMULA_OR_CHARGE_SCHEDULE);
        values.put("INVALID_TIME_FORMAT", INVALID_TIME_FORMAT);
        values.put("INVALID_FLIGHT_LEVEL",INVALID_FLIGHT_LEVEL);
    }


    private String value;

    FlightMovementValidatorIssue(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, FlightMovementValidatorIssue> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightMovementValidatorIssue forValue(final String value) {
        FlightMovementValidatorIssue item = null;
        if (StringUtils.isNotEmpty(value)) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
