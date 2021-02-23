package ca.ids.abms.modules.recalculation;

import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.jobs.ItemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FLMRecalculationProcessor implements ItemProcessor<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(FLMRecalculationProcessor.class);

    private final FlightMovementService flightMovementService;

    FLMRecalculationProcessor(final FlightMovementService flightMovementService) {
        this.flightMovementService = flightMovementService;
    }

    @Override
    public Integer processItem(final Integer id) {
        LOG.debug("Charges recalculation in progress for the flight movement ID {}", id);
        final Boolean feedback = this.flightMovementService.calculateCharges(id);

        if (feedback != null && feedback) {
            LOG.debug("The flight movement ID {} has been updated", id);
            return id;
        } else {
            LOG.debug("The flight movement ID {} has NOT been updated", id);
            return null;
        }
    }
}
