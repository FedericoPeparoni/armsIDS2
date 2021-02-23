package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlightType {

    NORMAL, DELTA;

    private static Map<String, FlightType> values = new HashMap<>(3);

    static {
        values.put("normal", NORMAL);
        values.put("delta", DELTA);
    }

    @JsonCreator
    public static FlightType forValue(String value) {
        FlightType item;
        if (value != null) {
            item = values.get(value.toLowerCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, FlightType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
