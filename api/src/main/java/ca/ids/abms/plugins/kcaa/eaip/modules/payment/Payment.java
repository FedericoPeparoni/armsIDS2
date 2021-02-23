package ca.ids.abms.plugins.kcaa.eaip.modules.payment;

public class Payment {

    private Integer paymentId;

    private Integer paymentStatusId;

    private Integer paymentCountryId;

    private Integer paymentReqId;

    private Integer paymentArId;

    private Integer paymentManinfoId;

    private Integer paymentInvoiceId;

    private String paymentManinfoNames;

    private String paymentDocumentNo;

    private String paymentDesc;

    private Double paymentTotalAmount;

    private String paymentCurrency;

    private String paymentCreatedBy;

    private String paymentCreatedComments;

    public Integer getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Integer paymentId) {
        this.paymentId = paymentId;
    }

    public Integer getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Integer paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public Integer getPaymentCountryId() {
        return paymentCountryId;
    }

    public void setPaymentCountryId(Integer paymentCountryId) {
        this.paymentCountryId = paymentCountryId;
    }

    public Integer getPaymentReqId() {
        return paymentReqId;
    }

    public void setPaymentReqId(Integer paymentReqId) {
        this.paymentReqId = paymentReqId;
    }

    public Integer getPaymentArId() {
        return paymentArId;
    }

    public void setPaymentArId(Integer paymentArId) {
        this.paymentArId = paymentArId;
    }

    public Integer getPaymentManinfoId() {
        return paymentManinfoId;
    }

    public void setPaymentManinfoId(Integer paymentManinfoId) {
        this.paymentManinfoId = paymentManinfoId;
    }

    public Integer getPaymentInvoiceId() {
        return paymentInvoiceId;
    }

    public void setPaymentInvoiceId(Integer paymentInvoiceId) {
        this.paymentInvoiceId = paymentInvoiceId;
    }

    public String getPaymentManinfoNames() {
        return paymentManinfoNames;
    }

    public void setPaymentManinfoNames(String paymentManinfoNames) {
        this.paymentManinfoNames = paymentManinfoNames;
    }

    public String getPaymentDocumentNo() {
        return paymentDocumentNo;
    }

    public void setPaymentDocumentNo(String paymentDocumentNo) {
        this.paymentDocumentNo = paymentDocumentNo;
    }

    public String getPaymentDesc() {
        return paymentDesc;
    }

    public void setPaymentDesc(String paymentDesc) {
        this.paymentDesc = paymentDesc;
    }

    public Double getPaymentTotalAmount() {
        return paymentTotalAmount;
    }

    public void setPaymentTotalAmount(Double paymentTotalAmount) {
        this.paymentTotalAmount = paymentTotalAmount;
    }

    public String getPaymentCurrency() {
        return paymentCurrency;
    }

    public void setPaymentCurrency(String paymentCurrency) {
        this.paymentCurrency = paymentCurrency;
    }

    public String getPaymentCreatedBy() {
        return paymentCreatedBy;
    }

    public void setPaymentCreatedBy(String paymentCreatedBy) {
        this.paymentCreatedBy = paymentCreatedBy;
    }

    public String getPaymentCreatedComments() {
        return paymentCreatedComments;
    }

    public void setPaymentCreatedComments(String paymentCreatedComments) {
        this.paymentCreatedComments = paymentCreatedComments;
    }
}

