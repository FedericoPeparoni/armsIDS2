/**
 * 
 */
package ca.ids.abms.modules.flightmovements.enumerate;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author heskina
 *
 */
public enum FlightmovementCategoryType {
	DOMESTIC,
    ARRIVAL,
    DEPARTURE,
    OVERFLIGHT,
    OTHER;
    
    private static Map<String, FlightmovementCategoryType> values = new HashMap<>(3);
    static {
        values.put("DO", DOMESTIC);
        values.put("AR", ARRIVAL);
        values.put("DE", DEPARTURE);
        values.put("OV", OVERFLIGHT);
        values.put("OT", OTHER);
    }

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, FlightmovementCategoryType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightmovementCategoryType forValue(String value) {
    	FlightmovementCategoryType item=null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
