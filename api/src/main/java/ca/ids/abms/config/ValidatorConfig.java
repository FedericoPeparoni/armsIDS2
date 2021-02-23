package ca.ids.abms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.Validator;

@Configuration
@SuppressWarnings("unused")
public class ValidatorConfig {

    @Bean
    public Validator validatorFactory () {
        return new LocalValidatorFactoryBean();
    }
}
