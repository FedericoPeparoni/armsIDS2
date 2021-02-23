package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalTime;

import org.assertj.core.util.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;

@Component
class DepMessageProcessor {

    public DepMessageProcessor (
            final FlightMessageUtils flightMessageUtils,
            final DlaMessageProcessor dlaMessageProcessor) {
        this.flightMessageUtils = flightMessageUtils;
        this.dlaMessageProcessor = dlaMessageProcessor;
    }

    @SuppressWarnings ("squid:S1172")
    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.DEP);
        final FlightMessage dep = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing DEP message {}", dep);
        
        // validate syntax
        validate (ctx);
        
        // find flight movement and record departure
        depart (ctx);

        // done
        LOG.info ("done processing DEP message {}", dep);
    }
    
    void validate (final AmhsMessageContext ctx) {
        // same as DLA
        dlaMessageProcessor.validate(ctx);
    }
    
    private void depart (final FlightMovement x, final LocalTime actualDepTime) {
        final String actualDepTimeStr = TimeOfDayParser.formatTimeOfDay(actualDepTime);
        final String flightName = flightMessageUtils.getFlightDisplayName(x);
        LOG.info ("{}: setting actualDepartureTime=[{}]", flightName, actualDepTimeStr);
        x.setActualDepartureTime(actualDepTimeStr);
    }
    
    private void depart (final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final FlightMessage m = p.flightMessage;
        
        // Find the relevant flight
        final FlightMovement f = flightMessageUtils.findFlight (ctx);
        if (f != null) {
            // departureTime must be set at this point
            final LocalTime actualDepTime = TimeOfDayParser.parseFplTimeOfDay (m.item13.departureTime);
            Preconditions.checkNotNull (actualDepTime);
            depart (f, actualDepTime);
            return;
        }
        
        // Save it to pending message table
        flightMessageUtils.handleOrphanedAts (ctx);
    }
    
    private static final Logger LOG = LoggerFactory.getLogger (DepMessageProcessor.class);
    private final FlightMessageUtils flightMessageUtils;
    private final DlaMessageProcessor dlaMessageProcessor;
}
