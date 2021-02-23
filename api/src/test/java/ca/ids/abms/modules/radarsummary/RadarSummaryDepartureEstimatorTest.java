package ca.ids.abms.modules.radarsummary;

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

public class RadarSummaryDepartureEstimatorTest {

    private DepartureEstimatorMethodA methodA;
    private DepartureEstimatorMethodC methodC;
    private RadarSummaryDepartureEstimator estimator;

    @Before
    public void setup() {
        methodA = mock(DepartureEstimatorMethodA.class);
        methodC = mock(DepartureEstimatorMethodC.class);
        estimator = new RadarSummaryDepartureEstimator(methodA, methodC);
    }

    @Test
    public void resolveMissingDepartureTimeSuccessfulMethodATest() {

        // when method A returns estimate, radar summary must be updated
        RadarSummary radarSummary = MOCK.RADAR();
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay())
            .setDepTime("2350").build();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(result);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        estimator.resolveMissingDepartureTime(radarSummary);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(radarSummary.getDayOfFlight()).isEqualTo(LocalDate.of(2018, 12, 29).atStartOfDay());
        assertThat(radarSummary.getDepartureTime()).isEqualTo("2350");
    }

    @Test
    public void resolveMissingDepartureTimeSuccessfulMethodCTest() {

        // when method A returns estimate, radar summary must be updated
        RadarSummary radarSummary = MOCK.RADAR();
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay())
            .setDepTime("2350").build();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(result);
        estimator.resolveMissingDepartureTime(radarSummary);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(radarSummary.getDayOfFlight()).isEqualTo(LocalDate.of(2018, 12, 29).atStartOfDay());
        assertThat(radarSummary.getDepartureTime()).isEqualTo("2350");
    }

    @Test
    public void resolveMissingDepartureTimeUnsuccessfulMethodAandCTest() {

        // when method A returns no estimate, radar summary must not be updated
        RadarSummary radarSummary = MOCK.RADAR();

        when(methodA.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        when(methodC.estimateDepartureTime(any(DepartureEstimatorModel.class))).thenReturn(null);
        estimator.resolveMissingDepartureTime(radarSummary);

        verify(methodA, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(1)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        assertThat(radarSummary.getDayOfFlight()).isEqualTo(null);
        assertThat(radarSummary.getDepartureTime()).isEqualTo(null);
    }

    @Test
    public void hasInvalidFieldsTest() {

        // radar summary does not throw any exceptions when null and does not call any estimators
        estimator.resolveMissingDepartureTime(null);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));


        // radar summary with day of flight or departure time should not call any estimators
        RadarSummary radarSummary = MOCK.RADAR();
        radarSummary.setDayOfFlight(LocalDate.of(2018, 12, 29).atStartOfDay());
        radarSummary.setDepartureTime("2350");

        estimator.resolveMissingDepartureTime(radarSummary);
        verify(methodA, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
        verify(methodC, times(0)).estimateDepartureTime(any(DepartureEstimatorModel.class));
    }

    static class MOCK {

        static final Integer ID = 0;
        static final String FLIGHT_ID = "ABC123";
        static final LocalDateTime DATE_OF_CONTACT = LocalDate.of(2018, 12, 30).atStartOfDay();
        static final String DEP_AD = "ABCD";
        static final String DEST_AD = "WXYZ";
        static final String DEP_TIME = null;
        static final String CRUISING_SPEED = "N0250";
        static final String FIR_ENTRY_POINT = "LMNO";
        static final String FIR_ENTRY_TIME = "0600";

        static RadarSummary RADAR() {

            RadarSummary result = new RadarSummary();

            result.setId(ID);
            result.setFlightIdentifier(FLIGHT_ID);
            result.setDate(DATE_OF_CONTACT);
            result.setDepartureAeroDrome(DEP_AD);
            result.setDestinationAeroDrome(DEST_AD);
            result.setDepartureTime(DEP_TIME);
            result.setCruisingSpeed(CRUISING_SPEED);
            result.setFirEntryPoint(FIR_ENTRY_POINT);
            result.setFirEntryTime(FIR_ENTRY_TIME);

            return result;
        }
    }
}
