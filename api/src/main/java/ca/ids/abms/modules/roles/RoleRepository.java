package ca.ids.abms.modules.roles;

import java.util.Collection;

import ca.ids.abms.config.db.ABMSRepository;

public interface RoleRepository extends ABMSRepository<Role, Integer> {

    Collection<Role> findAllByIdIn(Collection<Integer> ids);

    Role getOneByNameIgnoreCase(String name);

    @Override
    void refresh(Role entity);
}
