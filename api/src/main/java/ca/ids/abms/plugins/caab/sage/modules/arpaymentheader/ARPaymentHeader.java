package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "AR_PaymentHeader")
public class ARPaymentHeader implements Serializable {

    @Column(name = "CustomerCode", length = 20)
    private String customerCode;

    @Column(name = "BankCode", length = 50)
    private String bankCode;

    @Column(name = "EntryDescription", length = 50)
    private String entryDescription;

    @Column(name = "Currency", length = 20)
    private String currency;

    @Column(name = "DocumentType", length = 20)
    private String documentType;

    @Id
    @Column(name = "ReceiptNumber", length = 50)
    private String receiptNumber;

    @Column(name = "DocumentDate", length = 20)
    private String documentDate;

    @Column(name = "InvoiceNumber", length = 50)
    private String invoiceNumber;

    @Column(name = "DocumentTotal", length = 20)
    private String documentTotal;

    @Column(name = "Flag", length = 5)
    private String flag;

    @Column(name = "UploadedOn")
    private Date uploadedOn;

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getEntryDescription() {
        return entryDescription;
    }

    public void setEntryDescription(String entryDescription) {
        this.entryDescription = entryDescription;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDocumentTotal() {
        return documentTotal;
    }

    public void setDocumentTotal(String documentTotal) {
        this.documentTotal = documentTotal;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Date getUploadedOn() {
        return uploadedOn;
    }

    public void setUploadedOn(Date uploadedOn) {
        this.uploadedOn = uploadedOn;
    }
}
