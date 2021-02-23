package ca.ids.abms.modules.localaircraftregistry;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.converter.JSR310DateConverters;

@Service
@Transactional
public class LocalAircraftRegistryService {

    private final Logger log = LoggerFactory.getLogger(LocalAircraftRegistryService.class);

    private LocalAircraftRegistryRepository localAircraftRegistryRepository;

    private final static String COA_DATE_OF_RENEWAL = "coaDateOfRenewal";
    private final static String COA_DATE_OF_EXPIRY = "coaDateOfExpiry";

    public LocalAircraftRegistryService(LocalAircraftRegistryRepository aLocalAircraftRegistryRepository) {
        localAircraftRegistryRepository = aLocalAircraftRegistryRepository;
    }

    public void delete(Integer id) {
        log.debug("Request to delete local aircraft registry : {}", id);
        localAircraftRegistryRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public LocalAircraftRegistry getOne(Integer id) {
        log.debug("Request to get local aircraft registry : {}", id);
        return localAircraftRegistryRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public LocalAircraftRegistry findByRegistrationNumber(String registrationNumber) {
        log.debug("Request to find local aircraft registry by registration number  : {}", registrationNumber);
        return localAircraftRegistryRepository.findByRegistrationNumber(registrationNumber);
    }

    public LocalAircraftRegistry save(LocalAircraftRegistry localAircraftRegistry) {
        log.debug("Request to save local aircraft registry : {}", localAircraftRegistry);

        validateDate(localAircraftRegistry.getCoaDateOfRenewal(), localAircraftRegistry.getCoaDateOfExpiry());
        localAircraftRegistry.setRegistrationNumber(validateRegNum(localAircraftRegistry.getRegistrationNumber()));

        return localAircraftRegistryRepository.save(localAircraftRegistry);
    }

    public LocalAircraftRegistry update(Integer id, LocalAircraftRegistry localAircraftRegistry) {
        log.debug("Request to update local aircraft registry : {}", localAircraftRegistry);

        validateDate(localAircraftRegistry.getCoaDateOfRenewal(), localAircraftRegistry.getCoaDateOfExpiry());
        localAircraftRegistry.setRegistrationNumber(validateRegNum(localAircraftRegistry.getRegistrationNumber()));

        LocalAircraftRegistry existingLocalAircraftRegistry = localAircraftRegistryRepository.getOne(id);
        ModelUtils.merge(localAircraftRegistry, existingLocalAircraftRegistry);
        return localAircraftRegistryRepository.save(existingLocalAircraftRegistry);
    }

    public LocalAircraftRegistry createOrUpdate(final LocalAircraftRegistry item) {
        item.setRegistrationNumber(validateRegNum(item.getRegistrationNumber()));
        LocalAircraftRegistry existing = localAircraftRegistryRepository.findByRegistrationNumber(item.getRegistrationNumber());
        if (existing != null) {
            return update(existing.getId(), item);
        } else {
            return save(item);
        }
    }

    @Transactional(readOnly = true)
    public Page<LocalAircraftRegistry> findAllLocalAircraftRegistriesByFilter(String aFilter, String textSearch, Pageable aPageable) {

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        if (aFilter != null && !aFilter.equals("all")) {
            LocalDateTime today = LocalDateTime.now();
            if (aFilter.equals("active")) {
                filterBuilder.restrictOn(Filter.included(today, COA_DATE_OF_RENEWAL, COA_DATE_OF_EXPIRY));
            }
            if (aFilter.equals("expired")) {
                filterBuilder.restrictOn(Filter.notIncluded(today, COA_DATE_OF_RENEWAL, COA_DATE_OF_EXPIRY));
            }
        }
        log.debug("Attempting to find local aircraft registries by filter: {} and Search: {}",
            aFilter, textSearch);
        return localAircraftRegistryRepository.findAll(filterBuilder.build(), aPageable);
    }

    private boolean validateDate(LocalDateTime renewalDate, LocalDateTime expiryDate) {
        if (renewalDate.isAfter(expiryDate)) {
            log.debug("Bad request: renewal date is after expiry date");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_DATE);
            final String details =
                "Renewal date" + ": " +
                renewalDate.format(fmt) + "; " +
                "expiry date" + ": " +
                expiryDate.format(fmt);
            throw new CustomParametrizedException(ErrorConstants.ERR_RENEWAL_EXPIRY_DATE, new Exception(details));
        } else {
            return true;
        }
    }

    private String validateRegNum(String registrationNumber) {
        if (registrationNumber.contains("-") || registrationNumber.contains(" ")) {
            registrationNumber = registrationNumber.replace("-", "").replace(" ", "");
        }

        if (!registrationNumber.matches("[a-zA-Z\\d]{2,7}")) {
            log.debug("Bad request: registration number contains special characters or the length is incorrect  : {}", registrationNumber);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Registration number should be alphanumeric with the length between 2 and 7 characters" + ": " + registrationNumber));
        }
        return registrationNumber.toUpperCase();
    }

    public long countAllLocalAircraftRegistries() {
        return localAircraftRegistryRepository.count();
    }
}
