package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DailySchedule {

    SUNDAY("1"),
    MONDAY("2"),
    TUESDAY("3"),
    WEDNESDAY("4"),
    THURSDAY("5"),
    FRIDAY("6"),
    SATURDAY("7");

    private static Map<String, DailySchedule> values = new HashMap<>(21);
    static {
        values.put("1", SUNDAY);
        values.put("2", MONDAY);
        values.put("3", TUESDAY);
        values.put("4", WEDNESDAY);
        values.put("5", THURSDAY);
        values.put("6", FRIDAY);
        values.put("7", SATURDAY);
    }


    private String value;

    DailySchedule(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    @JsonValue
    public String toValue() {
        String value=null;
        for (Map.Entry<String, DailySchedule> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value= entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static DailySchedule forValue(final String value) {
        DailySchedule item = null;
        if (StringUtils.isNotEmpty(value)) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
