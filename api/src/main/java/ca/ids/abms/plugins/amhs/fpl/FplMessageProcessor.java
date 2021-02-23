package ca.ids.abms.plugins.amhs.fpl;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.spatiareader.processor.FPLObjectProcessor;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;
import ca.ids.abms.plugins.amhs.AmhsRejectedExceptionHelper;
import ca.ids.abms.util.json.JsonHelper;

@Component
class FplMessageProcessor {

    public FplMessageProcessor (
            final FPLObjectProcessor fplObjectProcessor,
            final JsonHelper jsonHelper,
            final RejectedItemService rejectedItemService,
            final AmhsRejectedExceptionHelper amhsRejectedExceptionHelper) {
        this.fplObjectProcessor = fplObjectProcessor;
        this.jsonHelper = jsonHelper;
        this.rejectedItemService = rejectedItemService;
        this.amhsRejectedExceptionHelper = amhsRejectedExceptionHelper;
    }

    @SuppressWarnings ("squid:S1172")
    @Transactional
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageType(AmhsMessageType.FPL);
        final FlightMessage fpl = ctx.getAmhsParsedMessage().flightMessage;
        LOG.info ("processing FPL message {}", fpl);
        
        final FlightMessageValidator v = new FlightMessageValidator (ctx);
        
        // validate
        validate (ctx, v);
        
        // create or update the flight
        final FplObjectDto dto = toFplObjectDto (ctx);
        
        try {
            v.check();
            fplObjectProcessor.updateRejectableFplObject(dto);
        }
        catch (final RejectedException x) {
            reject (ctx, dto, x);
        }
        catch (final Exception x) {
            reject (ctx, dto, x);
        }
        v.clear();
        
        // done
        LOG.info ("done processing FPL message {}", fpl);
    }
    
    private void validate (final AmhsMessageContext ctx, final FlightMessageValidator v) {
        v.checkRequiredItems (3, 7, 8, 9, 10, 13, 15, 16);
        v.checkOptionalItems (18);
    }
    
    private void reject (final AmhsMessageContext ctx, final FplObjectDto dto, final RejectedException x) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        LOG.info ("rejecting AMHS message [{}] - {}", x.getMessage(), p);
        final String jsonText = jsonHelper.toJsonString(dto);
        final byte[] jsonData = jsonText.getBytes(StandardCharsets.UTF_8);
        this.rejectedItemService.create (
                RejectedItemType.FLIGHT_MOVEMENTS, // recordType
                x,
                null,                              // originator
                p.amhsMessage.getFilename(),       // filename
                null,                              // rawText
                null,                              // header
                jsonData
        );
    }
    
    private void reject (final AmhsMessageContext ctx, final FplObjectDto dto, final Exception x) {
        reject (ctx, dto, amhsRejectedExceptionHelper.validationError(x));
    }
    
    private LocalDate getDayOfFlight (final AmhsMessageContext ctx) {
        final Item18 item18 = ctx.getAmhsParsedMessage().flightMessage.item18;
        if (item18 != null && item18.dayOfFlight != null) {
            return item18.dayOfFlight;
        }
        if (!ctx.hasErrors()) {
            final Item13 item13 = ctx.getAmhsParsedMessage().flightMessage.item13;
            final LocalTime depTime = TimeOfDayParser.parseFplTimeOfDay(item13.departureTime);
            final LocalDateTime filingDateTime = ctx.getAmhsParsedMessage().amhsMessage.getFilingDateTime();
            if (depTime.isBefore(filingDateTime.toLocalTime())) {
                return filingDateTime.toLocalDate().plusDays(1);
            }
            return filingDateTime.toLocalDate();
        }
        return null;
    }

    private FplObjectDto toFplObjectDto (final AmhsMessageContext ctx) {
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        final String rawText = ctx.getAmhsParsedMessage().amhsMessage.getBody();
        final FplObjectDto dto = new FplObjectDto();
        dto.setCatalogueFplObjectId(null);
        dto.setCatalogueDate(LocalDateTime.now());
        dto.setCataloguePrcStatus ("P");
        dto.setDayOfFlight(getDayOfFlight(ctx));
        dto.setDepartureTime(m.item13 == null ? null : m.item13.departureTime);
        dto.setFlightId(m.item7 == null ? null : m.item7.callsign);
        dto.setDepartureAd(m.item13 == null ? null : m.item13.departureAirport);
        dto.setDestinationAd(m.item16 == null ? null : m.item16.destinationAirport);
        dto.setFlightRules(m.item8 == null ? null : m.item8.flightRules);
        dto.setFlightType(m.item8 == null ? null : m.item8.flightType);
        dto.setAircraftType(m.item9 == null ? null : m.item9.aircraftType);
        dto.setWakeTurb(m.item9 == null ? null : m.item9.wakeTurb);
        dto.setMsgDepartureTime(null);
        dto.setSpeed(m.item15 == null ? null : m.item15.cruisingSpeed);
        dto.setFlightLevel(m.item15 == null ? null : m.item15.flightLevel);
        dto.setRoute(m.item15 == null ? null : m.item15.route);
        dto.setTotalEet(m.item16 == null ? null : m.item16.totalEet);
        dto.setArrivalAd(null);
        dto.setArrivalTime(null);
        dto.setOtherInfo(m.item18 == null ? null : m.item18 == null ? null : m.item18.otherInfo);
        dto.setRawFpl(rawText);
        return dto;
    }

    private static final Logger LOG = LoggerFactory.getLogger (FplMessageProcessor.class);
    private final FPLObjectProcessor fplObjectProcessor;
    private final JsonHelper jsonHelper;
    private final RejectedItemService rejectedItemService;
    private final AmhsRejectedExceptionHelper amhsRejectedExceptionHelper;
}
