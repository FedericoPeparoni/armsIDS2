package ca.ids.abms.modules.dataimport;

import java.beans.IntrospectionException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanFilter;
import com.opencsv.bean.MappingStrategy;
import com.opencsv.exceptions.CsvBadConverterException;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.CustomDataMapper;

public class ABMSCsvToBean<T extends RejectableCsvModel> extends CsvToBean<T> {

    private static final Logger LOG = LoggerFactory.getLogger(ABMSCsvToBean.class);

    private final int ignoreRows;

    private final String stopToken;

    public ABMSCsvToBean() {
        super();
        this.ignoreRows = 0;
        this.stopToken = StringUtils.EMPTY;
    }

    public ABMSCsvToBean(int ignoreRows) {
        super();
        this.ignoreRows = ignoreRows;
        this.stopToken = StringUtils.EMPTY;
    }

    public ABMSCsvToBean(int ignoreRows, final String stopToken) {
        super();
        this.ignoreRows = ignoreRows;
        this.stopToken = stopToken;
    }

    /**
     * Parse the values from the CSVReader.
     * @param mapper Mapping strategy for the bean.
     * @param csv CSVReader
     * @return List of Objects.
     */
    public List<T> parse(MappingStrategy<T> mapper, ABMSCsvReader csv) throws IOException {
        return this.parse(mapper, csv, null, true);
    }

    /**
     * Parse the values from the CSVReader.
     * @param mapper Mapping strategy for the bean.
     * @param csv CSVReader
     * @return List of Objects.
     */
    public List<T> parse(CustomDataMapper<T> mapper, ABMSCsvReader csv) throws IOException {
        return this.parse(mapper, csv, null, true);
    }

    /**
     * Parse the values from the CSVReader.
     * Only throws general exceptions from external code used. Problems related
     * to opencsv and the data provided to it are captured for later processing
     * by user code and can be accessed through {@link #getCapturedExceptions()}.
     *
     * @param mapper          Mapping strategy for the bean.
     * @param csv             CSVReader
     * @param filter          CsvToBeanFilter to apply - null if no filter.
     * @param throwExceptions If false, exceptions internal to opencsv will not
     *                        be thrown, but can be accessed after processing is finished through
     *                        {@link #getCapturedExceptions()}.
     * @return List of Objects.
     */
    public List<T> parse(MappingStrategy<T> mapper, ABMSCsvReader csv,
                         CsvToBeanFilter filter, boolean throwExceptions) throws IOException {
        final List<T> list = new ArrayList<>();
        long lineProcessed = 0;
        int ignoreHeaderRows = this.ignoreRows;
        RawData line = null;
        try {
            mapper.captureHeader(csv);
        } catch (Exception e) {
            if (throwExceptions) {
                throw new CustomParametrizedException("Error capturing CSV header!", e);
            } else {
                LOG.debug("Cannot capture the CSV header", e);
            }
        }
        try {
            while (null != (line = csv.readNextSingleLine(stopToken, ignoreHeaderRows > 0))
                && !line.isEndOfData()) {
                lineProcessed++;
                if (ignoreHeaderRows > 0) {
                    ignoreHeaderRows--;
                } else {
                    processLine(mapper, filter, line, lineProcessed, list);
                }
            }
            return list;
        } catch (Exception e) {
            if (throwExceptions) {
                throw new IOException("Read error at line" + " " + lineProcessed +  " " + "with values" + ": " +
                    line, e);
            } else {
                LOG.warn("Read error at line {} with values: {} ", lineProcessed, line, e);
            }
        }
        return list;
    }

    /**
     * Parse the values from the CSVReader using a {CustomDataMapper}
     * Only throws general exceptions from external code used. Problems related
     * to opencsv and the data provided to it are captured for later processing
     * by user code and can be accessed through {@link #getCapturedExceptions()}.
     *
     * @param mapper          A custom data mapper
     * @param csv             CSVReader
     * @param filter          CsvToBeanFilter to apply - null if no filter.
     * @param throwExceptions If false, exceptions internal to opencsv will not
     *                        be thrown, but can be accessed after processing is finished through
     *                        {@link #getCapturedExceptions()}.
     * @return List of Objects.
     *
     */
    public List<T> parse(final CustomDataMapper<T> mapper, ABMSCsvReader csv,
                         CsvToBeanFilter filter, boolean throwExceptions) throws IOException {
        final List<T> list = new ArrayList<>();
        long lineProcessed = 0;
        int ignoreHeaderRows = this.ignoreRows;
        RawData line = null;
        try {
            while (null != (line = csv.readNextSingleLine(stopToken, ignoreHeaderRows > 0))
                && !line.isEndOfData()) {
                lineProcessed++;
                if (ignoreHeaderRows > 0) {
                    ignoreHeaderRows--;
                } else {
                    processLine(mapper, filter, line, lineProcessed, list);
                }
            }
            return list;
        } catch (Exception e) {
            if (throwExceptions) {
                throw new IOException(
                    "Read error at line" +  " " + lineProcessed + " " + "because" + ": " + e.getLocalizedMessage(), e);
            } else {
                LOG.warn("Read error at line {} with values: {} ", lineProcessed, line, e);
            }
        }
        return list;
    }

    private void processLine(final CustomDataMapper<T> mapper, final CsvToBeanFilter filter, RawData lineRead,
                             long lineNo, final List<T> list)
        throws IllegalAccessException, InvocationTargetException,
        InstantiationException, IntrospectionException,
        CsvBadConverterException, CsvDataTypeMismatchException,
        CsvRequiredFieldEmptyException, CsvConstraintViolationException {
        if (!lineRead.isEmptyLine() && (filter == null || filter.allowLine(lineRead.getResult()))) {
            try {
                final T parsedObject = mapper.convertStringArrayIntoCsvModel(lineRead.getResult());
                parsedObject.setRawText(lineRead.getInitialData());
                parsedObject.setParsed(true);
                parsedObject.setLine(lineNo);
                list.add(parsedObject);
            } catch (final Exception csve) {
                final RejectableCsvModel unparsedObject = new DefaultRejectableCsvModel();
                unparsedObject.setRawText(lineRead.getInitialData());
                unparsedObject.setParsed(false);
                unparsedObject.setLine(lineNo);
                unparsedObject.setErrorMessage(ExceptionFactory.resolveManagedErrors(csve, " at row #" + lineNo));
                list.add((T) unparsedObject);
            }
        }
    }

    private void processLine(final MappingStrategy<T> mapper, final CsvToBeanFilter filter, RawData lineRead, long lineNo,
                             final List<T> list)
        throws IllegalAccessException, InvocationTargetException,
        InstantiationException, IntrospectionException,
        CsvBadConverterException, CsvDataTypeMismatchException,
        CsvRequiredFieldEmptyException, CsvConstraintViolationException {
        if (!lineRead.isEmptyLine() && (filter == null || filter.allowLine(lineRead.getResult()))) {
            try {
                final T parsedObject = processLine(mapper, lineRead.getResult());
                parsedObject.setRawText(lineRead.getInitialData());
                parsedObject.setParsed(true);
                parsedObject.setLine(lineNo);
                list.add(parsedObject);
            } catch (final Exception csve) {
                final RejectableCsvModel unparsedObject = new DefaultRejectableCsvModel();
                unparsedObject.setRawText(lineRead.getInitialData());
                unparsedObject.setParsed(false);
                unparsedObject.setLine(lineNo);
                unparsedObject.setErrorMessage(ExceptionFactory.resolveManagedErrors(csve, " at row #" + lineNo));
                list.add((T) unparsedObject);
            }
        }
    }
}
