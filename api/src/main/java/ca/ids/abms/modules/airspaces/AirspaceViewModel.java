package ca.ids.abms.modules.airspaces;

public class AirspaceViewModel {

    private Integer id;

    private String airspaceName;

    private String airspaceType;

    private String airspaceFullName;

    private Boolean airspaceIncluded;
    
    private Double airspaceCeiling;
    
    public String getAirspaceName() {
        return airspaceName;
    }

    public String getAirspaceType() {
        return airspaceType;
    }

    public Integer getId() {
        return id;
    }

    public String getAirspaceFullName() {
        return airspaceFullName;
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
  
}
