package ca.ids.abms.modules.pendingtransactions;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class PendingChargeAdjustmentViewModel {

    private Integer id;

    @NotNull
    private PendingTransactionViewModel pendingTransaction;

    @NotNull
    private String invoiceType;

    /* Field for aviation and non-aviation invoice */
    @NotNull
    private LocalDateTime date;

    /* Field for aviation invoice only */
    private String flightId;
    
    /* Field for unified-tax invoice only */
    private String registrationNumber;

    /* Field for non-aviation invoice only */
    private String aerodrome;

    /* Field for aviation and non-aviation invoice */
    @NotNull
    private String chargeDescription;

    /* Field for aviation and non-aviation invoice */
    @NotNull
    private Double chargeAmount;

    private String externalAccountingSystemIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PendingTransactionViewModel getPendingTransaction() {
        return pendingTransaction;
    }

    public void setPendingTransaction(PendingTransactionViewModel pendingTransaction) {
        this.pendingTransaction = pendingTransaction;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

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
    

    public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
        this.aerodrome = aerodrome;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PendingChargeAdjustmentViewModel that = (PendingChargeAdjustmentViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (chargeDescription != null ? !chargeDescription.equals(that.chargeDescription) : that.chargeDescription != null)
            return false;
        return chargeAmount != null ? chargeAmount.equals(that.chargeAmount) : that.chargeAmount == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (chargeDescription != null ? chargeDescription.hashCode() : 0);
        result = 31 * result + (chargeAmount != null ? chargeAmount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PendingChargeAdjustmentViewModel{" +
            "id=" + id +
            ", pendingTransaction=" + pendingTransaction +
            ", invoiceType='" + invoiceType + '\'' +
            ", date=" + date +
            ", flightId='" + flightId + '\'' +
            ", aerodrome='" + aerodrome + '\'' +
            ", chargeDescription='" + chargeDescription + '\'' +
            ", chargeAmount=" + chargeAmount +
            '}';
    }
}
