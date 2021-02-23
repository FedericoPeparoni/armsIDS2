package ca.ids.abms.modules.invoices;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class InvoiceTemplateCsvExportModel {

    @CsvProperty(value = "Invoice Name")
    private String invoiceTemplateName;

    private String invoiceCategory;

    public String getInvoiceTemplateName() {
        return invoiceTemplateName;
    }

    public void setInvoiceTemplateName(String invoiceTemplateName) {
        this.invoiceTemplateName = invoiceTemplateName;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setInvoiceCategory(String invoiceCategory) {
        this.invoiceCategory = invoiceCategory;
    }
}
