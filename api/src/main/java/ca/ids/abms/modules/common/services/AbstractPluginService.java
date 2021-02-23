package ca.ids.abms.modules.common.services;

import ca.ids.abms.modules.plugins.Plugin;
import ca.ids.abms.modules.plugins.PluginRepository;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class AbstractPluginService<T extends AbstractPluginServiceProvider> {

    private List<T> providers;

    /**
     * Autowired plugin repository used to validate if plugins enabled or not.
     */
    @Autowired
    private PluginRepository pluginRepository;

    /**
     * Autowired optioanl Plugin Serivice Providers from Application Context.
     *
     * @param providers Plugin Service Providers
     */
    @Autowired(required = false)
    protected void setPluginServiceProviders(final List<T> providers) {
        this.providers = providers;
    }

    /**
     * Returns the list of providers that are enabled.
     *
     * @return List of Plugin Service Providers
     */
    protected List<T> getPluginServiceProviders() {
        if (this.providers != null && !this.providers.isEmpty()) {
            return this.providers.stream()
                .filter(t -> this.isEnabled(t.pluginKey()))
                .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Determine if provided plugin Id exists and is enabled using
     * the plugin repository.
     *
     * @param pluginKey plugin key
     * @return true if exists and enabled
     */
    private boolean isEnabled(PluginKey pluginKey) {
        Plugin provider = this.pluginRepository.findOneByKey(pluginKey);
        return provider != null && provider.getEnabled();
    }
}
