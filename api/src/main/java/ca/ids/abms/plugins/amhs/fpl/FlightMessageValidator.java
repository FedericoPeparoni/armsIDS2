package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Preconditions;

import static org.apache.commons.lang3.StringUtils.trimToNull;

import ca.ids.abms.plugins.amhs.AmhsMessageContext;

import static ca.ids.abms.plugins.amhs.fpl.ItemComponentNames.*;

public class FlightMessageValidator {
    
    public FlightMessageValidator (final AmhsMessageContext ctx) {
        this.ctx = ctx;
        this.m = ctx.getAmhsParsedMessage().flightMessage;
    }
    
    public void checkRequiredItems (final int... ids) {
        checkItems (true, ids);
    }
    
    public void checkOptionalItems (final int... ids) {
        checkItems (false, ids);
    }
    
    public void check() {
        ctx.check();
    }
    
    public void clear() {
        ctx.clear();
    }

    // ------------------- private ------------------------
    
    private void checkItems (boolean required, final int... ids) {
        for (final int id: ids) {
            final ItemMapping im = findItemMapping (id);
            final ItemBase item = im.getItemFunc.apply(m);
            if (item != null) {
                im.checkItemFunc.accept(this, item);
                continue;
            }
            if (required) {
                errMissingField (String.format ("item %d", id));
            }
        }
    }
    
    private ItemMapping findItemMapping (int id) {
        final ItemMapping im = ITEM_MAPPING.get (id);
        Preconditions.checkArgument(im != null, "illegal item id %d", id);
        return im;
    }
    
    private static class ItemMapping {
        final Function <FlightMessage, ItemBase> getItemFunc;
        final BiConsumer <FlightMessageValidator, ItemBase> checkItemFunc;
        
        @SuppressWarnings("unchecked")
        public static <Item extends ItemBase> ItemMapping create (
                Function <FlightMessage, Item> getItemFunc,
                final BiConsumer <FlightMessageValidator, Item> checkItemFunc) {
            return new ItemMapping (
                    (Function <FlightMessage, ItemBase>)getItemFunc,
                    (BiConsumer <FlightMessageValidator, ItemBase>)checkItemFunc);
        }
        
        private ItemMapping (
                final Function <FlightMessage, ItemBase> getItemFunc,
                final BiConsumer <FlightMessageValidator, ItemBase> checkItemFunc) {
            this.getItemFunc = getItemFunc;
            this.checkItemFunc = checkItemFunc;
        }
    }
    private final Map <Integer, ItemMapping> ITEM_MAPPING = createItemMapping();
    
    private static Map <Integer, ItemMapping> createItemMapping() {
        final Map <Integer, ItemMapping> map = new HashMap<>();
        map.put (3, ItemMapping.create (m->m.item3, FlightMessageValidator::checkItem3));
        map.put (7, ItemMapping.create (m->m.item7, FlightMessageValidator::checkItem7));
        map.put (8, ItemMapping.create (m->m.item8, FlightMessageValidator::checkItem8));
        map.put (9, ItemMapping.create (m->m.item9, FlightMessageValidator::checkItem9));
        map.put (10, ItemMapping.create (m->m.item10, FlightMessageValidator::checkItem10));
        map.put (13, ItemMapping.create (m->m.item13, FlightMessageValidator::checkItem13));
        map.put (15, ItemMapping.create (m->m.item15, FlightMessageValidator::checkItem15));
        map.put (16, ItemMapping.create (m->m.item16, FlightMessageValidator::checkItem16));
        map.put (17, ItemMapping.create (m->m.item17, FlightMessageValidator::checkItem17));
        map.put (18, ItemMapping.create (m->m.item18, FlightMessageValidator::checkItem18));
        map.put (22, ItemMapping.create (m->m.item22, FlightMessageValidator::checkItem22));
        return map;
    }
    
    
    private void checkItem3 (final Item3 item3) {
        checkRequiredField (item3.type, MESSAGE_TYPE);
    }

    private void checkItem7 (final Item7 item7) {
        
        // callsign
        item7.callsign = trimToNull (item7.callsign);
        checkRequiredField (item7.callsign, CALLSIGN);
        checkPattern (item7.callsign, CALLSIGN, RE_CALLSIGN);
        checkLen (item7.callsign, CALLSIGN, 1, 7);
        
    }
    
