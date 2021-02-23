package ca.ids.abms.modules.permissions;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Transactional
public class PermissionService {

    private final PermissionsRepository permissionsRepository;

    public PermissionService(PermissionsRepository permissionsRepository) {
        this.permissionsRepository = permissionsRepository;
    }

    @Transactional(readOnly = true)
    public Collection<Permission> getAllPermissions() {
        return permissionsRepository.findAll();
    }

}
