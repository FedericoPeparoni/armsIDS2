package ca.ids.spring.cache.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that all call results are cached. If exception
 * occurs and an identical cached call can be found, the
 * exception is squashed and the cached results are returned.
 *
 * Throws CacheableRetryException if retry true and method
 * return is not void.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface CacheableOnException {

    /**
     * An array of the kinds of exceptions cacheable can be applied to OR exceptions
     * that should be excluded if `exclude = true`.
     *
     * All exceptions will be cacheable if empty and `exclude = false`. If empty and
     * `exclude = true`, will ignore all exceptions, equivalent to off.
     */
    Class[] exceptions() default {};

    /**
     * Returns true or false if cacheable request can be retried.
     */
    boolean retry() default false;

    /**
     * Names of the caches in which method invocation results are stored.
     * <p>Names may be used to determine the target cache (or caches), matching
     * the qualifier value or bean name of a specific bean definition.
     */
    String[] value() default {};

    /**
     * Exclude exception list from cacheable if `true`, default is `false`.
     */
    boolean exclude() default false;
}
