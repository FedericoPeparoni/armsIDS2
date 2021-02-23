package ca.ids.abms.modules.roles;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.util.exception.OperationNotAllowedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@SuppressWarnings({"unused", "squid:S1452"})
public class RolesController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RolesController.class);

    private final RoleService roleService;
    private final RoleMapper roleMapper;
    private final ReportDocumentCreator reportDocumentCreator;
    private static final String UPDATE_NOT_ALLOWED = "Update of a role not allowed: {}";

    public RolesController(final RoleService roleService,
                           final RoleMapper roleMapper,
                           final ReportDocumentCreator reportDocumentCreator) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllRoles(@SortDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable,
                                         @RequestParam(name = "search", required = false) final String textSearch,
                                         @RequestParam(name = "ignoreChangePassword", required = false) final Boolean ignoreChangePassword,
                                         @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get roles that contain the text: {}", textSearch);

        Page<Role> roles = roleService.getAllRoles(textSearch, ignoreChangePassword, pageable);

        if (csvExport != null && csvExport) {
            final List<Role> list = roles.getContent();
            final List<RoleCsvExportModel> csvExportModel = roleMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Groups", csvExportModel, RoleCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RoleViewModel> resultPage = new PageImplCustom<>(roleMapper.toViewModel(roles),
                pageable, roles.getTotalElements(), roleService.countAll(ignoreChangePassword));
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RoleViewModel> getRole(@PathVariable Integer id) {
        Role role = roleService.getRoleById(id);
        RoleViewModel roleDto = roleMapper.toViewModel(role);
        return Optional.ofNullable(roleDto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('group_modify')")
    @PostMapping
    public ResponseEntity<RoleViewModel> createRole(@Valid @RequestBody RoleViewModel roleDto) throws URISyntaxException {
        LOG.debug("REST request to save Role : {}", roleDto);
        if (roleDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        Role roleToCreate = roleMapper.toModel(roleDto);
        Role createdRole = roleService.createRole(roleToCreate);
        RoleViewModel result = roleMapper.toViewModel(createdRole);

        return ResponseEntity.created(new URI("/api/roles/" + result.getId()))
            .body(result);
    }

    @PreAuthorize("hasAuthority('group_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Integer id) {
        LOG.debug("REST request to delete Role with id : {}", id);
        try {
            roleService.deleteRole(id);
        } catch (OperationNotAllowedException onae) {
            LOG.warn("Delete of a role not allowed: {}", onae.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('group_modify')")
    @PutMapping(value = "/{id}/permissions")
    public ResponseEntity<RoleViewModel> updateRolePermissions(@PathVariable Integer id,
                                                               @RequestBody Collection<RolePermissionViewModel> rolePermissions) {
        Collection<Integer> permissionIds = roleMapper.getPermissionIds(rolePermissions);
        RoleViewModel updatedRoleDto;
        try {
        Role role = roleService.updateUserRolePermissions(id, permissionIds);
            updatedRoleDto = roleMapper.toViewModel(role);
        } catch (OperationNotAllowedException onae) {
            LOG.warn(UPDATE_NOT_ALLOWED, onae.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return Optional.ofNullable(updatedRoleDto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('group_modify')")
    @PutMapping(value = "/{id}/owned-roles")
    public ResponseEntity<RoleViewModel> updateOwnedRoles(@PathVariable Integer id,
                                                          @RequestBody Collection<RoleOwnershipViewModel> roleOwnedCollection) {
        Collection<Integer> ownedRoleIds = roleMapper.getOwnedRolesIds(roleOwnedCollection);
        RoleViewModel updatedRoleDto;
        try {
            Role role = roleService.updateRoleOwnership(id, ownedRoleIds);
            updatedRoleDto = roleMapper.toViewModel(role);
        } catch (OperationNotAllowedException onae) {
            LOG.warn(UPDATE_NOT_ALLOWED, onae.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return Optional.ofNullable(updatedRoleDto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('group_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RoleViewModel> updateRole(@PathVariable Integer id,
                                                    @RequestBody RoleViewModel roleDto) {
        Role role = roleMapper.toModel(roleDto);
        RoleViewModel updatedRoleDto;
        try {
            Role updatedRole = roleService.updateRole(id, role);
            updatedRoleDto = roleMapper.toViewModel(updatedRole);
        } catch (OperationNotAllowedException onae) {
            LOG.warn(UPDATE_NOT_ALLOWED, onae.getLocalizedMessage());
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return Optional.ofNullable(updatedRoleDto)
            .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
