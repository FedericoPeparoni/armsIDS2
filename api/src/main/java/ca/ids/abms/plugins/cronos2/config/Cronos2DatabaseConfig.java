package ca.ids.abms.plugins.cronos2.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@SuppressWarnings("WeakerAccess")
public class Cronos2DatabaseConfig {

    private static final String DATA_SOURCE = "cronos2DataSource";

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "cronos2NamedParameterJdbcTemplate";

    @Bean
    public DataSourceProperties cronos2DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(DATA_SOURCE)
    @ConfigurationProperties("app.cronos2")
    public DataSource cronos2DataSource() {
        return cronos2DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate cronos2NamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(cronos2DataSource());
    }
}
