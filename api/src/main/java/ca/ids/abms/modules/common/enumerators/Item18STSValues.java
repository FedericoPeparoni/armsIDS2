package ca.ids.abms.modules.common.enumerators;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Item18STSValues {

    ALTRV,
    ATFMX,
    FFR,
    FLTCK,
    HAZMAT,
    HEAD,
    HOSP,
    HUM,
    MARSA,
    MEDEVAC,
    NONRVSM,
    SAR,
    STATE;

    private static Map<String, Item18STSValues> values = new HashMap<>(13);

    static {
        values.put("ALTRV", ALTRV);
        values.put("ATFMX", ATFMX);
        values.put("FFR", FFR);
        values.put("FLTCK", FLTCK);
        values.put("HAZMAT", HAZMAT);
        values.put("HEAD", HEAD);
        values.put("HOSP", HOSP);
        values.put("HUM", HUM);
        values.put("MARSA", MARSA);
        values.put("MEDEVAC", MEDEVAC);
        values.put("NONRVSM", NONRVSM);
        values.put("SAR", SAR);
        values.put("STATE", STATE);
    }

    @JsonCreator
    public static Item18STSValues forValue(String value) {
        Item18STSValues item;
        if (value != null) {
            item = values.get(value.toUpperCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, Item18STSValues> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

     public static boolean ifExists (final String value) {
        return values.containsKey(value);
    }
}
