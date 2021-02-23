package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodA;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodC;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AtcMovementLogDepartureEstimatorTest {

    private DepartureEstimatorMethodA methodA;
    private DepartureEstimatorMethodC methodC;
    private AtcMovementLogDepartureEstimator estimator;

    @Before
    public void setup() {
        methodA = mock(DepartureEstimatorMethodA.class);
        methodC = mock(DepartureEstimatorMethodC.class);
        estimator = new AtcMovementLogDepartureEstimator(methodA, methodC);
    }

    @Test
    public void resolveMissingDepartureTimeSuccessfulMethodATest() {

        // when method A returns estimate, atc movement log must be updated
        AtcMovementLog atcMovementLog = MOCK.ATC();
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay())
            .setDepTime("2350").build();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(result);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        estimator.resolveMissingDepartureTime(atcMovementLog);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(atcMovementLog.getDayOfFlight()).isEqualTo(LocalDate.of(2018, 12, 29).atStartOfDay());
        assertThat(atcMovementLog.getDepartureTime()).isEqualTo("2350");
    }

    @Test
    public void resolveMissingDepartureTimeSuccessfulMethodCTest() {

        // when method A returns estimate, atc movement log must be updated
        AtcMovementLog atcMovementLog = MOCK.ATC();
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay())
            .setDepTime("2350").build();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(result);
        estimator.resolveMissingDepartureTime(atcMovementLog);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(atcMovementLog.getDayOfFlight()).isEqualTo(LocalDate.of(2018, 12, 29).atStartOfDay());
        assertThat(atcMovementLog.getDepartureTime()).isEqualTo("2350");
    }

    @Test
    public void resolveMissingDepartureTimeUnsuccessfulMethodAandCTest() {

        // when method A returns no estimate, atc movement log must not be updated
        AtcMovementLog atcMovementLog = MOCK.ATC();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        estimator.resolveMissingDepartureTime(atcMovementLog);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(atcMovementLog.getDayOfFlight()).isEqualTo(null);
        assertThat(atcMovementLog.getDepartureTime()).isEqualTo(null);
    }

    @Test
    public void hasInvalidFieldsTest() {

        // atc movement log does not throw any exceptions when null and does not call any estimators
        estimator.resolveMissingDepartureTime(null);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));


        // atc movement log with day of flight or departure time should not call any estimators
        AtcMovementLog atcMovementLog = MOCK.ATC();
        atcMovementLog.setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay());
        atcMovementLog.setDepartureTime("2350");

        estimator.resolveMissingDepartureTime(atcMovementLog);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
    }

    static class MOCK {

        static final Integer ID = 0;
        static final String FLIGHT_ID = "ABC123";
        static final LocalDateTime DATE_OF_CONTACT = LocalDate.of(2018, 12, 30).atStartOfDay();
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "WXYZ";
        static final String DEP_TIME = null;

        static AtcMovementLog ATC() {

            AtcMovementLog result = new AtcMovementLog();

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
