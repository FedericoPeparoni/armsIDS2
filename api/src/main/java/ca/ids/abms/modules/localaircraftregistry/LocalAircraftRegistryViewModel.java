package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class LocalAircraftRegistryViewModel extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	private Integer id;

    @Size(max = 50)
    private String registrationNumber;

    @Size(max = 300)
    private String ownerName;

    @Size(max = 100)
    private String analysisType;

    private Double mtowWeight;

    private LocalDateTime coaDateOfRenewal;

    private LocalDateTime coaDateOfExpiry;

    @Size(min = 4, max = 50)
    private String createdBy;

    @Size(min = 4, max = 50)
    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

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

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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

    public void setCreatedBy(String aCreatedBy) {
        createdBy = aCreatedBy;
    }

    public void setUpdatedBy(String aUpdatedBy) {
        updatedBy = aUpdatedBy;
    }

    public void setCreatedAt(LocalDateTime aCreatedAt) {
        createdAt = aCreatedAt;
    }

    public void setUpdatedAt(LocalDateTime aUpdatedAt) {
        updatedAt = aUpdatedAt;
    }
}
