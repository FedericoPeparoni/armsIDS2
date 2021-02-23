package ca.ids.abms.modules.flight;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import ca.ids.abms.config.error.*;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;

@Service
@Transactional
@SuppressWarnings("WeakerAccess")
public class FlightScheduleService {

    private static final Logger LOG = LoggerFactory.getLogger(FlightScheduleService.class);

    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";
    private static final String ACCOUNT_ID = "id";
    private static final String ACCOUNT = "account";

    private static final List<DayOfWeek> ALL_DAYS_OF_WEEK = Arrays.asList(
        DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    private final FlightScheduleRepository flightScheduleRepository;
    private final AccountRepository accountRepository;
    private final FlightMovementBuilderUtility flightMovementBuilderUtility;
    private final AerodromeService aerodromeService;
    private final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private final FlightMovementService flightMovementService;

    public FlightScheduleService(
        final FlightScheduleRepository flightScheduleRepository,
        final AccountRepository accountRepository,
        final FlightMovementBuilderUtility flightMovementBuilderUtility,
        final AerodromeService aerodromeService,
        final UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
        final FlightMovementService flightMovementService
    ) {
        this.flightScheduleRepository = flightScheduleRepository;
        this.accountRepository = accountRepository;
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.aerodromeService = aerodromeService;
        this.unspecifiedDepartureDestinationLocationService = unspecifiedDepartureDestinationLocationService;
        this.flightMovementService = flightMovementService;
    }

    @Transactional(readOnly = true)
    public FlightSchedule checkToInactive(FlightSchedule item) {
        FlightSchedule fs = null;
        final Integer flightScheduleId = checkIfExistsFlightSchedule(item);
        if (flightScheduleId != null) {
            fs = flightScheduleRepository.getOne(flightScheduleId);
        }
        return fs;
    }

    public FlightSchedule create(FlightSchedule flightSchedule) {
        LOG.debug("Request to create FlightSchedule : {}", flightSchedule);
        validateExisting(flightSchedule);
        return this.create(flightSchedule, null);
    }

    public FlightSchedule create(FlightSchedule flightSchedule, ItemLoaderObserver o) {

        validate(flightSchedule);

        LOG.debug("Request to create FlightSchedule ");
        return flightScheduleRepository.save(flightSchedule);

    }

    public FlightSchedule createOrUpdate(final FlightSchedule item) {
        return this.createOrUpdate(item, null);
    }

    public FlightSchedule createOrUpdate(final FlightSchedule item, ItemLoaderObserver o) {
        final Integer flightScheduleId = checkIfExistsFlightSchedule(item);
        if (flightScheduleId != null) {
            return update(flightScheduleId, item, o);
        } else {
            return create(item, o);
        }
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete FlightSchedule : {}", id);
        try {
            flightScheduleRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public List<FlightSchedule> findAll(Integer accountId) {
        LOG.debug("Request to find all FlightSchedules by AccountId '{}'", accountId);
        Account account = accountRepository.getOne(accountId);
        return flightScheduleRepository.findByAccount(account);
    }

    @Transactional(readOnly = true)
    public List<FlightSchedule> findAll(Integer accountId, String textSearch) {
        LOG.debug("Request to find all FlightSchedules by AccountId '{}' and text search '{}'",
            accountId, textSearch);
        Account account = null;
        if (accountId != null) {
            account = accountRepository.getOne(accountId);
        }
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        if (account != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, ACCOUNT_ID, account.getId()));
        }
        return flightScheduleRepository.findAll(filterBuilder.build());

    }

    @Transactional(readOnly = true)
    public Page<FlightSchedule> findAll(Integer accountId, String textSearch, Pageable pageable) {
        LOG.debug("Request to find all FlightSchedules by AccountId '{}' and text search '{}' for pageable '{}'",
            accountId, textSearch, pageable);
        Account account = null;
        if (accountId != null) {
            account = accountRepository.getOne(accountId);
        }
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        if (account != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, ACCOUNT_ID, account.getId()));
        }
        return flightScheduleRepository.findAll(filterBuilder.build(), pageable);

    }

    @Transactional(readOnly = true)
    public Page<FlightSchedule> findAll(String textSearch, Pageable pageable) {
        LOG.debug("Request to find all FlightSchedules by text search '{}' and pageable '{}'", textSearch, pageable);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return flightScheduleRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public FlightSchedule findOne(Integer id) {
        LOG.debug("Request to find FlightSchedule by ID: {}", id);
        return flightScheduleRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Integer getAccountId(List<FlightScheduleCsvViewModel> flightScheduleLst) {
        Integer accountId = null;
        if (flightScheduleLst != null && !flightScheduleLst.isEmpty()) {
            FlightScheduleCsvViewModel flightScheduleCsvViewModel = flightScheduleLst.get(0);
            String flightServiceName = flightScheduleCsvViewModel.getFlightServiceNumber();
            Account acc = flightMovementBuilderUtility.getAccountByFlightId(flightServiceName);
            if (acc != null) {
                accountId = acc.getId();
            }
        }
        return accountId;
    }

    public FlightSchedule update(Integer id, FlightSchedule flightSchedule) {
        try {

            FlightSchedule updatedFlightSchedule;
            if (id == null || flightSchedule == null) {
                LOG.debug("Request to update flightSchedule failed because ID or flightSchedule is null");
                updatedFlightSchedule = null;
            } else {
                updatedFlightSchedule = this.update(id, flightSchedule, null);
            }
            return updatedFlightSchedule;

        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public FlightSchedule update(Integer id, FlightSchedule flightSchedule, ItemLoaderObserver o) {

        if (id == null || flightSchedule == null) {
            LOG.debug("Request to update flightSchedule failed because ID or flightSchedule is null");
            return null;
        }

        LOG.debug("Request to update FlightSchedule : {}", flightSchedule);

        validate(flightSchedule);

        FlightSchedule existingFlightSchedule = flightScheduleRepository.getOne(id);
        ModelUtils.merge(flightSchedule, existingFlightSchedule, "id");

        existingFlightSchedule.setEndDate(flightSchedule.getEndDate());

        return flightScheduleRepository.save(existingFlightSchedule);
    }

    @Transactional(readOnly = true)
    public Integer checkIfExistsFlightSchedule(FlightSchedule aFlightSchedule) {
        Integer returnValue = null;
        if (aFlightSchedule != null && aFlightSchedule.getFlightServiceNumber() != null) {
            String dailySchedule = aFlightSchedule.getDailySchedule();
            List<FlightSchedule> flightScheduleLst = flightScheduleRepository
                    .findByFlightServiceNumber(aFlightSchedule.getFlightServiceNumber());
            if (flightScheduleLst != null && !flightScheduleLst.isEmpty()) {
                FlightSchedule existingFlightSchedule = flightScheduleLst.get(0);
                String existingDailySchedule = existingFlightSchedule.getDailySchedule();
                Set<String> matchingDays = hasCommonDays(dailySchedule, existingDailySchedule);
                if (matchingDays != null && !matchingDays.isEmpty()) {
                    returnValue = existingFlightSchedule.getId();
                }
            }
        }
        return returnValue;
    }

    private Set<String> hasCommonDays(String s1, String s2) {
        Set<String> set = null;
        if (StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2)) {
            String[] s1Split = s1.split(",");
            String[] s2Split = s2.split(",");
            set = new LinkedHashSet<>(Arrays.asList(s1Split));
            set.retainAll(Arrays.asList(s2Split));
        }
        return set;
    }

    private void validateExisting(final FlightSchedule flightSchedule) {
        Integer flightId = checkIfExistsFlightSchedule(flightSchedule);
        if (flightId != null) {
            LOG.debug("Bad request: flight schedule {} has already been inserted with days {}",
                flightSchedule.getFlightServiceNumber(), flightSchedule.getDailySchedule());
            FlightSchedule existingFlightSchedule = flightScheduleRepository.getOne(flightId);
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                    new Exception("Flight schedule has already been inserted with another daily schedule" + ": "
                            + existingFlightSchedule.getDailySchedule()));
        }
    }

    private String resolveAerodrome(final String aerodromeIdentifier) {
        String aerodrome = null;
        if (StringUtils.isNotBlank(aerodromeIdentifier)) {
            aerodrome = aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier);
            if (StringUtils.isBlank(aerodrome)) {
                UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService
                        .findTextIdentifier(aerodromeIdentifier);
                if (unspecifiedLocation != null) {
                    if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                        // resolve unspecified by Aerodrome Identifier
                        aerodrome = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
                    } else {
                        // resolve unspecified by Aerodrome Coordinates
                        aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(
                                unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
                    }
                }
            }
        }
        return aerodrome;
    }

    private void validate(final FlightSchedule flightSchedule) {

        // validate departure and destination aerodrome values - the aerodrome should exist in ABMS db or NAVDB ('ZZZZ' or 'AFIL" is not accepted)
        String depAd = resolveAerodrome(flightSchedule.getDepAd());
        String destAd = resolveAerodrome(flightSchedule.getDestAd());
        if (StringUtils.isBlank(depAd) || StringUtils.isBlank(destAd) || depAd.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
                || destAd.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)) {
            String invalidDepAerodrome = getInvalidAerodrome(depAd, flightSchedule.getDepAd());
            String invalidDestAerodrome = getInvalidAerodrome(destAd, flightSchedule.getDestAd());

            String aerodromeDetails;
            String details;

            if (StringUtils.isNotBlank(invalidDepAerodrome) && StringUtils.isNotBlank(invalidDestAerodrome)) {
                aerodromeDetails = String.format("%s and %s", invalidDepAerodrome, invalidDestAerodrome);
                details = String.format("Aerodromes %s are not valid", aerodromeDetails);
            } else {
                aerodromeDetails = StringUtils.isNotBlank(invalidDepAerodrome) ? invalidDepAerodrome : invalidDestAerodrome;
                details = String.format("Aerodrome %s is not valid", aerodromeDetails);
            }

            LOG.debug("Bad request: {}", details);
            final ErrorDTO errorDTO = new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_AERODROME_VALIDATION)
                .setErrorMessage(": " + aerodromeDetails)
                .build();
            throw ExceptionFactory.getInvalidDataException(errorDTO);
        }

        // validate account id
        Account account = flightSchedule.getAccount() != null && flightSchedule.getAccount().getId() != null
            ? accountRepository.getOne(flightSchedule.getAccount().getId()) : null;
        if (account == null) {
            LOG.debug("Bad request: Account not found");
            final ErrorDTO errorDTO = new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_NOT_FOUND)
                .appendDetails(ACCOUNT)
                .addInvalidField(FlightSchedule.class, ACCOUNT, "resource not found")
                .build();
            throw ExceptionFactory.getInvalidDataException(errorDTO);
        } else {
            flightSchedule.setAccount(account);
        }
    }

    private String getInvalidAerodrome(final String resolvedAerodrome, final String initialAerodrome) {
        return StringUtils.isBlank(resolvedAerodrome) || resolvedAerodrome.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
            ? initialAerodrome : "";
    }

    /**
     * Add missing and unexpected flights to flight schedule view model.
     */
    public void addMissingAndUnexpectedFlights(final List<FlightScheduleViewModel> flightSchedules) {
        if (flightSchedules != null) {
            for (FlightScheduleViewModel flightSchedule : flightSchedules) {
                resolveMissingAndUnexpectedFlights(flightSchedule);
            }
        }
    }

    /**
     * Add missing and unexpected flights to flight schedule view model.
     */
    public void resolveMissingAndUnexpectedFlights(final FlightScheduleViewModel flightSchedule) {
        List<FlightScheduleMovementViewModel> missingFlights = new ArrayList<>();
        List<FlightScheduleMovementViewModel> unexpectedFlights = new ArrayList<>();

        try {

            // convert daily schedule into expected days of week
            // daily schedule is a comma separated list of week days from 1 to 7 starting on Sunday
            List<Integer> dailySchedule = new ArrayList<>();
            StringTokenizer st = new StringTokenizer(flightSchedule.getDailySchedule(), ",");
            while (st.hasMoreTokens()) {
                dailySchedule.add(Integer.parseInt(st.nextToken()));
            }

            // parse daily schedule as DaysOfWeek for expected and unexpected days
            List<DayOfWeek> expectedDays = daysOfWeek(dailySchedule);
            // noinspection unchecked
            List<DayOfWeek> unexpectedDays = ListUtils.subtract(ALL_DAYS_OF_WEEK, expectedDays);

            // flight schedule start date is required
            LocalDateTime currentDate = flightSchedule.getStartDate();

            // flight schedule end date is optional
            // search up to current date if null
            LocalDateTime endDate = flightSchedule.getEndDate() == null
                ? LocalDateTime.now() : flightSchedule.getEndDate();

            // loop through all days of week from start date to end date
            while (currentDate.isBefore(endDate)) {

                // find flight movement by logical key in logical order and filter out CANCELED, DELETED or DECLINED
                List<FlightMovement> flightMovements = flightMovementService
                    .findAllByLogicalKey(
                        flightSchedule.getFlightServiceNumber(), currentDate, flightSchedule.getDepTime(),
                        flightSchedule.getDepAd())
                    .stream().filter(
                        fm -> fm.getStatus() != FlightMovementStatus.CANCELED &&
                            fm.getStatus() != FlightMovementStatus.DECLINED &&
                            fm.getStatus() != FlightMovementStatus.DELETED)
                    .collect(Collectors.toList());

                // if day of week falls within expected days
                if (expectedDays.contains(currentDate.getDayOfWeek())) {

                    // add to missing list if no flight movements found
                    if (flightMovements.isEmpty()) {
                        missingFlights.add(new FlightScheduleMovementViewModel(flightSchedule, currentDate));
                    }

                    // add to unexpected list if more then one non-CANCELED flight movement found
                    else {
                        final List<FlightMovement> nonCanceledFlightMovements = flightMovements.stream()
                                .filter (x->x.getStatus() != FlightMovementStatus.CANCELED)
                                .collect (Collectors.toList());
                        if (nonCanceledFlightMovements.size() > 1) {
                            unexpectedFlights.addAll(nonCanceledFlightMovements.stream().skip(1)
                                .map(FlightScheduleMovementViewModel::new)
                                .collect(Collectors.toList()));
                        }
                    }
                }

                // add to unexpected if day of week falls within unexpected days and flight movements found
                if (unexpectedDays.contains(currentDate.getDayOfWeek()) && !flightMovements.isEmpty()) {
                    unexpectedFlights.addAll(flightMovements.stream()
                        .map(FlightScheduleMovementViewModel::new)
                        .collect(Collectors.toList()));
                }

                // bump current date by one
                currentDate = currentDate.plusDays(1);
            }
        } catch (Exception e) {
            LOG.error("Error retrieving missing flight movement ", e);
        }

        // set missing and unexpected flight movements in view model
        flightSchedule.setMissingFlightMovements(missingFlights);
        flightSchedule.setUnexpectedFlights(unexpectedFlights);
    }

    /**
     * Get DaysOfWeek value from integer list of week days starting on Sunday.
     */
    private List<DayOfWeek> daysOfWeek(final List<?> daysOfWeek) {
        List<DayOfWeek> result = new ArrayList<>();
        for (Object day : daysOfWeek) {

            // ensure day value of list is an integer as ListUtils is unchecked
            if (!(day instanceof Integer))
                continue;

            // shift days to left one as DayOfWeek values start on MONDAY
            result.add((Integer) day - 1 < 1
                ? DayOfWeek.SUNDAY
                : DayOfWeek.of((Integer) day - 1));
        }
        return result;
    }

    public long countAll() {
        return flightScheduleRepository.count();
    }

    public long countAllSelfCareFlightSchedules(Integer userId) {
        if (userId == null) {
            return flightScheduleRepository.countAllSelfCareFlightSchedules();
        }
        return flightScheduleRepository.countAllSelfCareFlightSchedules(userId);
    }
}
