package ca.ids.abms.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.*;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SecurityConfiguration;
import springfox.documentation.swagger.web.SecurityConfigurationBuilder;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.Collections;

/**
 * Configure Swagger (REST API Spec + UI/browser).
 * <p>
 * This will create the spec URL endpoint /api/spec/v2 that returns a JSON description
 * of all the endpoints, parameters & models in this project. It uses a library
 * called SpringFox; but note that we are not using the UI component that came with SpringFox,
 * but we are using a "vanilla" version of swagger UI. This is because the UI provided by
 * SpringFox doesn't quite work in our environment.
 * <p>
 * In any case:
 * <ul>
 *   <li><code>/api/spec/v2</code>: serves the API spec (provided by SpringFox configured in this class).
 *   <li><code>/api/spec/web</code>: serves <code>index.html</code> from <code>src/main/resources/api_spec_web</code>;
 *       this mapping is configured in {@link WebMvcConfig} class
 *   <li><code>/api/spec/web/swagger-ui</code>: serves CSS and JS files that make up the (3rd-party) swagger UI/browser,
 *       used by index.html; these files are provided as classpath resources of swagger-ui JAR (a maven dependency).
 *       This is also configured in {@link WebMvcConfig}
 * </ul>
 * 
 * @see <a href="https://swagger.io/docs/specification/">https://swagger.io/docs/specification/</a> - OpenAPI spec
 * @see <a href="http://springfox.github.io/springfox/">http://springfox.github.io/springfox/</a> - SpringFox
 * @see <a href="https://swagger.io/tools/swagger-ui/">https://swagger.io/tools/swagger-ui/</a> - Swagger UI
 */

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final String SECURITY_DEF_NAME = "spring_oauth";
    private static final String CLIENT_ID = "abms_web";
    private static final String CLIENT_SECRET = "abms_web";
    private static final String TOKEN_URL = "/oauth/token";

    @Value("${swagger.enabled}")
    private Boolean enabled;

    @Value("${abms.http.proxyContextPath}")
    private String proxyContextPath;

    @Value("${server.contextPath:/}")
    private String serverContextPath;
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .select()
            .apis(RequestHandlerSelectors.any())
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(Collections.singletonList(securityScheme()))
            .securityContexts(Collections.singletonList(securityContext()))
            .apiInfo(apiInfo())
            .pathMapping(makePath("/"))
            .enable(enabled);
    }

    @Bean
    public SecurityConfiguration security() {
        return SecurityConfigurationBuilder.builder()
            .clientId(CLIENT_ID)
            .clientSecret(CLIENT_SECRET)
            .useBasicAuthenticationWithAccessCodeGrant(true)
            .build();
    } 

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
        	.title("ARMS API")
            .description("IDS AirNav S.r.l. \n ARMS Service api endpoint documentation.")
            .contact(new Contact(
                "IDS AirNav",
                "https://www.idsairnav.com/",
                "customercare@idsairnav.com"))
            .version(getClass().getPackage().getImplementationVersion())
            .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
            .securityReferences(
                Collections.singletonList(new SecurityReference(SECURITY_DEF_NAME, scopes())))
            .forPaths(PathSelectors.any())
            .build();
    }

    private SecurityScheme securityScheme() {
        // consider AuthorizationCodeGrantBuilder instead but at the time of this change
        // `authorization_code` grant_type appears to be broken..
        GrantType grantType = new ResourceOwnerPasswordCredentialsGrant (makePath (TOKEN_URL));
        return new OAuthBuilder().name(SECURITY_DEF_NAME)
            .grantTypes(Collections.singletonList(grantType))
            .scopes(Arrays.asList(scopes()))
            .build();
    }

    private AuthorizationScope[] scopes() {
        return new AuthorizationScope[]{};
    }

    private String makePath (final String path) {
        if (StringUtils.isNotEmpty(proxyContextPath)) {
            return prependRoot (proxyContextPath, path);
        }
        if (StringUtils.isNotEmpty(serverContextPath)) {
            return prependRoot (serverContextPath, path);
        }
        return prependRoot ("/", path);
    }

    static String prependRoot (final String root, final String path) {
        if (root.endsWith ("/")) {
            if (path.startsWith("/")) {
                return root.replaceAll("/$", "") + path;
            }
            return root + path;
        }
        if (path.startsWith("/")) {
            return root + path;
        }
        return root + "/" + path;
    }

    @Bean
    public UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .displayRequestDuration(true)
                .validatorUrl("")
                .build();
    }
}
