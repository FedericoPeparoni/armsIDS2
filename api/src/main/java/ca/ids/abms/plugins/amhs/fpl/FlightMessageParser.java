package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import ca.ids.abms.amhs.AmhsMessage;
import ca.ids.abms.plugins.amhs.AmhsMessageCategory;
import ca.ids.abms.plugins.amhs.AmhsMessageType;
import ca.ids.abms.plugins.amhs.AmhsMessageTypeDetector;
import ca.ids.abms.plugins.amhs.AmhsParsedMessage;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;

import static ca.ids.abms.plugins.amhs.fpl.FlightMessageParserUtils.*;
import static org.apache.commons.lang3.StringUtils.stripToNull;
import static org.apache.commons.lang3.StringUtils.normalizeSpace;

@SuppressWarnings ("squid:S1172")
@Component
public class FlightMessageParser {
    
    FlightMessage parse (final String body, final LocalDateTime filingDateTime) {
        final AmhsMessage m = new AmhsMessage();
        m.setBody(body);
        m.setFilingDateTime(filingDateTime);
        m.setFilename("UNKNOWN");
        final AmhsMessageType type = AmhsMessageTypeDetector.guessType (body);
        final AmhsParsedMessage amhsParsedMessage = new AmhsParsedMessage (m, type);
        final AmhsMessageContext ctx = new AmhsMessageContext (amhsParsedMessage);
        amhsParsedMessage.flightMessage = new FlightMessage();
        parse (ctx);
        return amhsParsedMessage.flightMessage;
    }

    public void parse(final AmhsMessageContext ctx) {
        final AmhsParsedMessage p = ctx.getAmhsParsedMessage();
        final AmhsMessageType type = p.amhsMessageType;
        Preconditions.checkArgument (type.getCategory() == AmhsMessageCategory.FPL);
        p.flightMessage = new FlightMessage();
        final Consumer <AmhsMessageContext> parser = parserMap.get (type);
        parser.accept (ctx);
    }
    
    // -------------------- private --------------------------------
    
    private static EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> parserMap = createParserMap();
    
    private static EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> createParserMap() {
        final EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> m = new EnumMap<> (AmhsMessageType.class);
        m.put (AmhsMessageType.FPL, FlightMessageParser::parseFpl);
        m.put (AmhsMessageType.DLA, FlightMessageParser::parseDla);
        m.put (AmhsMessageType.CNL, FlightMessageParser::parseCnl);
        m.put (AmhsMessageType.DEP, FlightMessageParser::parseDep);
        m.put (AmhsMessageType.ARR, FlightMessageParser::parseArr);
        m.put (AmhsMessageType.CHG, FlightMessageParser::parseChg);
        return m;
    }

    private static void parseFpl(final AmhsMessageContext ctx) {
        final List <String> parts = getFplTokens (ctx.getBody(), 9);
        ctx.check (parts.size() >= 8, "expecting 8 or 9 dash-separated tokens");
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;
        
        m.item3 = parseItem3 (ctx, parts.get(0), ctx.getType());
        m.item7 = parseItem7 (ctx, parts.get(1));
        m.item8 = parseItem8 (ctx, parts.get(2));
        m.item9 = parseItem9 (ctx, parts.get(3));
        m.item10 = parseItem10 (ctx, parts.get(4));
        m.item13 = parseItem13 (ctx, parts.get(5));
        m.item15 = parseItem15 (ctx, parts.get(6));
        m.item16 = parseItem16 (ctx, parts.get(7));
        
        if (parts.size() >= 9) {
            m.item18 = parseItem18(ctx, parts.get(8));
        }
    }

    private static void parseDla(final AmhsMessageContext ctx) {
        final List <String> parts = getFplTokens (ctx.getBody(), 5);
        ctx.check (parts.size() >= 4, "expecting 4 or 5 dash-separated tokens");
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;

        m.item3 = parseItem3 (ctx, parts.get(0), ctx.getType());
        m.item7 = parseItem7 (ctx, parts.get(1));
        m.item13 = parseItem13 (ctx, parts.get(2));
        m.item16 = parseItem16 (ctx, parts.get(3));

        if (parts.size() >= 5) {
            m.item18 = parseItem18(ctx, parts.get(4));
        }
    }

    private static void parseCnl(final AmhsMessageContext ctx) {
        // same as DLA
        parseDla (ctx);
    }

    private static void parseDep(final AmhsMessageContext ctx) {
        // same as DLA
        parseDla (ctx);
    }

