package ca.ids.abms.modules.roles;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface RoleOwnershipRepository extends JpaRepository<RoleOwnership, Integer> {

    Set<RoleOwnership> getAllRoleOwnershipByOwnedRoleId (Role ownedRoleId);
}
