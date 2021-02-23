package ca.ids.abms.modules.aircraft;

import javax.validation.constraints.NotNull;

public class AircraftTypeComboViewModel {

    private Integer id;

    @NotNull
    private String aircraftType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }
}

