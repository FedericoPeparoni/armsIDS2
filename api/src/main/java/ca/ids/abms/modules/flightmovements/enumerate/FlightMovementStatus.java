package ca.ids.abms.modules.flightmovements.enumerate;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlightMovementStatus {

    INCOMPLETE,
    ACTIVE,
    REJECTED,
    PENDING,
    INVOICED,
    INVPAID,
    PAID,
    CANCELED,
	DELETED,
    DECLINED,
    OTHER;

    private static Map<String, FlightMovementStatus> values = new HashMap<>(10);
    static {
        values.put("ACTIVE", ACTIVE);
        values.put("REJECTED", REJECTED);
        values.put("INCOMPLETE", INCOMPLETE);
        values.put("PENDING", PENDING);
        values.put("INVPAID", INVPAID);
        values.put("INVOICED", INVOICED);
        values.put("PAID", PAID);
        values.put("CANCELED", CANCELED);
        values.put("DELETED", DELETED);
        values.put("DECLINED", DECLINED);
        values.put("OTHER", OTHER);
    }

    FlightMovementStatus(){}

    @JsonValue
    public String toValue() {
        String value = null;
        for (Map.Entry<String, FlightMovementStatus> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value = entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightMovementStatus forValue(String value) {
        FlightMovementStatus item = null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }

}
