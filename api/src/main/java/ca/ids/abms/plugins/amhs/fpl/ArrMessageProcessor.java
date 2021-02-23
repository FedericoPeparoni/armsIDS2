package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;

@Component
class ArrMessageProcessor {

    public ArrMessageProcessor (final FlightMessageUtils flightMessageUtils) {
        this.flightMessageUtils = flightMessageUtils;
    }

    @SuppressWarnings ("squid:S1172")
    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.ARR);
        final FlightMessage arr = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing ARR message {}", arr);
        
        // validate
        final ArrivalInfo arrivalInfo = validate (ctx);
        
        // Find the flight and record arrival time etc
        arrive (ctx, arrivalInfo);

        // done
        LOG.info ("done processing ARR message {}", arr);
    }
    
    private void arrive (final AmhsMessageContext ctx, final ArrivalInfo arrivalInfo) {
        // Find the relevant flight
        final FlightMovement f = flightMessageUtils.findFlight (ctx);
        if (f != null) {
            arrive (f, arrivalInfo);
            return;
        }
        
        // Save it to pending message table
        flightMessageUtils.handleOrphanedAts (ctx);
    }
    
    private void arrive (final FlightMovement x, final ArrivalInfo arrivalInfo) {
        final String flightName = flightMessageUtils.getFlightDisplayName(x);
        LOG.info ("{}: setting arrivalAd=[{}] arrivalTime=[{}]", flightName, arrivalInfo.arrivalAirport, arrivalInfo.arrivalTime);
        x.setArrivalAd (arrivalInfo.arrivalAirport);
        x.setArrivalTime (arrivalInfo.arrivalTime);
    }
    
    private ArrivalInfo validate (final AmhsMessageContext ctx) {
        final FlightMessageValidator v = new FlightMessageValidator (ctx);
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        final LocalDateTime filingDateTime = ctx.getAmhsParsedMessage().amhsMessage.getFilingDateTime();
        
        v.checkRequiredItems(3, 7, 13);
        v.checkOptionalItems(16, 17, 18);
        v.check();
        
        if (m.item17 != null) {
            return new ArrivalInfo (m.item17.arrivalAirport, m.item17.arrivalTime);
        }
        return new ArrivalInfo (m.item13.departureAirport, FlightMessageParserUtils.formatTimeOfDay (filingDateTime.toLocalTime()));
    }
    
    private static class ArrivalInfo {
        final String arrivalAirport;
        final String arrivalTime;
        ArrivalInfo (final String arrivalAirport, final String arrivalTime) {
            this.arrivalAirport = arrivalAirport;
            this.arrivalTime = arrivalTime;
        }
    }

    private static final Logger LOG = LoggerFactory.getLogger (ArrMessageProcessor.class);
    private final FlightMessageUtils flightMessageUtils;
}
