package ca.ids.abms.config.auth;

import ca.ids.abms.security.AbmsUser;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthCustomFilter extends GenericFilterBean {

    private static final String[] ALLOWED_TO_CHANGE_PWD = {"/api/user", "/api/change-pwd", "/api/user-event-logs",
        "/api/system-configurations"};

    @Override
    public void doFilter(final ServletRequest req, final ServletResponse response, final FilterChain chain)
        throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.isAuthenticated()
            && authentication.getPrincipal() instanceof AbmsUser
            && ((AbmsUser)(authentication.getPrincipal())).isForcedToChangeThePassword()
            && !StringUtils.startsWithAny(request.getRequestURI(), ALLOWED_TO_CHANGE_PWD)) {

            ((HttpServletResponse)response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
        chain.doFilter(req, response);
    }
}
