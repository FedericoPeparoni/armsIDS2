package ca.ids.abms.modules.spatiareader;

import ca.ids.abms.modules.common.services.AbstractPluginServiceProvider;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.plugins.PluginKey;

import java.time.LocalDateTime;
import java.util.List;

public abstract class FlightPlansServiceProvider extends AbstractPluginServiceProvider {

    public FlightPlansServiceProvider(PluginKey pluginKey) {
        super(pluginKey);
    }

    public abstract List<FplObject> findAll(final LocalDateTime minDate);
}
