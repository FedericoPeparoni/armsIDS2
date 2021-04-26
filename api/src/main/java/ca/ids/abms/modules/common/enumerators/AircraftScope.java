package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum AircraftScope {

	AGRICULTURE, FLIGHT_SCHOOL ;

	private static Map<String, AircraftScope> values = new HashMap<>(5);

    static {
        values.put("agriculture", AGRICULTURE);
        values.put("flight-school", FLIGHT_SCHOOL);

    }

    @JsonCreator
    public static AircraftScope forValue(String value) {
    	AircraftScope item;
        if (value != null) {
            item = values.get(value.toLowerCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, AircraftScope> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

}
