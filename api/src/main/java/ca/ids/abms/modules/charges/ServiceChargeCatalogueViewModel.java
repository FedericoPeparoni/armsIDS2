package ca.ids.abms.modules.charges;

import ca.ids.abms.modules.common.enumerators.BasisForCharge;
import ca.ids.abms.modules.common.enumerators.ExternalDatabaseForCharge;
import ca.ids.abms.modules.common.enumerators.InvoiceCategory;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ServiceChargeCatalogueViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 100)
    @NotNull
    private String chargeClass;

    @Size(max = 100)
    @NotNull
    private String category;

    @Size(max = 100)
    @NotNull
    private String type;

    @Size(max = 100)
    @NotNull
    private String subtype;

    @Size(max = 100)
    @NotNull
    private String description;

    @NotNull
    private BasisForCharge chargeBasis;

    @NotNull
    private Double minimumAmount;

    @NotNull
    private Double maximumAmount;

    private Double amount;

    @NotNull
    private InvoiceCategory invoiceCategory;

    private ExternalDatabaseForCharge externalDatabaseForCharge;

    private String externalAccountingSystemIdentifier;

    private ExternalChargeCategory externalChargeCategory;

    private Currency currency;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChargeClass() {
        return chargeClass;
    }

    public void setChargeClass(String chargeClass) {
        this.chargeClass = chargeClass;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BasisForCharge getChargeBasis() {
        return chargeBasis;
    }

    public void setChargeBasis(BasisForCharge chargeBasis) {
        this.chargeBasis = chargeBasis;
    }

    public ExternalDatabaseForCharge getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(ExternalDatabaseForCharge externalDatabaseForCharge) {
        this.externalDatabaseForCharge = externalDatabaseForCharge;
    }

    public String getExternalAccountingSystemIdentifier() {
        return externalAccountingSystemIdentifier;
    }

    public void setExternalAccountingSystemIdentifier(String externalAccountingSystemIdentifier) {
        this.externalAccountingSystemIdentifier = externalAccountingSystemIdentifier;
    }

    public Double getMinimumAmount() {
        return minimumAmount;
    }

    public void setMinimumAmount(Double minimumAmount) {
        this.minimumAmount = minimumAmount;
    }

    public Double getMaximumAmount() {
        return maximumAmount;
    }

    public void setMaximumAmount(Double maximumAmount) {
        this.maximumAmount = maximumAmount;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public InvoiceCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(InvoiceCategory invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }

    public ExternalChargeCategory getExternalChargeCategory() {
        return externalChargeCategory;
    }

    public void setExternalChargeCategory(ExternalChargeCategory externalChargeCategory) {
        this.externalChargeCategory = externalChargeCategory;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServiceChargeCatalogueViewModel that = (ServiceChargeCatalogueViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "ServiceChargeCatalogueViewModel{" +
            "id=" + id +
            ", chargeClass='" + chargeClass + '\'' +
            ", category='" + category + '\'' +
            ", type='" + type + '\'' +
            ", subtype='" + subtype + '\'' +
            ", description='" + description + '\'' +
            ", basisCharge=" + chargeBasis +
            ", minimumAmount=" + minimumAmount +
            ", maximumAmount=" + maximumAmount +
            ", amount=" + amount +
            ", invoiceCategory=" + invoiceCategory + '\'' +
            ", currency=" + currency +
            '}';
    }
}
