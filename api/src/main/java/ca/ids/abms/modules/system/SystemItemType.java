package ca.ids.abms.modules.system;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.modules.plugins.Plugin;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class SystemItemType implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "plugin_id")
    @JsonIgnore
    private Plugin plugin;

    @JsonIgnore
    @OneToMany(mappedBy = "itemClass")
    private Set<SystemConfiguration> systemConfigurations = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SystemItemType that = (SystemItemType) o;

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

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }

    public void setSystemConfigurations(Set<SystemConfiguration> aSystemConfigurations) {
        systemConfigurations = aSystemConfigurations;
    }

    @Override
    public String toString() {
        return "SystemItemType [id=" + id + ", name=" + name + "]";
    }
}
