package ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum RequestType {
    CREATE,
    UPDATE,
    DELETE;

    private static Map<String, RequestType> values = new HashMap<>(6);
    static {
        values.put("create", CREATE);
        values.put("update", UPDATE);
        values.put("delete", DELETE);
    }

    @JsonValue
    public String toValue() {
        String value = null;
        for (Map.Entry<String, RequestType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value = entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static RequestType forValue(String value) {
        RequestType item = null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }
}
