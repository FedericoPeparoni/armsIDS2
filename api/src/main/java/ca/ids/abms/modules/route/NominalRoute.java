package ca.ids.abms.modules.route;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@UniqueKey(columnNames={"pointa", "pointb", "type","nominalRouteFloor", "nominalRouteCeiling"})
public class NominalRoute extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @SearchableText
    private String type;

    @NotNull
    @SearchableText
    private String pointa;

    @NotNull
    @SearchableText
    private String pointb;

    @SearchableText
    private String status;

    @NotNull
    private Double nominalDistance;

    private Boolean biDirectional;
    
    @NotNull
    @Max(999)
    @Min(0)
    private Double nominalRouteFloor;
    
    @NotNull
    @Max(999)
    @Min(0)
    private Double nominalRouteCeiling;
    
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NominalRoute that = (NominalRoute) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Integer getId() {
        return id;
    }

    public Double getNominalDistance() {
        return nominalDistance;
    }

    public String getPointa() {
        return pointa;
    }

    public String getPointb() {
        return pointb;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setNominalDistance(Double aNominalDistance) {
        nominalDistance = aNominalDistance;
    }

    public void setPointa(String aPointa) {
        pointa = aPointa;
    }

    public void setPointb(String aPointb) {
        pointb = aPointb;
    }

    public void setStatus(String aStatus) {
        status = aStatus;
    }

    public void setType(String aType) {
        type = aType;
    }

    public Boolean getBiDirectional() {
        return biDirectional;
    }

    public void setBiDirectional(Boolean biDirectional) {
        this.biDirectional = biDirectional;
    }

    public Double getNominalRouteFloor() {
        return nominalRouteFloor;
    }

    public void setNominalRouteFloor(Double nominalRouteFloor) {
        this.nominalRouteFloor = nominalRouteFloor;
    }

    public Double getNominalRouteCeiling() {
        return nominalRouteCeiling;
    }

    public void setNominalRouteCeiling(Double nominalRouteCeiling) {
        this.nominalRouteCeiling = nominalRouteCeiling;
    }

    @Override
    public String toString() {
        return "NominalRoute [id=" + id + ", type=" + type + ", pointa=" + pointa + ", pointb=" + pointb + ", status="
                + status + ", nominalDistance=" + nominalDistance + ",  nominalRouteCeiling=" +  nominalRouteCeiling  + ",  nominalRouteFloor=" +  nominalRouteFloor +"]";
    }
}
