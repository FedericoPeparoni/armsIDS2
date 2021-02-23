package ca.ids.abms.modules.permissions;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;

@Mapper
public interface PermissionMapper {

    Collection<PermissionViewModel> toViewModel(Collection<Permission> permissions);

    PermissionViewModel toViewModel(Permission permission);

    @Mapping(target = "rolePermissions", ignore = true)
    Permission toModel(PermissionViewModel vm);
}
