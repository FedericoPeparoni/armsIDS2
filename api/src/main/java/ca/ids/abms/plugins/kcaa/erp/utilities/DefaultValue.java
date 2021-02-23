package ca.ids.abms.plugins.kcaa.erp.utilities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DefaultValue {

    public static final Boolean BOOLEAN = false;

    public static final Double DOUBLE = 0d;

    public static final Integer INTEGER = 0;

    public static final LocalDateTime LOCAL_DATE_TIME = LocalDateTime.parse("1753-01-01T00:00:00", DateTimeFormatter.ISO_DATE_TIME);

    public static final String STRING = "";

    private DefaultValue() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
