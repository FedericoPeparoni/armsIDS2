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
public enum FlightmovementCategoryNationality {
	NATIONAL,
    FOREIGN;

    private static Map<String, FlightmovementCategoryNationality> values = new HashMap<>(3);
    static {
        values.put("NA", NATIONAL);
        values.put("FO", FOREIGN);
    }

    FlightmovementCategoryNationality() {}

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, FlightmovementCategoryNationality> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightmovementCategoryNationality forValue(String value) {
    	FlightmovementCategoryNationality item=null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
