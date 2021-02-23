package ca.ids.abms.modules.plugins;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Java6Assertions.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.persistence.EntityNotFoundException;

import ca.ids.abms.modules.system.SystemConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;

@SuppressWarnings("unchecked")
public class PluginServiceTest {

    private Page page;
    private PluginRepository pluginRepository;
    private PluginService pluginService;

    private static final String ERR_UPDATE_NO_LONGER_EXISTS = "The record being edited no longer exists";

    @Before
    public void setup() {
        page = mock(Page.class);

        pluginRepository = mock(PluginRepository.class);
        when(pluginRepository.findAll(any(FiltersSpecification.class), any(Pageable.class))).thenReturn(page);
        when(pluginRepository.getOne(anyInt())).thenReturn(PluginTest.getPlugin());
        when(pluginRepository.save(any(Plugin.class))).thenReturn(PluginTest.getPlugin());

        SystemConfigurationService systemConfigurationService = mock(SystemConfigurationService.class);
        when(systemConfigurationService.getValue(any())).thenReturn("TEST");

        pluginService = new PluginService(pluginRepository, systemConfigurationService);
    }

    @Test
    public void findAllTest() {
        Page result = pluginService.findAll("mock search", null);
        assertThat(result).isEqualTo(page);

        result = pluginService.findAll("mock search", null, null);
        assertThat(result).isEqualTo(page);

        result = pluginService.findAll("mock search", null, true);
        assertThat(result).isEqualTo(page);

        result = pluginService.findAll("mock search", null, false);
        assertThat(result).isEqualTo(page);

        verify(pluginRepository, times(4)).findAll(any(FiltersSpecification.class), any(Pageable.class));
    }

    @Test
    public void updateTest() {
        Plugin result = pluginService.update(1, PluginTest.getPlugin());

        assertThat(result).isEqualTo(PluginTest.getPlugin());

        verify(pluginRepository, times(1)).getOne(1);
        verify(pluginRepository, times(1)).save(any(Plugin.class));
    }

    @Test
    public void updateNoLongerExistsTest() {
        // force plugin repository to throw a runtime exception
        when(pluginRepository.getOne(1)).thenThrow(new EntityNotFoundException());

        // run update and expect it to throw exception
        try {
            pluginService.update(1, PluginTest.getPlugin());
            fail("Expected " + CustomParametrizedException.class + " thrown with description : "
                    + ERR_UPDATE_NO_LONGER_EXISTS);
        } catch (CustomParametrizedException ex) {
            assertThat(ERR_UPDATE_NO_LONGER_EXISTS);
        }

        // verify that necessary dependency injection methods have been run appropriate amount of times
        verify(pluginRepository, times(1)).getOne(1);
        verify(pluginRepository, times(0)).save(any(Plugin.class));
    }
}
