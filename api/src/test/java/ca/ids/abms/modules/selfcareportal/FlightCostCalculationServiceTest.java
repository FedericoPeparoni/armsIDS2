package ca.ids.abms.modules.selfcareportal;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.selfcareportal.flightcostcalculation.SCFlightCostCalculation;
import ca.ids.abms.modules.selfcareportal.flightcostcalculation.SCFlightCostCalculationService;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightCostCalculationServiceTest {

    private FlightMovementBuilder flightMovementBuilder;
    private SCFlightCostCalculationService flightCostCalculationService;

    @Before
    public void setup() {
        FlightMovementService flightMovementService = mock(FlightMovementService.class);
        flightMovementBuilder = mock(FlightMovementBuilder.class);
        flightCostCalculationService = new SCFlightCostCalculationService(flightMovementService, flightMovementBuilder);
    }

    @Test
    public void calculateFlightCost() throws Exception {
        SCFlightCostCalculation flightCostCalculation = new SCFlightCostCalculation();
        flightCostCalculation.setAircraftType("A000");
        flightCostCalculation.setDepAerodrome("FALA");
        flightCostCalculation.setDestAerodrome("FAOR");
        flightCostCalculation.setEstimatedElapsedTime("0111");
        flightCostCalculation.setRoute("DCT");
        flightCostCalculation.setSpeed("FAST");

        FlightMovement fm = new FlightMovement();
        fm.setAircraftType(flightCostCalculation.getAircraftType());
        fm.setItem18RegNum(flightCostCalculation.getRegistrationNumber());
        fm.setCruisingSpeedOrMachNumber(flightCostCalculation.getSpeed());
        fm.setEstimatedElapsedTime(flightCostCalculation.getEstimatedElapsedTime());
        fm.setDestAd(flightCostCalculation.getDestAerodrome());
        fm.setDepAd(flightCostCalculation.getDepAerodrome());
        fm.setFplRoute(flightCostCalculation.getRoute());

        when(flightMovementBuilder.calculateFlightMovement(fm, false, false))
            .thenReturn(fm);

        FlightMovement flightMovement = flightCostCalculationService.calculate(flightCostCalculation);
        assertThat(flightMovement.getAircraftType()).isEqualTo(flightCostCalculation.getAircraftType());
    }
}
