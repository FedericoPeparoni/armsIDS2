package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@SuppressWarnings({"WeakerAccess", "unused"})
public class AircraftRegistrationService {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftRegistrationService.class);

    private final AccountRepository accountRepository;
    private final AircraftRegistrationRepository aircraftRegistrationRepository;
    private final AircraftRegistrationPrefixRepository aircraftRegistrationPrefixRepository;

    public AircraftRegistrationService(final AccountRepository accountRepository,
                                       final AircraftRegistrationRepository aircraftRegistrationRepository,
                                       final AircraftRegistrationPrefixRepository aircraftRegistrationPrefixRepository) {
        this.accountRepository = accountRepository;
        this.aircraftRegistrationRepository = aircraftRegistrationRepository;
        this.aircraftRegistrationPrefixRepository = aircraftRegistrationPrefixRepository;
    }

    @Transactional(readOnly = true)
    public Integer checkIfExistsAircraftRegistration(AircraftRegistration aircraftRegistration) {
        Integer returnValue = null;

        if (aircraftRegistration != null && aircraftRegistration.getRegistrationNumber() != null) {
            AircraftRegistration atcMovementLogByUniqueKey = findAircraftRegistrationByRegistrationNumber(
                    aircraftRegistration.getRegistrationNumber(), LocalDateTime.now());
            if (atcMovementLogByUniqueKey != null) {
                returnValue = atcMovementLogByUniqueKey.getId();
            }
        }
        return returnValue;
    }

    public AircraftRegistration createAircraftRegistration(AircraftRegistration aircraftRegistration) {
        return aircraftRegistrationRepository.save(aircraftRegistration);

    }

    public AircraftRegistration createOrUpdate(AircraftRegistration item) {
        final Integer aircraftRegistrationId = checkIfExistsAircraftRegistration(item);
        if (aircraftRegistrationId != null) {
            return update(aircraftRegistrationId, item);
        } else {
            return save(item);
        }
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete AircraftRegistration : {}", id);
        try {
            aircraftRegistrationRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }
    
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateAircraftRegistrationCOAByIdAndDates(Integer id, LocalDateTime coa_issue_date, LocalDateTime coa_expiry_date) {
        aircraftRegistrationRepository.updateAircraftRegistrationCOAByIdAndDates(id, coa_issue_date, coa_expiry_date);
    }

    @Transactional(readOnly = true)
    public AircraftRegistration findAircraftRegistrationByRegistrationNumber(String registrationNumber, LocalDateTime date) {
        LOG.debug("Request aircraftRegistrations by registration number {} for date {}", registrationNumber, date);
        AircraftRegistration ar = null;
        if (date != null) {
            List<AircraftRegistration> arList = aircraftRegistrationRepository
                    .findAircraftRegistrationByRegistrationNumberAndCheckDate(registrationNumber, date);
            if (arList != null && !arList.isEmpty()) {
                LOG.debug("There is {} for registration number {}", arList.size(), registrationNumber);
                ar = arList.get(0);
            } else {
                LOG.debug("No registration number was found");
            }
        } else {
            List<AircraftRegistration> arList = aircraftRegistrationRepository
                    .findAircraftRegistrationByRegistrationNumber(registrationNumber);
            if (arList != null && !arList.isEmpty()) {
                ar = arList.get(0);
            }
        }
        return ar;
    }

    @Transactional(readOnly = true)
    public List<AircraftRegistration> findAll() {
        LOG.debug("Request to get all AircraftRegistrations");

        return aircraftRegistrationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<AircraftRegistration> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to get AircraftRegistration");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchText);
        return aircraftRegistrationRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public List<AircraftRegistration> findAircraftRegistrationForSelfCareUser(final Integer userId, final String searchFilter) {
        if (StringUtils.isNotBlank(searchFilter)) {
            LOG.debug("Request to get AircraftRegistration for self-care user id: {} and text filter: {}", userId, searchFilter);
            return aircraftRegistrationRepository.findAircraftRegistrationForSelfCareUser(userId, searchFilter.toLowerCase());
        }
        LOG.debug("Request to get AircraftRegistration for self-care user id: {}", userId);
        return aircraftRegistrationRepository.findAircraftRegistrationForSelfCareUser(userId);
    }

    @Transactional(readOnly = true)
    public List<AircraftRegistration> findAllAircraftRegistrationForSelfCareAccounts(final String searchFilter) {
        if (StringUtils.isNotBlank(searchFilter)) {
            LOG.debug("Request to get AircraftRegistration for all self-care accounts by text filter: {}", searchFilter);
            return aircraftRegistrationRepository.findAllAircraftRegistrationForSelfCareAccounts(searchFilter.toLowerCase());
        }
        LOG.debug("Request to get AircraftRegistration for all self-care accounts");
        return aircraftRegistrationRepository.findAllAircraftRegistrationForSelfCareAccounts();
    }

    @Transactional(readOnly = true)
    public AircraftRegistration getOne(Integer id) {
        LOG.debug("Request to get AircraftRegistration : {}", id);
        return aircraftRegistrationRepository.getOne(id);
    }

    public AircraftRegistration save(AircraftRegistration aircraftRegistration) {
        LOG.debug("Request to save AircraftRegistration : {}", aircraftRegistration);
        validate(aircraftRegistration, null);
        return aircraftRegistrationRepository.save(aircraftRegistration);
    }

    @Transactional(readOnly = true)
    public boolean startsWithAircraftRegistrationPrefix(String aRegistrationNumber) {
        boolean ret = false;
        List<AircraftRegistrationPrefix> prefixes = aircraftRegistrationPrefixRepository.findAll();
        Optional<AircraftRegistrationPrefix> optional = prefixes.stream()
                .filter(x -> aRegistrationNumber.startsWith(x.getAircraftRegistrationPrefix())).findFirst();
        if (optional.isPresent()) {
            ret = true;
        }
        return ret;
    }

    public AircraftRegistration update(Integer id, AircraftRegistration aircraftRegistration) {
        LOG.debug("Request to update AircraftRegistration : {}", aircraftRegistration);
        validate(aircraftRegistration, id);
        try {
            AircraftRegistration existingAircraftRegistration = aircraftRegistrationRepository.getOne(id);
            ModelUtils.merge(aircraftRegistration, existingAircraftRegistration);
            return aircraftRegistrationRepository.save(existingAircraftRegistration);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    public void validate(AircraftRegistration aircraftRegistration, Integer id) {

        // validate registration number
        String atReNum = aircraftRegistration.getRegistrationNumber();
        if (atReNum.length() < 2) {
            LOG.debug("Bad request: registration number is too short : {}", atReNum);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                    new Exception("Registration number is too short: " + atReNum));
        }

        // validate country code
        Country countryCode = aircraftRegistration.getCountryOfRegistration();
        if (countryCode == null) {
            final ErrorDTO errorDto = new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_COUNTRY_CODE_MISSING)
                .addInvalidField(aircraftRegistration.getClass(), "countryOfRegistration", "may not be null")
                .build();

            throw ExceptionFactory.getInvalidDataException(errorDto);
        }

        // validate account id
        Account account = aircraftRegistration.getAccount() != null && aircraftRegistration.getAccount().getId() != null
            ? accountRepository.getOne(aircraftRegistration.getAccount().getId()) : null;
        if (account == null) {
            final ErrorDTO errorDTO = new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_NOT_FOUND)
                .addInvalidField(aircraftRegistration.getClass(), "account", "resource not found")
                .build();
            throw ExceptionFactory.getInvalidDataException(errorDTO);
        } else {
            aircraftRegistration.setAccount(account);
        }

        // validate overlapping dates
        LocalDateTime sd = aircraftRegistration.getRegistrationStartDate();
        LocalDateTime ed = aircraftRegistration.getRegistrationExpiryDate();
        if (ed.isAfter(sd)) {

            List<AircraftRegistration> overlaps = id == null
                ? aircraftRegistrationRepository.getOverlapsDates(sd, ed, atReNum)
                : aircraftRegistrationRepository.getOverlapsDates(sd, ed, atReNum, id);
            if (overlaps != null && !overlaps.isEmpty()) {
                LOG.debug("Bad request: overlapped dates for aircraft registration number : {}", atReNum);
                throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                    new Exception("Overlapped dates for aircraft registration number: " + atReNum));
            }
        } else {
            LOG.debug("Bad request: expiring date is overlapped starting date");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_DATE_TIME);
            final String details = "Start date: " + sd.format(fmt) + "; expiry date:" + ed.format(fmt);
            throw new CustomParametrizedException(ErrorConstants.ERR_START_END_DATE, new Exception(details));
        }
    }

    public long countAllAircraftRegistrations() {
        return aircraftRegistrationRepository.count();
    }

    public long countAllAircraftRegistrationsForSelfCareUser(Integer id) {
        return aircraftRegistrationRepository.countAllForSelfCareUser(id);
    }

    public long countAllForSelfCareAccounts() {
        return aircraftRegistrationRepository.countAllForSelfCareAccounts();
    }
}
