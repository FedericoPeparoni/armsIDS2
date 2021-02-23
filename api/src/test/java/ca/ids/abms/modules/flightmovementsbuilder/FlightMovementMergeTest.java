package ca.ids.abms.modules.flightmovementsbuilder;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
import ca.ids.abms.modules.flightmovementsbuilder.utility.DeltaFlightUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;

/**
 * Created by c.talpa on 04/03/2017.
 */
public class FlightMovementMergeTest {

    private FlightMovementMerge flightMovementMerge;

    @Before
    public void setup() {
        flightMovementMerge = new FlightMovementMerge(
            mock(DeltaFlightUtility.class), mock(ThruFlightPlanUtility.class));
    }

    @Test
    public void overwriteAllFieldsExceptUserUpdatedTest() {

        // CASE 1: exclude departure time field
        FlightMovement mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_UPDATE.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());


        // CASE 2: exclude no fields
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_UPDATE.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());


        // CASE 3: exclude departure time and destination aerodrome fields
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("dep_time,dep_ad").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_UPDATE.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());


        // CASE 4: exclude no fields
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields(null).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_UPDATE.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());


        // CASE 5: return null for invalid source
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY)
                .manuallyChangedFields("dep_time,dest_ad").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNull(mergedFlightMovement);


        // CASE 6: default exclude fields id and source are never overwritten
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.MANUAL)
                .manuallyChangedFields("dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.ID, mergedFlightMovement.getId());
        Assert.assertEquals(FlightMovementSource.MANUAL, mergedFlightMovement.getSource());
        Assert.assertEquals(MOCK_UPDATE.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_UPDATE.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());


        // CASE 7: null update flight values do not overwrite existing flight values
        // this assumes that dest ad field is not annotated with `@MergeOnNull`
        mergedFlightMovement = flightMovementMerge.overwriteAllFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .destAd(null).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEST_AD, mergedFlightMovement.getDestAd());
    }

    @Test
    public void overwritePassengerFieldsExceptUserUpdatedTest() {

        // CASE 1: non-passenger fields are not overwritten
        FlightMovement mergedFlightMovement = flightMovementMerge.overwritePassengerFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_EXIST.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.PASSENGER_DOM, mergedFlightMovement.getPassengersChargeableDomestic());
        Assert.assertEquals(MOCK_UPDATE.PASSENGER_INT, mergedFlightMovement.getPassengersChargeableIntern());


        // CASE 2: passenger fields are overwritten except user defined
        mergedFlightMovement = flightMovementMerge.overwritePassengerFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("passengers_chargeable_domestic,dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_EXIST.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.PASSENGER_DOM, mergedFlightMovement.getPassengersChargeableDomestic());
        Assert.assertEquals(MOCK_UPDATE.PASSENGER_INT, mergedFlightMovement.getPassengersChargeableIntern());


        // CASE 3: null value passenger fields overwrite on merge
        // set updated passenger counts to null as each must be marked with `@MergeOnNull`
        mergedFlightMovement = flightMovementMerge.overwritePassengerFieldsExceptUserUpdated(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("dep_time").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .passengersChargeableDomestic(null)
                .passengersChargeableIntern(null)
                .passengersChild(null).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertNull(mergedFlightMovement.getPassengersChargeableDomestic());
        Assert.assertNull(mergedFlightMovement.getPassengersChargeableIntern());
        Assert.assertNull(mergedFlightMovement.getPassengersChild());
    }

    @Test
    public void updateFlightMovementCreatedByRadarTowerAtcTest() {

        // CASE 1: flight plan can fill in only empty fields
        FlightMovement mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY)
                .dateOfFlight(null)
                .aircraftType(null).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());


        // CASE 2: result should be null as updating from radar summary, atc, or tower are not considered valid update sources
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.ATC_LOG).build());
        Assert.assertNull(mergedFlightMovement);

        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build());
        Assert.assertNull(mergedFlightMovement);

        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.TOWER_LOG).build());
        Assert.assertNull(mergedFlightMovement);


        // CASE 3: result should be null as existing network or manual are not considered valid existing sources
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNull(mergedFlightMovement);

        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.MANUAL).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNull(mergedFlightMovement);


        // CASE 4: existing values are not overwritten by null values except aircraft type when ZZZZ
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY)
                .aircraftType("ZZZZ")
                .fplRoute(null)
                .radarRouteText("MOCK RADAR ROUTE 001").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .fplRoute("MOCK FPL ROUTE 002")
                .radarRouteText(null).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals("MOCK RADAR ROUTE 001", mergedFlightMovement.getRadarRouteText());
        Assert.assertEquals("MOCK FPL ROUTE 002", mergedFlightMovement.getFplRoute());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());

        // CASE 5: should overwrite arrival aerodrome and arrival time for delta and thru flights only

        // when updating flight movement is a delta flight plan, overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .deltaFlight(Boolean.TRUE).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_EXIST.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when updating flight movement is a thru flight plan, overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .thruFlight(Boolean.TRUE).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_EXIST.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when updating flight movement is a delta flight plan, DO NOT overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .deltaFlight(Boolean.FALSE).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_EXIST.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when updating flight movement is a thru flight plan, DO NOT overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByRadarTowerAtc(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK)
                .thruFlight(Boolean.FALSE).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_EXIST.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_EXIST.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
    }

    @Test
    public void updateFlightMovementCreatedByFPLTest() {

        // CASE 1: should not overwrite existing depAd, destAd, and actualDepartureTime fields
        FlightMovement mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_AD, mergedFlightMovement.getDepAd());
        Assert.assertEquals(MOCK_EXIST.DEST_AD, mergedFlightMovement.getDestAd());
        Assert.assertEquals(MOCK_EXIST.ACTUAL_DEPARTURE_TIME, mergedFlightMovement.getActualDepartureTime());
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());


        // CASE 2: should not overwrite existing when updated value is null
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY)
                .depTime(null).build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());


        // CASE 3: result should be null as updating from network or manual are not considered valid sources
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.NETWORK).build());
        Assert.assertNull(mergedFlightMovement);

        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.MANUAL).build());
        Assert.assertNull(mergedFlightMovement);


        // CASE 4: result should be null as existing radar summary, atc, tower are not considered valid sources
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.ATC_LOG).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.ATC_LOG).build());
        Assert.assertNull(mergedFlightMovement);
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.TOWER_LOG).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.ATC_LOG).build());
        Assert.assertNull(mergedFlightMovement);
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.RADAR_SUMMARY).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.ATC_LOG).build());
        Assert.assertNull(mergedFlightMovement);


        // CASE 5: should not overwrite existing flight plan route
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .fplRoute("MOCK FPL ROUTE 001")
                .radarRouteText("MOCK RADAR ROUTE 001").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY)
                .fplRoute(null)
                .radarRouteText("MOCK RADAR ROUTE 002").build());
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals("MOCK FPL ROUTE 001", mergedFlightMovement.getFplRoute());
        Assert.assertEquals("MOCK RADAR ROUTE 002", mergedFlightMovement.getRadarRouteText());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());

        // CASE 6: should not overwrite manually changed fields
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .manuallyChangedFields("aircraft_type").build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());


        // CASE 7: should not overwrite arrival aerodrome and arrival time for delta and thru flights only

        // when existing flight movement is a delta flight plan, DO NOT overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .deltaFlight(Boolean.TRUE).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when existing flight movement is a thru flight plan, DO NOT overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .thruFlight(Boolean.TRUE).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_EXIST.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when existing flight movement is a delta flight plan, overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .deltaFlight(Boolean.FALSE).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());

        // when existing flight movement is a thru flight plan, overwrite existing
        mergedFlightMovement = flightMovementMerge.updateFlightMovementCreatedByFPL(
            MOCK_EXIST.BUILDER(FlightMovementSource.NETWORK)
                .thruFlight(Boolean.FALSE).build(),
            MOCK_UPDATE.BUILDER(FlightMovementSource.RADAR_SUMMARY).build()
        );
        Assert.assertNotNull(mergedFlightMovement);
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_AD, mergedFlightMovement.getArrivalAd());
        Assert.assertEquals(MOCK_UPDATE.ARRIVAL_TIME, mergedFlightMovement.getArrivalTime());
        Assert.assertEquals(MOCK_UPDATE.DEP_TIME, mergedFlightMovement.getDepTime());
        Assert.assertEquals(MOCK_UPDATE.DATE_OF_FLIGHT, mergedFlightMovement.getDateOfFlight());
        Assert.assertEquals(MOCK_UPDATE.AIRCRAFT_TYPE, mergedFlightMovement.getAircraftType());
    }

    static class MOCK_EXIST {

        static final Integer ID = 0;
        static final String FLIGHT_ID = "ABC001";
        static final LocalDateTime DATE_OF_FLIGHT = LocalDateTime.of(2019, 1, 31, 0, 0);
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "LMNO";
        static final String DEP_TIME = "0100";
        static final String ACTUAL_DEPARTURE_TIME = "0195";
        static final String ARRIVAL_AD = "LMNO";
        static final String ARRIVAL_TIME = "0300";
        static final String AIRCRAFT_TYPE = "A100";
        static final Integer PASSENGER_DOM = 10;
        static final Integer PASSENGER_INT = 5;
        static final Integer PASSENGER_CHILD = 2;

        static MOCK_FLIGHT.BUILDER BUILDER(final FlightMovementSource source) {
            return new MOCK_FLIGHT.BUILDER(source)
                .id(ID)
                .flightId(FLIGHT_ID)
                .dateOfFlight(DATE_OF_FLIGHT)
                .depAd(DEP_AD)
                .destAd(DEST_AD)
                .depTime(DEP_TIME)
                .actualDepartureTime(ACTUAL_DEPARTURE_TIME)
                .arrivalAd(ARRIVAL_AD)
                .arrivalTime(ARRIVAL_TIME)
                .aircraftType(AIRCRAFT_TYPE)
                .passengersChargeableDomestic(PASSENGER_DOM)
                .passengersChargeableIntern(PASSENGER_INT)
                .passengersChild(PASSENGER_CHILD);
        }
    }

    static class MOCK_UPDATE {

        static final Integer ID = null;
        static final String FLIGHT_ID = "ABC002";
        static final LocalDateTime DATE_OF_FLIGHT = LocalDateTime.of(2019, 2, 1, 0, 0);
        static final String DEP_AD = "HIJK";
        static final String DEST_AD = "WXYZ";
        static final String DEP_TIME = "0200";
        static final String ACTUAL_DEPARTURE_TIME = "0205";
        static final String ARRIVAL_AD = "WXYZ";
        static final String ARRIVAL_TIME = "0400";
        static final String AIRCRAFT_TYPE = "A200";
        static final Integer PASSENGER_DOM = 20;
        static final Integer PASSENGER_INT = 15;
        static final Integer PASSENGER_CHILD = null;

        static MOCK_FLIGHT.BUILDER BUILDER(final FlightMovementSource source) {
            return new MOCK_FLIGHT.BUILDER(source)
                .id(ID)
                .flightId(FLIGHT_ID)
                .dateOfFlight(DATE_OF_FLIGHT)
                .depAd(DEP_AD)
                .destAd(DEST_AD)
                .depTime(DEP_TIME)
                .actualDepartureTime(ACTUAL_DEPARTURE_TIME)
                .arrivalAd(ARRIVAL_AD)
                .arrivalTime(ARRIVAL_TIME)
                .aircraftType(AIRCRAFT_TYPE)
                .passengersChargeableDomestic(PASSENGER_DOM)
                .passengersChargeableIntern(PASSENGER_INT)
                .passengersChild(PASSENGER_CHILD);
        }
    }

    static class MOCK_FLIGHT {

        static class BUILDER {

            private Integer id;
            private String flightId;
            private LocalDateTime dateOfFlight;
            private String depAd;
            private String destAd;
            private String depTime;
            private String actualDepartureTime;
            private String arrivalAd;
            private String arrivalTime;
            private String aircraftType;
            private Integer passengersChargeableDomestic;
            private Integer passengersChargeableIntern;
            private Integer passengersChild;
            private FlightMovementSource source;
            private String manuallyChangedFields;
            private String fplRoute;
            private String radarRouteText;
            private Boolean deltaFlight;
            private Boolean thruFlight;

            BUILDER (final FlightMovementSource source) {
                this.source = source;
            }

            BUILDER id(final Integer id) {
                this.id = id;
                return this;
            }

            BUILDER flightId(final String flightId) {
                this.flightId = flightId;
                return this;
            }

            BUILDER dateOfFlight(final LocalDateTime dateOfFlight) {
                this.dateOfFlight = dateOfFlight;
                return this;
            }

            BUILDER depAd(final String depAd) {
                this.depAd = depAd;
                return this;
            }

            BUILDER destAd(final String destAd) {
                this.destAd = destAd;
                return this;
            }

            BUILDER depTime(final String depTime) {
                this.depTime = depTime;
                return this;
            }

            BUILDER actualDepartureTime(final String actualDepartureTime) {
                this.actualDepartureTime = actualDepartureTime;
                return this;
            }

            BUILDER arrivalAd(final String arrivalAd) {
                this.arrivalAd = arrivalAd;
                return this;
            }

            BUILDER arrivalTime(final String arrivalTime) {
                this.arrivalTime = arrivalTime;
                return this;
            }

            BUILDER aircraftType(final String aircraftType) {
                this.aircraftType = aircraftType;
                return this;
            }

            BUILDER passengersChargeableDomestic(final Integer passengersChargeableDomestic) {
                this.passengersChargeableDomestic = passengersChargeableDomestic;
                return this;
            }

            BUILDER passengersChargeableIntern(final Integer passengersChargeableIntern) {
                this.passengersChargeableIntern = passengersChargeableIntern;
                return this;
            }

            BUILDER passengersChild(final Integer passengersChild) {
                this.passengersChild = passengersChild;
                return this;
            }

            BUILDER manuallyChangedFields(final String manuallyChangedFields) {
                this.manuallyChangedFields = manuallyChangedFields;
                return this;
            }

            BUILDER fplRoute(final String fplRoute) {
                this.fplRoute = fplRoute;
                return this;
            }

            BUILDER radarRouteText(final String radarRouteText) {
                this.radarRouteText = radarRouteText;
                return this;
            }

            BUILDER deltaFlight(final Boolean deltaFlight) {
                this.deltaFlight = deltaFlight;
                return this;
            }

            BUILDER thruFlight(final Boolean thruFlight) {
                this.thruFlight = thruFlight;
                return this;
            }

            FlightMovement build() {
                FlightMovement result = new FlightMovement();

                result.setId(id);
                result.setFlightId(flightId);
                result.setDateOfFlight(dateOfFlight);
                result.setDepAd(depAd);
                result.setDestAd(destAd);
                result.setDepTime(depTime);
                result.setActualDepartureTime(actualDepartureTime);
                result.setArrivalAd(arrivalAd);
                result.setArrivalTime(arrivalTime);
                result.setAircraftType(aircraftType);
                result.setPassengersChargeableDomestic(passengersChargeableDomestic);
                result.setPassengersChargeableIntern(passengersChargeableIntern);
                result.setPassengersChild(passengersChild);
                result.setSource(source);
                result.setManuallyChangedFields(manuallyChangedFields);
                result.setFplRoute(fplRoute);
                result.setRadarRouteText(radarRouteText);
                result.setDeltaFlight(deltaFlight);
                result.setThruFlight(thruFlight);

                return result;
            }
        }
    }
}
