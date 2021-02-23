package ca.ids.abms.modules.exemptions.aircrafttype;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeRepository;

public class AircraftTypeExemptionServiceTest {

    private AircraftTypeExemptionRepository aircraftTypeExemptionRepository;
    private AircraftTypeExemptionService aircraftTypeExemptionService;
    private AircraftTypeRepository aircraftTypeRepository;

    @Test
    public void createItem() {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setAircraftType("A123");
        AircraftTypeExemption aircraftTypeExemption = new AircraftTypeExemption();
        aircraftTypeExemption.setId(1);
        aircraftTypeExemption.setAircraftType(aircraftType);
        when(aircraftTypeExemptionRepository.save(any(AircraftTypeExemption.class))).thenReturn(aircraftTypeExemption);
        when(aircraftTypeRepository.findByAircraftType(any())).thenReturn(aircraftType);

        AircraftTypeExemption result = aircraftTypeExemptionService.create(aircraftTypeExemption, "A130");
        Assert.assertEquals(result.getId(), aircraftTypeExemption.getId());
        Assert.assertEquals(result.getAircraftType().getAircraftType(), aircraftTypeExemption.getAircraftType().getAircraftType());
    }

    @Test
    public void deleteItem() {
        aircraftTypeExemptionService.delete(1);
        verify(aircraftTypeExemptionRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllItems() {
        List<AircraftTypeExemption> aircraftTypeExemptions = Collections.singletonList(new AircraftTypeExemption());

        when(aircraftTypeExemptionRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(aircraftTypeExemptions));

        Page<AircraftTypeExemption> results = aircraftTypeExemptionService.findAll(mock(Pageable.class), null);

        Assert.assertEquals(results.getTotalElements(), aircraftTypeExemptions.size());
    }

    @Test
    public void getItemById() {
        AircraftType aircraftType = new AircraftType();
        aircraftType.setId(2);
        AircraftTypeExemption aircraftTypeExemption = new AircraftTypeExemption();
        aircraftTypeExemption.setId(1);
        aircraftTypeExemption.setAircraftType(aircraftType);

        when(aircraftTypeExemptionRepository.getOne(any())).thenReturn(aircraftTypeExemption);
        when(aircraftTypeRepository.getOne(any())).thenReturn(aircraftType);

        AircraftTypeExemption result = aircraftTypeExemptionService.getOne(1);
        Assert.assertEquals(result, aircraftTypeExemption);
        Assert.assertEquals(result.getAircraftType().getId(), aircraftType.getId());
    }

    @Before
    public void setup() {
        aircraftTypeExemptionRepository = mock(AircraftTypeExemptionRepository.class);
        aircraftTypeRepository = mock(AircraftTypeRepository.class);
        aircraftTypeExemptionService = new AircraftTypeExemptionService(aircraftTypeExemptionRepository,
                aircraftTypeRepository);
    }

    @Test
    public void updateItem() {
        AircraftType firstAircraftType = new AircraftType();
        firstAircraftType.setAircraftType("1");
        AircraftType secondAircraftType = new AircraftType();
        secondAircraftType.setAircraftType("1");
        AircraftTypeExemption existingAircraftTypeExemption = new AircraftTypeExemption();
        existingAircraftTypeExemption.setId(0);
        existingAircraftTypeExemption.setAircraftType(firstAircraftType);

        AircraftTypeExemption aircraftTypeExemption = new AircraftTypeExemption();
        aircraftTypeExemption.setId(0);
        aircraftTypeExemption.setAircraftType(secondAircraftType);

        when(aircraftTypeExemptionRepository.getOne(any())).thenReturn(existingAircraftTypeExemption);

        when(aircraftTypeExemptionRepository.save(any(AircraftTypeExemption.class)))
                .thenReturn(existingAircraftTypeExemption);

        when(aircraftTypeRepository.findByAircraftType(any())).thenReturn(secondAircraftType);

        AircraftTypeExemption result = aircraftTypeExemptionService.update(1, aircraftTypeExemption, "A130");

        Assert.assertEquals(result.getId(), aircraftTypeExemption.getId());
        Assert.assertEquals(result.getAircraftType().getAircraftType(), aircraftTypeExemption.getAircraftType().getAircraftType());
    }
}
