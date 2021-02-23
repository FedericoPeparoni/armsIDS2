package ca.ids.abms.modules.flight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;

public class FlightScheduleServiceTest {

    private FlightScheduleRepository flightScheduleRepository;
    private FlightMovementService flightMovementService;
    private FlightScheduleService flightScheduleService;
    private AerodromeService aerodromeService;

    @Before
    public void setup() {
        flightScheduleRepository = mock(FlightScheduleRepository.class);
        flightMovementService = mock(FlightMovementService.class);
        aerodromeService = mock(AerodromeService.class);

        AccountRepository accountRepository = mock(AccountRepository.class);

        flightScheduleService = new FlightScheduleService(flightScheduleRepository, accountRepository,
            mock(FlightMovementBuilderUtility.class), aerodromeService,
            mock(UnspecifiedDepartureDestinationLocationService.class), flightMovementService);

        when(accountRepository.getOne(anyInt()))
            .thenReturn(mockAccount());
    }

    @Test
    public void createFlightSchedule() {
        FlightSchedule flightSchedule = mockFlightSchedule("FALA", "FAOR");
        flightSchedule.setFlightServiceNumber("FSN001");

        when(flightScheduleRepository.save(any(FlightSchedule.class)))
            .thenReturn(flightSchedule);
        when(aerodromeService.checkAerodromeIdentifier(any()))
            .thenReturn(flightSchedule.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier(any()))
            .thenReturn(flightSchedule.getDestAd());

        FlightSchedule result = flightScheduleService.create(flightSchedule);
        assertThat(result.getFlightServiceNumber()).isEqualTo(flightSchedule.getFlightServiceNumber());
    }

    @Test
    public void getAllFlightSchedules() {
        List<FlightSchedule> flightScheduleList = Collections.singletonList(mockFlightSchedule("ABCD", "WXYZ"));

        when(flightScheduleRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(flightScheduleList));

        Page<FlightSchedule> results = flightScheduleService.findAll(1, null, mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(flightScheduleList.size());
    }

    @Test
    public void getFlightScheduleById() {
        when(flightScheduleRepository.findOne(0))
            .thenReturn(mockFlightSchedule("ABCD", "WXYZ"));

        FlightSchedule result = flightScheduleService.findOne(0);
        assertThat(result).isEqualTo(mockFlightSchedule("ABCD", "WXYZ"));
    }

    @Test
    public void updateFlightSchedule() {
        FlightSchedule existingFlightSchedule = mockFlightSchedule("FBFT", "FBKE");
        FlightSchedule updateFlightSchedule = mockFlightSchedule("FALA", "FAOR");

        when(flightScheduleRepository.getOne(1)).thenReturn(existingFlightSchedule);
        when(flightScheduleRepository.save(any(FlightSchedule.class))).thenReturn(existingFlightSchedule);

        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(updateFlightSchedule.getDepAd());
        when(aerodromeService.checkAerodromeIdentifier(any())).thenReturn(updateFlightSchedule.getDestAd());

        FlightSchedule result = flightScheduleService.update(1, updateFlightSchedule);
        assertThat(result.getDepAd()).isEqualTo(updateFlightSchedule.getDepAd());
    }

    @Test
    public void deleteFlightSchedule() {
        flightScheduleService.delete(1);
        verify(flightScheduleRepository).delete(any(Integer.class));
    }

    @Test
    public void findMissingFlightsTest() {

        // define valid flight schedule
        FlightScheduleViewModel flightSchedule = mockFlightScheduleViewModel();

        // mock flight movement repository method
        when(flightMovementService.findAllByLogicalKey(anyString(), any(LocalDateTime.class), anyString(), anyString()))
            .thenReturn(Collections.emptyList());

        // assert that find missing flights returns list of size 6 for each daily scheduled day for each week active
       flightScheduleService.resolveMissingAndUnexpectedFlights(flightSchedule);
        assertThat(flightSchedule.getMissingFlightMovements().size()).isEqualTo(6);
    }

    @Test
    public void findUnexpectedFlightsTest() {

        // define valid flight schedule
        FlightScheduleViewModel flightSchedule = mockFlightScheduleViewModel();

        // mock flight movement repository method
        when(flightMovementService.findAllByLogicalKey(anyString(), any(LocalDateTime.class), anyString(), anyString()))
            .thenReturn(Collections.singletonList(new FlightMovement()));

        // assert that find unexpected flights returns list of size 8 for each non daily scheduled day for each week active
        flightScheduleService.resolveMissingAndUnexpectedFlights(flightSchedule);
        assertThat(flightSchedule.getUnexpectedFlights().size()).isEqualTo(8);

        // assert that find unexpected flights returns empty list when daily schedule is every day of week
        flightSchedule.setDailySchedule("1,2,3,4,5,6,7");
        flightScheduleService.resolveMissingAndUnexpectedFlights(flightSchedule);
        assertThat(flightSchedule.getUnexpectedFlights()).isEmpty();
    }

    private Account mockAccount() {
        final Account account = new Account();
        account.setId(0);
        account.setName("MOCK ACCOUNT NAME");
        return account;
    }

    private FlightSchedule mockFlightSchedule(final String depAd, final String destAd) {
        FlightSchedule flightSchedule = new FlightSchedule();

        flightSchedule.setId(0);
        flightSchedule.setDepAd(depAd);
        flightSchedule.setDestAd(destAd);
        flightSchedule.setAccount(mockAccount());

        return flightSchedule;
    }

    private FlightScheduleViewModel mockFlightScheduleViewModel() {
        final LocalDate currentDate = LocalDate.now();
        FlightScheduleViewModel flightSchedule = new FlightScheduleViewModel();
        flightSchedule.setFlightServiceNumber("ABC1234");
        flightSchedule.setDepAd("FBFT");
        flightSchedule.setDepTime("0900");
        flightSchedule.setDestAd("FBKE");
        flightSchedule.setDailySchedule("2,5,6");
        flightSchedule.setStartDate(currentDate.minusWeeks(1).atStartOfDay());
        flightSchedule.setEndDate(currentDate.plusWeeks(1).atStartOfDay());
        return flightSchedule;
    }
}
