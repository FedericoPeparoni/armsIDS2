package ca.ids.abms.modules.selfcareportal.flightcostcalculation;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/flight-cost-calculation")
public class SCFlightCostCalculationController {

    private final Logger log = LoggerFactory.getLogger(SCFlightCostCalculationController.class);
    private final SCFlightCostCalculationService flightCostCalculationService;

    public SCFlightCostCalculationController(SCFlightCostCalculationService flightCostCalculationService) {
        this.flightCostCalculationService = flightCostCalculationService;
    }

    @RequestMapping(method = RequestMethod.POST)
    public FlightMovement calculate(@Valid @RequestBody SCFlightCostCalculation flightCostCalculation) throws FlightMovementBuilderException {
        log.debug("REST request to calculate a cost of flight : {}", flightCostCalculation);
        return flightCostCalculationService.calculate(flightCostCalculation);
    }
}
