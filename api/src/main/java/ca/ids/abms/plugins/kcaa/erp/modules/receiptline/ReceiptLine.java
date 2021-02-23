package ca.ids.abms.plugins.kcaa.erp.modules.receiptline;

import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "KCAA erp$Receipt Line")
@SuppressWarnings("unused")
public class ReceiptLine implements Serializable {

    public static final String ACCOUNT_TYPE_COLUMN_NAME = "Account Type";

    public static final String APP_STATUS_COLUMN_NAME = "AppStatus";

    public static final String APPLIES_TO_DOC_TYPE_COLUMN_NAME = "Applies-to Doc_ Type";

    public static final String NO_COLUMN_NAME = "No";

    @Id
    @Column(name = NO_COLUMN_NAME, length = 20)
    @NotNull
    private String no = DefaultValue.STRING;

    @Column(name = "Line No_", precision = 10)
    @NotNull
    private Integer lineNo = DefaultValue.INTEGER;

    @Column(name = ACCOUNT_TYPE_COLUMN_NAME, precision = 10)
    @NotNull
    private Integer accountType = DefaultValue.INTEGER;

    @Column(name = "Account No_", length = 30)
    @NotNull
    private String accountNo = DefaultValue.STRING;

    @Column(name = "Description", length = 250)
    @NotNull
    private String description = DefaultValue.STRING;

    @Column(name = "Amount", precision = 38, scale = 20)
    @NotNull
    private Double amount = DefaultValue.DOUBLE;

    @Column(name = APPLIES_TO_DOC_TYPE_COLUMN_NAME, precision = 10)
    @NotNull
    private Integer appliesToDocType = DefaultValue.INTEGER;

    @Column(name = "Applies-to Doc_ No_", length = 20)
    @NotNull
    private String appliesToDocNo = DefaultValue.STRING;

    @Column(name = "Department Code", length = 20)
    @NotNull
    private String departmentCode = DefaultValue.STRING;

    @Column(name = "Station Code", length = 20)
    @NotNull
    private String stationCode = DefaultValue.STRING;

    @Column(name = APP_STATUS_COLUMN_NAME, precision = 10)
    @NotNull
    private Integer appStatus = DefaultValue.INTEGER;

    @Column(name = "Amount LCY", precision = 38, scale = 20)
    @NotNull
    private Double amountLcy = DefaultValue.DOUBLE;

    @Column(name = "Invoice No_", length = 20)
    @NotNull
    private String invoiceNo = DefaultValue.STRING;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.no = no;
    }

    public Integer getLineNo() {
        return lineNo;
    }

    public void setLineNo(Integer lineNo) {
        this.lineNo = lineNo;
    }

    public Integer getAccountType() {
        return accountType;
    }

    public void setAccountType(Integer accountType) {
        this.accountType = accountType;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getAppliesToDocType() {
        return appliesToDocType;
    }

    public void setAppliesToDocType(Integer appliesToDocType) {
        this.appliesToDocType = appliesToDocType;
    }

    public String getAppliesToDocNo() {
        return appliesToDocNo;
    }

    public void setAppliesToDocNo(String appliesToDocNo) {
        this.appliesToDocNo = appliesToDocNo;
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

    public Integer getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(Integer appStatus) {
        this.appStatus = appStatus;
    }

    public Double getAmountLcy() {
        return amountLcy;
    }

    public void setAmountLcy(Double amountLcy) {
        this.amountLcy = amountLcy;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}
