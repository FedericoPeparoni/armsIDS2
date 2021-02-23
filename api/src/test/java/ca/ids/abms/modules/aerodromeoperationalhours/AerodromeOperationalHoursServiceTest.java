package ca.ids.abms.modules.aerodromeoperationalhours;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ChargesUtility;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AerodromeOperationalHoursServiceTest {

    private AerodromeOperationalHoursRepository aerodromeOperationalHoursRepository;
    private AerodromeOperationalHoursService aerodromeOperationalHoursService;
    private SystemConfigurationService systemConfigurationService;
    private FlightMovementRepository flightMovementRepository;
    private ChargesUtility chargesUtility;

    @Before
    public void setup() {
        aerodromeOperationalHoursRepository = mock(AerodromeOperationalHoursRepository.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        flightMovementRepository = mock(FlightMovementRepository.class);
        chargesUtility = mock(ChargesUtility.class);
        aerodromeOperationalHoursService = new AerodromeOperationalHoursService(aerodromeOperationalHoursRepository,
            systemConfigurationService, flightMovementRepository, chargesUtility);
    }

    @Test
    public void createAerodromeOperationalHours() {
        AerodromeOperationalHours aerodromeOperationalHours = new AerodromeOperationalHours();

        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setId(1);

        aerodromeOperationalHours.setAerodrome(aerodrome);
        aerodromeOperationalHours.setOperationalHoursFriday("0000-0200;1400-1600");
        aerodromeOperationalHours.setOperationalHoursMonday("0800-1000;1400-1600");

        when(aerodromeOperationalHoursRepository.save(any(AerodromeOperationalHours.class)))
            .thenReturn(aerodromeOperationalHours);

        AerodromeOperationalHours result = aerodromeOperationalHoursService.save(aerodromeOperationalHours);
        assertThat(result.getAerodrome().getId()).isEqualTo(1);
        assertThat(result.getOperationalHoursFriday()).isEqualTo("0000-0200;1400-1600");
        assertThat(result.getOperationalHoursMonday()).isEqualTo("0800-1000;1400-1600");
    }

    @Test
    public void deleteAerodromeOperationalHours() {
        aerodromeOperationalHoursService.delete(1);
        verify(aerodromeOperationalHoursRepository).delete(any(Integer.class));
    }

    @Test
    public void getAerodromeOperationalHoursById() {
        AerodromeOperationalHours aerodromeOperationalHours = new AerodromeOperationalHours();
        aerodromeOperationalHours.setId(1);

        when(aerodromeOperationalHoursRepository.getOne(any()))
            .thenReturn(aerodromeOperationalHours);

        AerodromeOperationalHours result = aerodromeOperationalHoursService.getOne(1);
        assertThat(result).isEqualTo(aerodromeOperationalHours);
    }

    @Test
    public void getAllAerodromeOperationalHours() {
        List<AerodromeOperationalHours> aerodromeOperationalHours = Collections.singletonList(new AerodromeOperationalHours());

        when(aerodromeOperationalHoursRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(aerodromeOperationalHours));

        Page<AerodromeOperationalHours> results = aerodromeOperationalHoursService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(aerodromeOperationalHours.size());
    }

    @Test
    public void updateAerodromeOperationalHours() {
        AerodromeOperationalHours existingAerodromeOperationalHours = new AerodromeOperationalHours();

        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setId(1);

        existingAerodromeOperationalHours.setAerodrome(aerodrome);
        existingAerodromeOperationalHours.setOperationalHoursFriday("0000-0200;1400-1600");
        existingAerodromeOperationalHours.setOperationalHoursMonday("0800-1000;1400-1600");

        when(aerodromeOperationalHoursRepository.save(any(AerodromeOperationalHours.class)))
            .thenReturn(existingAerodromeOperationalHours);

        AerodromeOperationalHours newAerodromeOperationalHours = existingAerodromeOperationalHours;
        newAerodromeOperationalHours.setAerodrome(aerodrome);
        newAerodromeOperationalHours.setOperationalHoursFriday("1400-1600");
        newAerodromeOperationalHours.setOperationalHoursMonday("1800-2000;2200-2400");

        AerodromeOperationalHours result = aerodromeOperationalHoursService.update(1, newAerodromeOperationalHours);
        assertThat(result.getOperationalHoursFriday()).isEqualTo("1400-1600");
        assertThat(result.getOperationalHoursMonday()).isEqualTo("1800-2000;2200-2400");
    }
}
