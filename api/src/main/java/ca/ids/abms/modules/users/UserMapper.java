package ca.ids.abms.modules.users;

import ca.ids.abms.modules.selfcareportal.userregistration.SCUserRegistrationViewModel;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ca.ids.abms.modules.permissions.Permission;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.roles.RoleOwnership;
import ca.ids.abms.modules.roles.RoleOwnershipViewModel;
import ca.ids.abms.modules.roles.RolePermission;
import ca.ids.abms.modules.roles.RolePermissionViewModel;
import ca.ids.abms.modules.roles.RoleViewModel;
import org.mapstruct.MappingTarget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Mapper
public interface UserMapper {

    List<UserViewModel> toViewModel(Iterable<User> users);

    UserViewModel toViewModel(User user);

    List<UserViewModelLight> toViewModelLight(Iterable<User> users);

    UserViewModelLight toViewModelLight(User user);
    
    default Collection<String> mapPermissions(Collection<Permission> permissions) {
        return permissions.stream().map(Permission::getName).collect(Collectors.toList());
    }

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "billings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "passwordHistory", ignore = true)
    @Mapping(target = "emailActivationKey", ignore = true)
    @Mapping(target = "activationKeyExpiration", ignore = true)
    @Mapping(target = "temporaryPassword", ignore = true)
    @Mapping(target = "temporaryPasswordExpiration", ignore = true)
    @Mapping(target = "selfCarePortalApprovalRequest", ignore = true)
    User toModel(UserViewModel dto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "billings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "passwordHistory", ignore = true)
    @Mapping(target = "emailActivationKey", ignore = true)
    @Mapping(target = "activationKeyExpiration", ignore = true)
    @Mapping(target = "temporaryPassword", ignore = true)
    @Mapping(target = "temporaryPasswordExpiration", ignore = true)
    @Mapping(target = "selfCarePortalApprovalRequest", ignore = true)
    User toModel(SCUserRegistrationViewModel dto);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "billings", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "billingLedgers", ignore = true)
    @Mapping(target = "passwordHistory", ignore = true)
    @Mapping(target = "emailActivationKey", ignore = true)
    @Mapping(target = "activationKeyExpiration", ignore = true)
    @Mapping(target = "temporaryPassword", ignore = true)
    @Mapping(target = "temporaryPasswordExpiration", ignore = true)
    @Mapping(target = "selfCarePortalApprovalRequest", ignore = true)
    @Mapping(target = "login", ignore = true)
    @Mapping(target = "email", ignore = true)  
    @Mapping(target = "password", ignore = true)    
    @Mapping(target = "contactInformation", ignore = true)   
    @Mapping(target = "smsNumber", ignore = true)       
    @Mapping(target = "billingCenter", ignore = true)    
    @Mapping(target = "language", ignore = true)    
    @Mapping(target = "jobTitle", ignore = true)   
    @Mapping(target = "isSelfcareUser", ignore = true)   
    @Mapping(target = "forcePasswordChange", ignore = true)    
    @Mapping(target = "registrationStatus", ignore = true)    
    User toModel(UserViewModelLight dto);
    
    Collection<RoleViewModel> toViewModel(Collection<Role> roles);

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

    @Mapping(target = "groups", ignore = true)
    UserCsvExportModel toCsvModel(User item);

    List<UserCsvExportModel> toCsvModel(Iterable<User> items);

    @AfterMapping
    default void resolveCsvExportModel(final User source, @MappingTarget UserCsvExportModel target) {
        List<String> groups =  new ArrayList<>();
        for (Role role: source.getRoles()) {
            groups.add(role.getName());
        }
        target.setGroups(groups.toString().replaceAll("[\\[\\]]", ""));
    }
}
