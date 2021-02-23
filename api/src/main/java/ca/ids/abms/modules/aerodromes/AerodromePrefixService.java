package ca.ids.abms.modules.aerodromes;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.countries.Country;

@Service
@Transactional
public class AerodromePrefixService {

    private final Logger log = LoggerFactory.getLogger(AerodromePrefixService.class);

    private AerodromePrefixRepository aerodromePrefixRepository;

    public AerodromePrefixService(AerodromePrefixRepository aAerodromePrefixRepository) {
        aerodromePrefixRepository = aAerodromePrefixRepository;
    }

    public AerodromePrefix create(AerodromePrefix aerodromePrefix) {
        log.debug("Request to create AircraftRegistrationPrefix : {}", aerodromePrefix);
        return aerodromePrefixRepository.save(aerodromePrefix);
    }

    /**
     * Check if prefix exists based on string representation before creating.
     * @param aerodromePrefix;
     */
    public void createFromStringArray(AerodromePrefix aerodromePrefix) {
        log.debug("Request to create AerodromePrefix from string array: {}", aerodromePrefix);
        if (findAerodromePrefixByPrefix(aerodromePrefix.getAerodromePrefix()) == null) {
            create(aerodromePrefix);
        } else {
            throw new CustomParametrizedException(
                String.format("Aerodrome prefix already exists: %s", aerodromePrefix.getAerodromePrefix())
            );
        }
    }

    public void delete(Integer id) {
        log.debug("Request to delete AerodromePrefix : {}", id);
        try {
            aerodromePrefixRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    /**
     * Delete prefixes that no longer exist in country object array.
     * @param countryPrefixes;
     * @param existingCountryPrefixes;
     */
    public void deleteFromStringArray(
        List<AerodromePrefix> countryPrefixes,
        List<AerodromePrefix> existingCountryPrefixes
    ) {
        existingCountryPrefixes.forEach(prefix -> {
            if (!countryPrefixes.contains(prefix)) {
                delete(prefix.getId());
            }
        });
    }

    @Transactional(readOnly = true)
    public List<AerodromePrefix> findAerodromePrefixByCountryCode(Country countryCode) {
        log.debug("Request aircraftRegistrationPrefixes by country code");
        return aerodromePrefixRepository.findByCountryCode(countryCode);
    }

    @Transactional(readOnly = true)
    public AerodromePrefix findAerodromePrefixByPrefix(String prefix) {
        log.debug("Request aerodromePrefixes by prefix");
        return aerodromePrefixRepository.findByAerodromePrefix(prefix);
    }
}
