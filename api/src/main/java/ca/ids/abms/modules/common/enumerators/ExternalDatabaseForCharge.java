package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum ExternalDatabaseForCharge {

    AATIS,
    EAIP;

    private static Map<String, ExternalDatabaseForCharge> values = new HashMap<>(2);

    static {
        values.put("AATIS", AATIS);
        values.put("EAIP", EAIP);
    }

    @JsonCreator
    public static ExternalDatabaseForCharge forValue(String value) {
        ExternalDatabaseForCharge item = null;
        if (value != null) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, ExternalDatabaseForCharge> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
