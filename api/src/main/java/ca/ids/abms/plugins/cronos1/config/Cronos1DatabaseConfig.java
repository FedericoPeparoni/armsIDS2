package ca.ids.abms.plugins.cronos1.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
@SuppressWarnings("WeakerAccess")
public class Cronos1DatabaseConfig {

    private static final String DATA_SOURCE = "cronos1DataSource";

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "cronos1NamedParameterJdbcTemplate";

    @Bean
    public DataSourceProperties cronos1DataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(DATA_SOURCE)
    @ConfigurationProperties("app.spatiadb")
    public DataSource cronos1DataSource() {
        return cronos1DataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate cronos1NamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(cronos1DataSource());
    }
}
