package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
import ca.ids.abms.modules.common.dto.BulkLoaderSummary;
import ca.ids.abms.modules.common.enumerators.FlightCategory;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by s.craymer on 31/08/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class FlightMovementServiceObservableTest {

    private BulkLoaderSummary bulkLoaderSummary;
    private FlightMovement flightMovement;

    @Mock
    private FlightMovementBuilder flightMovementBuilder;
    @Spy
    @InjectMocks
    private FlightMovementService flightMovementService;

    @Mock
    FlightMovementRepository flightMovementRepository;

    @Mock
    FlightMovementRepositoryUtility flightMovementRepositoryUtility;

    @Mock
    ThruFlightPlanUtility thruFlightPlanUtility;

    @Mock
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {

        bulkLoaderSummary = new BulkLoaderSummary();

        flightMovement = new FlightMovement();
        flightMovement.setFlightId("TEST1FM");
        flightMovement.setDateOfFlight(LocalDateTime.now());
        flightMovement.setDepTime("1012");
        flightMovement.setDepAd("FYRU");
        flightMovement.setDestAd("FYBG");
        flightMovement.setAircraftType("C210");
        flightMovement.setActualDepartureTime("1234");
        flightMovement.setArrivalTime("1414");
        flightMovement.setArrivalAd("1614");
        flightMovement.setFlightType("L");
        flightMovement.setMovementType(FlightMovementType.OTHER);
        flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);

        when(flightMovementRepositoryUtility.persist(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
        when(flightMovementRepositoryUtility.overwrite(any(FlightMovement.class)))
            .thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void createUpdateFlightMovementFromAtcMovementLog() throws Exception {

        when(flightMovementBuilder.createFlightMovementFromAtcMovementLog(any(AtcMovementLog.class)))
            .thenReturn(flightMovement);
        when(flightMovementBuilder.updateFlightMovementFromAtcMovementLog(any(FlightMovement.class), any(AtcMovementLog.class)))
            .thenReturn(flightMovement);
        when(systemConfigurationService.getBillingOrgCode()).thenReturn(BillingOrgCode.EANA);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT)).thenReturn(true);

        doReturn(null).when(flightMovementService).findFlightMovementFromAtcMovementLog(any(AtcMovementLog.class));
        flightMovementService.createUpdateFlightMovementFromAtcMovementLog(new AtcMovementLog(), bulkLoaderSummary);

        doReturn(flightMovement).when(flightMovementService).findFlightMovementFromAtcMovementLog(any(AtcMovementLog.class));
        flightMovementService.createUpdateFlightMovementFromAtcMovementLog(new AtcMovementLog(), bulkLoaderSummary);

        assertThat(bulkLoaderSummary.getFplAdded()).isEqualTo(1L);
        assertThat(bulkLoaderSummary.getFplUpdated()).isEqualTo(1L);
    }

    @Test
    public void createUpdateFlightMovementFromRadarSummary() throws Exception {

        when(flightMovementBuilder.createFlightMovementFromRadarSummary(any(RadarSummary.class)))
            .thenReturn(flightMovement);
        when(flightMovementBuilder.updateFlightMovementFromRadarSummary(any(FlightMovement.class), any(RadarSummary.class)))
            .thenReturn(flightMovement);
        when(systemConfigurationService.getBillingOrgCode()).thenReturn(BillingOrgCode.EANA);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT)).thenReturn(true);

        // set ID
        flightMovement.setId(1919231);

        RadarSummary radarSummary = new RadarSummary();
        radarSummary.setFlightIdentifier("FLID01");
        radarSummary.setDate(LocalDateTime.now());
        radarSummary.setDepartureAeroDrome("FBSK");
        radarSummary.setDestinationAeroDrome("FBMN");
        radarSummary.setDepartureTime("1000");

        doReturn(null).when(flightMovementService).findFlightMovementFromRadarSummary(any(RadarSummary.class));
        when(flightMovementRepository.findAllByFlightIdAndDateOfFlight(radarSummary.getFlightIdentifier(), radarSummary.getDate())).thenReturn(null);
        when(thruFlightPlanUtility.isThruFlight(any())).thenReturn(false);
        flightMovementService.createUpdateFlightMovementFromRadarSummary(radarSummary, bulkLoaderSummary);

        when(thruFlightPlanUtility.isThruFlight(any())).thenReturn(false);
        doReturn(flightMovement).when(flightMovementService).findFlightMovementFromRadarSummary(any(RadarSummary.class));
        flightMovementService.createUpdateFlightMovementFromRadarSummary(radarSummary, bulkLoaderSummary);

        assertThat(bulkLoaderSummary.getFplAdded()).isEqualTo(1L);
        assertThat(bulkLoaderSummary.getFplUpdated()).isEqualTo(1L);
    }

    @Test
    public void createUpdateFlightMovementFromTowerMovementLog() throws Exception {

        when(flightMovementBuilder.createFlightMovementFromTowerMovementLog(any(TowerMovementLog.class)))
            .thenReturn(flightMovement);
        when(flightMovementBuilder.updateFlightMovementFromTowerMovementLog(any(FlightMovement.class), any(TowerMovementLog.class)))
            .thenReturn(flightMovement);
        when(systemConfigurationService.getBillingOrgCode()).thenReturn(BillingOrgCode.EANA);
        when(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT)).thenReturn(true);

        doReturn(null).when(flightMovementService).findFlightMovementFromTowerMovementLog(any(TowerMovementLog.class));
        flightMovementService.createUpdateFlightMovementFromTowerMovementLog(mockTowerMovementLog(), bulkLoaderSummary);

        doReturn(flightMovement).when(flightMovementService).findFlightMovementFromTowerMovementLog(any(TowerMovementLog.class));
        flightMovementService.createUpdateFlightMovementFromTowerMovementLog(mockTowerMovementLog(), bulkLoaderSummary);

        assertThat(bulkLoaderSummary.getFplAdded()).isEqualTo(1L);
        assertThat(bulkLoaderSummary.getFplUpdated()).isEqualTo(1L);
    }

    @Test
    public void updateFlightMovementFromPassengerServiceCharge() throws Exception {

        List<FlightMovement> flightMovementList = new ArrayList<>(Collections.singletonList(flightMovement));

        when(flightMovementBuilder.updateFlightMovementFromPassengerPassengerServiceReturn(any(FlightMovement.class), any(PassengerServiceChargeReturn.class)))
            .thenReturn(flightMovement);

        doReturn(flightMovementList).when(flightMovementService).findFlightMovementByPassengerServiceChargeReturn(any(PassengerServiceChargeReturn.class), any(boolean.class));
        flightMovementService.updateFlightMovementFromPassengerServiceCharge(new PassengerServiceChargeReturn(), true, bulkLoaderSummary);

        assertThat(bulkLoaderSummary.getFplAdded()).isEqualTo(0L);
        assertThat(bulkLoaderSummary.getFplUpdated()).isEqualTo(1L);
    }

    private static TowerMovementLog mockTowerMovementLog() {
        TowerMovementLog result = new TowerMovementLog();

        result.setId(0);
        result.setFlightId("ABC123");
        result.setRegistration("ABCDE");
        result.setDateOfContact(LocalDate.of(2019, 1, 15).atStartOfDay());
        result.setDepartureContactTime("0650");
        result.setDepartureAerodrome("ABCD");
        result.setDestinationAerodrome("WXYZ");
        result.setRoute("DCT");
        result.setAircraftType("AB12");
        result.setFlightCategory(FlightCategory.NON_SCH);
        result.setFlightLevel("F123");
        result.setOperatorName("ABC");
        result.setFlightCrew(2);
        result.setPassengers(8);

        return result;
    }
}
