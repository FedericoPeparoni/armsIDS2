package ca.ids.abms.modules.amhsconfiguration;

import ca.ids.abms.config.db.FiltersSpecification;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class AmhsConfigurationServiceTest {

    private AmhsConfigurationRepository amhsConfigurationRepository;
    private AmhsConfigurationService amhsConfigurationService;
    private AmhsConfigurationValidator amhsConfigurationValidator;
    private AmhsAgentConfigService amhsAgentConfigService;

    @Before
    public void setup() {
        amhsConfigurationRepository = mock(AmhsConfigurationRepository.class);
        amhsConfigurationValidator = mock(AmhsConfigurationValidator.class);
        amhsAgentConfigService = mock(AmhsAgentConfigService.class);
        amhsConfigurationService = new AmhsConfigurationService (
                amhsConfigurationRepository, amhsConfigurationValidator, amhsAgentConfigService);
    }

    @Test
    public void getAmhsConfigurationById() throws Exception {
        AmhsConfiguration amhsconfiguration = new AmhsConfiguration();
        amhsconfiguration.setId(1);

        when(amhsConfigurationRepository.getOne(any())).thenReturn(amhsconfiguration);

        AmhsConfiguration result = amhsConfigurationRepository.getOne(1);
        assertThat(result).isEqualTo(amhsconfiguration);
    }

    @Test
    public void getAllAmhsConfigurations() {
        List<AmhsConfiguration> amhsConfigs = Collections.singletonList(new AmhsConfiguration());

        when(amhsConfigurationRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(amhsConfigs));

        Page<AmhsConfiguration> results = amhsConfigurationService.findAll(null, null);

        assertThat(results.getTotalElements()).isEqualTo(amhsConfigs.size());
    }
/*
    @Test
    public void createAmhsConfiguration() {
        AmhsConfiguration amhsConfiguration = new AmhsConfiguration();
        amhsConfiguration.setChannelName("Channel Name");
        amhsConfiguration.setId(1);
        amhsConfiguration.setAssociations((double) 10);
        amhsConfiguration.setBindType("AUTHENTICATION");
        amhsConfiguration.setChannelType("INCOMING");

        List<String> existingAmhsConfigs = new ArrayList<>();
        existingAmhsConfigs.add("test");

        when(amhsConfigurationRepository.save(any(AmhsConfiguration.class)))
            .thenReturn(amhsConfiguration);

        AmhsConfiguration result = amhsConfigurationService.create(amhsConfiguration);
        assertThat(result.getChannelName()).isEqualTo(amhsConfiguration.getChannelName());
    }

    @Test
    public void updateAmhsConfiguration() {
        AmhsConfiguration existingConfig = new AmhsConfiguration();
        existingConfig.setId(1);
        existingConfig.setChannelName("name");

        AmhsConfiguration amhsConfiguration = new AmhsConfiguration();
        amhsConfiguration.setChannelName("new name");

        when(amhsConfigurationRepository.getOne(any()))
            .thenReturn(existingConfig);

        when(amhsConfigurationRepository.save(any(AmhsConfiguration.class)))
            .thenReturn(amhsConfiguration);

        AmhsConfiguration result = amhsConfigurationService.update(1, amhsConfiguration);

        assertThat(result.getChannelName()).isEqualTo("new name");
    }

    @Test
    public void deleteAmhsConfiguration() {
        AmhsConfiguration amhsconfiguration = new AmhsConfiguration();
        amhsconfiguration.setId(1);

        when(amhsConfigurationRepository.getOne(any())).thenReturn(amhsconfiguration);

        amhsConfigurationService.remove(1);
        verify(amhsConfigurationRepository).delete(any(Integer.class));
    }
    */
}
