package ca.ids.spring.cache.interceptors;

import ca.ids.spring.cache.CacheableException;
import ca.ids.spring.cache.CacheableExceptionProvider;
import ca.ids.spring.cache.annotations.CacheOnExceptionConfig;
import ca.ids.spring.cache.annotations.CacheableOnException;
import ca.ids.spring.cache.exceptions.CacheRetryException;
import ca.ids.spring.cache.exceptions.CacheableRuntimeException;
import ca.ids.spring.cache.managers.CacheExceptionManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.interceptor.*;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
@SuppressWarnings("WeakerAccess")
public class CacheOnExceptionAspect extends AbstractCacheInvoker {

    private CacheManager cacheManager;
    private KeyGenerator keyGenerator;
    private CacheableExceptionProvider cacheableExceptionProvider;

    public CacheOnExceptionAspect(CacheManager cacheManager, KeyGenerator keyGenerator,
                                  CacheableExceptionProvider cacheableExceptionProvider) {
        this.cacheManager = cacheManager;
        this.keyGenerator = keyGenerator;
        this.cacheableExceptionProvider = cacheableExceptionProvider;
    }

    /**
     * Get result for key on the specified cache names and
     * invoke the error handler if an exception occurs. Return {@code null}
     * if the handler does not throw any exception, which simulates a cache
     * miss in case of error.
     *
     * Generically specify a type that return value will be cast to.
     *
     * @param caches list of cache names
     * @param key they key whose associated value is to be returned
     * @return the value to which this cache maps the specified key
     */
    @SuppressWarnings("unchecked")
    private <T> T doGet(String[] caches, Object key, Class<T> type) {
        for (String cacheName : caches) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                Object value = doGet(cache, key).get();
                if (value == null || type == null || type.isInstance(value))
                    return (T) value;
            }
        }
        throw new IllegalStateException("Could not retrieve cached value of type [" + type.getName() + "]");
    }

    /**
     * Put result for key on the specified cache names and
     * invoke the error handler if an exception occurs.
     *
     * @param caches list of cache names
     * @param key the key with which the specified value is to be associated
     * @param result the value to be associated with the specified key
     */
    private void doPut(String[] caches, Object key, Object result) {
        for (String value : caches) {
            Cache cache = cacheManager.getCache(value);
            if (cache != null)
                doPut(cache, key, result);
        }
    }

    /**
     * Generate a unique key from JoinPoint using KeyGenerate supplied.
     *
     * @param joinPoint join point to generate unique key from.
     * @return unique key object.
     */
    private Object generateKey(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        return this.keyGenerator.generate(joinPoint.getTarget(), methodSignature.getMethod(), joinPoint.getArgs());
    }

    /**
     * Attempt to run method and cache results if successful. If fail, attempt to squash exception with cached results
     * if available for unique method call.
     *
     * @param pjp proceeding join point
     * @param cacheableOnException cacheable on exception annotation
     * @param clazz class containing any config annotations
     * @return result of method call
     * @throws Throwable throws exception if cache resolving not successful
     */
    private Object invoke(ProceedingJoinPoint pjp, CacheableOnException cacheableOnException, Class<?> clazz) throws Throwable {

        // set default values required for invocation
        Class[] exceptions = cacheableOnException.exceptions();
        boolean retry = cacheableOnException.retry();
        String[] values = cacheableOnException.value();
        boolean exclude = cacheableOnException.exclude();

        // attempt to resolve exceptions and exclude from class annotations
        // exceptions are required either from @CacheableOnException or @CacheOnExceptionConfig
        if (exceptions.length == 0) {
            CacheOnExceptionConfig cacheOnExceptionConfig = clazz.getAnnotation(CacheOnExceptionConfig.class);
            if (cacheOnExceptionConfig != null && cacheOnExceptionConfig.exceptions().length > 0) {
                exceptions = cacheOnExceptionConfig.exceptions();
                exclude = cacheOnExceptionConfig.exclude();
            }
        }

        // attempt to resolve cache values from class annotations
        if (values.length == 0) {
            CacheConfig cacheConfig = clazz.getAnnotation(CacheConfig.class);
            if (cacheConfig != null && cacheConfig.cacheNames().length > 0)
                values = cacheConfig.cacheNames();
        }

        // invoke process
        return invoke(pjp, exceptions, retry, values, exclude);
    }

    /**
     * Attempt to run method and cache results if successful. If fail, attempt to squash exception with cached results
     * if available for unique method call.
     *
     * @param pjp proceeding join point
     * @param exceptions exceptions to catch for catching
     * @param retry attempt to retry next cycle instead of using cache
     * @param caches cache stores to use
     * @param exclude exclude exceptions list (inverse)
     * @return result of method call
     * @throws Throwable throws exception if cache resolving not successful
     */
    @SuppressWarnings("unchecked")
    private Object invoke(ProceedingJoinPoint pjp, Class[] exceptions, boolean retry, String[] caches, boolean exclude) throws Throwable {
        Object result;
        try {

            // attempt to invoke method
            result = pjp.proceed();

            // add result to cache
            this.doPut(caches, this.generateKey(pjp), result);

        } catch (Throwable ex) {

            // if CacheableRuntimeException, pull out inner exception values
            Throwable cause = ex;
            Object[] metadata = null;
            if (ex instanceof CacheableRuntimeException && ex.getCause() != null) {
                CacheableRuntimeException _ex = (CacheableRuntimeException) ex;
                cause = _ex.getCause();
                metadata = _ex.getMetadata();
            }

            // skip if stack trace contains CacheExceptionManager, retry cycle invoker
            String managerClassName = CacheExceptionManager.class.getCanonicalName();
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                if (stackTraceElement.getClassName().equals(managerClassName))
                    throw cause;
            }

            // save throwable cause to the cacheable exception provider if retry
            // or attempt to return cached value if exists
            return this.processCacheableException(pjp, exceptions, retry, caches, exclude, cause, metadata);
        }

        // return results
        return result;
    }

    /**
     * If retry true, attempt to save original invoked method call to cacheableExceptionProvider. Else,
     * attempt to resolve return value from supplied caches.
     *
     * @param pjp proceeding join point
     * @param exceptions exceptions to catch for catching
     * @param retry attempt to retry next cycle instead of using cache
     * @param caches cache stores to use
     * @param exclude exclude exceptions list (inverse)
     * @param throwable throwable causes of catch
     * @param metadata cacheable metadata if any
     * @return result of method call
     * @throws Throwable throws exception if cache resolving not successful
     */
    @SuppressWarnings({"squid:S00112", "unchecked"})
    private Object processCacheableException(ProceedingJoinPoint pjp, Class[] exceptions, boolean retry, String[] caches,
                                            boolean exclude, Throwable throwable, Object[] metadata) throws Throwable {

        // loop threw all causes and compare against list of exceptions
        Throwable cause = null;
        do {
            cause = cause == null ? throwable : cause.getCause();

            // only handle supplied exceptions
            if (cacheableExceptionProvider.handleException(exceptions, cause, exclude)) {

                // method return type
                MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
                Class methodReturnType = methodSignature != null ? methodSignature.getReturnType() : void.class;

                // cache until next retry cycle
                if (retry) {

                    // retrieve method from signature if exists
                    Method method = methodSignature != null ? methodSignature.getMethod() : null;

                    // return if no method found, nothing to do
                    if (method == null)
                        return null;

                    // save cacheable exception to provider to be called on next retry cycle
                    cacheableExceptionProvider.create(new CacheableException(pjp.getTarget().getClass(), method.getName(),
                        method.getParameterTypes(), pjp.getArgs(), exceptions, caches, exclude, metadata, cause));

                    // handle return type
                    if (methodReturnType == void.class)
                        return null;
                    else
                        throw new CacheRetryException(cause);
                }

                try {

                    // get cached value if exists
                    return doGet(caches, this.generateKey(pjp), methodReturnType);

                } catch (IllegalStateException ignored) {

                    // throw original exception instead of illegal state
                    // equivalent to no cache existing at all
                    throw cause;
                }

            }
        } while (cause.getCause() != null && cause.getCause() != cause);

        // if no throwable cause matches list of exceptions
        // throw original cause as if unhandled by this interceptor
        throw throwable;
    }

    @Pointcut("execution(@ca.ids.spring.cache.annotations.CacheableOnException * *.*(..))")
    void annotatedMethod() {
        // defined named aspectj pointcut for reuse in advise methods below
    }

    @Pointcut("execution(* (@ca.ids.spring.cache.annotations.CacheableOnException *).*(..))")
    void methodOfAnnotatedClass() {
        // defined named aspectj pointcut for reuse in advise methods below
    }

    @Around("annotatedMethod() && @annotation(annotation)")
    public Object adviseAnnotatedMethods(ProceedingJoinPoint pjp, CacheableOnException annotation) throws Throwable {
        return invoke(pjp, annotation, pjp.getTarget().getClass());
    }

    @Around("methodOfAnnotatedClass() && !annotatedMethod() && @target(annotation)")
    public Object adviseMethodsOfAnnotatedClass(ProceedingJoinPoint pjp, CacheableOnException annotation)
        throws Throwable {
        return invoke(pjp, annotation, pjp.getThis().getClass());
    }
}
