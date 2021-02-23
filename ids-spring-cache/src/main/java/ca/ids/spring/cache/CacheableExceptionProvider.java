package ca.ids.spring.cache;

import java.util.Arrays;
import java.util.List;

public interface CacheableExceptionProvider {

    /**
     * Used to find all cached exceptions to retry.
     *
     * @return list of cached exceptions to retry.
     */
    List<CacheableException> findAll();

    /**
     * Create cacheable exception to be attempted on next retry cycle.
     *
     * @param cacheableException cacheable exception to retry.
     */
    void create(CacheableException cacheableException);

    /**
     * Update cacheable exception to be attempted again on next retry cycle.
     *
     * @param cacheableException cacheable exception to retry.
     */
    void update(CacheableException cacheableException);

    /**
     * Remove cacheable exception for next retry cycle.
     *
     * @param cacheableException cacheable exception to remove.
     */
    void remove(CacheableException cacheableException);

    /**
     * Handle if cause exists in list of exceptions or list is empty.
     *
     * @throws IllegalArgumentException thrown if `cause` is null
     * @param exceptions exceptions to catch
     * @param cause throwable causes of catch (IllegalArgumentException if null)
     * @param exclude exclude exceptions list (inverse)
     * @return true if cause should be handled
     */
    default boolean handleException(final Class[] exceptions, final Throwable cause, final boolean exclude) {
        if (cause == null) throw new IllegalArgumentException("Cause cannot be null");

        boolean result = exclude;
        if (exceptions == null || exceptions.length == 0 || Arrays.asList(exceptions).contains(cause.getClass()))
            result = !exclude;

        return result;
    }
}
