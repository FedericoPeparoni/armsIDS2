package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemLoaderResult {

    CREATED, UPDATED, DELETED, REJECTED;

    private static Map<String, ItemLoaderResult> values = new HashMap<>(5);

    static {
        values.put("created", CREATED);
        values.put("updated", UPDATED);
        values.put("deleted", DELETED);
        values.put("rejected", REJECTED);
    }

    @JsonCreator
    public static ItemLoaderResult forValue(String value) {
        ItemLoaderResult item = null;
        if (value != null) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, ItemLoaderResult> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
