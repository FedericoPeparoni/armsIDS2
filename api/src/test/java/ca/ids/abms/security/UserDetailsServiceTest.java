package ca.ids.abms.security;

import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailsServiceTest {

    private UserRepository     userRepository;
    private UserDetailsService service;

    @Before
    public void setup() {
        userRepository = mock(UserRepository.class);

        service = new UserDetailsService(userRepository);
    }

    @Test
    public void loadUserByUsername() throws Exception {
        String username = "test";
        String password = "foobar";

        User user = new User();
        user.setUserRoles(Collections.emptySet());
        user.setPassword(password);

        when(userRepository.getOneByLogin(username)).thenReturn(user);

        UserDetails result = service.loadUserByUsername(username);

        assertThat(result.getUsername()).isEqualTo(username);
        assertThat(result.getPassword()).isEqualTo(password);
        assertThat(result.getAuthorities()).hasSize(0);
    }

    @Test(expected = UsernameNotFoundException.class)
    public void loadUserByUserNameThrows() throws Exception {
        when(userRepository.getOneByLogin(anyString())).thenReturn(null);

        service.loadUserByUsername("invalid");
    }
}
