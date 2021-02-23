package ca.ids.abms.modules.roles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.ids.abms.config.db.FiltersSpecification;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.permissions.PermissionsRepository;
import ca.ids.abms.modules.users.UserRoleRepository;

public class RoleServiceTest {

    private RoleRepository roleRepository;
    private RoleOwnershipRepository roleOwnershipRepository;
    private RoleService roleService;
    private RolePermissionRepository rolePermissionRepository;
    private PermissionsRepository permissionsRepository;
    private UserRoleRepository userRoleRepository;

    @Before
    public void setup() {
        roleRepository = mock(RoleRepository.class);
        roleOwnershipRepository = mock(RoleOwnershipRepository.class);
        rolePermissionRepository = mock(RolePermissionRepository.class);
        permissionsRepository = mock(PermissionsRepository.class);
        userRoleRepository = mock(UserRoleRepository.class);
        roleService = new RoleService(roleRepository, rolePermissionRepository, permissionsRepository, roleOwnershipRepository,userRoleRepository);
    }

    @Test
    public void getAllRoles() throws Exception {
        Role role = new Role();
        role.setId(1);
        Role ownedRole = new Role();
        ownedRole.setId(2);
        Set<RolePermission> rolePermissions = new HashSet<>();
        rolePermissions.add(new RolePermission());
        role.setRolePermissions(rolePermissions);
        RoleOwnership roleOwnership = new RoleOwnership();
        roleOwnership.setOwnedRoleId(ownedRole);
        Set<RoleOwnership> roleOwnerships = new HashSet<>();
        roleOwnerships.add(roleOwnership);
        role.setRoleOwnership(roleOwnerships);

        List<Role> roles = Collections.singletonList(role);
        when(roleRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(roles));
        Pageable pageable = new PageRequest(1, 20);
        Page<Role> results = roleService.getAllRoles("dd", false, pageable);
        assertThat(results.getTotalElements()).isEqualTo(roles.size());

        Role result = (Role) results.getContent().get(0);

        assertThat(result.getId() == role.getId());
        assertThat(result.getRolePermissions().size() == role.getRolePermissions().size());
        assertThat(result.getPermissions().size() == rolePermissions.toArray().length);
        assertThat(result.getRoleOwnership().size() == role.getOwnedRoles().size());
        RoleOwnership resultRoleOwnership = (RoleOwnership)result.getRoleOwnership().toArray()[0];
        assertThat(resultRoleOwnership.getOwnedRoleId() != null);
        assertThat(resultRoleOwnership.getOwnedRoleId().getId() == ownedRole.getId());
    }

    @Test
    public void getRoleById() throws Exception {
        Role role = new Role();
        role.setId(1);

        when(roleRepository.getOne(any()))
            .thenReturn(role);

        Role result = roleService.getRoleById(1);
        assertThat(result).isEqualTo(role);
    }

    @Test
    public void createRole() throws Exception {
        Role role = new Role();
        role.setName("name");

        when(roleRepository.save(any(Role.class)))
            .thenReturn(role);

        Role result = roleService.createRole(role);
        assertThat(result.getName()).isEqualTo(role.getName());
    }

    @Test
    public void updateRole() throws Exception {
        Role existingRole = new Role();
        existingRole.setName("name");

        Role role = new Role();
        role.setName("new name");

        when(roleRepository.getOne(any()))
            .thenReturn(existingRole);

        when(roleRepository.save(any(Role.class)))
            .thenReturn(existingRole);

        Role result = roleService.updateRole(1,role);

        assertThat(result.getName()).isEqualTo("new name");
    }

    @Test
    public void deleteRole() throws Exception {
        Role existingRole = new Role();
        existingRole.setId(1);
        when(roleRepository.getOne(any()))
            .thenReturn(existingRole);
        roleService.deleteRole(1);
        verify(roleRepository).delete(any(Integer.class));
    }

}
