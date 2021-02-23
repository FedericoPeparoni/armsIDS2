package ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit;

import java.time.LocalDate;

public class InvoicePermit {

    private String adhocFeePaid;

    private Integer adhocStatusId;

    private String adhocPermitNumber;

    private LocalDate adhocAdhocFeePaymentDate;

    private String adhocAdhocFeePaymentBy;

    private String adhocAdhocFeePaymentNo;

    private Double adhocAdhocFeePaymentAmount;

    private String adhocAdhocFeePaymentComments;

    private Double adhocTotalFeePaymentAmount;

    public Double getAdhocFee() {
        return adhocFee;
    }

    public void setAdhocFee(Double adhocFee) {
        this.adhocFee = adhocFee;
    }

    private Double adhocFee;

    private String adhocFeeCurrencyCode;

    private Double adhocFeeConverted;

    private String adhocFeeConvertedCurrencyCode;

    public String getAdhocFeePaid() {
        return adhocFeePaid;
    }

    public void setAdhocFeePaid(String adhocFeePaid) {
        this.adhocFeePaid = adhocFeePaid;
    }

    public Integer getAdhocStatusId() {
        return adhocStatusId;
    }

    public void setAdhocStatusId(Integer adhocStatusId) {
        this.adhocStatusId = adhocStatusId;
    }

    public String getAdhocPermitNumber() {
        return adhocPermitNumber;
    }

    public void setAdhocPermitNumber(String adhocPermitNumber) {
        this.adhocPermitNumber = adhocPermitNumber;
    }

    public LocalDate getAdhocAdhocFeePaymentDate() {
        return adhocAdhocFeePaymentDate;
    }

    public void setAdhocAdhocFeePaymentDate(LocalDate adhocAdhocFeePaymentDate) {
        this.adhocAdhocFeePaymentDate = adhocAdhocFeePaymentDate;
    }

    public String getAdhocAdhocFeePaymentBy() {
        return adhocAdhocFeePaymentBy;
    }

    public void setAdhocAdhocFeePaymentBy(String adhocAdhocFeePaymentBy) {
        this.adhocAdhocFeePaymentBy = adhocAdhocFeePaymentBy;
    }

    public String getAdhocAdhocFeePaymentNo() {
        return adhocAdhocFeePaymentNo;
    }

    public void setAdhocAdhocFeePaymentNo(String adhocAdhocFeePaymentNo) {
        this.adhocAdhocFeePaymentNo = adhocAdhocFeePaymentNo;
    }

    public Double getAdhocAdhocFeePaymentAmount() {
        return adhocAdhocFeePaymentAmount;
    }

    public void setAdhocAdhocFeePaymentAmount(Double adhocAdhocFeePaymountAmount) {
        this.adhocAdhocFeePaymentAmount = adhocAdhocFeePaymountAmount;
    }

    public String getAdhocAdhocFeePaymentComments() {
        return adhocAdhocFeePaymentComments;
    }

    public void setAdhocAdhocFeePaymentComments(String adhocAdhocFeePaymentComments) {
        this.adhocAdhocFeePaymentComments = adhocAdhocFeePaymentComments;
    }

    public Double getAdhocTotalFeePaymentAmount() {
        return adhocTotalFeePaymentAmount;
    }

    public void setAdhocTotalFeePaymentAmount(Double adhocTotalFeePaymentAmount) {
        this.adhocTotalFeePaymentAmount = adhocTotalFeePaymentAmount;
    }

    public Double getAdhocFeeConverted() {
        return adhocFeeConverted;
    }

    public void setAdhocFeeConverted(Double adhocFeeConverted) {
        this.adhocFeeConverted = adhocFeeConverted;
    }

    public String getAdhocFeeCurrencyCode() {
        return adhocFeeCurrencyCode;
    }

    public void setAdhocFeeCurrencyCode(String adhocFeeCurrencyCode) {
        this.adhocFeeCurrencyCode = adhocFeeCurrencyCode;
    }

    public String getAdhocFeeConvertedCurrencyCode() {
        return adhocFeeConvertedCurrencyCode;
    }

    public void setAdhocFeeConvertedCurrencyCode(String adhocFeeConvertedCurrencyCode) {
        this.adhocFeeConvertedCurrencyCode = adhocFeeConvertedCurrencyCode;
    }
}

