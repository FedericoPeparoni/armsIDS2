package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;

@Component
public class FlightMessageUtils {
    
    public FlightMessageUtils (final FlightMovementRepository flightMovementRepository, final FlightMovementService flightMovementService) {
        this.flightMovementRepository = flightMovementRepository;
        this.flightMovementService = flightMovementService;
    }
    
    public void ensureUpdateAllowed (final FlightMovement x) {
        if (x != null && x.getStatus() != null) {
            switch (x.getStatus()) {
            case INCOMPLETE:
            case ACTIVE:
            case REJECTED:
            case PENDING:
            case CANCELED:
            case DECLINED:
            case OTHER:
                break;
            case INVOICED:
            case INVPAID:
            case PAID:
            case DELETED:
                throw new FlightMessageException ("update not allowed because flight status is " + x.getStatus());
            }
        }
    }
    
    /** Format a flight ID/name for debug logs */
    public String getFlightDisplayName (final FlightMovement x) {
        return FlightMovementBuilder.getFlightName(x);
    }
    
    /** Return full departure date/time of a flight */
    public LocalDateTime getFullDepTime (final FlightMovement x) {
        if (x.getDateOfFlight() != null) {
            final LocalTime depTime = TimeOfDayParser.parseFplTimeOfDay(x.getDepTime());
            if (depTime != null) {
                return LocalDateTime.of (x.getDateOfFlight().toLocalDate(), depTime);
            }
        }
        return null;
    }
    
    /** Find a flight; destAd is optional */
    public List <FlightMovement> findFlights (final Sort order, final String flightId, final String depAd, final String destAd, final LocalDate dofFrom, final LocalDate dofTo) {
        Preconditions.checkNotNull (flightId);
        Preconditions.checkNotNull (depAd);
        Preconditions.checkArgument (dofFrom != null);
        Preconditions.checkArgument (dofTo != null);
        
        // See also: https://docs.spring.io/spring-data/jpa/docs/1.10.3.RELEASE/reference/html/#specifications
        return this.flightMovementRepository.findAll((root, query, criteriaBuilder)->{
            final List <Predicate> andList = new ArrayList<>();
            
            // flightId
            andList.add (criteriaBuilder.equal(root.get ("flightId"), flightId));
            
            // depAd
            andList.add (criteriaBuilder.equal(root.get ("depAd"), depAd));
            
            // destAd
            if (destAd != null) {
                andList.add (criteriaBuilder.equal(root.get ("destAd"), destAd));
            }
            
            // dofFrom, dofTo
            andList.add (criteriaBuilder.between(root.get ("dateOfFlight"), dofFrom.atStartOfDay(), dofTo.atStartOfDay()));
            
            // Combine all predicates
            return criteriaBuilder.and (andList.toArray(new Predicate[0]));
        }, order);
    }
    
    public FlightMovement findFlight (final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final FlightMessage m = p.flightMessage;
        final String flightId = m.getCallsign();
        final LocalDate dof = getDof (ctx);
        final String depTime = m.getDepartureTime();
        final String eet = m.getTotalEet();
        final String depAd = m.getDepartureAirport();
        return findFlightByLogicalKey(flightId, dof, depTime, depAd, eet);
    }
    
    public void handleOrphanedAts(AmhsMessageContext ctx) {
        throw new FlightMessageException ("Flight not found");
    }
    
    // --------------------------- private -----------------------------
    
    /** Get DOF from item18 if possible, or filingDateTime otherwise */
    public LocalDate getDof (final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final FlightMessage m = p.flightMessage;
        final LocalDateTime filingDateTime = p.amhsMessage.getFilingDateTime();
        if (m.getDayOfFlight() != null) {
            return m.getDayOfFlight();
        }
        return filingDateTime.toLocalDate();
    }
    
    private final FlightMovement findFlightByLogicalKey (final String flightId, final LocalDate dof, final String depTime, final String depAd, final String eet) {
        return this.flightMovementService.getFlightMovementByLogicalKey(flightId, dof.atStartOfDay(), depTime, depAd, eet);
    }
    
    private final FlightMovementRepository flightMovementRepository;
    private final FlightMovementService flightMovementService;
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger (FlightMessageUtils.class);

}
