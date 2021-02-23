package ca.ids.abms.modules.countries;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.*;

import ca.ids.abms.modules.aerodromes.AerodromePrefixRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public class RegionalCountryServiceTest {

    private RegionalCountryRepository regionalCountryRepository;
    private RegionalCountryService regionalCountryService;
    private CountryRepository countryRepository;
    private NavDBUtils navDBUtils;
    private AerodromePrefixRepository aerodromePrefixRepository;

    @Before
    public void setup() {
        regionalCountryRepository = mock(RegionalCountryRepository.class);
        countryRepository = mock(CountryRepository.class);
        navDBUtils = mock(NavDBUtils.class);
        aerodromePrefixRepository = mock(AerodromePrefixRepository.class);
        regionalCountryService = new RegionalCountryService(regionalCountryRepository, countryRepository, navDBUtils, aerodromePrefixRepository);
    }

    @Test
    public void createItem() {
        Country country = new Country();
        country.setId(2);
        RegionalCountry regionalCountry = new RegionalCountry();
        regionalCountry.setCountry(country);
        Collection<RegionalCountry> toReturn = new ArrayList<>();
        toReturn.add(regionalCountry);

        when(countryRepository.getOne(any())).thenReturn(country);
        when(regionalCountryRepository.save(any(RegionalCountry.class))).thenReturn(regionalCountry);
        when(regionalCountryRepository.findAllByOrderByCountryNameAsc())
            .thenReturn(toReturn);

        Collection<RegionalCountry> input = new ArrayList<>();
        input.add(regionalCountry);
        Collection<RegionalCountry> saved = regionalCountryService.create(input);
        assertThat(saved != null && saved.size() == 1);

        RegionalCountry result = saved.stream().findFirst().get();
        assertThat(result != null && result.getId() == regionalCountry.getId());
        assertThat(result.getCountry() != null && result.getCountry().getId() == regionalCountry.getCountry().getId());
    }

    @Test
    public void deleteItem() {
        regionalCountryService.delete(1);
        verify(regionalCountryRepository).delete(any(Integer.class));
    }

    @Test
    public void getAllItems() {
        List<RegionalCountry> regionalCountries = Collections.singletonList(new RegionalCountry());

        when(regionalCountryRepository.findAllByOrderByCountryNameAsc(any(Pageable.class)))
                .thenReturn(new PageImpl<>(regionalCountries));

        when(regionalCountryRepository.findAll(any(Pageable.class)))
            .thenReturn(new PageImpl<>(regionalCountries));

        Page<RegionalCountry> results = regionalCountryService.findAll(mock(Pageable.class));

        assertThat(results.getTotalElements()).isEqualTo(regionalCountries.size());
    }

    @Test
    public void getItemById() {
        Country country = new Country();
        country.setId(2);
        RegionalCountry regionalCountry = new RegionalCountry();
        regionalCountry.setId(1);
        regionalCountry.setCountry(country);

        when(regionalCountryRepository.findOne(anyInt())).thenReturn(regionalCountry);
        when(countryRepository.getOne(any())).thenReturn(country);

        RegionalCountry result = regionalCountryService.getOne(1);
        assertThat(result).isEqualTo(regionalCountry);
        assertThat(result.getCountry().getId()).isEqualTo(country.getId());
    }

    @Test
    public void updateItem() {
        Country firstCountry = new Country();
        firstCountry.setId(1);
        RegionalCountry existingRegionalCountry = new RegionalCountry();
        existingRegionalCountry.setCountry(firstCountry);
        existingRegionalCountry.setId(1);

        Country secondCountry = new Country();
        secondCountry.setId(2);
        RegionalCountry secondExistingRegionalCountryToDelete = new RegionalCountry();
        secondExistingRegionalCountryToDelete.setCountry(secondCountry);
        secondExistingRegionalCountryToDelete.setId(2);

        Country thirdCountry = new Country();
        thirdCountry.setId(3);
        RegionalCountry regionalCountryToAdd = new RegionalCountry();
        regionalCountryToAdd.setCountry(thirdCountry);
        regionalCountryToAdd.setId(3);

        List<RegionalCountry> currentRegionalCountries = new ArrayList<>();
        currentRegionalCountries.add(existingRegionalCountry);
        currentRegionalCountries.add(secondExistingRegionalCountryToDelete);

        List<RegionalCountry> requiredCountries = new ArrayList<>();
        requiredCountries.add(existingRegionalCountry);
        requiredCountries.add(regionalCountryToAdd);

        List<RegionalCountry> regionalCountriesAfterAdding = new ArrayList<>();
        regionalCountriesAfterAdding.add(existingRegionalCountry);
        regionalCountriesAfterAdding.add(secondExistingRegionalCountryToDelete);
        regionalCountriesAfterAdding.add(regionalCountryToAdd);

        when(regionalCountryRepository.findAll()).thenReturn(currentRegionalCountries);
        when(regionalCountryRepository.save(anyListOf(RegionalCountry.class))).thenReturn(regionalCountriesAfterAdding);
        when(regionalCountryRepository.findAllByOrderByCountryNameAsc()).thenReturn(requiredCountries);

        Collection<RegionalCountry> results = regionalCountryService.update(requiredCountries);
        verify(regionalCountryRepository).delete(anyListOf(RegionalCountry.class));

        assertThat(results.size() == requiredCountries.size());
        for (RegionalCountry result : results) {
            assertThat(requiredCountries.contains(result));
        }
    }
}
