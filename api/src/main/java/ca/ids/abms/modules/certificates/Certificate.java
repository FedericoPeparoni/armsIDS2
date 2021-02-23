package ca.ids.abms.modules.certificates;

import ca.ids.abms.modules.certificates.templates.CertificateTemplate;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class Certificate extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "certificate_type_id")
    @NotNull
    private CertificateTemplate certificateTemplate;

    @NotNull
    @Size(max = 100)
    private String certifiedOrganizationOrPerson;

    private byte[] certificateImage;

    private String certificateImageType;

    private Boolean expiryWarningIssued;

    @NotNull
    private LocalDateTime dateOfIssue;

    @NotNull
    private LocalDateTime dateOfExpiryWarning;

    @NotNull
    private LocalDateTime dateOfExpiry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public CertificateTemplate getCertificateTemplate() {
        return certificateTemplate;
    }

    public void setCertificateTemplate(CertificateTemplate certificateTemplate) {
        this.certificateTemplate = certificateTemplate;
    }

    public String getCertifiedOrganizationOrPerson() {
        return certifiedOrganizationOrPerson;
    }

    public void setCertifiedOrganizationOrPerson(String certifiedOrganizationOrPerson) {
        this.certifiedOrganizationOrPerson = certifiedOrganizationOrPerson;
    }

    public byte[] getCertificateImage() {
        return certificateImage;
    }

    public void setCertificateImage(byte[] certificateImage) {
        this.certificateImage = certificateImage;
    }

    public String getCertificateImageType() {
        return certificateImageType;
    }

    public void setCertificateImageType(String certificateImageType) {
        this.certificateImageType = certificateImageType;
    }

    public Boolean getExpiryWarningIssued() {
        return expiryWarningIssued;
    }

    public void setExpiryWarningIssued(Boolean expiryWarningIssued) {
        this.expiryWarningIssued = expiryWarningIssued;
    }

    public LocalDateTime getDateOfIssue() {
        return dateOfIssue;
    }

    public void setDateOfIssue(LocalDateTime dateOfIssue) {
        this.dateOfIssue = dateOfIssue;
    }

    public LocalDateTime getDateOfExpiryWarning() {
        return dateOfExpiryWarning;
    }

    public void setDateOfExpiryWarning(LocalDateTime dateOfExpiryWarning) {
        this.dateOfExpiryWarning = dateOfExpiryWarning;
    }

    public LocalDateTime getDateOfExpiry() {
        return dateOfExpiry;
    }

    public void setDateOfExpiry(LocalDateTime dateOfExpiry) {
        this.dateOfExpiry = dateOfExpiry;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Certificate that = (Certificate) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Certificate{" +
            "id=" + id +
            ", certificateTemplate=" + certificateTemplate +
            ", certifiedOrganizationOrPerson='" + certifiedOrganizationOrPerson + '\'' +
            ", expiryWarningIssued=" + expiryWarningIssued +
            ", dateOfIssue=" + dateOfIssue +
            ", dateOfExpiryWarning=" + dateOfExpiryWarning +
            ", dateOfExpiry=" + dateOfExpiry +
            '}';
    }
}
