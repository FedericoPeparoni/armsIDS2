package ca.ids.abms.modules.aerodrome;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.formulas.ldp.LdpBillingFormulaRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.aerodromes.AerodromeCategory;
import ca.ids.abms.modules.aerodromes.AerodromeCategoryRepository;
import ca.ids.abms.modules.aerodromes.AerodromeCategoryService;

public class AerodromeCategoryServiceTest {

    private AerodromeCategoryRepository aerodromeCategoryRepository;

    private AerodromeCategoryService aerodromeCategoryService;

    @Before
    public void setup() {
        aerodromeCategoryRepository = mock(AerodromeCategoryRepository.class);
        aerodromeCategoryService = new AerodromeCategoryService(
            aerodromeCategoryRepository, mock(LdpBillingFormulaRepository.class));
    }

    @Test
    public void createAerodromeCategory() {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setCategoryName("name");

        when(aerodromeCategoryRepository.save(any(AerodromeCategory.class))).thenReturn(aerodromeCategory);

        AerodromeCategory result = aerodromeCategoryService.createAerodromeCategory(aerodromeCategory);
        assertThat(result.getCategoryName()).isEqualTo(aerodromeCategory.getCategoryName());
    }

    @Test
    public void deleteAerodromeCategory() {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setId(1);
        when(aerodromeCategoryRepository.getOne(any())).thenReturn(aerodromeCategory);

        aerodromeCategoryService.delete(1);
        verify(aerodromeCategoryRepository).delete(any(Integer.class));
    }

    @Test
    public void getAerodromeCategoryById() {
        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setId(1);

        when(aerodromeCategoryRepository.getOne(any())).thenReturn(aerodromeCategory);

        AerodromeCategory result = aerodromeCategoryService.getOne(1);
        assertThat(result).isEqualTo(aerodromeCategory);
    }

    @Test
    public void getAllAerodromecategories() {
        List<AerodromeCategory> aerodromes = Collections.singletonList(new AerodromeCategory());

        when(aerodromeCategoryRepository.findAll(any(FiltersSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(aerodromes));

        Page<AerodromeCategory> results = aerodromeCategoryService.findAll("", mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(aerodromes.size());
    }

    @Test
    public void updateAerodromeCategory() {
        AerodromeCategory existingAerodrome = new AerodromeCategory();
        existingAerodrome.setCategoryName("name");

        AerodromeCategory aerodromeCategory = new AerodromeCategory();
        aerodromeCategory.setCategoryName("new name");

        when(aerodromeCategoryRepository.getOne(any())).thenReturn(existingAerodrome);

        when(aerodromeCategoryRepository.save(any(AerodromeCategory.class))).thenReturn(existingAerodrome);

        AerodromeCategory result = aerodromeCategoryService.update(1, aerodromeCategory);

        assertThat(result.getCategoryName()).isEqualTo("new name");
    }

}
