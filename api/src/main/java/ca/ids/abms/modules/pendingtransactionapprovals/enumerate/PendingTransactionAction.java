package ca.ids.abms.modules.pendingtransactionapprovals.enumerate;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum PendingTransactionAction {
    APPROVAL, REJECTION;

    private static final Map<String, PendingTransactionAction> types = new HashMap<>(2);

    static {
        types.put("APPROVAL", APPROVAL);
        types.put("REJECTION", REJECTION);
    }

    @JsonCreator
    public static PendingTransactionAction forValue(String value) {
        return value == null ? null : types.get(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, PendingTransactionAction> entry : types.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }

}
