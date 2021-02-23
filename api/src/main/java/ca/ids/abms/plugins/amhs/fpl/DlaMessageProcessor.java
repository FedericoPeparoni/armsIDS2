package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.base.Preconditions;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;

@Component
class DlaMessageProcessor {

    public DlaMessageProcessor (final FlightMovementService flightMovementService, final FlightMessageUtils flightMessageUtils) {
        this.flightMessageUtils = flightMessageUtils;
    }

    @SuppressWarnings ("squid:S1172")
    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.DLA);
        final FlightMessage dla = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing DLA message {}", dla);
        
        // validate syntax
        validate (ctx);
        
        // find and update the flight movement
        delay (ctx);
        
        // done
        LOG.info ("done processing DLA message {}", dla);
    }
    
    private void delay (final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final FlightMessage m = p.flightMessage;

        // Find the relevant flight
        final FlightMovement f = findFlight (ctx);
        if (f != null) {
            // departureTime must be set at this point
            final LocalTime newDepTime = TimeOfDayParser.parseFplTimeOfDay(m.item13.departureTime);
            Preconditions.checkNotNull (newDepTime);
            delay (f, m.getDayOfFlight(), newDepTime);
            return;
        }
        
        // Save it to pending message table
        flightMessageUtils.handleOrphanedAts (ctx);
    }
    
    private void delay (final FlightMovement x, LocalDate newDof, final LocalTime newTime) {
        final String flightName = flightMessageUtils.getFlightDisplayName(x);
        // DOF not provided: increment current DOF by 1 if new departure time seems "before"
        // old departure time (ie. on the next day)
        if (newDof == null) {
            final LocalDate oldDof = x.getDateOfFlight().toLocalDate();
            final LocalTime oldTime = TimeOfDayParser.parseFplTimeOfDay(x.getDepTime());
            if (oldTime != null && newTime.isBefore(oldTime)) {
                newDof = oldDof.plusDays(1);
            }
        }
        final String newTimeStr = TimeOfDayParser.formatTimeOfDay (newTime);
        LOG.info ("{}: setting dayOfFlight=[{}] depTime=[{}]", flightName, newDof, newTimeStr);
        x.setDateOfFlight(newDof.atStartOfDay());
        x.setDepTime(newTimeStr);
    }
    
    private FlightMovement findFlight (final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final FlightMessage m = p.flightMessage;
        final LocalDateTime filingDateTime = p.amhsMessage.getFilingDateTime();
        if (m.getCallsign() == null || m.getDepartureAirport() == null) {
            return null;
        }
        final String flightId = m.getCallsign();
        final String depAd = m.getDepartureAirport();
        final String destAd = m.getDestinationAirport();
        final LocalDate newDof = m.getDayOfFlight();
        final LocalTime newDepTime = TimeOfDayParser.parseFplTimeOfDay (m.getDepartureTime());
        final LocalDate baseDate = newDof == null ? filingDateTime.toLocalDate() : newDof;
        
        // create full new dep time
        final LocalDateTime fullNewDepTime = LocalDateTime.of (newDof, newDepTime);
        
        final Sort order = new Sort(Sort.Direction.ASC, "dateOfFlight", "depTime", "id");
        final List <FlightMovement> list = flightMessageUtils.findFlights (
                order,
                flightId,
                depAd,
                destAd,
                baseDate.minusDays(1),
                baseDate.plusDays(1));
        FlightMovement flight;
        // Find 1st non-arrived flight whose departure time is <= the new departure time
        flight = list.stream()
                .filter(f->StringUtils.isEmpty(f.getArrivalTime()))
                .filter (f->!flightMessageUtils.getFullDepTime (f).isAfter(fullNewDepTime))
                .findFirst()
                .orElse(null);
        if (flight != null) {
            return flight;
        }
        // Find 1st flight whose departure time is <= the new departure time
        flight = list.stream()
                .filter (f->!flightMessageUtils.getFullDepTime (f).isAfter(fullNewDepTime))
                .findFirst()
                .orElse(null);
        if (flight != null) {
            return flight;
        }
        
        
        return null;
    }
    
    void validate (final AmhsMessageContext ctx) {
        final FlightMessageValidator v = new FlightMessageValidator (ctx);
        v.checkRequiredItems(3, 7, 13, 16);
        v.checkOptionalItems(18);
        v.check();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger (DlaMessageProcessor.class);
    private final FlightMessageUtils flightMessageUtils;
}
