package ca.ids.abms.modules.flightmovementsbuilder.utility;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.flightmovements.FlightMovementViewModel;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.KCAAFlightChargesType;
import ca.ids.abms.modules.flightmovementsbuilder.vo.SmallAircraftVO;
import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistryService;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementPair;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Component
public class KCAAFlightUtility {

    private static final Logger LOG = LoggerFactory.getLogger(KCAAFlightUtility.class);

    // Small Aircraft
    private static final String KCAA_COUNTRY_NAME = "Kenya";

    // Somali Flight Pair
    private static final String KCAA_FLIGHT_PAIR_AD = "HKWJ";
    private static final String SOMALIA_COUNTRY_NAME = "Somalia";

    private final AerodromeService aerodromeService;
    private final CountryService countryService;
    private final FlightMovementRepository flightMovementRepository;
    private final AircraftRegistrationService aircraftRegistrationService;
    private final AccountService accountService;
    private final SystemConfigurationService systemConfigurationService;
    private final PluginService pluginService;
    private final LocalAircraftRegistryService localAircraftRegistryService;

    @SuppressWarnings("squid:S00107")
    public KCAAFlightUtility(
        final AircraftRegistrationService aircraftRegistrationService,
        final CountryService countryService,
        final AccountService accountService,
        final AerodromeService aerodromeService,
        final FlightMovementRepository flightMovementRepository,
        final SystemConfigurationService systemConfigurationService,
        final PluginService pluginService,
        final LocalAircraftRegistryService localAircraftRegistryService
    ) {
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.aerodromeService = aerodromeService;
        this.countryService = countryService;
        this.accountService = accountService;
        this.flightMovementRepository = flightMovementRepository;
        this.systemConfigurationService = systemConfigurationService;
        this.pluginService = pluginService;
        this.localAircraftRegistryService = localAircraftRegistryService;
    }

    /**
     * Aircraft is local if valid aircraft registration where isLocal TRUE.
     */
    private SmallAircraftVO findLocalAircraft(String item18RegNum, String flightId, LocalDateTime dof) {
        if (item18RegNum == null && flightId == null && dof == null) return null;

        // find aircraft via the aircraft registration table
        AircraftRegistration aircraftRegistration = item18RegNum != null && !item18RegNum.isEmpty()
            ? aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(item18RegNum, dof)
            : aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(flightId, dof);

        // only return found aircraft if it is local
        return aircraftRegistration == null || !aircraftRegistration.getIsLocal() ? null : aircraftRegistration;
    }

    /**
     * Only for KCAA: Aircraft is local if it exist in local aircraft registry.
     */
    private SmallAircraftVO findLocalAircraftKCAA(String item18RegNum, String flightId, LocalDateTime dof) {
        if (item18RegNum == null && flightId == null && dof == null) return null;

        // find aircraft via local aircraft registry
        return item18RegNum != null && !item18RegNum.isEmpty()
            ? localAircraftRegistryService.findByRegistrationNumber(item18RegNum)
            : localAircraftRegistryService.findByRegistrationNumber(flightId);
    }

    /**
     * Only returns true if aircraft is a valid small aircraft BUT the CoA is NOT valid for date of flight.
     */
    public Boolean isInvalidSmallAircraft(final FlightMovement flightMovement) {
        if (flightMovement == null || !isSmallAircraft(flightMovement)) return false;

        LocalDateTime flightDate = flightMovement.getDateOfFlight();

        // KCAA registers small aircraft under LocalAircraftRegistry
        // non-KCAA organizations register small aircraft under AircraftRegistration where isLocal=TRUE
        SmallAircraftVO aircraft = systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            ? findLocalAircraftKCAA(flightMovement.getItem18RegNum(), flightMovement.getFlightId(), flightDate)
            : findLocalAircraft(flightMovement.getItem18RegNum(), flightMovement.getFlightId(), flightDate);

        // invalid small aircraft if..
        // KCAA: kcaa prefix and no local aircraft found or CoA is invalid for flight date
        // ELSE: found local aircraft and CoA is invalid for flight date
        return systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            ? isKCAAircraftByPrefix(flightMovement) && (aircraft == null || !aircraft.isSmallAircraftCoaValid(flightDate))
            : aircraft != null && !aircraft.isSmallAircraftCoaValid(flightDate);
    }

