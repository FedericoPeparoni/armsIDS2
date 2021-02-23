package ca.ids.abms.config;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CorsFilter extends OncePerRequestFilter {

    private final CorsConfiguration corsConfiguration;

    public CorsFilter(AbmsProperties abmsProperties) {
        this.corsConfiguration = abmsProperties.getCors();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setHeader("Access-Control-Allow-Origin", String.join(",", corsConfiguration.getAllowedOrigins()));
        response.setHeader("Access-Control-Allow-Credentials", corsConfiguration.getAllowCredentials().toString());
        response.setHeader("Access-Control-Allow-Methods", String.join(",", corsConfiguration.getAllowedMethods()));
        response.setHeader("Access-Control-Max-Age", corsConfiguration.getMaxAge().toString());
        response.setHeader("Access-Control-Allow-Headers", String.join(",", corsConfiguration.getAllowedHeaders()));
        response.setHeader("Access-Control-Expose-Headers", String.join(",", corsConfiguration.getExposedHeaders()));
        try {
            String userName;
            if (request.getUserPrincipal() != null) {
                userName = request.getUserPrincipal().getName();
            } else {
                userName = "guest";
            }
            final String mdcData = String.format("%s@%s", userName, request.getRemoteAddr());
            /* Variable 'abms-mdc-data' is referenced in Spring Boot's logging.pattern.level property */
            MDC.put("abms-mdc-data", mdcData);
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove ("abms-mdc-data");
        }
    }
}
