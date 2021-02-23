package ca.ids.abms.modules.atcmovements;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodA;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodC;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This component estimates missing dayOfFlight and departueTime fields in AtcMovementLog records.
 */
@Component
class AtcMovementLogDepartureEstimator {

    private static final Logger LOG = LoggerFactory.getLogger (AtcMovementLogDepartureEstimator.class);

    private final DepartureEstimatorMethodA methodA;
    private final DepartureEstimatorMethodC methodC;

    AtcMovementLogDepartureEstimator(
        final DepartureEstimatorMethodA methodA, final DepartureEstimatorMethodC methodC
    ) {
        this.methodA = methodA;
        this.methodC = methodC;
    }

    /**
     * Estimate missing departure time if departure contact time does not exist.
     */
    void resolveMissingDepartureTime(final AtcMovementLog atcMovementLog) {

        // atc movement log must be defined and not contain a departure contact time
        if (hasInvalidFields(atcMovementLog))
            return;

        try {

            final LocalDateTime originalDayOfFlight = atcMovementLog.getDayOfFlight();
            final String originalDepTime = atcMovementLog.getDepartureTime();

            // map atc movement log entity to departure estimator model with fields
            // for all applicable departure estimator methods used
            DepartureEstimatorModel model = mapToModel(atcMovementLog);

            // use method A and then method C if required, method B is TBD
            // return immediately if unable to estimate departure
            DepartureEstimatorResult result = methodA.estimateDepartureTime(model);
            if (result == null) result = methodC.estimateDepartureTime(model);
            if (result == null) return;

            // update atc movement log day of flight and departure time from results
            atcMovementLog.setDayOfFlight(result.getDayOfFlight());
            atcMovementLog.setDepartureTime(result.getDepTime());

            if (!Objects.equals(originalDayOfFlight, atcMovementLog.getDayOfFlight()))
                LOG.debug ("{}: Adjusted dayOfFlight from {} to {}", atcMovementLog.getDisplayName(),
                    originalDayOfFlight, atcMovementLog.getDayOfFlight());

            if (!Objects.equals(originalDepTime, atcMovementLog.getDepartureTime()))
                LOG.debug ("{}: Adjusted departureTime from {} to {}", atcMovementLog.getDisplayName(),
                    originalDepTime, atcMovementLog.getDepartureTime());

        } catch (final Exception e) {
            LOG.error ("{}: Error while trying to guess missing departure time for: e",
                atcMovementLog.getDisplayName(), e.getMessage(), e);
        }
    }

    /**
     * Return true if atc movement log does not have valid fields to calculate departure and day of flight. Atc
     * movement log matching should only be preformed if either departure time or day of flight is not defined.
     */
    private boolean hasInvalidFields(final AtcMovementLog atcMovementLog) {

        boolean isInvalid;

        if (atcMovementLog == null)
            isInvalid = true;
        else
            isInvalid = atcMovementLog.getDayOfFlight() != null &&
                StringUtils.isNotBlank(atcMovementLog.getDepartureTime());

        return isInvalid;
    }

    /**
     * Map ATC Movement Log to Departure Estimator Model with only applicable fields.
     *
     * TimeOfContact is entry, mid, or exit points and date of contact is assumed to be
     * referencing the first available time.
     */
    private DepartureEstimatorModel mapToModel(final AtcMovementLog atcMovementLog) {
        return new DepartureEstimatorModel.Builder(atcMovementLog.getDisplayName())
            .flightId(atcMovementLog.getFlightId())
            .dateOfContact(atcMovementLog.getDateOfContact())
            .timeOfContact(ObjectUtils.firstNonNull(
                atcMovementLog.getFirEntryTime(), atcMovementLog.getFirMidTime(), atcMovementLog.getFirExitTime()))
            .depAd(atcMovementLog.getDepartureAerodrome())
            .regNum(atcMovementLog.getRegistration())
            .aircraftType(atcMovementLog.getAircraftType())
            .firEntryPoint(atcMovementLog.getFirEntryPoint())
            .firEntryTime(atcMovementLog.getFirEntryTime())
            .build();
    }
}
