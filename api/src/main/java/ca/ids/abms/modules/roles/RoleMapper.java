package ca.ids.abms.modules.roles;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Mapper
public interface RoleMapper {

    Collection<RoleViewModel> toViewModel(Collection<Role> roles);

    List<RoleViewModel> toViewModel(Iterable<Role> items);

    @Mapping(target = "permissions", source = "rolePermissions")
    @Mapping(target = "ownedRoles", source = "roleOwnership")
    RoleViewModel toViewModel(Role role);

    @Mapping(target = "id", source = "permission.id")
    @Mapping(target = "name", source = "permission.name")
    RolePermissionViewModel toViewModel(RolePermission rolePermission);

    @Mapping(target = "id", source = "ownedRoleId.id")
    @Mapping(target = "name", source = "ownedRoleId.name")
    RoleOwnershipViewModel toViewModel(RoleOwnership roleOwnership);
    
    default Collection<Integer> getPermissionIds(Collection<RolePermissionViewModel> rolePermissionViewModels) {
        return rolePermissionViewModels.stream().map(RolePermissionViewModel::getId).collect(Collectors.toList());
    }
    
    default Collection<Integer> getOwnedRolesIds(Collection<RoleOwnershipViewModel> roleOwnershipViewModel) {
        return roleOwnershipViewModel.stream().map(RoleOwnershipViewModel::getId).collect(Collectors.toList());
    }

    @Mapping(target = "rolePermissions", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "roleOwnership", ignore = true)
    @Mapping(target = "ownedRoles", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toModel(RoleViewModel dto);

    RoleCsvExportModel toCsvModel(Role item);

    List<RoleCsvExportModel> toCsvModel(Iterable<Role> items);
}
