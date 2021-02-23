package ca.ids.abms.modules.charges;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;

@Entity
@UniqueKey(columnNames = "description")
public class ServiceChargeCatalogue extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String chargeClass;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String category;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String type;

    @Size(max = 100)
    @NotNull
    @SearchableText
    private String subtype;

    @Size(max = 100)
    @NotNull
    @Column(unique = true)
    @SearchableText
    private String description;

    @Size(max = 30)
    @NotNull
    @SearchableText
    private String chargeBasis;

    @NotNull
    private Double minimumAmount;

    @NotNull
    private Double maximumAmount;

    @MergeOnNull
    private Double amount;

    private String externalDatabaseForCharge;

    @Column(name = "external_accounting_system_identifier", length = 20)
    @SearchableText
    @Size(max = 20)
    private String externalAccountingSystemIdentifier;

    @NotNull
    @Size(max = 15)
    @SearchableText
    private String invoiceCategory;

    @ManyToOne
    @JoinColumn(name = "external_charge_category_id")
    private ExternalChargeCategory externalChargeCategory;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "currency")
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

    public String getChargeBasis() {
        return chargeBasis;
    }

    public void setChargeBasis(String chargeBasis) {
        this.chargeBasis = chargeBasis;
    }

    public String getExternalDatabaseForCharge() {
        return externalDatabaseForCharge;
    }

    public void setExternalDatabaseForCharge(String externalDatabaseForCharge) {
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

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
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

        if (!(o instanceof ServiceChargeCatalogue)) {
            return false;
        }
        ServiceChargeCatalogue that = (ServiceChargeCatalogue) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
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
            ", invoiceCategory='" + invoiceCategory + '\'' +
            ", currency=" + currency +
            '}';
    }
}
