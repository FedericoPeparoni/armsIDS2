package ca.ids.abms.modules.dataimport;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.HeaderColumnNameTranslateMappingStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.controllers.BulkLoaderComponent;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;
import ca.ids.abms.modules.common.mappers.CustomDataMapper;
import ca.ids.abms.modules.common.mappers.UseCustomDataMapper;

/**
 * This is a concrete implementation of CSV file import for rejectable items
 * @param <T>
 */
@Component(BulkLoaderComponent.DATA_IMPORT_SERVICE)
public class RejectableRowCsvImportServiceImpl<T extends RejectableCsvModel> extends CsvImportServiceImp<T>
    implements ApplicationContextAware {

    private static final String CONTENT_TYPE_ERROR = "Csv file upload error: Invalid MediaType";

    private static final Logger LOG = LoggerFactory.getLogger(RejectableRowCsvImportServiceImpl.class);

    private ApplicationContext applicationContext;

    public List<T> parseCsvFromInputStream(String origMimeType, InputStream csvFileInputStream, STRATEGY strategy,
                                           Class<T> targetClass) throws Exception {
        final MediaType parsedMediaType = MediaType.parseMediaType(origMimeType);
        if (!this.getCsvMediaTypes().contains(parsedMediaType)) {
            throw new IllegalArgumentException(CONTENT_TYPE_ERROR);
        }
        List<T> result;
        final String csvContent = readFileContent(csvFileInputStream);

        if (targetClass.isAnnotationPresent(UseCustomDataMapper.class)) {
            final UseCustomDataMapper annotation = targetClass.getAnnotation(UseCustomDataMapper.class);
            final Object bean = applicationContext.getBean(annotation.qualifier());
            if (bean instanceof CustomDataMapper) {
                result = customMapToCSV(csvContent, targetClass, (CustomDataMapper)bean, true);
            } else {
                throw new RuntimeException("Configuration error: an incompatible custom mapper has been found" + ": "
                    + annotation.qualifier() + " " + "may be an implementation of" + " " + CustomDataMapper.class.getName());
            }
        } else {
            if (strategy == STRATEGY.BIND_BY_POSITION) {
                result = positionMapToCSV(csvContent, targetClass, true);
            } else {
                result = columnStrategyMap(csvContent, targetClass);
            }
        }
        return result;
    }

    public ABMSCsvReader createCSVReader(InputStream csvFileInputStream, char seperatedChar, char quoteChar,
                                     boolean ignoreFirstLine) {
        ABMSCsvReader reader = null;
        int firstLine = 0;
        if (ignoreFirstLine) {
            firstLine = 1;
        }
        if (csvFileInputStream != null) {
            try {
                final String csvContent = readFileContent(csvFileInputStream);
                reader = new ABMSCsvReader(new StringReader(csvContent), seperatedChar,quoteChar, firstLine);
            } catch (Exception e) {
                LOG.error("Error on e createCSVReader:", e);
            }
        }
        return reader;
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
    private <T extends RejectableCsvModel> List<T> columnStrategyMap(String csvContent, Class<T> mapToClazz) throws IOException {

        Preconditions.checkNotNull(mapToClazz);

        CsvToBean<T> csvToBean = new ABMSCsvToBean<>();

        Map<String, String> columnMapping = new HashMap<>();
        Arrays.stream(mapToClazz.getDeclaredFields()).forEach(field -> {
            columnMapping.put(field.getName(), field.getName());
        });

        validateColumnMapping(csvContent,columnMapping);

        HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<T>();
        strategy.setType(mapToClazz);
        strategy.setColumnMapping(columnMapping);

        CSVReader reader = new ABMSCsvReader(new StringReader(csvContent));

        return csvToBean.parse(strategy, reader);
    }

    public List<T> positionCsvToObject(String providedCsv, Class<T> targetClass) throws IOException {
        return positionCsvToObject(providedCsv, targetClass, true);
    }

    public List<T> positionCsvToObject(String providedCsv, Class<T> targetClass, boolean readFromFile) throws IOException {
        List<T> result;
        if (targetClass.isAnnotationPresent(UseCustomDataMapper.class)) {
            final UseCustomDataMapper annotation = targetClass.getAnnotation(UseCustomDataMapper.class);
            final Object bean = applicationContext.getBean(annotation.qualifier());
            if (bean instanceof CustomDataMapper) {
                result = customMapToCSV(providedCsv, targetClass, (CustomDataMapper)bean, readFromFile);
            } else {
                throw new RuntimeException("Configuration error: an incompatible custom mapper has been found" + ": "
                    + annotation.qualifier() + " " + "may be an implementation of" + " " + CustomDataMapper.class.getName());
            }
        } else {
            result = positionMapToCSV(providedCsv, targetClass, readFromFile);
        }
        return result;
    }

    private <T> List<T> positionMapToCSV(String csvContent, Class<T> mapToClazz, boolean readFromFile) throws IOException {
        assert mapToClazz != null;
        ABMSCsvToBean csvToBean;

        /* If required, enable some logic to the parser */
        if (readFromFile && mapToClazz.isAnnotationPresent(ControlCSVFile.class)) {
            int ignoreFirstRows = mapToClazz.getAnnotation(ControlCSVFile.class).ignoreFirstRows();
            final String stopAtThisToken = mapToClazz.getAnnotation(ControlCSVFile.class).stopAtThisToken();
            csvToBean = new ABMSCsvToBean<>(ignoreFirstRows, stopAtThisToken);
        } else {
            csvToBean = new ABMSCsvToBean<>();
        }
        final List<String> columList = new ArrayList<>();
        Arrays.stream(mapToClazz.getDeclaredFields()).forEach(field -> {
            columList.add(field.getName());
        });
        final ColumnPositionMappingStrategy <T> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(mapToClazz);
        final ABMSCsvReader reader = new ABMSCsvReader(new StringReader(csvContent));
        return csvToBean.parse(strategy, reader);
    }

    private <T> List<T> customMapToCSV(String csvContent, Class<T> mapToClazz, CustomDataMapper customDataMapper, boolean readFromFile)
        throws IOException {
        assert mapToClazz != null;
        assert customDataMapper != null;
        ABMSCsvToBean csvToBean;

        /* If required, enable some logic to the parser */
        if (readFromFile && mapToClazz.isAnnotationPresent(ControlCSVFile.class)) {
            int ignoreFirstRows = mapToClazz.getAnnotation(ControlCSVFile.class).ignoreFirstRows();
            final String stopAtThisToken = mapToClazz.getAnnotation(ControlCSVFile.class).stopAtThisToken();
            csvToBean = new ABMSCsvToBean<>(ignoreFirstRows, stopAtThisToken);
        } else {
            csvToBean = new ABMSCsvToBean<>();
        }
        final List<String> columList = new ArrayList<>();
        Arrays.stream(mapToClazz.getDeclaredFields()).forEach(field -> {
            columList.add(field.getName());
        });
        final ColumnPositionMappingStrategy <T> strategy = new ColumnPositionMappingStrategy<>();
        strategy.setType(mapToClazz);
        final ABMSCsvReader reader = new ABMSCsvReader(new StringReader(csvContent));
        return csvToBean.parse(customDataMapper, reader);
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

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
