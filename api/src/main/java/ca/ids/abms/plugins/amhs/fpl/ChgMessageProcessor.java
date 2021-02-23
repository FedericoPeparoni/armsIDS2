package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepositoryUtility;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;

@Component
class ChgMessageProcessor {

    public ChgMessageProcessor (
            final FlightMessageUtils flightMessageUtils,
            final FlightMovementService flightMovementService,
            final FlightMovementRepositoryUtility flightMovementRepositoryUtility) {
        this.flightMessageUtils = flightMessageUtils;
        this.flightMovementService = flightMovementService;
        this.flightMovementRepositoryUtility = flightMovementRepositoryUtility;
    }

    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.CHG);
        final FlightMessage fpl = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing FPL message {}", fpl);
        
        // validate
        validate (ctx);
        
        // create or update the flight
        update (ctx);
        
        // done
        LOG.info ("done processing FPL message {}", fpl);
    }
    
    private void validate (final AmhsMessageContext ctx) {
        final FlightMessageValidator v = new FlightMessageValidator (ctx);
        v.checkRequiredItems (3, 7, 13, 16, 22);
        v.checkOptionalItems(18);
        v.check();
    }
    
    private void update (final AmhsMessageContext ctx) {
        final FlightMovement flight = flightMessageUtils.findFlight (ctx);
        if (flight != null) {
            update (ctx, flight);
            return;
        }
        // Save it to pending message table
        flightMessageUtils.handleOrphanedAts (ctx);
    }
    private void update (final AmhsMessageContext ctx, final FlightMovement flight) {
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        final LocalDateTime filingDateTime = ctx.getAmhsParsedMessage().amhsMessage.getFilingDateTime();
        if (flight != null) {
            final String flightName = flightMessageUtils.getFlightDisplayName(flight);
            if (m.item22 == null || CollectionUtils.isEmpty(m.item22.amendments)) {
                LOG.info ("{}: no amendments specified, ignoring message", flightName);
            }
            flightMessageUtils.ensureUpdateAllowed(flight);
            LocalDate newDof = null;
            String newDepTimeStr = null;
            // Process amendments
            for (final ItemBase item: m.item22.amendments) {
                switch (item.id) {
                case 7:
                    final Item7 item7 = (Item7)item;
                    LOG.info ("{}: setting flightId=[{}]", flightName, item7.callsign);
                    flight.setFlightId (item7.callsign);
                    break;
                case 8:
                    final Item8 item8 = (Item8)item;
                    LOG.info ("{}: setting flightRules=[{}] flightType=[{}]", flightName, item8.flightRules, item8.flightType);
                    flight.setFlightRules(item8.flightRules);
                    flight.setFlightType(item8.flightType);
                    break;
                case 9:
                    final Item9 item9 = (Item9)item;
                    LOG.info ("{}: setting aircraftType=[{}] wakeTurb=[{}]", flightName, item9.aircraftType, item9.wakeTurb);
                    flight.setAircraftType (item9.aircraftType);
                    flight.setWakeTurb(item9.wakeTurb);
                    break;
                case 10:
                    break;
                case 13:
                    final Item13 item13 = (Item13)item;
                    LOG.info ("{}: setting depAd=[{}] depTime=[{}]", flightName, item13.departureAirport, item13.departureTime);
                    flight.setDepAd(item13.departureAirport);
                    flight.setDepTime(item13.departureTime);
                    newDepTimeStr = item13.departureTime;
                    break;
                case 15:
                    final Item15 item15 = (Item15)item;
                    LOG.info ("{}: setting cruisingSpeed=[{}] flightLevel=[{}]", flightName, item15.cruisingSpeed, item15.flightLevel);
                    flight.setCruisingSpeedOrMachNumber(item15.cruisingSpeed);
                    flight.setFlightLevel(item15.flightLevel);
                    if (!StringUtils.isEmpty (item15.route)) {
                        LOG.info ("{}: setting fplRoute=[{}]", flightName, item15.route);
                        flight.setFplRoute(item15.route);
                    }
                    break;
                case 16:
                    final Item16 item16 = (Item16)item;
                    LOG.info ("{}: setting destAd=[{}] eet=[{}]", flightName, item16.destinationAirport, item16.totalEet);
                    flight.setDestAd(item16.destinationAirport);
                    flight.setEstimatedElapsedTime(item16.totalEet);
                    break;
                case 17:
                    final Item17 item17 = (Item17)item;
                    LOG.info ("{}: setting arrivalAd=[{}] arrivalTime=[{}]", flightName, item17.arrivalAirport, item17.arrivalTime);
                    flight.setArrivalAd(item17.arrivalAirport);
                    flight.setArrivalTime(item17.arrivalTime);
                    break;
                case 18:
                    final Item18 item18 = (Item18)item;
                    LocalDate dayOfFlight = item18.dayOfFlight;
                    if (dayOfFlight == null) {
                        newDof = DayOfFlightParser.extractDof(item18.text, filingDateTime);
                    }
                    else {
                        newDof = dayOfFlight;
                    }
                    LOG.info ("{}: setting otherInfo=[{}]", flightName, item18.otherInfo);
                    flight.setOtherInfo (item18.otherInfo);
                    FlightMovementBuilder.checkAndParseItem18Field(flight);
                    break;
                }
            } // for

            // update DOF
            if (newDof == null && newDepTimeStr != null) {
                final LocalTime newDepTime = TimeOfDayParser.parseFplTimeOfDay (newDepTimeStr);
                if (newDepTime.isBefore(filingDateTime.toLocalTime())) {
                    newDof = filingDateTime.toLocalDate().plusDays(1);
                }
                else {
                    newDof = filingDateTime.toLocalDate();
                }
            }
            if (newDof != null) {
                LOG.info ("{}: setting dayOfFlight=[{}]", flightName, newDof);
                flight.setDateOfFlight(newDof.atStartOfDay());
            }
            
            // save it
            save (flight);
        }
    }
    
    private void save (final FlightMovement flight) {
        final int id = flight.getId();
        flightMovementRepositoryUtility.detach (flight);
        try {
            flightMovementService.updateFlightMovementFromUI (id, flight);
        } catch (FlightMovementBuilderException e) {
            // FIXME: what's this for??
            // copied from FlightMovementController.
            if(StringUtils.isBlank (flight.getEstimatedElapsedTime())){
                throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class);
            }

            if(StringUtils.isBlank(flight.getCruisingSpeedOrMachNumber())){
                throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class);
            }

            throw new CustomParametrizedException(e.getFlightMovementBuilderIssue().toValue(), new Exception(e.getLocalizedMessage()));
        }
    }
    
    private final FlightMessageUtils flightMessageUtils;
    private final FlightMovementService flightMovementService;
    private final FlightMovementRepositoryUtility flightMovementRepositoryUtility;
    private static final Logger LOG = LoggerFactory.getLogger(ChgMessageProcessor.class);
}
