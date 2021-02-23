package ca.ids.abms.util.csv.annotations;

import java.lang.annotation.*;

/**
 * An annotation used to indicate the order of serialized CSV properties.
 * <p>
 * If {@link CsvPropertyOrder#value()} is empty, no specific ordering will be applied.
 * <p>
 * If {@link CsvPropertyOrder#value()} contains items, CSV properties will be ordered in that way.
 * <p>
 * If {@link CsvPropertyOrder#value()} is missing some properties that are present in the class,
 * it will order the specified properties and then output the unspecified ones in no specific order.
 * <p>
 * If {@link CsvPropertyOrder#alphabetical()} is set to true, it will take precedence over
 * {@link CsvPropertyOrder#value()} and list properties alphabetically in ascending order
 *
 * @author Derek McKinnon
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CsvPropertyOrder {

    /**
     * An array of property names in desired order of output.
     */
    String[] value() default {};

    /**
     * A value indicating whether or not to sort properties in ascending alphabetical order.
     */
    boolean alphabetical() default false;
}
