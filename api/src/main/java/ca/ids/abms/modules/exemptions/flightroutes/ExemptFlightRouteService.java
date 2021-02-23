package ca.ids.abms.modules.exemptions.flightroutes;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.exemptions.ExemptionType;
import ca.ids.abms.modules.exemptions.ExemptionTypeProvider;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

import com.google.common.base.Preconditions;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocation;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.util.models.geometry.CoordinatesConversionUtility;
import ca.ids.abms.modules.utilities.flights.FlightUtility;

import java.util.ArrayList;
import java.util.Collection;

@Service
@Transactional
public class ExemptFlightRouteService implements ExemptionTypeProvider {

    private ExemptFlightRouteRepository exemptFlightRouteRepository;
    private AerodromeService aerodromeService;
    private UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;
    private SystemConfigurationService systemConfigurationService;
    
    ExemptFlightRouteService(ExemptFlightRouteRepository exemptFlightRouteRepository,
                             AerodromeService aerodromeService,
                             UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService,
                             SystemConfigurationService systemConfigurationService) {
        this.exemptFlightRouteRepository = exemptFlightRouteRepository;
        this.aerodromeService=aerodromeService;
        this.unspecifiedDepartureDestinationLocationService=unspecifiedDepartureDestinationLocationService;
        this.systemConfigurationService = systemConfigurationService;
    }

    public ExemptFlightRoute create(ExemptFlightRoute exemptFlightRoute) {

        String depAd = resolveAerodrome(exemptFlightRoute.getDepartureAerodrome(), true);
        String destAd = resolveAerodrome(exemptFlightRoute.getDestinationAerodrome(), false);
        if(StringUtils.isBlank(depAd) || StringUtils.isBlank(destAd)){
            String aerodromeWrong = depAd == null ? ": "+ exemptFlightRoute.getDepartureAerodrome(): "";
            aerodromeWrong += destAd == null ?  ", "+ exemptFlightRoute.getDestinationAerodrome(): "";
            throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_AERODROME_VALIDATION +
                aerodromeWrong, AtcMovementLog.class, "departureAeroDrome", "destinationAeroDrome" );
        }

        return exemptFlightRouteRepository.save(exemptFlightRoute);
    }

    public ExemptFlightRoute update(Integer id, ExemptFlightRoute item) {

        String depAd = resolveAerodrome(item.getDepartureAerodrome(), true);
        String destAd = resolveAerodrome(item.getDestinationAerodrome(), false);
        if (StringUtils.isBlank(depAd) || StringUtils.isBlank(destAd)) {
            String aerodromeWrong = depAd == null ? ": " + item.getDepartureAerodrome() : "";
            aerodromeWrong += destAd == null ? ", " + item.getDestinationAerodrome() : "";
            throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_AERODROME_VALIDATION + aerodromeWrong,
                    AtcMovementLog.class, "departureAeroDrome", "destinationAeroDrome");
        }

        try {
            final ExemptFlightRoute existingItem = exemptFlightRouteRepository.getOne(id);
            ModelUtils.merge(item, existingItem, "id", "createdAt", "createdBy", "updateAt", "updatedBy");
            return exemptFlightRouteRepository.save(existingItem);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    @Transactional(readOnly = true)
    public ExemptFlightRoute getOne(Integer id) {
        return exemptFlightRouteRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public Page<ExemptFlightRoute> findAll(Pageable pageable, final String textSearch) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);
        return exemptFlightRouteRepository.findAll(filterBuilder.build(), pageable);
    }

    public void delete(Integer id) {
        try {
            exemptFlightRouteRepository.delete(id);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e,ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }
    }

    /**
     * Return applicable ExemptFlightRoute by provided flight movement.
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(FlightMovement flightMovement) {
        Preconditions.checkArgument(flightMovement != null);
        Double fl =null;
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
            fl = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL)); 
        }
        return findApplicableExemptions(flightMovement.getDepAd(), flightMovement.getDestAd(), FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),fl));
    }

    /**
     * Return applicable ExemptFlightRoute by provided departuer and destination aerodromes.
     */
    @Transactional(readOnly = true)
    public Collection<ExemptionType> findApplicableExemptions(final String depAd, final String destAd, final Double flightLevel) {

        Collection<ExemptionType> exemptions = new ArrayList<>();
        if (StringUtils.isNotBlank(depAd) && StringUtils.isNotBlank(destAd)) {
            if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) && flightLevel != null) {
                exemptions.addAll(exemptFlightRouteRepository.findAllByDepAndDestAndFlightLevel(depAd, destAd, flightLevel));
            } else {
                exemptions.addAll(exemptFlightRouteRepository.findAllByDepAndDest(depAd, destAd));
            }
        }
        return exemptions;
    }

    private String resolveAerodrome(String aerodromeIdentifier, final boolean checkAfil) {
        String aerodrome = null;
        if (StringUtils.isNotBlank(aerodromeIdentifier)) {
            aerodrome = aerodromeService.checkAerodromeIdentifier(aerodromeIdentifier, true, checkAfil);
            if(StringUtils.isBlank(aerodrome)) {
                UnspecifiedDepartureDestinationLocation unspecifiedLocation = unspecifiedDepartureDestinationLocationService.findTextIdentifier(aerodromeIdentifier);
                if (unspecifiedLocation != null) {
                    if (unspecifiedLocation.getAerodromeIdentifier() != null) {
                        // resolve unspecified by Aerodrome Identifier
                        aerodrome = unspecifiedLocation.getAerodromeIdentifier().getAerodromeName();
                    } else {
                        // resolve unspecified by Aerdrome Coordinates
                        aerodrome = CoordinatesConversionUtility.convertLatitudeLongitudeFromDecimalToDMS(unspecifiedLocation.getLatitude(), unspecifiedLocation.getLongitude());
                    }
                }
            }
        }
        return aerodrome;
    }

    public long countAll() {
        return exemptFlightRouteRepository.count();
    }
}
