package ca.ids.abms.modules.selfcareportal.flightsearch;

import javax.persistence.Id;
import java.time.LocalDateTime;

public class SCFlightMovementViewModel {

    @Id
    private Integer id;

    private Integer accountId;

    private String accountName;

    private Double associatedAccountUsdBalance;

    private String flightId;

    private String item18RegNum;

    private LocalDateTime dateOfFlight;

    private String status;

    private String depTime;

    private String depAd;

    private String destAd;

    private Double totalChargesUsd;

    private Double amountPrepaid;

    private String flightNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Double getAssociatedAccountUsdBalance() {
        return associatedAccountUsdBalance;
    }

    public void setAssociatedAccountUsdBalance(Double associatedAccountUsdBalance) {
        this.associatedAccountUsdBalance = associatedAccountUsdBalance;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getItem18RegNum() {
        return item18RegNum;
    }

    public void setItem18RegNum(String item18RegNum) {
        this.item18RegNum = item18RegNum;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public Double getTotalChargesUsd() {
        return totalChargesUsd;
    }

    public void setTotalChargesUsd(Double totalChargesUsd) {
        this.totalChargesUsd = totalChargesUsd;
    }

    public Double getAmountPrepaid() {
        return amountPrepaid;
    }

    public void setAmountPrepaid(Double amountPrepaid) {
        this.amountPrepaid = amountPrepaid;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }
}
