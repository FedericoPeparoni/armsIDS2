package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodA;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

public class TowerMovementLogDepartureEstimatorTest {

    private DepartureEstimatorMethodA methodA;
    private TowerMovementLogDepartureEstimator estimator;

    @Before
    public void setup() {
        methodA = mock(DepartureEstimatorMethodA.class);
        estimator = new TowerMovementLogDepartureEstimator(methodA);
    }

    @Test
    public void resolveMissingDepartureTimeSuccessfulMethodATest() {

        // when method A returns estimate, tower movement log must be updated
        TowerMovementLog towerMovementLog = MOCK.TOWER();
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay())
            .setDepTime("2350").build();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(result);
        estimator.resolveMissingDepartureTime(towerMovementLog);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(towerMovementLog.getDayOfFlight()).isEqualTo(LocalDate.of(2018, 12, 29).atStartOfDay());
        assertThat(towerMovementLog.getDepartureTime()).isEqualTo("2350");
    }

    @Test
    public void resolveMissingDepartureTimeUnsuccessfulMethodATest() {

        // when method A returns no estimate, tower movement log must not be updated
        TowerMovementLog towerMovementLog = MOCK.TOWER();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        estimator.resolveMissingDepartureTime(towerMovementLog);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(towerMovementLog.getDayOfFlight()).isEqualTo(null);
        assertThat(towerMovementLog.getDepartureTime()).isEqualTo(null);
    }

    @Test
    public void hasInvalidFieldsTest() {

        // tower movement log does not throw any exceptions when null and does not call any estimators
        estimator.resolveMissingDepartureTime(null);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));


        // tower movement log with departure contact time should not call any estimators
        TowerMovementLog towerMovementLog = MOCK.TOWER();
        towerMovementLog.setDepartureContactTime("0100");

        estimator.resolveMissingDepartureTime(towerMovementLog);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));


        // tower movement log with day of flight or departure time should not call any estimators
        towerMovementLog = MOCK.TOWER();
        towerMovementLog.setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay());
        towerMovementLog.setDepartureTime("2350");

        estimator.resolveMissingDepartureTime(towerMovementLog);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
    }

    static class MOCK {

        static final Integer ID = 0;
        static final String FLIGHT_ID = "ABC123";
        static final LocalDateTime DATE_OF_CONTACT = LocalDate.of(2018, 12, 30).atStartOfDay();
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "WXYZ";
        static final String DEP_TIME = null;

        static TowerMovementLog TOWER() {

            TowerMovementLog result = new TowerMovementLog();

            result.setId(ID);
            result.setFlightId(FLIGHT_ID);
            result.setDateOfContact(DATE_OF_CONTACT);
            result.setDepartureAerodrome(DEP_AD);
            result.setDestinationAerodrome(DEST_AD);
            result.setDepartureTime(DEP_TIME);

            return result;
        }
    }
}
