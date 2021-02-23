package ca.ids.abms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;

// Adapted from JHipster
public final class DefaultProfileUtil {

    private static final Logger LOG = LoggerFactory.getLogger (DefaultProfileUtil.class);

    private static final String PROP_LOGGING_CONFIG = "logging.config";
    private static final String PROP_BANNER_LOCATION = "banner.location";
    private static final String PROP_SPRING_CONFIG_LOCATION = "spring.config.location";
    private static final String PROP_DERBY_ERROR_FIELD = "derby.stream.error.field";
    private static final String DERBY_ERROR_STREAM = "java.lang.System.err";

    /**
     * Run this static method within the application's main method. Used to configure
     * default environment and system properties.
     *
     * @param app spring application being initialized
     */
    public static void addDefaultProfile(SpringApplication app) {

        // set default environment properties
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put(PROP_SPRING_CONFIG_LOCATION, Constants.SPRING_CONFIG_LOCATION);
        defProperties.put(PROP_LOGGING_CONFIG, Constants.LOGBACK_CONFIG_FILE);
        defProperties.put(PROP_BANNER_LOCATION, Constants.LOGBACK_BANNER_FILE);
        app.setDefaultProperties(defProperties);

        // set default system properties
        app.addInitializers(DefaultProfileUtil::configureApplicationContext);
    }

    public static String[] getActiveProfiles(Environment env) {
        String[] profiles = env.getActiveProfiles();
        return profiles.length == 0 ? env.getDefaultProfiles() : profiles;
    }

    @SuppressWarnings("unused")
    private static void configureApplicationContext(ConfigurableApplicationContext configurableApplicationContext) {

        // point derby log to STDERR
        setSystemProperty(PROP_DERBY_ERROR_FIELD, DERBY_ERROR_STREAM);

        // explicitly set spring security strategy to thread local to prevent unsafe
        // security context threading issue when reusing threads (see US93009)
        setSystemProperty(SecurityContextHolder.SYSTEM_PROPERTY, SecurityContextHolder.MODE_THREADLOCAL);
    }

    private static void setSystemProperty(final String key, final String value) {
        System.setProperty(key, value);
        LOG.trace("Set system property '{}' to {}.", key, value);
    }

    private DefaultProfileUtil() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
