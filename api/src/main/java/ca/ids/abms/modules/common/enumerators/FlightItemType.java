package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FlightItemType {

    ITEM18_STS, ITEM18_RMK, ITEM8_TYPE;

    private static Map<String, FlightItemType> values = new HashMap<>(3);

    static {
        values.put("ITEM18-STS", ITEM18_STS);
        values.put("ITEM18-RMK", ITEM18_RMK);
        values.put("ITEM8-TYPE", ITEM8_TYPE);
    }

    @JsonCreator
    public static FlightItemType forValue(String value) {
        FlightItemType item = null;
        if (value != null) {
            item = values.get(value.toUpperCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, FlightItemType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

    public boolean ifItemValueIsValid (final String itemValue) {
        switch(this) {
            case ITEM18_STS: return Item18STSValues.ifExists(itemValue);
            case ITEM8_TYPE: return Item8Values.ifExists(itemValue);
            case ITEM18_RMK: return true; // free text
        }
        return false;
    }
}
