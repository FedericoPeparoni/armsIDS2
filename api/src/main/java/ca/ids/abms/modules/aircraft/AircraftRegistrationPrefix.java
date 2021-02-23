package ca.ids.abms.modules.aircraft;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class AircraftRegistrationPrefix extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 7)
    @NotNull
    @SearchableText
    private String aircraftRegistrationPrefix;

    @Size(max = 400)
    private String pattern;

    @ManyToOne
    @JoinColumn(name = "country_code")
    @NotNull
    private Country countryCode;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof AircraftRegistrationPrefix)) {
            return false;
        }

        AircraftRegistrationPrefix that = (AircraftRegistrationPrefix) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

    public String getAircraftRegistrationPrefix() {
        return aircraftRegistrationPrefix;
    }

    public Country getCountryCode() {
        return countryCode;
    }

    public Integer getId() {
        return id;
    }

    public String getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAircraftRegistrationPrefix(String aAircraftRegistrationPrefix) {
        aircraftRegistrationPrefix = aAircraftRegistrationPrefix;
    }

    public void setCountryCode(Country aCountryCode) {
        countryCode = aCountryCode;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setPattern(String aPattern) {
        pattern = aPattern;
    }

    @Override
    public String toString() {
        return "AircraftRegistrationPrefix [id=" + id + ", aircraftRegistrationPrefix=" + aircraftRegistrationPrefix
                + ", countryCode=" + countryCode + "]";
    }
}
