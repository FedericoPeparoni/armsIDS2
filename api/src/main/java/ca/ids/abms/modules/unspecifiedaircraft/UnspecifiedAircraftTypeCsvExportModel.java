package ca.ids.abms.modules.unspecifiedaircraft;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class UnspecifiedAircraftTypeCsvExportModel {

    private String textIdentifier;

    private String name;

    private String aircraftType;

    @CsvProperty(value = "MTOW", mtow = true, precision = 2)
    private Double mtow;

    private String status;

    public String getTextIdentifier() {
        return textIdentifier;
    }

    public void setTextIdentifier(String textIdentifier) {
        this.textIdentifier = textIdentifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public Double getMtow() {
        return mtow;
    }

    public void setMtow(Double mtow) {
        this.mtow = mtow;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
