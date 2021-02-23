package ca.ids.abms.modules.recalculation;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.jobs.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FLMReconciliationProcessor implements ItemProcessor<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(FLMReconciliationProcessor.class);

    private final FlightMovementService flightMovementService;

    FLMReconciliationProcessor(final FlightMovementService flightMovementService) {
        this.flightMovementService = flightMovementService;
    }

    @Override
    public Integer processItem(final Integer id) throws FlightMovementBuilderException {
        LOG.debug("Reconciliation in progress for the flight movement ID {}", id);

        final FlightMovement flightMovement = flightMovementService.reconcileFlightMovementById(id);
        if(flightMovement != null) {
            return flightMovement.getId();
        } else {
            return null;
        }
    }
}
