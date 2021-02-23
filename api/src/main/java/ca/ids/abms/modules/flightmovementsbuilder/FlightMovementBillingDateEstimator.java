package ca.ids.abms.modules.flightmovementsbuilder;

import java.time.LocalDateTime;

import ca.ids.abms.util.StringUtils;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.estimators.contact.ContactEstimatorArrival;
import ca.ids.abms.modules.estimators.contact.ContactEstimatorModel;
import ca.ids.abms.modules.estimators.contact.ContactEstimatorOverflight;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryType;

@Component
class FlightMovementBillingDateEstimator {

    private static final Logger LOG = LoggerFactory.getLogger (FlightMovementBillingDateEstimator.class);

    private final ContactEstimatorArrival contactEstimatorArrival;
    private final ContactEstimatorOverflight contactEstimatorOverflight;

    FlightMovementBillingDateEstimator(
        final ContactEstimatorArrival contactEstimatorArrival,
        final ContactEstimatorOverflight contactEstimatorOverflight
    ){
        this.contactEstimatorArrival = contactEstimatorArrival;
        this.contactEstimatorOverflight = contactEstimatorOverflight;
    }

    LocalDateTime resolveBillingDate(final FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        // default billing date to date of flight
        LocalDateTime billingDate = flightMovement.getDateOfFlight();

        // use default date of flight when missing crucial data
        if (flightMovement.getFlightCategoryScope() == null || flightMovement.getFlightCategoryType() == null) {
            LOG.warn("Unable to resolve billing date for {}: flight category scope or type are null.",
                flightMovement.getDisplayName());
        }

        // use date of flight for all DOMESTIC and DEPARTURE flight movements, no need for further calculations
        else if (flightMovement.getFlightCategoryScope().equals(FlightmovementCategoryScope.DOMESTIC) ||
            flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE)) {
            LOG.trace("Resolving billing date to date of flight for DOMESTIC or DEPARTURE flight movement.");
            billingDate = flightMovement.getDateOfFlight();
        }

        // Use either the actual arrival time or the departure time plus estimated elapsed time as the arrival time.
        // If the arrival time earlier than the departure time, set the billing date to the day after the day of flight.
        else if (flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.ARRIVAL)) {
            LOG.trace("Resolving billing date by contact date arrival for ARRIVAL flight movement.");
            LocalDateTime arrivalDate = contactEstimatorArrival.estimateContactDateByArrival(mapToModel(flightMovement));
            if (arrivalDate != null) billingDate = arrivalDate;
        }

        //	Calculate the distance between departure and the FIR entry point.
        //	Use this distance and the flight speed to calculate the elapsed time from departure to the FIR entry point.
        //	Add the elapsed time to the FIR entry point to the departure time.
        //	If the FIR entry point time is earlier than the departure time, set the billing date to the day after the day of flight.
        else if (flightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.OVERFLIGHT)) {
            LOG.trace("Resolving billing date by contact date overflight for OVERFLIGHT flight movement.");
            LocalDateTime contactDate =  contactEstimatorOverflight.estimateContactDateOverflight(mapToModel(flightMovement));
            if (contactDate != null) billingDate = contactDate;
        }

        LOG.debug("Resolved billing date to '{}' for {}.", billingDate, flightMovement.getDisplayName());
        return billingDate;
    }

    private ContactEstimatorModel mapToModel(final FlightMovement fm) {
        ContactEstimatorModel.Builder model = new ContactEstimatorModel.Builder(fm.getDisplayName());

        model.depAd(StringUtils.isNotBlank(fm.getActualDepAd())
            ? fm.getActualDepAd() : fm.getDepAd());
        model.cruisingSpeed(fm.getCruisingSpeedOrMachNumber());
        model.dateOfFlight(fm.getDateOfFlight());
        model.deptime(StringUtils.isNotBlank(fm.getActualDepartureTime())
            ? fm.getActualDepartureTime() : fm.getDepTime());
        model.actualArrivalTime(fm.getArrivalTime());
        model.eet(fm.getEstimatedElapsedTime());
        model.contactPoint(fm.getBillableEntryPoint());

        return model.build();
    }
}
