package ca.ids.abms.modules.countries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.modules.aerodromes.AerodromePrefix;
import ca.ids.abms.modules.aerodromes.AerodromePrefixService;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefix;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefixService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class CountryServiceTest {

    private CountryRepository countryRepository;
    private CountryService countryService;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {

        AerodromePrefixService aerodromePrefixService = mock(AerodromePrefixService.class);
        AircraftRegistrationPrefixService aircraftRegistrationPrefixService = mock(AircraftRegistrationPrefixService.class);

        countryRepository = mock(CountryRepository.class);
        systemConfigurationService = mock(SystemConfigurationService.class);

        countryService = new CountryService(aerodromePrefixService, aircraftRegistrationPrefixService,
            countryRepository, systemConfigurationService);
    }

    @Test
    public void findDefaultCountryTest() {

        // test when default country is null, return null
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn(null);
        assertThat(countryService.findDefaultCountry())
            .isNull();

        // test when default country is empty, return null
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("");
        assertThat(countryService.findDefaultCountry())
            .isNull();

        // test when default country is not found, return null
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("ABC");
        assertThat(countryService.findDefaultCountry())
            .isNull();

        // mock default country
        Country country = new Country();
        country.setCountryCode("XYZ");
        country.setCountryName("Mock Country");

        when(countryRepository.findCountryByCountryCode("XYZ"))
            .thenReturn(country);
        when(countryRepository.findCountryByCountryName("Mock Country"))
            .thenReturn(country);

        // test when default country is a country code, return country
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("XYZ");
        assertThat(countryService.findDefaultCountry())
            .isEqualTo(country);

        // test when default country is a country name, return country
        when(systemConfigurationService.getCurrentValue(SystemConfigurationItemName.ANSP_COUNTRY_CODE))
            .thenReturn("Mock Country");
        assertThat(countryService.findDefaultCountry())
            .isEqualTo(country);
    }

    @Test
    public void getAllCountries() {
        List<Country> countries = Collections.singletonList(new Country());

        when(countryRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
        .thenReturn(new PageImpl<>(countries));

        Page<Country> results = countryService.findAll(mock(Pageable.class), "");

        assertThat(results.getTotalElements()).isEqualTo(countries.size());
    }

    @Test
    public void getAllCountriesIncludingPrefixes() {
        List<Country> countries = Collections.singletonList(new Country());

        when(countryRepository.findAllIncludingPrefixes(any(Pageable.class)))
            .thenReturn(new PageImpl<>(countries));

        Page<Country> results = countryService.findAllIncludingPrefixes(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(countries.size());
    }

    @Test
    public void handleAerodromePrefixes() {
        // New country
        Country newCountry = new Country();
        newCountry.setCountryName("Kenya");
        newCountry.setCountryCode("KEN");

        AerodromePrefix aP1 = new AerodromePrefix();
        aP1.setAerodromePrefix("KC");

        AerodromePrefix aP2 = new AerodromePrefix();
        aP2.setAerodromePrefix("KA");

        List<AerodromePrefix> aPList = new ArrayList<>();
        aPList.add(aP1);
        aPList.add(aP2);

        newCountry.setAerodromePrefixes(aPList);

        when(countryService.save(any(Country.class))).thenReturn(newCountry);

        assertThat(countryService.create(newCountry)).isEqualTo(newCountry);
        assertThat(newCountry.getAerodromePrefixes()).size().isEqualTo(2);
    }

    @Test
    public void handleAircraftRegistrationPrefixes() {
        // New country
        Country newCountry = new Country();
        newCountry.setCountryName("Kenya");
        newCountry.setCountryCode("KEN");

        AircraftRegistrationPrefix aRP1 = new AircraftRegistrationPrefix();
        aRP1.setAircraftRegistrationPrefix("KC");

        AircraftRegistrationPrefix aRP2 = new AircraftRegistrationPrefix();
        aRP2.setAircraftRegistrationPrefix("KA");

        List<AircraftRegistrationPrefix> aRPList = new ArrayList<>();
        aRPList.add(aRP1);
        aRPList.add(aRP2);

        newCountry.setAircraftRegistrationPrefixes(aRPList);

        when(countryService.save(any(Country.class))).thenReturn(newCountry);

        assertThat(countryService.create(newCountry)).isEqualTo(newCountry);
        assertThat(newCountry.getAircraftRegistrationPrefixes()).size().isEqualTo(2);
    }
}
