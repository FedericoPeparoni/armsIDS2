package ca.ids.abms.modules.invoices;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames = "invoiceCategory")
public class InvoiceTemplate extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Size(max = 100)
    @Column(unique = true)
    @NotNull
    private String invoiceTemplateName;

    @Size(max = 50)
    @NotNull
    private String invoiceCategory;

    @Size(max = 128)
    @NotNull
    private String invoiceFilename;

    @NotNull
    private byte[] templateDocument;

    @Size(max = 100)
    @NotNull
    private String mimeType;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        InvoiceTemplate that = (InvoiceTemplate) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Integer getId() {
        return id;
    }

    public String getInvoiceCategory() {
        return invoiceCategory;
    }

    public String getInvoiceTemplateName() {
        return invoiceTemplateName;
    }

    public byte[] getTemplateDocument() {
        return templateDocument;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setInvoiceCategory(String aInvoiceCategory) {
        invoiceCategory = aInvoiceCategory;
    }

    public void setInvoiceTemplateName(String aInvoiceTemplateName) {
        invoiceTemplateName = aInvoiceTemplateName;
    }

    public String getInvoiceFilename() {
        return invoiceFilename;
    }

    public void setInvoiceFilename(String invoiceFilename) {
        this.invoiceFilename = invoiceFilename;
    }

    public void setTemplateDocument(byte[] aTemplateDocument) {
        templateDocument = aTemplateDocument;
    }

    @Override
    public String toString() {
        return "InvoiceTemplate [id=" + id + ", invoiceTemplateName=" + invoiceTemplateName + ", invoiceCategory="
                + invoiceCategory + ", templateDocument size=" + templateDocument.length + "]";
    }
}
