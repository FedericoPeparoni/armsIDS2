package ca.ids.abms.modules.aircraft;

import ca.ids.abms.modules.countries.Country;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import org.springframework.data.jpa.domain.Specification;

public class AircraftRegistrationServiceTest {

    private AircraftRegistrationRepository aircraftRegistrationRepository;
    private AircraftRegistrationPrefixRepository aircraftRegistrationPrefixRepository;
    private AircraftRegistrationService aircraftRegistrationService;

    @Test
    public void createAircraftRegistration() {
        AircraftRegistration aircraftRegistration = new AircraftRegistration();
        aircraftRegistration.setRegistrationNumber("I00001");
        aircraftRegistration.setCountryOfRegistration(new Country());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);
        aircraftRegistration.setRegistrationStartDate(start );
        aircraftRegistration.setRegistrationExpiryDate(end);
        AircraftType at = new AircraftType();
        at.setId(1);
        aircraftRegistration.setAircraftType(at);
        aircraftRegistration.setAccount(mockAccount());

        AircraftRegistrationPrefix prefix = new AircraftRegistrationPrefix();
        prefix.setAircraftRegistrationPrefix("I");
        List<AircraftRegistrationPrefix> prefixes = new ArrayList<>();
        prefixes.add(prefix);

        when(aircraftRegistrationRepository.save(any(AircraftRegistration.class)))
        .thenReturn(aircraftRegistration);
        when(aircraftRegistrationPrefixRepository.findByCountryCode(any(Country.class)))
        .thenReturn(prefixes);

        AircraftRegistration result = aircraftRegistrationService.save(aircraftRegistration);
        assertThat(result.getRegistrationNumber()).isEqualTo(aircraftRegistration.getRegistrationNumber());
    }

    @Test
    public void deleteAircraftRegistration() {
        aircraftRegistrationService.delete(1);
        verify(aircraftRegistrationRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllAircraftRegistrations() {
        List<AircraftRegistration> aircraftRegistrations = Collections.singletonList(new AircraftRegistration());

        // noinspection unchecked
        when(aircraftRegistrationRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(aircraftRegistrations));

        Page<AircraftRegistration> results = aircraftRegistrationService.findAll(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(aircraftRegistrations.size());
    }

    @Test
    public void getAircraftRegistrationById() {
        AircraftRegistration aircraftRegistration = new AircraftRegistration();
        aircraftRegistration.setId(1);

        when(aircraftRegistrationRepository.getOne(any()))
        .thenReturn(aircraftRegistration);

        AircraftRegistration result = aircraftRegistrationService.getOne(1);
        assertThat(result).isEqualTo(aircraftRegistration);
    }

    @Before
    public void setup() {
        AccountRepository accountRepository = mock(AccountRepository.class);

        aircraftRegistrationRepository = mock(AircraftRegistrationRepository.class);
        aircraftRegistrationPrefixRepository = mock(AircraftRegistrationPrefixRepository.class);
        aircraftRegistrationService = new AircraftRegistrationService(accountRepository, aircraftRegistrationRepository,
            aircraftRegistrationPrefixRepository, null, null);

        when(accountRepository.getOne(anyInt()))
            .thenReturn(new Account());
    }

    @Test
    public void updateAircraftRegistration() {
        AircraftRegistration existingAircraftRegistration = new AircraftRegistration();
        existingAircraftRegistration.setRegistrationNumber("I110");

        AircraftRegistration aircraftRegistration = new AircraftRegistration();
        aircraftRegistration.setRegistrationNumber("I0000new");
        aircraftRegistration.setCountryOfRegistration(new Country());
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusDays(3);
        aircraftRegistration.setRegistrationStartDate(start);
        aircraftRegistration.setRegistrationExpiryDate(end);
        aircraftRegistration.setAccount(mockAccount());

        AircraftRegistrationPrefix prefix = new AircraftRegistrationPrefix();
        prefix.setAircraftRegistrationPrefix("I");
        List<AircraftRegistrationPrefix> prefixes = new ArrayList<>();
        prefixes.add(prefix);

        when(aircraftRegistrationPrefixRepository.findByCountryCode(any(Country.class)))
        .thenReturn(prefixes);

        when(aircraftRegistrationRepository.getOne(any()))
        .thenReturn(existingAircraftRegistration);

        when(aircraftRegistrationRepository.save(any(AircraftRegistration.class)))
        .thenReturn(existingAircraftRegistration);

        AircraftRegistration result = aircraftRegistrationService.update(1, aircraftRegistration);

        assertThat(result.getRegistrationNumber()).isEqualTo("I0000NEW");
    }

    private Account mockAccount() {
        Account account =  new Account();
        account.setId(0);
        return account;
    }
}
