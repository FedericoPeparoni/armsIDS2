package ca.ids.abms.modules.users;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.roles.RoleRepository;
import ca.ids.abms.modules.roles.RoleViewModel;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ModelUtils;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@SuppressWarnings({"WeakerAccess", "unused"})
public class UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final SystemConfigurationService systemConfigurationService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private ConsumerTokenServices consumerTokenServices;

    public UserService(
        final UserRepository userRepository, final PasswordEncoder passwordEncoder, final RoleRepository roleRepository,
        final UserRoleRepository userRoleRepository, final SystemConfigurationService systemConfigurationService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.userRoleRepository = userRoleRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers(Boolean selfCareUser) {

        LOG.debug("Attempting to find users. Self Care: {}", selfCareUser);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();

        if (selfCareUser != null) {
            filterBuilder.restrictOn(selfCareUser
                ? Filter.isTrue(User.IS_SELFCARE_USER_FIELD_NAME)
                : Filter.isFalse(User.IS_SELFCARE_USER_FIELD_NAME));
        }

        return userRepository.findAll(filterBuilder.build());
    }

    @Transactional(readOnly = true)
    public User getUserById(Integer id) {
        return userRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public User getUserByLogin(String login) {
        return userRepository.getOneByLogin(login);
    }

    @Transactional(readOnly = true)
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails details = (UserDetails) auth.getPrincipal();
        return userRepository.getOneByLogin(details.getUsername());
    }

    @Transactional
    public User createUser(User user) {
        LOG.debug("Request to create User : {}", user);

        validateUniqueFields(user);

        String password;
        if (user.getPassword() != null) {
            this.checkIfPasswordMeetPasswordRequirements(user.getPassword());
            password = user.getPassword();

        } else {
            throw ExceptionFactory.getNullablePasswordException(User.class);
        }
        user.setPassword(passwordEncoder.encode(password));

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Integer id, User user) {
        LOG.debug("Request to update User : {}", user);

        try {
            final User existingUser = getUserById(id);

            validateUniqueFields(user);

            ModelUtils.mergeOnly(user, existingUser, "billingCenter", "contactInformation", "name", "smsNumber", "jobTitle", "email", "forcePasswordChange");

            existingUser.setTemporaryPassword(null);
            existingUser.setTemporaryPasswordExpiration(null);

            if (user.getPassword() != null) {
                this.checkIfPasswordMeetPasswordRequirements(user.getPassword());
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            return userRepository.save(existingUser);

        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }

    }

    public void validateUniqueFields(User user) {

        if (user == null) {
            return;
        }

        List<User> login;
        List<User> smsNumber;
        List<User> email;
        List<User> name;

        if (user.getId() != null) {
            login = userRepository.findByLoginAndIdNot(user.getLogin(), user.getId());
            smsNumber = userRepository.findBySmsNumberAndIdNot(user.getSmsNumber(), user.getId());
            email = userRepository.findByEmailAndIdNot(user.getEmail(), user.getId());
            name = userRepository.findByNameAndIdNot(user.getName(), user.getId());
        } else {
            login = userRepository.findAllByLogin(user.getLogin());
            smsNumber = userRepository.findAllBySmsNumber(user.getSmsNumber());
            email = userRepository.findAllByEmail(user.getEmail());
            name = userRepository.findAllByName(user.getName());
        }

        if (login != null && !login.isEmpty()) {
            LOG.debug("Bad request: A User with the login: {} is already exist", user.getLogin());
            final String details = "A user with this login already exists";
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details), "login");
        }

        if (smsNumber != null && !smsNumber.isEmpty() && !(user.getIsSelfcareUser() && user.getSmsNumber() == null)) {
            LOG.debug("Bad request: A User with the sms number: {} is already exist", user.getSmsNumber());
            final String details = "A user with this sms number already exists";
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details), "smsNumber");
        }

        if (email != null && !email.isEmpty()) {
            LOG.debug("Bad request: A User with the email: {} is already exist", user.getEmail());
            final String details = "A user with this email already exists";
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details), "email");
        }

        if (name != null && !name.isEmpty()) {
            LOG.debug("Bad request: A User with the name: {} is already exist", user.getName());
            final String details = "A user with this name already exists";
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, new Exception(details), "name");
        }
    }

    @Transactional
    public User updatePassword(final ChangePassword changePassword) {
        Preconditions.checkArgument(changePassword != null);

        String plainCurrentPassword = changePassword.getOldPassword();
        String plainNewPassword = changePassword.getNewPassword();

        final User authenticatedUser = getAuthenticatedUser();
        if (Boolean.FALSE.equals(authenticatedUser.getForcePasswordChange())) {
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_NOT_REQUIRED);
        }

        if (plainNewPassword == null || plainCurrentPassword == null) {
            throw ExceptionFactory.getNullablePasswordException(User.class);
        }

        if (StringUtils.equals(plainCurrentPassword, plainNewPassword)) {
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_SAME);
        }

        checkIfPasswordMeetPasswordRequirements(plainNewPassword);
        final String newPasswordEncoded = passwordEncoder.encode(plainNewPassword);
        if (newPasswordEncoded.equals(authenticatedUser.getPasswordHistory())) {
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_USED);
        }

        if (authenticatedUser.getTemporaryPassword() != null) {
            if (!passwordEncoder.matches(plainCurrentPassword, authenticatedUser.getTemporaryPassword())) {
                throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_MATCH_TEMP);
            }
            authenticatedUser.setPasswordHistory(authenticatedUser.getTemporaryPassword());
        } else if (!passwordEncoder.matches(plainCurrentPassword, authenticatedUser.getPassword())) {
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_MATCH);
        } else {
            authenticatedUser.setPasswordHistory(authenticatedUser.getPassword());
        }

        authenticatedUser.setForcePasswordChange(false);
        authenticatedUser.setPassword(newPasswordEncoded);
        authenticatedUser.setTemporaryPassword(null);
        authenticatedUser.setTemporaryPasswordExpiration(null);

        return userRepository.save(authenticatedUser);
    }

    @Transactional
    public User setCurrentUserLanguage(User user, String language) {
        user.setLanguage(language);

        return userRepository.save(user);
    }

    @Transactional
    public User updateUserRoles(Integer id, Collection<RoleViewModel> requiredRoles) {
        User existingUser = getUserById(id);

        boolean selfCareOperatorRole = requiredRoles.stream().anyMatch(role -> {
            Role selfCareOperator = roleRepository.getOneByNameIgnoreCase("Self-care operators");

            if (role != null && selfCareOperator != null) {
                return role.getId().equals(selfCareOperator.getId());
            } else {
                return false;
            }
        });

        if (!existingUser.getIsSelfcareUser() && selfCareOperatorRole) {
            LOG.debug("Bad request: user should be a Self-Care User in order to be added to the Self-care operators group");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("User should be a Self-Care User in order to be added to the Self-care operators group"));
        }
        if (existingUser.getIsSelfcareUser() && selfCareOperatorRole && requiredRoles.size() > 1 || existingUser.getIsSelfcareUser() && !selfCareOperatorRole) {
            LOG.debug("Bad request: a self-care user may only be included in the Self-care operators group");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("A self-care user may only be included in the Self-care operators group"));
        }
        else {
            final Map<Integer, UserRole> currentUserRoles = existingUser.getUserRoles().stream()
                    .collect(Collectors.toMap(ro -> ro.getRole().getId(), ro -> ro));

            final Set<Integer> rolesId = requiredRoles.stream().map(RoleViewModel::getId)
                    .collect(Collectors.toSet());

            final Map<Integer, Role> roles = roleRepository.findAllByIdIn(rolesId).stream()
                    .collect(Collectors.toMap(Role::getId, p -> p));

            final List<UserRole> userRolesToAdd = rolesId.stream().filter(i -> !currentUserRoles.containsKey(i))
                    .map(i -> mapUserRole(existingUser, roles.get(i))).collect(Collectors.toList());

            final List<UserRole> userRolesToDelete = currentUserRoles.entrySet().stream()
                    .filter(e -> !rolesId.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());

            if (LOG.isDebugEnabled()) {
                LOG.debug("@User: {} - Roles to add: {} - Roles to remove: {}", id, userRolesToAdd, userRolesToDelete);
            }
            userRoleRepository.save(userRolesToAdd);
            userRoleRepository.delete(userRolesToDelete);
            userRoleRepository.flush();
            userRepository.refresh(existingUser);
            return userRepository.findOne(id);
        }
    }

    @Transactional
    public void deleteUser(Integer id) {
        LOG.debug("Request to delete User : {}", id);
        User existingUser = getUserById(id);

        try {
            if (existingUser != null) {
                final Set<UserRole> userRoles = existingUser.getUserRoles();
                if (CollectionUtils.isNotEmpty(userRoles)) {
                    userRoleRepository.delete(userRoles);
                }
                userRoleRepository.flush();

                if (existingUser.getRegistrationStatus()) {
                   deleteToken(existingUser.getLogin());
                }

                userRepository.delete(id);
                userRepository.flush();
            } else {
            	final ErrorDTO errorDto = new ErrorDTO.Builder()
                        .setErrorMessage("User doesn't exist")
                        .appendDetails("User doesn't exist and cannot be deleted")
                        .build();
                    throw ExceptionFactory.getInvalidDataException(errorDto);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private void deleteToken(String username) {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        String clientId = ((OAuth2Authentication) a).getOAuth2Request().getClientId();
        Collection<OAuth2AccessToken> tokens = tokenStore.findTokensByClientIdAndUserName(
                clientId,
                username);
        for (OAuth2AccessToken token : tokens) {
                consumerTokenServices.revokeToken(token.getValue());
        }
    }

    private UserRole mapUserRole(User user, Role role) {
        final UserRole userRole = new UserRole();
        userRole.setUser(user);
        userRole.setRole(role);
        return userRole;
    }

    private void checkIfPasswordMeetPasswordRequirements(String password) {
        int minLength = Integer.parseInt(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.PASSWORD_MINIMUM_LENGTH));
        Boolean lowercase = systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_LOWERCASE);
        Boolean numeric = systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_NUMERIC);
        Boolean uppercase = systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_UPPERCASE);
        Boolean specialCharacter = systemConfigurationService.getBoolean(SystemConfigurationItemName.PASSWORD_SPECIAL);

        if (password.length() < minLength) { // minlength
            throw ExceptionFactory.getMinLengthPasswordException(User.class, User.PASSWORD_FIELD_NAME);
        }

        if (lowercase) { // lowercase
            Pattern p = Pattern.compile("[a-z]");
            if (!p.matcher(password).find()) {
                throw ExceptionFactory.getLowercasePasswordException(User.class, User.PASSWORD_FIELD_NAME);
            }
        }

        if (numeric) { // numeric
            Pattern p = Pattern.compile("[\\d]");
            if (!p.matcher(password).find()) {
                throw ExceptionFactory.getNumericPasswordException(User.class, User.PASSWORD_FIELD_NAME);
            }
        }

        if (uppercase) { // uppercase
            Pattern p = Pattern.compile("[A-Z]");
            if (!p.matcher(password).find()) {
                throw ExceptionFactory.getUppercasePasswordException(User.class, User.PASSWORD_FIELD_NAME);
            }
        }

        if (specialCharacter) { // special characters
            Pattern p = Pattern.compile("[\\s\\!\\\"\\#\\$\\%\\&\\'\\(\\)\\*\\+\\,\\-\\.\\/\\:\\;\\<\\=\\>\\?\\@\\[\\\\\\]\\^\\_\\`\\{\\|\\}\\~]");
            if (! p.matcher(password).find()) {
                throw ExceptionFactory.getSpecialCharacterPasswordException(User.class, User.PASSWORD_FIELD_NAME);
            }
        }
    }

    public List<String> getSelfCarePortalAdminAddress() {
        return userRepository.getSelfCarePortalAdminAddress();
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsersByFilter(String textSearch, Boolean selfCareUser, Pageable aPageable, Integer scUserId) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (selfCareUser != null) {
            filterBuilder.restrictOn(selfCareUser
                ? Filter.isTrue(User.IS_SELFCARE_USER_FIELD_NAME)
                : Filter.isFalse(User.IS_SELFCARE_USER_FIELD_NAME));
        }

        if (scUserId != null) {
            filterBuilder.restrictOn(Filter.equals("id", scUserId));
        }

        LOG.debug("Attempting to find users by filters. Search: {}, Type: {}",
            textSearch, selfCareUser);
        return userRepository.findAll(filterBuilder.build(), aPageable);
    }

    @Transactional
    public void deleteExpiredUsers() {
        LOG.debug("Checking activation key expiration for self-care users");
        List<Integer> users = userRepository.getExpiredActivationKey();
        for (Integer id: users) {
            deleteUser(id);
        }
    }

    @Transactional
    public void checkExpiredTempPasswords() {
        LOG.debug("Checking users expired temporary passwords");
        List<User> users = userRepository.getExpiredTempPasswords();
        for (User user: users) {
            user.setTemporaryPassword(null);
            user.setTemporaryPasswordExpiration(null);
        }
    }

    @Transactional
    public String createTemporaryPassword(String email) {
        User user = userRepository.getOneByEmail(email);

        if (user == null) {
            LOG.debug("Bad request: there no user with email: {}" , email);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("There is no user with this email"));
        }

        if (!user.getRegistrationStatus()) {
            LOG.debug("Bad request: user account has not been confirmed: {}" , email);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("User account has not been confirmed"));
        }

        String newTempPassword = UUID.randomUUID().toString().replace("-", "").substring(0,10);

        user.setTemporaryPassword(passwordEncoder.encode(newTempPassword));
        user.setTemporaryPasswordExpiration(LocalDateTime.now().plusHours(24));

        return newTempPassword;
    }

    public long countAll() {
        return userRepository.count();
    }

    public long countAllSelfcareUsers() {
        return userRepository.countAllSelfcareUsers();
    }
}
