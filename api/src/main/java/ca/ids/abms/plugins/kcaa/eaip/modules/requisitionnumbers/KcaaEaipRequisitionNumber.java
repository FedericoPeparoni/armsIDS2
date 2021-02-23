package ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "kcaa_eaip_requisition_numbers")
public class KcaaEaipRequisitionNumber extends AuditedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "req_number", unique = true)
    @NotNull
    private String reqNumber;

    @Column(name = "external_database_for_charge")
    @NotNull
    private ExternalDatabaseForCharge externalDatabaseForCharge;

    @ManyToOne
    @JoinColumn(name = "billing_ledger_id")
    private BillingLedger billingLedger;

    @Column(name = "req_currency")
    @NotNull
    private String reqCurrency;

    @Column(name = "req_id")
    private Integer reqId;

    @Column(name = "req_status_id")
    private Integer reqStatusId;

    @Column(name = "req_total_amount")
    private Double reqTotalAmount;

    @Column(name = "req_total_amount_converted")
    private Double reqTotalAmountConverted;

    @Column(name = "req_country_id")
    private Integer reqCountryId;

    @Column(name = "req_ar_id")
    private Integer reqArId;

    @Column(name = "req_maninfo_id")
    private Integer reqManinfoId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReqNumber() {
        return reqNumber;
    }

    public void setReqNumber(String reqNumber) {
        this.reqNumber = reqNumber;
    }

    public void setInvoicePermitNumber(String reqNumber) {
        this.reqNumber = reqNumber;
    }

    public ExternalDatabaseForCharge getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(ExternalDatabaseForCharge externalDatabaseForCharge) {
        this.externalDatabaseForCharge = externalDatabaseForCharge;
    }

    public BillingLedger getBillingLedger() {
        return billingLedger;
    }

    public void setBillingLedger(BillingLedger billingLedger) {
        this.billingLedger = billingLedger;
    }

    public String getReqCurrency() {
        return reqCurrency;
    }

    public void setReqCurrency(String reqCurrency) {
        this.reqCurrency = reqCurrency;
    }

    public Integer getReqId() {
        return reqId;
    }

    public void setReqId(Integer reqId) {
        this.reqId = reqId;
    }

    public Integer getReqStatusId() {
        return reqStatusId;
    }

    public void setReqStatusId(Integer reqStatusId) {
        this.reqStatusId = reqStatusId;
    }

    public Double getReqTotalAmount() {
        return reqTotalAmount;
    }

    public void setReqTotalAmount(Double reqTotalAmount) {
        this.reqTotalAmount = reqTotalAmount;
    }

    public Double getReqTotalAmountConverted() {
        return reqTotalAmountConverted;
    }

    public void setReqTotalAmountConverted(Double reqTotalAmountConverted) {
        this.reqTotalAmountConverted = reqTotalAmountConverted;
    }
    public Integer getReqCountryId() {
        return reqCountryId;
    }

    public void setReqCountryId(Integer reqCountryId) {
        this.reqCountryId = reqCountryId;
    }

    public Integer getReqArId() {
        return reqArId;
    }

    public void setReqArId(Integer reqArId) {
        this.reqArId = reqArId;
    }

    public Integer getReqManinfoId() {
        return reqManinfoId;
    }

    public void setReqManinfoId(Integer reqManinfoId) {
        this.reqManinfoId = reqManinfoId;
    }

}

