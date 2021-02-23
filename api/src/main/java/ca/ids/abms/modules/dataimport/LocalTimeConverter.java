package ca.ids.abms.modules.dataimport;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * This custom LocalTime Converter is needed because opencsv doesn't support conversion from String to LocalTime type
 * @author i.protasov
 *
 */
public class LocalTimeConverter extends AbstractBeanField {


    @Value("${csv-reader.time-pattern?: 'HHmm'}")
    private String timePattern;

    @Override
    protected LocalTime convert(String value)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {


        if(timePattern==null){
            timePattern="HHmm";
        }

        LocalTime result = null;

        if (value != null && !value.trim().isEmpty()) {
            final DateTimeFormatter fmt  = DateTimeFormatter.ofPattern(timePattern);
            result = LocalTime.parse(value, fmt);
        }

        return result;
    }

}
