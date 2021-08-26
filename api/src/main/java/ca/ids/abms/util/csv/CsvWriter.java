package ca.ids.abms.util.csv;

import ca.ids.abms.modules.system.SystemConfigurationService;
import com.opencsv.CSVWriter;

import java.io.*;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import static com.opencsv.CSVWriter.*;

/**
 * Serializes a {@link Collection} of data into a CSV stream.
 *
 * @author Derek McKinnon
 */
public final class CsvWriter {

    private final SystemConfigurationService systemConfigurationService;

    // Private constructor to prevent instantiation
    public CsvWriter(SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Serializes a {@link Collection} of data into a CSV stream.
     *
     * @param data  The data to serialize
     * @param clazz The type of the data being mapped
     * @return {@link InputStream} of the serialized CSV data
     */
    public <T> InputStream createStream(Collection<T> data, Class<T> clazz, boolean export) throws Exception {
        return createStream(data, clazz, null, export);
    }

    public <T> InputStream createStream(Collection<T> data, Class<T> clazz, boolean export, boolean header) throws Exception {
        return createStream(data, clazz, null, export, header);
    }

    private <T> InputStream createStream(Collection<T> data,
                                         Class<T> clazz,
                                         Map<String, String> placeholders,
                                         boolean export) throws Exception {

        AutoCsvMapper<T> mapper = new AutoCsvMapper<>(clazz, systemConfigurationService);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (CSVWriter writer = createWriter(os)) {
                Consumer<Collection<String>> action = row -> writer.writeNext(row.toArray(new String[0]));

                mapper.mapHeaders(action, placeholders);

                data.forEach(t -> mapper.mapData(t, action, export, true));
            }

            return new ByteArrayInputStream(os.toByteArray());
        }
    }

    private <T> InputStream createStream(Collection<T> data,
                                         Class<T> clazz,
                                         Map<String, String> placeholders,
                                         boolean export,
                                         boolean header) throws Exception {

        AutoCsvMapper<T> mapper = new AutoCsvMapper<>(clazz, systemConfigurationService);
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (CSVWriter writer = createWriter(os)) {
                Consumer<Collection<String>> action = row ->
                    writer.writeNext(
                        row.toArray(new String[0]));

                if(header == true){
                    mapper.mapHeaders(action, placeholders);
                }


                data.forEach(t ->
                    mapper.mapData(t, action, export, header));
            }

            return new ByteArrayInputStream(os.toByteArray());
        }
    }

    private static CSVWriter createWriter(OutputStream os) {
        OutputStreamWriter sw = new OutputStreamWriter(os);
        return new CSVWriter(
            sw, DEFAULT_SEPARATOR, DEFAULT_QUOTE_CHARACTER,
            DEFAULT_ESCAPE_CHARACTER, RFC4180_LINE_END
        );
    }
}
