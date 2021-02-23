package ca.ids.oxr.client.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class UnixTimestampDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        LocalDateTime dateTime;
        try {
            dateTime = LocalDateTime.ofEpochSecond(jp.getValueAsLong(), 0, ZoneOffset.UTC);
        } catch (final NumberFormatException e) {
            throw new IOException("Unable to deserialize timestamp: " + jp.getValueAsLong(), e);
        }
        return dateTime;
    }
}
