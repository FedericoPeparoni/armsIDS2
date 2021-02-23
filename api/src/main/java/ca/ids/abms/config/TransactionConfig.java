package ca.ids.abms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@SuppressWarnings("WeakerAccess")
public class TransactionConfig implements TransactionManagementConfigurer {

    private final PlatformTransactionManager platformTransactionManager;

    public TransactionConfig() {
        platformTransactionManager = new JpaTransactionManager();
    }

    @Bean
    @Primary
    public PlatformTransactionManager transactionManager() {
        return platformTransactionManager;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return transactionManager();
    }
}
