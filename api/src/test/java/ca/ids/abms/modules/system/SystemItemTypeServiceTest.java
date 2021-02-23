package ca.ids.abms.modules.system;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.system.SystemItemType;
import ca.ids.abms.modules.system.SystemItemTypeRepository;
import ca.ids.abms.modules.system.SystemItemTypeService;

public class SystemItemTypeServiceTest {
    private SystemItemTypeRepository systemItemTypeRepository;
    private SystemItemTypeService systemItemTypeService;

    @Test
    public void getAllSystemItemTypes() throws Exception {
        List<SystemItemType> systemItemTypes = Collections.singletonList(new SystemItemType());

        when(systemItemTypeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(systemItemTypes));

        Page<SystemItemType> results = systemItemTypeService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(systemItemTypes.size());
    }

    @Before
    public void setup() {
        systemItemTypeRepository = mock(SystemItemTypeRepository.class);
        systemItemTypeService = new SystemItemTypeService(systemItemTypeRepository);
    }
}
