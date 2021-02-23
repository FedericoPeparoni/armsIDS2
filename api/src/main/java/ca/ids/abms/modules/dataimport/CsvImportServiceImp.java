package ca.ids.abms.modules.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.CustomParametrizedException;

/**
 * This is a concrete implementation of CSV file import
 * @author i.protasov
 *
 * @param <T>
 */
@Component
@Primary
public class CsvImportServiceImp<T> implements DataImportService<T> {

    private static final Logger LOG = LoggerFactory.getLogger(CsvImportServiceImp.class);

    // csv files can have mime types of application/vnd.ms-excel if excel is installed
    // see stackoverflow discussion here https://stackoverflow.com/a/7076079/3502564
    private final MediaType MEDIA_TYPE_APPLICATION_EXCEL_CSV = new MediaType("application", "vnd.ms-excel");
    private final MediaType MEDIA_TYPE_TEXT_CSV = new MediaType("text", "csv");

    @Value("${csv-reader.ignore-first-line}")
    private Boolean ignoreFirstLine;

    @Value("${csv-reader.seperated-char}")
    private Character separatedChar;

    @Value("${csv-reader.quote-char}")
    private Character quateChar;

    @Override
    public List<T> parseFromFile(File file, Class<T> targetClass) {
        return parseFromFile(file, STRATEGY.BIND_BY_POSITION, targetClass);
    }

    @Override
    public List<T> parseFromFile(File file, STRATEGY strategy, Class<T> targetClass) {
        try {

            // attempt to get content type from file
            String contentType = Files.probeContentType(file.toPath());

            // if not content type found return
            if (contentType == null || contentType.isEmpty()) {
                LOG.warn("Could not determine content type of file {}.", file);
                return null;
            }

            // parse file with appropriate method depending on content type
            if (contentType.equals(XLSXToCSVConverter.APPLICATION_XLXS))
                return parseXLSXFromInputStream(new FileInputStream(file), strategy, targetClass);
            else
                return parseCsvFromInputStream(contentType, new FileInputStream(file), strategy, targetClass);

        } catch (Exception ex) {
            throw new CustomParametrizedException(ex.getMessage(), ex);
        }
    }

    @Override
    public List<T> parseFromMultipartFile(MultipartFile file, Class<T> targetClass) {
       return parseFromMultipartFile(file, STRATEGY.BIND_BY_POSITION, targetClass);
    }

    @Override
    public List<T> parseFromMultipartFile(final MultipartFile file, final STRATEGY strategy, final Class<T> targetClass) {
        try {
            if (file.getContentType()!= null && file.getContentType().equals(XLSXToCSVConverter.APPLICATION_XLXS)) {
                return parseXLSXFromInputStream(file.getInputStream(), strategy, targetClass);
            }
            else {
                return parseCsvFromInputStream(file.getContentType(), file.getInputStream(), strategy, targetClass);
            }
        } catch (final IOException ex) {
            if (ex.getMessage() != null && ex.getCause() != null && ex.getCause().getMessage() != null) {
                final Pattern pattern = Pattern.compile(".*error at line (\\d+).*", Pattern.CASE_INSENSITIVE);
                final Matcher matcher = pattern.matcher(ex.getMessage());
                if (matcher.matches()) {
                    final String line = matcher.group(1);
                    final String descr = String.format("Line %s: %s", line, ex.getCause().getMessage());
                    throw new CustomParametrizedException(descr, ex);
                }
            }
            throw new CustomParametrizedException(ex.getMessage(), ex);
        } catch (final Exception ex) {
            throw new CustomParametrizedException(ex.getMessage(), ex);
        }
    }

    private List<T> parseXLSXFromInputStream(InputStream fileInputStream, STRATEGY strategy,
                                             Class<T> targetClass) throws Exception {

        String errorMsg = "XLSX file upload error: Invalid MediaType";
        String content = null;
        try {
            content = XLSXToCSVConverter.convert(fileInputStream);
        } catch (InvalidMediaTypeException ex) {
            LOG.error(errorMsg);
        }

        return parseContent(content, strategy, targetClass);
    }

    public List<T> parseCsvFromInputStream(String origMimeType, InputStream csvFileInputStream, STRATEGY strategy,
                                           Class<T> targetClass) throws Exception {

        String errorMsg = "Csv file upload error: Invalid MediaType";

        MediaType parsedMediaType = null;

        try {
            parsedMediaType = MediaType.parseMediaType(origMimeType);
        } catch (InvalidMediaTypeException ex) {
            LOG.error(errorMsg);
        }

        if (parsedMediaType == null || !this.getCsvMediaTypes().contains(parsedMediaType)) {
            LOG.error(errorMsg);
            throw new CustomParametrizedException(errorMsg);
        }

        String csvContent = readFileContent(csvFileInputStream);
        return parseContent(csvContent, strategy, targetClass);
    }

    private List<T> parseContent(String content, STRATEGY strategy, Class<T> targetClass) throws IOException{
        if (strategy == STRATEGY.BIND_BY_POSITION) {
            return positionCsvToObject(content, targetClass);
        } else {
            return parseColumnCsvToObject(content, targetClass);
        }
    }

    public List<T> positionCsvToObject(String providedCsv, Class<T> targetClass) throws IOException {

        List<T> result = null;

        result = (List<T>) positionMapToCSV(providedCsv, targetClass);

        return result;
    }

