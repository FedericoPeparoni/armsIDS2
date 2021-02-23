package ca.ids.abms.modules.flightmovementsbuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ids.abms.modules.system.SystemConfigurationService;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.atcmovements.AtcMovementLogBillableRouteFinder;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.NavDBUtils;

public class AtcMovementLogBillableRouteFinderTest {

    private AerodromeService aerodromeService;
    private UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private AtcMovementLogBillableRouteFinder atcMovementLogBillableRouteFinder;

    @Before
    public void setUp() {

        NavDBUtils navdbUtils = mock(NavDBUtils.class);
        AirspaceService airspaceService = mock(AirspaceService.class);
        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);

        aerodromeService = mock (AerodromeService.class);
        unspecifiedDepartureDestinationLocationService = mock(UnspecifiedDepartureDestinationLocationService.class);

        FlightMovementAerodromeService flightMovementAerodromeService = new FlightMovementAerodromeService(
            aerodromeService, unspecifiedDepartureDestinationLocationService, navdbUtils, airspaceService,
            systemConfigurationService);

        atcMovementLogBillableRouteFinder = new AtcMovementLogBillableRouteFinder(flightMovementAerodromeService);
    }

    private FlightMovement createFlightMovement (final FlightmovementCategoryType type) {
        final FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightCategoryType(type);
        flightMovement.setDepAd("DEP_AD");
        flightMovement.setItem18Dep ("ITEM18_DEP");
        flightMovement.setDestAd("DEST_AD");
        flightMovement.setItem18Dest ("ITEM18_DEST");
        return flightMovement;
    }

    private AtcMovementLog createAtcMovementLog() {
        final AtcMovementLog atcMovementLog = new AtcMovementLog();
        atcMovementLog.setDepartureAerodrome("DEP_AD");
        atcMovementLog.setDestinationAerodrome("ATC_DEST_AD");
        atcMovementLog.setFirEntryPoint("ATC_ENTRY_POINT");
        atcMovementLog.setFirMidPoint("ATC_MID_POINT");
        atcMovementLog.setFirExitPoint ("ATC_EXIT_POINT");
        return atcMovementLog;
    }

    private UnspecifiedDepartureDestinationLocation createUnspecLoc (final Double lat, final Double lng) {
        final UnspecifiedDepartureDestinationLocation x = new UnspecifiedDepartureDestinationLocation();
        x.setLatitude (lat);
        x.setLongitude(lng);
        return x;
    }

    private Aerodrome createAerodrome (final Double lat, final Double lng) {
        final Aerodrome aerodrome = new Aerodrome();
        if (lat != null && lng != null) {
            final Coordinate coord = new Coordinate (lng, lat);
            final GeometryFactory gf = new GeometryFactory();
            final Point p = gf.createPoint (coord);
            aerodrome.setGeometry (p);
            return aerodrome;
        }
        return aerodrome;
    }

    @Test
    public void testBasic() {
        final FlightMovement flightMovement = createFlightMovement (FlightmovementCategoryType.DOMESTIC);
        final AtcMovementLog atcMovementLog = createAtcMovementLog();
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT ATC_MID_POINT ATC_EXIT_POINT");

        when (aerodromeService.findAeroDromeByAeroDromeName ("ATC_ENTRY_POINT")).thenReturn (createAerodrome (1.0, 2.0));
        when (unspecifiedDepartureDestinationLocationService.findByTextIdentifierOrAerodromeIdentifier("ATC_EXIT_POINT")).thenReturn(createUnspecLoc (3.0, 4.0));
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT ATC_MID_POINT 030000N0040000E");

        atcMovementLog.setFirMidPoint("050000N0060000E");
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT 050000N0060000E 030000N0040000E");
    }

    @Test
    public void testMissingEntryPoint() {
        final FlightMovement flightMovement = createFlightMovement (FlightmovementCategoryType.DOMESTIC);
        final AtcMovementLog atcMovementLog = createAtcMovementLog();
        atcMovementLog.setFirEntryPoint (null);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("DEP_AD ATC_MID_POINT ATC_EXIT_POINT");

        flightMovement.setFlightCategoryType(FlightmovementCategoryType.ARRIVAL);
        flightMovement.setFlightCategoryScope(FlightmovementCategoryScope.INTERNATIONAL);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isNull();

        flightMovement.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("DEP_AD ATC_MID_POINT ATC_EXIT_POINT");

    }

    @Test
    public void testMissingExitPoint() {
        final FlightMovement flightMovement = createFlightMovement (FlightmovementCategoryType.DOMESTIC);
        final AtcMovementLog atcMovementLog = createAtcMovementLog();
        atcMovementLog.setFirExitPoint (null);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT ATC_MID_POINT ATC_DEST_AD");

        flightMovement.setFlightCategoryScope(FlightmovementCategoryScope.INTERNATIONAL);
        flightMovement.setFlightCategoryType(FlightmovementCategoryType.DEPARTURE);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isNull();

        flightMovement.setFlightCategoryType(FlightmovementCategoryType.ARRIVAL);
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT ATC_MID_POINT ATC_DEST_AD");

        when (aerodromeService.findAeroDromeByAeroDromeName ("DEST_AD")).thenReturn (createAerodrome (1.0, 2.0));
        assertThat (atcMovementLogBillableRouteFinder.getBillableRouteString(flightMovement, atcMovementLog)).isEqualTo("ATC_ENTRY_POINT ATC_MID_POINT ATC_DEST_AD");
    }

}
