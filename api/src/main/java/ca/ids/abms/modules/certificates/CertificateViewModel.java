package ca.ids.abms.modules.certificates;

import ca.ids.abms.modules.certificates.templates.CertificateTemplate;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * Created by c.talpa on 14/12/2016.
 */
public class CertificateViewModel extends VersionedViewModel {

    @Id
    private Integer id;

    @NotNull
    private CertificateTemplate certificateTemplate;

    @NotNull
    @Size(max = 100)
    private String certifiedOrganizationOrPerson;

    private byte[] certificateImage;

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
    public String toString() {
        return "CertificateViewModel{" +
            "id=" + id +
            ", certifiedOrganizationOrPerson='" + certifiedOrganizationOrPerson + '\'' +
            ", expiryWarningIssued=" + expiryWarningIssued +
            ", dateOfIssue=" + dateOfIssue +
            ", dateOfExpiryWarning=" + dateOfExpiryWarning +
            ", dateOfExpiry=" + dateOfExpiry +
            '}';
    }
}
