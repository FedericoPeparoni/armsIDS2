package ca.ids.abms.plugins.kcaa.aatis.config;

import ca.ids.abms.plugins.kcaa.aatis.config.db.KcaaAatisDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SuppressWarnings("unused")
public class KcaaAatisDatabaseConfig {

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "kcaaAatisNamedParameterJdbcTemplate";

    public static final String TRANSACTION_MANAGER = "kcaaAatisTransactionManager";

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate kcaaAatisNamedParameterJdbcTemplate(final KcaaAatisDataSource kcaaAatisDataSource) {
        return new NamedParameterJdbcTemplate(kcaaAatisDataSource);
    }

    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager kcaaAatisTransactionManager(final KcaaAatisDataSource kcaaAatisDataSource) {
        return new DataSourceTransactionManager(kcaaAatisDataSource);
    }
}
