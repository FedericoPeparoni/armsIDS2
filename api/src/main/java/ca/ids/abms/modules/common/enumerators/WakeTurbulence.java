package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum WakeTurbulence {

    L, M, H, J;

    private static Map<String, WakeTurbulence> values = new HashMap<>(2);

    static {
        values.put("L", L);
        values.put("M", M);
        values.put("H", H);
        values.put("J", J);
    }

    @JsonCreator
    public static WakeTurbulence forValue(String value) {
        WakeTurbulence item;
        if (value != null) {
            item = values.get(value.toUpperCase());
        } else {
            item = H;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, WakeTurbulence> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return "H";
    }

    public String valueOf() {
        return toValue();
    }

    public static String getDefaultToValue() {
        return H.toValue();
    }

    public static WakeTurbulence getDefaultForValue() {
        return H;
    }
}
