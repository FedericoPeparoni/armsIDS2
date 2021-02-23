package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.modules.countries.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AerodromePrefixRepository extends JpaRepository<AerodromePrefix, Integer> {

    List<AerodromePrefix> findByCountryCode(Country countryCode);

    AerodromePrefix findOneByCountryCodeAndAerodromePrefix(Country countryCode, String aerodromePrefix);

    AerodromePrefix findByAerodromePrefix(String aerodromePrefix);
}
