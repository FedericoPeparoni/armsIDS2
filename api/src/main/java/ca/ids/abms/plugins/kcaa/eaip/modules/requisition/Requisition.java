package ca.ids.abms.plugins.kcaa.eaip.modules.requisition;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Requisition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer reqId;

    private String reqNo;

    private Integer reqStatusId;

    private Double reqTotalAmount;

    private Double reqTotalAmountConverted;

    private String reqCurrency;

    private Integer reqCountryId;

    private Integer reqArId;

    private Integer reqManinfoId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReqNo() {
        return reqNo;
    }

    public void setReqNo(String reqNo) {
        this.reqNo = reqNo;
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

