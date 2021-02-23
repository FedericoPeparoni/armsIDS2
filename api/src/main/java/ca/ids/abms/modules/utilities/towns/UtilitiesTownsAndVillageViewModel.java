package ca.ids.abms.modules.utilities.towns;

import ca.ids.abms.modules.utilities.schedules.UtilitiesScheduleViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class UtilitiesTownsAndVillageViewModel {

    private Integer id;

    @NotNull
    @Size(max = 128)
    private String townOrVillageName;

    private UtilitiesScheduleViewModel waterUtilitySchedule;

    private UtilitiesScheduleViewModel residentialElectricityUtilitySchedule;

    private UtilitiesScheduleViewModel commercialElectricityUtilitySchedule;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTownOrVillageName() {
        return townOrVillageName;
    }

    public void setTownOrVillageName(String townOrVillageName) {
        this.townOrVillageName = townOrVillageName;
    }

    public UtilitiesScheduleViewModel getWaterUtilitySchedule() {
        return waterUtilitySchedule;
    }

    public void setWaterUtilitySchedule(UtilitiesScheduleViewModel waterUtilitySchedule) {
        this.waterUtilitySchedule = waterUtilitySchedule;
    }

    public UtilitiesScheduleViewModel getResidentialElectricityUtilitySchedule() {
        return residentialElectricityUtilitySchedule;
    }

    public void setResidentialElectricityUtilitySchedule(UtilitiesScheduleViewModel residentialElectricityUtilitySchedule) {
        this.residentialElectricityUtilitySchedule = residentialElectricityUtilitySchedule;
    }

    public UtilitiesScheduleViewModel getCommercialElectricityUtilitySchedule() {
        return commercialElectricityUtilitySchedule;
    }

    public void setCommercialElectricityUtilitySchedule(UtilitiesScheduleViewModel commercialElectricityUtilitySchedule) {
        this.commercialElectricityUtilitySchedule = commercialElectricityUtilitySchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesTownsAndVillageViewModel that = (UtilitiesTownsAndVillageViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return townOrVillageName != null ? townOrVillageName.equals(that.townOrVillageName) : that.townOrVillageName == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (townOrVillageName != null ? townOrVillageName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UtilitiesTownsAndVillageViewModel{" +
            "id=" + id +
            ", townOrVillageName='" + townOrVillageName + '\'' +
            '}';
    }
}
