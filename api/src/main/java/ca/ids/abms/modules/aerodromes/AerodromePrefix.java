package ca.ids.abms.modules.aerodromes;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames={"aerodromePrefix", "countryCode"})
public class AerodromePrefix extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 4)
    @NotNull
    @SearchableText
    private String aerodromePrefix;

    @ManyToOne
    @JoinColumn(name = "country_code")
    @NotNull
    private Country countryCode;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof AerodromePrefix)) {
            return false;
        }

        AerodromePrefix that = (AerodromePrefix) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

    public Country getCountryCode() {
        return countryCode;
    }

    public Integer getId() {
        return id;
    }

    public String getAerodromePrefix() {
        return aerodromePrefix;
    }

    public void setAerodromePrefix(String aerodromePrefix) {
        this.aerodromePrefix = aerodromePrefix;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setCountryCode(Country aCountryCode) {
        countryCode = aCountryCode;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    @Override
    public String toString() {
        return "AircraftRegistrationPrefix [id=" + id + ", aerodromePrefix=" + aerodromePrefix
                + ", countryCode=" + countryCode + "]";
    }
}
