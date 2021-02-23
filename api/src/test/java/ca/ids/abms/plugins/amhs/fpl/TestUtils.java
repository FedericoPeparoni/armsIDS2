package ca.ids.abms.plugins.amhs.fpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import ca.ids.abms.amhs.AmhsMessage;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageParser;
import ca.ids.abms.plugins.amhs.AmhsMessageTypeDetector;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;
import ca.ids.abms.util.JSR310LocalDateDeserializer;

class TestUtils {

    private static final DateTimeFormatter ISO_FIXED_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    
    public static AmhsMessageContext parse (final String body, final LocalDateTime filingDateTime) {
        final AmhsParsedMessage amhsParsedMessage = new AmhsParsedMessage();
        amhsParsedMessage.amhsMessageType = AmhsMessageTypeDetector.guessType(body);
        amhsParsedMessage.amhsMessage = new AmhsMessage();
        amhsParsedMessage.amhsMessage.setBody(body);
        amhsParsedMessage.amhsMessage.setFilename("UNKNOWN");
        amhsParsedMessage.amhsMessage.setFilingDateTime(filingDateTime);
        amhsParsedMessage.amhsMessage.setRawMessageText("");
        final AmhsMessageContext ctx = new AmhsMessageContext (amhsParsedMessage);
        
        final FlightMessageParser flightMessageParser = new FlightMessageParser();
        final AmhsMessageParser amhsMessageParser = new AmhsMessageParser (flightMessageParser);
        
        amhsMessageParser.parse(ctx);
        
        return ctx;
    }
    
    public static ObjectMapper createObjectMapper() {
        final JavaTimeModule timeModule = new JavaTimeModule();
        timeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(ISO_FIXED_FORMAT));
        timeModule.addDeserializer(LocalDate.class, JSR310LocalDateDeserializer.INSTANCE);
        final List<Module> modules = Arrays.asList(timeModule);

        return new Jackson2ObjectMapperBuilder()
            .featuresToDisable (DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .modules (modules)
            .build();
    }

    public static <T> T readJson (final ObjectMapper objectMapper, final InputStream stream, final Class<T> clazz) {
        try {
            return objectMapper.readValue (stream, clazz);
        } catch (final IOException x) {
            throw new RuntimeException(x);
        }
    }

    public static Stream<RefMessageResource> readJsonFileNamesFromDir(final String dir) {
        return Stream.of(new File(dir).listFiles()).filter(f -> f.getName().endsWith(".json"))
                .map(f -> new RefMessageResource(f.toString())).sorted((a, b) -> a.name.compareTo(b.name));
    }

}
