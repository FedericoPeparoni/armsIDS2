package ca.ids.abms.modules.estimators.contact;

import java.time.LocalDateTime;
import java.time.LocalTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.vividsolutions.jts.geom.Coordinate;

import ca.ids.abms.modules.estimators.EstimatorUtility;
import ca.ids.abms.util.StringUtils;

/**
 * Calculate the distance between departure and the FIR entry point.
 * Use this distance and the flight speed to calculate the elapsed time from departure to the FIR entry point.
 * Add the elapsed time to the FIR entry point to the departure time.
 * If the FIR entry point time is earlier than the departure time, set the billing date to the day after the day of flight.
 */
@Component
public class ContactEstimatorOverflight {
    private static final Logger LOG = LoggerFactory.getLogger(ContactEstimatorOverflight.class);

    private final EstimatorUtility estimatorUtility;
    
    ContactEstimatorOverflight(final EstimatorUtility estimatorUtility){
        this.estimatorUtility=estimatorUtility;
    }
    
    public LocalDateTime estimateContactDateOverflight(ContactEstimatorModel model)
    {
        LocalDateTime result=null;
        // model name string for log messages
        final String displayName = model.getDisplayName();
        LOG.debug("Estimate Contact Date Overflight: Trying to estimate contact time for {}", displayName);

        // return null if model does not contain enough information
        // to use method A when determining departure time
        if (!hasRequiredFields(model)) {
            LOG.debug ("Estimate Contact Date Overflight: unable estimate arrivcontactale time for {}: " +
                "one of required fields is missing or blank", displayName);
            return null;
        }
        
        // parse entry time
        final LocalTime departureTime = EstimatorUtility.tryParseTime (model.getDeptime());
        if (departureTime == null) {
            LOG.debug ("Estimate Contact Date Overflight: unable to estimate contact time for {}: departure time \"{}\" is invalid",
                    displayName, model.getDeptime());
            return null;
        }

        // parse cruising speed into a "meters per second" value
        final Double speedMetersPerSecond = EstimatorUtility.tryParseCruisingSpeed (model.getCruisingSpeed());
        if (speedMetersPerSecond == null) {
            LOG.debug ("Estimate Contact Date Overflight: unable to estimate contact time for {}: cruising speed \"{}\" is invalid",
                    displayName, model.getCruisingSpeed());
            return null;
        }
        LOG.trace ("Estimate Contact Date Overflight: speed={} m/s", speedMetersPerSecond);

        // resolve departure aerodrome to a geographical location
        final Coordinate departureCoord = estimatorUtility.tryResolveAerodrome (model.getDepAd());
        if (departureCoord == null) {
            LOG.debug ("Estimate Contact Date Overflight: unable to estimate contact time for {}: departure aerodrome \"{}\" can't be resolved to a geographical location",
                    displayName, model.getDepAd());
            return null;
        }
        LOG.trace ("Estimate Contact Date Overflight: departureCoord={}", departureCoord);

        // resolve waypoint to a geographical location
        final Coordinate entryCoord = estimatorUtility.tryResolveEntryWaypoint (model.getContactPoint(), departureCoord);
        if (entryCoord == null) {
            LOG.debug ("Estimate Contact Date Overflight: unable to estimate contact time for {}: entry waypoint \"{}\" can't be resolved to a geographical location",
                    displayName, model.getContactPoint());
            return null;
        }
        LOG.trace ("Estimate Contact Date Overflight: entryCoord={}", entryCoord);

        // get the distance between departure and entry points in meters
        final Double distanceMeters = estimatorUtility.getGeoidDistance (departureCoord, entryCoord);
        if (distanceMeters == null) {
            LOG.debug ("Estimate Contact Date Overflight: unable to estimate contact time for {}: couldn't calculate distance between departure and entry point", displayName);
            return null;
        }
        LOG.trace ("Estimate Contact Date OverflightC: distance={} m", distanceMeters);

        // elapsed time in seconds
        final long secondsSinceDeparture = (long)(distanceMeters / speedMetersPerSecond);
        LOG.trace ("Estimate Contact Date Overflight: secondsSinceDeparture = distance/speed = {}", secondsSinceDeparture);

        // full date/time
        final LocalDateTime departureDateTime = LocalDateTime.of (model.getDateOfFlight().toLocalDate(), departureTime);

        // estimated departure time
        final LocalDateTime estimatedContactDateTime = departureDateTime.plusSeconds(secondsSinceDeparture);
        LOG.debug ("Estimate Contact Date Overflight: estimatedDepartureTime = {}", estimatedContactDateTime);
         
        result = estimatedContactDateTime.toLocalDate().atStartOfDay();
        return result;
    }

    /**
     * Return true if model has enough information to determine
     * for OVERFLIGHT
     */
    private boolean hasRequiredFields(final ContactEstimatorModel model) {

        // only valid if model is not null and contains departure aerodrome, date of flight,
        // departure time, cruising speed and contact point
        return model != null &&
            StringUtils.isNotBlank(model.getDepAd()) &&
            model.getDateOfFlight() != null &&
            StringUtils.isNotBlank(model.getDeptime()) &&
            StringUtils.isNotBlank(model.getCruisingSpeed()) &&
            StringUtils.isNotBlank(model.getContactPoint());
    }
}
