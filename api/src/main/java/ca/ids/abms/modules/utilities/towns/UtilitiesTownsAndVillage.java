package ca.ids.abms.modules.utilities.towns;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.AuditedEntity;
import ca.ids.abms.modules.utilities.schedules.UtilitiesSchedule;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames = "townOrVillageName")
public class UtilitiesTownsAndVillage  extends AuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 128)
    @SearchableText
    private String townOrVillageName;

    @ManyToOne
    @JoinColumn(name = "water_utility_schedule")
    @NotNull
    private UtilitiesSchedule waterUtilitySchedule;

    @ManyToOne
    @JoinColumn(name = "residential_electricity_utility_schedule")
    @NotNull
    private UtilitiesSchedule residentialElectricityUtilitySchedule;

    @ManyToOne
    @JoinColumn(name = "commercial_electricity_utility_schedule")
    @NotNull
    private UtilitiesSchedule commercialElectricityUtilitySchedule;

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

    public UtilitiesSchedule getWaterUtilitySchedule() {
        return waterUtilitySchedule;
    }

    public void setWaterUtilitySchedule(UtilitiesSchedule waterUtilitySchedule) {
        this.waterUtilitySchedule = waterUtilitySchedule;
    }

    public UtilitiesSchedule getResidentialElectricityUtilitySchedule() {
        return residentialElectricityUtilitySchedule;
    }

    public void setResidentialElectricityUtilitySchedule(UtilitiesSchedule residentialElectricityUtilitySchedule) {
        this.residentialElectricityUtilitySchedule = residentialElectricityUtilitySchedule;
    }

    public UtilitiesSchedule getCommercialElectricityUtilitySchedule() {
        return commercialElectricityUtilitySchedule;
    }

    public void setCommercialElectricityUtilitySchedule(UtilitiesSchedule commercialElectricityUtilitySchedule) {
        this.commercialElectricityUtilitySchedule = commercialElectricityUtilitySchedule;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesTownsAndVillage that = (UtilitiesTownsAndVillage) o;

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
        return "UtilitiesTownsAndVillage{" +
            "id=" + id +
            ", townOrVillageName='" + townOrVillageName + '\'' +
            '}';
    }
}
