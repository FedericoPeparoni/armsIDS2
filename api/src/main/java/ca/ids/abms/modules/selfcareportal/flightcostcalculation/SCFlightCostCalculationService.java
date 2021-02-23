package ca.ids.abms.modules.selfcareportal.flightcostcalculation;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class SCFlightCostCalculationService {

    private final Logger log = LoggerFactory.getLogger(SCFlightCostCalculationService.class);
    private FlightMovementService flightMovementService;
    private FlightMovementBuilder flightMovementBuilder;

    private static final String DEFAULT_DEPARTURE_TIME = "0000";

    public SCFlightCostCalculationService(FlightMovementService flightMovementService, FlightMovementBuilder flightMovementBuilder) {
        this.flightMovementService = flightMovementService;
        this.flightMovementBuilder = flightMovementBuilder;
    }

    public FlightMovement calculate(SCFlightCostCalculation flightCostCalculation) throws FlightMovementBuilderException {
        log.debug("Request to calculate a flight : {}", flightCostCalculation);

        FlightMovement fm = new FlightMovement();

        if (flightCostCalculation.getSpeed() != null) {
            fm.setCruisingSpeedOrMachNumber(flightCostCalculation.getSpeed());
        }

        if (flightCostCalculation.getEstimatedElapsedTime() != null) {
            fm.setEstimatedElapsedTime(flightCostCalculation.getEstimatedElapsedTime());
        }

        fm.setAircraftType(flightCostCalculation.getAircraftType());
        fm.setItem18RegNum(flightCostCalculation.getRegistrationNumber());
        fm.setDestAd(flightCostCalculation.getDestAerodrome());
        fm.setDepAd(flightCostCalculation.getDepAerodrome());
        fm.setFplRoute(flightCostCalculation.getRoute());
        fm.setDateOfFlight(LocalDateTime.now().toLocalDate().atStartOfDay());
        fm.setBillingDate(LocalDateTime.now().toLocalDate().atStartOfDay());
        fm.setDepTime(DEFAULT_DEPARTURE_TIME);

        boolean checkRegNum = flightCostCalculation.getRegistrationNumber() != null;

        flightMovementService.checkValidValues(fm);
        flightMovementBuilder.calculateFlightMovement(fm, false, checkRegNum);

        return fm;

    }
}
