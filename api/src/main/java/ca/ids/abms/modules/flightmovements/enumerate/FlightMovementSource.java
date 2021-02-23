package ca.ids.abms.modules.flightmovements.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by c.talpa on 17/01/2017.
 */
public enum FlightMovementSource {

    NETWORK,
    RADAR_SUMMARY,
    ATC_LOG,
    TOWER_LOG,
    MANUAL;

    public static final String NETWORK_TYPE="AFTN/AMHS/SPATIA";

    private static Map<String, FlightMovementSource> values = new HashMap<>(5);
    static {
        values.put(NETWORK_TYPE, NETWORK);
        values.put("radar-summary", RADAR_SUMMARY);
        values.put("atc-log", ATC_LOG);
        values.put("tower-log", TOWER_LOG);
        values.put("manual", MANUAL);
    }

    FlightMovementSource() {}

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, FlightMovementSource> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static FlightMovementSource forValue(String value) {
        FlightMovementSource item=null;
        if (value != null && !value.isEmpty()) {
            if(value.equalsIgnoreCase(NETWORK_TYPE)){
                item=FlightMovementSource.NETWORK;
            }else {
                item = values.get(value.toLowerCase());
            }
        }
        return item;
    }

}
