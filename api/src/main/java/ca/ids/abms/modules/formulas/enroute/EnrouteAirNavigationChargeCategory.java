package ca.ids.abms.modules.formulas.enroute;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@UniqueKey(columnNames = "mtowCategoryUpperLimit")
public class EnrouteAirNavigationChargeCategory  extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Double mtowCategoryUpperLimit;

    @NotNull
    private String wFactorFormula;

    @JsonIgnore
    @OneToMany(mappedBy = "enrouteChargeCategory")
    @OrderBy("flightmovementCategory")
    private List<EnrouteAirNavigationChargeFormula> enrouteAirNavigationChargeFormulas = new ArrayList<>();

    private static final long serialVersionUID = 1L;

    public List<EnrouteAirNavigationChargeFormula> getEnrouteAirNavigationChargeFormulas() {
        return enrouteAirNavigationChargeFormulas;
    }

    public Integer getId() {
        return id;
    }

    public Double getMtowCategoryUpperLimit() {
        return mtowCategoryUpperLimit;
    }

    public String getwFactorFormula() {
        return wFactorFormula;
    }

    public void setEnrouteAirNavigationChargeFormulas(
            List<EnrouteAirNavigationChargeFormula> aEnrouteAirNavigationChargeFormulas) {
        enrouteAirNavigationChargeFormulas = aEnrouteAirNavigationChargeFormulas;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMtowCategoryUpperLimit(Double aMtowCategoryUpperLimit) {
        mtowCategoryUpperLimit = aMtowCategoryUpperLimit;
    }

    public void setwFactorFormula(String aWFactorFormula) {
        wFactorFormula = aWFactorFormula;
    }
}
