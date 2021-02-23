package ca.ids.abms.modules.uploadedfiles.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum UploadedFileRecordType {
    ATC_MOVEMENT_LOG,
    PASSENGER_SERVICE_CHARGE_LOG,
    RADAR_SUMMARY,
    TOWER_LOG,
    UNKNOWN;

    private static Map<String, UploadedFileRecordType> values = new HashMap<>(5);
    static {
        values.put("ATC_MOVEMENT_LOG", ATC_MOVEMENT_LOG);
        values.put("PASSENGER_SERVICE_CHARGE_LOG", PASSENGER_SERVICE_CHARGE_LOG);
        values.put("RADAR_SUMMARY", RADAR_SUMMARY);
        values.put("TOWER_LOG", TOWER_LOG);
        values.put("UNKNOWN", UNKNOWN);
    }


    UploadedFileRecordType(){}

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, UploadedFileRecordType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }


    @JsonCreator
    public static UploadedFileRecordType forValue(String value) {
        UploadedFileRecordType item=null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
