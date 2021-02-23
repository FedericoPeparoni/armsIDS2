package ca.ids.abms.modules.localaircraftregistry;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalAircraftRegistryServiceTest {

    private LocalAircraftRegistryRepository localAircraftRegistryRepository;
    private LocalAircraftRegistryService localAircraftRegistryService;

    @Before
    public void setup() {
        localAircraftRegistryRepository = mock(LocalAircraftRegistryRepository.class);
        localAircraftRegistryService = new LocalAircraftRegistryService(localAircraftRegistryRepository);
    }

    @Test
    public void createLocalAircraftRegistry() throws Exception {
        LocalAircraftRegistry localAircraftRegistry = new LocalAircraftRegistry();
        localAircraftRegistry.setRegistrationNumber("ABC123");

        LocalDateTime renewal = LocalDateTime.now();
        LocalDateTime expiry = renewal.plusDays(3);
        localAircraftRegistry.setCoaDateOfRenewal(renewal);
        localAircraftRegistry.setCoaDateOfExpiry(expiry);

        when(localAircraftRegistryRepository.save(any(LocalAircraftRegistry.class))).thenReturn(localAircraftRegistry);

        LocalAircraftRegistry result = localAircraftRegistryService.save(localAircraftRegistry);
        assertThat(result.getRegistrationNumber()).isEqualTo(localAircraftRegistry.getRegistrationNumber());
    }

    @Test
    public void deleteLocalAircraftRegistry() throws Exception {
        localAircraftRegistryService.delete(1);
        verify(localAircraftRegistryRepository).delete(any(Integer.class));
    }


    @Test
    public void updateLocalAircraftRegistry() throws Exception {
        LocalAircraftRegistry existingLocalAircraftRegistry = new LocalAircraftRegistry();
        existingLocalAircraftRegistry.setRegistrationNumber("ABS123");

        LocalAircraftRegistry localAircraftRegistry = new LocalAircraftRegistry();
        localAircraftRegistry.setRegistrationNumber("NEW123");
        
        LocalDateTime renewal = LocalDateTime.now();
        LocalDateTime expiry = renewal.plusDays(3);
        localAircraftRegistry.setCoaDateOfRenewal(renewal);
        localAircraftRegistry.setCoaDateOfExpiry(expiry);

        when(localAircraftRegistryRepository.getOne(any())).thenReturn(existingLocalAircraftRegistry);

        when(localAircraftRegistryRepository.save(any(LocalAircraftRegistry.class))).thenReturn(existingLocalAircraftRegistry);

        LocalAircraftRegistry result = localAircraftRegistryService.update(1, localAircraftRegistry);

        assertThat(result.getRegistrationNumber()).isEqualTo("NEW123");
    }

    @Test
    public void getALocalAircraftRegistryById() throws Exception {
        LocalAircraftRegistry localAircraftRegistry = new LocalAircraftRegistry();
        localAircraftRegistry.setId(1);

        when(localAircraftRegistryRepository.getOne(any())).thenReturn(localAircraftRegistry);

        LocalAircraftRegistry result = localAircraftRegistryService.getOne(1);
        assertThat(result).isEqualTo(localAircraftRegistry);
    }
}
