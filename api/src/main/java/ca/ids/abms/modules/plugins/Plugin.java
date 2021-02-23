package ca.ids.abms.modules.plugins;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.plugins.utility.PluginKeyConverter;
import ca.ids.abms.modules.system.SystemItemType;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.plugins.PluginKey;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "plugins")
public class Plugin extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @NotNull
    @SearchableText
    private String name;

    @Column(name = "description")
    @SearchableText
    private String description;

    @Column(name = "enabled")
    @NotNull
    private Boolean enabled;

    @Column(name = "visible")
    @NotNull
    private Boolean visible;

    @Column(name = "key", unique = true)
    @Convert(converter = PluginKeyConverter.class)
    private PluginKey key;

    @Column(name = "site", length = 100)
    private String site;

    @JsonIgnore
    @OneToMany(mappedBy = "plugin")
    private Set<SystemItemType> systemItemTypes = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getVisible() {
        return visible;
    }

    public void setVisible(Boolean visible) {
        this.visible = visible;
    }

    public PluginKey getKey() {
        return key;
    }

    public void setKey(PluginKey key) {
        this.key = key;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Set<SystemItemType> getSystemItemTypes() {
        return systemItemTypes;
    }

    public void setSystemItemTypes(Set<SystemItemType> systemItemTypes) {
        this.systemItemTypes = systemItemTypes;
    }

    @Override
    public String toString() {
        return "Plugin [id=" + id + ", name=" + name + ", description=" + description + ", key=" + key
            + ", enabled=" + enabled + ", visible=" + key + ", site=" + site + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Plugin that = (Plugin) o;
        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
