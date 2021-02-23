package ca.ids.abms.modules.dataimport;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Created by c.talpa on 24/01/2017.
 */
public class LocalDateTimeConverter extends AbstractBeanField {

    @Value("${csv-reader.date-time-pattern?: 'yyyy-MM-dd HHmm'}")
    private String dateTimePattern;

    protected LocalDateTime convert(String value)
        throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {

        if(dateTimePattern==null){
            dateTimePattern="yyyy-MM-dd HHmm";
        }
        LocalDateTime result =convertToLocalDateTime(value, dateTimePattern);

        return result;
    }


    protected LocalDateTime convertToLocalDateTime(String value, String pattern)
        throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {

        LocalDateTime result = null;

        if(pattern!=null) {
            if (value != null && !value.trim().isEmpty()) {
                final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(pattern);
                result = LocalDateTime.parse(value, DATE_FORMAT);
            }
        }

        return result;
    }
}
