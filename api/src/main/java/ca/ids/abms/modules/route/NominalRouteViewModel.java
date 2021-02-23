package ca.ids.abms.modules.route;

import ca.ids.abms.modules.util.models.VersionedViewModel;

public class NominalRouteViewModel extends VersionedViewModel {

    private Integer id;

    private String type;

    private String pointa;

    private String pointb;

    private String status;

    private Double nominalDistance;
    
    private Boolean biDirectional;

    private Double nominalRouteFloor;
    
    private Double nominalRouteCeiling;
    
    
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NominalRouteViewModel that = (NominalRouteViewModel) o;

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
                + status + ", nominalDistance=" + nominalDistance +  ",  nominalRouteCeiling=" +  nominalRouteCeiling 
                + ",  nominalRouteFloor=" +  nominalRouteFloor +"]";
    }
}
