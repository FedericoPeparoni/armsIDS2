package ca.ids.abms.modules.towermovements;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodA;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * This component estimates missing dayOfFlight and departueTime fields in TowerMovementLog records.
 */
@Component
class TowerMovementLogDepartureEstimator {

    private static final Logger LOG = LoggerFactory.getLogger (TowerMovementLogDepartureEstimator.class);

    private final DepartureEstimatorMethodA methodA;

    TowerMovementLogDepartureEstimator(final DepartureEstimatorMethodA methodA) {
        this.methodA = methodA;
    }

    /**
     * Estimate missing departure time if departure contact time does not exist.
     */
    void resolveMissingDepartureTime(final TowerMovementLog towerMovementLog) {

        // tower movement log must be defined and not contain a departure contact time
        if (hasInvalidFields(towerMovementLog))
            return;

        try {

            final LocalDateTime originalDayOfFlight = towerMovementLog.getDayOfFlight();
            final String originalDepTime = towerMovementLog.getDepartureTime();

            // map tower movement log entity to departure estimator model with fields
            // for all applicable departure estimator methods used
            DepartureEstimatorModel model = mapToModel(towerMovementLog);

            // use method A only, method B TBD and method C does not apply
            DepartureEstimatorResult result = methodA.estimateDepartureTime(model);
            if (result == null) return;

            // update tower movement log day of flight and departure time from results
            towerMovementLog.setDayOfFlight(result.getDayOfFlight());
            towerMovementLog.setDepartureTime(result.getDepTime());

            if (!Objects.equals(originalDayOfFlight, towerMovementLog.getDayOfFlight()))
                LOG.debug ("{}: Adjusted dayOfFlight from {} to {}", towerMovementLog.getDisplayName(),
                    originalDayOfFlight, towerMovementLog.getDayOfFlight());

            if (!Objects.equals(originalDepTime, towerMovementLog.getDepartureTime()))
                LOG.debug ("{}: Adjusted departureTime from {} to {}", towerMovementLog.getDisplayName(),
                    originalDepTime, towerMovementLog.getDepartureTime());

        } catch (final Exception e) {
            LOG.error ("{}: Error while trying to estimate missing departure time: {}",
                towerMovementLog.getDisplayName(), e.getMessage(), e);
        }
    }

    /**
     * Return true if tower movement log does not have valid fields to calculate departure and day of flight. Tower
     * movement log matching should only be preformed if their is no departure contact time and either departure time
     * or day of flight is not defined.
     */
    private boolean hasInvalidFields(final TowerMovementLog towerMovementLog) {

        boolean isInvalid;

        if (towerMovementLog == null)
            isInvalid = true;
        else if (StringUtils.isNotBlank(towerMovementLog.getDepartureContactTime()))
            isInvalid = true;
        else
            isInvalid = towerMovementLog.getDayOfFlight() != null &&
                StringUtils.isNotBlank(towerMovementLog.getDepartureTime());

        return isInvalid;
    }

    /**
     * Map tower movement log to departure estimator model with only applicable fields.
     *
     * TimeOfContact is dep or dest contact time and date of contact is assumed to be
     * referencing the first available time.
     */
    private DepartureEstimatorModel mapToModel(final TowerMovementLog towerMovementLog) {
        return new DepartureEstimatorModel.Builder(towerMovementLog.getDisplayName())
            .flightId(towerMovementLog.getFlightId())
            .dateOfContact(towerMovementLog.getDateOfContact())
            .timeOfContact(ObjectUtils.firstNonNull(
                towerMovementLog.getDepartureContactTime(), towerMovementLog.getDestinationContactTime()))
            .depAd(towerMovementLog.getDepartureAerodrome())
            .build();
    }
}
