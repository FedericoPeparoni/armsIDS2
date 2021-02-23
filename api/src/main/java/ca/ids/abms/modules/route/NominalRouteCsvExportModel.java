package ca.ids.abms.modules.route;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class NominalRouteCsvExportModel {

    private String type;

    @CsvProperty(value = "Point A")
    private String pointa;

    @CsvProperty(value = "Point B")
    private String pointb;

    @CsvProperty(value = "Bidirectional Route")
    private Boolean biDirectional;

    private String status;

    private Double nominalDistance;

    private Double nominalRouteFloor;
    
    private Double nominalRouteCeiling;

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

    public Boolean getBiDirectional() {
        return biDirectional;
    }

    public void setBiDirectional(Boolean biDirectional) {
        this.biDirectional = biDirectional;
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
}
