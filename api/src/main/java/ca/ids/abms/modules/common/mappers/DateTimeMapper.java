package ca.ids.abms.modules.common.mappers;

import ca.ids.abms.modules.common.services.DateTimeConfigurationHandler;
import static ca.ids.abms.modules.common.mappers.DateTimeMapperUtils.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.ZoneId;

import java.util.Date;


/**
 * This is a mapper helpful to convert the date/time configurable
 */
public abstract class DateTimeMapper {

    @Autowired
    private DateTimeConfigurationHandler dateTimeConfigurationHandler;

    public DateTimeConfigurationHandler getDateTimeConfigurationHandler() {
        return this.dateTimeConfigurationHandler;
    }

    public void setDateTimeConfigurationHandler(DateTimeConfigurationHandler dateTimeConfigurationHandler) {
        this.dateTimeConfigurationHandler = dateTimeConfigurationHandler;
    }

    public LocalDateTime mapLocalDateTimeISO (final String date) {
        return parseISODateTime(date);
    }

    public String mapLocalDateTimeISO (final LocalDateTime date) {
        return parseISODateTime(date);
    }

    public LocalDateTime mapJavaDateToLocalDateTime(final Date date) {
        LocalDateTime result = null;
        if (date != null) {
            result = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC"));
        }
        return result;
    }

    public LocalDate mapJavaDateToLocalDate(final Date date) {
        LocalDate result = null;
        if (date != null) {
            result = LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("UTC")).toLocalDate();
        }
        return result;
    }
}
