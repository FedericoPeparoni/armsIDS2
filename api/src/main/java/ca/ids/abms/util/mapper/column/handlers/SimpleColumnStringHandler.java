package ca.ids.abms.util.mapper.column.handlers;

import ca.ids.abms.util.mapper.column.SimpleColumnHandler;

import javax.persistence.Column;

public class SimpleColumnStringHandler implements SimpleColumnHandler<String> {

    /**
     * Simple truncation of object field with substring from start to Column.length value.
     */
    public String handle(final Column column, final String value) {

        // trim value using column length, null is left untouched
        if (value != null)
            return trim(value, column.length());
        else
            return null;
    }

    /**
     * Get substring if value length is greater then allowed column length
     */
    private String trim(final String value, final Integer length) {

        if (value == null || length == null)
            throw new IllegalArgumentException("Arguments cannot be null.");

        // only apply substring if value length is greater then provided length
        String result;
        if (value.length() > length)
            result = value.substring(0, length);
        else
            result = value;

        return result;
    }
}
