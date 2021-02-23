package ca.ids.abms.modules.system.validation;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.util.jdbc.ConnectionUrlUtility;

@Component
public class ConnectionUrlValidator {

    /**
     * Validate connection url by attempting to parse and create a data source. A connection
     * is also attempted and returns true only if sucessful. Exceptions thrown if not.
     *
     * @param connectionUrl connection url to validate
     * @return true if successfully opened a connection
     */
    public Boolean isConnectionUrlValid(String connectionUrl) {
        Connection connection = null;
        try {
            connection = parseDataSource(connectionUrl).getConnection();
            return true;
        } catch (Exception ex) {
            throw new CustomParametrizedException(new ErrorDTO.Builder(ex.getMessage()).build());
        } finally {
            if (connection != null) try { connection.close(); } catch (SQLException ignored) { /* ignore exception */ }
        }
    }

    /**
     * Parse connection url as a data source.
     *
     * @param connectionUrl value to parse
     * @return data source from connection url
     */
    private DataSource parseDataSource(String connectionUrl) {
        if (connectionUrl == null || connectionUrl.isEmpty())
            throw new IllegalArgumentException("Connection URL must not be empty.");

        DataSourceProperties dataSourceProperties = new DataSourceProperties();
        dataSourceProperties.setDriverClassName(ConnectionUrlUtility.parseDriverClassName(connectionUrl));
        dataSourceProperties.setUrl(ConnectionUrlUtility.resolveConnectionUrl(connectionUrl));
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

}
