package ca.ids.abms.util;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility interface for enums used as DB entity properties. Supports searching
 * via @{@link SearchableText} etc. See {@link UnspecifiedAircraftTypeStatus} for
 * an example usage.
 * <p>
 * When properties of this type are annotaed with @{@link SearchableEntity},
 * the query logic ({@link FilterSpecification}) will look for any records
 * that match either the corresponding Enum members literally (via {@link Enum#name()} or
 * via any of the strings returned by {@link #labels}, possibly translated (see {@link Translation} class).
 * <p>
 * For example,
 * <code><pre>
 * ...
 * @SearchableText
 * UnspecifiedAircraftTypeStatus status
 * ...
 * </code></pre>
 * 
 * This will match status values such as "MANUAL" (based on enum member ID), as well
 * as "Manual" (based on enum "labels"), including any translations thereof.
 */
public interface EnumWithLabels {
    
    public List <String> labels();
    
    public String toJsonValue();
    
    public static <E extends Enum<E> & EnumWithLabels> E forJsonValue (final E[] values, final String jsonValue) {
        if (jsonValue != null) {
            for (final E v: values) {
                if (StringUtils.equalsIgnoreCase (v.toJsonValue(), jsonValue)) {
                    return v;
                }
            }
        }
        return null;
    }
    
    
}
