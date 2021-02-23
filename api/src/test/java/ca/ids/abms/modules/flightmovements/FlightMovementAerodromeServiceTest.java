package ca.ids.abms.modules.flightmovements;

import java.util.ArrayList;
import java.util.List;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.util.GeometryUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;

import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FlightMovementAerodromeServiceTest {

    private AerodromeService aerodromeService;
    private UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private FlightMovementAerodromeService flightMovementAerodromeService;
    private Aerodrome aerodrome;
    private UnspecifiedDepartureDestinationLocation unspecifiedLocation;
    private AirspaceService airspaceService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {

        NavDBUtils navdbUtils = mock(NavDBUtils.class);

        aerodromeService = mock(AerodromeService.class);
        airspaceService = mock(AirspaceService.class);
        unspecifiedDepartureDestinationLocationService = mock(UnspecifiedDepartureDestinationLocationService.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        aerodrome = new Aerodrome();
        unspecifiedLocation = new UnspecifiedDepartureDestinationLocation();

        flightMovementAerodromeService = new FlightMovementAerodromeService(aerodromeService,
            unspecifiedDepartureDestinationLocationService, navdbUtils, airspaceService, systemConfigurationService);
    }

    @Test
    public void testCheckAerodromeIdentifier(){
        // Test Case 1
        String aerodromeIdentifier=null;
        aerodromeIdentifier = flightMovementAerodromeService.checkAerodromeIdentifier(aerodromeIdentifier);
        Assert.assertNull(aerodromeIdentifier);

        // Test case 2
        aerodromeIdentifier="ZZZZ";
        when(aerodromeService.checkAerodromeIdentifier("ZZZZ", true, false)).thenReturn("ZZZZ");
        aerodromeIdentifier = flightMovementAerodromeService.checkAerodromeIdentifier(aerodromeIdentifier);
        Assert.assertTrue(aerodromeIdentifier.equalsIgnoreCase("ZZZZ"));

        // Test case 3
        aerodromeIdentifier="FBSK";
        when(aerodromeService.checkAerodromeIdentifier("FBSK", true, false)).thenReturn("FBSK");
        aerodromeIdentifier = flightMovementAerodromeService.checkAerodromeIdentifier(aerodromeIdentifier);
        Assert.assertTrue(aerodromeIdentifier.equalsIgnoreCase("FBSK"));

    }

    @Test
    public void testGetAeroDrome(){

        // Test case 1
        String aerodromeIdentifier=null;
        Aerodrome aerodromeResult=flightMovementAerodromeService.getAeroDrome(aerodromeIdentifier);
        Assert.assertNull(aerodromeResult);

        // Test case 2
        aerodromeIdentifier="ZZZZ";
        aerodromeResult=flightMovementAerodromeService.getAeroDrome(aerodromeIdentifier);
        Assert.assertNull(aerodromeResult);

        // Test case 3
        aerodromeIdentifier="FBSK";
        aerodrome.setAerodromeName("FBSK");
        when(aerodromeService.findAeroDromeByAeroDromeName("FBSK")).thenReturn(aerodrome);
        aerodromeResult=flightMovementAerodromeService.getAeroDrome(aerodromeIdentifier);
        Assert.assertTrue(aerodromeResult.getAerodromeName().equalsIgnoreCase(aerodrome.getAerodromeName()));

    }

    @Test
    public void testResolveAerodrome(){
        // Test case 1
        try {
            String aerodromeIdentifier=null;
            String item18=null;
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertNull(aerodromeResult);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 2
        try {
            String aerodromeIdentifier="FBSK";
            String item18=null;
            when(aerodromeService.checkAerodromeIdentifier(eq(aerodromeIdentifier), any(boolean.class), any(boolean.class)))
                .thenReturn(aerodromeIdentifier);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeIdentifier.equalsIgnoreCase(aerodromeResult));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }


        // Test case 3: we have ZZZZ but on Item18 we have ZZZZ the method throws the exception
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="ZZZZ";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier("FBSK")).thenReturn("FBSK");
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.TRUE);
        }

        // Test case 4: we have ZZZZ but on Item18 we have FBSK and it's in Billing DB
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="FBSK";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier("FBSK")).thenReturn("FBSK");
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeResult.equalsIgnoreCase(item18));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 5 : we have ZZZZ but on Item18 we have LIRF and it's in NAVDB
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="LIRF";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(item18);
            when(aerodromeService.checkAerodromeIdentifier(item18, true, false)).thenReturn(item18);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeResult.equalsIgnoreCase(item18));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }


        // Test case 6 : we have ZZZZ but on Item18 we have DUBA, we have DUBA in UnspecifiedLocation
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="DUBA";
            aerodrome.setAerodromeName("DUBA");
            unspecifiedLocation.setAerodromeIdentifier(aerodrome);
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(null);
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(unspecifiedLocation);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeResult.equalsIgnoreCase(item18));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 7 : we have ZZZZ but on Item18 we have VUMBURA, we don't have in BillingDB and NavDB
        // We can't create Unspecified Location
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="VUMBURA";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(null);
            when(aerodromeService.checkAerodromeIdentifier(item18, true, false)).thenReturn(null);
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(null);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeResult.equalsIgnoreCase(item18));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 8 : we have ZZZZ but on Item18 we have VUMBURA1897S02289E, we don't have in BillingDB and NavDB
        // We can create Unspecified Location
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="VUMBURA1897S02289E";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(null);
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(null);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);
            Assert.assertTrue(aerodromeResult.equalsIgnoreCase("VUMBURA"));

        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 9 : we have ZZZZ but on Item18 we have 1897S02289E, we don't have in BillingDB and NavDB
        // We can't create Unspecified Location
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="1897S02289E";
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(null);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false,false);
            Assert.assertTrue(item18.equalsIgnoreCase(aerodromeResult));
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 10 : we have ZZZZ but on Item18 we don't have good information, we don't have in BillingDB and NavDB
        // We can't create Unspecified Location
        try {
            String aerdromeIdentifier="ZZZZ";
            String item18="1234CARMTALPA";
            when(aerodromeService.checkAerodromeIdentifier(aerdromeIdentifier)).thenReturn(aerdromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(null);
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(null);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerdromeIdentifier, item18, false);

        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.TRUE);
        }

        // Test case 11 : we have ZZZZ but on Item18 we have Delta flight route 
        // We can't create Unspecified Location
        try {
            String aerodromeIdentifier="ZZZZ";
            String item18="XIGERA1310/1320 JEDIBE1335/1345 KADIZORA1355";
            when(aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier)).thenReturn(aerodromeIdentifier);
            when(aerodromeService.checkAerodromeIdentifier(item18)).thenReturn(null);
            when(unspecifiedDepartureDestinationLocationService.findTextIdentifier(item18)).thenReturn(null);
            String aerodromeResult = flightMovementAerodromeService.resolveAerodrome(aerodromeIdentifier, item18, false);

        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.TRUE);
        }
    }

    @Test
    public void testIsCircularRoute(){
        // Test case 1
        try {
            String depAd=null;
            String item18Dep=null;
            String destAd=null;
            String item18Dest=null;
            Boolean returnValue = flightMovementAerodromeService.isCircularRoute(depAd,item18Dep,destAd,item18Dest);
            Assert.assertEquals(Boolean.FALSE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 2
        try {
            String depAd="FBSK";
            String item18Dep=null;
            String destAd="FBSK";
            String item18Dest=null;
            when(aerodromeService.checkAerodromeIdentifier(eq(depAd), any(boolean.class), any(boolean.class))).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRoute(depAd,item18Dep,destAd,item18Dest);
            Assert.assertEquals(Boolean.TRUE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 3
        try {
            String depAd="ZZZZ";
            String item18Dep="FBSK";
            String destAd="FBSK";
            String item18Dest=null;
            when(aerodromeService.checkAerodromeIdentifier(depAd)).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRoute(depAd,item18Dep,destAd,item18Dest);
            Assert.assertEquals(Boolean.TRUE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 4
        try {
            String depAd="ZZZZ";
            String item18Dep="FBSK";
            String destAd="ZZZZ";
            String item18Dest="FBSK";
            when(aerodromeService.checkAerodromeIdentifier(depAd)).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRoute(depAd,item18Dep,destAd,item18Dest);
            Assert.assertEquals(Boolean.TRUE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void testIsCircularRouteDelta(){
        // Test case 1
        try {
            String depAd=null;
            String item18Dep=null;
            String item18Dest=null;
            Boolean returnValue = flightMovementAerodromeService.isCircularRouteDelta(depAd, item18Dep, item18Dest);
            Assert.assertEquals(Boolean.FALSE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 2
        try {
            String depAd="FBSK";
            String item18Dep=null;
            String item18Dest=null;
            when(aerodromeService.checkAerodromeIdentifier(depAd)).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRouteDelta(depAd,item18Dep,item18Dest);
            Assert.assertEquals(Boolean.FALSE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 3
        try {
            String depAd="FBMN";
            String item18Dest="FBSV0800/0810 SELINDA0830/0840 XARANNA0920/0830 STANLEYS0940/0955 FBMN1005";
            String item18Dep=null;
            when(aerodromeService.checkAerodromeIdentifier(eq(depAd), any(boolean.class), any(boolean.class))).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRouteDelta(depAd,item18Dep,item18Dest);
            Assert.assertEquals(Boolean.TRUE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }

        // Test case 4
        try {
            String depAd="ZZZZ";
            String item18Dep="FBMN";
            String item18Dest="FBSV0800/0810 SELINDA0830/0840 XARANNA0920/0830 STANLEYS0940/0955 FBMN1005";
            when(aerodromeService.checkAerodromeIdentifier(depAd)).thenReturn(depAd);
            Boolean returnValue = flightMovementAerodromeService.isCircularRouteDelta(depAd,item18Dep,item18Dest);
            Assert.assertEquals(Boolean.TRUE, returnValue);
        } catch (FlightMovementBuilderException e) {
            Assert.assertTrue(Boolean.FALSE);
        }
    }

    @Test
    public void testIsDomestic(){

        // CASE 1: identifier is null or empty, return false
        Assert.assertFalse(flightMovementAerodromeService.isAerodromeDomestic(null));
        Assert.assertFalse(flightMovementAerodromeService.isAerodromeDomestic("  "));

        // CASE 2: throw IllegalStateException if not system configuration defined
        try {
            flightMovementAerodromeService.isAerodromeDomestic("MOCK");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException ignored) {}

        // CASE 2: coordinates are null or empty, return true
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.AERODROME_NATIONALITY_DETERMINED_BY))
            .thenReturn("Location");
    	Assert.assertTrue(flightMovementAerodromeService.isAerodromeDomestic("MOCK"));

    	final GeometryFactory gf = new GeometryFactory();

        final ArrayList<Coordinate> points = new ArrayList<Coordinate>();
        points.add(new Coordinate(-10, -10));
        points.add(new Coordinate(-10, 10));
        points.add(new Coordinate(10, 10));
        points.add(new Coordinate(10, -10));
        points.add(new Coordinate(-10, -10));

        final Polygon polygon = gf.createPolygon(new LinearRing(new CoordinateArraySequence(points
            .toArray(new Coordinate[0])), gf), null);

        List<String> airspaces = new ArrayList<>();
        airspaces.add(polygon.toText());
        when(airspaceService.getAllAirspaceGeometry()).thenReturn(airspaces);

        // CASE 3: assert identifier that falls within FIR returns true
        String avC = GeometryUtils.formatAviationCoordinate(new Coordinate(0, 0));
    	Assert.assertTrue(flightMovementAerodromeService.isAerodromeDomestic(avC));

        // CASE 4: assert identifier that does NTO fall within FIR returns false
    	avC = GeometryUtils.formatAviationCoordinate(new Coordinate(20, 20));
    	Assert.assertFalse(flightMovementAerodromeService.isAerodromeDomestic(avC));
    }
}
