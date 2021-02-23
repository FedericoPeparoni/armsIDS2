package ca.ids.abms.modules.util.models.geometry;

import java.util.regex.Pattern;

/**
 * Created by c.talpa on 08/03/2017.
 */
public class CoordinatesPatternFormatter {

    public static final Pattern DEGREES_ONLY_PATTERN=Pattern.compile("([0-9]{2})([NnSs])\\s*([0-9]{3})([EeWw])");
    public static final Pattern DEGREES_ONLY_PATTERN_FRONT=Pattern.compile("([NnSs])([0-9]{2})\\s*([EeWw])([0-9]{3})");

    public static final Pattern DEGREES_MINUTES_PATTERN=Pattern.compile("([0-9]{4})([NnSs])\\s*([0-9]{5})([EeWw])");
    public static final Pattern DEGREES_MINUTES_PATTERN_FRONT=Pattern.compile("([NnSs])([0-9]{4})\\s*([EeWw])([0-9]{5})");

    public static final Pattern DEGREES_MINUTES_SECONDS_PATTERN=Pattern.compile("([0-9]{6})([NnSs])\\s*([0-9]{6,7})([EeWw])");
    public static final Pattern DEGREES_MINUTES_SECONDS_PATTERN_FRONT=Pattern.compile("([NnSs])([0-9]{6})\\s*([EeWw])([0-9]{6,7})");


    public static final String DEGREES_ONLY_FORMATTER="DDCPDDDCP";

    public static final String DEGREES_MINUTES_FORMATTER="DDMMCPDDDMMCP";

    public static final String DEGREES_MINUTES_SECONDS_FORMATTER="DDMMSSCPDDDMMSSCP";

}
