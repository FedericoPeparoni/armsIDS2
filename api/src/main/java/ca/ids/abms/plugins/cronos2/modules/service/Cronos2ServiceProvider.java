package ca.ids.abms.plugins.cronos2.modules.service;

import ca.ids.abms.modules.spatiareader.FlightPlansServiceProvider;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class Cronos2ServiceProvider extends FlightPlansServiceProvider {

    private final Cronos2Service cronos2Service;

    public Cronos2ServiceProvider(final Cronos2Service cronos2Service) {
        super(PluginKey.CRONOS_2);
        this.cronos2Service = cronos2Service;
    }

    @Override
    public List<FplObject> findAll(LocalDateTime minDate) {
        return cronos2Service.findAll(minDate);
    }
}
