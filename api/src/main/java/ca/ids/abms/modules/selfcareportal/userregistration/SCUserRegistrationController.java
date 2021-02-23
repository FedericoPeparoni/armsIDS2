package ca.ids.abms.modules.selfcareportal.userregistration;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import ca.ids.abms.modules.roles.RoleMapper;
import ca.ids.abms.modules.roles.RoleService;
import ca.ids.abms.modules.roles.RoleViewModel;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserMapper;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.users.UserViewModel;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

@RestController
@RequestMapping("/api/sc-user-registration")
@SuppressWarnings("unused")
public class SCUserRegistrationController {

    private static final Logger LOG = LoggerFactory.getLogger(SCUserRegistrationController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final SCUserRegistrationService userRegistrationService;
    private final QuerySubmissionService querySubmissionService;
    private final RoleMapper roleMapper;
    private final RoleService roleService;
    private final SystemConfigurationService systemConfigurationService;

    public SCUserRegistrationController(final UserService userService,
                                        final UserMapper userMapper,
                                        final QuerySubmissionService querySubmissionService,
                                        final SCUserRegistrationService userRegistrationService,
                                        final RoleMapper roleMapper,
                                        final RoleService roleService,
                                        final SystemConfigurationService systemConfigurationService) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.querySubmissionService = querySubmissionService;
        this.userRegistrationService = userRegistrationService;
        this.roleMapper = roleMapper;
        this.roleService = roleService;
        this.systemConfigurationService = systemConfigurationService;
    }

    @Transactional
    @PostMapping
    public ResponseEntity<UserViewModel> createUserFromSC(@Valid @RequestBody SCUserRegistrationViewModel userDto) throws URISyntaxException {
        LOG.debug("REST request to create a User from self-care portal : {}", userDto);

        String config = systemConfigurationService.getValue(SystemConfigurationItemName.REQUIRE_EMAIL_VERIFICATION);

        if (userDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        userDto.setIsSelfcareUser(true);
        userDto.setBillingCenter(null);

        if (config == null || config.equalsIgnoreCase("t")) {
            userDto.setRegistrationStatus(false);
        } else {
            userDto.setRegistrationStatus(true);
        }

        final User user = userMapper.toModel(userDto);

        if (config == null || config.equalsIgnoreCase("t")) {
            user.setEmailActivationKey(UUID.randomUUID().toString().replace("-", ""));
            user.setActivationKeyExpiration(LocalDateTime.now().plusHours(24));
        } else {
            user.setEmailActivationKey(null);
            user.setActivationKeyExpiration(null);
        }

        User newUser = userService.createUser(user);

        final RoleViewModel role = roleMapper.toViewModel(roleService.getRoleByName("Self-care operators"));
        Collection<RoleViewModel> requiredRoles = new ArrayList<>();
        requiredRoles.add(role);
        newUser = userService.updateUserRoles(newUser.getId(), requiredRoles);
        final UserViewModel result = userMapper.toViewModel(newUser);

        if (config == null || config.equalsIgnoreCase("t")) {
            String text = "Thanks for signing up! Your user account has been created.\n\n" +
            "Please click this link to activate your user account:\n\n" +
            userDto.getUrl() + "-activate?key=" + user.getEmailActivationKey();

            querySubmissionService.send("Verification", text, new ArrayList<>(Collections.singletonList(user.getEmail())), true);
        }

        return ResponseEntity.created(new URI("/api/sc-user-registration/" + result.getId())).body(result);
    }

    @PostMapping("/{activate}")
    public Boolean activateUserFromSC(@Valid @RequestBody String key) {
        LOG.debug("REST request to activate a User from self-care portal with the key: {}", key);

        if (key == null) {
            return false;
        }

        return userRegistrationService.activateUser(key);
    }
}
