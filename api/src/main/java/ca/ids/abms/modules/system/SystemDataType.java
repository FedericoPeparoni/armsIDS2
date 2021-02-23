package ca.ids.abms.modules.system;

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

@Entity
public class SystemDataType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true)
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "dataType")
    private Set<SystemConfiguration> systemConfigurations = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemDataType that = (SystemDataType) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<SystemConfiguration> getSystemConfigurations() {
        return systemConfigurations;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setSystemConfigurations(Set<SystemConfiguration> aSystemConfigurations) {
        systemConfigurations = aSystemConfigurations;
    }

    @Override
    public String toString() {
        return "SystemDataType [id=" + id + ", name=" + name + "]";
    }
}
