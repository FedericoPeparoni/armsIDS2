package ca.ids.abms.modules.system;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface SystemConfigurationRepository extends JpaRepository<SystemConfiguration, Integer> {

    @Query("select sc from SystemConfiguration sc, SystemItemType sit "
            + "where sc.itemClass.id = sit.id and sit.plugin is null "
            + "order by sit.name asc, sc.itemName asc")
    Page<SystemConfiguration> findSystemConfigurationsByOrderByItemClassAscItemNameAsc(Pageable pageable);

    @Query("select sc from SystemConfiguration sc, SystemItemType sit "
        + "where sc.itemClass.id = sit.id and sit.plugin.id = :pluginId "
        + "order by sit.name asc, sc.itemName asc")
    Page<SystemConfiguration> findPluginConfigurationsByOrderByItemClassAscItemNameAsc(
        @Param("pluginId") Integer pluginId, Pageable pageable);

    Page<SystemConfiguration> findByClientStorageForbiddenFalse(Pageable pageable);

    SystemConfiguration getOneByItemName(final String itemName);

    @Query(value = "select * from abms.system_configurations where item_name like '%Language%' order by item_name",
        nativeQuery = true)
    List<SystemConfiguration> getNoauthLanguagesSystemConfigurations();

    @Query(value = "select sc from SystemConfiguration sc where sc.itemName = 'MTOW unit of measure' or sc.itemName = 'Distance unit of measure' order by sc.itemName")
    List<SystemConfiguration> getNoauthUnitsOfMeasureSystemConfigurations();

    @Query(value = "select sc from SystemConfiguration sc where sc.itemName = 'Air navigation charges currency'")
    SystemConfiguration getNoauthAirNavigationChargesCurrency();

    @Query(value = "select sc from SystemConfiguration sc where sc.itemName = 'ANSP currency'")
    SystemConfiguration getNoauthAnspCurrency();

    @Query(value = "select sc from SystemConfiguration sc where sc.itemName like '%Password%'")
    List<SystemConfiguration> getPasswordSettings();

    @Query(value = "select sc from SystemConfiguration sc where sc.itemName = :name")
    List<SystemConfiguration> getSelfCareSettings(@Param("name") String name);

}
