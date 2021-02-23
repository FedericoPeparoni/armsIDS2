package ca.ids.abms.modules.utilities.schedules;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.VersionedViewModel;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageViewModel;

public class UtilitiesScheduleViewModel extends VersionedViewModel {

    private Integer scheduleId;

    @NotNull
    private ScheduleType scheduleType;

    @NotNull
    private Integer  minimumCharge;

    private Set<UtilitiesRangeBracketViewModel> utilitiesRangeBracket = new HashSet<>();

    private Set<UtilitiesTownsAndVillageViewModel> utilitiesWaterTownsAndVillage = new HashSet<>();

    private Set<UtilitiesTownsAndVillageViewModel> residentialElectricityUtilitySchedule = new HashSet<>();

    private Set<UtilitiesTownsAndVillageViewModel> commercialElectricityUtilitySchedule = new HashSet<>();

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public ScheduleType getScheduleType() {
        return scheduleType;
    }

    public void setScheduleType(ScheduleType scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(Integer minimumCharge) {
        this.minimumCharge = minimumCharge;
    }

    public Set<UtilitiesRangeBracketViewModel> getUtilitiesRangeBracket() {
        return utilitiesRangeBracket;
    }

    public void setUtilitiesRangeBracket(Set<UtilitiesRangeBracketViewModel> utilitiesRangeBracket) {
        this.utilitiesRangeBracket = utilitiesRangeBracket;
    }

    public Set<UtilitiesTownsAndVillageViewModel> getUtilitiesWaterTownsAndVillage() {
        return utilitiesWaterTownsAndVillage;
    }

    public void setUtilitiesWaterTownsAndVillage(Set<UtilitiesTownsAndVillageViewModel> utilitiesWaterTownsAndVillage) {
        this.utilitiesWaterTownsAndVillage = utilitiesWaterTownsAndVillage;
    }

    public Set<UtilitiesTownsAndVillageViewModel> getResidentialElectricityUtilitySchedule() {
        return residentialElectricityUtilitySchedule;
    }

    public void setResidentialElectricityUtilitySchedule(Set<UtilitiesTownsAndVillageViewModel> residentialElectricityUtilitySchedule) {
        this.residentialElectricityUtilitySchedule = residentialElectricityUtilitySchedule;
    }

    public Set<UtilitiesTownsAndVillageViewModel> getCommercialElectricityUtilitySchedule() {
        return commercialElectricityUtilitySchedule;
    }

    public void setCommercialElectricityUtilitySchedule(Set<UtilitiesTownsAndVillageViewModel> commercialElectricityUtilitySchedule) {
        this.commercialElectricityUtilitySchedule = commercialElectricityUtilitySchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesScheduleViewModel that = (UtilitiesScheduleViewModel) o;

        return scheduleId != null ? scheduleId.equals(that.scheduleId) : that.scheduleId == null;

    }

    @Override
    public int hashCode() {
        return scheduleId != null ? scheduleId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UtilitiesScheduleViewModel{" +
                "scheduleId=" + scheduleId +
                ", scheduleType=" + scheduleType +
                ", minimumCharge=" + minimumCharge +
                '}';
    }
}
