package ca.ids.abms.config;

import java.util.List;

import ca.ids.abms.config.pagination.ABMSPageableHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Value("${swagger.enabled}")
    private Boolean swaggerEnabled;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        addPageableArgumentResolvers(resolvers);
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        // required for swagger-ui, only add if enabled at startup
        if (swaggerEnabled) {
            addSwaggerResourceHandlers(registry);
        }
    }
    
    @Override
    public void addViewControllers (final ViewControllerRegistry registry) {
        if (swaggerEnabled) {
            addSwaggerViewControllers (registry);
        }
    }

    private void addPageableArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ABMSPageableHandlerMethodArgumentResolver());
    }

    // see also comments in SwaggerConfig.java
    private void addSwaggerResourceHandlers(final ResourceHandlerRegistry registry) {
        // Serve swagger-ui resources as /api/spec/web/swagger-ui/** ; they are referenced by
        // index.html (see below)
        registry.addResourceHandler("/api/spec/web/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/3.19.0/");
        // Serve index.html as /api/spec/web/index.html ; stored in src/main/resources/api_spec_web
        registry.addResourceHandler("/api/spec/web/**")
            .addResourceLocations("classpath:/api_spec_web/");
    }
    
    // see also comments in SwaggerConfig.java
    private void addSwaggerViewControllers(ViewControllerRegistry registry) {
        // redirect "web" to "web/"
        registry.addViewController("/api/spec/web").setViewName("redirect:web/");
        // serve index.html when "web/" is requested
        registry.addViewController("/api/spec/web/").setViewName("forward:index.html");
    }

}
