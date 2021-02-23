package ca.ids.abms.modules.countries;

public class CountryCsvExportModel {

    private String countryCode;

    private String countryName;

    private String aircraftRegistrationPrefixes;

    private String aerodromePrefixes;

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getAircraftRegistrationPrefixes() {
        return aircraftRegistrationPrefixes;
    }

    public void setAircraftRegistrationPrefixes(String aircraftRegistrationPrefixes) {
        this.aircraftRegistrationPrefixes = aircraftRegistrationPrefixes;
    }

    public String getAerodromePrefixes() {
        return aerodromePrefixes;
    }

    public void setAerodromePrefixes(String aerodromePrefixes) {
        this.aerodromePrefixes = aerodromePrefixes;
    }
}
