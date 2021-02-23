package ca.ids.abms.modules.charges;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class ServiceChargeCatalogueCsvExportModel {

    private String chargeClass;

    private String category;

    private String type;

    private String subtype;

    private String description;

    private String currency;

    @CsvProperty(value = "Min Amount", precision = 2)
    private Double minimumAmount;

    @CsvProperty(value = "Max Amount", precision = 2)
    private Double maximumAmount;

    @CsvProperty(value = "Actual Amount", precision = 2)
    private Double amount;

    private String chargeBasis;

    private String invoiceCategory;

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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

    public String getChargeBasis() {
        return chargeBasis;
    }

    public void setChargeBasis(String chargeBasis) {
        this.chargeBasis = chargeBasis;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }
}