    /*
     * ARR:
     *  item3
     *  item7
     *  item13
     *  item16 or item17
     *  item18
     */
    private static void parseArr(final AmhsMessageContext ctx) {
        final List <String> parts = getFplTokens (ctx.getBody(), 5);
        ctx.check (parts.size() >= 4, "expecting 4 or 5 dash-separated tokens");
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;

        m.item3 = parseItem3 (ctx, parts.get(0), ctx.getType());
        m.item7 = parseItem7 (ctx, parts.get(1));
        m.item13 = parseItem13 (ctx, parts.get(2));
        
        Item17 item17 = parseItem17(ctx, parts.get(3));
        if (!item17.isEmpty()) {
            m.item17 = item17;
        }
        else {
            m.item16 = parseItem16 (ctx, parts.get(3));
        }
        if (parts.size() >= 5) {
            m.item18 = parseItem18(ctx, parts.get(4));
        }
    }
    
    /*
     * CHG:
     *  item3
     *  item7
     *  item13
     *  item16
     *  item18 (optional)
     *  item22 (repeated)
     */
    private static void parseChg(final AmhsMessageContext ctx) {
        final List <String> parts = getFplTokens (ctx.getBody(), 5);
        ctx.check (parts.size() >= 4, "expecting 4 or ore more dash-separated tokens");
        final FlightMessage m = ctx.getAmhsParsedMessage().flightMessage;

        m.item3 = parseItem3 (ctx, parts.get(0), ctx.getType());
        m.item7 = parseItem7 (ctx, parts.get(1));
        m.item13 = parseItem13 (ctx, parts.get(2));
        m.item16 = parseItem16 (ctx, parts.get(3));
        
        if (parts.size() > 4) {
            // If next item doesn't look like item 22, assume its item18
            if (!RE_ITEM22_START.matcher(parts.get (4)).find()) {
                // split it on dash
                final List <String> moreParts = getFplTokens(parts.get(4), 2);
                if (!moreParts.isEmpty()) {
                    // first part before dash is item18
                    m.item18 = parseItem18 (ctx, moreParts.get(0));
                    // rest is item 22
                    if (moreParts.size() > 1) {
                        m.item22 = parseItem22 (ctx, moreParts.get (1));
                    }
                }
                return;
            }
            // assume its item 22
            m.item22 = parseItem22 (ctx, parts.get (4));
        }
    }
    
    // ----------------------------- item3 --------------------------------------
    
    private static Item3 parseItem3(final AmhsMessageContext ctx, final String text, final AmhsMessageType type) {
        final String msgNumRef = stripToNull (text.substring(3));
        return new Item3 (text, type, msgNumRef);
    }

    // ----------------------------- item7 --------------------------------------
    
    private static Item7 parseItem7(final AmhsMessageContext ctx, final String text) {
        final int slash = text.indexOf('/');
        if (slash >= 0) {
            return new Item7 (text, stripToNull(text.substring(0, slash)));
        }
        return new Item7(text, text);
    }

    // ----------------------------- item8 --------------------------------------

    private static Item8 parseItem8(final AmhsMessageContext ctx, final String text) {
        if (text.length() > 0) {
            if (text.length() > 1) {
                return new Item8 (text, stripToNull(text.substring(0, 1)), stripToNull(text.substring(1, 2)));
            }
            return new Item8(text, stripToNull(text), null);
        }
        return new Item8(text, null, null);
    }

    // ----------------------------- item9 --------------------------------------

    private static final Pattern RE_AIRCRAFT_NUM_TYPE_1 = Pattern.compile("([0-9]{2})([a-zA-Z0-9]{2,4})");
    private static final Pattern RE_AIRCRAFT_NUM_TYPE_2 = Pattern.compile("[a-zA-Z0-9]{2,4}");

    @SuppressWarnings ("squid:AssignmentInSubExpressionCheck")
    private static Item9 parseItem9(final AmhsMessageContext ctx, final String text) {
        final int slash = text.indexOf('/');
        final String numberAndType = slash >= 0 ? text.substring(0, slash) : text;
        String aircraftNumber = null;
        String aircraftType = null;
        Matcher m;
        if ((m = RE_AIRCRAFT_NUM_TYPE_1.matcher(numberAndType)).matches()) {
            aircraftNumber = m.group(1);
            aircraftType = m.group(2);
        } else if ((m = RE_AIRCRAFT_NUM_TYPE_2.matcher(numberAndType)).matches()) {
            aircraftType = m.group(0);
        }
        final String wakeTurb = slash >= 0 ? StringUtils.strip(stripToNull(text.substring(slash + 1))) : null;
        return new Item9(text, aircraftNumber, aircraftType, wakeTurb);
    }

    // ----------------------------- item10 --------------------------------------
    
    private static Item10 parseItem10(final AmhsMessageContext ctx, final String text) {
        return new Item10 (text, text);
    }

    // ----------------------------- item13 --------------------------------------

    private static Item13 parseItem13(final AmhsMessageContext ctx, final String text) {
        String departureAirport = null;
        String departureTime = null;
        if (text.length() >= 4) {
            departureAirport = text.substring(0, 4);
            if (text.length() >= 5) {
                departureTime = text.substring(4);
            }
        }
        return new Item13(text, departureAirport, departureTime);
    }

    // ----------------------------- item15 --------------------------------------

