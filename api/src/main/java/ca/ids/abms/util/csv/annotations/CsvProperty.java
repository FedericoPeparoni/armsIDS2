package ca.ids.abms.util.csv.annotations;

import java.lang.annotation.*;
import java.util.Map;

/**
 * A marker annotation used to define a property to be serialized.
 * <p>
 * By default, all class properties are automatically serialized unless
 * annotated with {@link CsvIgnore}.
 * <p>
 * {@link CsvProperty#value()} is the name of the column header. By default,
 * it will use the name of the field to which it is attached. If a custom name
 * if provided, it will be used instead.
 * <p>
 * {@link CsvProperty#nested()} is used to indicate a field that should be flattened
 * out into a row.
 *
 * @author Derek McKinnon
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvProperty {

    /**
     * The name of the column header for the field.
     * If not specified, it indicates to use the field name by default.
     * <p>
     * SpEL-like variables may be used in conjunction with a {@link Map}
     * supplied at runtime to the mapping service. Variables should be in
     * the format of {@code #{placeholder}}, where {@code placeholder} is
     * the name of the variable.
     *
     * @return The column header name.
     */
    String value() default "";

    /**
     * Indicates whether or not this property should be flattened into a row.
     */
    boolean nested() default false;

    /**
     * Indicates that a decimal value should be truncated to the provided precision.
     */
    int precision() default -1;

    /**
     * Indicates that a date/time value should follow the provided pattern.
     */
    String dateFormat() default "";

    /**
     * Indicates that a field is date and should be converted to Date format according to system configuration.
     */
    boolean date() default false;

    /**
     * Indicates that a field is date with time and should be converted to Date format according to system configuration with showing time.
     */
    boolean dateTime() default false;

    /**
     * Indicates that a field is MTOW value and should be displayed as kg/tons according to system configuration.
     */
    boolean mtow() default false;

    /**
     * Indicates that a field is distance value and should be displayed as km/nm according to system configuration.
     */
    boolean distance() default false;

    boolean latitude() default false;

    boolean longitude() default false;

    boolean inverse() default false;
}
