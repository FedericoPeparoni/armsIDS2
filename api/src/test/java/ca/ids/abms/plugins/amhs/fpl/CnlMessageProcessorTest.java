package ca.ids.abms.plugins.amhs.fpl;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CnlMessageProcessorTest {

    @Test
    public void testBasic() {
        final LocalDateTime filingDateTime = LocalDateTime.of (2020, 1, 1, 0, 0);
        final String body = "(CNL-BWI805/A2616-TTPP1100-SMJP-DOF/200915)";
        final AmhsMessageContext ctx = TestUtils.parse (body, filingDateTime);
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        proc.process(ctx);
        ctx.check();
        assertThat (m.item3.type).isEqualTo(AmhsMessageType.CNL);
        assertThat (m.item7.callsign).isEqualTo("BWI805");
        assertThat (m.item13.departureAirport).isEqualTo("TTPP");
        assertThat (m.item13.departureTime).isEqualTo("1100");
        assertThat (m.item16.destinationAirport).isEqualTo("SMJP");
        assertThat (m.item18.dayOfFlight).isEqualTo(LocalDate.of (2020, 9, 15));
    }
    
    @Before
    public void initProc() {
        final FlightMessageUtils flightMessageUtils = mock (FlightMessageUtils.class);
        final FlightMovement oldFlight = new FlightMovement();
        oldFlight.setStatus(FlightMovementStatus.PENDING);
        when (flightMessageUtils.findFlight(any())).thenReturn(oldFlight);
        
        final FlightMovementService flightMovementService = mock (FlightMovementService.class);
        final DlaMessageProcessor dlaProc = new DlaMessageProcessor (flightMovementService, flightMessageUtils);
        
        proc = new CnlMessageProcessor (flightMessageUtils, dlaProc);
    }
    
    private CnlMessageProcessor proc;

}
