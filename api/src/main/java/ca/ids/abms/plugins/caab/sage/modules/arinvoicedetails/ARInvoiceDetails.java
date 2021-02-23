package ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.sql.Date;

@Entity
@Table(name = "AR_InvoiceDetails")
public class ARInvoiceDetails implements Serializable {

    @Column(name = "ItemCode", length = 50)
    private String itemCode;

    @Column(name = "DistributionCode", length = 50)
    private String distributionCode;

    @Id
    @Column(name = "DocumentNumber", length = 50)
    private String documentNumber;

    @Column(name = "UOM", length = 50)
    private String uom;

    @Column(name = "Quantity", length = 20)
    private String quantity;

    @Column(name = "UnitPrice", length = 20)
    private String unitPrice;

    @Column(name = "RevenueAccount", length = 50)
    private String revenueAccount;

    @Column(name = "Amount", length = 20)
    private String amount;

    @Column(name = "Comments", length = 250)
    private String comments;

    @Column(name = "Descriptions", length = 250)
    private String descriptions;

    @Column(name = "Flag", length = 5)
    private String flag;

    @Column(name = "UploadedOn")
    private Date uploadedOn;

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getDistributionCode() {
        return distributionCode;
    }

    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getRevenueAccount() {
        return revenueAccount;
    }

    public void setRevenueAccount(String revenueAccount) {
        this.revenueAccount = revenueAccount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
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
