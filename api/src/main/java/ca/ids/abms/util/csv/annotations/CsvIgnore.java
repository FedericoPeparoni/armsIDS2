package ca.ids.abms.util.csv.annotations;

import java.lang.annotation.*;

/**
 * Marker annotation that indicates that the annotated field
 * is not to be serialized into CSV.
 *
 * @author Derek McKinnon
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvIgnore {

    /**
     * Optional argument that defines whether this annotation is active or not.
     */
    boolean value() default true;
}
