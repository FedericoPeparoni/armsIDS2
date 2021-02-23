package ca.ids.abms.modules.aircraft;

import java.time.LocalDateTime;
import java.util.Collection;

import javax.validation.constraints.Size;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.waketurbulence.WakeTurbulenceCategory;

public class AircraftTypeViewModel extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	private Integer id;

    @Size(max = 4)
    private String aircraftType;

    @Size(max = 100)
    private String aircraftName;

    private byte[] aircraftImage;

    @Size(max = 100)
    private String manufacturer;

    private WakeTurbulenceCategory wakeTurbulenceCategory;

    private Double maximumTakeoffWeight;

    @Size(min = 4, max = 50)
    private String createdBy;

    @Size(min = 4, max = 50)
    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private Collection<String> accounts;

    public Collection<String> getAccounts() {
        return accounts;
    }

    public byte[] getAircraftImage() {
        return aircraftImage;
    }

    public String getAircraftName() {
        return aircraftName;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Integer getId() {
        return id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public Double getMaximumTakeoffWeight() {
        return maximumTakeoffWeight;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public WakeTurbulenceCategory getWakeTurbulenceCategory() {
        return wakeTurbulenceCategory;
    }

    public void setAccounts(Collection<String> aAccounts) {
        accounts = aAccounts;
    }

    public void setAircraftImage(byte[] aAircraftImage) {
        aircraftImage = aAircraftImage;
    }

    public void setAircraftName(String aAircraftName) {
        aircraftName = aAircraftName;
    }

    public void setAircraftType(String aAircraftType) {
        aircraftType = aAircraftType;
    }

    public void setCreatedAt(LocalDateTime aCreatedAt) {
        createdAt = aCreatedAt;
    }

    public void setCreatedBy(String aCreatedBy) {
        createdBy = aCreatedBy;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setManufacturer(String aManufacturer) {
        manufacturer = aManufacturer;
    }

    public void setMaximumTakeoffWeight(Double aMaximumTakeoffWeight) {
        maximumTakeoffWeight = aMaximumTakeoffWeight;
    }

    public void setUpdatedAt(LocalDateTime aUpdatedAt) {
        updatedAt = aUpdatedAt;
    }

    public void setUpdatedBy(String aUpdatedBy) {
        updatedBy = aUpdatedBy;
    }

    public void setWakeTurbulenceCategory(WakeTurbulenceCategory aWakeTurbulenceCategory) {
        wakeTurbulenceCategory = aWakeTurbulenceCategory;
    }

    @Override
    public String toString() {
        return "AircraftTypeViewModel [id=" + id + ", aircraftType=" + aircraftType + ", aircraftName=" + aircraftName
                + ", manufacturer=" + manufacturer + ", wakeTurbulenceCategory=" + wakeTurbulenceCategory
                + ", maximumTakeoffWeight=" + maximumTakeoffWeight + "]";
    }
}
