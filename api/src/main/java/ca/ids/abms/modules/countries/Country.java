package ca.ids.abms.modules.countries;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.aerodromes.AerodromePrefix;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationPrefix;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Country extends AuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 3)
    @Column(unique = true)
    @NotNull
    @SearchableText
    private String countryCode;

    @Size(max = 50)
    @Column(unique = true)
    @NotNull
    @SearchableText
    private String countryName;

    @JsonIgnore
    @OneToMany(mappedBy = "countryCode")
    private Set<Currency> currencies = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "countryCode", cascade = CascadeType.REMOVE)
    @SearchableEntity
    private List<AircraftRegistrationPrefix> aircraftRegistrationPrefixes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "countryCode", cascade = CascadeType.REMOVE)
    @SearchableEntity
    private List<AerodromePrefix> aerodromePrefixes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "countryOfRegistration")
    private Set<AircraftRegistration> aircraftRegistrations = new HashSet<>();

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Country))
            return false;

        Country that = (Country) obj;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public List<AircraftRegistrationPrefix> getAircraftRegistrationPrefixes() {
        return aircraftRegistrationPrefixes;
    }

    public Set<AircraftRegistration> getAircraftRegistrations() {
        return aircraftRegistrations;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public Set<Currency> getCurrencies() {
        return currencies;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAircraftRegistrationPrefixes(List<AircraftRegistrationPrefix> aAircraftRegistrationPrefixes) {
        aircraftRegistrationPrefixes = aAircraftRegistrationPrefixes;
    }

    public void setAircraftRegistrations(Set<AircraftRegistration> aAircraftRegistrations) {
        aircraftRegistrations = aAircraftRegistrations;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCurrencies(Set<Currency> currencies) {
        this.currencies = currencies;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public List<AerodromePrefix> getAerodromePrefixes() {
        return aerodromePrefixes;
    }

    public void setAerodromePrefixes(List<AerodromePrefix> aerodromePrefixes) {
        this.aerodromePrefixes = aerodromePrefixes;
    }

    @Override
    public String toString() {
        return "Country [id=" + id + ", countryCode=" + countryCode + ", countryName=" + countryName + "]";
    }
}
