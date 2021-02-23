package ca.ids.abms.modules.aerodromeserviceoutages;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceType;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeKey;
import ca.ids.abms.modules.aerodromeservicetypes.AerodromeServiceTypeMap;
import ca.ids.abms.modules.aerodromeservicetypes.DiscountType;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AerodromeServiceOutageServiceTest {
    private AerodromeServiceOutageService aerodromeServiceOutageService;
    private AerodromeServiceOutageRepository aerodromeServiceOutageRepository;

    @Before
    public void setup() {
        aerodromeServiceOutageRepository = mock(AerodromeServiceOutageRepository.class);
        aerodromeServiceOutageService = new AerodromeServiceOutageService(aerodromeServiceOutageRepository);
    }

    @Test
    public void createAerodromeServiceOutage() {
        AerodromeServiceOutage aerodromeServiceOutage = new AerodromeServiceOutage();

        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setId(1);

        AerodromeServiceType aerodromeServiceType = new AerodromeServiceType();
        aerodromeServiceType.setId(2);

        AerodromeServiceTypeKey aerodromeServiceTypeKey = new AerodromeServiceTypeKey(aerodrome, aerodromeServiceType);

        AerodromeServiceTypeMap aerodromeServiceTypeMap = new AerodromeServiceTypeMap();
        aerodromeServiceTypeMap.setId(aerodromeServiceTypeKey);

        aerodromeServiceOutage.setAerodromeServiceTypeMap(aerodromeServiceTypeMap);
        aerodromeServiceOutage.setStartDateTime(LocalDateTime.MIN);
        aerodromeServiceOutage.setEndDateTime(LocalDateTime.MAX);
        aerodromeServiceOutage.setAerodromeDiscountAmount(50.00);
        aerodromeServiceOutage.setApproachDiscountAmount(50.00);
        aerodromeServiceOutage.setAerodromeDiscountType(DiscountType.fixed);
        aerodromeServiceOutage.setApproachDiscountType(DiscountType.percentage);
        aerodromeServiceOutage.setFlightNotes("Notes");

        when(aerodromeServiceOutageRepository.save(any(AerodromeServiceOutage.class)))
            .thenReturn(aerodromeServiceOutage);

        AerodromeServiceOutage result = aerodromeServiceOutageService.save(aerodromeServiceOutage);
        assertThat(result.getAerodromeServiceTypeMap().getId().getAerodrome().getId()).isEqualTo(1);
        assertThat(result.getAerodromeServiceTypeMap().getId().getAerodromeServiceType().getId()).isEqualTo(2);
    }

    @Test
    public void deleteAerodromeServiceOutage() {
        aerodromeServiceOutageService.delete(1);
        verify(aerodromeServiceOutageRepository).delete(any(Integer.class));
    }

    @Test
    public void getAerodromeServiceOutageById() {
        AerodromeServiceOutage aerodromeServiceOutage = new AerodromeServiceOutage();
        aerodromeServiceOutage.setId(1);

        when(aerodromeServiceOutageRepository.getOne(any()))
            .thenReturn(aerodromeServiceOutage);

        AerodromeServiceOutage result = aerodromeServiceOutageService.getOne(1);
        assertThat(result).isEqualTo(aerodromeServiceOutage);
    }

    @Test
    public void getAllAerodromeServiceOutages() {
        List<AerodromeServiceOutage> outages = Collections.singletonList(new AerodromeServiceOutage());

        when(aerodromeServiceOutageRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(outages));

        Page<AerodromeServiceOutage> results = aerodromeServiceOutageService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(outages.size());
    }

    @Test
    public void updateAerodromeServiceOutage() {
        AerodromeServiceOutage existingOutage = new AerodromeServiceOutage();

        Aerodrome aerodrome = new Aerodrome();

        AerodromeServiceType aerodromeServiceType = new AerodromeServiceType();

        AerodromeServiceTypeKey aerodromeServiceTypeKey = new AerodromeServiceTypeKey(aerodrome, aerodromeServiceType);

        AerodromeServiceTypeMap aerodromeServiceTypeMap = new AerodromeServiceTypeMap();
        aerodromeServiceTypeMap.setId(aerodromeServiceTypeKey);

        existingOutage.setAerodromeServiceTypeMap(aerodromeServiceTypeMap);
        existingOutage.setStartDateTime(LocalDateTime.MIN);
        existingOutage.setEndDateTime(LocalDateTime.MAX);
        existingOutage.setAerodromeDiscountAmount(50.00);
        existingOutage.setApproachDiscountAmount(50.00);
        existingOutage.setAerodromeDiscountType(DiscountType.fixed);
        existingOutage.setApproachDiscountType(DiscountType.percentage);
        existingOutage.setFlightNotes("Notes");

        when(aerodromeServiceOutageRepository.save(any(AerodromeServiceOutage.class)))
            .thenReturn(existingOutage);

        AerodromeServiceOutage newOutage = existingOutage;
        newOutage.setFlightNotes("new Notes");

        AerodromeServiceOutage result = aerodromeServiceOutageService.update(1, newOutage);
        assertThat(result.getFlightNotes()).isEqualTo("new Notes");
    }
}
