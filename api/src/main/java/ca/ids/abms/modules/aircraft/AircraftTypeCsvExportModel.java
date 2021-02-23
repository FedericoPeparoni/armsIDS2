package ca.ids.abms.modules.aircraft;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AircraftTypeCsvExportModel {

    private String aircraftType;

    private String aircraftName;

    @CsvProperty(value = "Aircraft Manufacturer")
    private String manufacturer;

    @CsvProperty(value = "MTOW Override", mtow = true, precision = 2)
    private Double maximumTakeoffWeight;

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public void setAircraftName(String aircraftName) {
        this.aircraftName = aircraftName;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Double getMaximumTakeoffWeight() {
        return maximumTakeoffWeight;
    }

    public void setMaximumTakeoffWeight(Double maximumTakeoffWeight) {
        this.maximumTakeoffWeight = maximumTakeoffWeight;
    }
}
