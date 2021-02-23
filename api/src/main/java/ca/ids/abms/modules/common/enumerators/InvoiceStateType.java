package ca.ids.abms.modules.common.enumerators;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public enum InvoiceStateType {

    NEW("NEW"),
    APPROVED("APPROVED"),
    PUBLISHED("PUBLISHED"),
    PAID("PAID"),
    VOID("VOID");

    private final String name;

    InvoiceStateType(final String name) {
        this.name = name;
    }

    private static Map<String, InvoiceStateType> values = new HashMap<>(5);

    static {
        values.put(NEW.toValue(), NEW);
        values.put(APPROVED.toValue(), APPROVED);
        values.put(PUBLISHED.toValue(), PUBLISHED);
        values.put(PAID.toValue(), PAID);
        values.put(VOID.toValue(), VOID);
    }

    @JsonCreator
    public static InvoiceStateType forValue(String value) {
        InvoiceStateType item;
        if (value != null) {
            item = values.get(value.toUpperCase());
        } else {
            item = null;
        }
        return item;
    }

    @JsonValue
    public String toValue() {
        return this.name;
    }
}
