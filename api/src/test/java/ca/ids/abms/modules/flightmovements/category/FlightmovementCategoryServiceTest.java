package ca.ids.abms.modules.flightmovements.category;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.routesegments.RouteSegmentRepository;

public class FlightmovementCategoryServiceTest {
	
	private FlightmovementCategoryRepository flightmovementCategoryRepository;
	private FlightmovementCategoryService flightmovementCategoryService;

	@Before
    public void setup() {

		flightmovementCategoryRepository = mock(FlightmovementCategoryRepository.class);

        flightmovementCategoryService = new FlightmovementCategoryService(flightmovementCategoryRepository);

    }
	@Test
	public void getOneTest() {
		FlightmovementCategory fm = new FlightmovementCategory();
		fm.setId(2);
		fm.setName("TEST");
		fm.setCreatedBy("admin");
	}

}
