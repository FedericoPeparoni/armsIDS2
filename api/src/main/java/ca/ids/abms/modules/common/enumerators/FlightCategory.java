package ca.ids.abms.modules.common.enumerators;

import ca.ids.abms.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.HashMap;
import java.util.Map;

public enum FlightCategory {

    SCH, NON_SCH;

    private static Map<String, FlightCategory> values = new HashMap<>(2);

    static {
        values.put("sch", SCH);
        values.put("nonsch", NON_SCH);
    }

    public static FlightCategory mapFromValue(String value){
        FlightCategory flightCategory = null;
        value = StringUtils.checkAndTrimString(value);
        if(value != null) {
            String valueLowerCase = StringUtils.checkAndTrimString(value).toUpperCase();

            switch (valueLowerCase) {
                case "SCH":
                    flightCategory = FlightCategory.SCH;
                    break;
                case "NSCH":
                case "NONSCH":
                    flightCategory = FlightCategory.NON_SCH;
                    break;
                default:
                    // leave null when no known match
                    break;
            }
        }
        return flightCategory;
    }

    @JsonCreator
    public static FlightCategory forValue(String value) {
        FlightCategory item;
        if (value != null) {
            item = values.get(value.toLowerCase());
        } else {
            item = getDefaultForValue();
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, FlightCategory> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return getDefaultToValue();
    }

    public static String getDefaultToValue() {
        return NON_SCH.toValue();
    }

    public static FlightCategory getDefaultForValue() {
        return NON_SCH;
    }

}
