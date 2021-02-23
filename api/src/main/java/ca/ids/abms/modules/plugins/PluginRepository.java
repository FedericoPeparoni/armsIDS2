package ca.ids.abms.modules.plugins;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PluginRepository extends ABMSRepository<Plugin, Integer> {

    Plugin findOneByKey(PluginKey key);

    @Query("SELECT count(*) FROM Plugin WHERE site = :organizationName or site is null and visible = true")
    long countAllVisibleByOrganization(@Param("organizationName") final String organizationName);

    @Query("SELECT count(*) FROM Plugin WHERE site = :organizationName or site is null")
    long countAllByOrganization(@Param("organizationName") final String organizationName);
}
