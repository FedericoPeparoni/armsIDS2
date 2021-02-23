package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.util.models.ApplicationConstants;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;

/**
 * Calculate billable a route string from ATC log
 * <p>
 * This utility class is for creating a route string from fir entry/exit/mid points
 * of an ATC log entry; while resolving aerodromes to their coordinates. The returned
 * string is compatible with route parser.
 *
 * @author dpanech
 */
@Component
public class AtcMovementLogBillableRouteFinder {

    public AtcMovementLogBillableRouteFinder (
            final FlightMovementAerodromeService flightMovementAerodromeService) {
        this.flightMovementAerodromeService = flightMovementAerodromeService;
    }

    /**
     * Return a route string based on the given ATC log item's FIR entry/exit/mid points.
     *
     * @param logEntry - ATC log entry; its firEntryPoint, firExitPoint, firMidPoint, departureAerodrome and destinationAerodrome will be used.
     * @param flightMovement - flight movement, its movementType, depAd, item18Dep, destAd and item18Dest will be used
     */
    public String getBillableRouteString (final FlightMovement flightMovement, final AtcMovementLog logEntry) {

        // Create a route from entry/exit/mid points; the mid point is optional
        final String firstLocation = do_getFirstLocation (flightMovement, logEntry);
        final String lastLocation = do_getLastLocation (flightMovement, logEntry);
        if (firstLocation != null && lastLocation != null) {
            final String midLocation = do_getMidLocation (logEntry);
            if (midLocation != null) {
                return String.format ("%s %s %s", firstLocation, midLocation, lastLocation);
            }
            return String.format ("%s %s", firstLocation, lastLocation);
        }
        return null;
    }

    // -------------------------------- private -------------------------------

    /**
     * Get the starting point of the route; possibly resolved to coordinates
     */
    private String do_getFirstLocation (final FlightMovement flightMovement, final AtcMovementLog logEntry) {
        String resolved;

        // Try to resolve ATC entry point
        final String firEntryPoint = do_normalizeString (logEntry.getFirEntryPoint());
        resolved = this.flightMovementAerodromeService.resolveLocation(firEntryPoint);
        if (resolved != null) {
            return resolved;
        }

        // FIR entry point is missing, try departure aerodromes
        if (firEntryPoint == null) {

            // If this flight departs from a domestic airport, try to resolve departure airport to coordinates
            if (flightMovement.getFlightCategoryType() != null && 
            		(flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE)||
            				flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC))) {

                // Try to resolve ATC departure aerodrome to coordinates
                final String depAd = do_normalizeString (logEntry.getDepartureAerodrome());
                if (depAd != null) {
                    resolved = this.flightMovementAerodromeService.resolveLocation(depAd);
                    if (resolved != null) {
                        return resolved;
                    }
                }

                // Try to resolve FPL item18 departure field to coordinates
                final String item18Dep = do_normalizeString (flightMovement.getItem18Dep());
                if (item18Dep != null) {
                    resolved = this.flightMovementAerodromeService.resolveLocation(item18Dep);
                    if (resolved != null) {
                        return resolved;
                    }
                    // If item 18 departure can't be resolved, it's probably a navaid or similar, just return it
                    return item18Dep;
                }

            }

        }

        return firEntryPoint;
    }

    /**
     * Get the end point of the route; possibly resolved to coordinates
     */
    private String do_getLastLocation (final FlightMovement flightMovement, final AtcMovementLog logEntry) {
        String resolved;

        // Try to resolve ATC exit point
        final String firExitPoint = do_normalizeString (logEntry.getFirExitPoint());
        resolved = this.flightMovementAerodromeService.resolveLocation(firExitPoint);
        if (resolved != null) {
            return resolved;
        }

        // Exit point is missing, try destination aerodromes
        if (firExitPoint == null) {

            // If this flight arrives at a domestic airport, try to resolve destination airport to coordinates
            if (flightMovement.getFlightCategoryType() != null && 
            		(flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL) ||
            				flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DOMESTIC))) {

                // Try to resolve destination airport from the ATC log
                final String atcDestAd = do_normalizeString (logEntry.getDestinationAerodrome());
                if (atcDestAd != null) {
                    resolved = this.flightMovementAerodromeService.resolveLocation(atcDestAd);
                    if (resolved != null) {
                        return resolved;
                    }
                }

                // Try to resolve flight plan destination aerodrome:
                //   - don't try to resolve ZZZZ
                //   - don't try to resolve if it's the same as ATC destination aerodrome, because we already checked it
                final String fplDestAd = do_normalizeString (flightMovement.getDestAd());
                if (fplDestAd != null && !fplDestAd.equals(ApplicationConstants.PLACEHOLDER_ZZZZ) && (atcDestAd == null || !fplDestAd.equals(atcDestAd))) {
                    resolved = this.flightMovementAerodromeService.resolveLocation(fplDestAd);
                    if (resolved != null) {
                        return resolved;
                    }
                }

                // Try to resolve flight plan's item 18 destination
                //   - don't try to resolve if it's the same as ATC destination aerodrome, because we already checked it
                final String fplItem18Dest = do_normalizeString (Item18Parser.getFirstAerodromeOrDMS(flightMovement.getItem18Dest()));
                if (fplItem18Dest != null && (atcDestAd == null || !fplItem18Dest.equals(atcDestAd))) {
                    resolved = this.flightMovementAerodromeService.resolveLocation(fplDestAd);
                    if (resolved != null) {
                        return resolved;
                    }
                    // If we couldn't resolve item18, but it's not empty, it's probably a navaid or similar, just return it
                    return fplItem18Dest;
                }

            }

        }
        return firExitPoint;
    }

    /**
     * Get the midpoint for the route; possibly resolved to coordinates
     */
    private String do_getMidLocation (final AtcMovementLog logEntry) {
        String resolved;

        // Try to resolve the mid point
        final String firMidPoint = do_normalizeString (logEntry.getFirMidPoint());
        resolved = this.flightMovementAerodromeService.resolveLocation(firMidPoint);
        if (resolved != null) {
            return resolved;
        }

        return firMidPoint;
    }


    /**
     * Trim a string; convert empty strings to null
     */
    private static String do_normalizeString (String s) {
        if (s != null) {
            s = s.trim();
            if (s.isEmpty()) {
                return null;
            }
        }
        return s;
    }

    private final FlightMovementAerodromeService flightMovementAerodromeService;

}
