package ca.ids.abms.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.io.WKTReader;

public class GeometryUtils {

    private static final Logger log = Logger.getLogger(GeometryUtils.class);
    
    public enum CoordinateFormatPrecision {
        DEG,
        DEG_MIN,
        DEG_MIN_SEC
    }

    /**
     * Parse a coordinate string such as "010203N0040506E" or "01 02 03 N 004 05 06 E"
     */
    public static Coordinate parseAviationCoordinate (final String s) {
        if (s != null) {
            final Matcher m = RE_AVIATION_COORDINATES.matcher(s.trim().toUpperCase (Locale.US));
            if (m.matches()) {
                final int latDeg = Integer.parseInt (m.group(1));
                final int latMin = m.group(2) == null ? 0 : Integer.parseInt (m.group(2));
                final int latSec = m.group(3) == null ? 0 : Integer.parseInt (m.group(3));
                final int latSign = m.group(4).equals ("N") ? 1 : -1;
                final double lat = ((double)latDeg + ((double)latMin) / 60 + ((double)latSec) / 60 / 60) * latSign;

                final int lngDeg = Integer.parseInt (m.group(5));
                final int lngMin = m.group(6) == null ? 0 : Integer.parseInt (m.group(6));
                final int lngSec = m.group(7) == null ? 0 : Integer.parseInt (m.group(7));
                final int lngSign = m.group(8).equals ("E") ? 1 : -1;
                final double lng = ((double)lngDeg + ((double)lngMin) / 60 + ((double)lngSec) / 60 / 60) * lngSign;

                return new Coordinate (lng, lat);
            }
        }
        return null;
    }
    private static final Pattern RE_AVIATION_COORDINATES = Pattern.compile("^(\\d{2})\\s*(\\d{2})?\\s*(\\d{2})?\\s*([NS])\\s*(\\d{3})\\s*(\\d{2})?\\s*(\\d{2})?\\s*([EW])$");

    /**
     * Format a coordinate into a string such as "010203N0040506E"
     */
    public static String formatAviationCoordinate (final Coordinate coord) {
        return formatAviationCoordinate (coord, null);
    }

    /**
     * Format a coordinate into a string such as "010203N0040506E"
     */
    private static String formatAviationCoordinate(final Coordinate coord, final CoordinateFormatPrecision prec) {
        if (coord != null) {
            return formatAviationCoordinate (coord.getOrdinate(Coordinate.Y), coord.getOrdinate(Coordinate.X), prec);
        }
        return null;
    }

    /**
     * Format a coordinate into a string such as "010203N0040506E"
     */
    public static String formatAviationCoordinate (double latitude, double longitude) {
        return formatAviationCoordinate (latitude, longitude, null);
    }

    /**
     * Format a coordinate into a string such as "010203N0040506E"
     */
    private static String formatAviationCoordinate(double latitude, double longitude, final CoordinateFormatPrecision prec) {
        double latAbs = Math.abs (latitude);
        if (latAbs > 90.0d) latAbs = latAbs % 90.0d;
        final String latH = latitude >= 0.0d ? "N" : "S";
        int latDeg = (int)Math.floor(latAbs);
        double latFrac = (latAbs - latDeg) * 60.0d;
        int latMin = (int)Math.floor(latFrac);
        latFrac = (latFrac - latMin) * 60.0d;
        int latSec = (int)Math.round(latFrac);
        if (latSec == 60) {
            latMin = latMin + 1;
            latSec = 0;
            if (latMin == 60) {
                latDeg = latDeg + 1;
                latMin = 0;
            }
        }

        double lngAbs = Math.abs (longitude);
        if (lngAbs > 180.0d) lngAbs = lngAbs % 180.0d;
        final String lngH = longitude >= 0.0d ? "E" : "W";
        int lngDeg = (int)Math.floor(lngAbs);
        double lngFrac = (lngAbs - lngDeg) * 60.0d;
        int lngMin = (int)Math.floor(lngFrac);
        lngFrac = (lngFrac - lngMin) * 60.0d;
        int lngSec = (int)Math.round(lngFrac);
        if (lngSec == 60) {
            lngMin = lngMin + 1;
            lngSec = 0;
            if (lngMin == 60) {
                lngDeg = lngDeg + 1;
                lngMin = 0;
            }
        }

        String coordStr;
        switch (prec == null ? CoordinateFormatPrecision.DEG_MIN_SEC : prec) {
        case DEG:
            coordStr = String.format ("%02d%s%03d%s",
                    latDeg, latH,
                    lngDeg, lngH);
            break;
        case DEG_MIN:
            coordStr = String.format ("%02d%02d%s%03d%02d%s",
                    latDeg, latMin, latH,
                    lngDeg, lngMin, lngH);
            break;
        case DEG_MIN_SEC:
        default:
            coordStr = String.format ("%02d%02d%02d%s%03d%02d%02d%s",
                    latDeg, latMin, latSec, latH,
                    lngDeg, lngMin, lngSec, lngH);
            break;
        }

        return coordStr;
    }
    
    public static Geometry lineStringToMultiLineString(Collection<LineString> geometries) {
        Geometry geom = null;
        
        String multilinestring = "MULTILINESTRING(";
        StringJoiner multiline = new StringJoiner(", ");
        Iterator<LineString> i = geometries.iterator();
        while(i.hasNext()) {
            Geometry g = i.next();
            String singleLinestring = g.toText();
            multiline.add(singleLinestring);
        }
    
        String lines = multiline.toString();
        
        lines=lines.replace("LINESTRING", "");
        multilinestring = multilinestring + lines + ")";  
    
        WKTReader reader = new WKTReader();
        
        try {
                geom = reader.read(multilinestring);
                geom.setSRID(4326);
        } catch(Exception ex) {
            log.warn(ex.getMessage());
        }
        
        return geom;
    }
    
    public static Geometry linestringFromPoints(String start, String end) {
        Geometry geom = null;
        if(StringUtils.isBlank(start) || StringUtils.isBlank(end))
            return null;
        
        List<Coordinate> lineCoordinates = new ArrayList<>();
        lineCoordinates.add(parseAviationCoordinate(start));
        lineCoordinates.add(parseAviationCoordinate(end));
        geom =new GeometryFactory().createLineString(lineCoordinates.toArray(new Coordinate[lineCoordinates.size()]));
        geom.setSRID(4326);
        
        return geom;
    }
}
    
