package ca.ids.abms.util.jdbc;

import org.springframework.beans.FatalBeanException;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.validation.BindException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class MutableDataSource extends DelegatingDataSource implements EnvironmentAware {

    private static final String DEFAULT_DRIVER_CLASS_NAME = "org.postgresql.Driver";

    private static final String DEFAULT_URL = "jdbc:postgresql://localhost/postgres";

    private final DataSourceProperties dataSourceProperties = new DataSourceProperties();

    private String configurationPropertiesPrefix;

    private ConfigurableEnvironment environment;

    /**
     * Construct a mutable data source with application configuration
     * properties.
     *
     * NOTE: url, username (optional), and password (optional) must
     * be set within override methods and NOT application configuration properties.
     *
     * @param configurationPropertiesPrefix application properties prefix
     */
    public MutableDataSource(String configurationPropertiesPrefix) {
        super();
        this.configurationPropertiesPrefix = configurationPropertiesPrefix;
    }

    @Override
    public void afterPropertiesSet() {
        // set initial target data source after initialization
        // DelegatingDataSource requires a target data source
        setTargetDataSource(getUrl(), getUsername(), getPassword());
        super.afterPropertiesSet();
    }

    /**
     * Should only be called within a connection pool manager.
     *
     * @return mew sql connection
     * @throws SQLException thrown if sql connection could NOT be obtained
     */
    @Override
    public Connection getConnection() throws SQLException {
        validateTargetDataSource();
        return super.getConnection();
    }

    /**
     * Should only be called within a connection pool manager.
     *
     * @param username username for connection
     * @param password password for connection
     * @return mew sql connection
     * @throws SQLException thrown if sql connection could NOT be obtained
     */
    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        validateTargetDataSource();
        return super.getConnection(username, password);
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }

    /**
     * DataSource password used to get connection. Default is `null`.
     *
     * @return data source password
     */
    protected String getPassword() {
        return null;
    }

    /**
     * DataSource url used to get connection. Default is `null`.
     *
     * @return data source url
     */
    protected String getUrl() {
        return null;
    }

    /**
     * DataSource username used to get connection. Default is `null`.
     *
     * @return data source username
     */
    protected String getUsername() {
        return null;
    }

    private Boolean isDataSourcePropertiesValid(String url, String username, String password) {
        return isUrlValid(url) && isUsernameValid(username) && isPasswordValid(password);
    }

    private Boolean isPasswordValid(String password) {
        return (dataSourceProperties.getPassword() == null && password == null)
            || (dataSourceProperties.getPassword() != null && dataSourceProperties.getPassword().equals(password));
    }

    private Boolean isUrlValid(String url) {
        return (dataSourceProperties.getUrl() == null && url == null)
            || (dataSourceProperties.getUrl() != null && dataSourceProperties.getUrl().equals(url));
    }

    private Boolean isUsernameValid(String username) {
        return (dataSourceProperties.getUsername() == null && username == null)
            || (dataSourceProperties.getUsername() != null && dataSourceProperties.getUsername().equals(username));
    }

    private void populateConfigurationProperties(final DataSource dataSource) {
        if (configurationPropertiesPrefix == null || environment == null)
            return;

        PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<>(dataSource);
        factory.setTargetName(configurationPropertiesPrefix);
        factory.setPropertySources(environment.getPropertySources());
        factory.setConversionService(environment.getConversionService());

        try {
            factory.bindPropertiesToTarget();
        } catch (BindException ex) {
            throw new FatalBeanException("Could not bind DataSource properties", ex);
        }
    }

    private void setTargetDataSource(String url, String username, String password) {

        String driverClassName = ConnectionUrlUtility.parseDriverClassName(url);
        if (driverClassName == null)
            driverClassName = DEFAULT_DRIVER_CLASS_NAME;

        if (url == null || url.isEmpty())
            url = DEFAULT_URL;

        dataSourceProperties.setDriverClassName(driverClassName);
        dataSourceProperties.setUrl(ConnectionUrlUtility.resolveConnectionUrl(url));
        dataSourceProperties.setUsername(username);
        dataSourceProperties.setPassword(password);

        DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().build();
        populateConfigurationProperties(dataSource);

        super.setTargetDataSource(dataSource);
    }

    private void validateTargetDataSource() {
        String url = getUrl();
        String username = getUsername();
        String password = getPassword();

        if (!isDataSourcePropertiesValid(url, username, password))
            setTargetDataSource(url, username, password);
    }
}
