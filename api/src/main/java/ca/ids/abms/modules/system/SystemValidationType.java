package ca.ids.abms.modules.system;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum SystemValidationType {

    INVOICE_SPEC, CONNECTION_URL;

    private static Map<String, SystemValidationType> values = new HashMap<>(1);
    static {
        values.put("INVOICE_SPEC", INVOICE_SPEC);
        values.put("CONNECTION_URL", CONNECTION_URL);
    }

    @JsonValue
    public String toValue() {
        String value = null;
        for (Map.Entry<String, SystemValidationType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                value = entry.getKey();
        }
        return value;
    }

    @JsonCreator
    public static SystemValidationType forValue(String value) {
        SystemValidationType item = null;
        if (value != null && !value.isEmpty()) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }
}
