package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.modules.aerodromes.cluster.RepositioningAerodromeClusterService;
import ca.ids.abms.modules.common.enumerators.CrossingDistanceStrategy;
import ca.ids.abms.modules.exemptions.flightroutes.ExemptFlightRouteService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.route.BiDirectionalNominalRoute;
import ca.ids.abms.modules.route.NominalRouteService;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.geometry.GeometryUtil;
import com.vividsolutions.jts.geom.Geometry;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by c.talpa on 11/03/2017.
 */
@SuppressWarnings("unchecked")
public class FlightMovementBillableTest {

    private static final double ATC_CROSS_DIST = 11.0d;
    private static final Geometry ATC_GEOM = GeometryUtil.convertStringToGeometry ("MULTIPOLYGON (((20 35, 10 30, 10 10, 30 5, 45 20, 20 35)))");

    private static final double FPL_CROSS_DIST = 12.0d;
    private static final Geometry FPL_GEOM = GeometryUtil.convertStringToGeometry ("MULTIPOLYGON (((20 36, 10 30, 10 10, 30 5, 45 20, 20 36)))");

    private static final double TOWER_CROSS_DIST = 13.0d;
    private static final Geometry TOWER_GEOM = GeometryUtil.convertStringToGeometry ("MULTIPOLYGON (((20 37, 10 30, 10 10, 30 5, 45 20, 20 37)))");

    private static final double RADAR_CROSS_DIST = 14.0d;
    private static final Geometry RADAR_GEOM = GeometryUtil.convertStringToGeometry ("MULTIPOLYGON (((20 38, 10 30, 10 10, 30 5, 45 20, 20 38)))");

    private SystemConfigurationService systemConfigurationService;
    private NominalRouteService nominalRouteService;
    private FlightMovementBillable flightMovementBillable;

    private FlightMovement do_createFlightMovement (final FlightmovementCategoryType type, FlightmovementCategory fmc) {
        final FlightMovement flightMovement = new FlightMovement();
        flightMovement.setFlightCategoryType(type);
        flightMovement.setFlightmovementCategory(fmc);
        flightMovement.setDepAd("DEP_AD");
        flightMovement.setDestAd("DEST_AD");
        flightMovement.setItem18Dest("ITEM18_DEST");
        flightMovement.setAtcCrossingDistance(ATC_CROSS_DIST);
        flightMovement.setAtcLogTrack(ATC_GEOM);
        flightMovement.setFplCrossingDistance(FPL_CROSS_DIST);
        flightMovement.setFplRouteGeom(FPL_GEOM);
        flightMovement.setTowerCrossingDistance(TOWER_CROSS_DIST);
        flightMovement.setTowerLogTrack(TOWER_GEOM);
        flightMovement.setRadarCrossingDistance(RADAR_CROSS_DIST);
        flightMovement.setRadarRoute(RADAR_GEOM);

        final List <RouteSegment> routeSegmentList = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            final RouteSegment rs = new RouteSegment();
            rs.setSegmentType(SegmentType.RADAR);
            rs.setSegmentNumber(i);
            rs.setSegmentStartLabel("RADAR_SEGMENT_" + i);
            rs.setSegmentEndLabel("RADAR_SEGMENT_END_" + i);
            routeSegmentList.add (rs);
        }
        for (int i = 0; i < 4; ++i) {
            final RouteSegment rs = new RouteSegment();
            rs.setSegmentType(SegmentType.ATC);
            rs.setSegmentNumber(i);
            rs.setSegmentStartLabel("ATC_SEGMENT_" + i);
            rs.setSegmentEndLabel("ATC_SEGMENT_END_" + i);
            routeSegmentList.add (rs);
        }
        for (int i = 0; i < 4; ++i) {
            final RouteSegment rs = new RouteSegment();
            rs.setSegmentType(SegmentType.SCHED);
            rs.setSegmentNumber(i);
            rs.setSegmentStartLabel("FPL_SEGMENT_" + i);
            rs.setSegmentEndLabel("FPL_SEGMENT_END_" + i);
            routeSegmentList.add (rs);
        }
        for (int i = 0; i < 4; ++i) {
            final RouteSegment rs = new RouteSegment();
            rs.setSegmentType(SegmentType.TOWER);
            rs.setSegmentNumber(i);
            rs.setSegmentStartLabel("TOWER_SEGMENT_" + i);
            rs.setSegmentEndLabel("TOWER_SEGMENT_END_" + i);
            routeSegmentList.add (rs);
        }
        for (int i = 0; i < 4; ++i) {
            final RouteSegment rs = new RouteSegment();
            rs.setSegmentType(SegmentType.USER);
            rs.setSegmentNumber(i);
            rs.setSegmentStartLabel("USER_SEGMENT_" + i);
            rs.setSegmentEndLabel("USER_SEGMENT_END_" + i);
            routeSegmentList.add (rs);
        }

