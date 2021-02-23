package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.RouteSegmentRepository;
import com.google.common.base.Preconditions;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.persistence.LockModeType;
import javax.persistence.PessimisticLockException;

/**
 * Used to perform common repository operations such as detaching, overwriting, saving, locking, etc. on a
 * flight movement entity and related route segments.
 */
@Component
public class FlightMovementRepositoryUtility {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementRepositoryUtility.class);

    private final FlightMovementRepository flightMovementRepository;
    private final RouteSegmentRepository routeSegmentRepository;
    private final AccountService accountService;

    FlightMovementRepositoryUtility(final FlightMovementRepository flightMovementRepository,
                                    final RouteSegmentRepository routeSegmentRepository,
                                    final AccountService accountService) {
        this.flightMovementRepository = flightMovementRepository;
        this.routeSegmentRepository = routeSegmentRepository;
        this.accountService = accountService;
    }

    /**
     * Detach a flight movement and related route segments from persistence context. All related route segments
     * are loaded before detaching as they are a lazy loaded relationship.
     */
    public void detach(final FlightMovement flightMovement) {
        if (flightMovement == null) return;

        LOG.debug("Detaching {}", flightMovement);

        // must first load each route segment first into context as they are lazy loaded
        if (flightMovement.getRouteSegments() != null) {
            for (RouteSegment segment : flightMovement.getRouteSegments()) {
                routeSegmentRepository.detach(segment);
            }
        }

        // detach flight movement, this should also detach related route segments but it
        // was done above in case the cascade relationship is ever changed
        flightMovementRepository.detach(flightMovement);
    }

    /**
     * This method persist a FlightMovement but overwrites any existing entries ignoring
     * optimistic locking by version number. This should only be used for detached entities.
     *
     * Restriction: Only allows overwriting flight movements who's status has not been invoiced/paid or deleted.
     *
     * Limitation: This was requested by PO who wants surveillance log data to be stored even when the
     * flight movement has been modified im mean time, see TFS User Story 114575. This could cause problems where a
     * flight plan and a radar summary are imported at the exact same time. If so, then only the one to persist its
     * changes last will be be included within the flight movement object.
     *
     * NOTE: Creating and updating FlightMovement should be done from this method or
     * {@link #persist(FlightMovement)} only.
     */
    FlightMovement overwrite(final FlightMovement flightMovement) {
        if (flightMovement == null) return null;

        LOG.debug("Saving {}", flightMovement);

        // perform necessary logic for flight movements with a unique identifier
        if (flightMovement.getId() != null) {

            // validate that the status is still valid
            FlightMovementStatus status = flightMovementRepository.findFlightMovementStatus(flightMovement.getId());
            if (ObjectUtils.equals(status, FlightMovementStatus.INVOICED) || ObjectUtils.equals(status, FlightMovementStatus.PAID)
                || ObjectUtils.equals(status, FlightMovementStatus.DELETED) || ObjectUtils.equals(status, FlightMovementStatus.DECLINED)) {

                FlightMovementService.handleBadStatus(status, flightMovement.getDisplayName());
                return null;
            }

            // must remove existing route segments since overwrite is used for detached entities
            // and detached entities do not have changes persisted for related items
            routeSegmentRepository.removeAllByFlightMovement(flightMovement);
        }

        // overwrite all existing flight movement route segments
        if (flightMovement.getRouteSegments() != null) {
            for (RouteSegment routeSegment : flightMovement.getRouteSegments()) {
                routeSegment.setFlightMovement(flightMovement);
                routeSegmentRepository.overwrite(routeSegment);
            }
        }

        // overwrite existing flight movement
        return flightMovementRepository.overwrite(flightMovement);
    }

    /**
     *  This method persist FlightMovement respecting optimistic locking by version number.
     *
     *  NOTE: Creating and updating FlightMovement should be done from this method or
     *  {@link #overwrite(FlightMovement)} only.
     *
     * @return flight movement persisted
     */
    public FlightMovement persist(final FlightMovement flightMovement){
        if (flightMovement == null) return null;

        LOG.debug("Saving {}", flightMovement);

        FlightMovement persistedFlightMovement = flightMovementRepository.save(flightMovement);
        if (flightMovement.getRouteSegments() != null) {
            for (RouteSegment routeSegment : flightMovement.getRouteSegments()) {
                routeSegment.setFlightMovement(persistedFlightMovement);
                routeSegmentRepository.save(routeSegment);
            }
        }

        //update account last activity date/time for Whitelisting
        accountService.updateAccountLastActivityDateTimeForWhitelisting(persistedFlightMovement.getAccount());

        return persistedFlightMovement;
    }

    /**
     * Refresh and apply pessimistically lock to a flight movement and related route segments.
     *
     * NOTE: If locking is used, Hibernate and JPA do not provide lock timeout configuration with PostgreSQL.
     * Therefore, it is possible that pessimistic lock exceptions are thrown if flight movement is already locked as
     * it will not wait when acquiring locks.
     */
    public void refreshAndLock(final FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);

        LOG.debug("Locking {}", flightMovement);

        try {

            // pessimistic force increment lock flight movement as they are versioned entities
            flightMovementRepository.refresh(flightMovement, LockModeType.PESSIMISTIC_FORCE_INCREMENT);

            // pessimistic write lock route segments as they are NOT versioned entities
            if (flightMovement.getRouteSegments() != null) {
                for (RouteSegment routeSegment : flightMovement.getRouteSegments()) {
                    routeSegmentRepository.refresh(routeSegment, LockModeType.PESSIMISTIC_WRITE);
                }
            }

        } catch (PessimisticLockException ex) {
            LOG.warn("Failed to obtain pessimistic lock because of '{}' for {}", ex.getMessage(), flightMovement);
            throw new CustomParametrizedException(ErrorConstants.ERR_CONCURRENCY_FAILURE, ex);
        }
    }
}
