package ca.ids.abms.modules.unspecified;

import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.util.stringmatcher.StringMatcherService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;

@Service
@Transactional
public class UnspecifiedDepartureDestinationLocationService {

    private static final Logger LOG = LoggerFactory.getLogger(UnspecifiedDepartureDestinationLocationService.class);

    private static final Integer MATCH_THRESHOLD = 91;

    private final AerodromeRepository aerodromeRepository;
    private final CountryService countryService;
    private final StringMatcherService stringMatcherService;
    private final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository;

    public UnspecifiedDepartureDestinationLocationService(
        final AerodromeRepository aerodromeRepository,
        final CountryService countryService,
        final StringMatcherService stringMatcherService,
        final UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository
    ) {
        this.aerodromeRepository = aerodromeRepository;
        this.countryService = countryService;
        this.stringMatcherService = stringMatcherService;
        this.unspecifiedDepartureDestinationLocationRepository = unspecifiedDepartureDestinationLocationRepository;
    }

    @Transactional
    public UnspecifiedDepartureDestinationLocation create(
        final UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation,
        final String aerodromeName
    ) {
        LOG.debug("Request to create UnspecifiedDepartureDestinationLocation by name '{}'", aerodromeName);

        // find and set aerodrome identifier by name
        Aerodrome aerodrome = aerodromeRepository.findByAerodromeName(aerodromeName);
        unspecifiedDepartureDestinationLocation.setAerodromeIdentifier(aerodrome);

        // set country code by aerodrome prefix if known, will set as default if unknown
        Country country = null;
        if(aerodrome != null && aerodrome.getAerodromeName() != null && aerodrome.getAerodromeName().length() > 2) {

            // find all countries with aerodrome prefix
            final List<Country> countries = countryService.findCountryByAerodromePrefix(aerodrome
                .getAerodromeName().substring(0, 2));

            // if countries found, set to first found
            if (CollectionUtils.isNotEmpty(countries))
                country = countries.get(0);
        }

        // if no country found by aerodrome, use default country
        if (country == null)
            country = countryService.findDefaultCountry();

        // set country if found, else throw invalid data exception as no default country
        if (country != null)
            unspecifiedDepartureDestinationLocation.setCountryCode(country);
        else
            throw ExceptionFactory.getInvalidDataException(new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_COUNTRY_CODE_INVALID).build());

