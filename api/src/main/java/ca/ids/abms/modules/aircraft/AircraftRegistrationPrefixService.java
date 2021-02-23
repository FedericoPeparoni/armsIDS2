package ca.ids.abms.modules.aircraft;

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
public class AircraftRegistrationPrefixService {

    private final Logger log = LoggerFactory.getLogger(AircraftRegistrationPrefixService.class);

    private AircraftRegistrationPrefixRepository aircraftRegistrationPrefixRepository;

    public AircraftRegistrationPrefix create(AircraftRegistrationPrefix aircraftRegistrationPrefix) {
        log.debug("Request to create AircraftRegistrationPrefix : {}", aircraftRegistrationPrefix);
        return aircraftRegistrationPrefixRepository.save(aircraftRegistrationPrefix);
    }

    /**
     * Check if prefix exists based on string representation before creating.
     * @param aircraftRegistrationPrefix;
     */
    public void createFromStringArray(AircraftRegistrationPrefix aircraftRegistrationPrefix) {
        log.debug("Request to create AircraftRegistrationPrefixes from string array: {}", aircraftRegistrationPrefix);
        if (findAircraftRegistrationPrefixByPrefix(aircraftRegistrationPrefix.getAircraftRegistrationPrefix()) == null) {
            create(aircraftRegistrationPrefix);
        } else {
            throw new CustomParametrizedException(
                String.format(
                   "Aircraft prefix already exists: %s", aircraftRegistrationPrefix.getAircraftRegistrationPrefix())
            );
        }
    }

    public void delete(Integer id) {
        log.debug("Request to delete AircraftRegistrationPrefix : {}", id);
        try {
            aircraftRegistrationPrefixRepository.delete(id);
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
        List<AircraftRegistrationPrefix> countryPrefixes,
        List<AircraftRegistrationPrefix> existingCountryPrefixes
    ) {
        existingCountryPrefixes.forEach(prefix -> {
            if (!countryPrefixes.contains(prefix)) {
                delete(prefix.getId());
            }
        });
    }

    public AircraftRegistrationPrefixService(AircraftRegistrationPrefixRepository aAircraftRegistrationPrefixRepository) {
        aircraftRegistrationPrefixRepository = aAircraftRegistrationPrefixRepository;
    }

    @Transactional(readOnly = true)
    public List<AircraftRegistrationPrefix> findAircraftRegistrationPrefixByCountryCode(Country countryCode) {
        log.debug("Request aircraftRegistrationPrefixes by country code");
        return aircraftRegistrationPrefixRepository.findByCountryCode(countryCode);
    }

    @Transactional(readOnly = true)
    private AircraftRegistrationPrefix findAircraftRegistrationPrefixByPrefix(String prefix) {
        log.debug("Request aircraftRegistrationPrefixes by prefix");
        return aircraftRegistrationPrefixRepository.findByAircraftRegistrationPrefix(prefix);
    }
}
