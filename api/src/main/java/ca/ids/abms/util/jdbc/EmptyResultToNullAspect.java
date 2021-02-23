package ca.ids.abms.util.jdbc;

import ca.ids.abms.util.jdbc.annotations.HandleEmptyResult;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

@Aspect
@Component
@SuppressWarnings({"squid:S00112", "unused"})
public class EmptyResultToNullAspect extends AbstractCacheInvoker {

    /**
     * Attempt to run method and catch {@link org.springframework.dao.EmptyResultDataAccessException}. If caught,
     * attempt to return {@code null}.
     *
     * @param pjp proceeding join point
     * @param annotation handle empty result annotation
     *
     * @return result of method call or null
     */
    private Object invoke(final ProceedingJoinPoint pjp, final HandleEmptyResult annotation) throws Throwable {

        Object result;
        try {

            // attempt to invoke method
            result = pjp.proceed();

        } catch (final EmptyResultDataAccessException ex) {

            // only set result to null if annotation defined
            // this should always be true if defined appropriately
            if (annotation != null) result = null;
            else throw ex;
        }

        // return results
        return result;
    }

    @Pointcut("execution(@ca.ids.abms.util.jdbc.annotations.HandleEmptyResult * *.*(..))")
    void annotatedMethod() {
        // defined named aspectj pointcut for reuse in advise methods below
    }

    @Around("annotatedMethod() && @annotation(annotation)")
    public Object adviseAnnotatedMethods(ProceedingJoinPoint pjp, HandleEmptyResult annotation) throws Throwable {
        return invoke(pjp, annotation);
    }
}
