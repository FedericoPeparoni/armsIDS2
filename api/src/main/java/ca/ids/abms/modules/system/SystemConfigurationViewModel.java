package ca.ids.abms.modules.system;

import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import java.time.LocalDateTime;

import javax.validation.constraints.Size;

public class SystemConfigurationViewModel extends VersionedViewModel {

    private Integer id;

    @Size(max = 100)
    private String itemName;

    private SystemItemType itemClass;

    private SystemDataType dataType;

    @Size(max = 100)
    private String units;

    @Size(max = 100)
    private String range;

    @Size(max = 100)
    private String defaultValue;

    @Size(max = 100)
    private String currentValue;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    private Boolean clientStorageForbidden;

    private String systemValidationType;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCurrentValue() {
        return currentValue;
    }

    public SystemDataType getDataType() {
        return dataType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Integer getId() {
        return id;
    }

    public SystemItemType getItemClass() {
        return itemClass;
    }

    public String getItemName() {
        return itemName;
    }

    public String getRange() {
        return range;
    }

    public String getUnits() {
        return units;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setCreatedAt(LocalDateTime aCreatedAt) {
        createdAt = aCreatedAt;
    }

    public void setCreatedBy(String aCreatedBy) {
        createdBy = aCreatedBy;
    }

    public void setCurrentValue(String aCurrentValue) {
        currentValue = aCurrentValue;
    }

    public void setDataType(SystemDataType aDataType) {
        dataType = aDataType;
    }

    public void setDefaultValue(String aDefaultValue) {
        defaultValue = aDefaultValue;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setItemClass(SystemItemType aItemClass) {
        itemClass = aItemClass;
    }

    public void setItemName(String aItemName) {
        itemName = aItemName;
    }

    public void setRange(String aRange) {
        range = aRange;
    }

    public void setUnits(String aUnits) {
        units = aUnits;
    }

    public void setUpdatedAt(LocalDateTime aUpdatedAt) {
        updatedAt = aUpdatedAt;
    }

    public void setUpdatedBy(String aUpdatedBy) {
        updatedBy = aUpdatedBy;
    }

    public Boolean getClientStorageForbidden() {
        return clientStorageForbidden;
    }

    public void setClientStorageForbidden(Boolean clientStorageForbidden) {
        this.clientStorageForbidden = clientStorageForbidden;
    }

    public String getSystemValidationType() {
        return systemValidationType;
    }

    public void setSystemValidationType(String systemValidationType) {
        this.systemValidationType = systemValidationType;
    }
}
