package ca.ids.abms.plugins.kcaa.erp.config;

import ca.ids.abms.plugins.kcaa.erp.config.db.KcaaErpDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SuppressWarnings("unused")
public class KcaaErpDatabaseConfig {

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "kcaaErpNamedParameterJdbcTemplate";

    public static final String TRANSACTION_MANAGER = "kcaaErpTransactionManager";

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate kcaaErpNamedParameterJdbcTemplate(final KcaaErpDataSource kcaaErpDataSource) {
        return new NamedParameterJdbcTemplate(kcaaErpDataSource);
    }

    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager kcaaErpTransactionManager(final KcaaErpDataSource kcaaErpDataSource) {
        return new DataSourceTransactionManager(kcaaErpDataSource);
    }
}
