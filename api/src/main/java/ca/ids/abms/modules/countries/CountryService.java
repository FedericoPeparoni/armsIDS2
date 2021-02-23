package ca.ids.abms.modules.countries;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.AerodromePrefixService;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefixService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CountryService {

    private static final Logger LOG = LoggerFactory.getLogger(CountryService.class);

    private final AerodromePrefixService aerodromePrefixService;
    private final AircraftRegistrationPrefixService aircraftRegistrationPrefixService;
    private final CountryRepository countryRepository;
    private final SystemConfigurationService systemConfigurationService;

    public CountryService(
        final AerodromePrefixService aerodromePrefixService,
        final AircraftRegistrationPrefixService aircraftRegistrationPrefixService,
        final CountryRepository countryRepository,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.aerodromePrefixService = aerodromePrefixService;
        this.aircraftRegistrationPrefixService = aircraftRegistrationPrefixService;
        this.countryRepository = countryRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    public Country save(Country country) {
        LOG.debug("Request to save Country : {}", country);
        return countryRepository.save(country);
    }

    /**
     * Creates country and associated aircraft registration and aerodrome prefixes.
     * Calls countryRepository save method before creating prefixes to ensure associated record exists.
     *
     * @param country;
     * @return country;
     */
    public Country create(Country country) {
        Country createdCountry;

        try {
            LOG.debug("Request to create Country : {}", country);

            createdCountry = countryRepository.save(country);

            handleAssociatedAircraftRegistrationPrefixes(createdCountry);
            handleAssociatedAerodromePrefixes(createdCountry);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return createdCountry;
    }

    /**
     * Updates country and associated aircraft registration and aerodrome prefixes.
     * Calls countryRepository save method.
     *
     * @param country;
     * @return country;
     */
    public Country update(Integer id, Country country) {
        Country updatedCountry;

        try {
            LOG.debug("Request to update Country : {}", country);
            Country existingCountry = countryRepository.getOne(id);

            handleAssociatedAircraftRegistrationPrefixes(country, existingCountry);
            handleAssociatedAerodromePrefixes(country, existingCountry);

            ModelUtils.checkVersionIfComparables(country, existingCountry);
            ModelUtils.merge(country, existingCountry);

            updatedCountry = countryRepository.save(existingCountry);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
        return updatedCountry;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Country: {}", id);
        try {
            countryRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<Country> findAll(Pageable pageable) {
        LOG.debug("Request to get countries");
        return countryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<Country> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to get countries including prefixes that contain the text: {}", searchText);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(searchText);
        return countryRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<Country> findAllIncludingPrefixes(Pageable pageable) {
        LOG.debug("Request to get countries including prefixes");
        return countryRepository.findAllIncludingPrefixes(pageable);
    }

    @Transactional(readOnly = true)
    public Country findCountryByPrefix(String aPrefix) {
        if (aPrefix == null) {
            return null;
        }
        LOG.debug("Request country by aircraft registration prefix");
        Country c =  countryRepository.findCountryByPrefix(aPrefix);
        if (c != null) {
            LOG.debug("There is country {} by prefix {} ", c.getCountryName(), aPrefix);
        }
        return c;
    }

    @Transactional(readOnly = true)
    public List<Country> findCountryByAerodromePrefix(String aerodromePrefix) {
        LOG.debug("Request countries by aerodrome prefix {}", aerodromePrefix);
        return (aerodromePrefix != null ? countryRepository.findCountryByAerodromePrefix(aerodromePrefix) : null);
    }

    @Transactional(readOnly = true)
    public Country findCountryByCountryCode(String code) {
        LOG.debug("Request country by country code {}", code);
    	return (code !=null ? countryRepository.findCountryByCountryCode(code) : null);
    }

    /**
     * Find country by default country via system configuration setting.
     *
     * @return country, returns `null` if non found.
     */
    @Transactional(readOnly = true)
    public Country findDefaultCountry() {
        Country country = null;

        // attempt to find country by default country value in system configuration
        String defaultCountryCode = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE);
        if (defaultCountryCode != null && !defaultCountryCode.isEmpty()) {

            // find by country code first
            country = findCountryByCountryCode(defaultCountryCode);

            // find by country name if no country found by code
            if (country == null)
                country = findCountryByCountryName(defaultCountryCode);
        }

        return country;
    }

    /**
     * Creates aircraft registration prefixes for new country record.
     * Prefixes are passed as an array. Each item in the array is aircraft registration prefix.
     * Prefix creation is handled by the aircraftRegistrationPrefixService.
     *
     * @param country;
     */
    private void handleAssociatedAircraftRegistrationPrefixes(Country country) {
        // create new prefixes from array found on country object
        country.getAircraftRegistrationPrefixes().forEach(prefix -> {
            // update prefix with newly created country
            prefix.setCountryCode(country);
            aircraftRegistrationPrefixService.createFromStringArray(prefix);
        });
    }

    /**
     * Creates aerodrome prefixes for new country record.
     * Prefixes are passed as an array. Each item in the array is aerodrome prefix.
     * Prefix creation is handled by the aerodromePrefixService.
     *
     * @param country;
     */
    private void handleAssociatedAerodromePrefixes(Country country) {
        // create new prefixes from array found on country object
        country.getAerodromePrefixes().forEach(prefix -> {
            // update prefix with newly created country
            prefix.setCountryCode(country);
            aerodromePrefixService.createFromStringArray(prefix);
        });
    }

    /**
     * Creates aircraft registration prefixes for existing country record.
     * Prefixes are passed as an array. Each item in the array is aircraft registration prefix.
     * Prefix creation is handled by the aircraftRegistrationPrefixService.
     * Prefixes that are found in the existing country record but missing from the updated country record are removed.
     *
     * @param country;
     * @param existingCountry;
     */
    private void handleAssociatedAircraftRegistrationPrefixes(Country country, Country existingCountry) {

        // delete prefixes that no longer exist in array found on country object
        aircraftRegistrationPrefixService.deleteFromStringArray(
            country.getAircraftRegistrationPrefixes(),
            existingCountry.getAircraftRegistrationPrefixes()
        );

        // create new prefixes from array found on country object
        country.getAircraftRegistrationPrefixes().forEach(aircraftRegistrationPrefixService::createFromStringArray);
    }

    /**
     * Creates aerodrome prefixes for existing country record.
     * Prefixes are passed as an array. Each item in the array is aerodrome prefix.
     * Prefix creation is handled by the aerodromePrefixService.
     * Prefixes that are found in the existing country record but missing from the updated country record are removed.
     *
     * @param country;
     * @param existingCountry;
     */
    private void handleAssociatedAerodromePrefixes(Country country, Country existingCountry) {

        // delete prefixes that no longer exist in array found on country object
        aerodromePrefixService.deleteFromStringArray(
            country.getAerodromePrefixes(),
            existingCountry.getAerodromePrefixes()
        );

        // create new prefixes from array found on country object
        country.getAerodromePrefixes().forEach(aerodromePrefixService::createFromStringArray);
    }

    private Country findCountryByCountryName(String countryName) {
        LOG.debug("Request country by country name {}", countryName);
        return (countryName !=null ? countryRepository.findCountryByCountryName(countryName) : null);
    }

    public long countAll() {
        return countryRepository.count();
    }
}
