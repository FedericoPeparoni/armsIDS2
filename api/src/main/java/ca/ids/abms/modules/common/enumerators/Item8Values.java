package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Item8Values {

    S, N, G, M, X;

    private static Map<String, Item8Values> values = new HashMap<>(13);

    static {
        values.put("S", S);
        values.put("N", N);
        values.put("G", G);
        values.put("M", M);
        values.put("X", X);
    }

    @JsonCreator
    public static Item8Values forValue(String value) {
        Item8Values item;
        if (value != null) {
            item = values.get(value.toUpperCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, Item8Values> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

    public static boolean ifExists (final String value) {
        return values.containsKey(value);
    }
}
