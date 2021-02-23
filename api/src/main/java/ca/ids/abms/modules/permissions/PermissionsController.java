package ca.ids.abms.modules.permissions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/permissions")
public class PermissionsController {

    private final PermissionService permissionService;
    private final PermissionMapper permissionMapper;

    public PermissionsController(PermissionService permissionService, PermissionMapper permissionMapper) {
        this.permissionService = permissionService;
        this.permissionMapper = permissionMapper;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<Collection<PermissionViewModel>> getAllPermissions() {
        Collection<PermissionViewModel> permissions = permissionMapper.toViewModel(permissionService.getAllPermissions());

        return ResponseEntity.ok(permissions);
    }



}