    public String readFileContent(InputStream csvFileInputStream) throws Exception {
        byte[] bdata = FileCopyUtils.copyToByteArray(csvFileInputStream);
        final String data = new String(bdata, StandardCharsets.UTF_8);
        return data;
    }


    public CSVReader createCSVReader(InputStream csvFileInputStream) {

        return createCSVReader(csvFileInputStream, separatedChar, quateChar, ignoreFirstLine);
    }


    public CSVReader createCSVReader(InputStream csvFileInputStream, char seperatedChar, char quoteChar,
                                     boolean ignoreFirstLine) {

        CSVReader reader = null;
        int firstLine = 0;
        if (ignoreFirstLine) {
            firstLine = 1;
        }

        if (csvFileInputStream != null) {
            try {
                String csvContent= readFileContent(csvFileInputStream);
                reader = new CSVReader(new StringReader(csvContent), seperatedChar,quoteChar, firstLine);
            } catch (Exception e) {
                LOG.error("Error on e createCSVReader:", e);
            }
        }
        return reader;
    }


    public List<T> parseColumnCsvToObject(String providedCsv, Class<T> targetClass) throws IOException {

        List<T> result = null;

        result = (List<T>) columnStrategyMap(providedCsv, targetClass);

        return result;
    }

    public List<T> parsePositionCsvToObject(InputStream csvFileInputStream, Class<T> targetClass) throws IOException {

        List<T> result = null;

        result = (List<T>) positionMapToCSV(csvFileInputStream, targetClass, separatedChar, quateChar, ignoreFirstLine);

        return result;
    }

    /* Utility Methods */

    /**
     *
     * @param csvContent
     * @param mapToClazz
     * @param <T>
     * @return
     * @throws IOException
     */
    private <T> List<T> columnStrategyMap(String csvContent, Class<T> mapToClazz) throws IOException {

        Preconditions.checkNotNull(mapToClazz);

        CsvToBean<T> csvToBean = new CsvToBean<T>();

        Map<String, String> columnMapping = new HashMap<>();
        Arrays.stream(mapToClazz.getDeclaredFields()).forEach(field -> {
            columnMapping.put(field.getName(), field.getName());
        });

        validateColumnMapping(csvContent,columnMapping);

        HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<T>();
        strategy.setType(mapToClazz);
        strategy.setColumnMapping(columnMapping);

        CSVReader reader = new CSVReader(new StringReader(csvContent));

        return csvToBean.parse(strategy, reader);
    }



    private <T> List<T> positionMapToCSV(String csvContent, Class<T> mapToClazz) throws IOException {

        Preconditions.checkNotNull(mapToClazz);

        CsvToBean<T> csvToBean = new CsvToBean<T>();
        List<String> columList=new ArrayList<>();
        Arrays.stream(mapToClazz.getDeclaredFields()).forEach(field -> {
            columList.add(field.getName());
        });

        String[] columns = columList.toArray(new String[columList.size()]);
        ColumnPositionMappingStrategy <T> strategy = new ColumnPositionMappingStrategy<T>();
        strategy.setType(mapToClazz);

        CSVReader reader = new CSVReader(new StringReader(csvContent));

        return csvToBean.parse(strategy, reader);
    }

    /**
     *
     * @param csvFileInputStream
     * @param targetClass
     * @param seperatedChar
     * @param quoteChar
     * @param ignoreFirstLine
     * @param <T>
     * @return
     * @throws IOException
     */
    private <T> List<T> positionMapToCSV(InputStream csvFileInputStream, Class<T> targetClass,char seperatedChar,char quoteChar, boolean ignoreFirstLine) throws IOException {

        Preconditions.checkNotNull(targetClass);

        CsvToBean<T> csvToBean = new CsvToBean<T>();
        List<String> columList=new ArrayList<>();
        Arrays.stream(targetClass.getDeclaredFields()).forEach(field -> {
            columList.add(field.getName());
        });

        ColumnPositionMappingStrategy <T> strategy = new ColumnPositionMappingStrategy<T>();
        strategy.setType(targetClass);

        CSVReader reader = createCSVReader(csvFileInputStream,seperatedChar,quoteChar, ignoreFirstLine);

        return csvToBean.parse(strategy, reader);
    }


    /**
     * Validate if at least one valid column exist in the imported CSV file
     * @param csvContent
     * @param columnMapping
     */
    private void validateColumnMapping(String csvContent, Map<String, String> columnMapping) {
        boolean isValid = false;

        if (columnMapping != null) {
            for (Map.Entry<String, String> entry : columnMapping.entrySet())
            {
                if (csvContent.contains(entry.getValue())) {
                    isValid = true;
                    break;
                }
            }
        }

        if (!isValid) {
            throw new CustomParametrizedException("Invalid columns");
        }
    }

    protected Boolean getIgnoreFirstLine() {
        return ignoreFirstLine;
    }

    protected Character getSeparatedChar() {
        return separatedChar;
    }

    protected Character getQuateChar() {
        return quateChar;
    }

    List<MediaType> getCsvMediaTypes() {
        return Arrays.asList(MediaType.TEXT_PLAIN, this.MEDIA_TYPE_TEXT_CSV, this.MEDIA_TYPE_APPLICATION_EXCEL_CSV);
    }
}
