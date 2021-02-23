package ca.ids.abms.config;

import com.lowagie.text.FontFactory;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportEngine;
import org.eclipse.birt.report.engine.api.IReportEngineFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Configuration
public class ReportingConfiguration {
    private final Logger log = LoggerFactory.getLogger(ReportingConfiguration.class);

    @Autowired
    private ApplicationContext applicationContext;
    private EngineConfig       config;
    private IReportEngine      engine;

    @PostConstruct
    @SuppressWarnings("unchecked")
    private void init() {
        FontFactory.register("reports/fonts/Trebuchet MS.ttf");

        config = new EngineConfig();
        config.getAppContext().put("spring", this.applicationContext);
        config.setLogger(java.util.logging.Logger.getLogger(getClass().getName()));

        try {
            log.debug("Initializing BIRT runtime");
            Platform.startup(config);
        } catch (BirtException e) {
            String error = "Could not start BIRT runtime!";
            log.error(error, e);

            throw new RuntimeException(error, e);
        }
    }

    @Bean
    public IReportEngine reportEngine() {
        IReportEngineFactory factory = (IReportEngineFactory) Platform.createFactoryObject(
            IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY
        );

        engine = factory.createReportEngine(config);

        if (engine == null) {
            String error = "Could not start BIRT runtime!";
            log.error(error);

            throw new RuntimeException(error);
        }

        log.debug("Created BIRT engine");

        return engine;
    }

    @PreDestroy
    public void destroy() {
        log.debug("Shutting down BIRT engine");
        engine.destroy();

        log.debug("Shutting down BIRT runtime");
        Platform.shutdown();
    }
}
