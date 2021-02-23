package ca.ids.abms.modules.flightmovementsbuilder.utility;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Item15Parser {

    private static final int GROUP_OF_INTEREST = 1;

    private static final Pattern THRU_PATTERN = Pattern.compile("(?:THRU|TRU)\\s*(?:PLAN|PLN)(.*)");

    private static final Pattern EET_PATTERN = Pattern.compile("(\\b\\d{4})");

    private static final Pattern AERODROME_PATTERN = Pattern.compile("([A-Z]{4,})");

    private static final Pattern SPEED_M_PATTERN = Pattern.compile("(^[Mm]([0-9]{3}))");

    private static final Pattern SPEED_K_N_PATTERN = Pattern.compile("(^([KkNn])([0-9]{4}))");

    private static final Pattern
        FLIGHT_LEVEL_PATTERN = Pattern.compile("(F\\d{3})");

    public static String parse(String route, Item15Field aField) {
        String parseValue = null;
        if (route != null && !route.isEmpty() && aField != null) {
            switch (aField) {
            case THRU:
                parseValue = getParsedValue(THRU_PATTERN, route, 0);
                break;
            case CRUISING_SPEED:
                parseValue = getParsedValue(SPEED_M_PATTERN, route, GROUP_OF_INTEREST);
                if (parseValue == null) {
                    parseValue = getParsedValue(SPEED_K_N_PATTERN, route, GROUP_OF_INTEREST);
                }
                break;
            case FLIGHT_LEVEL:
                parseValue = getParsedValue(FLIGHT_LEVEL_PATTERN, route, GROUP_OF_INTEREST);
                break;
            case EET:
                parseValue = getParsedValue(EET_PATTERN, route, 0);
                break;
            case AERODROME:
                parseValue = getParsedValue(AERODROME_PATTERN, route, 0);
                break;
            default:
                break;
            }
        }

        if (parseValue != null && parseValue.isEmpty()) {
            parseValue = null;
        }

        return parseValue;
    }

    private static String getParsedValue(final Pattern pattern, final String route, final int group) {
        String parseValue = null;
        Matcher matcher;
        matcher = pattern.matcher(route);
        if (matcher.find()) {
            parseValue = matcher.group(group);
            parseValue = parseValue.trim();
        }
        return parseValue;
    }
}
