package ca.ids.abms.modules.billings;

import javax.validation.constraints.NotNull;

import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumberViewModel;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumberViewModel;
import com.fasterxml.jackson.annotation.JsonInclude;

import ca.ids.abms.modules.aerodromes.AerodromeViewModel;
import ca.ids.abms.modules.charges.RecurringChargeViewModel;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueViewModel;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageViewModel;

@JsonInclude(JsonInclude.Include.ALWAYS)
public class InvoiceLineItemViewModel {

    @NotNull
    private AerodromeViewModel aerodrome;

    @NotNull
    private ServiceChargeCatalogueViewModel serviceChargeCatalogue;

    @NotNull
    private RecurringChargeViewModel recurringCharge;

    private String accountExternalSystemIdentifier;

    private String userDescription;

    @NotNull
    private Double amount;

    // These should be entered by user when creating a line item; each is used by
    // different service charge catalogue basis values
    private Double userUnitAmount;                // for basis == "unit"; unspecified units
    private Double userMarkupAmount;              // for basis == "percentage"; ANSP currency
    private Double userPrice;                     // for basis == "user"; ANSP currency
    private Double userElectricityMeterReading;   // for basis == "electricity"; unspecified units
    private Double userWaterMeterReading;         // for basis == "water"; unspecified units
    private Double userDiscountPercentage;        // for basis == "discount"; percent

    private UtilitiesTownsAndVillageViewModel userTown; // for basis == "electricity" || basis == "water"

    private KcaaAatisPermitNumberViewModel invoicePermit;

    private KcaaEaipRequisitionNumberViewModel requisition;

    public KcaaAatisPermitNumberViewModel getInvoicePermit() {
        return invoicePermit;
    }

    public void setInvoicePermit(KcaaAatisPermitNumberViewModel invoicePermit) {
        this.invoicePermit = invoicePermit;
    }

    public KcaaEaipRequisitionNumberViewModel getRequisition() {
        return requisition;
    }

    public void setRequisition(KcaaEaipRequisitionNumberViewModel requisition) {
        this.requisition = requisition;
    }

    public AerodromeViewModel getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(AerodromeViewModel aerodrome) {
        this.aerodrome = aerodrome;
    }

    public ServiceChargeCatalogueViewModel getServiceChargeCatalogue() {
        return serviceChargeCatalogue;
    }

    public void setServiceChargeCatalogue(ServiceChargeCatalogueViewModel serviceChargeCatalogue) {
        this.serviceChargeCatalogue = serviceChargeCatalogue;
    }

    public RecurringChargeViewModel getRecurringCharge() {
        return recurringCharge;
    }

    public void setRecurringCharge(RecurringChargeViewModel recurringChargeViewModel) {
        this.recurringCharge = recurringChargeViewModel;
    }

    public String getAccountExternalSystemIdentifier() {
        return accountExternalSystemIdentifier;
    }

    public void setAccountExternalSystemIdentifier(String accountExternalSystemIdentifier) {
        this.accountExternalSystemIdentifier = accountExternalSystemIdentifier;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getUserUnitAmount() {
        return userUnitAmount;
    }

    public void setUserUnitAmount(Double userUnitAmount) {
        this.userUnitAmount = userUnitAmount;
    }

    public Double getUserMarkupAmount() {
        return userMarkupAmount;
    }

    public void setUserMarkupAmount(Double userPercentage) {
        this.userMarkupAmount = userPercentage;
    }

    public Double getUserPrice() {
        return userPrice;
    }

    public void setUserPrice(Double userPrice) {
        this.userPrice = userPrice;
    }

    public Double getUserElectricityMeterReading() {
        return userElectricityMeterReading;
    }

    public void setUserElectricityMeterReading(Double userElectricityMeterReading) {
        this.userElectricityMeterReading = userElectricityMeterReading;
    }

    public Double getUserWaterMeterReading() {
        return userWaterMeterReading;
    }

    public void setUserWaterMeterReading(Double userWaterMeterReading) {
        this.userWaterMeterReading = userWaterMeterReading;
    }

    public Double getUserDiscountPercentage() {
        return userDiscountPercentage;
    }

    public void setUserDiscountPercentage(Double userDiscountPercentage) {
        this.userDiscountPercentage = userDiscountPercentage;
    }

    public UtilitiesTownsAndVillageViewModel getUserTown() {
        return userTown;
    }

    public void setUserTown(UtilitiesTownsAndVillageViewModel userTown) {
        this.userTown = userTown;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }
}
