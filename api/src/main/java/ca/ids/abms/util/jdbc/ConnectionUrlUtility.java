package ca.ids.abms.util.jdbc;

import java.util.HashMap;
import java.util.Map;

public class ConnectionUrlUtility {

    private static final Map<String, String> DRIVER_CLASS_NAMES = new HashMap<>();

    private static final String JDBC_URL_PREFIX = "jdbc:";

    static {
        DRIVER_CLASS_NAMES.put("POSTGRESQL", "org.postgresql.Driver");
        DRIVER_CLASS_NAMES.put("SQLSERVER", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
    }

    /**
     * Parse connection url into driver class name from service type.
     *
     * @param connectionUrl value to parse
     * @return parsed driver class name
     */
    public static String parseDriverClassName(String connectionUrl) {
        if (connectionUrl == null || connectionUrl.isEmpty())
            return null;

        int beginIndex;
        if (connectionUrl.toLowerCase().startsWith(JDBC_URL_PREFIX))
            beginIndex = connectionUrl.indexOf(':') + 1;
        else
            beginIndex = 0;
        int endIndex = connectionUrl.indexOf(':', beginIndex);
        if (beginIndex == -1 || endIndex == -1)
            throw new IllegalArgumentException("Connection URL format is missing JDBC database type.");
        return findDriverClassName(connectionUrl.substring(beginIndex, endIndex));
    }

    /**
     * Resolve connection url if possible. Allow `jdbc:` prefix to be excluded.
     *
     * @param connectionUrl value to resolve
     * @return resolved connection url
     */
    public static String resolveConnectionUrl(String connectionUrl) {
        if (connectionUrl == null || connectionUrl.isEmpty())
            return null;

        if (connectionUrl.toLowerCase().startsWith(JDBC_URL_PREFIX))
            return connectionUrl;
        else
            return JDBC_URL_PREFIX + connectionUrl;
    }

    /**
     * Find drive class name from mapping using service name as key.
     *
     * @param serviceName key to find
     * @return driver class name of key, null if none found
     */
    private static String findDriverClassName(String serviceName) {
        return DRIVER_CLASS_NAMES.get(serviceName.toUpperCase());
    }

    private ConnectionUrlUtility() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
