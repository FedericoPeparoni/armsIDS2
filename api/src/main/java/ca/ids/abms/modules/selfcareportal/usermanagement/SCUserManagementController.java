package ca.ids.abms.modules.selfcareportal.usermanagement;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.roles.RoleViewModel;
import ca.ids.abms.modules.users.*;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/sc-user-management")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCUserManagementController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCUserManagementController.class);

    private final UserService userService;
    private final UserMapper userMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public SCUserManagementController(final UserService userService, final UserMapper userMapper, final ReportDocumentCreator reportDocumentCreator){
        this.userService = userService;
        this.userMapper = userMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping
    public ResponseEntity<?> getAllSelfCareUsers(@RequestParam(name = "search", required = false) final String textSearch,
                                                 @RequestParam(required = false) Boolean selfCareUser,
                                                 @SortDefault(sort = {"login"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        if (textSearch != null)
            LOG.debug("REST request to get self-care users that contain the text: {}", textSearch);
        else
            LOG.debug("REST request to get self-care users");

        User currentUser = userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
        Page<User> page;
        long totalUsers;

        if(!currentUser.getIsSelfcareUser()) {
            page = userService.getAllUsersByFilter(textSearch, selfCareUser, pageable, null);
            totalUsers = userService.countAllSelfcareUsers();
        } else {
            page = userService.getAllUsersByFilter(textSearch, selfCareUser, pageable, currentUser.getId());
            totalUsers = 1;
        }

        if (csvExport != null && csvExport) {
            final List<User> list = page.getContent();
            final List<UserCsvExportModel> csvExportModel = userMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Users", csvExportModel, UserCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UserViewModel> resultPage = new PageImplCustom<>( userMapper.toViewModel(page), pageable, page.getTotalElements(), totalUsers);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping("/{id}")
    public UserViewModel getSelfCareUserById(@PathVariable Integer id) {
        LOG.debug("REST request to get self-care user by id: {}", id);
        return userMapper.toViewModel(userService.getUserById(id));
    }


    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PostMapping
    public UserViewModel createSelfCareUser(@Valid @RequestBody UserViewModel userDto) {
        LOG.debug("REST request to create a self-care user: {}", userDto);
        User user = userMapper.toModel(userDto);
        return userMapper.toViewModel(userService.createUser(user));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping("/{id}")
    public UserViewModel updateSelfCareUser(@PathVariable Integer id, @RequestBody UserViewModel userDto) {
        LOG.debug("REST request to update a self-care user: {}", userDto);
        User user = userMapper.toModel(userDto);
        return userMapper.toViewModel(userService.updateUser(id, user));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping("/{id}/roles")
    public UserViewModel updateSelfCareUserRoles(@PathVariable Integer id, @RequestBody Collection<RoleViewModel> roleIds) {
        return userMapper.toViewModel(userService.updateUserRoles(id, roleIds));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @DeleteMapping("/{id}")
    public void deleteSelfCareUser(@PathVariable Integer id) {
        LOG.debug("REST request to delete a self-care user with id: {}", id);
        userService.deleteUser(id);
    }
}
