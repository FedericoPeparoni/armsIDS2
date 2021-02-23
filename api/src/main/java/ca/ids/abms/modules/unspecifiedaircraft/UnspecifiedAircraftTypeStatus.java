package ca.ids.abms.modules.unspecifiedaircraft;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ca.ids.abms.util.EnumWithLabels;

public enum UnspecifiedAircraftTypeStatus implements EnumWithLabels {
    
    // Constructor arguments are "labels", used for text searching and for
    // JSON serialization (first label)

    MANUAL ("Manual"),
    SYS_GENERATED ("System Generated", "System");
    
    @JsonCreator
    public static UnspecifiedAircraftTypeStatus forJsonValue (final String jsonValue) {
        return EnumWithLabels.forJsonValue (values(), jsonValue);
    }

    @JsonValue
    @Override
    public String toJsonValue() {
        return labels.get(0);
    }
    
    @Override
    public List <String> labels() {
        return this.labels;
    }
    
    private UnspecifiedAircraftTypeStatus (final String... labels) {
        this.labels = Arrays.asList (labels);
    }
    private final List <String> labels;
    
}
