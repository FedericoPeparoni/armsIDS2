package ca.ids.abms.modules.flightmovements.enumerate;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EnrouteChargesBasis {
    RADAR_SUMMARY,
    ATC_LOG,
    TOWER_LOG,
    SCHEDULED,
    NOMINAL,
    MANUAL;

    private static Map<String, EnrouteChargesBasis> values = new HashMap<>(6);
    static {
        values.put("nominal-route", NOMINAL);
        values.put("radar-summary", RADAR_SUMMARY);
        values.put("atc-log", ATC_LOG);
        values.put("tower-log", TOWER_LOG);
        values.put("manual", MANUAL);
        values.put("scheduled", SCHEDULED);
    }

    EnrouteChargesBasis(){   }

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, EnrouteChargesBasis> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static EnrouteChargesBasis forValue(String value) {
    	EnrouteChargesBasis item=null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }

}
