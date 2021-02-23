package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by c.talpa on 28/03/2017.
 */
public class FlightMovementAircraftServiceTest {

    private AircraftTypeService aircraftTypeService;

    private AircraftRegistrationService aircraftRegistrationService;

    private UnspecifiedAircraftTypeService unspecifiedAircrafttypeService;

    private FlightMovementAircraftService flightMovementAircraftService;

    private AircraftRegistration aircraftRegistration;

    private AircraftType aircraftType;
    
    private FlightMovementRepository flightMovementRepository;
    
    private FlightMovement flightMovement;

    @Before
    public void setup() {
        aircraftTypeService=mock(AircraftTypeService.class);
        aircraftRegistrationService= mock(AircraftRegistrationService.class);
        unspecifiedAircrafttypeService=mock(UnspecifiedAircraftTypeService.class);

        flightMovementAircraftService= new FlightMovementAircraftService(aircraftTypeService,aircraftRegistrationService,unspecifiedAircrafttypeService,flightMovementRepository);

        aircraftRegistration= new AircraftRegistration();
        aircraftType= new AircraftType();
        aircraftType.setAircraftType("A380");
        aircraftRegistration.setAircraftType(aircraftType);

        flightMovement = mock(FlightMovement.class);
        flightMovementRepository=mock(FlightMovementRepository.class);
         
    }

    @Test
    public void testCheckAndResolveAircraftType(){
        FlightMovement fm = new FlightMovement();
    	// Test Case 1
        String registrationNumber=null;
        String aircraftTypeName="   ";
        String otherInfo=null;
        String item18Field=null;
        LocalDateTime dateOfFlight=null;

        when(flightMovementRepository.findLatestByItem18RegAircraftTypeKnown(registrationNumber, dateOfFlight)).thenReturn(null);
        String aircraftTypeResole=flightMovementAircraftService.checkAndResolveAircraftType(fm);

        Assert.assertTrue(aircraftTypeResole==null);

       
        
        // Test Case 2 - we have aircraft registration but we don't have aircraftType
        fm.setItem18RegNum("TALPA2");
        aircraftTypeName=" ";
        otherInfo="  ";
        item18Field="  ";
        aircraftRegistration.setAircraftType(null);
        when(aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber("TALPA2", null)).thenReturn(aircraftRegistration);

        aircraftTypeResole=flightMovementAircraftService.checkAndResolveAircraftType(fm);
        Assert.assertTrue(aircraftTypeResole==null);

        // Test Case 3 - we have aircraft registration
        registrationNumber="TALPA2";
        aircraftTypeName=" ";
        otherInfo="  ";
        item18Field="  ";
        aircraftRegistration.setAircraftType(aircraftType);
        when(aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber("TALPA2", null)).thenReturn(aircraftRegistration);

        aircraftTypeResole=flightMovementAircraftService.checkAndResolveAircraftType(fm);
        Assert.assertTrue(aircraftTypeResole.equalsIgnoreCase(aircraftType.getAircraftType()));



        // Test Case 4 - we have aircraft type name
        registrationNumber="  ";
        fm.setAircraftType("A380 ");
        otherInfo="  ";
        item18Field="  ";
        when(aircraftTypeService.findByAircraftType(aircraftTypeName)).thenReturn(aircraftType);
        aircraftTypeResole=flightMovementAircraftService.checkAndResolveAircraftType(fm);
        Assert.assertTrue(aircraftTypeResole.equalsIgnoreCase(aircraftType.getAircraftType()));
        
    }
}
