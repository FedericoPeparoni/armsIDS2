package ca.ids.abms.plugins.kcaa.erp.utilities.jdbc;

import ca.ids.abms.util.jdbc.AutoRowMapper;

import java.math.BigDecimal;

public class ErpRowMapper<T> extends AutoRowMapper<T> {

    /**
     * @param clazz The Class type for the generic class.
     */
    public ErpRowMapper(Class<T> clazz) {
        super(clazz);
    }

    @Override
    protected Object convertTypeIfNeeded(Object value) {
        if (value instanceof Short) {
            value = value.equals(1);
        } else if (value instanceof BigDecimal) {
            value = ((BigDecimal) value).doubleValue();
        } else {
            value = super.convertTypeIfNeeded(value);
        }
        return value;
    }
}
