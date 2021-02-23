package ca.ids.abms.modules.spatiareader;

import ca.ids.abms.modules.common.services.AbstractPluginService;
import ca.ids.abms.modules.spatiareader.entity.FplObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class FlightPlansService extends AbstractPluginService<FlightPlansServiceProvider> {

    private static final Logger LOG = LoggerFactory.getLogger(FlightPlansService.class);
        
    public List<FplObject> getFplObjectsStartingFromDate (final LocalDateTime startingDate) {
        List<FplObject> result = new ArrayList<>();
        try {
            for (final FlightPlansServiceProvider flightPlansServiceProvider : super.getPluginServiceProviders()) {
                result.addAll(flightPlansServiceProvider.findAll(startingDate));
            }
        } catch(CannotGetJdbcConnectionException ex) {
            LOG.error("Cronos DB is not available {}",ex.getMessage());
        }
        return result;
    }

    public boolean thereIsACronosPluginEnabled() {
        return !super.getPluginServiceProviders().isEmpty();
    }
}
