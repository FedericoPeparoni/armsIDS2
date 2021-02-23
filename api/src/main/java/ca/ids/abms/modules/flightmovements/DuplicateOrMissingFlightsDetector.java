package ca.ids.abms.modules.flightmovements;

import org.apache.commons.lang.StringUtils;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DuplicateOrMissingFlightsDetector {

    private static final DateTimeFormatter DATE_TIME_PATTERN = DateTimeFormatter.ofPattern("HHmm");

    private DuplicateOrMissingFlightsDetector() {
    }

    /**
     * This utility method analyze a list of visual object representing the flight movements
     * to detect if there are duplicates or missing flights.
     * This method compares adjacent flights so the list has to be ordered by registration number, date of flight and
     * departure time. The markers aren't persistent in fact they will be used only in frontend to highlight the
     * duplicates or missing items properly.
     *
     * @param flights - a list of visual objects of type {FlightMovementViewModel}
     * @param minimumWindow - the minimum window for departure time matching
     * @param percentageOfEET - the percentage of EET for departure time matching
     */
    public static void analyze (final List<FlightMovementViewModel> flights,
                                int minimumWindow,
                                int percentageOfEET,
                                boolean showAllFlights,
                                boolean showActualDepDest) {
        if (flights != null && !flights.isEmpty()) {
            FlightMovementViewModel currentFlight = flights.get(0);
            boolean hold = false;
            for (int i = 1; i < flights.size(); i++) {
                final FlightMovementViewModel nextFlight = flights.get(i);

                /* Step one: determine if the flights are duplicated */

                if (areSimilarFlights(currentFlight, nextFlight, showActualDepDest)) {
                    final long firstDepTime = getDepartureTimeInSeconds(currentFlight);
                    final long secondDepTime = getDepartureTimeInSeconds(nextFlight);
                    final String eet = currentFlight.getEstimatedElapsedTime();
                    if (secondDepTime - firstDepTime < (long) (minimumWindow * 60)) {
                        currentFlight.setMarkedAsDuplicate(true);
                        nextFlight.setMarkedAsDuplicate(true);
                    } else if (eet != null) {
                        final long firstEET = getTimeInSeconds(eet);
                        if (secondDepTime - firstDepTime < ((long) (percentageOfEET / 100)) * firstEET) {
                            currentFlight.setMarkedAsDuplicate(true);
                            nextFlight.setMarkedAsDuplicate(true);
                        }
                    }
                }
                /* Step two: determine if there is a missing flight between the above flights */

                if (isThereMissingFlight(currentFlight, nextFlight, showActualDepDest)) {
                    if (hold) {
                        nextFlight.setMarkedAsMissingBeforeThis(true);
                    } else {
                        currentFlight.setMarkedAsFirstMissing(true);
                        nextFlight.setMarkedAsMissingBeforeThis(true);
                        hold = true;
                    }
                } else if (hold) {
                    currentFlight.setMarkedAsLastMissing(true);
                    hold = false;
                }
                currentFlight = nextFlight;
            }
            if (hold) {
                currentFlight.setMarkedAsLastMissing(true);
            }
        }
    }

    private static boolean areSimilarFlights (final FlightMovementViewModel first,
                                              final FlightMovementViewModel second,
                                              boolean showActualDepDest) {
        String firstDeparture;
        String firstDestination;
        String secondDeparture;
        String secondDestination;

        if (showActualDepDest) {
            firstDeparture = first.getActualDepAd();
            firstDestination = first.getActualDestAd();
            secondDeparture = second.getActualDepAd();
            secondDestination = second.getActualDestAd();
        } else {
            firstDeparture = first.getDepAd();
            firstDestination = first.getDestAd();
            secondDeparture = second.getDepAd();
            secondDestination = second.getDestAd();
        }

        return StringUtils.equals(first.getItem18RegNum(), second.getItem18RegNum())
            && StringUtils.equals(firstDeparture, secondDeparture)
            && StringUtils.equals(firstDestination, secondDestination);
    }

    private static boolean isThereMissingFlight (final FlightMovementViewModel before,
                                                 final FlightMovementViewModel after,
                                                 boolean showActualDepDest) {
        String beforeDestination;
        String afterDeparture;
        if (showActualDepDest) {
            beforeDestination = before.getActualDestAd();
            afterDeparture = after.getActualDepAd();
        } else {
            beforeDestination = before.getDestAd();
            afterDeparture = after.getDepAd();
        }
        return StringUtils.equals(before.getItem18RegNum(), after.getItem18RegNum())
            && !StringUtils.equals(beforeDestination, afterDeparture)
            && !after.isMarkedAsDuplicate();
    }

    private static long getDepartureTimeInSeconds (final FlightMovementViewModel flight) {
        return (flight.getDateOfFlight().toLocalDate().atStartOfDay(ZoneId.of("UTC")).toEpochSecond() +
            LocalTime.parse(flight.getDepTime(), DATE_TIME_PATTERN).toSecondOfDay());
    }

    private static long getTimeInSeconds (final String time) {
        return LocalTime.parse(time, DATE_TIME_PATTERN).toSecondOfDay();
    }
}
