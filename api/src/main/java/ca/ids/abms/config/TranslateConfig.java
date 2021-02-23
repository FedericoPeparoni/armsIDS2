package ca.ids.abms.config;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ca.ids.abms.modules.languages.LanguagesService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.translation.Translation;

@Configuration
public class TranslateConfig {
    private final Logger log = LoggerFactory.getLogger(Translation.class);

    @Autowired
    private SystemConfigurationService systemConfigurationService;

    @Autowired
    private LanguagesService languagesService;

    @PostConstruct
    private void init() {
        try {
            log.debug("Loading language information");
            translateManager(systemConfigurationService, languagesService);

        } catch (Exception e) {
            String error = "Error occurred while loading language information!";
            log.error(error, e);

            throw new RuntimeException(error, e);
        }
    }

    @Bean
    public Translation translateManager(SystemConfigurationService systemConfigurationService, LanguagesService languagesService) {
        Translation translation = new Translation(systemConfigurationService, languagesService);
        translation.loadLanguageFiles();

        return translation;
    }
}
