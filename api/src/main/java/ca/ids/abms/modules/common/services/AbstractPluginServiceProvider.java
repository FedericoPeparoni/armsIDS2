package ca.ids.abms.modules.common.services;

import ca.ids.abms.plugins.PluginKey;

public abstract class AbstractPluginServiceProvider {

    private PluginKey pluginKey;

    public AbstractPluginServiceProvider(PluginKey pluginKey) {
        this.pluginKey = pluginKey;
    }

    public PluginKey pluginKey() {
        return pluginKey;
    }
}
