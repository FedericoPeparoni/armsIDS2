package ca.ids.abms;

import ca.ids.abms.config.AbmsProperties;
import org.junit.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AbmsProperties.class)
public class ApplicationTests {

    @Test
    public void contextLoads() {
    }

}
