package ca.ids.abms.modules.route;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.modules.utilities.flights.FlightUtility;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional
public class NominalRouteService {

    private static final Logger LOG = LoggerFactory.getLogger(NominalRouteService.class);

    private final BiDirectionalNominalRouteRepository biDirectionalNominalRouteRepository;
    private final NominalRouteRepository nominalRouteRepository;
    private final ThruFlightPlanUtility thruFlightPlanUtility;
    private final SystemConfigurationService systemConfigurationService;
    
    NominalRouteService(
        final BiDirectionalNominalRouteRepository biDirectionalNominalRouteRepository,
        final NominalRouteRepository nominalRouteRepository,
        final ThruFlightPlanUtility thruFlightPlanUtility,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.biDirectionalNominalRouteRepository = biDirectionalNominalRouteRepository;
        this.nominalRouteRepository = nominalRouteRepository;
        this.thruFlightPlanUtility = thruFlightPlanUtility;
        this.systemConfigurationService = systemConfigurationService;
    }

    public NominalRoute create(NominalRoute item, boolean isManual) {
        if (isManual) {
            item.setStatus(NominalRouteStatus.MANUAL.getValue());
        } else {
            item.setStatus(NominalRouteStatus.CALCULATED.getValue());
        }
        try {
            return nominalRouteRepository.save(item);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.DEF_ERR_ALREADY_EXISTS);
        }
    }

    public void delete(Integer id) {
        nominalRouteRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Page<NominalRoute> findAll(Pageable pageable, String searchText) {
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchText);
        return nominalRouteRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public NominalRoute getOne(Integer id) {
        return nominalRouteRepository.getOne(id);
    }

    public NominalRoute update(Integer id, NominalRoute item, boolean isManual) {
        final NominalRoute existingItem = nominalRouteRepository.getOne(id);
        ModelUtils.merge(item, existingItem, "status");
        if (isManual) {
            existingItem.setStatus(NominalRouteStatus.MANUAL.getValue());
        } else {
            existingItem.setStatus(NominalRouteStatus.CALCULATED.getValue());
        }
        return nominalRouteRepository.save(existingItem);
    }
    
    public BiDirectionalNominalRoute findByAerodromes(final String departureAerodrome, final String arrivalAerodrome, final Double flightLevel) {
        
        // If flight level for the airspace is required and not null - find nominal routes which contain given flight level between floor and ceiling
        // take the first one in case of overlapped
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) && flightLevel != null) {
            return biDirectionalNominalRouteRepository.findByPointsAndTypeAndFlightLevel(departureAerodrome, arrivalAerodrome,
                    NominalRouteType.AERODROME_TO_AERODROME.getValue(),flightLevel);
        }    
        
        // for other cases return the first nominal route by end points
        return biDirectionalNominalRouteRepository.findByPointsAndType(departureAerodrome, arrivalAerodrome,
                    NominalRouteType.AERODROME_TO_AERODROME.getValue());
    }

    /**
     * Finds the most appropriate nominal route to use based on the following precedence:
     * <p>
     * 1. FIR_TO_AERODROME
     * 2. FIR_TO_FIR
     * 3. AERODROME_TO_AERODROME
     *
     * @param flightMovement to check for nominal route
     * @return nominalRoute with highest precedence;
     */
    public BiDirectionalNominalRoute findNominalRouteBasedOnPrecedence(FlightMovement flightMovement) {

        Boolean validateFlightLevel = systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE);
        Double defaultLevel = Double.valueOf(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.DEFAULT_FLIGHT_LEVEL));
        
        String departureAerodrome = StringUtils.stripToNull(flightMovement.getDepAd());
        if (ApplicationConstants.PLACEHOLDER_ZZZZ.equalsIgnoreCase(departureAerodrome) || ApplicationConstants.PLACEHOLDER_AFIL.equalsIgnoreCase(departureAerodrome)) {
            departureAerodrome = Item18Parser.getFirstAerodromeOrDMS(flightMovement.getItem18Dep());
        }

        String destinationAerodrome = StringUtils.stripToNull(flightMovement.getDestAd());
        if (ApplicationConstants.PLACEHOLDER_ZZZZ.equalsIgnoreCase(destinationAerodrome)) {
            destinationAerodrome = Item18Parser.getFirstAerodromeOrDMS(flightMovement.getItem18Dest());
        }

        BiDirectionalNominalRoute nominalRoute = null;
        if (departureAerodrome != null && destinationAerodrome != null) {
            if (thruFlightPlanUtility.isThruFlight(flightMovement)) {
                LOG.debug("The flight {} {} {} {} is a THRU PLAN", flightMovement.getId(), flightMovement.getDateOfFlight(),
                    flightMovement.getDepTime(), flightMovement.getDepAd());
                final List<RouteSegment> segments = flightMovement.getRouteSegments();
                if (segments != null) {
                    nominalRoute = new BiDirectionalNominalRoute();
                    nominalRoute.setPointa(departureAerodrome);
                    nominalRoute.setPointb(destinationAerodrome);

                    // use only schedule segments to find nominal by start and end labels
                    List<RouteSegment> scheduledSegments = segments.stream()
                        .filter(s -> Objects.equals(s.getSegmentType(), SegmentType.SCHED))
                        .collect(Collectors.toList());

                    Double distance = 0d;
                    
                    for (final RouteSegment segment : scheduledSegments) {
                        final BiDirectionalNominalRoute nominalSegmentRoute;
                        if(validateFlightLevel) {
                            nominalSegmentRoute = findByAerodromes(
                                    segment.getSegmentStartLabel(), segment.getSegmentEndLabel(), segment.getFlightLevel());
                        } else {
                            nominalSegmentRoute = findByAerodromes(
                                    segment.getSegmentStartLabel(), segment.getSegmentEndLabel(), null);
                        }
                        if (nominalSegmentRoute != null) {
                            distance += nominalSegmentRoute.getNominalDistance();
                        } else {
                            distance = null;
                            break;
                        }
                    }

                    nominalRoute.setNominalDistance(distance);
                    nominalRoute.setStatus(NominalRouteStatus.CALCULATED.getValue());
                    nominalRoute.setType(NominalRouteType.AERODROME_TO_AERODROME.getValue());
                    nominalRoute.isInverse(false);
                }
            }
            if (nominalRoute == null && flightMovement.getBillableEntryPoint() != null
                    && flightMovement.getBillableExitPoint() != null) {
                // FIR TO AERODROME takes top precedence -- return immediately
                // if found
                if(validateFlightLevel) {
                    nominalRoute = findByFirToAerodromeOrInverse(flightMovement.getBillableEntryPoint(),
                        flightMovement.getBillableExitPoint(), destinationAerodrome, departureAerodrome,FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),defaultLevel));
                } else {
                    nominalRoute = findByFirToAerodromeOrInverse(flightMovement.getBillableEntryPoint(),
                            flightMovement.getBillableExitPoint(), destinationAerodrome, departureAerodrome,null);                    
                }
                if (nominalRoute == null) {
                    // FIR TO FIR
                    if(validateFlightLevel) {
                    nominalRoute = findByFirToFir(flightMovement.getBillableEntryPoint(),
                            flightMovement.getBillableExitPoint(),FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),defaultLevel));
                    } else {
                        nominalRoute = findByFirToFir(flightMovement.getBillableEntryPoint(),
                                flightMovement.getBillableExitPoint(),null);
                    }
                }
            }

            if (nominalRoute == null) {
                // AERODROME TO AERODROME
                if(validateFlightLevel) {
                    nominalRoute = findByAerodromes(departureAerodrome, destinationAerodrome,FlightUtility.convertFlightLevel(flightMovement.getFlightLevel(),defaultLevel));
                } else {
                    nominalRoute = findByAerodromes(departureAerodrome, destinationAerodrome,null);
                }
            }
        }
        return nominalRoute;
    }

    long countAllNominalRoutes() {
        return nominalRouteRepository.count();
    }

    private BiDirectionalNominalRoute findByFirToAerodromeOrInverse(
        final String entryFir, final String exitFir, final String destinationAerodrome, final String departureAerodrome, final Double flightLevel
    ) {
        // If flight level for the airspace is required and not null - find nominal routes which contain given flight level between floor and ceiling
        // take the first one in case of overlapped

        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) && flightLevel != null) {
            return biDirectionalNominalRouteRepository.findByFirAndFlightLevelToAerodromeOrInverse(
                    entryFir,
                    exitFir,
                    destinationAerodrome,
                    departureAerodrome,
                    NominalRouteType.FIR_TO_AERODROME.getValue(),
                    flightLevel
                  );
        }
        // for other cases return the first nominal route by end points
        return biDirectionalNominalRouteRepository.findByFirToAerodromeOrInverse(
                    entryFir,
                    exitFir,
                    destinationAerodrome,
                    departureAerodrome,
                    NominalRouteType.FIR_TO_AERODROME.getValue()
                  );
    }

    private BiDirectionalNominalRoute findByFirToFir(final String fir1, final String fir2, final Double flightLevel) {
        
        // If flight level for the airspace is required and not null - find nominal routes which contain given flight level between floor and ceiling
        // take the first one in case of overlapped
        if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE) && flightLevel != null) {
            return biDirectionalNominalRouteRepository.findByFirToFirAndFlightLevel(fir1, fir2, NominalRouteType.FIR_TO_FIR.getValue(),flightLevel);
        }
        
        // for other cases return the first nominal route by end points
        return biDirectionalNominalRouteRepository.findByFirToFir(fir1, fir2, NominalRouteType.FIR_TO_FIR.getValue());
    }
}
