package ca.ids.abms.modules.countries;

import java.util.List;

import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.aerodromes.AerodromePrefix;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefix;

public class CountryViewModel {

    private Integer id;

    @NotNull
    private String countryCode;

    @NotNull
    private String countryName;

    @NotNull
    private List<AerodromePrefix> aerodromePrefixes;

    @NotNull
    private List<AircraftRegistrationPrefix> aircraftRegistrationPrefixes;

    private String pattern;

    public Integer getId() {
        return id;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String aCountryCode) {
        countryCode = aCountryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String aCountryName) {
        countryName = aCountryName;
    }

    public List<AerodromePrefix> getAerodromePrefixes() {
        return aerodromePrefixes;
    }

    public void setAerodromePrefixes(List<AerodromePrefix> aAerodromePrefixes) {
        aerodromePrefixes = aAerodromePrefixes;
    }

    public List<AircraftRegistrationPrefix> getAircraftRegistrationPrefixes() {
        return aircraftRegistrationPrefixes;
    }

    public void setAircraftRegistrationPrefixes(List<AircraftRegistrationPrefix> aAircraftRegistrationPrefixes) {
        aircraftRegistrationPrefixes = aAircraftRegistrationPrefixes;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String aPattern) {
        pattern = aPattern;
    }

        @Override
    public String toString() {
        return "CountryViewModel [id=" + id + ", countryName=" + countryName + "]";
    }
}
