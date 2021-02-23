package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum BasisForCharge {

    FIXED, PERCENTAGE, UNIT, USER, WATER, COMMERCIAL_ELECTRIC, RESIDENTIAL_ELECTRIC, DISCOUNT, EXTERNAL_DATABASE;

    private static Map<String, BasisForCharge> values = new HashMap<>(8);

    static {
        values.put("fixed", FIXED);
        values.put("percentage", PERCENTAGE);
        values.put("unit", UNIT);
        values.put("user", USER);
        values.put("water", WATER);
        values.put("commercial-electric", COMMERCIAL_ELECTRIC);
        values.put("residential-electric", RESIDENTIAL_ELECTRIC);
        values.put("discount", DISCOUNT);
        values.put("external-database", EXTERNAL_DATABASE);
    }

    @JsonCreator
    public static BasisForCharge forValue(String value) {
        BasisForCharge item = null;
        if (value != null) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, BasisForCharge> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
