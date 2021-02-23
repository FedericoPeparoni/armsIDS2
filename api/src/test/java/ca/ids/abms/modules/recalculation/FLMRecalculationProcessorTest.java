package ca.ids.abms.modules.recalculation;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.jobs.ItemProcessor;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class FLMRecalculationProcessorTest{

    @Mock
    private FlightMovementService flightMovementService;

    private ItemProcessor<Integer> recalculationProcessor;

    private ItemProcessor<Integer> reconciliationProcessor;

    @Before
    public void setup() throws Exception {
        when(flightMovementService.calculateCharges(1)).thenReturn(true);
        when(flightMovementService.calculateCharges(2)).thenReturn(false);


        FlightMovement flightMovement = new FlightMovement();
        flightMovement.setId(3);

        when(flightMovementService.reconcileFlightMovementById(3)).thenReturn(flightMovement);
        when(flightMovementService.reconcileFlightMovementById(4)).thenReturn(null);

        recalculationProcessor = new FLMRecalculationProcessor(flightMovementService);
        reconciliationProcessor = new FLMReconciliationProcessor(flightMovementService);
    }

    @Test
    public void testRecalculationProcessItem() throws Exception {
        assertThat(recalculationProcessor.processItem(1)).isEqualTo(1);
        assertThat(recalculationProcessor.processItem(2)).isNull();
    }

    @Test
    public void testReconciliationProcessItem() throws Exception {
        assertThat(reconciliationProcessor.processItem(3)).isEqualTo(3);
        assertThat(reconciliationProcessor.processItem(4)).isNull();
    }
}
