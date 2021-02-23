package ca.ids.abms.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AbmsUser extends User {

    private boolean isForcedToChangeThePassword;

    public AbmsUser (String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, true, true, true, true, authorities);
    }

    public AbmsUser (String username, String password, Collection<? extends GrantedAuthority> authorities, Boolean isForcedToChangeThePassword) {
        super(username, password, true, true, true, true, authorities);
        this.isForcedToChangeThePassword = isForcedToChangeThePassword != null ? isForcedToChangeThePassword : false;
    }

    public boolean isForcedToChangeThePassword() {
        return isForcedToChangeThePassword;
    }

    public void setForcedToChangeThePassword(boolean forcedToChangeThePassword) {
        isForcedToChangeThePassword = forcedToChangeThePassword;
    }
}
