package ca.ids.abms.modules.selfcareportal.flightsearch;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
public class SCFlightSearchService {

    private static final Logger LOG = LoggerFactory.getLogger(SCFlightSearchService.class);

    private static final String ACCOUNT = "account";

    private final FlightMovementRepository flightMovementRepository;

    public SCFlightSearchService(final FlightMovementRepository flightMovementRepository) {
        this.flightMovementRepository = flightMovementRepository;
    }

    @SuppressWarnings("WeakerAccess")
    @Transactional(readOnly = true)
    public Page<FlightMovement> findAllFlightMovementByFilter(final Pageable pageable,
                                                              final Integer accountId,
                                                              final String flightId,
                                                              final String icaoCode,
                                                              final LocalDate startDate,
                                                              final LocalDate endDate,
                                                              final Integer userId) {
        LOG.debug("Request to get all flight movements for Self-Care accounts by accountId: {}, flightId: {}, icaoCode: {}, startDate: {}, endDate: {}, userId: {}",
            accountId, flightId, icaoCode, startDate, endDate, userId);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder();

        filterBuilder.restrictOn(JoinFilter.notEmpty(ACCOUNT, "accountUsers"));

        if (accountId != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, "id", accountId));
        }

        if (flightId != null) {
            filterBuilder.restrictOn(Filter.equals("flightId", flightId));

        }

        if (icaoCode != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, "icaoCode", icaoCode));
        }

        if (startDate != null || endDate != null) {
            LocalDateTime startAt;
            LocalDateTime endAt;
            if (startDate == null) {
                startAt = (LocalDateTime.now()).minusYears(1000);
            } else {
                startAt = startDate.atStartOfDay();
            }
            if (endDate == null) {
                endAt = (LocalDateTime.now()).plusYears(1000);
            } else {
                endAt = endDate.atTime(LocalTime.MAX);
            }
            filterBuilder.restrictOn(Filter.included("dateOfFlight", startAt, endAt));
        }

        if (userId != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, "accountUsers", "id", userId));
        }

        return flightMovementRepository.findAll(filterBuilder.build(), pageable);
    }

    public long countAllForSelfCareAccounts() {
        return flightMovementRepository.countAllForSelfCareAccounts();
    }

    public long countAllForSelfCareUser(int userId) {
        return flightMovementRepository.countAllForSelfCareUser(userId);
    }
}
