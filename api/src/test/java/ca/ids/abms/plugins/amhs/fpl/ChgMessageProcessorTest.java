package ca.ids.abms.plugins.amhs.fpl;

import org.junit.Before;
import org.junit.Test;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepositoryUtility;
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

public class ChgMessageProcessorTest {

    @Test
    public void testBasic() {
        final LocalDateTime filingDateTime = LocalDateTime.of (2020, 1, 1, 0, 0);
        final String body = "(CHG-BWI802-TTPP1100-SMJP-DOF/200915-8/IN-9/AT42/M-13/MDJB1300\n" + 
                "-15/N0485F430 DCT -16/MRLB0235 MROC-18/PBN/A1B5D3L1S2 DOF/200603\n" + 
                "EET/TNCF0020 SKEC0043 MPZL0127 MHCC0200 ORGN/KAUSFFLX RMK/OVF\n" + 
                "COLOMBIA 011837 OVF PANAMA 201071)";
        final AmhsMessageContext ctx = TestUtils.parse (body, filingDateTime);
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        proc.process(ctx);
        ctx.check();
        assertThat (m.item3.type).isEqualTo(AmhsMessageType.CHG);
        assertThat (m.item7.callsign).isEqualTo("BWI802");
        assertThat (m.item13.departureAirport).isEqualTo("TTPP");
        assertThat (m.item13.departureTime).isEqualTo("1100");
        assertThat (m.item16.destinationAirport).isEqualTo("SMJP");
        assertThat (m.item18.dayOfFlight).isEqualTo(LocalDate.of (2020, 9, 15));
        assertThat (m.item22.amendments.size()).isEqualTo(6);
        
        assertThat (m.item22.amendments.get(0).id).isEqualTo(8);
        final Item8 item8 = (Item8) m.item22.amendments.get(0);
        assertThat (item8.flightRules).isEqualTo("I");
        assertThat (item8.flightType).isEqualTo("N");
        
        assertThat (m.item22.amendments.get(1).id).isEqualTo(9);
        final Item9 item9 = (Item9) m.item22.amendments.get(1);
        assertThat (item9.aircraftType).isEqualTo("AT42");
        assertThat (item9.wakeTurb).isEqualTo("M");
        
        assertThat (m.item22.amendments.get(2).id).isEqualTo(13);
        final Item13 item13 = (Item13) m.item22.amendments.get(2);
        assertThat (item13.departureAirport).isEqualTo("MDJB");
        assertThat (item13.departureTime).isEqualTo("1300");
        
        assertThat (m.item22.amendments.get(3).id).isEqualTo(15);
        final Item15 item15 = (Item15) m.item22.amendments.get(3);
        assertThat (item15.cruisingSpeed).isEqualTo("N0485");
        assertThat (item15.flightLevel).isEqualTo("F430");
        assertThat (item15.route).isEqualTo("DCT");
        
        assertThat (m.item22.amendments.get(4).id).isEqualTo(16);
        final Item16 item16 = (Item16) m.item22.amendments.get(4);
        assertThat (item16.destinationAirport).isEqualTo("MRLB");
        assertThat (item16.totalEet).isEqualTo("0235");
        assertThat (item16.altDestinationAirport).isEqualTo("MROC");
        
        assertThat (m.item22.amendments.get(5).id).isEqualTo(18);
        final Item18 item18 = (Item18) m.item22.amendments.get(5);
        assertThat (item18.dayOfFlight).isEqualTo(LocalDate.of (2020, 6, 3));
    }
    
    @Before
    public void initProc() {
        final FlightMessageUtils flightMessageUtils = mock (FlightMessageUtils.class);
        final FlightMovement oldFlight = new FlightMovement();
        oldFlight.setId(-1);
        oldFlight.setStatus(FlightMovementStatus.PENDING);
        when (flightMessageUtils.findFlight(any())).thenReturn(oldFlight);
        
        final FlightMovementService flightMovementService = mock (FlightMovementService.class);
        
        final FlightMovementRepositoryUtility flightMovementRepositoryUtility = mock (FlightMovementRepositoryUtility.class);
        
        proc = new ChgMessageProcessor (flightMessageUtils, flightMovementService, flightMovementRepositoryUtility);
    }
    
    private ChgMessageProcessor proc;

}
