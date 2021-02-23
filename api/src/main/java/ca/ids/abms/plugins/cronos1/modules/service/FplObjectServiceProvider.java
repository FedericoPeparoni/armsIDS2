package ca.ids.abms.plugins.cronos1.modules.service;

import ca.ids.abms.modules.spatiareader.FlightPlansServiceProvider;
import ca.ids.abms.modules.spatiareader.entity.FplObject;
import ca.ids.abms.plugins.PluginKey;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class FplObjectServiceProvider extends FlightPlansServiceProvider {

    private final FplObjectService fplObjectService;

    public FplObjectServiceProvider (final FplObjectService fplObjectService) {
        super(PluginKey.CRONOS_1);
        this.fplObjectService = fplObjectService;
    }

    @Override
    public List<FplObject> findAll(LocalDateTime minDate) {
        return fplObjectService.findAll(minDate);
    }
}
