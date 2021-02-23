package ca.ids.abms.modules.certificates.templates;

import ca.ids.abms.modules.common.dto.EmbeddedFileDto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class CertificateTemplateViewModel extends EmbeddedFileDto {

    private Integer id;

    @NotNull
    @Size(max = 50)
    private String certificateTemplateName;

    @NotNull
    private Integer licenseDuration;

    @NotNull
    private Integer expiryWarningInterval;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CertificateTemplateViewModel that = (CertificateTemplateViewModel) o;

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
