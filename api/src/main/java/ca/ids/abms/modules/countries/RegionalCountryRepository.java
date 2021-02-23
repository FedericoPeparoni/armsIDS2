package ca.ids.abms.modules.countries;

import ca.ids.abms.config.db.ABMSRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface RegionalCountryRepository extends ABMSRepository<RegionalCountry, Integer> {

    @Query(
            "select rc from RegionalCountry rc, Country c where rc.country.id = c.id"
            + " order by c.countryName asc"
    )
    Page<RegionalCountry> findAllByOrderByCountryNameAsc(Pageable pageable);

    @Query(
        "select rc from RegionalCountry rc, Country c where rc.country.id = c.id"
            + " order by c.countryName asc"
    )
    Collection<RegionalCountry> findAllByOrderByCountryNameAsc();

    @Query("SELECT rc FROM RegionalCountry rc JOIN rc.country c " +
        "WHERE lower(c.countryCode) LIKE '%' || :textSearch || '%' " +
        "OR lower(c.countryName) LIKE '%' || :textSearch || '%'")
    Page<RegionalCountry> findAllByOrderByCountryNameAsc(Pageable pageable, final @Param("textSearch") String textSearch);

    Collection<RegionalCountry> findAllByCountryIn(Collection<Country> countryId);
}
