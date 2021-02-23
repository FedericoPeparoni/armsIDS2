package ca.ids.abms.plugins.common.utilities;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import ca.ids.spring.cache.exceptions.CacheableRuntimeException;
import org.slf4j.Logger;

import java.util.Arrays;

public class PluginExceptionHelper {

    /**
     * Handle exception with plugin sql statements. Return as throwable
     * CacheableRuntimeException. This should only be used inside a `@CacheableOnException`
     * annotated method or sub-method.
     *
     * @param exception exception that was caught and should be handled
     * @param pluginSqlStatements sql statements used to export the object
     * @return throwable cacheable runtime exception
     */
    public static CacheableRuntimeException handleExportException(
        final Exception exception, final PluginSqlStatement[] pluginSqlStatements
    ) {
        // return a cacheable runtime exception that can be thrown with sql statements and main cause
        return new CacheableRuntimeException(PluginSqlStatement.sqlToMetadata(pluginSqlStatements),
            ExceptionFactory.resolveMainCause(exception));
    }

    /**
     * Log appropriate messages for exception returned when attempting to export objects to an external system.
     *
     * @param logger logger to use for printing warn, debug, and trace messages
     * @param exception exception that was caught when exporting
     * @param objects object(s) that could not be exported
     */
    public static void logExportException(final Logger logger, final Exception exception, final Object... objects) {
        // log messages to the appropriate logger level using supplied logger
        objectsWarnLogger(logger, objects);
        causeDebugLogger(logger, exception);
        exceptionTraceLogger(logger, exception);
    }

    /**
     * Log trace message about suppressed exception.
     */
    private static void exceptionTraceLogger(final Logger logger, final Exception exception) {
        logger.trace("The following RuntimeException was suppressed.", exception);
    }

    /**
     * Log debug message about cause and resolution of suppressed exception.
     */
    private static void causeDebugLogger(final Logger logger, final Exception exception) {
        logger.debug("The system will attempt to export again in the next retry cycle because of '{}' : {}",
            exception.getClass().getSimpleName(), exception.getMessage());
    }

    /**
     * Log warn message about object(s) that could not be exported into the
     * external system.
     */
    private static void objectsWarnLogger(final Logger logger, final Object[] objects) {
        if (objects == null || objects.length <= 0)
            logger.warn("Could not export into the external system.");
        else if (objects.length == 1)
            logger.warn("Could not export the following object into the external system: {}", objects[0]);
        else
            logger.warn("Could not export the following objects into the external system: {}",
                Arrays.stream(objects).map(Object::toString));
    }

    private PluginExceptionHelper() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
