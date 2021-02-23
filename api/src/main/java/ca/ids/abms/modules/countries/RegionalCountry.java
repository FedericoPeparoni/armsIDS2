package ca.ids.abms.modules.countries;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class RegionalCountry extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    private Integer id;

    @NotNull
    @JoinColumn(name = "country_id")
    @OneToOne
    @SearchableEntity
    private Country country;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RegionalCountry that = (RegionalCountry) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Country getCountry() {
        return country;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setCountry(Country aCountry) {
        country = aCountry;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    @Override
    public String toString() {
        return "RegionalCountry [id=" + id + ", country=" + country + "]";
    }


}
