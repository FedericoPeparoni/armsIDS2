package ca.ids.abms.modules.estimators.contact;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.estimators.EstimatorUtility;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.StringUtils;

/**
 * Algorithm For Calculating Billing-Date for International/Regional Arrival
 *
 * Use either the actual arrival time or the departure time plus estimated elapsed time as the arrival time.
 * If the arrival time earlier than the departure time, set the billing date to the day after the day of flight.
 */
@Component
public class ContactEstimatorArrival {

    private static final Logger LOG = LoggerFactory.getLogger(ContactEstimatorArrival.class);

    public LocalDateTime estimateContactDateByArrival(ContactEstimatorModel model) {

        // model name string for log messages
        final String displayName = model.getDisplayName();
        LOG.debug("Estimate Contact Date By Arrival: Trying to estimate arrival time for {}", displayName);

        // return null if model does not contain enough information
        // to use method A when determining departure time
        if (!hasRequiredFields(model)) {
            LOG.debug ("Estimate Contact Date By Arrival: unable estimate arrivale time for {}: " +
                "one of required fields is missing or blank", displayName);
            return null;
        }

        if(model.getActualArrivalTime() != null) {
            // use actual arrival
            // parse entry time
            if (EstimatorUtility.tryParseTime (model.getActualArrivalTime()) != null) {
                return FlightUtility.resolveDateOfContact(model.getDeptime(), model.getActualArrivalTime(), model.getDateOfFlight());
            } else {
                LOG.debug ("Estimate Contact Date By Arrival: unable to estimate arrivale time for {}: actual arrival time \"{}\" is invalid",
                    displayName, model.getActualArrivalTime());
            }
        }

        // try to use departure time + eet
        return FlightUtility.resolveDateOfArrival(model.getDeptime(), model.getEet(), model.getDateOfFlight());
    }

    /**
     * Return true if model has enough information to determine
     * billing date for ARRIVAL flights
     */
    private boolean hasRequiredFields(final ContactEstimatorModel model) {

        // only valid if model is not null and contains departure aerodrome, date of flight,
        // departure time and eet or actual arrival time
        return model != null &&
            StringUtils.isNotBlank(model.getDepAd()) && model.getDateOfFlight() != null && (
                StringUtils.isNotBlank(model.getActualArrivalTime()) || (
                    StringUtils.isNotBlank(model.getDeptime()) && StringUtils.isNotBlank(model.getEet())));
    }
}
