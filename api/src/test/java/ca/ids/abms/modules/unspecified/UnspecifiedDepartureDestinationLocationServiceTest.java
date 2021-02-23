package ca.ids.abms.modules.unspecified;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.util.stringmatcher.StringMatcherService;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeRepository;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;

public class UnspecifiedDepartureDestinationLocationServiceTest {

    private AerodromeRepository aerodromeRepository;
    private CountryService countryService;
    private UnspecifiedDepartureDestinationLocationRepository unspecifiedDepartureDestinationLocationRepository;
    private UnspecifiedDepartureDestinationLocationService unspecifiedDepartureDestinationLocationService;

    @Before
    public void setup() {

        StringMatcherService stringMatcherService = mock(StringMatcherService.class);

        aerodromeRepository = mock(AerodromeRepository.class);
        countryService = mock(CountryService.class);
        unspecifiedDepartureDestinationLocationRepository = mock(UnspecifiedDepartureDestinationLocationRepository.class);

        unspecifiedDepartureDestinationLocationService = new UnspecifiedDepartureDestinationLocationService(
            aerodromeRepository, countryService, stringMatcherService, unspecifiedDepartureDestinationLocationRepository);

        when(countryService.findDefaultCountry())
            .thenReturn(mockCountry());
    }

    @Test
    public void createUnspecifiedDepartureDestinationLocation() {

        Aerodrome aerodrome = mockAerodrome("mock aerodrome name");

        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
        unspecifiedDepartureDestinationLocation.setTextIdentifier("text identifier");
        unspecifiedDepartureDestinationLocation.setAerodromeIdentifier(aerodrome);

        when(aerodromeRepository.findByAerodromeName(any()))
            .thenReturn(aerodrome);

        when(countryService.findCountryByAerodromePrefix(any()))
            .thenReturn(Collections.singletonList(mockCountry()));

        when(unspecifiedDepartureDestinationLocationRepository.save(any(UnspecifiedDepartureDestinationLocation.class)))
            .thenReturn(unspecifiedDepartureDestinationLocation);

        UnspecifiedDepartureDestinationLocation result = unspecifiedDepartureDestinationLocationService
                .create(unspecifiedDepartureDestinationLocation, aerodrome.getAerodromeName());

        assertThat(result.getTextIdentifier()).isEqualTo(unspecifiedDepartureDestinationLocation.getTextIdentifier());
    }

    @Test
    public void getAllUnspecifiedDepartureDestinationLocations() {

        List<UnspecifiedDepartureDestinationLocation> unspecifiedDepartureDestinationLocations = Collections
            .singletonList(new UnspecifiedDepartureDestinationLocation());

        // noinspection unchecked
        when(unspecifiedDepartureDestinationLocationRepository.findAll(any(FiltersSpecification.class), any(Pageable.class)))
            .thenReturn(new PageImpl<>(unspecifiedDepartureDestinationLocations));

        Page<UnspecifiedDepartureDestinationLocation> result = unspecifiedDepartureDestinationLocationService
            .findAll("test", mock(Pageable.class));

        assertThat(result.getTotalElements())
            .isEqualTo(unspecifiedDepartureDestinationLocations.size());
    }

    @Test
    public void getUnspecifiedDepartureDestinationLocationById() {

        UnspecifiedDepartureDestinationLocation unspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
        unspecifiedDepartureDestinationLocation.setId(1);

        when(unspecifiedDepartureDestinationLocationRepository.findOne(1))
            .thenReturn(unspecifiedDepartureDestinationLocation);

        assertThat(unspecifiedDepartureDestinationLocationService.findOne(1))
            .isEqualTo(unspecifiedDepartureDestinationLocation);
    }

    @Test
    public void updateUnspecifiedDepartureDestinationLocation() {

        Aerodrome aerodrome = mockAerodrome("mock aerodrome name");

        UnspecifiedDepartureDestinationLocation existingUnspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
        existingUnspecifiedDepartureDestinationLocation.setTextIdentifier("text identifier");

        UnspecifiedDepartureDestinationLocation updateUnspecifiedDepartureDestinationLocation = new UnspecifiedDepartureDestinationLocation();
        updateUnspecifiedDepartureDestinationLocation.setTextIdentifier("text identifier update");
        updateUnspecifiedDepartureDestinationLocation.setAerodromeIdentifier(aerodrome);

        when(unspecifiedDepartureDestinationLocationRepository.getOne(1))
            .thenReturn(existingUnspecifiedDepartureDestinationLocation);
        when(unspecifiedDepartureDestinationLocationRepository.save(any(UnspecifiedDepartureDestinationLocation.class)))
            .thenReturn(existingUnspecifiedDepartureDestinationLocation);

        when(aerodromeRepository.findByAerodromeName(any()))
            .thenReturn(aerodrome);

        UnspecifiedDepartureDestinationLocation result = unspecifiedDepartureDestinationLocationService
            .update(1, updateUnspecifiedDepartureDestinationLocation, aerodrome.getAerodromeName());
        assertThat(result.getTextIdentifier()).isEqualTo("text identifier update");
    }

    @Test
    public void deleteUnspecifiedDepartureDestinationLocation() {
        unspecifiedDepartureDestinationLocationService.delete(1);
        verify(unspecifiedDepartureDestinationLocationRepository)
            .delete(any(Integer.class));
    }

    @Test
    public void validateUnspecifiedDepartureDestinationLocation() {

    	String textIdentifier = "TEXT";
    	String aerodromeIdentifier = "ADDD";

        Aerodrome aerodrome = mockAerodrome(aerodromeIdentifier);

    	UnspecifiedDepartureDestinationLocation location = new UnspecifiedDepartureDestinationLocation();
        location.setAerodromeIdentifier(aerodrome);
        location.setTextIdentifier(textIdentifier);

    	// test where no locations found for identifiers
        when(unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(textIdentifier))
            .thenReturn(null);
        when(unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(aerodromeIdentifier))
            .thenReturn(null);
        assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isTrue();

        // test where no locations found for flipped identifiers
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(aerodromeIdentifier))
            .thenReturn(location);
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(textIdentifier))
            .thenReturn(location);
    	assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isTrue();

    	// test where locations returned for both identifiers
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(textIdentifier))
            .thenReturn(location);
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(aerodromeIdentifier))
            .thenReturn(location);
    	assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isFalse();

    	// test where location returned for only aerodrome identifier
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(textIdentifier))
            .thenReturn(null);
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(aerodromeIdentifier))
            .thenReturn(location);
    	assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isFalse();

    	// test where location returned for only text identifier
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByAerodromeIdentifierAerodromeNameOrderById(textIdentifier))
            .thenReturn(location);
    	when(unspecifiedDepartureDestinationLocationRepository.findFirstByTextIdentifierOrderById(aerodromeIdentifier))
            .thenReturn(null);
    	assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isFalse();

        // test where location is null, text identifier is null or text identifier is empty
        assertThat(unspecifiedDepartureDestinationLocationService.validate(null))
            .isFalse();
        location.setTextIdentifier("");
        assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isFalse();
        location.setTextIdentifier(null);
        assertThat(unspecifiedDepartureDestinationLocationService.validate(location))
            .isFalse();
    }

    private Aerodrome mockAerodrome(final String name) {
        Aerodrome aerodrome = new Aerodrome();
        aerodrome.setAerodromeName(name);
        return aerodrome;
    }

    private Country mockCountry() {
        Country country = new Country();
        country.setCountryCode("KES");
        return country;
    }
}
