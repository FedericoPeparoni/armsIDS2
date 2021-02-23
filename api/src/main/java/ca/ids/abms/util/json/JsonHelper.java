package ca.ids.abms.util.json;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonHelper {
    
    private static final Pattern RE_CR = Pattern.compile ("\\r");
    private final ObjectMapper objectMapper;
    
    public JsonHelper (final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    public String toJsonString (final Object o) {
        if (o != null) {
            try {
                final String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString (o);
                return RE_CR.matcher (json).replaceAll ("");
            } catch (JsonProcessingException x) {
                throw new RuntimeException (x);
            }
        }
        return null;
    }

}
