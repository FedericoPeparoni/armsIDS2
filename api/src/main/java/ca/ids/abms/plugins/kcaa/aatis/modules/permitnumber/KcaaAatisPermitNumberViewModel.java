package ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber;

import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;

import javax.validation.constraints.NotNull;

public class KcaaAatisPermitNumberViewModel {

    @NotNull
    private String invoicePermitNumber;

    @NotNull
    private ExternalDatabaseForCharge externalDatabaseForCharge;

    @NotNull
    private Double adhocTotalFeePaymentAmount;

    public String getInvoicePermitNumber() {
        return invoicePermitNumber;
    }

    public void setInvoicePermitNumber(String invoicePermitNumber) {
        this.invoicePermitNumber = invoicePermitNumber;
    }

    public ExternalDatabaseForCharge getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(ExternalDatabaseForCharge externalDatabaseForCharge) {
        this.externalDatabaseForCharge = externalDatabaseForCharge;
    }

    public Double getAdhocTotalFeePaymentAmount() {
        return adhocTotalFeePaymentAmount;
    }

    public void setAdhocTotalFeePaymentAmount(Double adhocTotalFeePaymentAmount) {
        this.adhocTotalFeePaymentAmount = adhocTotalFeePaymentAmount;
    }
}