        // validation and throw duplicate error on exception
        if (validate(unspecifiedDepartureDestinationLocation))
        	return unspecifiedDepartureDestinationLocationRepository.save(unspecifiedDepartureDestinationLocation);
        else
        	throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_UNSPECIFIED_LOCATION_ID_DUPLICATED,
                UnspecifiedDepartureDestinationLocationService.class);
    }

    @Transactional(readOnly = true)
    public boolean aerodromeExists(String aerodrome) {
        Example<UnspecifiedDepartureDestinationLocation> example = Example.of(new UnspecifiedDepartureDestinationLocation(aerodrome));
        return unspecifiedDepartureDestinationLocationRepository.exists(example);
    }

    @Transactional
    public void delete(Integer id) {
        LOG.debug("Request to delete UnspecifiedDepartureDestinationLocation : {}", id);
        try {
            unspecifiedDepartureDestinationLocationRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Page<UnspecifiedDepartureDestinationLocation> findAll(final String search, final Pageable pageable) {
        LOG.debug("Request to find all UnspecifiedDepartureDestinationLocation with text search '{}'", search);

        // build filter by searchable text
        FiltersSpecification.Builder builder = new FiltersSpecification.Builder(search);

        // find all based on filter specifications and pageable
        return unspecifiedDepartureDestinationLocationRepository.findAll(builder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public UnspecifiedDepartureDestinationLocation findOne(Integer id) {
        LOG.debug("Request to find UnspecifiedDepartureDestinationLocation by ID: {}", id);
        return unspecifiedDepartureDestinationLocationRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public UnspecifiedDepartureDestinationLocation findTextIdentifier(String textIdentifier) {
        LOG.debug("Request to find UnspecifiedDepartureDestinationLocation by TextIdentifier: {}", textIdentifier);

        UnspecifiedDepartureDestinationLocation uDDL = unspecifiedDepartureDestinationLocationRepository
            .findFirstByTextIdentifierOrderById(textIdentifier);

        if (uDDL != null) {
            return uDDL;
        } else {
            List<UnspecifiedDepartureDestinationLocation> uDDLs = unspecifiedDepartureDestinationLocationRepository.findAll();
            return stringMatcherService.getTopMatch(textIdentifier, uDDLs, MATCH_THRESHOLD, 1);
        }
    }

    @Transactional(readOnly = true)
    public UnspecifiedDepartureDestinationLocation findByTextIdentifierOrAerodromeIdentifier(String textIdentifier) {
        LOG.debug("Request to find UnspecifiedDepartureDestinationLocation by TextIdentifier OR AerodromeIdentifier : {}", textIdentifier);

        // find by text identifier and return if it exists
        UnspecifiedDepartureDestinationLocation uDDL = unspecifiedDepartureDestinationLocationRepository
            .findFirstByTextIdentifierOrderById(textIdentifier);
        if (uDDL != null) return uDDL;

        // else find by aerodrome identifier and return if it exists
        uDDL = unspecifiedDepartureDestinationLocationRepository
            .findFirstByAerodromeIdentifierAerodromeNameOrderById(textIdentifier);
        if (uDDL != null) return uDDL;

        // else find all and return the best match by matching threshold
        List<UnspecifiedDepartureDestinationLocation> uDDLs = unspecifiedDepartureDestinationLocationRepository
            .findAll();

        return stringMatcherService.getTopMatch(textIdentifier, uDDLs, MATCH_THRESHOLD, 2);
    }

    @Transactional
    public UnspecifiedDepartureDestinationLocation update(
        Integer id,
        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation,
        String aerodromeName
    ) {
        LOG.debug("Request to unspecifiedDepartureDestinationLocation : {}", unspecifiedDepartureDestinationLocation);

        try {
            UnspecifiedDepartureDestinationLocation existingUnspecifiedDepartureDestinationLocation = unspecifiedDepartureDestinationLocationRepository
                    .getOne(id);
            ModelUtils.merge(unspecifiedDepartureDestinationLocation, existingUnspecifiedDepartureDestinationLocation,
                    "id");
            if (aerodromeName != null) {
                final Aerodrome aerodrome = aerodromeRepository.findByAerodromeName(aerodromeName);
                existingUnspecifiedDepartureDestinationLocation.setAerodromeIdentifier(aerodrome);
            }

            // validation
            if (validate(existingUnspecifiedDepartureDestinationLocation)) {
                return unspecifiedDepartureDestinationLocationRepository
                        .save(existingUnspecifiedDepartureDestinationLocation);
            } else {
                throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_UNSPECIFIED_LOCATION_ID_DUPLICATED,
                        UnspecifiedDepartureDestinationLocationService.class);
            }
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public Boolean validate(UnspecifiedDepartureDestinationLocation uLocation){
    	Boolean result = Boolean.FALSE;

        // The values for textIdentifier and aerodromeIdentifier must be unique across both columns
    	if(uLocation != null && StringUtils.isStringIfNotNull(uLocation.getTextIdentifier())) {

    		if (unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(uLocation.getTextIdentifier()) != null)
    			return false;

    		// check textIdentifier column for existing aerodromeIdentifier value
    		if (uLocation.getAerodromeIdentifier() != null &&
                StringUtils.isStringIfNotNull(uLocation.getAerodromeIdentifier().getAerodromeName()) &&
                unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(uLocation.getAerodromeIdentifier().getAerodromeName()) != null)
                return false;

    		result = Boolean.TRUE;
    	}

    	return result;
    }

    public long countAllUnspecifiedLocations() {
        return unspecifiedDepartureDestinationLocationRepository.count();
    }
}
