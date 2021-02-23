package ca.ids.abms.security;

import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final Logger         log;
    private final UserRepository userRepository;

    public UserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
        log = LoggerFactory.getLogger(UserDetailsService.class);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        login = login.toLowerCase();

        log.debug("Attempting to fetch user details for '{}'", login);

        User user = userRepository.getOneByLogin(login);

        if (user == null) {
            log.error("User '{}' not found!", login);
            throw new UsernameNotFoundException(String.format("User '%s' not found!", login));
        }

        if (user.getRegistrationStatus() != null && !user.getRegistrationStatus()) {
            log.error("User '{}' has not been activated!", login);
            throw new UsernameNotFoundException(String.format("User '%s' has not been activated!", login));
        }

        final List <String> authStrList = user.getPermissions().stream().sorted().distinct().collect(Collectors.toList());
        log.debug("Loaded {} permission(s) for user \"{}\": {}", authStrList.size(), login, authStrList);

        List<GrantedAuthority> authorities = user.getPermissions().stream()
            .map(p -> (GrantedAuthority) () -> p)
            .collect(Collectors.toList());

        if (user.getTemporaryPassword() != null) {
            return new AbmsUser(login, user.getTemporaryPassword(), authorities, user.getForcePasswordChange());
        }
        else {
            return new AbmsUser(login, user.getPassword(), authorities, user.getForcePasswordChange());
        }

    }
}
