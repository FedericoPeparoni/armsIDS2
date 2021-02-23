package ca.ids.abms.plugins.kcaa.erp.modules.receiptheader;

import ca.ids.abms.plugins.kcaa.erp.modules.receiptline.ReceiptLine;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "KCAA erp$Receipt Header")
@SuppressWarnings({"unused", "WeakerAccess"})
public class ReceiptHeader {

    static final String TIMESTAMP_COLUMN_NAME = "timestamp";

    @Id
    @Column(name = "No", length = 30)
    @NotNull
    private String no;

    @Column(name = "Posting Date")
    @NotNull
    private LocalDateTime postingDate;

    @Column(name = "Date Posted")
    @NotNull
    private LocalDateTime datePosted;

    @Column(name = "Currency Code", length = 20)
    @NotNull
    private String currencyCode;

    @Column(name = "Currency Factor", precision = 38, scale = 20)
    @NotNull
    private Double currencyFactor;

    @Column(name = "Bank", length = 10)
    @NotNull
    private String bank;

    @Column(name = "Posting Description", length = 250)
    @NotNull
    private String postingDescription;

    @Column(name = "AppStatus", precision = 10)
    @NotNull
    private Integer appStatus;

    @Column(name = "No_ Series", length = 10)
    @NotNull
    private String noSeries;

    @Column(name = "EnteredBy", length = 20)
    @NotNull
    private String enteredBy;

    @Column(name = "Pay mode", precision = 10)
    @NotNull
    private Integer payMode;

    @Column(name = "Cheque No_", length = 20)
    @NotNull
    private String chequeNo;

    @Column(name = "Department Code", length = 20)
    @NotNull
    private String departmentCode;

    @Column(name = "Station Code", length = 20)
    @NotNull
    private String stationCode;

    @Column(name = "Cashier ID", length = 20)
    @NotNull
    private String cashierId;

    @Column(name = "Casher Date")
    @NotNull
    private LocalDateTime casherDate;

    @Column(name = "Cashier Time")
    @NotNull
    private LocalDateTime cachierTime;

    @Column(name = "Station of Preparation", length = 20)
    @NotNull
    private String stationOfPreparation;

    @Column(name = "Department Name", length = 50)
    @NotNull
    private String departmentName;

    @Column(name = "Bank Name", length = 50)
    @NotNull
    private String bankName;

    @Column(name = "Type", precision = 10)
    @NotNull
    private Integer type;

    @Column(name = "Type No_", length = 30)
    @NotNull
    private String typeNo;

    @Column(name = "Received From", length = 50)
    @NotNull
    private String receivedFrom;

    @Column(name = "Printed", precision = 3)
    @NotNull
    private Boolean printed;

    @Transient
    private List<ReceiptLine> receiptLines;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public LocalDateTime getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(LocalDateTime postingDate) {
        this.postingDate = postingDate;
    }

    public LocalDateTime getDatePosted() {
        return datePosted;
    }

    public void setDatePosted(LocalDateTime datePosted) {
        this.datePosted = datePosted;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Double getCurrencyFactor() {
        return currencyFactor;
    }

    public void setCurrencyFactor(Double currencyFactor) {
        this.currencyFactor = currencyFactor;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getPostingDescription() {
        return postingDescription;
    }

    public void setPostingDescription(String postingDescription) {
        this.postingDescription = postingDescription;
    }

    public Integer getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(Integer appStatus) {
        this.appStatus = appStatus;
    }

    public String getNoSeries() {
        return noSeries;
    }

    public void setNoSeries(String noSeries) {
        this.noSeries = noSeries;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public Integer getPayMode() {
        return payMode;
    }

    public void setPayMode(Integer payMode) {
        this.payMode = payMode;
    }

    public String getChequeNo() {
        return chequeNo;
    }

    public void setChequeNo(String chequeNo) {
        this.chequeNo = chequeNo;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getStationCode() {
        return stationCode;
    }

    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public LocalDateTime getCasherDate() {
        return casherDate;
    }

    public void setCasherDate(LocalDateTime casherDate) {
        this.casherDate = casherDate;
    }

    public LocalDateTime getCachierTime() {
        return cachierTime;
    }

    public void setCachierTime(LocalDateTime cachierTime) {
        this.cachierTime = cachierTime;
    }

    public String getStationOfPreparation() {
        return stationOfPreparation;
    }

    public void setStationOfPreparation(String stationOfPreparation) {
        this.stationOfPreparation = stationOfPreparation;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(String typeNo) {
        this.typeNo = typeNo;
    }

    public String getReceivedFrom() {
        return receivedFrom;
    }

    public void setReceivedFrom(String receivedFrom) {
        this.receivedFrom = receivedFrom;
    }

    public Boolean getPrinted() {
        return printed;
    }

    public void setPrinted(Boolean printed) {
        this.printed = printed;
    }

    public List<ReceiptLine> getReceiptLines() {
        return receiptLines;
    }

    public void setReceiptLines(List<ReceiptLine> receiptLines) {
        this.receiptLines = receiptLines;
    }
}