    private void checkItem8 (final Item8 item8) {
        
        // flightRules
        item8.flightRules = trimToNull (item8.flightRules);
        checkEnum (item8.flightRules, FLIGHT_RULES, "I", "V", "Y", "Z");
        
        // flightType
        item8.flightType = trimToNull (item8.flightType);
        checkEnum (item8.flightType, FLIGHT_TYPE, "S", "N", "G", "M", "X");
    }
    
    private void checkItem9 (final Item9 item9) {
        
        // aircraftNumber
        item9.aircraftNumber = trimToNull (item9.aircraftNumber);
        checkPattern (item9.aircraftNumber, AIRCRAFT_NUMBER, RE_AIRCRAFT_NUMBER);
        checkLen (item9.aircraftNumber, AIRCRAFT_NUMBER, 1, 2);
        
        // aircraftType
        item9.aircraftType = trimToNull (item9.aircraftType);
        checkRequiredField (item9.aircraftType, AIRCRAFT_TYPE);
        checkPattern (item9.aircraftType, AIRCRAFT_TYPE, RE_AIRCRAFT_TYPE);
        checkLen (item9.aircraftType, AIRCRAFT_TYPE, 2, 4);
        
        // wakeTurb
        item9.wakeTurb = trimToNull (item9.wakeTurb);
        checkEnum (item9.wakeTurb, WAKE_TURB, "H", "M", "L");
    }
    
    private void checkItem10 (final Item10 item10) {
        // equipment
        item10.equip = trimToNull (item10.text);
        checkPattern (item10.equip, EQUIPMENT, RE_NAV_EQUIP);
    }
    
    private void checkItem13 (final Item13 item13) {
        // departureAirport
        item13.departureAirport = trimToNull (item13.departureAirport);
        checkRequiredField (item13.departureAirport, DEPARTURE_AIRPORT);
        checkPattern (item13.departureAirport, DEPARTURE_AIRPORT, RE_ICAO_LOCATION);
        
        // departureTime
        item13.departureTime = checkTime (item13.departureTime, DEPARTURE_TIME);
        checkRequiredField (item13.departureTime, DEPARTURE_TIME);
    }
    
    private void checkItem15 (final Item15 item15) {
        // cruisingSpeed
        item15.cruisingSpeed = trimToNull (item15.cruisingSpeed);
        checkPattern (item15.cruisingSpeed, CRUISING_SPEED, RE_CRUISING_SPEED);
        
        // flightLevel
        item15.flightLevel = trimToNull (item15.flightLevel);
        checkPattern (item15.flightLevel, FLIGHT_LEVEL, RE_FLIGHT_LEVEL);
        
        // route
        item15.route = trimToNull (item15.route);
    }
    
    private void checkItem16 (final Item16 item16) {
        // destinationAirport
        item16.destinationAirport = trimToNull (item16.destinationAirport);
        checkRequiredField (item16.destinationAirport, DESTINATION_AIRPORT);
        checkPattern (item16.destinationAirport, DESTINATION_AIRPORT, RE_ICAO_LOCATION);
        
        // altDestinationAirport
        item16.altDestinationAirport2 = trimToNull (item16.altDestinationAirport);
        checkPattern (item16.altDestinationAirport, ALT_DESTINATION_AIRPORT, RE_ICAO_LOCATION);
        
        // altDestinationAirport2
        item16.altDestinationAirport2 = trimToNull (item16.altDestinationAirport2);
        checkPattern (item16.altDestinationAirport2, ALT_DESTINATION_AIRPORT_2, RE_ICAO_LOCATION);
        
        // totalEet
        item16.totalEet = checkTime (item16.totalEet, TOTAL_EET);
    }
    
    private void checkItem17 (final Item17 item17) {
        // arrivalAirport
        item17.arrivalAirport = trimToNull (item17.arrivalAirport);
        checkRequiredField (item17.arrivalAirport, ARRIVAL_AIRPORT);
        checkPattern (item17.arrivalAirport, ARRIVAL_AIRPORT, RE_ICAO_LOCATION);
        
        // arrivalTime
        item17.arrivalTime = checkTime (item17.arrivalTime, ARRIVAL_TIME);
        checkRequiredField (item17.arrivalTime, ARRIVAL_TIME);
        
        // arrivalAirportName
    }
    
