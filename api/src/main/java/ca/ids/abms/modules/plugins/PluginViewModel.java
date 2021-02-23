package ca.ids.abms.modules.plugins;

import ca.ids.abms.plugins.PluginKey;

import java.util.Objects;

public class PluginViewModel {

    private Integer id;

    private String name;

    private String description;

    private Boolean enabled;

    private Boolean visible;

    private PluginKey key;

    private String site;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginViewModel that = (PluginViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PluginViewModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", enabled=" + enabled +
            ", visible=" + visible +
            ", key=" + key +
            ", site='" + site + '\'' +
            '}';
    }
}
