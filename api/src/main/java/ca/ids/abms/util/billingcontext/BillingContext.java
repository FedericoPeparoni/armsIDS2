package ca.ids.abms.util.billingcontext;

import com.google.common.base.Preconditions;

import java.util.EnumMap;
import java.util.Map;

/**
 * BillingContext should be used to cache expensive methods that are performed frequently on a
 * single thread by creating ThreadLocalVariables.
 *
 * ThreadLocalVariables are created when the operation starts and removed when the operation ends.
 *
 * To use the BillingContext:
 *
 * 1. Use the billingContextUtility perform method
 * ex.

            billingContextUtility.perform(

            // Pass a list of cacheTargets. CacheTargets are used to create ThreadLocalVariables.
                Arrays.asList(
                    BillingContextKey.BILLABLE_AIRSPACE,
                    BillingContextKey.ANSP_CURRENCY
                ));

            // pass the method as a lambda
                () -> processCsvModels(csvModels, summary, fileName, header)

            );

 * 2. Short circuit the expensive method
 *ex.
            if (BillingContext.getBillableAirspacesForRouteParser() != null) {
                return BillingContext.getBillableAirspacesForRouteParser();
            }
            ...

 *
 * To create a new ThreadLocalVariable for BillingContext to use, define a new BillingContextKey.
 *
 * NOTE: ThreadLocalVariables are cleared when the lambda completes
 */
public class BillingContext {

    private static final BillingContextThreadLocal THREAD_LOCAL = new BillingContextThreadLocal();

    /**
     * Clear all entries in the BillingContext.
     */
    public static void clear() {
        Map<BillingContextKey, Object> map = THREAD_LOCAL.get();
        if (map != null) {
            map.clear();
            THREAD_LOCAL.remove();
        }
    }

    /**
     * Get the context identified by the <code>key</code> parameter.
     */
    @SuppressWarnings("unchecked")
    public static <T> T get(final BillingContextKey key) {
        Object value = getKeyValue(key);
        return value == null ? null : (T) value;
    }

    /**
     * Put a context value (the <code>value</code> parameter) as identified with
     * the <code>key</code> parameter into the current thread's context map.
     *
     * If the current thread does not have a context map it is created as a side
     * effect of this call.
     *
     * @throws IllegalArgumentException in case the "key" parameter is null
     */
    public static <T> void put(final BillingContextKey key, final T value) {
        Preconditions.checkArgument(key != null, "key cannot be null");

        Map<BillingContextKey, Object> map = THREAD_LOCAL.get();
        if (map == null) {
            map = new EnumMap<>(BillingContextKey.class);
            THREAD_LOCAL.set(map);
        }
        map.put(key, value);
    }

    /**
     * Remove the the context identified by the <code>key</code> parameter.
     */
    public static void remove(final BillingContextKey key) {
        Map<BillingContextKey, Object> map = THREAD_LOCAL.get();
        if (map != null) {
            map.remove(key);
        }
    }

    /**
     * Return a copy of the current thread's context map.
     *
     * Returned value may be null.
     */
    static Map<BillingContextKey, Object> getCopyOfContextMap() {
        Map<BillingContextKey, Object> map = THREAD_LOCAL.get();
        return map == null ? null
            : new EnumMap<>(map);
    }

    private static Object getKeyValue(final BillingContextKey key) {
        Map<BillingContextKey, Object> map = THREAD_LOCAL.get();
        if (map != null && key != null) {
            return map.get(key);
        } else {
            return null;
        }
    }

    private BillingContext() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
