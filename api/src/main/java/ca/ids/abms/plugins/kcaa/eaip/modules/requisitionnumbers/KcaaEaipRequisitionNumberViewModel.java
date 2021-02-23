package ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers;

import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;

import javax.validation.constraints.NotNull;

public class KcaaEaipRequisitionNumberViewModel {

    @NotNull
    private String reqNumber;

    @NotNull
    private ExternalDatabaseForCharge externalDatabaseForCharge;

    @NotNull
    private String reqCurrency;

    private Double reqTotalAmountConverted;

    public String getReqNumber() {
        return reqNumber;
    }

    public void setReqNumber(String reqNumber) {
        this.reqNumber = reqNumber;
    }

    public ExternalDatabaseForCharge getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(ExternalDatabaseForCharge externalDatabaseForCharge) {
        this.externalDatabaseForCharge = externalDatabaseForCharge;
    }

    public String getReqCurrency() {
        return reqCurrency;
    }

    public void setReqCurrency(String reqCurrency) {
        this.reqCurrency = reqCurrency;
    }

    public Double getReqTotalAmountConverted() {
        return reqTotalAmountConverted;
    }

    public void setReqTotalAmountConverted(Double reqTotalAmountConverted) {
        this.reqTotalAmountConverted = reqTotalAmountConverted;
    }
}
