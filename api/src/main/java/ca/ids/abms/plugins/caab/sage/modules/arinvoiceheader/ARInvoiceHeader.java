package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "AR_InvoiceHeader")
public class ARInvoiceHeader implements Serializable {

    @Column(name = "CustomerCode", length = 20)
    private String customerCode;

    @Column(name = "InvoiceType", length = 20)
    private String invoiceType;

    @Column(name = "DocumentType", length = 20)
    private String documentType;

    @Column(name = "DocumentDate", length = 20)
    private String documentDate;

    @Column(name = "Currency", length = 20)
    private String currency;

    @Column(name = "DocumentDescription", length = 250)
    private String documentDescription;

    @Id
    @Column(name = "DocumentNumber", length = 50)
    private String documentNumber;

    @Column(name = "SpecialInstructions", length = 250)
    private String specialInstructions;

    @Column(name = "PONumber", length = 50)
    private String poNumber;

    @Column(name = "OrderNumber", length = 50)
    private String orderNumber;

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

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(String documentDate) {
        this.documentDate = documentDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDocumentDescription() {
        return documentDescription;
    }

    public void setDocumentDescription(String documentDescription) {
        this.documentDescription = documentDescription;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
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
