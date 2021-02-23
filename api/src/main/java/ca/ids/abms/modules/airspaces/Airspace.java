package ca.ids.abms.modules.airspaces;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import com.vividsolutions.jts.geom.Geometry;

import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
@Table(name="airspaces")
public class Airspace extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    private Integer id;

    @Size(max = 100)
    @Column(unique = true)
    @NotNull
    @SearchableText
    private String airspaceName;

    @NotNull
    @SearchableText
    private String airspaceType;

    @SearchableText
    private String airspaceFullName;

    @Column(name="airspace_boundary", columnDefinition = "geometry")
    @NotNull
    private Geometry airspaceBoundary;

    @Column(name = "airspace_included")
    private Boolean airspaceIncluded;
    
    @Column(name = "airspace_ceiling")
    @Max(999)
    @Min(0)
    private Double airspaceCeiling;
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        Airspace that = (Airspace) obj;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Geometry getAirspaceBoundary() {
        return airspaceBoundary;
    }

    public String getAirspaceName() {
        return airspaceName;
    }

    public String getAirspaceType() {
        return airspaceType;
    }

    public String getAirspaceFullName() {
        return airspaceFullName;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAirspaceBoundary(Geometry aAirspaceBoundary) {
        airspaceBoundary = aAirspaceBoundary;
    }

    public void setAirspaceName(String aAirspaceName) {
        airspaceName = aAirspaceName;
    }

    public void setAirspaceType(String aAirspaceType) {
        airspaceType = aAirspaceType;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setAirspaceFullName(String airspaceFullName) {
        this.airspaceFullName = airspaceFullName;
    }

    public Boolean getAirspaceIncluded() {
        return airspaceIncluded;
    }

    public void setAirspaceIncluded(Boolean airspaceIncluded) {
        this.airspaceIncluded = airspaceIncluded;
    }

    public Double getAirspaceCeiling() {
        return airspaceCeiling;
    }

    public void setAirspaceCeiling(Double airspaceCeiling) {
        this.airspaceCeiling = airspaceCeiling;
    }

    @Override
    public String toString() {
        return "Airspace [id=" + id + ", airspaceName=" + airspaceName + ", airspaceType=" + airspaceType + "]";
    }
}
