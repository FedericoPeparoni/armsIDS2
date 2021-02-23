package ca.ids.abms.modules.billings;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class InvoiceLineItemCsvExportModel {

    @CsvProperty(precision = 2)
    private Double amount;

    private String category;

    private String chargeClass;

    private String description;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getChargeClass() {
        return chargeClass;
    }

    public void setChargeClass(String chargeClass) {
        this.chargeClass = chargeClass;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
