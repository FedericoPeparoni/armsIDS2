package ca.ids.abms.modules.transactions.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum TransactionDocumentType {
    APPROVAL, RECEIPT, SUPPORTING;

    private static final Map<String, TransactionDocumentType> types = new HashMap<>(3);

    static {
        types.put("APPROVAL", APPROVAL);
        types.put("RECEIPT", RECEIPT);
        types.put("SUPPORTING", SUPPORTING);
    }

    @JsonCreator
    public static TransactionDocumentType forValue(String value) {
        return value == null ? null : types.get(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, TransactionDocumentType> entry : types.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
