package ca.ids.abms.modules.chargesadjustment;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.transactions.Transaction;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "charges_adjustments")
public class ChargesAdjustment implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /* Field for aviation and non-aviation invoice */
    private LocalDateTime date;

    /* Field for aviation invoice only */
    private String flightId;
    
    /* Field for unified-tax invoice only */
    private String registrationNumber;

    /* Field for non-aviation invoice only */
    private String aerodrome;

    /* Field for aviation and non-aviation invoice */
    private String chargeDescription;

    /* Field for aviation and non-aviation invoice */
    private Double chargeAmount;

    /* Field for non-aviation invoice only */
    @Column(name = "external_accounting_system_identifier", length = 20)
    @Size(max = 20)
    private String externalAccountingSystemIdentifier;

    /* Field for non-aviation invoice only */
    @Column(name = "external_charge_category_name", length = 50)
    @Size(max = 50)
    private String externalChargeCategoryName;

    /* Persisted so it can be applied after approval (CREDIT NOTE) */
    @ManyToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    /* Persisted so it can be applied after approval (DEBIT NOTE) */
    @ManyToOne
    @JoinColumn(name = "billing_ledger_id")
    private BillingLedger billingLedger;

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getChargeDescription() {
        return chargeDescription;
    }

    public void setChargeDescription(String chargeDescription) {
        this.chargeDescription = chargeDescription;
    }

    public Double getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(Double chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }

    public String getExternalChargeCategoryName() {
        return externalChargeCategoryName;
    }

    public void setExternalChargeCategoryName(String externalChargeCategoryName) {
        this.externalChargeCategoryName = externalChargeCategoryName;
    }

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BillingLedger getBillingLedger() {
        return billingLedger;
    }

    public void setBillingLedger(BillingLedger billingLedger) {
        this.billingLedger = billingLedger;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChargesAdjustment that = (ChargesAdjustment) o;

        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (flightId != null ? !flightId.equals(that.flightId) : that.flightId != null) return false;
        if (aerodrome != null ? !aerodrome.equals(that.aerodrome) : that.aerodrome != null)
            return false;
        if (chargeDescription != null ? !chargeDescription.equals(that.chargeDescription) : that.chargeDescription != null)
            return false;
        return chargeAmount != null ? chargeAmount.equals(that.chargeAmount) : that.chargeAmount == null;
    }

  

    @Override
    public String toString() {
        return "ChargesAdjustment{" +
            "id='" + id + '\'' +
            ", date='" + date + '\'' +
            ", flightId='" + flightId + '\'' +
            ", aerodrome='" + aerodrome + '\'' +
            ", chargeDescription='" + chargeDescription + '\'' +
            ", chargeAmount=" + chargeAmount +
            '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}
}
