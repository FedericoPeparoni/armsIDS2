package ca.ids.abms.plugins.kcaa.common.services;

import ca.ids.abms.modules.common.services.PluginJdbcUtility;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;

@SuppressWarnings("squid:S00119")
public class KcaaPluginJdbcUtility<T, ID extends Serializable> extends PluginJdbcUtility<T, ID> {

    public KcaaPluginJdbcUtility(Class<T> clazz, RowMapper<T> rowMapper) {
        super(clazz, rowMapper);
    }

    /**
     * Force all 'Double' instance params as natural BigDecimal value.
     */
    @Override
    protected MapSqlParameterSource objectParams(T object) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        for (Field field : object.getClass().getDeclaredFields()) {
            try {
                field.setAccessible(true);
                params.addValue(fieldParamName(field), fieldParamValue(field, object));
            } catch (IllegalAccessException ex) {
                // ignore fields that are inaccessible
            }
        }
        return params;
    }

    private Object fieldParamValue(Field field, Object object) throws IllegalAccessException {
        Object value = field.get(object);
        return value instanceof Double
            ? BigDecimal.valueOf((double) value)
            : value;
    }
}
