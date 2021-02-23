package ca.ids.abms.modules.countries;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CountryRepository extends ABMSRepository<Country, Integer> {

    Page<Country> findAllByOrderByCountryNameAsc(Pageable pageable);

    @Query(value="SELECT c.* "
            + "FROM abms.aircraft_registration_prefixes a, abms.countries c "
            + "WHERE a.aircraft_registration_prefix = substring ( :prefix, 1, char_length ( a.aircraft_registration_prefix ) ) "
            + "AND a.country_code = c.id "
            + "ORDER BY char_length ( a.aircraft_registration_prefix ) desc LIMIT 1 ", nativeQuery = true)
    Country findCountryByPrefix(@Param("prefix") String prefix);

    @Query(value="select c.* from countries c, aerodrome_prefixes ae where ae.country_code = c.id and ae.aerodrome_prefix = :aerodromePrefix",
        nativeQuery = true)
    List<Country> findCountryByAerodromePrefix(@Param("aerodromePrefix") String aerodromePrefix);

    // AS is necessary for referencing the aliases in request
    @Query(value="SELECT c.*, MAX(arp.aircraft_registration_prefix) AS aircraft_registration_prefix, MAX(ap.aerodrome_prefix) AS aerodrome_prefix " +
        "FROM abms.countries c " +
        "LEFT JOIN aircraft_registration_prefixes arp ON arp.country_code = c.id " +
        "LEFT JOIN aerodrome_prefixes ap ON ap.country_code = c.id " +
        "GROUP BY c.id " +
        "--#pageable\n", nativeQuery = true)
    Page<Country> findAllIncludingPrefixes(Pageable pageable);
    
    Country findCountryByCountryCode(String code);
    
    Country findCountryByCountryName(String countryName);
}
