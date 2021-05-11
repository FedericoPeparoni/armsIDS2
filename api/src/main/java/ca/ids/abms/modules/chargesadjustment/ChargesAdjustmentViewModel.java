package ca.ids.abms.modules.chargesadjustment;

import java.time.LocalDateTime;

public class ChargesAdjustmentViewModel {

    private Integer id;

    private LocalDateTime date;

    private String flightId;
    
    private String registrationNumber;

    private String aerodrome;

    private String chargeDescription;

    private Double chargeAmount;

    private String externalAccountingSystemIdentifier;

    private String externalChargeCategoryName;

    private Integer transactionId;

    private Integer billingLedgerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getExternalChargeCategoryName() {
        return externalChargeCategoryName;
    }

    public void setExternalChargeCategoryName(String externalChargeCategoryName) {
        this.externalChargeCategoryName = externalChargeCategoryName;
    }

    public Integer getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getBillingLedgerId() {
        return billingLedgerId;
    }

    public void setBillingLedgerId(Integer billingLedgerId) {
        this.billingLedgerId = billingLedgerId;
    }

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

}