    private void checkItem18 (final Item18 item18) {
        final LocalDateTime filingDateTime = ctx.getAmhsParsedMessage().amhsMessage.getFilingDateTime();
        
        // dayOfFlight
        final Matcher m = RE_DOF.matcher (item18.otherInfo);
        if (m.find()) {
            final String dofStr = m.group(1);
            final LocalDate dof = DayOfFlightParser.parseDof (dofStr, filingDateTime);
            if (dof == null) {
                errBadSyntax (DAY_OF_FLIGHT, dofStr);
            }
            item18.dayOfFlight = dof;
        }
        
        // otherInfo
    }
    
    private void checkItem22 (final Item22 item22) {
        // amendments
        item22.amendments = item22.amendments == null ? new ArrayList<>() : item22.amendments;
        for (final ItemBase item: item22.amendments) {
            final ItemMapping im = findItemMapping (item.id);
            im.checkItemFunc.accept (this, item);
        }
    }
    
    // ----------- private ----------------
    
    private <T> boolean checkRequiredField (final T v, final String field) {
        if (v == null) {
            errMissingField (field);
            return false;
        }
        return true;
    }
    
    private boolean checkPattern (final String s, final String field, final Pattern regex) {
        if (s != null && !matches (s, regex)) {
            errBadSyntax (field, s);
            return false;
        }
        return true;
    }
    
    private boolean checkEnum (final String s, final String field, final String... allowedValues) {
        if (!StringUtils.equalsAny(s, allowedValues)) {
            errEnum (field, allowedValues);
            return false;
        }
        return true;
    }
    
    private boolean checkLen (final String s, final String field, final Integer min, final Integer max) {
        if (s != null) {
            if (min != null && s.length() < min) {
                errTooShort (field, min);
                return false;
            }
            if (max != null && s.length() > max) {
                errTooLong (field, max);
                return false;
            }
        }
        return true;
    }
    
    private String checkTime (final String s, final String field) {
        if (s != null) {
            final LocalTime t = TimeOfDayParser.parseFplTimeOfDay(s);
            if (t == null) {
                errBadSyntax (field, s);
                return null;
            }
            return TimeOfDayParser.formatTimeOfDay(t);
        }
        return null;
    }
    
    private boolean matches (final String s, final Pattern p) {
        return p.matcher(s).matches();
    }
    
    private void errMissingField (final String field) {
        ctx.error("{0} is required", field);
    }
    
    private void errBadSyntax (final String field, final String text) {
        ctx.error("Invalid syntax [{1}] in {0}", field, text);
    }
    
    private void errTooShort (final String field, final int min) {
        ctx.error ("{0} must contain >= {1} characters", field, min);
    }
    
    private void errTooLong (final String field, final int max) {
        ctx.error ("{0} must contain <= {1} characters", field, max);
    }
    
    private void errEnum (final String field, final String... allowedValues) {
        ctx.error ("{0} must be one of {1}", field, Arrays.asList (allowedValues));
    }
    
    private static final Pattern RE_AIRCRAFT_NUMBER = Pattern.compile ("^[0-9]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_AIRCRAFT_TYPE = Pattern.compile ("^[A-Z0-9]+$", Pattern.CASE_INSENSITIVE);
    //private static final Pattern RE_NAV_EQUIP = Pattern.compile ("^[NS][ABCDEFGHIJKLMOPQRTUVWXYZ]+/[NACXPISD]{1,2}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_NAV_EQUIP = Pattern.compile ("^[A-Z0-9]+/[A-Z0-9]{1,2}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_ICAO_LOCATION = Pattern.compile ("^[A-Z0-9]{4}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_CRUISING_SPEED = Pattern.compile ("^(?:K[0-9]{4})|(?:N[0-9]{4})|(?:M[0-9]{3})$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_FLIGHT_LEVEL = Pattern.compile ("^(?:F[0-9]{3})|(?:S[0-9]{4})|(?:A[0-9]{3})|(?:M[0-9]{4})|(?:VFR)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_CALLSIGN = Pattern.compile ("^[A-Z0-9]+$", Pattern.CASE_INSENSITIVE);
    private static final Pattern RE_DOF = Pattern.compile("\\bDOF\\s*/\\s*(\\S+)");
    
    private final AmhsMessageContext ctx;
    private final FlightMessage m;

}
