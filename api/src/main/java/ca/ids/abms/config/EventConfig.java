package ca.ids.abms.config;

import ca.ids.abms.config.event.ApplicationShutdown;
import ca.ids.abms.config.event.ApplicationStartup;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@SuppressWarnings("unused")
public class EventConfig {

    @Bean
    public ApplicationStartup applicationStartup() {
        return new ApplicationStartup();
    }

    @Bean
    public ApplicationShutdown applicationShutdown() {
        return new ApplicationShutdown();
    }
}
