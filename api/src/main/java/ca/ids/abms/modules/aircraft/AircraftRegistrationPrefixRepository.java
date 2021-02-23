package ca.ids.abms.modules.aircraft;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.ids.abms.modules.countries.Country;

public interface AircraftRegistrationPrefixRepository extends JpaRepository<AircraftRegistrationPrefix, Integer> {

    List<AircraftRegistrationPrefix> findByCountryCode(Country countryCode);

    AircraftRegistrationPrefix findByAircraftRegistrationPrefix(String aircraftRegistrationPrefix);
}
