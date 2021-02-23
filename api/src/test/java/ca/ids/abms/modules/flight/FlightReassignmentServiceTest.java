package ca.ids.abms.modules.flight;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;

public class FlightReassignmentServiceTest {

    private FlightReassignmentRepository flightReassignmentRepository;
    private FlightReassignmentService flightReassignmentService;
    private FlightReassignmentAerodromeRepository flightReassignmentAerodromeRepository;
    private AccountRepository accountRepository;
    @Before
    public void setup() {
        flightReassignmentRepository = mock(FlightReassignmentRepository.class);
        flightReassignmentAerodromeRepository = mock(FlightReassignmentAerodromeRepository.class);
        accountRepository = mock(AccountRepository.class);
        flightReassignmentService = new FlightReassignmentService(flightReassignmentRepository, flightReassignmentAerodromeRepository, accountRepository);
    }

    @Test
    public void createFlightReassignment() throws Exception {
        FlightReassignment flightReassignment = new FlightReassignment();
        List<FlightReassignmentAerodrome> frsLst = new ArrayList();
        FlightReassignmentAerodrome fra = new FlightReassignmentAerodrome();
        fra.setAerodromeIdentifier("SVMI");
        frsLst.add(fra);
        flightReassignment.setIdentificationType("Flight Id");
        flightReassignment.setIdentifierText("LR5775");
        Account acc = new Account();
        acc.setId(1);
        flightReassignment.setAccount(acc);
        when(flightReassignmentRepository.save(any(FlightReassignment.class))).thenReturn(flightReassignment);
        when(accountRepository.getOne(any())).thenReturn(acc);
        FlightReassignment result = flightReassignmentService.create(flightReassignment, frsLst);
        assertThat(result.getIdentifierText()).isEqualTo(flightReassignment.getIdentifierText());
    }

    @Test
    public void getAllFlightReassignments() throws Exception {
        Account account = new Account();
        account.setId(1);
        List<FlightReassignment> flightReassignmentList = Collections.singletonList(new FlightReassignment());

        when(flightReassignmentRepository.findAll(any(FiltersSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(flightReassignmentList));
        Page<FlightReassignment> results = flightReassignmentService.findAll(1, null, mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(flightReassignmentList.size());
    }

    @Test
    public void getFlightReassignmentById() throws Exception {
        FlightReassignment flightReassignment = new FlightReassignment();
        flightReassignment.setId(1);

        when(flightReassignmentRepository.findOne(1)).thenReturn(flightReassignment);

        FlightReassignment result = flightReassignmentService.findOne(1);
        assertThat(result).isEqualTo(flightReassignment);
    }


    @Test
    public void updateFlightReassignment() throws Exception {
        FlightReassignment existingFlightReassignment = new FlightReassignment();
        List<FlightReassignmentAerodrome> frsLst = new ArrayList();
        FlightReassignmentAerodrome fra = new FlightReassignmentAerodrome();
        fra.setAerodromeIdentifier("SVMI");
        frsLst.add(fra);
        existingFlightReassignment.setIdentificationType("Flight Id");
        existingFlightReassignment.setIdentifierText("LR5775");
        Account acc = new Account();
        acc.setId(1);
        existingFlightReassignment.setAccount(acc);
        FlightReassignment updateFlightReassignment = new FlightReassignment();
        updateFlightReassignment.setIdentificationType("Flight Id");
        updateFlightReassignment.setIdentifierText("LR5776");
        updateFlightReassignment.setAccount(acc);
        when(flightReassignmentRepository.getOne(1)).thenReturn(existingFlightReassignment);
        when(flightReassignmentRepository.save(any(FlightReassignment.class))).thenReturn(existingFlightReassignment);

        when(accountRepository.getOne(any())).thenReturn(acc);
        FlightReassignment result = flightReassignmentService.update(1, updateFlightReassignment, frsLst);
        assertThat(result.getIdentifierText()).isEqualTo(updateFlightReassignment.getIdentifierText());
    }

    @Test
    public void deleteFlightReassignment() throws Exception {
        FlightReassignment existingFlightReassignment = new FlightReassignment();
        List<FlightReassignmentAerodrome> frsLst = new ArrayList();
        FlightReassignmentAerodrome fra = new FlightReassignmentAerodrome();
        fra.setAerodromeIdentifier("SVMI");
        frsLst.add(fra);
        existingFlightReassignment.setIdentificationType("Flight Id");
        existingFlightReassignment.setIdentifierText("LR5775");
        Account acc = new Account();
        acc.setId(1);
        existingFlightReassignment.setAccount(acc);
        when(flightReassignmentRepository.findOne(1)).thenReturn(existingFlightReassignment);
        flightReassignmentService.delete(1);
        verify(flightReassignmentRepository).delete(any(Integer.class));
    }
}
