package ca.ids.abms.modules.flightmovementsbuilder.utility.charges;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.flightmovements.FlightMovement;

@Component
public class DefaultAdditionalChargeProvider implements AdditionalChargeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAdditionalChargeProvider.class);

    @Override
    public void initialize(final FlightMovement flightMovement) {
        LOG.trace("Using default implementation of initialize(); nothing will happen");
    }

    @Override
    public void calculate(final FlightMovement flightMovement) {
        LOG.trace("Using default implementation of calculate(); nothing will happen");
    }
}
