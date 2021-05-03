package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@Table(name = "unified_tax_charges")
public class UnifiedTaxCharges extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {
	
	private static final long serialVersionUID = 1L;
	
	
	@Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
	
	
	@Column(name = "amount")
    private double amount;

	@Column(name = "percentage")
    private double percentage;

	
	
	
	 @ManyToOne
	    @JoinColumn(name = "aircraft_registration_id")
	private AircraftRegistration aircraftRegistration;
	 
	 @ManyToOne
	    @JoinColumn(name = "billing_ledger_id")
	private BillingLedger billingLedger;
	 
	 
	 
	
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public AircraftRegistration getAircraftRegistration() {
		return aircraftRegistration;
	}

	public void setAircraftRegistration(AircraftRegistration aircraftRegistration) {
		this.aircraftRegistration = aircraftRegistration;
	}

	public BillingLedger getBillingLedger() {
		return billingLedger;
	}

	public void setBillingLedger(BillingLedger billingLedger) {
		this.billingLedger = billingLedger;
	}

	
	
	
}
