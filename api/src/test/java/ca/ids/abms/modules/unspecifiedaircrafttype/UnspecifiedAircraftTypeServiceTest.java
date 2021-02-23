package ca.ids.abms.modules.unspecifiedaircrafttype;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftType;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeRepository;
import ca.ids.abms.modules.unspecifiedaircraft.UnspecifiedAircraftTypeService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class UnspecifiedAircraftTypeServiceTest {

    private UnspecifiedAircraftTypeRepository unspecifiedAircraftTypeRepository;
    private UnspecifiedAircraftTypeService unspecifiedAircraftTypeService;

    @Before
    public void setup() {
        unspecifiedAircraftTypeRepository = mock(UnspecifiedAircraftTypeRepository.class);
        unspecifiedAircraftTypeService = new UnspecifiedAircraftTypeService(unspecifiedAircraftTypeRepository);
    }

    @Test
    public void createUnspecifiedAircraftType() {
        UnspecifiedAircraftType unspecifiedAircraftType = new UnspecifiedAircraftType();
        unspecifiedAircraftType.setTextIdentifier("text identifier");

        when(unspecifiedAircraftTypeRepository.save(any(UnspecifiedAircraftType.class)))
        .thenReturn(unspecifiedAircraftType);

        UnspecifiedAircraftType result = unspecifiedAircraftTypeService.create(unspecifiedAircraftType);
        assertThat(result.getTextIdentifier()).isEqualTo(unspecifiedAircraftType.getTextIdentifier());
    }

    @Test
    public void getAllUnspecifiedAircraftTypes() {

        List<UnspecifiedAircraftType> unspecifiedAircraftTypes = Collections.singletonList(new UnspecifiedAircraftType());

        // noinspection unchecked
        when(unspecifiedAircraftTypeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class))).thenReturn(new PageImpl<>(unspecifiedAircraftTypes));

        Page<UnspecifiedAircraftType> results = unspecifiedAircraftTypeService.findAll("test", mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(unspecifiedAircraftTypes.size());
    }

    @Test
    public void getUnspecifiedAircraftTypeById() {
        UnspecifiedAircraftType unspecifiedAircraftType = new UnspecifiedAircraftType();
        unspecifiedAircraftType.setId(1);

        when(unspecifiedAircraftTypeRepository.findOne(1)).thenReturn(unspecifiedAircraftType);

        UnspecifiedAircraftType result = unspecifiedAircraftTypeService.findOne(1);
        assertThat(result).isEqualTo(unspecifiedAircraftType);
    }

    @Test
    public void updateUnspecifiedAircraftType() {
        UnspecifiedAircraftType existingUnspecifiedAircraftType = new UnspecifiedAircraftType();
        existingUnspecifiedAircraftType.setTextIdentifier("text identifier");

        UnspecifiedAircraftType updateUnspecifiedAircraftType = new UnspecifiedAircraftType();
        updateUnspecifiedAircraftType.setTextIdentifier("text identifier update");

        when(unspecifiedAircraftTypeRepository.getOne(1)).thenReturn(existingUnspecifiedAircraftType);

        when(unspecifiedAircraftTypeRepository.save(any(UnspecifiedAircraftType.class))).thenReturn(existingUnspecifiedAircraftType);

        UnspecifiedAircraftType result = unspecifiedAircraftTypeService.update(1, updateUnspecifiedAircraftType);

        assertThat(result.getTextIdentifier()).isEqualTo("text identifier update");
    }

    @Test
    public void deleteUnspecifiedAircraftType() {
        unspecifiedAircraftTypeService.delete(1);
        verify(unspecifiedAircraftTypeRepository).delete(any(Integer.class));
    }
}
