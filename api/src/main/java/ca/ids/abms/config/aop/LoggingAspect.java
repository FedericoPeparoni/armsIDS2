package ca.ids.abms.config.aop;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.config.error.CustomRuntimeException;

// Adapted from JHipster
@Aspect
public class LoggingAspect {

    private final Logger log = LoggerFactory.getLogger (LoggingAspect.class);

    @Pointcut("within(ca.ids.abms.modules..*Controller) || within(ca.ids.abms.modules..*Service) || within(ca.ids.abms.modules..*Repository)")
    public void loggingPointcut() {
    }

    @AfterThrowing(pointcut = "loggingPointcut()", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        if (!(e instanceof CustomRuntimeException)) {
            log.error("Exception in {}.{}() with cause = {} and exception = {}:{}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), (e.getCause() != null ? e.getCause() : "NULL"), e.getClass().getName(), e.getLocalizedMessage());
            if (log.isDebugEnabled()) {
                log.debug("", e);
            }
        }
    }

    @Around("loggingPointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isTraceEnabled()) {
            log.trace("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isTraceEnabled()) {
                log.trace("Exit: {}.{}() with result = {}", joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
}
