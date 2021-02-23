package ca.ids.abms.modules.aircraft;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.exemptions.aircrafttype.AircraftTypeExemptionRepository;
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

public class AircraftTypeServiceTest {
    private AircraftTypeRepository aircraftTypeRepository;
    private AircraftTypeService aircraftTypeService;
    private AircraftTypeExemptionRepository aircraftTypeExemptionRepository;
    private AircraftRegistrationRepository aircraftRegistrationRepository;

    @Test
    public void createAircraftType() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setAircraftName("name");
        aircraftType.setAircraftType("A101");

        when(aircraftTypeRepository.save(any(AircraftType.class)))
        .thenReturn(aircraftType);

        AircraftType result = aircraftTypeService.save(aircraftType);
        assertThat(result.getAircraftName()).isEqualTo(aircraftType.getAircraftName());
    }

    @Test
    public void deleteAircraftType() throws Exception {

        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        when(aircraftTypeRepository.findById(any()))
            .thenReturn(aircraftType);

        aircraftTypeService.delete(1);
        verify(aircraftTypeRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllAircraftTypes() throws Exception {
        List<AircraftType> aircraftTypes = Collections.singletonList(new AircraftType());

        when(aircraftTypeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(aircraftTypes));

        Page<AircraftType> results = aircraftTypeService.findAll(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(aircraftTypes.size());
    }

    @Test
    public void getAircraftTypeById() throws Exception {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(1);

        when(aircraftTypeRepository.getOne(any()))
        .thenReturn(aircraftType);

        AircraftType result = aircraftTypeService.getOne(1);
        assertThat(result).isEqualTo(aircraftType);
    }

    @Before
    public void setup() {
        aircraftTypeRepository = mock(AircraftTypeRepository.class);
        aircraftRegistrationRepository = mock(AircraftRegistrationRepository.class);
        aircraftTypeExemptionRepository = mock(AircraftTypeExemptionRepository.class);
        aircraftTypeService = new AircraftTypeService(aircraftTypeRepository, aircraftRegistrationRepository, aircraftTypeExemptionRepository);
    }

    @Test
    public void updateAircraftType() throws Exception {
        AircraftType existingAircraftType = new AircraftType();
        existingAircraftType.setAircraftName("name");
        existingAircraftType.setAircraftType("A101");

        AircraftType aircraftType = new AircraftType();
        aircraftType.setAircraftName("new name");
        aircraftType.setAircraftType("A102");

        when(aircraftTypeRepository.getOne(any()))
        .thenReturn(existingAircraftType);

        when(aircraftTypeRepository.save(any(AircraftType.class)))
        .thenReturn(existingAircraftType);

        AircraftType result = aircraftTypeService.update(1, aircraftType);

        assertThat(result.getAircraftName()).isEqualTo("new name");
    }
}
