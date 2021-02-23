package ca.ids.abms.modules.roles;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.permissions.Permission;
import ca.ids.abms.modules.permissions.PermissionsRepository;
import ca.ids.abms.modules.users.UserRole;
import ca.ids.abms.modules.users.UserRoleRepository;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.exception.OperationNotAllowedException;

@Service
@Transactional
public class RoleService {

    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final PermissionsRepository permissionsRepository;
    private final RoleOwnershipRepository roleOwnershipRepository;
    private final UserRoleRepository userRoleRepository;
    private static final Logger LOG = LoggerFactory.getLogger(RoleService.class);

    private static final String ADMIN_ROLE_NAME = "Administrator";

    @SuppressWarnings("squid:S2068")
    private static final String CHANGE_PASSWORD_ROLE_NAME = "Change the password";

    private static final String ROLE_NAME_FIELD = "name";

    public RoleService(RoleRepository roleRepository, RolePermissionRepository rolePermissionRepository,
            PermissionsRepository permissionsRepository, RoleOwnershipRepository roleOwnershipRepository,
            UserRoleRepository userRoleRepository) {
        this.roleRepository = roleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.permissionsRepository = permissionsRepository;
        this.roleOwnershipRepository = roleOwnershipRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Transactional(readOnly = true)
    public Page<Role> getAllRoles(String textSearch, Boolean ignoreChangePassword, Pageable pageable) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        LOG.debug("Attempting to find roles. Search: {}", textSearch);

        if (ignoreChangePassword != null && ignoreChangePassword)
            filterBuilder.restrictOn(Filter.notEqual(ROLE_NAME_FIELD, CHANGE_PASSWORD_ROLE_NAME));

        return roleRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Role getRoleById(int id) {
        return roleRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String name) {
        return roleRepository.getOneByNameIgnoreCase(name);
    }

    public Role updateUserRolePermissions(Integer roleId, Collection<Integer> permissions) {
        Role roleToUpdate = getRoleById(roleId);
        if (roleToUpdate != null) {
            if (ADMIN_ROLE_NAME.equals(roleToUpdate.getName()))
                throw ExceptionFactory.getAdministratorException(RoleService.class);
            roleToUpdate = this.updateRolePermissions(roleToUpdate, permissions);
        }
        return roleToUpdate;
    }

    public Role createRole(Role role) {
        return roleRepository.save(role);
    }

    public Role updateRole(Integer id, Role role) {
        Role existingRole = getRoleById(id);
        if (ADMIN_ROLE_NAME.equals(existingRole.getName()) || ADMIN_ROLE_NAME.equals(role.getName())) {
            throw ExceptionFactory.getAdministratorException(RoleService.class);
        }
        ModelUtils.merge(role, existingRole, "createdAt", "createdBy", "id");
        return roleRepository.save(existingRole);
    }

    public void deleteRole(Integer id) throws OperationNotAllowedException {
        Role role = roleRepository.getOne(id);
        if (role != null) {
            if (ADMIN_ROLE_NAME.equals(role.getName()))
                throw ExceptionFactory.getAdministratorException(RoleService.class);

            final Set<RoleOwnership> roleOwnerships = role.getRoleOwnership();
            if (CollectionUtils.isNotEmpty(roleOwnerships)) {
                roleOwnershipRepository.delete(roleOwnerships);
            }
            final Set<RoleOwnership> parentRelationship = roleOwnershipRepository
                    .getAllRoleOwnershipByOwnedRoleId(role);
            if (CollectionUtils.isNotEmpty(parentRelationship)) {
                roleOwnershipRepository.delete(parentRelationship);
            }
            final Set<RolePermission> rolePermissions = role.getRolePermissions();
            if (CollectionUtils.isNotEmpty(rolePermissions)) {
                rolePermissionRepository.delete(rolePermissions);
            }
            final Set<UserRole> userRoles = role.getUserRoles();
            if (CollectionUtils.isNotEmpty(userRoles)) {
                userRoleRepository.delete(userRoles);
            }
            roleOwnershipRepository.flush();
            rolePermissionRepository.flush();
            userRoleRepository.flush();
            roleRepository.delete(id);
            roleRepository.flush();
        }
    }

    public Role updateRoleOwnership(Integer id, Collection<Integer> requiredRolesId) {
        final Role currentRole = getRoleById(id);
        final Map<Integer, RoleOwnership> currentOwnedRoles = currentRole.getRoleOwnership().stream()
                .collect(Collectors.toMap(ro -> ro.getOwnedRoleId().getId(), ro -> ro));

        final Map<Integer, Role> roles = roleRepository.findAllByIdIn(requiredRolesId).stream()
                .collect(Collectors.toMap(Role::getId, p -> p));

        final List<RoleOwnership> rolesOwnershipToAdd = requiredRolesId.stream()
                .filter(i -> !currentOwnedRoles.containsKey(i)).map(i -> mapRoleOwnership(currentRole, roles.get(i)))
                .collect(Collectors.toList());

        final List<RoleOwnership> rolesOwnershipToDelete = currentOwnedRoles.entrySet().stream()
                .filter(e -> !requiredRolesId.contains(e.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList());

        roleOwnershipRepository.save(rolesOwnershipToAdd);
        roleOwnershipRepository.delete(rolesOwnershipToDelete);
        roleOwnershipRepository.flush();
        roleRepository.refresh(currentRole);
        return roleRepository.findOne(id);
    }

    public Role updateRolePermissions(Role role, Collection<Integer> permissionIds) {
        Map<Integer, RolePermission> currentPermissions = role.getRolePermissions().stream()
                .collect(Collectors.toMap(gp -> gp.getPermission().getId(), gp -> gp));

        Map<Integer, Permission> permissions = permissionsRepository.findAllByIdIn(permissionIds).stream()
                .collect(Collectors.toMap(Permission::getId, p -> p));

        List<RolePermission> permissionsToAdd = permissionIds.stream().filter(i -> !currentPermissions.containsKey(i))
                .map(i -> mapRolePermission(role, permissions.get(i))).collect(Collectors.toList());

        List<RolePermission> permissionsToDelete = currentPermissions.entrySet().stream()
                .filter(e -> !permissionIds.contains(e.getKey())).map(Map.Entry::getValue).collect(Collectors.toList());

        rolePermissionRepository.save(permissionsToAdd);
        rolePermissionRepository.delete(permissionsToDelete);
        rolePermissionRepository.flush();
        roleRepository.refresh(role);
        return roleRepository.findOne(role.getId());
    }

    protected Role retrieveRole(final Collection<Role> roles, final Integer roleId, boolean recursively) {
        Role foundRole = null;
        if (roles != null) {
            for (final Role role : roles) {
                if (role.getId() == roleId) {
                    foundRole = role;
                    break;
                }
                if (recursively) {
                    foundRole = retrieveRole(role.getOwnedRoles(), roleId, false);
                }
                if (foundRole != null) {
                    break;
                }
            }
        }
        return foundRole;
    }

    private RolePermission mapRolePermission(Role role, Permission permission) {
        final RolePermission rolePermission = new RolePermission();
        rolePermission.setRole(role);
        rolePermission.setPermission(permission);

        return rolePermission;
    }

    private RoleOwnership mapRoleOwnership(Role role, Role ownedRole) {
        final RoleOwnership roleOwnership = new RoleOwnership();
        roleOwnership.setParentRoleId(role);
        roleOwnership.setOwnedRoleId(ownedRole);
        return roleOwnership;
    }

    public long countAll(Boolean ignoreChangePassword) {
    	long rolesCount = roleRepository.count();
    	
    	if (ignoreChangePassword != null && ignoreChangePassword) {
    		--rolesCount;
    	}
    	return rolesCount;
    }
}
