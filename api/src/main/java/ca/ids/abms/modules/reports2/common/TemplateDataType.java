package ca.ids.abms.modules.reports2.common;

import org.eclipse.birt.core.data.DataType;
import org.eclipse.birt.core.data.DataTypeUtil;
import org.eclipse.birt.core.exception.BirtException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum TemplateDataType {

    STRING      (1, DataType.STRING_TYPE),
    FLOAT       (2, DataType.DOUBLE_TYPE),
    DECIMAL     (3, DataType.DECIMAL_TYPE),
    DATE_TIME   (4, DataType.DATE_TYPE),
    BOOLEAN     (5, DataType.BOOLEAN_TYPE),
    INTEGER     (6, DataType.INTEGER_TYPE),
    DATE        (7, DataType.SQL_DATE_TYPE),
    TIME        (8, DataType.SQL_TIME_TYPE),
    UNKNOWN     (0, DataType.UNKNOWN_TYPE);

    private final int sourceDataType;
    private final int birtDataType;
    private final static Map<Integer, TemplateDataType> dataTypesConversion = new HashMap<>(10);

    static {
        dataTypesConversion.put(STRING.sourceDataType, STRING);
        dataTypesConversion.put(FLOAT.sourceDataType, FLOAT);
        dataTypesConversion.put(DECIMAL.sourceDataType, DECIMAL);
        dataTypesConversion.put(DATE_TIME.sourceDataType, DATE_TIME);
        dataTypesConversion.put(BOOLEAN.sourceDataType, BOOLEAN);
        dataTypesConversion.put(INTEGER.sourceDataType, INTEGER);
        dataTypesConversion.put(DATE.sourceDataType, DATE);
        dataTypesConversion.put(TIME.sourceDataType, TIME);
        dataTypesConversion.put(UNKNOWN.sourceDataType, UNKNOWN);
    }

    TemplateDataType(int sourceDataType, int birtDataType) {
        this.sourceDataType = sourceDataType;
        this.birtDataType = birtDataType;
    }

    public int getSourceDataType () {
        return this.sourceDataType;
    }

    public int getBirtDataType () {
        return this.birtDataType;
    }

    public static TemplateDataType getTemplateDataType(int sourceDataType) {
        TemplateDataType dataType = dataTypesConversion.get(sourceDataType);
        if (dataType == null) {
            dataType = UNKNOWN;
        }
        return dataType;
    }

    public static Object convertValue (Object item, int dataType) throws BirtException {
        assert (item != null);
        final TemplateDataType tDataType = TemplateDataType.getTemplateDataType(dataType);
        final Object value = tDataType.parseValue(item);
        return DataTypeUtil.convert(value, tDataType.getBirtDataType());
    }

    public static Object[] convertValue (Object[] items, int dataType) throws BirtException {
        assert (items != null);
        Object[] result;
        final TemplateDataType tDataType = TemplateDataType.getTemplateDataType(dataType);
        result = tDataType.createArray(items);
        for (int i=0; i<items.length; i++) {
            final Object value = tDataType.parseValue(items[i]);
            result[i] = DataTypeUtil.convert(value, tDataType.getBirtDataType());
        }
        return result;
    }

    private static final DateTimeFormatter SRC_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    private static final DateTimeFormatter DEST_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private String convertDateTimeToDate (String sourceDateTime) {
        final LocalDateTime dateTime = LocalDateTime.parse(sourceDateTime, SRC_DATE_TIME_FORMATTER);
        return dateTime.format(DEST_DATE_FORMATTER);
    }

    private Object parseValue(final Object value) {
        switch (this) {
            case DATE: {
                    if (value instanceof String) {
                        return convertDateTimeToDate((String) value);
                    } else {
                        return value;
                    }
            }
            default: {
                return value;
            }
        }
    }

    private Object[] createArray (final Object[] items) {
        switch (this) {
            case FLOAT: {
                return new Double[items.length];
            }
            case DECIMAL: {
                return new BigDecimal[items.length];
            }
            case DATE_TIME: {
                return new Date[items.length];
            }
            case BOOLEAN: {
                return new Boolean[items.length];
            }
            case INTEGER: {
                return new Integer[items.length];
            }
            case DATE: {
                return new java.sql.Date[items.length];
            }
            case TIME: {
                return new java.sql.Time[items.length];
            }
            default: {
                return new String[items.length];
            }
        }
    }
}
