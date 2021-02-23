package ca.ids.abms.modules.flightmovementsbuilder.utility;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Objects;

public class RouteUtility {
    

    /**
     * Add entry/exit points to a route route string if they are not already in it
     */
    public static String addRouteEndPoints (final String routeString, final String entryPoint, final String exitPoint) {
        final LinkedList <String> points = parseRouteString (routeString);
        final String normEntryPoint = StringUtils.stripToNull(entryPoint);
        if (normEntryPoint != null && (points.isEmpty() || !samePoint (points.get(0), normEntryPoint))) {
            points.add (0, normEntryPoint);
        }
        final String normExitPoint = StringUtils.stripToNull (exitPoint);
        if (normExitPoint != null && (points.isEmpty() || !samePoint (points.get(points.size() - 1), normExitPoint))) {
            points.add (normExitPoint);
        }
        return points.stream().collect (Collectors.joining(" "));
    }
    
    

    private static LinkedList<String> parseRouteString (final String routeString) {
        if (StringUtils.isBlank (routeString)) {
            return new LinkedList<>();
        }
        final List <String> list = RE_WS.splitAsStream(routeString)
            .filter (StringUtils::isNotBlank)
            .collect (Collectors.toList())
        ;
        return new LinkedList<> (list);
    }
    private static String pointIdent (final String fixToken) {
        if (fixToken != null) {
            return RE_ROUTE_FIX_CLEANUP.matcher (fixToken).replaceAll("");
        }
        return null;
    }
    private static boolean samePoint (final String p1, final String p2) {
        return Objects.equal (pointIdent (p1), pointIdent (p2));
    }
    
    private static final Pattern RE_WS = Pattern.compile ("\\s+");
    private static final Pattern RE_ROUTE_FIX_CLEANUP = Pattern.compile ("[^a-zA-Z0-9].*");
    private RouteUtility() {}
}
