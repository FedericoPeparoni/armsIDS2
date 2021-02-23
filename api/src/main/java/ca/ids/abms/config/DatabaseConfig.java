package ca.ids.abms.config;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.jpa.vendor.HibernateJpaSessionFactoryBean;

import javax.sql.DataSource;

@Configuration
@SuppressWarnings({"WeakerAccess", "unused"})
public class DatabaseConfig {

    @Bean
    public HibernateJpaSessionFactoryBean sessionFactory() {
        return new HibernateJpaSessionFactoryBean();
    }

    @Bean
    @Primary
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean
    @Primary
    public NamedParameterJdbcTemplate primaryNamedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public DataSourceProperties navDBDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean("navDBDataSource")
    @ConfigurationProperties("app.navdb")
    public DataSource navDBDataSource() {
        return navDBDataSourceProperties().initializeDataSourceBuilder().build();
    }

    @Bean("navDBJdbcTemplate")
    public JdbcTemplate navDBJdbcTemplate() {
        return new JdbcTemplate(navDBDataSource());
    }
}
