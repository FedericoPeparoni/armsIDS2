package ca.ids.abms.modules.mtow;

import ca.ids.abms.modules.util.models.VersionedViewModel;

import java.time.LocalDateTime;

public class AverageMtowFactorViewModel extends VersionedViewModel {

    private Integer id;

    private Double upperLimit;

    private Double averageMtowFactor;

    private FactorClass factorClass;

    private String createdBy;

    private LocalDateTime createdAt;

    private String updatedBy;

    private LocalDateTime updatedAt;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AverageMtowFactorViewModel that = (AverageMtowFactorViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Double getAverageMtowFactor() {
        return averageMtowFactor;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public Double getUpperLimit() {
        return upperLimit;
    }

    public FactorClass getFactorClass() {
        return factorClass;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAverageMtowFactor(Double aAverageMtowFactor) {
        averageMtowFactor = aAverageMtowFactor;
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

    public void setUpdatedAt(LocalDateTime aUpdatedAt) {
        updatedAt = aUpdatedAt;
    }

    public void setUpdatedBy(String aUpdatedBy) {
        updatedBy = aUpdatedBy;
    }

    public void setUpperLimit(Double aUpperLimit) {
        upperLimit = aUpperLimit;
    }

    public void setFactorClass(FactorClass aFactorClass) {
        factorClass = aFactorClass;
    }

    @Override
    public String toString() {
        return "AverageMtowFactorViewModel [id=" + id + ", upperLimit=" + upperLimit + ", averageMtowFactor="
                + averageMtowFactor + "]";
    }
}
