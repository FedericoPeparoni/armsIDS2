package ca.ids.abms.modules.utilities.towns;

public class UtilitiesTownsAndVillageCsvExportModel {

    private String townOrVillageName;

    private String waterUtilitySchedule;

    private String commercialElectricitySchedule;

    private String residentialElectricitySchedule;

    public String getTownOrVillageName() {
        return townOrVillageName;
    }

    public void setTownOrVillageName(String townOrVillageName) {
        this.townOrVillageName = townOrVillageName;
    }

    public String getWaterUtilitySchedule() {
        return waterUtilitySchedule;
    }

    public void setWaterUtilitySchedule(String waterUtilitySchedule) {
        this.waterUtilitySchedule = waterUtilitySchedule;
    }

    public String getCommercialElectricitySchedule() {
        return commercialElectricitySchedule;
    }

    public void setCommercialElectricitySchedule(String commercialElectricitySchedule) {
        this.commercialElectricitySchedule = commercialElectricitySchedule;
    }

    public String getResidentialElectricitySchedule() {
        return residentialElectricitySchedule;
    }

    public void setResidentialElectricitySchedule(String residentialElectricitySchedule) {
        this.residentialElectricitySchedule = residentialElectricitySchedule;
    }
}
