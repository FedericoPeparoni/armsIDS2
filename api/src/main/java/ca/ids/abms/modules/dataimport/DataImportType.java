package ca.ids.abms.modules.dataimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum DataImportType {

    ATC(Values.ATC),
    PASSENGER(Values.PASSENGER),
    RADAR(Values.RADAR),
    TOWER(Values.TOWER),
    LOCAL_AIRCRAFT_REGISTRY(Values.LOCAL_AIRCRAFT_REGISTRY),
    FLIGHT_SCHEDULE(Values.FLIGHT_SCHEDULE),
    AIRCRAFT_REGISTRATION(Values.AIRCRAFT_REGISTRATION);

    private static Map<String, DataImportType> values = new HashMap<>();

    static {
        values.put(Values.ATC, ATC);
        values.put(Values.PASSENGER, PASSENGER);
        values.put(Values.RADAR, RADAR);
        values.put(Values.TOWER, TOWER);
        values.put(Values.LOCAL_AIRCRAFT_REGISTRY, LOCAL_AIRCRAFT_REGISTRY);
        values.put(Values.FLIGHT_SCHEDULE, FLIGHT_SCHEDULE);
        values.put(Values.AIRCRAFT_REGISTRATION, AIRCRAFT_REGISTRATION);
    }


    private String value;

    DataImportType(String value){
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "DataImportType{" +
            "value='" + value + '\'' +
            '}';
    }

    @JsonCreator
    public static DataImportType forValue(String value) {
        return values.get(value.toLowerCase());
    }

    @JsonValue
    public String toValue() {
        String returnValue = null;
        for (Map.Entry<String, DataImportType> entry : values.entrySet()) {
            if (entry.getValue() == this) {
                returnValue = entry.getKey().toLowerCase();
                break;
            }
        }
        return returnValue;
    }

    /**
     * Used within annotations to define components.
     */
    public static class Values {
        public static final String ATC = "AtcMovementLogImporter";
        public static final String PASSENGER = "PassengerServiceChargeReturnImporter";
        public static final String RADAR = "RadarSummaryImporter";
        public static final String TOWER = "TowerMovementLogImporter";
        public static final String LOCAL_AIRCRAFT_REGISTRY = "LocalAircraftRegistryImporter";
        public static final String FLIGHT_SCHEDULE = "FlightScheduleImporter";
        public static final String AIRCRAFT_REGISTRATION = "AircraftRegistrationImporter";
    }
}
