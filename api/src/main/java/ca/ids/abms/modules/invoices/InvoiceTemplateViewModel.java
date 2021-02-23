package ca.ids.abms.modules.invoices;

import ca.ids.abms.modules.common.dto.EmbeddedFileDto;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.translation.Translation;

public class InvoiceTemplateViewModel extends EmbeddedFileDto {

    private Integer id;

    private String invoiceTemplateName;

    private InvoiceTemplateCategory invoiceCategory;

    public Integer getId() {
        return id;
    }

    public InvoiceTemplateCategory getInvoiceCategory() {
        return invoiceCategory;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setInvoiceCategory(InvoiceTemplateCategory aInvoiceCategory) {
        invoiceCategory = aInvoiceCategory;
    }

    public String getInvoiceTemplateName() {
        return invoiceTemplateName;
    }

    public void setInvoiceTemplateName(String invoiceTemplateName) {
        this.invoiceTemplateName = invoiceTemplateName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceTemplateViewModel that = (InvoiceTemplateViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return invoiceTemplateName != null ? invoiceTemplateName.equals(that.invoiceTemplateName) : that.invoiceTemplateName == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (invoiceTemplateName != null ? invoiceTemplateName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "InvoiceTemplateViewModel{" +
            "id=" + id +
            ", invoiceTemplateName='" + invoiceTemplateName + '\'' +
            ", invoiceCategory='" + invoiceCategory + '\'' +
            '}';
    }
}
