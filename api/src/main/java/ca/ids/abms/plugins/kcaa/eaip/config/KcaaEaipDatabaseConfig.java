package ca.ids.abms.plugins.kcaa.eaip.config;

import ca.ids.abms.plugins.kcaa.eaip.config.db.KcaaEaipDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SuppressWarnings("unused")
public class KcaaEaipDatabaseConfig {

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "kcaaEaipNamedParameterJdbcTemplate";

    public static final String TRANSACTION_MANAGER = "kcaaEaipTransactionManager";

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate kcaaEaipNamedParameterJdbcTemplate(final KcaaEaipDataSource kcaaEaipDataSource) {
        return new NamedParameterJdbcTemplate(kcaaEaipDataSource);
    }

    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager kcaaEaipTransactionManager(final KcaaEaipDataSource kcaaEaipDataSource) {
        return new DataSourceTransactionManager(kcaaEaipDataSource);
    }
}
