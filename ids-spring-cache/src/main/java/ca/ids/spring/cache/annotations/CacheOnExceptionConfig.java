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
@Target({ElementType.TYPE})
public @interface CacheOnExceptionConfig {

    /**
     * Returns an array of the kinds of exceptions cacheable
     * can be applied to.
     * @return an array of the kinds of exceptions cacheable
     * can be applied to.
     */
    Class[] exceptions() default {};

    /**
     * Exclude exception list from cacheable if `true`, default is `false`.
     */
    boolean exclude() default false;
}
