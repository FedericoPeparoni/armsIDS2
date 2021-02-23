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

import ca.ids.abms.modules.system.SystemDataType;
import ca.ids.abms.modules.system.SystemDataTypeRepository;
import ca.ids.abms.modules.system.SystemDataTypeService;

public class SystemDataTypeServiceTest {
    private SystemDataTypeRepository systemDataTypeRepository;
    private SystemDataTypeService systemDataTypeService;

    @Test
    public void getAllSystemDataTypes() throws Exception {
        List<SystemDataType> systemDataTypes = Collections.singletonList(new SystemDataType());

        when(systemDataTypeRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(systemDataTypes));

        Page<SystemDataType> results = systemDataTypeService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(systemDataTypes.size());
    }

    @Before
    public void setup() {
        systemDataTypeRepository = mock(SystemDataTypeRepository.class);
        systemDataTypeService = new SystemDataTypeService(systemDataTypeRepository);
    }
}
