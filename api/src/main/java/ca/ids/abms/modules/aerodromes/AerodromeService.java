package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutage;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageRepository;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserRepository;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import ca.ids.abms.modules.util.models.geometry.CoordinatesVO;
import ca.ids.abms.util.StringUtils;
import com.vividsolutions.jts.geom.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class AerodromeService {

    private final AerodromeRepository aerodromeRepository;
    private final UserRepository userRepository;
    private final AerodromeServiceOutageRepository aerodromeServiceOutageRepository;
    private final NavDBUtils navDBUtils;
    private final CountryService countryService;
    private final SystemConfigurationService systemConfigurationService;

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeService.class);

    private static final String CLASS_0 = "Class 0";

    public AerodromeService(final AerodromeRepository aerodromeRepository,
                            final UserRepository userRepository,
                            final NavDBUtils navDBUtils,
                            final AerodromeServiceOutageRepository aerodromeServiceOutageRepository,
                            final CountryService countryService,
                            final SystemConfigurationService systemConfigurationService) {
        this.aerodromeRepository = aerodromeRepository;
        this.userRepository = userRepository;
        this.navDBUtils = navDBUtils;
        this.aerodromeServiceOutageRepository = aerodromeServiceOutageRepository;
        this.countryService = countryService;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Will check if {@code aerodromeIdentifier} is {@code 'ZZZZ' or 'AFIL" (only for departure aerodromes)} or exists in either NAVDB or ABMSDB.
     *
     * Note, this method will return coordinates from ABMSDB Aerodromes if it does NOT exist in NAVDB. If you require
     * that the aerodrome name is returned instead, see {@link #checkAerodromeIdentifier(String, boolean, boolean)}.
     *
     * @param aerodromeIdentifier aerodrome name
     * @return resolved aerodrome identifier or coordinate
     */
    @Transactional(readOnly = true)
    public String checkAerodromeIdentifier(final String aerodromeIdentifier) {
        return this.checkAerodromeIdentifier(aerodromeIdentifier, true, false);
    }

    /**
     * Will check if {@code aerodromeIdentifier} is {@code 'ZZZZ' or 'AFIL" (only for departure aerodromes)} or exists in either NAVDB or ABMSDB.
     *
     * @param aerodromeIdentifier aerodrome name
     * @param resolveToCoordinates resolve to coordinates if not in NAVDB
     * @return resolved aerodrome identifier or coordinate
     */
    @Transactional(readOnly = true)
    public String checkAerodromeIdentifier(final String aerodromeIdentifier, final boolean resolveToCoordinates, final boolean checkAfil) {
        LOG.debug("Check Aerodrome identifier : {}", aerodromeIdentifier);
        String aerodrome = null;

        // Check if aerodromeIdentifier is Null
        if (StringUtils.isBlank(aerodromeIdentifier)) {
            LOG.error("Error Aerodrome identifier is NULL !!!");
            return null;
        }

        // Check if aerodromeIdentifier is equal ZZZZ or AFIL
        if (aerodromeIdentifier.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ)
            || (checkAfil && aerodromeIdentifier.equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL))) {
            aerodrome = aerodromeIdentifier;
        } else {
            // Check if aerodrome designator is in NAVDB
            if (!navDBUtils.checkIdentFromAirportNAVDB(aerodromeIdentifier)) {
                // Check if aerodrome designator is in Billing DB
                CoordinatesVO coordinatesVO = getCoordinatesFromAeroDromeABMSDB(aerodromeIdentifier);
                if (coordinatesVO != null && resolveToCoordinates) {
                    aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(coordinatesVO.getLatitude(), coordinatesVO.getLongitude());
                } else if (coordinatesVO != null) {
                    aerodrome = aerodromeIdentifier;
                } else {
                    LOG.warn("The aerodrome identifier '{}' cannot be found with coordinates in Billing DB", aerodromeIdentifier);
                }
            } else {
                aerodrome = aerodromeIdentifier;
            }
        }

        LOG.debug("Aerodrome is : {}", aerodrome);
        return aerodrome;
    }

    public void delete(Integer id) {
        LOG.debug("Request to delete Aerodrome : {}", id);
        validateDelete(id);
        try {
            aerodromeRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    private void validateDelete(Integer id) {
        LOG.debug("Request to validate Aerodrome for deletion : {}", id);
        Aerodrome aerodromeWithOutages = aerodromeRepository.findAerodromeServiceOutagesByAerodromeId(id);
        if (aerodromeWithOutages != null) {
            LOG.debug("Bad request: The Aerodrome is used in Aerodrome Service Outages: {}", aerodromeWithOutages);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                new Exception("The Aerodrome cannot be deleted, it is used in Aerodrome Service Outages"));
        }
    }

    @Transactional(readOnly = true)
    public Aerodrome findAeroDromeByAeroDromeName(String aerodromeName) {
        LOG.debug("Request to find Aerodrome : {}", aerodromeName);
        return aerodromeRepository.findByAerodromeName(aerodromeName);
    }

    @Transactional(readOnly = true)
    public List<Aerodrome> findAerodromesForCurrentBillingCenter() {
        LOG.debug("Request to get all Aerodromes");
        final User currentUser = userRepository.getOneByLogin (SecurityUtils.getCurrentUserLogin());
        return aerodromeRepository.findForUserBC(currentUser);
    }

    @Transactional(readOnly = true)
    public List<Aerodrome> findAll() {
        LOG.debug("Request to get all Aerodromes");
        return aerodromeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Aerodrome> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to get aerodromes");
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchText);
        return aerodromeRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public Aerodrome getOne(Integer id) {
        LOG.debug("Request to get Aerodrome : {}", id);
        return aerodromeRepository.getOne(id);
    }

    /**
     * Checks if AD is associated with a FIR based on prefix
     *
     * @param aerodromeName;
     * @param countryName;
     * @return boolean;
     */
    @Transactional(readOnly = true)
    public boolean isAdPrefixAssociatedWithCountry (final String aerodromeName, final String countryName) {
        List<String> prefixes = navDBUtils.getAerodromePrefixes(countryName);

        return prefixes.stream().anyMatch(aerodromeName::startsWith);
    }

    @Transactional(readOnly = true)
    public boolean isIcaoAerodrome (final String aerodromeIdentifier) {
        return navDBUtils.checkIdentFromAirportNAVDB(aerodromeIdentifier);
    }

    @Transactional(readOnly = true)
    public boolean isMannedAerodrome(String aAerodromeIdentifier) {
        boolean ret = false;
        Aerodrome aerodrome = findAeroDromeByAeroDromeName(aAerodromeIdentifier);
        if (aerodrome != null) {
            AerodromeCategory ac= aerodrome.getAerodromeCategory();
            String categoryName = ac.getCategoryName();
            if (!categoryName.equals(CLASS_0)) {
                ret = true;
            }
        }
        return ret;
    }

    public Aerodrome save(Aerodrome aerodrome) {
        LOG.debug("Request to save Aerodrome : {}", aerodrome);
        checkIfDomesticAerodrome(aerodrome.getAerodromeName());
        boolean checkAixm = navDBUtils.checkIdentFromAirportNAVDB(aerodrome.getAerodromeName());
        LOG.debug("Check aixm on navdb : {}", checkAixm);
        aerodrome.setAixmFlag(checkAixm);
        return aerodromeRepository.save(aerodrome);
    }

    public Aerodrome update(final Integer id, final Aerodrome aerodrome) {
        LOG.debug("Request to update Aerodrome : {}", aerodrome);
        checkIfDomesticAerodrome(aerodrome.getAerodromeName());
        try {
            Aerodrome existingAerodrome = aerodromeRepository.getOne(id);
            validateUpdate(id, aerodrome, existingAerodrome);

            ModelUtils.checkVersionIfComparables(aerodrome, existingAerodrome);
            ModelUtils.merge(aerodrome, existingAerodrome, "aerodromeServices");

            // update existing aerodrome service types
            Set<AerodromeServiceType> services = aerodrome.getAerodromeServices();
            Set<AerodromeServiceType> existingServices = existingAerodrome.getAerodromeServices();

            // add new and remove old services
            existingServices.addAll(services);
            existingServices.removeIf(i -> !services.contains(i));

            return aerodromeRepository.save(existingAerodrome);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    private void validateUpdate(final Integer id, final Aerodrome aerodrome, final Aerodrome existingAerodrome) {
        LOG.debug("Request to validate Aerodrome for modification : {}", id);

        final List<Integer> aerodromeServices = aerodrome.getAerodromeServices()
            .stream().map(AerodromeServiceType::getId).collect(Collectors.toList());

        final List<Integer> existingAerodromeServices = existingAerodrome.getAerodromeServices()
            .stream().map(AerodromeServiceType::getId).collect(Collectors.toList());

        final List<Integer> servicesToDelete = existingAerodromeServices.stream()
            .filter(e -> !aerodromeServices.contains(e)).collect(Collectors.toList());

        final List<AerodromeServiceOutage> aerodromeServiceOutage = aerodromeServiceOutageRepository.findAllByAerodromeId(id);

        final List<AerodromeServiceOutage> serviceUsed = aerodromeServiceOutage.stream().filter(e -> servicesToDelete
            .contains(e.getAerodromeServiceTypeMap().getId().getAerodromeServiceType().getId())).collect(Collectors.toList());

        if (!serviceUsed.isEmpty()) {
            final List<String> names = serviceUsed.stream().map(e -> e.getAerodromeServiceTypeMap().getId()
                .getAerodromeServiceType().getServiceName()).distinct().collect(Collectors.toList());

            LOG.debug("Bad request: The Aerodrome Service(s): {} is used in Aerodrome Service Outages", names);
            throw new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION,
                new Exception(String.format("The Aerodrome Service: %s cannot be modified, " +
                    "it is used in Aerodrome Service Outages", names.toString())));
        }
    }

    /**
     * Aerodrome is determined domestic by the system configuration setting "Country Code" prefix.
     *
     * This is only performs aerodrome prefix validation. Actual validation of a flight movement aerodrome
     * using prefix and FIR is done within {@link ca.ids.abms.modules.flightmovements.FlightMovementAerodromeService#isAerodromeDomestic(String)}.
     */
    @Transactional(readOnly = true)
    public Boolean isDomesticAerodrome(final String aerodrome) {

        String defaultCountryCode = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE);
        Country domesticCountry = defaultCountryCode != null ? countryService.findCountryByCountryCode(defaultCountryCode) : null;
        List<AerodromePrefix> aerodromePrefixes = domesticCountry != null ? domesticCountry.getAerodromePrefixes() : null;

        boolean anyMatch = false;
        if (aerodromePrefixes != null && !aerodromePrefixes.isEmpty()) {
            anyMatch = aerodromePrefixes.stream().anyMatch(aerodromePrefix -> aerodrome.startsWith(aerodromePrefix.getAerodromePrefix()));
        }
        return anyMatch;
    }

    @Transactional(readOnly = true)
    public Boolean validateAeroDromeIdentifier(final String aerodromeIdentifier) {

        Boolean returnValue = Boolean.FALSE;
        String aeroDrome = checkAerodromeIdentifier(aerodromeIdentifier);
        if (StringUtils.isNotBlank(aeroDrome)) {
            returnValue = Boolean.TRUE;
        }
        return returnValue;
    }

    /**
     * @param aerodromeIdentifier String
     * @return CoordinatesVO
     */
    private CoordinatesVO getCoordinatesFromAeroDromeABMSDB(final String aerodromeIdentifier) {
        LOG.debug("Get Coordinates from AeroDrome ABMSDB");
        CoordinatesVO coordinate = null;

        if (StringUtils.isNotBlank(aerodromeIdentifier)) {
            Aerodrome aerodrome = findAeroDromeByAeroDromeName(aerodromeIdentifier);
            if (aerodrome != null) {
                Point geometry = (Point) aerodrome.getGeometry();
                coordinate = new CoordinatesVO(geometry.getY(),geometry.getX());
            }
        } else {
            LOG.debug("Aerodrome Identifier is NULL !!!");
        }

        LOG.debug("End get Coordinates from AeroDrome ABMSDB");
        return coordinate;
    }

    long countAllAerodromes() {
        return aerodromeRepository.count();
    }

    List<Aerodrome> getAllAerodromesWithServices() {
        return aerodromeRepository.getAllAerodromesWithServices();
    }

    private void checkIfDomesticAerodrome (final String aerodrome) {
        if (!isDomesticAerodrome(aerodrome)) {
            LOG.debug("Bad request: Aerodrome {} is not domestic", aerodrome);
            final String details = "The record cannot be created/updated because the aerodrome is not domestic";
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(details));
        }
    }
}
