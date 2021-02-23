package ca.ids.abms.config.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String USER = "ROLE_USER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    public static final String SELF_CARE_ACCESS = "self_care_access";

    public static final String SELF_CARE_ADMIN = "self_care_admin";

    private AuthoritiesConstants() {
    }
}
