package ca.ids.abms.modules.system;

import ca.ids.abms.modules.countries.CountryRepository;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.transactions.TransactionRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SystemConfigurationServiceTest {
    private SystemConfigurationRepository systemConfigurationRepository;
    private TransactionRepository transactionRepository;
    private FlightMovementRepository flightMovementRepository;
    private SystemConfigurationService systemConfigurationService;
    private CountryRepository countryRepository;

    @Test
    public void getAllSystemConfigurations() throws Exception {
        List<SystemConfiguration> systemConfigurations = Collections.singletonList(new SystemConfiguration());

        when(systemConfigurationRepository.findSystemConfigurationsByOrderByItemClassAscItemNameAsc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(systemConfigurations));

        Page<SystemConfiguration> results = systemConfigurationService.findSystemConfigurations(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(systemConfigurations.size());
    }

    @Test
    public void getSystemConfigurationById() throws Exception {
        SystemConfiguration systemConfiguration = new SystemConfiguration();
        systemConfiguration.setId(1);

        when(systemConfigurationRepository.getOne(any())).thenReturn(systemConfiguration);

        SystemConfiguration result = systemConfigurationService.getOne(1);
        assertThat(result).isEqualTo(systemConfiguration);
    }

    @Before
    public void setup() {
        systemConfigurationRepository = mock(SystemConfigurationRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        flightMovementRepository = mock(FlightMovementRepository.class);
        this.countryRepository = mock(CountryRepository.class);
        systemConfigurationService = new SystemConfigurationService(systemConfigurationRepository, transactionRepository,
            flightMovementRepository, countryRepository);
    }

    @Test
    public void updateSystemConfiguration() throws Exception {
        Collection<SystemConfigurationViewModel> systemConfigurations = new ArrayList<SystemConfigurationViewModel>();
        SystemConfiguration existingSystemConfiguration = new SystemConfiguration();
        existingSystemConfiguration.setCurrentValue("value");
        existingSystemConfiguration.setItemName("item name");


        SystemConfigurationViewModel systemConfiguration = new SystemConfigurationViewModel();
        systemConfiguration.setCurrentValue("new value");
        systemConfiguration.setItemName("new name");
        systemConfigurations.add(systemConfiguration);

        when(systemConfigurationRepository.getOne(any())).thenReturn(existingSystemConfiguration);

        when(systemConfigurationRepository.save(any(SystemConfiguration.class)))
                .thenReturn(existingSystemConfiguration);

        Collection<SystemConfiguration> result = systemConfigurationService.update(systemConfigurations);
        for (SystemConfiguration  r : result) {
            assertThat(r.getCurrentValue()).isEqualTo("new value");
        }

    }
}
