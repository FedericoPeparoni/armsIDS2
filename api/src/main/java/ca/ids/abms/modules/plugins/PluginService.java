package ca.ids.abms.modules.plugins;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.plugins.utility.PluginFiltersSpecification;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.plugins.PluginKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@SuppressWarnings("WeakerAccess")
public class PluginService {

    private static final Logger LOG = LoggerFactory.getLogger(PluginService.class);

    private final PluginRepository pluginRepository;

    private final SystemConfigurationService systemConfigurationService;

    public PluginService(final PluginRepository pluginRepository,
                         final SystemConfigurationService systemConfigurationService) {
        this.pluginRepository = pluginRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Return all visible plugins that are registered within the database.
     *
     * @param search text search
     * @param pageable pageable properties
     * @return page containing plugins
     */
    @Transactional(readOnly = true)
    public Page<Plugin> findAll(final String search, final Pageable pageable) {
        return this.findAll(search, pageable, true);
    }

    /**
     * Return all plugins that are registered within the database respecting visible parameter. If
     * visible is null, return all plugins both visible and not visible. If visible parameter is
     * true, return only organization specific and cross organization plugins.
     *
     * @param search text search
     * @param pageable pageable properties
     * @param visible visible plugin query, null for all
     * @return page containing plugins
     */
    @Transactional(readOnly = true)
    public Page<Plugin> findAll(final String search, final Pageable pageable, final Boolean visible) {

        // build filter by searchable text
        FiltersSpecification.Builder builder = new FiltersSpecification.Builder(search);

        // create plugin specific filter specifications from general filter builder
        // this will limit visible plugins based on visibility param and organization name
        PluginFiltersSpecification pluginFiltersSpecification = new PluginFiltersSpecification(builder, visible,
            systemConfigurationService.getValue(SystemConfigurationItemName.ORGANISATION_NAME));

        // find all based on filter specifications and pageable
        return pluginRepository.findAll(pluginFiltersSpecification, pageable);
    }

    /**
     * Returns boolean signifying if plugin is enabled
     *
     * @param pluginKey a plugin's key
     * @return boolean getEnabled or false if plugin is not found
     */
    @Transactional(readOnly = true)
    public Boolean isEnabled(final PluginKey pluginKey) {
        Plugin plugin = pluginRepository.findOneByKey(pluginKey);

        return plugin != null ? plugin.getEnabled() : false;
    }

    /**
     * Updates plugin properties by existing id.
     *
     * @param id existing plugin id
     * @param plugin plugin properties to update
     * @return updated plugin
     */
    public Plugin update(final Integer id, final Plugin plugin) {
        LOG.debug("Request to update Plugin : {}", plugin);
        Plugin updatedPlugin;
        try {
            Plugin existingPlugin = pluginRepository.getOne(id);

            ModelUtils.checkVersionIfComparables(plugin, existingPlugin);
            ModelUtils.merge(plugin, existingPlugin);

            updatedPlugin = pluginRepository.save(existingPlugin);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return updatedPlugin;
    }

    public long countAllByOrganization(final boolean visible) {
        if (visible)
            return pluginRepository.countAllVisibleByOrganization(systemConfigurationService.getValue(SystemConfigurationItemName.ORGANISATION_NAME));
        else
            return pluginRepository.countAllByOrganization(systemConfigurationService.getValue(SystemConfigurationItemName.ORGANISATION_NAME));
    }
}
