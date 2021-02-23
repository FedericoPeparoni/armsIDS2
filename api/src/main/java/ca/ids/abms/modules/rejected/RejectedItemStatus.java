package ca.ids.abms.modules.rejected;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;

public enum RejectedItemStatus {

    UNCORRECTED("uncorrected"),
    CORRECTED("corrected");

    private String value;

    private static HashMap<String, RejectedItemStatus> values = new HashMap<>(2);

    RejectedItemStatus(final String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    static {
        values.put("uncorrected", UNCORRECTED);
        values.put("corrected", CORRECTED);
    }

    @JsonCreator
    public static RejectedItemStatus forValue(String value) {
        RejectedItemStatus item = null;
        if (value != null) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        return this.getValue();
    }

    @Override
    public String toString() {
        return "RejectedItemStatus{" +
            "value='" + value + '\'' +
            '}';
    }
}
