package ca.ids.abms.modules.users;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.roles.RoleRepository;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {
        RoleRepository roleRepository = mock(RoleRepository.class);
        UserRoleRepository userRoleRepository = mock(UserRoleRepository.class);

        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        systemConfigurationService = mock(SystemConfigurationService.class);

        userService = new UserService(userRepository, passwordEncoder, roleRepository, userRoleRepository,
            systemConfigurationService);
    }

    @Test
    public void getAllUsers() {
        List<User> users = Collections.singletonList(new User());

        when(userRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(users));

        Page<User> results = userService.getAllUsers(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(users.size());
    }

    @Test
    public void getUserById() {
        User user = new User();
        user.setId(1);

        when(userRepository.getOne(any()))
            .thenReturn(user);

        User result = userService.getUserById(1);
        assertThat(result).isEqualTo(user);
    }

    @Test
    public void getUserByLogin() {
        User user = new User();
        user.setLogin("foobar");

        when(userRepository.getOneByLogin(any()))
            .thenReturn(user);

        User result = userService.getUserByLogin("foobar");
        assertThat(result.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    public void createUser() {
        when(passwordEncoder.encode(any()))
            .thenReturn("encrypted");

        User user = new User();
        user.setPassword("test");

        when(userRepository.save(any(User.class)))
            .thenReturn(user);

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");

        User result = userService.createUser(user);
        assertThat(result.getPassword()).isEqualTo("encrypted");
    }

    private static final String USER_NAME = "user name";
    private static final String OLD_PWD = "old password";
    private static final String NEW_PWD = "new password";
    private static final String OLD_ENC_PWD = "old encrypted";
    private static final String NEW_ENC_PWD = "new encrypted";

    @Test
    public void changePassword() {
        final User authenticatedUser = new User();
        authenticatedUser.setName(USER_NAME);
        authenticatedUser.setPassword(OLD_ENC_PWD);
        authenticatedUser.setForcePasswordChange(true);
        authenticatedUser.setTemporaryPassword(OLD_ENC_PWD);
        authenticatedUser.setTemporaryPasswordExpiration(LocalDateTime.now().plusDays(1));

        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(USER_NAME);

        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.PASSWORD_MINIMUM_LENGTH))
            .thenReturn("8");
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_LOWERCASE))
            .thenReturn(true);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_NUMERIC))
            .thenReturn(false);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_UPPERCASE))
            .thenReturn(false);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_SPECIAL))
            .thenReturn(false);

        when(passwordEncoder.encode(eq(OLD_PWD)))
            .thenReturn(OLD_ENC_PWD);
        when(passwordEncoder.encode(eq(NEW_PWD)))
            .thenReturn(NEW_ENC_PWD);
        when(passwordEncoder.matches(eq(OLD_PWD), eq(OLD_ENC_PWD)))
            .thenReturn(true);
        when(userRepository.getOneByLogin(eq(USER_NAME)))
            .thenReturn(authenticatedUser);

        when(userRepository.save(any(User.class)))
            .thenReturn(authenticatedUser);

        final User updatedUser = userService.updatePassword(MOCK_CHANGE_PASSWORD());

        assertThat(updatedUser.getPassword()).isEqualTo(NEW_ENC_PWD);
        assertThat(updatedUser.getForcePasswordChange()).isFalse();
        assertThat(updatedUser.getTemporaryPassword()).isNull();
        assertThat(updatedUser.getTemporaryPasswordExpiration()).isNull();
    }

    @Test
    public void updateUser() {
        User existingUser = new User();
        existingUser.setPassword("old password");
        existingUser.setName("old name");

        User user = new User();
        user.setPassword("new password");
        user.setName("new name");

        when(userRepository.getOne(any()))
            .thenReturn(existingUser);

        when(passwordEncoder.encode(any()))
            .thenReturn("encrypted");

        when(userRepository.save(any(User.class)))
            .thenReturn(existingUser);

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");

        User result = userService.updateUser(1, user);
        assertThat(result.getPassword()).isEqualTo("encrypted");
        assertThat(result.getName()).isEqualTo("new name");
    }

    @Test
    public void updateUserWithoutNameOrPassword() {
        User existingUser = new User();
        existingUser.setPassword("old password");
        existingUser.setName("old name");

        User user = new User();

        when(userRepository.getOne(any()))
            .thenReturn(existingUser);

        when(userRepository.save(any(User.class)))
            .thenReturn(existingUser);

        User result = userService.updateUser(1, user);
        assertThat(result.getPassword()).isEqualTo("old password");
        assertThat(result.getName()).isEqualTo("old name");
    }

    @Test
    public void deleteUser() {
    	User existingUser = new User();
        existingUser.setId(1);
        existingUser.setRegistrationStatus(false);

        when(userRepository.getOne(any()))
            .thenReturn(existingUser);
        userService.deleteUser(1);

        verify(userRepository).delete(any(Integer.class));
    }

    @Test
    public void userPasswordMinLength() {
        User user = new User();
        user.setPassword("hunter2");

        when(systemConfigurationService.getBoolean(anyString())).thenReturn(Boolean.FALSE);
        when(systemConfigurationService.getCurrentValue("Password minimum length")).thenReturn("8");

        assertThatThrownBy(() -> userService.createUser(user))
            .hasMessage(ErrorConstants.ERR_PASSWORD_MINIMUM_LENGTH.toValue());

        user.setPassword("hunter22");
        userService.createUser(user);
    }

    @Test
    public void userPasswordLowercase() {
        User user = new User();
        user.setPassword("HUNTER2");

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_LOWERCASE)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.createUser(user))
            .hasMessage(ErrorConstants.ERR_PASSWORD_LOWERCASE.toValue());

        user.setPassword("hunter2");
        userService.createUser(user);
    }

    @Test
    public void userPasswordNumeric() {
        User user = new User();
        user.setPassword("hunter");

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_NUMERIC)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.createUser(user))
            .hasMessage(ErrorConstants.ERR_PASSWORD_NUMERIC.toValue());

        user.setPassword("hunter2");
        userService.createUser(user);
    }

    @Test
    public void userPasswordSpecialCharacter() {
        User user = new User();
        user.setPassword("hunter2");

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_SPECIAL)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.createUser(user))
            .hasMessage(ErrorConstants.ERR_PASSWORD_SPECIAL.toValue());

        user.setPassword("hunter2!");
        userService.createUser(user);
    }

    @Test
    public void userPasswordUppercase() {
        User user = new User();
        user.setPassword("hunter2");

        when(systemConfigurationService.getCurrentValue(anyString())).thenReturn("0");
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_UPPERCASE)).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> userService.createUser(user))
            .hasMessage(ErrorConstants.ERR_PASSWORD_UPPERCASE.toValue());

        user.setPassword("Hunter2");
        userService.createUser(user);
    }

    @Test
    public void validateUniqueFields() {

        // validate email
        assertThatThrownBy(() -> {
            User user = new User();
            user.setId(2);
            user.setEmail("test123@gmail.com");

            User existingUser = new User();
            existingUser.setEmail("test123@gmail.com");

            List<User> existingUserWithEmail = new ArrayList<>();
            existingUserWithEmail.add(existingUser);
            when(userRepository.findByEmailAndIdNot(user.getEmail(), 2))
                .thenReturn(existingUserWithEmail);

            userService.validateUniqueFields(user);
        }).hasMessageMatching("ERR_UNIQUENESS_VIOLATION").hasCause(new Exception("A user with this email already exists"));

        // validate smsNumber
        assertThatThrownBy(() -> {
            User user = new User();
            user.setId(2);
            user.setIsSelfcareUser(false);
            user.setSmsNumber("5139085");

            User existingUser = new User();
            existingUser.setSmsNumber("5139085");

            List<User> existingUserWithSmsNumber = new ArrayList<>();
            existingUserWithSmsNumber.add(existingUser);
            when(userRepository.findBySmsNumberAndIdNot(user.getSmsNumber(), 2))
                .thenReturn(existingUserWithSmsNumber);

            userService.validateUniqueFields(user);
        }).hasMessageMatching("ERR_UNIQUENESS_VIOLATION").hasCause(new Exception("A user with this sms number already exists"));

        // validate name
        assertThatThrownBy(() -> {
            User user = new User();
            user.setId(2);
            user.setName("Ivan Popov");

            User existingUser = new User();
            existingUser.setName("Ivan Popov");

            List<User> existingUserWithName = new ArrayList<>();
            existingUserWithName.add(existingUser);
            when(userRepository.findByNameAndIdNot(user.getName(), 2))
                .thenReturn(existingUserWithName);

            userService.validateUniqueFields(user);
        }).hasMessageMatching("ERR_UNIQUENESS_VIOLATION").hasCause(new Exception("A user with this name already exists"));
    }

    private static ChangePassword MOCK_CHANGE_PASSWORD() {
        ChangePassword result = new ChangePassword();
        result.setOldPassword(UserServiceTest.OLD_PWD);
        result.setNewPassword(UserServiceTest.NEW_PWD);
        return result;
    }
}
