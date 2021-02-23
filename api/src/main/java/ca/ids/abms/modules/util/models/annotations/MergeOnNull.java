package ca.ids.abms.modules.util.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Apply to class properties that require value to be merged to target object even when value is `null`.
 *
 * This is used in conjunction with `ca.ids.abms.modules.util.models.ModelUtils` for the methods
 * `merge(Object source, Object target, String... excludedFields)` and
 * `mergeOnly(Object source, Object target, String... includedFields)`. These methods only copy the
 * source property to the target property if the source property value IS NOT `null` and IS NOT annotated
 * with `@MergeOnNull`.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface MergeOnNull {

    /**
     * Optional argument that defines whether this annotation is active
     * or not. The only use for value 'false' if for overriding purposes
     * (which is not needed often); most likely it is needed for use
     * with "mix-in annotations" (aka "annotation overrides").
     * For most cases, however, default value of "true" is just fine
     * and should be omitted.
     */
    boolean value() default true;
}
