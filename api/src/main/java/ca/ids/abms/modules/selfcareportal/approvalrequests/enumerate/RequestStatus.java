package ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RequestStatus {
    OPEN,
    APPROVED,
    REJECTED;

    private static Map<String, RequestStatus> values = new HashMap<>(6);
    static {
        values.put("open", OPEN);
        values.put("approved", APPROVED);
        values.put("rejected", REJECTED);
    }

    @JsonValue
    public String toValue() {
        String value = null;
        for (Map.Entry<String, RequestStatus> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value = entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static RequestStatus forValue(String value) {
        RequestStatus item = null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }
}
