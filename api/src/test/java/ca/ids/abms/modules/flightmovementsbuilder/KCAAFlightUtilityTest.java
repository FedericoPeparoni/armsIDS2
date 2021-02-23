package ca.ids.abms.modules.flightmovementsbuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aerodromes.AerodromePrefixService;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefixService;
import ca.ids.abms.modules.aircraft.AircraftRegistrationRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.FlightMovementPair;
import ca.ids.abms.modules.flightmovementsbuilder.utility.KCAAFlightUtility;
import ca.ids.abms.modules.localaircraftregistry.LocalAircraftRegistryService;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class KCAAFlightUtilityTest {

	private  AircraftRegistrationService aircraftRegistrationService;
    private  LocalAircraftRegistryService localAircraftRegistryService;
    private  CountryService countryService;
    private  FlightMovementRepository flightMovementRepository;
    private  AerodromeService aerodromeService;
    private  AccountService accountService;
    private  SystemConfigurationService systemConfigurationService;
    private  PluginService pluginService;
    private  AerodromePrefixService aerodromePrefixService;

    private static final LocalDateTime DATE_OF_FLIGHT = LocalDate.now().atStartOfDay();
    private static final String KCAA_FLIGHT_PAIR_AD = "HKWJ";
    private static final String VALID_KCAA_AD = "HKJK";
    private static final String SOMALI_AD = "HCMM";
    private static final String FLIGHT_ID = "5Y123";

    @Before
    public void setup() {

        aerodromePrefixService = mock(AerodromePrefixService.class);
        AircraftRegistrationPrefixService aircraftRegistrationPrefixService = mock(AircraftRegistrationPrefixService.class);
        CountryRepository countryRepository = mock(CountryRepository.class);
        
        AircraftRegistrationRepository aircraftRegistrationRepository = mock(AircraftRegistrationRepository.class);
        aircraftRegistrationService = mock(AircraftRegistrationService.class);
        localAircraftRegistryService = mock(LocalAircraftRegistryService.class);
        systemConfigurationService = mock(SystemConfigurationService.class);

        accountService = mock(AccountService.class);
        aerodromeService = mock(AerodromeService.class);
        countryService = new CountryService(aerodromePrefixService, aircraftRegistrationPrefixService,
            countryRepository, systemConfigurationService);
        flightMovementRepository = mock(FlightMovementRepository.class);

        // mock country
        pluginService = mock(PluginService.class);

        Country country = new Country();
        country.setId(1);
        country.setCountryCode("KEN");
        country.setCountryName("Kenya");

        // mock local aircraft registry
        AircraftRegistration aircraft = new AircraftRegistration();
        aircraft.setRegistrationNumber(FLIGHT_ID);
        aircraft.setMtowOverride(2.00);
        aircraft.setCoaExpiryDate(DATE_OF_FLIGHT.plusDays(2));

        // aerodrome service
        when(aerodromeService.isAdPrefixAssociatedWithCountry(SOMALI_AD, "Somalia"))
            .thenReturn(true);
        when(aerodromeService.isAdPrefixAssociatedWithCountry(VALID_KCAA_AD, "Kenya"))
            .thenReturn(true);

        // country repository
        when(countryRepository.findCountryByPrefix(any()))
            .thenReturn(country);

        // local aircraft registry repository
        List<AircraftRegistration> al = new ArrayList<>();
        al.add(aircraft);
        
        when(aircraftRegistrationRepository.findAircraftRegistrationByRegistrationNumberAndCheckDate(FLIGHT_ID, DATE_OF_FLIGHT))
            .thenReturn(al);
        when(aircraftRegistrationRepository.findAircraftRegistrationByRegistrationNumber(FLIGHT_ID))
            .thenReturn(al);
        when(aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(FLIGHT_ID, DATE_OF_FLIGHT))
            .thenReturn(aircraft);
    }

    @Test
    public void isKCAAByPrefix() {
        KCAAFlightUtility kFU = new KCAAFlightUtility(
        	aircraftRegistrationService,
            countryService,
            accountService,
            aerodromeService,
            flightMovementRepository,
            systemConfigurationService,
            pluginService,
            localAircraftRegistryService
        );

        FlightMovement fmValid = new FlightMovement();
        fmValid.setItem18RegNum(FLIGHT_ID);
        Boolean isKCAAircraftByPrefix = kFU.isKCAAircraftByPrefix(fmValid);
        assertThat(isKCAAircraftByPrefix).isTrue();
    }

    @Test
    public void isValidSmallAircraft() {
        KCAAFlightUtility kFU = new KCAAFlightUtility(
        	aircraftRegistrationService,
            countryService,
            accountService,
            aerodromeService,
            flightMovementRepository,
            systemConfigurationService,
            pluginService,
            localAircraftRegistryService
        );

        FlightMovement fmValid = new FlightMovement();
        fmValid.setItem18RegNum(FLIGHT_ID);
        fmValid.setActualMtow(1.2);
        fmValid.setDateOfFlight(DATE_OF_FLIGHT);
        when(systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT)).thenReturn(5700);
       
        AircraftRegistration aircraft = new AircraftRegistration();
        aircraft.setRegistrationNumber(FLIGHT_ID);
        aircraft.setMtowOverride(1.2);
        aircraft.setCoaExpiryDate(DATE_OF_FLIGHT.plusDays(2));
        aircraft.setCoaIssueDate(DATE_OF_FLIGHT.minusDays(3));
        aircraft.setIsLocal(true);
        
        when(aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(FLIGHT_ID, DATE_OF_FLIGHT))
            .thenReturn(aircraft);
        Boolean isValidKCAASmallAircraft = kFU.isValidSmallAircraft(fmValid);
        assertThat(isValidKCAASmallAircraft).isTrue();

        // Billing date past COA expiry
        FlightMovement fmInvalidDate = new FlightMovement();
        fmInvalidDate.setItem18RegNum(FLIGHT_ID);
        fmInvalidDate.setActualMtow(1.2);
        fmInvalidDate.setDateOfFlight(DATE_OF_FLIGHT.plusDays(3));

        Boolean isInvalidDateKCAASmallAircraft = kFU.isValidSmallAircraft(fmInvalidDate);
        assertThat(isInvalidDateKCAASmallAircraft).isFalse();

        // MTOW too high to be considered small aircraft
        FlightMovement fmInvalidWeight = new FlightMovement();
        fmInvalidWeight.setItem18RegNum(FLIGHT_ID);
        fmInvalidWeight.setActualMtow(7.2);
        fmInvalidWeight.setDateOfFlight(DATE_OF_FLIGHT);

        Boolean isInvalidWeightKCAASmallAircraft = kFU.isValidSmallAircraft(fmInvalidWeight);
        assertThat(isInvalidWeightKCAASmallAircraft).isFalse();
    }

    @Test
    public void findKCAASomaliFlightPair() {
        KCAAFlightUtility kFU = new KCAAFlightUtility(
        	aircraftRegistrationService,
            countryService,
            accountService,
            aerodromeService,
            flightMovementRepository,
            systemConfigurationService,
            pluginService,
            localAircraftRegistryService
        );


        FlightMovement segmentOne = new FlightMovement();
        segmentOne.setId(1);
        segmentOne.setBillingDate(LocalDateTime.of(2017, 12, 1, 0, 0, 0));
        segmentOne.setDateOfFlight(LocalDateTime.of(2017, 12, 1, 0, 0, 0));
        segmentOne.setFlightId("KCAA FLIGHT PAIR");
        segmentOne.setDepAd(SOMALI_AD);
        segmentOne.setArrivalAd(KCAA_FLIGHT_PAIR_AD);
        segmentOne.setDepTime("1400");
        segmentOne.setStatus(FlightMovementStatus.PENDING);

        FlightMovement segmentTwo = new FlightMovement();
        segmentTwo.setId(2);
        segmentTwo.setBillingDate(LocalDateTime.of(2017, 12, 1, 0, 0, 0));
        segmentTwo.setDateOfFlight(LocalDateTime.of(2017, 12, 1, 0, 0, 0));
        segmentTwo.setFlightId("KCAA FLIGHT PAIR");
        segmentTwo.setDepAd(KCAA_FLIGHT_PAIR_AD);
        segmentTwo.setArrivalAd(VALID_KCAA_AD);
        segmentTwo.setDepTime("1430");
        segmentTwo.setStatus(FlightMovementStatus.PENDING);

        // arriving at KCAA_FLIGHT_PAIR_AD, find next flight
        List<FlightMovement> segmentOneList = new ArrayList<>();

        segmentOneList.add(segmentTwo);

        when(flightMovementRepository.findForKCAASomaliFlightPairsAfterFlight(
            segmentOne.getFlightId(),
            segmentOne.getDateOfFlight(),
            segmentOne.getDepTime()
        )).thenReturn(segmentOneList);

        FlightMovementPair flightMovementPair = kFU.findKCAASomaliFlightPair(segmentOne);

        assertThat(flightMovementPair.getSegmentTwo()).isNotNull();
        assertThat(flightMovementPair.getSegmentTwo()).isEqualTo(segmentTwo);

        // departing KCAA_FLIGHT_PAIR_AD, find previous flight
        List<FlightMovement> segmentTwoList = new ArrayList<>();

        segmentTwoList.add(segmentOne);

        when(flightMovementRepository.findForKCAASomaliFlightPairsBeforeFlight(
                segmentTwo.getFlightId(),
                segmentTwo.getDateOfFlight(),
                segmentTwo.getDepTime()
        )).thenReturn(segmentTwoList);

        FlightMovementPair flightMovementPairReverse = kFU.findKCAASomaliFlightPair(segmentTwo);

        assertThat(flightMovementPairReverse.getSegmentOne()).isNotNull();
        assertThat(flightMovementPairReverse.getSegmentOne()).isEqualTo(segmentOne);
    }
}