        flightMovement.setRouteSegments (routeSegmentList);
        return flightMovement;
    }

    private static SystemConfiguration do_createSystemConfiguration (final String value) {
        final SystemConfiguration x = new SystemConfiguration();
        x.setCurrentValue(value);
        return x;
    }

    private static SystemConfiguration do_createSystemConfiguration (final CrossingDistanceStrategy... strategyArray) {
        final StringBuilder buf = new StringBuilder();
        String sep = "";
        for (final CrossingDistanceStrategy s: strategyArray) {
            buf.append (sep);
            buf.append (s.toDatabaseString());
            sep = ",";
        }
        return do_createSystemConfiguration (buf.toString());
    }

    private static BiDirectionalNominalRoute do_createNominalRoute (final double dist) {
        final BiDirectionalNominalRoute x = new BiDirectionalNominalRoute();
        x.setPointa("NOMINAL_POINT_A");
        x.setPointb("NOMINAL_POINT_B");
        x.setNominalDistance (dist);
        x.isInverse(false);
        return x;
    }

    @Before
    public void setUp() {
        nominalRouteService = mock (NominalRouteService.class);
        systemConfigurationService = mock (SystemConfigurationService.class);
        flightMovementBillable = new FlightMovementBillable(mock(ExemptFlightRouteService.class),
            mock(FlightMovementBuilderUtility.class), nominalRouteService,
            mock(RepositioningAerodromeClusterService.class), systemConfigurationService);
    }

    @Test
    public void testPrecedence() {
    	FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
        final FlightMovement flightMovement = do_createFlightMovement (FlightmovementCategoryType.DOMESTIC,fmc);

        // EXEMPT_FLIGHTS_DISTANCE = 2.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("2.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 1.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("1.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 999.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("999.0"));

        // RADAR first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.RADAR,
                    CrossingDistanceStrategy.ATC_LOG
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);

        // ATC LOG first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                CrossingDistanceStrategy.ATC_LOG,
                CrossingDistanceStrategy.RADAR
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(ATC_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("ATC_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("ATC_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(ATC_GEOM);

        // FPL first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                CrossingDistanceStrategy.SCHEDULED,
                CrossingDistanceStrategy.RADAR
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(FPL_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("FPL_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("FPL_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(FPL_GEOM);

        // TOWER first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                CrossingDistanceStrategy.TOWER_LOG,
                CrossingDistanceStrategy.RADAR
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(TOWER_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("TOWER_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("TOWER_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(TOWER_GEOM);

        // TOWER first, but tower distance is null
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                CrossingDistanceStrategy.TOWER_LOG,
                CrossingDistanceStrategy.RADAR
            ));
        flightMovement.setTowerCrossingDistance(null);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);

        // zero length billable distance, non user segment type
        flightMovement.setTowerCrossingDistance(0.0d);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);

        // zero length billable distance, user segment type
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                CrossingDistanceStrategy.USER,
                CrossingDistanceStrategy.RADAR
            ));
        flightMovement.setUserCrossingDistance(0.0d);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(0.0d);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("USER_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("USER_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(FPL_GEOM);
    }

    @Test
    public void testSmallestLargest() {
    	FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
        final FlightMovement flightMovement = do_createFlightMovement (FlightmovementCategoryType.DOMESTIC,fmc);

        // EXEMPT_FLIGHTS_DISTANCE = 2.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("2.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 1.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("1.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 999.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("999.0"));

        // precedence = LARGEST
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.LARGEST
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);

        // precedence = SMALLEST
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.SMALLEST
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(ATC_CROSS_DIST);

    }

    @Test
    public void testExemptDist() {
    	FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
        final FlightMovement flightMovement = do_createFlightMovement (FlightmovementCategoryType.DOMESTIC,fmc);

        // EXEMPT_FLIGHTS_DISTANCE = 2.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("2.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 1.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("1.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 999.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("999.0"));

        // RADAR first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.RADAR
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);

        // EXEMPT_FLIGHTS_DISTANCE = 13.99
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("13.99"));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(RADAR_CROSS_DIST);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);

        // EXEMPT_FLIGHTS_DISTANCE = 15.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("14.0"));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(0.0);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);
    }

    @Test
    public void testMinMaxDist() {
    	FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
        final FlightMovement flightMovement = do_createFlightMovement (FlightmovementCategoryType.DOMESTIC,fmc);

        // EXEMPT_FLIGHTS_DISTANCE = 1.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("1.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 3.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("3.0"));
        // MAX_DOMESTIC_CROSSING_DISTANCE = 4.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("4.0"));

        // precedence
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.RADAR
            ));

        // dist = 2.0
        flightMovement.setRadarCrossingDistance(2.0);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(3.0);

        // dist = 2.99
        flightMovement.setRadarCrossingDistance(2.99);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(3.0);

        // dist = 3.0
        flightMovement.setRadarCrossingDistance(3.0);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(3.0);

        // dist = 3.4
        flightMovement.setRadarCrossingDistance(3.4);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(3.4);

        // dist = 3.99
        flightMovement.setRadarCrossingDistance(3.99);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isCloseTo(3.99, within (0.001));

        // dist = 4.0
        flightMovement.setRadarCrossingDistance(4.0);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo (4.0);

        // dist = 4.1
        flightMovement.setRadarCrossingDistance(4.1);
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo (4.0);

    }

    @Test
    public void testNominal() {
    	FlightmovementCategory fmc = new FlightmovementCategory();
    	fmc.setName("DOMESTIC");
    	fmc.setId(1);
        final FlightMovement flightMovement = do_createFlightMovement (FlightmovementCategoryType.DOMESTIC,fmc);

        // EXEMPT_FLIGHTS_DISTANCE = 2.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.EXEMPT_FLIGHTS_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("2.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 1.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MIN_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("1.0"));
        // MIN_DOMESTIC_CROSSING_DISTANCE = 999.0
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.MAX_DOMESTIC_CROSSING_DISTANCE))
            .thenReturn(do_createSystemConfiguration ("999.0"));

        BiDirectionalNominalRoute nominalRoute = do_createNominalRoute(55);

        // fake nominal route
        when (nominalRouteService.findNominalRouteBasedOnPrecedence(flightMovement)).thenReturn (nominalRoute);

        // RADAR first
        when (systemConfigurationService.getOneByItemName (SystemConfigurationItemName.CROSSING_DISTANCE_PRECEDENCE))
            .thenReturn(do_createSystemConfiguration (
                    CrossingDistanceStrategy.NOMINAL,
                    CrossingDistanceStrategy.RADAR
            ));
        flightMovementBillable.calculateBillableRouteAndDistance(flightMovement);
        assertThat (flightMovement.getBillableCrossingDist()).isEqualTo(55.0);
        assertThat (flightMovement.getBillableEntryPoint()).isEqualTo("RADAR_SEGMENT_0");
        assertThat (flightMovement.getBillableExitPoint()).isEqualTo("RADAR_SEGMENT_END_3");
        assertThat (flightMovement.getBillableRoute()).isEqualTo(RADAR_GEOM);
    }
}
