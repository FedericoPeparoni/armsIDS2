package ca.ids.abms.modules.flight;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.util.models.ModelUtils;

@Service
@Transactional
public class FlightReassignmentService {

    private static final Logger LOG = LoggerFactory.getLogger(FlightReassignmentService.class);

    private final FlightReassignmentRepository flightReassignmentRepository;

    private final AccountRepository accountRepository;
    private final FlightReassignmentAerodromeRepository flightReassignmentAerodromeRepository;

    private final static String ACCOUNT_ID = "id";

    public FlightReassignmentService(final FlightReassignmentRepository flightReassignmentRepository,
                                     final FlightReassignmentAerodromeRepository aFlightReassignmentAerodromeRepository,
                                     final AccountRepository accountRepository) {

        this.flightReassignmentRepository = flightReassignmentRepository;
        flightReassignmentAerodromeRepository = aFlightReassignmentAerodromeRepository;
        this.accountRepository = accountRepository;
    }

    public FlightReassignment create(final FlightReassignment flightReassignment,
                                     final List<FlightReassignmentAerodrome> anAerodromes) {
        LOG.debug("Request to create FlightReassignment ");
        Integer accId = flightReassignment.getAccount().getId();
        Account acc = accountRepository.getOne(accId);
        flightReassignment.setAccount(acc);
        Set<FlightReassignmentAerodrome> aerodromes = flightReassignment.getAerodromeIdentifiers();
        FlightReassignment saved = flightReassignmentRepository.save(flightReassignment);
        for (FlightReassignmentAerodrome anAerodrome : anAerodromes) {
            aerodromes.add(anAerodrome);
            anAerodrome.setFlightReassignment(saved);
            flightReassignmentAerodromeRepository.save(anAerodrome);
        }
        return saved;
    }

    public void delete(final Integer id) {
        LOG.debug("Request to delete FlightReassignment : {}", id);
        try {
            final FlightReassignment existingFlightReassignmentAerodrome = findOne(id);
            final Set<FlightReassignmentAerodrome> aerodromes = existingFlightReassignmentAerodrome
                    .getAerodromeIdentifiers();
            if (CollectionUtils.isNotEmpty(aerodromes)) {
                flightReassignmentAerodromeRepository.delete(aerodromes);
                flightReassignmentAerodromeRepository.flush();
            }
            flightReassignmentRepository.delete(id);
            flightReassignmentRepository.flush();
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<FlightReassignment> findAll(final Integer accountId, final String aTextSearch, final Pageable pageable) {
        LOG.debug("Request to find all Flight Reassignments");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(aTextSearch);

        if (accountId != null) {
            filterBuilder.restrictOn(JoinFilter.equal("account", ACCOUNT_ID, accountId));
        }

        return flightReassignmentRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<FlightReassignment> findAll(final Pageable pageable) {
        LOG.debug("Request to find all FlightReassignments");
        return flightReassignmentRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public FlightReassignment findOne(final Integer id) {
        LOG.debug("Request to find FlightReassignment by ID: {}", id);
        return flightReassignmentRepository.findOne(id);
    }

    public FlightReassignment update(final Integer id, final FlightReassignment flightReassignment,
            List<FlightReassignmentAerodrome> anAerodromes) {
        FlightReassignment updatedFlightReassignment = null;

        if (id == null || flightReassignment == null) {
            LOG.debug("Request to update flightReassignment failed because ID or flightReassignment is null");

            return updatedFlightReassignment;
        }

        if (flightReassignment.getAccount() != null && flightReassignment.getAccount().getId() != null) {
            Integer accId = flightReassignment.getAccount().getId();
            Account acc = accountRepository.getOne(accId);
            flightReassignment.setAccount(acc);
        }

        LOG.debug("Request to update FlightReassignment : {}", flightReassignment);

        FlightReassignment existingFlightReassignment = flightReassignmentRepository.getOne(id);
        ModelUtils.merge(flightReassignment, existingFlightReassignment, "aerodromeIdentifiers");
        updatedFlightReassignment = flightReassignmentRepository.save(existingFlightReassignment);

        final Map<String, FlightReassignmentAerodrome> mapCurrentAerodromes = existingFlightReassignment
                .getAerodromeIdentifiers().stream()
                .collect(Collectors.toMap(ro -> ro.getAerodromeIdentifier(), ro -> ro));

        final Collection<String> aerodromeIdentifiers = anAerodromes.stream()
                .map(FlightReassignmentAerodrome::getAerodromeIdentifier).collect(Collectors.toList());

        final Map<String, FlightReassignmentAerodrome> mapAerodromes = anAerodromes.stream()
                .collect(Collectors.toMap(ro -> ro.getAerodromeIdentifier(), ro -> ro));

        final List<FlightReassignmentAerodrome> aerodromeToAdd = aerodromeIdentifiers.stream()
                .filter(i -> !mapCurrentAerodromes.containsKey(i))
                .map(i -> setFlightReassignment(mapAerodromes.get(i), existingFlightReassignment))
                .collect(Collectors.toList());

        final List<FlightReassignmentAerodrome> aerodromeToDelete = mapCurrentAerodromes.entrySet()
                .stream().filter(e -> !aerodromeIdentifiers.contains(e.getKey())).map(Map.Entry::getValue)
                .collect(Collectors.toList());

        flightReassignmentAerodromeRepository.save(aerodromeToAdd);
        flightReassignmentAerodromeRepository.delete(aerodromeToDelete);
        flightReassignmentAerodromeRepository.flush();
        flightReassignmentRepository.refresh(updatedFlightReassignment);

        return updatedFlightReassignment;
    }

    private FlightReassignmentAerodrome setFlightReassignment(FlightReassignmentAerodrome aerodrome, FlightReassignment aFlightReassignment) {
        aerodrome.setFlightReassignment(aFlightReassignment);
        return aerodrome;
    }

    public long countAll() {
        return flightReassignmentRepository.count();
    }
}
