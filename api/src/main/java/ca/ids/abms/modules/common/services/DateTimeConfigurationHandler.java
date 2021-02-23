package ca.ids.abms.modules.common.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationRepository;
import ca.ids.abms.util.converter.JSR310DateConverters;

@Service
@Transactional
public class DateTimeConfigurationHandler {

    public DateTimeConfigurationHandler(SystemConfigurationRepository systemConfigurationRepository) {
        this.systemConfigurationRepository = systemConfigurationRepository;
    }

    private SystemConfigurationRepository systemConfigurationRepository;

    private static final String DATE_PATTERN_ITEM_NAME = "Date format";
    private static final String TIME_PATTERN_ITEM_NAME = "Time format";


    public LocalDateTime parseConfiguredDateTime (final String date) {
        if (date != null) {
            return JSR310DateConverters.convertStringToLocalDateTime(date, this.getCurrentDatePatternFromDB());
        } else {
            return null;
        }
    }

    public String parseConfiguresDateTime (final LocalDateTime date) {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern(this.getCurrentDatePatternFromDB(), Locale.ENGLISH));
        } else {
            return null;
        }
    }

    public LocalDate parseConfiguredDate (final String date) {
        if (date != null) {
            return JSR310DateConverters.convertStringToLocalDate(date, this.getCurrentDatePatternFromDB());
        } else {
            return null;
        }
    }

    public String parseConfiguredDate (final LocalDate date) {
        if (date != null) {
            return date.format(DateTimeFormatter.ofPattern(this.getCurrentDatePatternFromDB(), Locale.ENGLISH));
        } else {
            return null;
        }
    }

    public LocalTime parseConfiguredTime (final String time) {
        if (time != null) {
            return JSR310DateConverters.convertStringToLocalTime(time, this.getCurrentTimePatternFromDB());
        } else {
            return null;
        }
    }

    public String parseConfiguredTime (final LocalTime time) {
        if (time != null) {
            return time.format(DateTimeFormatter.ofPattern(this.getCurrentTimePatternFromDB()));
        } else {
            return null;
        }
    }

    public LocalDateTime parseLogDate (final String date, final String logName) {
        if (date != null) {
            final String normalizedDate = JSR310DateConverters.normalizeDate(date);
            return JSR310DateConverters.convertStringToLocalDateTime(normalizedDate, this.getLogDatePatternFromDB(logName));
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "currentDatePattern")
    protected String getCurrentDatePatternFromDB() {
        final SystemConfiguration item = systemConfigurationRepository.getOneByItemName(DATE_PATTERN_ITEM_NAME);
        String currentValue = item.getCurrentValue();
        if (StringUtils.isEmpty(currentValue)) {
            currentValue = JSR310DateConverters.DEFAULT_PATTERN_DATE;
        }
        return currentValue;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "logDatePattern", key = "#logName")
    protected String getLogDatePatternFromDB(final String logName) {
        final StringBuilder itemName = new StringBuilder(logName).append(' ').append(DATE_PATTERN_ITEM_NAME);
        final SystemConfiguration item = systemConfigurationRepository.getOneByItemName(itemName.toString());
        String currentValue = item.getCurrentValue();
        if (StringUtils.isEmpty(currentValue)) {
            currentValue = JSR310DateConverters.DEFAULT_PATTERN_DATE;
        }
        return currentValue;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "currentTimePattern")
    protected String getCurrentTimePatternFromDB() {
        final SystemConfiguration item = systemConfigurationRepository.getOneByItemName(TIME_PATTERN_ITEM_NAME);
        String currentValue = item.getCurrentValue();
        if (StringUtils.isEmpty(currentValue)) {
            currentValue = JSR310DateConverters.DEFAULT_PATTERN_TIME;
        }
        return currentValue;
    }
}
