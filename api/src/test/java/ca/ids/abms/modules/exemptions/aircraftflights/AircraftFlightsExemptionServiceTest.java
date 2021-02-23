package ca.ids.abms.modules.exemptions.aircraftflights;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AircraftFlightsExemptionServiceTest {

    private AircraftFlightsExemptionService aircraftFlightsExemptionService;
    private AircraftFlightsExemptionRepository aircraftFlightsExemptionRepository;

    @Before
    public void setup() {
        this.aircraftFlightsExemptionRepository = mock(AircraftFlightsExemptionRepository.class);
        this.aircraftFlightsExemptionService = new AircraftFlightsExemptionService(aircraftFlightsExemptionRepository);
    }

    @Test
    public void create() {
        AircraftFlightsExemption item = new AircraftFlightsExemption();
        item.setAircraftRegistration("X1234");
        item.setExemptionStartDate(LocalDateTime.now());
        item.setExemptionEndDate(LocalDateTime.now());
        item.setFlightNotes("notes");

        when(aircraftFlightsExemptionRepository.saveAndFlush(any(AircraftFlightsExemption.class))).thenReturn(item);

        AircraftFlightsExemption result = aircraftFlightsExemptionService.create(item);
        Assert.assertEquals(result.getAircraftRegistration(), item.getAircraftRegistration());
    }

    @Test
    public void update() {
        AircraftFlightsExemption item = new AircraftFlightsExemption();
        item.setId(3);
        item.setEnrouteFeesExempt(0.0);
        item.setExemptionStartDate(LocalDateTime.now());
        item.setExemptionEndDate(LocalDateTime.now());
        item.setFlightNotes("notes");
        item.setAircraftRegistration("X1234");

        when(aircraftFlightsExemptionRepository.getOne(any(Integer.class))).thenReturn(item);

        AircraftFlightsExemption itemUpdated = new AircraftFlightsExemption();
        itemUpdated.setId(3);
        itemUpdated.setEnrouteFeesExempt(100.0);
        itemUpdated.setExemptionStartDate(LocalDateTime.now());
        itemUpdated.setExemptionEndDate(LocalDateTime.now());
        itemUpdated.setFlightNotes("notes");
        itemUpdated.setAircraftRegistration("X1234");

        when(aircraftFlightsExemptionRepository.saveAndFlush(any(AircraftFlightsExemption.class)))
            .thenReturn(itemUpdated);

        AircraftFlightsExemption result = aircraftFlightsExemptionService.update(3, itemUpdated);
        Assert.assertEquals(result.getId(), item.getId());
        Assert.assertEquals(result.getEnrouteFeesExempt(), itemUpdated.getEnrouteFeesExempt());
    }

    @Test
    public void findAll() {
        AircraftFlightsExemption item = new AircraftFlightsExemption();
        item.setId(3);

        List<AircraftFlightsExemption> items = Collections.singletonList(item);

        when(aircraftFlightsExemptionRepository.findAll(Matchers.<Specification<AircraftFlightsExemption>>any(), any(Pageable.class))).thenReturn(new PageImpl<>(items));

        Page<AircraftFlightsExemption> results = aircraftFlightsExemptionService.findAll(mock(Pageable.class),null);
        Assert.assertEquals(items.size(), results.getTotalElements());
    }

    @Test
    public void getOne() {
        AircraftFlightsExemption item = new AircraftFlightsExemption();
        item.setId(3);

        when(aircraftFlightsExemptionRepository.getOne(any())).thenReturn(item);

        AircraftFlightsExemption result = aircraftFlightsExemptionService.getOne(3);
    }

    @Test
    public void delete() {
        AircraftFlightsExemption item = new AircraftFlightsExemption();
        item.setId(3);
        when(aircraftFlightsExemptionRepository.getOne(any()))
            .thenReturn(item);
        aircraftFlightsExemptionService.delete(3);
        verify(aircraftFlightsExemptionRepository).delete(any(Integer.class));
    }

}
