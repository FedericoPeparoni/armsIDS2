package ca.ids.abms.config;

// Adapted from JHipster
public final class Constants {

    public static final String LOGBACK_CONFIG_FILE = "classpath:config/logback/logback-spring.xml";
    public static final String LOGBACK_BANNER_FILE = "classpath:config/logback/banner.txt";
    public static final String SPRING_CONFIG_LOCATION = "classpath:config/overrides/,classpath:config/overrides/logging.properties";

    // private ctor to prevent instantiation
    private Constants() {
    }
}
