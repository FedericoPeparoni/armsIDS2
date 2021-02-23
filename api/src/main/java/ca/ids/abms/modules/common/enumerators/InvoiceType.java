package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum InvoiceType {

    AVIATION_IATA, AVIATION_NONIATA, NON_AVIATION, PASSENGER, DEBIT_NOTE, OVERDUE, INTEREST;

    private static Map<String, InvoiceType> values = new HashMap<>(5);

    static {
        values.put("aviation-iata", AVIATION_IATA);
        values.put("aviation-noniata", AVIATION_NONIATA);
        values.put("non-aviation", NON_AVIATION);
        values.put("passenger", PASSENGER);
        values.put("debit-note", DEBIT_NOTE);
        values.put("overdue", OVERDUE);
        values.put("interest", INTEREST);
    }

    @JsonCreator
    public static InvoiceType forValue(String value) {
        InvoiceType item;
        if (value != null) {
            item = values.get(value.toLowerCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, InvoiceType> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

}
