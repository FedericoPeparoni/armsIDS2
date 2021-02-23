package ca.ids.abms;

import ca.ids.abms.config.AbmsProperties;
import ca.ids.abms.config.DefaultProfileUtil;
import ca.ids.abms.config.db.ABMSJpaRepository;

import javax.annotation.PreDestroy;

import ca.ids.abms.config.event.ApplicationStartup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(AbmsProperties.class)
@EntityScan(basePackageClasses = { Application.class, Jsr310JpaConverters.class })
@EnableJpaRepositories(repositoryBaseClass = ABMSJpaRepository.class)
@Import(ca.ids.oxr.client.OpenExchangeRatesClient.class)
@SuppressWarnings("unused")
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    // Adapted from JHipster
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        SpringApplication app = new SpringApplication(Application.class);
        DefaultProfileUtil.addDefaultProfile(app);
        final ConfigurableApplicationContext ctx = app.run(args);
        // FIXME: remove?
        ctx.getEnvironment();
        // Create an empty file after successful initialization.
        ctx.getBean(ApplicationStartup.class).createStartupTriggerFile();
    }

    @PreDestroy
    void stopped() {
        LOG.info("Stopped Application");
    }
}
