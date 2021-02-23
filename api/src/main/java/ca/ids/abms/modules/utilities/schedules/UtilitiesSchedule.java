package ca.ids.abms.modules.utilities.schedules;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
public class UtilitiesSchedule extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @SearchableText
    private ScheduleType scheduleType;

    @NotNull
    private Integer  minimumCharge;

    @JsonIgnore
    @OneToMany(mappedBy = "schedule", fetch = FetchType.EAGER)
    private Set<UtilitiesRangeBracket> utilitiesRangeBracket = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "waterUtilitySchedule", fetch = FetchType.EAGER)
    private Set<UtilitiesTownsAndVillage> utilitiesWaterTownsAndVillage = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "commercialElectricityUtilitySchedule", fetch = FetchType.EAGER)
    private Set<UtilitiesTownsAndVillage> commercialElectricityUtilitySchedule = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "residentialElectricityUtilitySchedule", fetch = FetchType.EAGER)
    private Set<UtilitiesTownsAndVillage> residentialElectricityUtilitySchedule = new HashSet<>();

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

    public Set<UtilitiesRangeBracket> getUtilitiesRangeBracket() {
        return utilitiesRangeBracket;
    }

    public void setUtilitiesRangeBracket(Set<UtilitiesRangeBracket> utilitiesRangeBracket) {
        this.utilitiesRangeBracket = utilitiesRangeBracket;
    }

    public Set<UtilitiesTownsAndVillage> getUtilitiesWaterTownsAndVillage() {
        return utilitiesWaterTownsAndVillage;
    }

    public void setUtilitiesWaterTownsAndVillage(Set<UtilitiesTownsAndVillage> utilitiesWaterTownsAndVillage) {
        this.utilitiesWaterTownsAndVillage = utilitiesWaterTownsAndVillage;
    }

    public Set<UtilitiesTownsAndVillage> getResidentialElectricityUtilitySchedule() {
        return residentialElectricityUtilitySchedule;
    }

    public void setResidentialElectricityUtilitySchedule(Set<UtilitiesTownsAndVillage> residentialElectricityUtilitySchedule) {
        this.residentialElectricityUtilitySchedule = residentialElectricityUtilitySchedule;
    }

    public Set<UtilitiesTownsAndVillage> getCommercialElectricityUtilitySchedule() {
        return commercialElectricityUtilitySchedule;
    }

    public void setCommercialElectricityUtilitySchedule(Set<UtilitiesTownsAndVillage> commercialElectricityUtilitySchedule) {
        this.commercialElectricityUtilitySchedule = commercialElectricityUtilitySchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesSchedule that = (UtilitiesSchedule) o;

        return scheduleId != null ? scheduleId.equals(that.scheduleId) : that.scheduleId == null;
    }

    @Override
    public int hashCode() {
        return scheduleId != null ? scheduleId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "UtilitiesSchedule{" +
                "scheduleId=" + scheduleId +
                ", scheduleType=" + scheduleType +
                ", minimumCharge=" + minimumCharge +
                '}';
    }
}
