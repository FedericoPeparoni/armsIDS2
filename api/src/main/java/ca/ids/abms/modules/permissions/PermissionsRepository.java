package ca.ids.abms.modules.permissions;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

public interface PermissionsRepository extends JpaRepository<Permission, Integer> {

    Collection<Permission> findAllByIdIn(Collection<Integer> ids);
}
