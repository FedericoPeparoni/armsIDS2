package ca.ids.abms.modules.aerodrome;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromePrefix;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.aerodromeserviceoutages.AerodromeServiceOutageRepository;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.UserRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AerodromeServiceTest {
    private AerodromeRepository aerodromeRepository;
    private UserRepository userRepository;
    private AerodromeService aerodromeService;
    private NavDBUtils navDBUtils;
    private AerodromeServiceOutageRepository aerodromeServiceOutageRepository;
    private CountryService countryService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {
        aerodromeRepository = mock(AerodromeRepository.class);
        userRepository = mock(UserRepository.class);
        aerodromeServiceOutageRepository = mock(AerodromeServiceOutageRepository.class);
        navDBUtils = mock(NavDBUtils.class);
        countryService = mock(CountryService.class);
        systemConfigurationService = mock(SystemConfigurationService.class);
        aerodromeService = new AerodromeService(aerodromeRepository, userRepository, navDBUtils, aerodromeServiceOutageRepository, countryService, systemConfigurationService);
    }

    @Test
    public void createAerodrome() {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setAerodromeName("NAME");

        List<AerodromePrefix> list = new ArrayList<>();
        AerodromePrefix aerodromePrefix = new AerodromePrefix();
        aerodromePrefix.setAerodromePrefix("NA");
        list.add(aerodromePrefix);

        Country country = new Country();
        country.setCountryName("Test");
        country.setAerodromePrefixes(list);

        when(aerodromeRepository.save(any(Aerodrome.class)))
            .thenReturn(aerodrome);
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("TES");
        when(countryService.findCountryByCountryCode("TES"))
            .thenReturn(country);

        Aerodrome result = aerodromeService.save(aerodrome);
        assertThat(result.getAerodromeName()).isEqualTo(aerodrome.getAerodromeName());
    }

    @Test
    public void deleteAerodrome() {
        aerodromeService.delete(1);
        verify(aerodromeRepository).delete(any(Integer.class));
    }

    @Test
    public void getAerodromeById() {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setId(1);

        when(aerodromeRepository.getOne(any()))
        .thenReturn(aerodrome);

        Aerodrome result = aerodromeService.getOne(1);
        assertThat(result).isEqualTo(aerodrome);
    }

    @Test
    public void getAllAerodromes() {
        List<Aerodrome> aerodromes = Collections.singletonList(new Aerodrome());

        when(aerodromeRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(aerodromes));

        Page<Aerodrome> results = aerodromeService.findAll(mock(Pageable.class), null);

        assertThat(results.getTotalElements()).isEqualTo(aerodromes.size());
    }

    @Test
    public void updateAerodrome() {
        Aerodrome existingAerodrome = new Aerodrome();
        existingAerodrome.setAerodromeName("NAME");
        existingAerodrome.setAerodromeServices(new HashSet<>());

        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setAerodromeName("NAME NEW");
        aerodrome.setAerodromeServices(new HashSet<>());

        List<AerodromePrefix> list = new ArrayList<>();
        AerodromePrefix aerodromePrefix = new AerodromePrefix();
        aerodromePrefix.setAerodromePrefix("NA");
        list.add(aerodromePrefix);

        Country country = new Country();
        country.setCountryName("Test");
        country.setAerodromePrefixes(list);

        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("TES");

        when(countryService.findCountryByCountryCode("TES"))
            .thenReturn(country);

        when(aerodromeRepository.getOne(any()))
            .thenReturn(existingAerodrome);
        
        when(aerodromeRepository.save(any(Aerodrome.class)))
            .thenReturn(existingAerodrome);

        Aerodrome result = aerodromeService.update(1, aerodrome);

        assertThat(result.getAerodromeName()).isEqualTo("NAME NEW");
    }
}
