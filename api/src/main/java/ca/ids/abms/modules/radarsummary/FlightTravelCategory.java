package ca.ids.abms.modules.radarsummary;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ca.ids.abms.util.StringUtils;

/**
 * Created by c.talpa on 10/02/2017.
 */
public enum FlightTravelCategory {

    OVERFLIGHT("OVERFLIGHT"),
    ARRIVAL("ARRIVAL"),
    DEPARTURE("DEPARTURE"),
    DOMESTIC("DOMESTIC");

    private static Map<String, FlightTravelCategory> values = new HashMap<>();

    static {
        values.put("overflight", OVERFLIGHT);
        values.put("arrival", ARRIVAL);
        values.put("departure", DEPARTURE);
        values.put("domestic", DOMESTIC);
    }

    private String value;

    FlightTravelCategory(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    public static FlightTravelCategory mapFromValue(String value){
        FlightTravelCategory flightTravelCategory = null;

        value=StringUtils.checkAndTrimString(value);

        if (value != null) {
            String valueUpperCase = StringUtils.checkAndTrimString(value).toUpperCase();

            switch (valueUpperCase) {
                case "OVR":
                case "OVF":
                case "OVERFLIGHT": flightTravelCategory = FlightTravelCategory.OVERFLIGHT; break;

                case "DOM":
                case "INT":
                case "DOMESTIC": flightTravelCategory = FlightTravelCategory.DOMESTIC; break;

                case "DEP":
                case "DEPARTURE":
                case "OUT":
                case "OUTGOING":flightTravelCategory = FlightTravelCategory.DEPARTURE; break;

                case "ARR":
                case "ARRIVAL":
                case "INB":
                case "INCOMING":flightTravelCategory = FlightTravelCategory.ARRIVAL; break;
            }
        }

        return flightTravelCategory;
    }

    @Override
    public String toString() {
        return "FlightTravelCategory{" +
            "value='" + value + '\'' +
            '}';
    }

    @JsonCreator
    public static FlightTravelCategory forValue(String value) {
        FlightTravelCategory category = values.get(value.toLowerCase());

        return category;
    }

    @JsonValue
    public String toValue() {
        String returnValue=null;
        for (Map.Entry<String, FlightTravelCategory> entry : values.entrySet()) {
            if (entry.getValue() == this) {
                returnValue = entry.getKey().toLowerCase();
                break;
            }
        }

        return returnValue;
    }
}
