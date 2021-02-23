package ca.ids.abms.modules.countries;

import javax.validation.constraints.NotNull;

public class RegionalCountryViewModel {

    private Integer id;

    @NotNull
    private Country country;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RegionalCountryViewModel that = (RegionalCountryViewModel) o;

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
        return "RegionalCountryViewModel [id=" + id + ", country=" + country + "]";
    }
}
