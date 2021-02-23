package ca.ids.abms.modules.certificates.templates;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames = "certificateTemplateName")
public class CertificateTemplate extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 50)
    private String certificateTemplateName;

    @NotNull
    private Integer licenseDuration;

    @NotNull
    private Integer expiryWarningInterval;

    private byte[] templateDocument;

    private String templateDocumentType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCertificateTemplateName() {
        return certificateTemplateName;
    }

    public void setCertificateTemplateName(String certificateTemplateName) {
        this.certificateTemplateName = certificateTemplateName;
    }

    public Integer getLicenseDuration() {
        return licenseDuration;
    }

    public void setLicenseDuration(Integer licenseDuration) {
        this.licenseDuration = licenseDuration;
    }

    public Integer getExpiryWarningInterval() {
        return expiryWarningInterval;
    }

    public void setExpiryWarningInterval(Integer expiryWarningInterval) {
        this.expiryWarningInterval = expiryWarningInterval;
    }

    public byte[] getTemplateDocument() {
        return templateDocument;
    }

    public void setTemplateDocument(byte[] templateDocument) {
        this.templateDocument = templateDocument;
    }

    public String getTemplateDocumentType() {
        return templateDocumentType;
    }

    public void setTemplateDocumentType(String templateDocumentType) {
        this.templateDocumentType = templateDocumentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateTemplate that = (CertificateTemplate) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "CertificateTemplate{" +
            "id=" + id +
            ", certificateTemplateName='" + certificateTemplateName + '\'' +
            ", licenseDuration=" + licenseDuration +
            ", expiryWarningInterval=" + expiryWarningInterval +
            '}';
    }
}
