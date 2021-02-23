package ca.ids.abms.modules.radarsummary;

import java.time.LocalDateTime;
import java.util.Objects;

import ca.ids.abms.modules.estimators.departure.DepartureEstimatorModel;
import ca.ids.abms.modules.estimators.departure.DepartureEstimatorResult;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodA;
import ca.ids.abms.modules.estimators.departure.methods.DepartureEstimatorMethodC;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * This component estimates missing dayOfFlight and departueTime fields in RadarSummary records.
 */
@Component
public class RadarSummaryDepartureEstimator {

    private static final Logger LOG = LoggerFactory.getLogger (RadarSummaryDepartureEstimator.class);

    private final DepartureEstimatorMethodA methodA;
    private final DepartureEstimatorMethodC methodC;

    RadarSummaryDepartureEstimator(
        final DepartureEstimatorMethodA methodA,
        final DepartureEstimatorMethodC methodC
    ) {
        this.methodA = methodA;
        this.methodC = methodC;
    }

    /**
     * Estimate missing departure time if departure time or day of flight does not exist.
     */
    void resolveMissingDepartureTime(final RadarSummary radarSummary) {

        // radar summary must be defined and not contain departure time or day of flight
        if (hasInvalidFields(radarSummary))
            return;

        try {

            final LocalDateTime originalDayOfFlight = radarSummary.getDayOfFlight();
            final String originalDepTime = radarSummary.getDepartureTime();

            // map radar summary entity to departure estimator model with fields
            // for all applicable departure estimator methods used
            DepartureEstimatorModel model = mapToModel(radarSummary);

            // use method A and then method C if required, method B is TBD
            // return immediately if unable to estimate departure
            DepartureEstimatorResult result = methodA.estimateDepartureTime(model);
            if (result == null) result = methodC.estimateDepartureTime(model);
            if (result == null) return;

            // update radar summary day of flight and departure time from results
            radarSummary.setDayOfFlight(result.getDayOfFlight());
            radarSummary.setDepartureTime(result.getDepTime());

            if (!Objects.equals(originalDayOfFlight, radarSummary.getDayOfFlight()))
                LOG.debug ("{}: Adjusted dayOfFlight from {} to {}", radarSummary.getDisplayName(),
                    originalDayOfFlight, radarSummary.getDayOfFlight());

            if (!Objects.equals(originalDepTime, radarSummary.getDepartureTime()))
                LOG.debug ("{}: Adjusted departureTime from {} to {}", radarSummary.getDisplayName(),
                    originalDepTime, radarSummary.getDepartureTime());

        } catch (final Exception e) {
            LOG.error ("{}: Error while trying to estimate missing departure time: {}",
                radarSummary.getDisplayName(), e.getMessage(), e);
        }
    }

    /**
     * Return true if radar summary does not have valid fields to calculate departure and day of flight. Radar
     * summary matching should only be preformed if either departure time or day of flight is not defined.
     */
    private boolean hasInvalidFields(final RadarSummary radarSummary) {

        boolean isInvalid;

        if (radarSummary == null)
            isInvalid = true;
        else
            isInvalid = radarSummary.getDayOfFlight() != null &&
                StringUtils.isNotBlank(radarSummary.getDepartureTime());

        return isInvalid;
    }

    /**
     * Map radar summary to departure estimator model with only applicable fields.
     */
    private DepartureEstimatorModel mapToModel(final RadarSummary radarSummary) {
        return new DepartureEstimatorModel.Builder(radarSummary.getDisplayName())
            .flightId(radarSummary.getFlightIdentifier())
            .dateOfContact(radarSummary.getDayOfFlight())
            .depAd(radarSummary.getDepartureAeroDrome())
            .regNum(radarSummary.getRegistration())
            .aircraftType(radarSummary.getAircraftType())
            .cruisingSpeed(radarSummary.getCruisingSpeed())
            .firEntryPoint(radarSummary.getFirEntryPoint())
            .firEntryTime(radarSummary.getFirEntryTime())
            .build();
    }
}
