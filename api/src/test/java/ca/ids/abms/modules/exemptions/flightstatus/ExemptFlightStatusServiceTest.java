package ca.ids.abms.modules.exemptions.flightstatus;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.common.enumerators.FlightItemType;
import org.junit.Assert;
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

public class ExemptFlightStatusServiceTest {

    private ExemptFlightStatusService exemptFlightStatusService;
    private ExemptFlightStatusRepository exemptFlightStatusRepository;

    @Before
    public void setup() {
        this.exemptFlightStatusRepository = mock(ExemptFlightStatusRepository.class);
        this.exemptFlightStatusService = new ExemptFlightStatusService(exemptFlightStatusRepository);
    }

    @Test
    public void create() {
        ExemptFlightStatus item = new ExemptFlightStatus();
        item.setEnrouteFeesAreExempt(0.0);
        item.setFlightNotes("notes");

        when(exemptFlightStatusRepository.saveAndFlush(any(ExemptFlightStatus.class))).thenReturn(item);

        ExemptFlightStatus result = exemptFlightStatusService.create(item);
        Assert.assertEquals(result.getEnrouteFeesAreExempt(), item.getEnrouteFeesAreExempt());
    }

    @Test
    public void update() {
        ExemptFlightStatus item = new ExemptFlightStatus();
        item.setId(3);
        item.setEnrouteFeesAreExempt(0.0);
        item.setFlightNotes("notes");
        item.setFlightItemType(FlightItemType.ITEM8_TYPE);
        item.setFlightItemValue("test");

        when(exemptFlightStatusRepository.getOne(any(Integer.class))).thenReturn(item);

        ExemptFlightStatus itemUpdated = new ExemptFlightStatus();
        itemUpdated.setId(3);
        itemUpdated.setEnrouteFeesAreExempt(0.0);
        itemUpdated.setFlightItemType(FlightItemType.ITEM8_TYPE);
        itemUpdated.setFlightItemValue("test");


        when(exemptFlightStatusRepository.saveAndFlush(any(ExemptFlightStatus.class)))
            .thenReturn(itemUpdated);

        ExemptFlightStatus result = exemptFlightStatusService.update(3, itemUpdated);
        Assert.assertEquals(result.getId(), itemUpdated.getId());
        Assert.assertEquals(result.getEnrouteFeesAreExempt(), itemUpdated.getEnrouteFeesAreExempt());
    }

    @Test
    public void findAll() {
        ExemptFlightStatus item = new ExemptFlightStatus();
        item.setId(3);

        List<ExemptFlightStatus> items = Collections.singletonList(item);

        when(exemptFlightStatusRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        Page<ExemptFlightStatus> results = exemptFlightStatusService.findAll(mock(Pageable.class), "");
        Assert.assertEquals(items.size(), results.getTotalElements());
    }

    @Test
    public void getOne() {
        ExemptFlightStatus item = new ExemptFlightStatus();
        item.setId(3);

        when(exemptFlightStatusRepository.getOne(any())).thenReturn(item);

        ExemptFlightStatus result = exemptFlightStatusService.getOne(3);
        Assert.assertEquals(item.getId(), result.getId());
    }

    @Test
    public void delete() {
        ExemptFlightStatus item = new ExemptFlightStatus();
        item.setId(3);
        when(exemptFlightStatusRepository.getOne(any()))
            .thenReturn(item);
        exemptFlightStatusService.delete(3);
        verify(exemptFlightStatusRepository).delete(any(Integer.class));
    }

}