    private static final Pattern RE_ITEM_15 = Pattern.compile("((?:[KN][0-9]{4})|(?:[M][0-9]{3}))" + // speed
            "((?:[AF][0-9]{3})|(?:[MS][0-9]{4})|(?:VFR))" + // level
            "\\s*/*\\s*(.+?)\\s*", // rest
            Pattern.DOTALL);

    private static Item15 parseItem15(final AmhsMessageContext ctx, final String text) {
        final Matcher m = RE_ITEM_15.matcher(text);
        if (m.matches()) {
            return new Item15(text, m.group(1), m.group(2), normalizeSpace (stripToNull(m.group(3))));
        }
        return new Item15(text, null, null, null);
    }

    // ----------------------------- item16 --------------------------------------

    @SuppressWarnings ("squid:S5843")
    private static final Pattern RE_ITEM_16 = Pattern
            .compile("^\\s*([A-Z0-9]{4})(?:\\s*([0-9]{3,4})(?:\\s*([A-Z0-9]{4})(?:\\s*([A-Z0-9]{4}))?)?)?\\s*$");

    private static Item16 parseItem16(final AmhsMessageContext ctx, final String text) {
        final Matcher m = RE_ITEM_16.matcher(text);
        if (m.matches()) {
            return new Item16(text, m.group(1), stripToNull(m.group(3)), stripToNull(m.group(4)), stripToNull(m.group(2)));
        }
        return new Item16(text, null, null, null, null);
    }

    // ----------------------------- item17 --------------------------------------
    private static Item17 parseItem17(final AmhsMessageContext ctx, final String text) {
        final Matcher m = RE_ITEM_17.matcher(text);
        if (m.matches()) {
            return new Item17(text, m.group(1), m.group(2), StringUtils.trimToNull (m.group(3)));
        }
        return new Item17(text, null, null, null);
    }
    private static final Pattern RE_ITEM_17 = Pattern
            .compile("^\\s*([A-Z0-9]{4})\\s*([0-9]{4})(?:\\s*(.*?))\\s*$");

    // ----------------------------- item18 --------------------------------------
    private static Item18 parseItem18(final AmhsMessageContext ctx, final String text) {
        return new Item18 (text, null, normalizeSpace (stripToNull (text)));
    }
    
    // ----------------------------- item22 --------------------------------------
    private static Item22 parseItem22(final AmhsMessageContext ctx, final String text) {
        final List <String> parts = getFplTokens (text, 999);
        final List <ItemBase> items = new ArrayList<> (parts.size());
        for (final String p: parts) {
            final Matcher m = RE_ITEM22.matcher (p);
            if (!m.matches()) {
                ctx.error("Syntax error in item 22, epecting NUM/CONTENT, found=[{0}]",
                        ca.ids.abms.util.StringUtils.abbrev(p));
                continue;
            }
            final Integer itemCode = Integer.parseInt(m.group(1));
            final ItemParserMapping im = ITEM_PARSER_MAPPING.get (itemCode);
            if (im == null) {
                LOG.warn ("ignoring unsupported item {} in amendments field", itemCode);
                continue;
            }
            final ItemBase item = im.parseFunc.apply (ctx, m.group(2));
            items.add (item);
        }
        return new Item22 (text, items);
    }
    private static final Pattern RE_ITEM22 = Pattern.compile ("^\\s*(\\d{1,2})\\s*/\\s*(.+?)\\s*$", Pattern.DOTALL);
    private static final Pattern RE_ITEM22_START = Pattern.compile ("^\\s*(\\d{1,2})\\s*/");
    private static class ItemParserMapping {
        final BiFunction <AmhsMessageContext, String, ItemBase> parseFunc;
        public ItemParserMapping (final BiFunction <AmhsMessageContext, String, ItemBase> parseFunc) {
            this.parseFunc = parseFunc;
        }
    }
    private static final Map <Integer, ItemParserMapping> ITEM_PARSER_MAPPING = createItemParserMapping();
    private static Map <Integer, ItemParserMapping> createItemParserMapping() {
        Map <Integer, ItemParserMapping> map = new HashMap <>();
        map.put (7, new ItemParserMapping (FlightMessageParser::parseItem7));
        map.put (8, new ItemParserMapping (FlightMessageParser::parseItem8));
        map.put (9, new ItemParserMapping (FlightMessageParser::parseItem9));
        map.put (10, new ItemParserMapping (FlightMessageParser::parseItem10));
        map.put (13, new ItemParserMapping (FlightMessageParser::parseItem13));
        map.put (15, new ItemParserMapping (FlightMessageParser::parseItem15));
        map.put (16, new ItemParserMapping (FlightMessageParser::parseItem16));
        map.put (18, new ItemParserMapping (FlightMessageParser::parseItem18));
        return map;
    }
    
    // ---------------------------------------------------------------------------
    
    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(FlightMessageParser.class);

}
