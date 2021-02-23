package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountEventMapRepository;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefixRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistrationRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.exemptions.account.AccountExemptionRepository;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.RouteParserWrapper;
import ca.ids.abms.modules.flightmovementsbuilder.utility.cache.mapper.RouteCacheSegmentMapper;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;
import ca.ids.abms.modules.notifications.NotificationEventTypeRepository;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.SelfCarePortalInactivityExpiryNoticesService;
import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FlightMovementBuilderUtilityTest {

    private FlightMovementBuilderUtility flightMovementBuilderUtility;

    @Mock
    private AircraftTypeService aircraftTypeService;
    @Mock
    private UnspecifiedAircraftTypeService unspecifiedAircrafttypeService;
    @Mock
    private AircraftRegistrationRepository aircraftRegistrationRepository;
    @Mock
    private AircraftRegistrationPrefixRepository aircraftRegistrationPrefixRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private FlightMovementAerodromeService flightMovementAerodromeService;
    @Mock
    private AirspaceService airspaceService;
    @Mock
    private AverageMtowFactorService averageMtowFactorService;
    @Mock
    private AccountExemptionRepository accountExemptionRepository;
    @Mock
    private AccountEventMapRepository accountEventMapRepository;
    @Mock
    private NotificationEventTypeRepository notificationEventTypeRepository;
    @Mock
    private FlightMovementRepository flightMovementRepository;
    

    @Before
    public void init(){
        AccountService accountService = new AccountService(accountRepository, accountExemptionRepository, flightMovementRepository, mock(BillingLedgerRepository.class),
            accountEventMapRepository, notificationEventTypeRepository, mock(SelfCarePortalInactivityExpiryNoticesService.class), mock(QuerySubmissionService.class));

        AircraftRegistrationService aircraftRegistrationService = new AircraftRegistrationService(accountRepository, aircraftRegistrationRepository, aircraftRegistrationPrefixRepository);

        FlightMovementAircraftService flightMovementAircraftService = new FlightMovementAircraftService(
            aircraftTypeService, aircraftRegistrationService, unspecifiedAircrafttypeService, flightMovementRepository);

        flightMovementBuilderUtility = new FlightMovementBuilderUtility(flightMovementAerodromeService,
            flightMovementAircraftService, accountService, airspaceService, averageMtowFactorService,
            mock(RouteParserWrapper.class), mock(RouteCacheSegmentMapper.class), mock (FlightMovementRepository.class), mock(SystemConfigurationService.class));
    }

    @Test
    public void resolveAccountForFlightMovement () {
        when(accountRepository.findByOprIdentifier(eq("OPR")))
            .thenReturn (buildAccount("found_by_opr"));
        when(accountRepository.findByIcaoCode(eq("ICA")))
            .thenReturn (buildAccount("found_by_icao_code"));
        when(aircraftRegistrationRepository.findAircraftRegistrationByRegistrationNumberAndCheckDate (eq ("REG01"), any()))
            .thenReturn (buildRegList (buildAccount ("found_by_reg_num")));
        when(aircraftRegistrationRepository.findAircraftRegistrationByRegistrationNumber (eq ("REG01")))
            .thenReturn (buildRegList (buildAccount ("found_by_reg_num")));
        when(accountRepository.getOne(anyInt()))
            .thenReturn (buildAccount ("found_by_account_id"));
        
        final FlightMovement fm = new FlightMovement();
        fm.setItem18Operator("OPR");
        fm.setFlightId("ICAO_FLIGHT");
        fm.setItem18RegNum("REG01");
        fm.setAccount (buildAccount ("account"));
        fm.setDateOfFlight(LocalDateTime.now());

        // item18_operator => account.opr_identifier
        assertThat (resolveAccount (fm).getName()).isEqualTo("found_by_opr");
        
        // item18_operator => account.icao_code
        fm.setItem18Operator("ICA");
        assertThat (resolveAccount (fm).getName()).isEqualTo("found_by_icao_code");

        // invalid operator, valid registration
        fm.setItem18Operator("INVALID");
        fm.setItem18RegNum("REG01");
        assertThat (resolveAccount (fm).getName()).isEqualTo("found_by_reg_num");
        
        // flight_id
        fm.setFlightId("ICA1234");
        fm.setItem18RegNum("TEST");
        assertThat (resolveAccount (fm).getName()).isEqualTo("found_by_icao_code");
 
        // flight_id
        fm.setFlightId("INVALID");
        assertThat (resolveAccount (fm)).isNull();
 
        
        // account id
        fm.setFlightId("INVALID");
        assertThat (resolveAccount (fm)).isNull();
    }
    
    private Account resolveAccount (final FlightMovement fm) {
        return this.flightMovementBuilderUtility.resolveAccountForFlightMovement(fm);
    }

    private Account buildAccount(final String name) {
        final Account account = new Account();
        account.setId(1);
        account.setName(name);
        return account;
    }
    
    private List <AircraftRegistration> buildRegList (final Account account) {
        final AircraftRegistration x = new AircraftRegistration();
        x.setAccount (account);
        return Collections.singletonList(x);
    }
}
