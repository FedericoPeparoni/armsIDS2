package ca.ids.abms.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum ContentTypes {

    XLSX, XLS, CSV, TXT, JSON, OCTET_STREAM;

    private static final Map<String, ContentTypes> CONTENT_TYPES = new HashMap<>(5);

    static {
        CONTENT_TYPES.put("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", XLSX);
        CONTENT_TYPES.put("application/vnd.ms-excel", XLS);
        CONTENT_TYPES.put("text/csv", CSV);
        CONTENT_TYPES.put("text/plain", TXT);
        CONTENT_TYPES.put("application/json", JSON);
        CONTENT_TYPES.put("application/octet-stream", OCTET_STREAM);
    }

    @JsonCreator
    public static ContentTypes forValue(final String value) {
        return value == null || value.isEmpty() ? ContentTypes.OCTET_STREAM
            : CONTENT_TYPES.get(StringUtils.lowerCase(value));
    }

    @JsonValue
    public String toValue() {
        for (Map.Entry<String, ContentTypes> entry : CONTENT_TYPES.entrySet()) {
            if (entry.getValue() == this)
                return entry.getKey();
        }
        return null;
    }
}
