package ca.ids.abms.modules.localaircraftregistry;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.flightmovementsbuilder.vo.SmallAircraftVO;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class LocalAircraftRegistry extends VersionedAuditedEntity implements SmallAircraftVO {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @SearchableText
    @Column(name="registration_number")
    @NotNull
    private String registrationNumber;

    @SearchableText
    @Column(name="owner_name")
    @NotNull
    private String ownerName;

    @SearchableText
    @Column(name="analysis_type")
    @NotNull
    private String analysisType;

    @Column(name="mtow_weight")
    @NotNull
    private Double mtowWeight;

    @Column(name="coa_date_of_renewal")
    @NotNull
    private LocalDateTime coaDateOfRenewal;

    @Column(name="coa_date_of_expiry")
    @NotNull
    private LocalDateTime coaDateOfExpiry;

    @Override
    @Transactional
    public boolean isSmallAircraftCoaValid(LocalDateTime date) {
        return date != null && coaDateOfExpiry != null && coaDateOfRenewal != null &&
            (date.isBefore(coaDateOfExpiry) || date.isEqual(coaDateOfExpiry)) &&
            (date.isAfter(coaDateOfRenewal) || date.isEqual(coaDateOfRenewal));
    }

    public Integer getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public Double getMtowWeight() {
        return mtowWeight;
    }

    public LocalDateTime getCoaDateOfRenewal() {
        return coaDateOfRenewal;
    }

    public LocalDateTime getCoaDateOfExpiry() {
        return coaDateOfExpiry;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setRegistrationNumber(String aRegistrationNumber) {
        registrationNumber = aRegistrationNumber.toUpperCase();
    }

    public void setOwnerName(String aOwnerName) {
        ownerName = aOwnerName;
    }

    public void setAnalysisType(String aAnalysisType) {
        analysisType = aAnalysisType;
    }

    public void setMtowWeight(Double aMtowWeight) {
        mtowWeight = aMtowWeight;
    }

    public void setCoaDateOfRenewal(LocalDateTime aCoaDateOfRenewal) {
        coaDateOfRenewal = aCoaDateOfRenewal;
    }

    public void setCoaDateOfExpiry(LocalDateTime aCoaDateOfExpiry) {
        coaDateOfExpiry = aCoaDateOfExpiry;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof LocalAircraftRegistry))
            return false;

        LocalAircraftRegistry that = (LocalAircraftRegistry) obj;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return registrationNumber != null ? registrationNumber.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "LocalAircraftRegistry{" +
            "id=" + id +
            ", registrationNumber='" + registrationNumber + '\'' +
            ", ownerName=" + ownerName + '\'' +
            ", analysisType='" + analysisType + '\'' +
            ", mtowWeight='" + mtowWeight + '\'' +
            ", coaDateOfRenewal='" + coaDateOfRenewal + '\'' +
            ", coaDateOfExpiry='" + coaDateOfExpiry + '\'' +
            '}';
    }

}
