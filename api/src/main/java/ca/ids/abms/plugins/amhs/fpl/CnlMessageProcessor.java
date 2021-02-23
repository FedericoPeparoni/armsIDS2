package ca.ids.abms.plugins.amhs.fpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.MoreObjects;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;

@Component
class CnlMessageProcessor {

    public CnlMessageProcessor (
            final FlightMessageUtils flightMessageUtils,
            final DlaMessageProcessor dlaMessageProcessor) {
        this.flightMessageUtils = flightMessageUtils;
        this.dlaMessageProcessor = dlaMessageProcessor;
    }

    @SuppressWarnings ("squid:S1172")
    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.CNL);
        final FlightMessage cnl = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing CNL message {}", cnl);
        
        // validate
        validate (ctx);
        
        // find the flight and cancel it
        cancel (ctx);
        
        // done
        LOG.info ("done processing CNL message {}", cnl);
    }
    
    
    private void cancel (final AmhsMessageContext ctx) {
        // Find the relevant flight
        final FlightMovement f = flightMessageUtils.findFlight (ctx);
        if (f != null) {
            cancel (f);
            return;
        }
        
        // Save it to pending message table
        flightMessageUtils.handleOrphanedAts (ctx);
    }
    
    public void cancel (final FlightMovement x) {
        final String flightName = flightMessageUtils.getFlightDisplayName(x);
        final FlightMovementStatus status = MoreObjects.firstNonNull(x.getStatus(), FlightMovementStatus.OTHER);
        switch (status) {
        case INCOMPLETE:
        case PENDING:
        case OTHER:
            LOG.info ("{}: changing status to CANCELED", flightName);
            x.setStatus(FlightMovementStatus.CANCELED);
            break;
        case CANCELED:
            LOG.info ("{}: flight status is already CANCELED", flightName);
            break;
        default:
            throw new FlightMessageException ("unable to cancel flight whose status is " + status);
        }
    }
    
    void validate (final AmhsMessageContext ctx) {
        // same as DLA
        dlaMessageProcessor.validate(ctx);
    }
    
    private static final Logger LOG = LoggerFactory.getLogger (CnlMessageProcessor.class);
    private final FlightMessageUtils flightMessageUtils;
    private final DlaMessageProcessor dlaMessageProcessor;
}
