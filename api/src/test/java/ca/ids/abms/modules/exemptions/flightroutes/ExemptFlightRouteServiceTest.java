package ca.ids.abms.modules.exemptions.flightroutes;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.unspecified.UnspecifiedDepartureDestinationLocationService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class ExemptFlightRouteServiceTest {

    private ExemptFlightRouteService exemptFlightRouteService;
    private ExemptFlightRouteRepository exemptFlightRouteRepository;
    private AerodromeService aerodromeService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {
        this.exemptFlightRouteRepository = mock(ExemptFlightRouteRepository.class);
        this.aerodromeService=mock(AerodromeService.class);
        this.systemConfigurationService = mock(SystemConfigurationService.class);
        UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService = mock(UnspecifiedDepartureDestinationLocationService.class);
        this.exemptFlightRouteService = new ExemptFlightRouteService(exemptFlightRouteRepository,aerodromeService, unspecifiedDepartureDestinationLocationService, systemConfigurationService);
    }

    @Test
    public void create() {
        ExemptFlightRoute item = new ExemptFlightRoute();
        item.setDepartureAerodrome("NAP");
        item.setDestinationAerodrome("YOW");
        item.setLateDepartureFeesAreExempt(100.0);
        item.setEnrouteFeesAreExempt(0.0);
        item.setExemptionInEitherDirection(true);
        item.setFlightNotes("notes");
        when(exemptFlightRouteRepository.save(any(ExemptFlightRoute.class))).thenReturn(item);
        when(aerodromeService.checkAerodromeIdentifier(any(), any(boolean.class), any(boolean.class))).thenReturn(item.getDepartureAerodrome());
        ExemptFlightRoute result = exemptFlightRouteService.create(item);
        Assert.assertEquals(result.getDepartureAerodrome(), item.getDepartureAerodrome());
        Assert.assertEquals(result.getDestinationAerodrome(), item.getDestinationAerodrome());
        Assert.assertEquals(result.getLateDepartureFeesAreExempt(), item.getLateDepartureFeesAreExempt());
    }

    @Test
    public void update() {
        ExemptFlightRoute item = new ExemptFlightRoute();
        item.setId(3);
        item.setDepartureAerodrome("NAP");
        item.setDestinationAerodrome("YOW");
        item.setLateDepartureFeesAreExempt(100.0);
        item.setEnrouteFeesAreExempt(0.0);
        item.setExemptionInEitherDirection(true);
        item.setFlightNotes("notes");
        when(aerodromeService.checkAerodromeIdentifier(any(), any(boolean.class), any(boolean.class))).thenReturn(item.getDepartureAerodrome());
        when(exemptFlightRouteRepository.save(any(ExemptFlightRoute.class))).thenReturn(item);
        when(exemptFlightRouteRepository.getOne(any(Integer.class))).thenReturn(item);

        ExemptFlightRoute itemUpdated = new ExemptFlightRoute();
        itemUpdated.setId(3);
        itemUpdated.setDepartureAerodrome("YOW");
        itemUpdated.setDestinationAerodrome("NAP");
        itemUpdated.setLateDepartureFeesAreExempt(0.0);

        when(exemptFlightRouteRepository.saveAndFlush(any(ExemptFlightRoute.class)))
            .thenReturn(itemUpdated);

        ExemptFlightRoute result = exemptFlightRouteService.update(3, itemUpdated);
        Assert.assertEquals(item.getId(), result.getId());
        Assert.assertEquals("YOW", result.getDepartureAerodrome());
        Assert.assertEquals("NAP", result.getDestinationAerodrome());
        Assert.assertEquals((Double) 0.0, result.getLateDepartureFeesAreExempt());
    }

    @Test
    public void findAll() {
        ExemptFlightRoute item = new ExemptFlightRoute();
        item.setId(3);
        item.setDepartureAerodrome("NAP");
        item.setDestinationAerodrome("YOW");

        List<ExemptFlightRoute> items = Collections.singletonList(item);

        when(exemptFlightRouteRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(items));

        Page<ExemptFlightRoute> results = exemptFlightRouteService.findAll(mock(Pageable.class), "");
        Assert.assertEquals(items.size(), results.getTotalElements());
        Assert.assertEquals(items.get(0).getDepartureAerodrome(), results.getContent().get(0).getDepartureAerodrome());
        Assert.assertEquals(items.get(0).getDestinationAerodrome(), results.getContent().get(0).getDestinationAerodrome());
    }

    @Test
    public void getOne() {
        ExemptFlightRoute item = new ExemptFlightRoute();
        item.setId(3);
        item.setDepartureAerodrome("NAP");
        item.setDestinationAerodrome("YOW");

        when(exemptFlightRouteRepository.getOne(any())).thenReturn(item);

        ExemptFlightRoute result = exemptFlightRouteService.getOne(3);
        Assert.assertEquals("NAP", result.getDepartureAerodrome());
        Assert.assertEquals("YOW", result.getDestinationAerodrome());
    }

    @Test
    public void delete() {
        ExemptFlightRoute item = new ExemptFlightRoute();
        item.setId(3);
        when(exemptFlightRouteRepository.getOne(any()))
            .thenReturn(item);
        exemptFlightRouteService.delete(3);
        verify(exemptFlightRouteRepository).delete(any(Integer.class));
    }

}
