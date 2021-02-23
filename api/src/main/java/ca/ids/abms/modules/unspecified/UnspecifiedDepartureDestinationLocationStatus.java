package ca.ids.abms.modules.unspecified;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import ca.ids.abms.util.EnumWithLabels;

import java.util.Arrays;
import java.util.List;

public enum UnspecifiedDepartureDestinationLocationStatus implements EnumWithLabels {
    
    // Constructor arguments are "labels", used for text searching and for
    // JSON serialization (first label)
    
    MANUAL ("Manual"),
    SYS_GENERATED ("System Generated", "System");
    
    @Override
    public List<String> labels() {
        return labels;
    }

    @JsonValue
    @Override
    public String toJsonValue() {
        return labels.get (0);
    }
    
    @JsonCreator
    public static UnspecifiedDepartureDestinationLocationStatus forJsonValue (final String jsonValue) {
        return EnumWithLabels.forJsonValue(values(), jsonValue);
    }
    
    
    private UnspecifiedDepartureDestinationLocationStatus (final String... labels) {
        this.labels = Arrays.asList (labels);
    }
    
    private final List <String> labels;


}
