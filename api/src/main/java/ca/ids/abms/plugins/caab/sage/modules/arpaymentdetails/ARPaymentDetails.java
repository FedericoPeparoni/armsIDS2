package ca.ids.abms.plugins.caab.sage.modules.arpaymentdetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "AR_PaymentDetails")
public class ARPaymentDetails implements Serializable {

    @Id
    @Column(name = "ReceiptNumber", length = 50)
    private String receiptNumber;

    @Column(name = "InvoiceNumber", length = 50)
    private String invoiceNumber;

    @Column(name = "AppliedAmount", length = 20)
    private String appliedAmount;

    @Column(name = "Flag", length = 5)
    private String flag;

    @Column(name = "UploadedOn")
    private Date uploadedOn;

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) {
        this.receiptNumber = receiptNumber;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getAppliedAmount() {
        return appliedAmount;
    }

    public void setAppliedAmount(String appliedAmount) {
        this.appliedAmount = appliedAmount;
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
