package ca.ids.abms.modules.pendingtransactions.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum PendingTransactionDocumentType {
    APPROVAL,
    SUPPORTING;

    private static final Map<String, PendingTransactionDocumentType> types = new HashMap<>(3);

    static {
        types.put("APPROVAL", APPROVAL);
        types.put("SUPPORTING", SUPPORTING);
    }

    @JsonCreator
    public static PendingTransactionDocumentType forValue(String value) {
        return value == null ? null : types.get(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, PendingTransactionDocumentType> entry : types.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
