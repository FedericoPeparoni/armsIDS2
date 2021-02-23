package ca.ids.abms.modules.dataimport;

import com.opencsv.bean.AbstractBeanField;
import com.opencsv.exceptions.CsvConstraintViolationException;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.osgi.service.component.annotations.Component;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This custom LocalDate Converter is needed because opencsv doesn't support conversion from String to LocalDate type
 * @author i.protasov
 *
 */
@Component
public class LocalDateConverter extends AbstractBeanField {

    @Value("${csv-reader.date-pattern ?: 'yyyy-MM-dd'}")
    private String datePattern;

    @Override
    protected LocalDate convert(String value)
            throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {

        if(datePattern==null){
            datePattern="yyyy-MM-dd";
        }

        LocalDate result=convert(value,datePattern);
        return result;
    }


    protected LocalDate convert(String value, String pattern)
        throws CsvDataTypeMismatchException, CsvRequiredFieldEmptyException, CsvConstraintViolationException {

        LocalDate result = null;

        if(pattern!=null) {

            if (value != null && !value.trim().isEmpty()) {
                final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern(pattern);
                result = LocalDate.parse(value, DATE_FORMAT);
            }
        }

        return result;
    }

}
