package ca.ids.abms.modules.rejected;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

public enum RejectedItemType {
    FLIGHT_MOVEMENTS("FLIGHT_MOV"),
    ATS_MESSAGES("ATS_MESS"),
    ATS_MOVEMENTS_LOG("ATS_MOV"),
    TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG("TOWER_AIRC"),
    PASSENGER_SERVICE_CHARGE_RETURNS("PASSENGER_SERV"),
    PASSENGER_MANIFESTS("PASSENGER_MANIF"),
    RADAR_SUMMARIES("RADAR_SUMM"),
    LOCAL_AIRCRAFT_REGISTRY("LOCAL_AIRCRAFT_REGISTRY"),
    FLIGHT_SCHEDULE("FLIGHT_SCHEDULE"),
    AIRCRAFT_REGISTRATION("AIRCRAFT_REGISTRATION"),
    AMHS_MSG("AMHS_MSG"),
    OTHER("OTHER");

    private String value;

    RejectedItemType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    private static HashMap<String, RejectedItemType> values = new HashMap<>(8);

    static {
        values.put("FLIGHT_MOV", FLIGHT_MOVEMENTS);
        values.put("ATS_MESS", ATS_MESSAGES);
        values.put("ATS_MOV", ATS_MOVEMENTS_LOG);
        values.put("TOWER_AIRC", TOWER_AIRCRAFT_PASSENGER_MOVEMENTS_LOG);
        values.put("PASSENGER_SERV", PASSENGER_SERVICE_CHARGE_RETURNS);
        values.put("PASSENGER_MANIF", PASSENGER_MANIFESTS);
        values.put("RADAR_SUMM", RADAR_SUMMARIES);
        values.put("LOCAL_AIRCRAFT_REGISTRY", LOCAL_AIRCRAFT_REGISTRY);
        values.put("FLIGHT_SCHEDULE", FLIGHT_SCHEDULE);
        values.put("AIRCRAFT_REGISTRATION", AIRCRAFT_REGISTRATION);
        values.put("AMHS_MSG", AMHS_MSG);
        values.put("OTHER", OTHER);
    }

    @JsonCreator
    public static RejectedItemType forValue(String value) {
        RejectedItemType item = null;
        if (value != null) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        return this.getValue();
    }

    @Override
    public String toString() {
        return "RejectedItemType{" +
            "value='" + value + '\'' +
            '}';
    }
}
