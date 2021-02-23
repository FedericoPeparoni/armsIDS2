package ca.ids.abms.modules.formulas.enroute;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class EnrouteAirNavigationChargeFormula extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @NotNull
    @JoinColumn(name="enroute_charge_category_id")
    private EnrouteAirNavigationChargeCategory enrouteChargeCategory;
    
    @ManyToOne
    @NotNull
    @JoinColumn(name="flightmovement_category_id")
    private FlightmovementCategory flightmovementCategory;
    
    private String formula;
    
    private String dFactorFormula;

    private static final long serialVersionUID = 1L;

    public String getdFactorFormula() {
        return dFactorFormula;
    }

    public FlightmovementCategory getFlightmovementCategory() {
        return flightmovementCategory;
    }

    public String getFormula() {
        return formula;
    }

    public Integer getId() {
        return id;
    }

    public void setdFactorFormula(String aDFactorFormula) {
        dFactorFormula = aDFactorFormula;
    }

    public void setFlightmovementCategory(FlightmovementCategory aFlightmovementCategory) {
        flightmovementCategory = aFlightmovementCategory;
    }

    public void setFormula(String aFormula) {
        formula = aFormula;
    }

    public void setId(Integer aId) {
        id = aId;
    }

	public EnrouteAirNavigationChargeCategory getEnrouteChargeCategory() {
		return enrouteChargeCategory;
	}

	public void setEnrouteChargeCategory(EnrouteAirNavigationChargeCategory enrouteChargeCategory) {
		this.enrouteChargeCategory = enrouteChargeCategory;
	}
    
}
