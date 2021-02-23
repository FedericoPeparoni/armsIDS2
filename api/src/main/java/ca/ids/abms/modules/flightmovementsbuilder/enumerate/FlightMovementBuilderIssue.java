package ca.ids.abms.modules.flightmovementsbuilder.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum FlightMovementBuilderIssue {

    FLIGHT_MOVEMENT_NOT_EDITABLE,
    FLIGHT_MOVEMENT_CANCELLED,
    FLIGHT_MOVEMENT_DELETED,
    FLIGHT_MOVEMENT_DECLINED,
    FLIGHT_MOVEMENT_INVOICED,
    ROUTE_PARSER_ERROR,
    REJECTED_ITEM,
    ORPHAN_ITEM,
    UNKNOWN_AERODROME,
    UNKNOWN_AERODROME_ROUTE,
    UNKNOWN_AERODROME_ITEM18,
    UNKNOWN_SPEED_EET,
    MISSING_INFORMATION_FOR_CREATE_UNSPECIFIED_LOCATION;

    private static Map<String, FlightMovementBuilderIssue> values = new HashMap<>(10);
    static {
        values.put("Flight movement not editable", FLIGHT_MOVEMENT_NOT_EDITABLE);
        values.put("Flight has been cancelled", FLIGHT_MOVEMENT_CANCELLED);
        values.put("Flight has been deleted", FLIGHT_MOVEMENT_DELETED);
        values.put("Flight has been declined", FLIGHT_MOVEMENT_DECLINED);
        values.put("Flight has already been invoiced", FLIGHT_MOVEMENT_INVOICED);
        values.put("Route parser error", ROUTE_PARSER_ERROR);
        values.put("Rejected Item", REJECTED_ITEM);
        values.put("Orphan Item. Couldn't match with a Flight Movement", ORPHAN_ITEM);
        values.put("Unknown Aerodrome", UNKNOWN_AERODROME);
        values.put("Unknown Aerodrome and item18", UNKNOWN_AERODROME_ITEM18);
        values.put("Unknown Speed or EET", UNKNOWN_SPEED_EET);
        values.put("Unknown Aerodrome and/or  route", UNKNOWN_AERODROME_ROUTE);
        values.put("Missing information for creation unspecified location", MISSING_INFORMATION_FOR_CREATE_UNSPECIFIED_LOCATION);
    }

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, FlightMovementBuilderIssue> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightMovementBuilderIssue forValue(String value) {
        FlightMovementBuilderIssue item=null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
