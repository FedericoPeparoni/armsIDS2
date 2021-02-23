package ca.ids.abms.plugins.caab.sage.config;

import ca.ids.abms.plugins.caab.sage.config.db.CaabSageDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@SuppressWarnings("unused")
public class CaabSageDatabaseConfig {

    public static final String NAMED_PARAMETER_JDBC_TEMPLATE = "caabSageNamedParameterJdbcTemplate";

    public static final String TRANSACTION_MANAGER = "caabSageTransactionManager";

    @Bean(NAMED_PARAMETER_JDBC_TEMPLATE)
    public NamedParameterJdbcTemplate caabSageNamedParameterJdbcTemplate(final CaabSageDataSource caabSageDataSource) {
        return new NamedParameterJdbcTemplate(caabSageDataSource);
    }

    @Bean(TRANSACTION_MANAGER)
    public PlatformTransactionManager caabSageTransactionManager(final CaabSageDataSource caabSageDataSource) {
        return new DataSourceTransactionManager(caabSageDataSource);
    }
}
