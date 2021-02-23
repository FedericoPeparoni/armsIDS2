package ca.ids.abms.modules.waketurbulence;

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

import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategory;
import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategoryRepository;
import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategoryService;

public class WakeTurbulenceCategoryServiceTest {
    private WakeTurbulenceCategoryRepository wakeTurbulenceCategoryRepository;
    private WakeTurbulenceCategoryService wakeTurbulenceCategoryService;

    @Test
    public void getAllWakeTurbulenceCategorys() throws Exception {
        List<WakeTurbulenceCategory> wakeTurbulenceCategories = Collections.singletonList(new WakeTurbulenceCategory());

        when(wakeTurbulenceCategoryRepository.findAll(any(Pageable.class)))
        .thenReturn(new PageImpl<>(wakeTurbulenceCategories));

        Page<WakeTurbulenceCategory> results = wakeTurbulenceCategoryService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(wakeTurbulenceCategories.size());
    }

    @Before
    public void setup() {
        wakeTurbulenceCategoryRepository = mock(WakeTurbulenceCategoryRepository.class);
        wakeTurbulenceCategoryService = new WakeTurbulenceCategoryService(wakeTurbulenceCategoryRepository);
    }
}