    /**
     * Returns true if valid small aircraft and CoA is valid for date of flight.
     */
    public Boolean isValidSmallAircraft(FlightMovement flightMovement) {
        if (flightMovement == null || !isSmallAircraft(flightMovement)) return false;

        LocalDateTime flightDate = flightMovement.getDateOfFlight();

        // KCAA registers small aircraft under LocalAircraftRegistry
        // non-KCAA organizations register small aircraft under AircraftRegistration where isLocal=TRUE
        SmallAircraftVO aircraft = systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            ? findLocalAircraftKCAA(flightMovement.getItem18RegNum(), flightMovement.getFlightId(), flightDate)
            : findLocalAircraft(flightMovement.getItem18RegNum(), flightMovement.getFlightId(), flightDate);

        // valid small aircraft if..
        // KCAA: kcaa prefix and local aircraft found with valid CoA for flight date
        // ELSE: found local aircraft with valid CoA for flight date
        return systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            ? isKCAAircraftByPrefix(flightMovement) && aircraft != null && aircraft.isSmallAircraftCoaValid(flightDate)
            : aircraft != null && aircraft.isSmallAircraftCoaValid(flightDate);
    }

    public Boolean isSmallAircraft(FlightMovement flight) {
        if (flight == null || flight.getActualMtow() == null) return false;

        // convert from kg to ton
        Double maxWeight = convertMTOWKgToTon(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT));
        return flight.getActualMtow() < maxWeight;
    }

    private Double convertMTOWKgToTon(Integer mtowKg) {
        Double result = null;
        if(mtowKg != null) {
            result = mtowKg/ 907.185;
        }
        return result;
    }

    public boolean isTrainingFlight(final FlightMovement flightMovement) {
        boolean trainingFlight = false;
        Account account = flightMovement.getAccount();
        if (account != null && account.getId() != null) {
            if (account.getApprovedFlightSchoolIndicator() == null) {
                // Get the account from the DB in case the embedded object is detached, some fields could be not filled
                account = accountService.getOne(account.getId());
            }
            trainingFlight = Boolean.TRUE.equals(account.getApprovedFlightSchoolIndicator()) &&
                org.apache.commons.lang.StringUtils.containsIgnoreCase(flightMovement.getItem18Rmk(), "TRAINING");
        }
        return trainingFlight;
    }

    @Transactional(readOnly = true)
    public KCAAFlightChargesType getKCAAFlightChargesType (final FlightMovement flight) {
        KCAAFlightChargesType chargeType = KCAAFlightChargesType.STANDARD_CHARGES;

        Double maxWeight = convertMTOWKgToTon(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT));
        if (flight.getActualMtow() != null && flight.getActualMtow() < maxWeight) {
            /* MTOW is under max_weight */

            if (findLocalAircraftKCAA(flight.getItem18RegNum(), flight.getFlightId(),flight.getDateOfFlight()) != null) {
                /* A small Kenyan aircraft.
                 *
                 * Charges for KCAA small aircraft
                 *
                 * condition: the registration number is found in the KCAA local aircraft registry
                 * condition: MTOW is < 2500kg
                 * condition: COA is valid
                 */
                chargeType = KCAAFlightChargesType.KCAA_SMALL_AIRCRAFT_CHARGES;
            } else if (isTrainingFlight(flight)) {
                /* It's a training flight but is not a small Kenyan aircraft */

                if (areNotTherePreviouslyPaidTrainingFlight(flight)) {
                    /* There aren't previous paid flights */
                    chargeType = KCAAFlightChargesType.TRAINING_CONFIGURED_CHARGES;
                } else {
                    /* There are previous paid flights */
                    chargeType = KCAAFlightChargesType.TRAINING_NO_CHARGES;
                }
            }
        } else if (isTrainingFlight(flight)) {
            /* It's a training flight and MTOW is over 2500kg */

            if (areNotTherePreviouslyPaidTrainingFlight(flight)) {
                /* There aren't previous paid flights */
                chargeType = KCAAFlightChargesType.TRAINING_CONFIGURED_CHARGES;
            } else {
                /* There are previous paid flights */
                chargeType = KCAAFlightChargesType.TRAINING_NO_CHARGES;
            }
        }
        return chargeType;
    }

    /**
     * Find all previously training flights with enroute charges precalculated in pending or paid status
     */
    private boolean areNotTherePreviouslyPaidTrainingFlight (final FlightMovement sourceFlight) {
        if (StringUtils.isBlank(sourceFlight.getItem18RegNum())) {
            return true;
        }
        return CollectionUtils.isEmpty(
            flightMovementRepository.findAllPendingTrainingFlightsOfCurrentDay(
                sourceFlight.getItem18RegNum(), sourceFlight.getDateOfFlight(),
                sourceFlight.getId() != null ? sourceFlight.getId() : 0));
    }

    /**
     * Check if flight is a KCAA flight by matching registration number prefix to a country
     *
     * @param flight;
     * @return boolean;
     */
    public Boolean isKCAAircraftByPrefix(FlightMovement flight) {
        if (flight == null) {
            return false;
        }

        Country country = flight.getItem18RegNum() != null && !flight.getItem18RegNum().isEmpty()
            ? countryService.findCountryByPrefix(flight.getItem18RegNum())
            : countryService.findCountryByPrefix(flight.getFlightId());

        return country != null && country.getCountryName().equalsIgnoreCase(KCAA_COUNTRY_NAME);
    }

    /**
     * Check if flight is a KCAA flight by matching registration number prefix to a country
     *
     * @param flight;
     * @return boolean;
     */
    public Boolean isKCAAircraftByPrefix(FlightMovementViewModel flight) {
        if (flight == null) {
            return false;
        }

        Country country = flight.getItem18RegNum() != null && !flight.getItem18RegNum().isEmpty()
            ? countryService.findCountryByPrefix(flight.getItem18RegNum())
            : countryService.findCountryByPrefix(flight.getFlightId());

        return country != null && country.getCountryName().equalsIgnoreCase(KCAA_COUNTRY_NAME);
    }

    /**
     * KCAA Somali flight pair is defined by:
     * flight_id;
     * date_of_flight;
     * date of flight of first and second flight are the same;
     * first segment: any Somali AD to Wajir;
     * second segment: Wajir to any Kenya AD;
     *
     * @param flight;
     * @return List<FlightMovement>;
     */
    public FlightMovementPair findKCAASomaliFlightPair(FlightMovement flight) {
        // flight Pair
        FlightMovementPair flightMovementPair = new FlightMovementPair();
        // arriving at KCAA_FLIGHT_PAIR_AD
        FlightMovement firstSegment = KCAA_FLIGHT_PAIR_AD.equalsIgnoreCase(flight.getArrivalAd()) ? flight : null;
        // departing KCAA_FLIGHT_PAIR_AD
        FlightMovement secondSegment = KCAA_FLIGHT_PAIR_AD.equalsIgnoreCase(flight.getDepAd()) ? flight : null;

        /*
         * skip other operations if the flight is not associated with the KCAA_FLIGHT_PAIR_AD
         * and/ or first segment doesn't depart from Somalia or second segment doesn't land in Kenya
         */
        if (!isDepOrDestValid(firstSegment, secondSegment)) {
            return null;
        }

        // find matching flight movements
        // 2019-10-18
        // Find all FM with the same flightId and DOF with dep_time after first segment or before second segment
        // Filter out the flights which don't have wajir as one of the ads or are PAID, INVOICED
        // check the closest flight from or to wajir
        // if you expect second leg, but the closest flight is not - your flight is a single flight, no match
        // if it is a second leg - match is found.
        // The same for the first leg.
        
        List<FlightMovement> matchingFlightMovements = findMatchingFlightMovements(firstSegment, secondSegment);

        if (matchingFlightMovements == null) {
            return null;
        }
        
        matchingFlightMovements.removeIf(f -> f.getStatus() != FlightMovementStatus.PENDING && f.getStatus() != FlightMovementStatus.INCOMPLETE);
        
        if(matchingFlightMovements.isEmpty()) {
            return null;
        }
        // next flight is a pair
        if(firstSegment == null ){
            // found first segment
           firstSegment = matchingFlightMovements.get(0);
        } 
        if(secondSegment == null) {
            // found second segment 
            secondSegment = matchingFlightMovements.get(0);
        }

        if(this.isSomaliaPairValid(firstSegment, secondSegment)) {
            flightMovementPair.setSegmentOne(matchingFlightMovements.get(0));
            flightMovementPair.setSegmentTwo(secondSegment);
        } else {
            return null;
        }
        

        return flightMovementPair;
    }

    /**
     * Checks if flight departure AD is within Somali FIR
     *
     * @param firstSegment;
     * @param secondSegment;
     * @return boolean;
     */
    private Boolean isDepOrDestValid(FlightMovement firstSegment, FlightMovement secondSegment) {
        Boolean isValid = true;

        // if neither segment is present, skip
        if (firstSegment == null && secondSegment == null) {
            isValid = false;
        }
        // if the first segment is present, ensure it departs from Somalia
        if (firstSegment != null && firstSegment.getDepAd() != null && !isDepAdWithinSomalia(firstSegment)) {
            isValid = false;
        }
        // if the second segment is present, ensure it lands in Kenya
        if (secondSegment != null && secondSegment.getArrivalAd() != null && !isDestAdWithinKenya(secondSegment)) {
            isValid = false;
        }

        return isValid;
    }

  private Boolean isSomaliaPairValid(FlightMovement firstSegment, FlightMovement secondSegment) {
        Boolean isValid = false;
        if(firstSegment != null && secondSegment!=null &&
            firstSegment.getDepAd() != null && isDepAdWithinSomalia(firstSegment) &&
                    secondSegment.getArrivalAd() != null && isDestAdWithinKenya(secondSegment) ) {
            isValid = true;
        }
        return isValid;
    }
    
    /**
     * Finds matching flight movements
     *
     * @param firstSegment;
     * @param secondSegment;
     * @return List<FlightMovement>;
     */
    private List<FlightMovement> findMatchingFlightMovements(FlightMovement firstSegment, FlightMovement secondSegment) {
        List<FlightMovement> matches = new ArrayList<>();
        Integer idToFilter;

        if (firstSegment != null) {
            if (firstSegment.getFlightId() == null || firstSegment.getDateOfFlight() == null || firstSegment.getDepTime() == null) {
                LOG.debug("Couldn't calculate first segment because of missing data. Flight Id or billing date or departure time is missing");
                return matches;
            }
            idToFilter =  firstSegment.getId();
            matches = flightMovementRepository.findForKCAASomaliFlightPairsAfterFlight(firstSegment.getFlightId(), firstSegment.getDateOfFlight(), firstSegment.getDepTime());
                    
        } else {
            if (secondSegment ==null || secondSegment.getFlightId() == null || secondSegment.getDateOfFlight() == null || secondSegment.getDepTime() == null) {
                LOG.debug("Couldn't calculate second segment because of missing data. Flight Id or billing date or departure time is missing");
                return matches;
            }
            idToFilter =secondSegment.getId();
            matches = flightMovementRepository.findForKCAASomaliFlightPairsBeforeFlight(secondSegment.getFlightId(), secondSegment.getDateOfFlight(), secondSegment.getDepTime());
        }
        // remove current flight from results
        if(matches!=null && idToFilter != null)
            matches.removeIf(f -> f.getId() != null && f.getId().equals(idToFilter));

        return matches;
    }

    /**
     * Checks if flight departure AD is within Somali FIR
     *
     * @param flight;
     * @return boolean;
     */
    private Boolean isDepAdWithinSomalia(FlightMovement flight) {
        return aerodromeService.isAdPrefixAssociatedWithCountry(flight.getDepAd(), SOMALIA_COUNTRY_NAME);
    }

    /**
     * Checks if flight arrival AD is within Kenyan FIR
     *
     * @param flight;
     * @return boolean;
     */
    private Boolean isDestAdWithinKenya(FlightMovement flight) {
        return aerodromeService.isAdPrefixAssociatedWithCountry(flight.getArrivalAd(), KCAA_COUNTRY_NAME);
    }

    public void setAdhocChargeRequiredForFlightMovements(List<FlightMovementViewModel> flightMovements) {
        // add adhoc fee requirement for KCAA flights
        if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            && pluginService.isEnabled(PluginKey.KCAA_AATIS)) {

            for (FlightMovementViewModel flightMovement : flightMovements) {
                setAdhocChargeRequired(flightMovement);
            }
        }
    }

    public void setAdhocChargeRequiredForFlightMovements(FlightMovementViewModel flightMovement) {
        // add adhoc fee requirement for KCAA flights
        if (systemConfigurationService.getBillingOrgCode() == BillingOrgCode.KCAA
            && pluginService.isEnabled(PluginKey.KCAA_AATIS)) {

            setAdhocChargeRequired(flightMovement);
        }
    }

    private void setAdhocChargeRequired(FlightMovementViewModel flightMovement) {
        // is KCAA aircraft: not required
        boolean isKcaaAircraft = this.isKCAAircraftByPrefix(flightMovement)
            || this.findLocalAircraftKCAA(flightMovement.getItem18RegNum(), flightMovement.getFlightId(), flightMovement.getDateOfFlight()) != null;

        // is scheduled: not required
        boolean isScheduled = flightMovement.getFlightType() != null
            // flight type N is NON SCHEDULED
            && !flightMovement.getFlightType().equalsIgnoreCase("N");

        // has preexisting adhoc charge: not required
        boolean hasAdhocCharge = flightMovement.getTaspCharge() != null;

        flightMovement.setAdhocChargeRequired(!isKcaaAircraft && !hasAdhocCharge && !isScheduled);
    }
}
