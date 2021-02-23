package ca.ids.abms.modules.estimators.departure.methods;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Algorithm For Calculating Day-Of-Flight and Departure-Time
 *
 * Method A – Matching With Previous Flights
 *
 * Check for a matching flight id for the both the current and previous day.
 *
 * Use this if the only information available is:
 * - Flight Id
 * - Departure aerodrome
 * - Contact date and time
 *
 * If there is only one then
 * - Use its departure-time
 * - If it’s departure-time is earlier than the contact time, then day-of-flight is contact date
 *   otherwise day-of-flight is previous day.
 *
 * If there are none or more than one then repeat this for the dates one week prior.
 *
 * If the second search returns none or more than one then use either method B or C.
 */
@Component
public class DepartureEstimatorMethodA {

    private static final Logger LOG = LoggerFactory.getLogger(DepartureEstimatorMethodA.class);

    /**
     * Flight identifier must match ICAO standards in order to be valid for this matching method.
     */
    private static final String FLIGHT_ID_PATTERN = "^[a-zA-Z]{3}\\d+$";

    /**
     * Number of weeks the system should look back from date of contact for existing flight movements.
     */
    private static final Integer NUMBER_OF_WEEKS = 1;

    private final FlightMovementRepository flightMovementRepository;

    DepartureEstimatorMethodA(final FlightMovementRepository flightMovementRepository) {
        this.flightMovementRepository = flightMovementRepository;
    }

    /**
     * Guess departure time and date of flight by matching the model to an existing
     * flight movement.
     */
    public DepartureEstimatorResult estimateDepartureTime(final DepartureEstimatorModel model) {

        // model name string for log messages
        final String displayName = model.getDisplayName();
        LOG.debug("METHOD A: Trying to match departure time for {}", displayName);

        // return null if model does not contain enough information
        // to use method A when determining departure time
        if (!hasRequiredFields(model)) {
            LOG.debug ("METHOD A: unable to match departure time for {}: " +
                "one of required fields is missing or blank", displayName);
            return null;
        }

        // find existing flight movement from model on date of arrival for the number amount of weeks back
        // if no valid flight movement found that matches, return false to indicate failed attempt
        String depTime = null;
        LocalDateTime dateOfFlight = null;

        for (int i = 0; i <= NUMBER_OF_WEEKS; i++) {
            FlightMovement flightMovement = findExistingFlightMovement(model, model.getDateOfContact().minusWeeks(i));
            if (!hasRequiredFields(flightMovement)) {
                LOG.debug ("METHOD A: unable to match departure time on date of arrival '{}' for {}: one of required " +
                    "fields is missing for flight movement {}", model.getDateOfContact(), displayName, flightMovement);
            } else {
                depTime = flightMovement.getDepTime();
                dateOfFlight = flightMovement.getDateOfFlight().plusWeeks(i);
                break;
            }
        }

        // return null indicating no valid flight movements found
        if (depTime == null && dateOfFlight == null) {
            LOG.debug ("METHOD A: unable to match departure time for {}: " +
                "no valid matching flight movements found", displayName);
            return null;
        }

        // resolve departure time and day of flight from flight movement
        DepartureEstimatorResult result = new DepartureEstimatorResult.Builder()
            .setDayOfFlight(dateOfFlight)
            .setDepTime(depTime)
            .build();

        LOG.debug ("METHOD A: matched day of flight for {} as '{}'",
            displayName, result.getDayOfFlight());
        LOG.debug ("METHOD A: matched departure time for {} as '{}'",
            displayName, result.getDepTime());

        // done - return result
        return result;
    }

    /**
     * Find existing flight movement that matches the available information for method A.
     */
    private FlightMovement findExistingFlightMovement(final DepartureEstimatorModel model, final LocalDateTime dateOfArrival) {

        // find all existing flight movements that match the model data available for method A
        List<FlightMovement> flightMovements = flightMovementRepository.findAllByFlightIdAndDepAdAndDateOfContactAndTimeOfContact(
                model.getFlightId(), model.getDepAd(), dateOfArrival, dateOfArrival.minusDays(1), model.getTimeOfContact());

        // return first flight movement ONLY if their is only one flight movement returned
        return flightMovements.size() != 1 ? null
            : flightMovements.get(0) ;
    }

    /**
     * Return true if model has enough information to determine
     * departure time from previous flight movement using Method A.
     */
    private static boolean hasRequiredFields(final DepartureEstimatorModel model) {

        // only valid if model is not null and contains flight identifier,
        // departure aerodrome, and date of contact
        return model != null &&
            model.getDateOfContact() != null && StringUtils.isNotBlank(model.getTimeOfContact()) &&
            model.getFlightId() != null && model.getFlightId().matches(FLIGHT_ID_PATTERN) &&
            StringUtils.isNotBlank(model.getDepAd());
    }

    /**
     * Return true if flight movement has enough information to determine
     * departure time for model using Method A.
     */
    private static boolean hasRequiredFields(final FlightMovement flightMovement) {

        // only valid if flight movement is not null and contains date of flight,
        // and departure time
        return flightMovement != null &&
            flightMovement.getDateOfFlight() != null &&
            StringUtils.isNotBlank(flightMovement.getDepTime());
    }
}
