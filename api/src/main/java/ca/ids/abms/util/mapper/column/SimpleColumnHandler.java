package ca.ids.abms.util.mapper.column;

import javax.persistence.Column;

/**
 * Simple `javax.persistence.Column` field validation handler.
 *
 * @param <T> target class to apply
 */
public interface SimpleColumnHandler<T> {

    /**
     * Handle specific object field's type 'T' and return validated result.
     *
     * @param column column annotation of object field
     * @param value field value being handled
     * @return valid field result
     */
    T handle(final Column column, final T value);
}
