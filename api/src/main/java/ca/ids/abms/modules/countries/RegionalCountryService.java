package ca.ids.abms.modules.countries;

import ca.ids.abms.modules.aerodromes.AerodromePrefix;
import ca.ids.abms.modules.aerodromes.AerodromePrefixRepository;
import ca.ids.abms.modules.util.models.NavDBUtils;
import ca.ids.abms.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RegionalCountryService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionalCountryService.class);

    private RegionalCountryRepository regionalCountryRepository;
    private CountryRepository countryRepository;
    private NavDBUtils navDBUtils;
    private AerodromePrefixRepository aerodromePrefixRepository;

    public RegionalCountryService(RegionalCountryRepository anRegionalCountryRepository,
                                  CountryRepository anCountryRepository, NavDBUtils navDBUtils,
                                  AerodromePrefixRepository aerodromePrefixRepository) {
        regionalCountryRepository = anRegionalCountryRepository;
        countryRepository = anCountryRepository;
        this.navDBUtils = navDBUtils;
        this.aerodromePrefixRepository = aerodromePrefixRepository;
    }

    public Collection<RegionalCountry> create(Collection<RegionalCountry> items) {
        Collection<RegionalCountry> results = new ArrayList<>();
        for (RegionalCountry item : items) {
            final Integer id = item.getCountry().getId();
            final Country country = this.countryRepository.getOne(id);
            item.setCountry(country);
            item.setId(id);
            results.add(regionalCountryRepository.save(item));
        }
        regionalCountryRepository.flush();
        return regionalCountryRepository.findAllByOrderByCountryNameAsc();
    }

    public void delete(Integer id) {
        regionalCountryRepository.delete(id);
    }

    @Transactional(readOnly = true)
    public Page<RegionalCountry> findAll(Pageable pageable) {
        LOG.debug("Request to find all RegionalCountry");
        return regionalCountryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<RegionalCountry> findAll(Pageable pageable, String searchText) {
        LOG.debug("Request to find all RegionalCountry");
         if (StringUtils.isNotBlank(searchText)) {
             return regionalCountryRepository.findAllByOrderByCountryNameAsc(pageable, searchText.toLowerCase());
         }
        return regionalCountryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public RegionalCountry getOne(Integer id) {
        return regionalCountryRepository.findOne(id);
    }

    public Collection<RegionalCountry> update(Collection<RegionalCountry> items) {
        final Map<Integer, Country> currentCountries = regionalCountryRepository.findAll().stream()
            .collect(Collectors.toMap(co -> co.getCountry().getId(), co -> co.getCountry()));

        final Collection<Country> requiredCountries = items.stream().map(rc -> rc.getCountry())
            .collect(Collectors.toList());
        final Collection<Integer> requiredCountriesId = items.stream().map(rc -> rc.getCountry().getId())
            .collect(Collectors.toList());

        final List<RegionalCountry> itemsToAdd = requiredCountries.stream().filter(i -> !currentCountries.containsKey(i.getId()))
            .map(i -> mapRegionalCountry(i)).collect(Collectors.toList());

        final List<RegionalCountry> itemsToDelete = currentCountries.entrySet().stream()
            .filter(e -> !requiredCountriesId.contains(e.getKey())).map(i -> mapRegionalCountry(i.getValue()))
            .collect(Collectors.toList());

        if (LOG.isDebugEnabled()) {
            LOG.debug("Regional countries to add: {} - Regional countries to remove: {}", itemsToAdd, itemsToDelete);
        }
        regionalCountryRepository.save(itemsToAdd);
        regionalCountryRepository.delete(itemsToDelete);
        regionalCountryRepository.flush();

        linkAerodromePrefixes((itemsToAdd));

        return regionalCountryRepository.findAllByOrderByCountryNameAsc();
    }

    private void linkAerodromePrefixes (final List<RegionalCountry> itemsToAdd) {
        if (itemsToAdd != null) {
            for (final RegionalCountry country : itemsToAdd) {
                final List<String> prefixes = navDBUtils.getAerodromePrefixes(country.getCountry().getCountryName());
                if (CollectionUtils.isNotEmpty(prefixes)) {
                    for (final String prefix : prefixes) {

                        AerodromePrefix aerodromePrefix = aerodromePrefixRepository
                            .findOneByCountryCodeAndAerodromePrefix(country.getCountry(), prefix);

                        if (aerodromePrefix == null) {
                            aerodromePrefix = new AerodromePrefix();
                        }

                        aerodromePrefix.setAerodromePrefix(prefix);
                        aerodromePrefix.setCountryCode(country.getCountry());
                        aerodromePrefixRepository.save(aerodromePrefix);
                    }
                    if (LOG.isDebugEnabled()) {
                        final StringBuilder prefixesList = new StringBuilder();
                        prefixes.forEach(p -> {prefixesList.append(' ').append(p);});
                        LOG.debug("For the country {} have been added the aerodrome prefixes:{}",
                            country.getCountry().getCountryName(), prefixesList.toString());
                    }
                } else {
                    LOG.warn("No aerodrome prefixes found for the country {}", country.getCountry().getCountryName());
                }
            }
        }
    }

    private RegionalCountry mapRegionalCountry (Country country) {
        final RegionalCountry regionalCountry = new RegionalCountry();
        regionalCountry.setId(country.getId());
        regionalCountry.setCountry(country);
        return regionalCountry;
    }

    public long countAll() {
        return regionalCountryRepository.count();
    }
}
