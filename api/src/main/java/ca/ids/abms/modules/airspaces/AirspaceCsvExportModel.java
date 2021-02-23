package ca.ids.abms.modules.airspaces;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AirspaceCsvExportModel {

    @CsvProperty(value = "Identifier")
    private String airspaceName;

    @CsvProperty(value = "Name")
    private String airspaceFullName;

    @CsvProperty(value = "Type")
    private String airspaceType;

    @CsvProperty(value = "Included")
    private Boolean airspaceIncluded;
    
    @CsvProperty(value = "Ceiling", precision = 2)
    private Double airspaceCeiling;

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

    public String getAirspaceName() {
        return airspaceName;
    }

    public void setAirspaceName(String airspaceName) {
        this.airspaceName = airspaceName;
    }

    public String getAirspaceFullName() {
        return airspaceFullName;
    }

    public void setAirspaceFullName(String airspaceFullName) {
        this.airspaceFullName = airspaceFullName;
    }

    public String getAirspaceType() {
        return airspaceType;
    }

    public void setAirspaceType(String airspaceType) {
        this.airspaceType = airspaceType;
    }
}
