package ca.ids.abms.modules.manifests;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum FlightType {
    INTERNATIONAL, DOMESTIC, BOTH;

    private static Map<String, FlightType> types = new HashMap<>(3);

    static {
        types.put("international", INTERNATIONAL);
        types.put("domestic", DOMESTIC);
        types.put("both", BOTH);
    }

    @JsonCreator
    public static FlightType forValue(String value) {
        return types.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, FlightType> entry : types.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
