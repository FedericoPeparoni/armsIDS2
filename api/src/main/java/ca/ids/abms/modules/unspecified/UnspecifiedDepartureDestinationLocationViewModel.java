package ca.ids.abms.modules.unspecified;

import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.VersionedViewModel;

public class UnspecifiedDepartureDestinationLocationViewModel extends VersionedViewModel {

    private Integer id;

    private String textIdentifier;

    private String name;

    private Boolean maintained;

    private String aerodromeIdentifier;

    private Double latitude;

    private Double longitude;

    private UnspecifiedDepartureDestinationLocationStatus status;

    private Country countryCode;

    public String getAerodromeIdentifier() {
        return aerodromeIdentifier;
    }

    public Integer getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Boolean getMaintained() {
        return maintained;
    }

    public String getName() {
        return name;
    }

    public UnspecifiedDepartureDestinationLocationStatus getStatus() {
        return status;
    }

    public String getTextIdentifier() {
        return textIdentifier;
    }

    public Country getCountryCode() { return countryCode; }

    public void setAerodromeIdentifier(String aAerodromeIdentifier) {
        aerodromeIdentifier = aAerodromeIdentifier;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setLatitude(Double aLatitude) {
        latitude = aLatitude;
    }

    public void setLongitude(Double aLongitude) {
        longitude = aLongitude;
    }

    public void setMaintained(Boolean aMaintained) {
        maintained = aMaintained;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setStatus(UnspecifiedDepartureDestinationLocationStatus aStatus) {
        status = aStatus;
    }

    public void setTextIdentifier(String aTextIdentifier) {
        textIdentifier = aTextIdentifier;
    }

    public void setCountryCode(Country aCountryCode) {
        countryCode = aCountryCode;
    }
}
