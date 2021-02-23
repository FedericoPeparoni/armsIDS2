package ca.ids.abms.util.mapper.column;

import ca.ids.abms.util.mapper.column.handlers.SimpleColumnDoubleHandler;
import ca.ids.abms.util.mapper.column.handlers.SimpleColumnIntegerHandler;
import ca.ids.abms.util.mapper.column.handlers.SimpleColumnStringHandler;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Column;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple `javax.persistence.Column` field validation mapper.
 *
 * @param <T> target class to apply
 */
public abstract class SimpleColumnMapper<T> {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleColumnMapper.class);

    private static final Map<Class, SimpleColumnHandler> dispatch = registerHandlers();

    /**
     * Post mapping for Column annotation handle. Simply truncates or rounds
     * values based on type and Column annotation values.
     */
    @AfterMapping()
    protected void columnMapping(@MappingTarget final T target) {
        if (target == null) return;

        // loop through each declared field with `Column` annotation and handle validation
        Arrays.stream(target.getClass().getDeclaredFields())
            .filter(f -> f.isAnnotationPresent(Column.class))
            .forEach(f -> handle(f, target));
    }

    /**
     * Registers all simple column handlers based on class types. Should only be
     * called when initializing dispatch map.
     *
     * @return map of registered simple column handlers
     */
    private static Map<Class, SimpleColumnHandler> registerHandlers() {
        Map<Class, SimpleColumnHandler> map = new HashMap<>();
        map.put(Double.class, new SimpleColumnDoubleHandler());
        map.put(Integer.class, new SimpleColumnIntegerHandler());
        map.put(String.class, new SimpleColumnStringHandler());
        return map;
    }

    /**
     * Handle field value of object depending on type definition.
     */
    private void handle(final Field field, final T object) {
        try {

            // silently skip if field or object is null
            if (field == null || object == null)
                return;

            // suppress access checking for reflection
            field.setAccessible(true);

            // obtain necessary field properties required for validation
            Column column = field.getAnnotation(Column.class);
            Object value = field.get(object);

            // set field value from handler using field column and value
            SimpleColumnHandler handler = dispatch.get(field.getType());
            if (column != null && handler != null)
                field.set(object, handler.handle(column, value));

        } catch (IllegalAccessException ex) {

            // ignore access control exceptions, nothing we can do
            LOG.error("Cannot access value for field '{}' in object '{}'.",
                field, object);
        }
    }
}
