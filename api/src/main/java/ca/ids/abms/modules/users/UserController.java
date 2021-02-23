package ca.ids.abms.modules.users;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.roles.RoleViewModel;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@SuppressWarnings({"unused", "squid:S1452"})
public class UserController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final QuerySubmissionService querySubmissionService;
    private final PasswordEncoder passwordEncoder;
    private final ReportDocumentCreator reportDocumentCreator;

    public UserController(final UserService userService,
                          final UserMapper userMapper,
                          final QuerySubmissionService querySubmissionService,
                          final PasswordEncoder passwordEncoder,
                          final ReportDocumentCreator reportDocumentCreator) {
        this.userService = userService;
        this.userMapper = userMapper;
        this.querySubmissionService = querySubmissionService;
        this.passwordEncoder = passwordEncoder;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestParam(name = "search", required = false) final String textSearch,
                                         @RequestParam(required = false) Boolean selfCareUser,
                                         @SortDefault(sort = {"login"}, direction = Sort.Direction.ASC) Pageable pageable,
                                         @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        if (textSearch != null)
            LOG.debug("REST request to get users that contain the text '{}' where self-care is '{}'.",
                textSearch, selfCareUser);
        else
            LOG.debug("REST request to get users where self-care is '{}'.", selfCareUser);

        final Page<User> page = userService.getAllUsersByFilter(textSearch, selfCareUser, pageable, null);

        if (csvExport != null && csvExport) {
            final List<User> list = page.getContent();
            final List<UserCsvExportModel> csvExportModel = userMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Users", csvExportModel, UserCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UserViewModel> resultPage = new PageImplCustom<>(userMapper.toViewModel(page), pageable, page.getTotalElements(), userService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping("/list")
    public List<UserViewModel> getAllUsers(@RequestParam(required = false) Boolean selfCareUser) {

        LOG.debug("REST request to get users where self-care is '{}'", selfCareUser);
        final List<User> users = userService.getAllUsers(selfCareUser);
        final List<UserViewModel> result = userMapper.toViewModel(users);
        result.sort(Comparator.comparing(UserViewModel::getName));

        return result;
    }

    @GetMapping("/listLight")
    public List<UserViewModelLight> getAllUsersLight(@RequestParam(required = false) Boolean selfCareUser) {

        LOG.debug("REST request to get users where self-care is '{}'", selfCareUser);
        final List<User> users = userService.getAllUsers(selfCareUser);
        final List<UserViewModelLight> result = userMapper.toViewModelLight(users);
        result.sort(Comparator.comparing(UserViewModelLight::getName));

        return result;
    }

    @GetMapping("/{id}")
    public UserViewModel getUserById(@PathVariable Integer id) {
        return userMapper.toViewModel(userService.getUserById(id));
    }

    /**
     * Returns the currently logged in user
     */
    @GetMapping("/current")
    public UserViewModel getCurrentUser() {
        return userMapper.toViewModel(userService.getAuthenticatedUser());
    }

    @PutMapping("/setCurrentUserLanguage/{id}/{language}")
    public void setCurrentUserLanguage(@PathVariable Integer id, @PathVariable String language) {
        User currentUser = userService.getUserById(id);
        userMapper.toViewModel(userService.setCurrentUserLanguage(currentUser, language));
    }

    @PreAuthorize("hasAuthority('user_modify')")
    @PostMapping
    public UserViewModel createUser(@Valid @RequestBody UserViewModel user) {
        return userMapper.toViewModel(userService.createUser(userMapper.toModel(user)));
    }

    @PreAuthorize("hasAuthority('user_modify')")
    @PutMapping("/{id}")
    public UserViewModel updateUser(@PathVariable Integer id, @RequestBody UserViewModel user) {
        return userMapper.toViewModel(userService.updateUser(id, userMapper.toModel(user)));
    }

    @PutMapping("/updatePassword")
    public UserViewModel updatePassword(@RequestBody final ChangeUserInfo userInfo) {
        // retrieve current user and validate
        User currentUser = userService.getAuthenticatedUser();
        String email = userInfo.getEmail();
        String sms = userInfo.getSmsNumber();

        if (currentUser == null)
            return null;
        else if (StringUtils.equals(userInfo.getOldPassword(), userInfo.getNewPassword()))
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_SAME);
        else if (!passwordEncoder.matches(userInfo.getOldPassword(), currentUser.getPassword()))
            throw new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_MATCH);

        currentUser.setPassword(userInfo.getNewPassword());

        if (email != null && !email.isEmpty()) {
            currentUser.setEmail(email);
        }

        if (sms != null && !sms.isEmpty()) {
            currentUser.setSmsNumber(sms);
        }

        return userMapper.toViewModel(userService.updateUser(currentUser.getId(), currentUser));
    }

    @PutMapping("/change-pwd")
    public UserViewModel changeUserPassword(@RequestBody final ChangePassword changePassword) {
        return userMapper.toViewModel(userService.updatePassword(changePassword));
    }

    @PreAuthorize("hasAuthority('user_modify')")
    @PutMapping("/{id}/roles")
    public UserViewModel updateUserRoles(@PathVariable Integer id, @RequestBody Collection<RoleViewModel> roleIds) {
        return userMapper.toViewModel(userService.updateUserRoles(id, roleIds));
    }

    @PreAuthorize("hasAuthority('user_modify')")
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PostMapping("/{passwordRecovery}")
    public Boolean createTemporaryPassword(@Valid @RequestBody String email) {
        LOG.debug("REST request to create a temporary password for user email: {}", email);

        if (email == null) {
            return false;
        }

        String tempPassword = userService.createTemporaryPassword(email);

        if (tempPassword == null) {
            return false;
        }

        String text =
            "A new temporary password has been created for your account.\n" +
            " \n" +
            "The new password is: " + tempPassword + " \n" +
            " \n" +
            "This password will expire in 24 hours.\n";

        return querySubmissionService.send("Password recovery", text, new ArrayList<>(Collections.singletonList(email)), true);
    }
}
