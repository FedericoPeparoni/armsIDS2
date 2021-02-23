package ca.ids.abms.util.jdbc;

import org.springframework.jdbc.core.RowMapper;

import ca.ids.abms.util.ReflectionUtils;
import ca.ids.abms.util.StringUtils;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.sql.*;

/**
 * Generic {@link RowMapper} that auto-magically maps ResultSet rows and values
 * to fields inside the target class type.
 *
 * @param <T> The target class in which to inject values into.
 *            This class must have a default no-argument constructor.
 */
public class AutoRowMapper<T> implements RowMapper<T> {

    private final Class<T> clazz;

    /**
     * @param clazz The Class type for the generic class.
     */
    public AutoRowMapper(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        T instance = ReflectionUtils.newInstance(clazz).orElseThrow(SQLException::new);

        ResultSetMetaData metaData = rs.getMetaData();

        // Column counting is 1-indexed
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            Object value = rs.getObject(i);
            if (rs.wasNull() || value == null) {
                continue;
            }

            mapColumn(columnName, value, instance);
        }

        return instance;
    }

    private void mapColumn(String columnName, Object value, T instance) {

        // attempt to get column name from `@Column.name` annotation if exists
        String fieldName = null;
        for(Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Column.class)
                && field.getAnnotation(Column.class) != null
                && !field.getAnnotation(Column.class).name().isEmpty()
                && field.getAnnotation(Column.class).name().equals(columnName)) {
                fieldName = field.getName();
                break;
            }
        }

        // default to camel case from snake case
        if (fieldName == null)
            fieldName = StringUtils.snakeCaseToCamelCase(columnName);

        value = convertTypeIfNeeded(value);

        ReflectionUtils.setFieldValue(instance, fieldName, value);
    }

    protected Object convertTypeIfNeeded(Object value) {
        if (value instanceof Timestamp) {
            value = ((Timestamp) value).toLocalDateTime();
        } else if (value instanceof Time) {
            value = ((Time) value).toLocalTime();
        } else if (value instanceof Date) {
            value = ((Date) value).toLocalDate();
        }

        return value;
    }
}
