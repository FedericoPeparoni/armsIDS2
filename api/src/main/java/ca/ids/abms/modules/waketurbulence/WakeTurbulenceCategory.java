package ca.ids.abms.modules.waketurbulence;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.modules.aircraft.AircraftType;

@Entity
public class WakeTurbulenceCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 25)
    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "wakeTurbulenceCategory")
    private Set<AircraftType> aircraftTypes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        WakeTurbulenceCategory that = (WakeTurbulenceCategory) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Set<AircraftType> getAircraftTypes() {
        return aircraftTypes;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAircraftTypes(Set<AircraftType> aAircraftTypes) {
        aircraftTypes = aAircraftTypes;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setName(String aName) {
        name = aName;
    }

    @Override
    public String toString() {
        return "WakeTurbulenceCategory [id=" + id + ", name=" + name + "]";
    }
}
