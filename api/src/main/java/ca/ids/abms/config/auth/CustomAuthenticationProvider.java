package ca.ids.abms.config.auth;

import ca.ids.abms.config.security.AuthoritiesConstants;
import ca.ids.abms.modules.usereventlogs.UserEventLog;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.security.UserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashMap;

@Component
public class CustomAuthenticationProvider extends DaoAuthenticationProvider {

    private static final Logger LOG = LoggerFactory.getLogger(CustomAuthenticationProvider.class);

    private final UserEventLogService userEventLogService;

    public CustomAuthenticationProvider(final UserDetailsService userDetailsService, UserEventLogService userEventLogService) {
        this.setUserDetailsService(userDetailsService);
        this.userEventLogService = userEventLogService;
    }

    @Override
    public Authentication authenticate(Authentication auth) {
        Authentication result = null;

        Boolean fromSelfCare = null;
        if (auth.getDetails() instanceof LinkedHashMap) {
            LinkedHashMap details = (LinkedHashMap)auth.getDetails();
            fromSelfCare = Boolean.valueOf((String) details.get("selfCare"));
        }

        try {
            result = super.authenticate(auth);

            if (result.isAuthenticated()) {
                boolean selfCareOperatorOnly = isSelfCareOperatorOnly(result.getAuthorities());
                boolean selfCareAdmin = isSelfCareAdmin(result.getAuthorities());
                if (fromSelfCare != null) {
                    if ((!fromSelfCare && selfCareOperatorOnly) || (fromSelfCare && !selfCareAdmin && !selfCareOperatorOnly)) {
                        result = null;
                        LOG.debug("Access to internal system denied for self-care user {} ", auth.getName());
                        saveFailedLogin(auth.getName());
                    }
                }
                else {
                    LOG.debug("User {} is authenticated successfully", auth.getName());
                }
            } else {
                LOG.debug("User {} is NOT authenticated", auth.getName());

                saveFailedLogin(auth.getName());
            }
        } catch (BadCredentialsException bce) {
            LOG.debug("User {} has provided wrong credentials", auth.getName());

            saveFailedLogin(auth.getName());
        }

        return result;
    }

    private void saveFailedLogin(String name) {
        final WebAuthenticationDetails authDetails = (WebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication().getDetails();
        UserEventLog userEventLog = new UserEventLog();

        userEventLog.setId(null);
        userEventLog.setUserName(name);
        userEventLog.setDateTime(LocalDateTime.now());
        userEventLog.setEventType("login");
        userEventLog.setUniqueRecordId("failed");
        userEventLog.setIpAddress(authDetails.getRemoteAddress());
        userEventLog.setModifiedColumnNamesValues("");
        userEventLog.setRecordPrimaryKey("");

        try {
            userEventLogService.create(userEventLog);
        } catch (Exception e) {
            LOG.error("ERRROR: {}, {}, {}", e.getLocalizedMessage(), e.getCause(), e.getLocalizedMessage());
        }
    }

    private boolean isSelfCareAdmin(Collection<? extends GrantedAuthority> authorities) {
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.SELF_CARE_ADMIN)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSelfCareOperatorOnly(Collection<? extends GrantedAuthority> authorities) {
        boolean selfCareOperatorOnly = false;

        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equalsIgnoreCase(AuthoritiesConstants.SELF_CARE_ACCESS)) {
                    selfCareOperatorOnly = true;
                } else {
                    return false;
                }
            }
        }
        return selfCareOperatorOnly;
    }
}
