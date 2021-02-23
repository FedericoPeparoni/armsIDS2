package ca.ids.abms.config;

import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

public class SwaggerConfigTest {

    @Test
    public void testPrependRoot() {
        assertThat (SwaggerConfig.prependRoot ("/root", "path")).isEqualTo("/root/path");
        assertThat (SwaggerConfig.prependRoot ("/root/", "path")).isEqualTo("/root/path");
        assertThat (SwaggerConfig.prependRoot ("/root", "/path")).isEqualTo("/root/path");
        assertThat (SwaggerConfig.prependRoot ("/root/", "/path")).isEqualTo("/root/path");
        assertThat (SwaggerConfig.prependRoot ("/root", "path/")).isEqualTo("/root/path/");
    }

}


