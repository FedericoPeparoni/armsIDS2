package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum InvoiceCategory {

    IATA_AVI, NON_IATA_AVI, CASH_AVI, NON_AVI, UTILITY, LEASE, CONCESSION, POS, RECEIPT;

    private static Map<String, InvoiceCategory> values = new HashMap<>(2);

    static {
        values.put("iata-avi", IATA_AVI);
        values.put("non-iata-avi", NON_IATA_AVI);
        values.put("cash-avi", CASH_AVI);
        values.put("non-avi", NON_AVI);
        values.put("utility", UTILITY);
        values.put("lease", LEASE);
        values.put("concession", CONCESSION);
        values.put("pos", POS);
        values.put("receipt", RECEIPT);
    }

    @JsonCreator
    public static InvoiceCategory forValue(String value) {
        InvoiceCategory item = null;
        if (value != null) {
            item = values.get(value.toLowerCase());
        }
        return item;
    }

    public String valueOf() {
        return toValue();
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, InvoiceCategory> entry : values.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
