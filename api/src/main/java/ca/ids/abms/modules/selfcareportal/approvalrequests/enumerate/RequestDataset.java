package ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RequestDataset {
    ACCOUNT,
    AIRCRAFT_REGISTRATION,
    FLIGHT_SCHEDULE;

    private static Map<String, RequestDataset> values = new HashMap<>(6);
    static {
        values.put("account", ACCOUNT);
        values.put("aircraft registration", AIRCRAFT_REGISTRATION);
        values.put("flight schedule", FLIGHT_SCHEDULE);
    }

    @JsonValue
    public String toValue() {
        String value = null;
        for (Map.Entry<String, RequestDataset> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value = entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static RequestDataset forValue(String value) {
        RequestDataset item = null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }
}
