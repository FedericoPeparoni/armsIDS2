package ca.ids.abms.modules.route;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import org.hibernate.annotations.Immutable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * Used solely for finding nominal routes. Adds additional is inverse
 * boolean property so indicate if nominal route found is inverted.
 */
@Entity
@Immutable
@UniqueKey(columnNames={"pointa", "pointb", "type"})
public class BiDirectionalNominalRoute extends VersionedAuditedEntity {

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

    private Boolean isInverse;

    private Double nominalRouteFloor;
    
    private Double nominalRouteCeiling;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPointa() {
        return pointa;
    }

    public void setPointa(String pointa) {
        this.pointa = pointa;
    }

    public String getPointb() {
        return pointb;
    }

    public void setPointb(String pointb) {
        this.pointb = pointb;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getNominalDistance() {
        return nominalDistance;
    }

    public void setNominalDistance(Double nominalDistance) {
        this.nominalDistance = nominalDistance;
    }

    public Boolean getBiDirectional() {
        return biDirectional;
    }

    public void setBiDirectional(Boolean biDirectional) {
        this.biDirectional = biDirectional;
    }

    public Boolean isInverse() {
        return isInverse;
    }

    public void isInverse(Boolean inverse) {
        isInverse = inverse;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BiDirectionalNominalRoute that = (BiDirectionalNominalRoute) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(type, that.type) &&
            Objects.equals(pointa, that.pointa) &&
            Objects.equals(pointb, that.pointb) &&
            Objects.equals(status, that.status) &&
            Objects.equals(nominalDistance, that.nominalDistance) &&
            Objects.equals(biDirectional, that.biDirectional) &&
            Objects.equals(isInverse, that.isInverse) &&
            Objects.equals(nominalRouteCeiling, that.nominalRouteCeiling) &&
            Objects.equals(nominalRouteFloor, that.nominalRouteFloor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, pointa, pointb, status, nominalDistance, biDirectional, isInverse,nominalRouteCeiling,nominalRouteFloor);
    }

    @Override
    public String toString() {
        return "BiDirectionalNominalRoute{" +
            "id=" + id +
            ", type='" + type + '\'' +
            ", pointa='" + pointa + '\'' +
            ", pointb='" + pointb + '\'' +
            ", status='" + status + '\'' +
            ", nominalDistance=" + nominalDistance +
            ", biDirectional=" + biDirectional +
            ", isInverse=" + isInverse +
            ", nominalRouteFloor=" + nominalRouteFloor +
            ", nominalRouteCeiling=" + nominalRouteCeiling +
            '}';
    }
}
