package ca.ids.abms.modules.mtow;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
public class AverageMtowFactor extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    @NotNull
    private Double upperLimit;

    @NotNull
    @SuppressWarnings("squid:S1700")
    private Double averageMtowFactor;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "factor_class", length = 30)
    private FactorClass factorClass;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AverageMtowFactor that = (AverageMtowFactor) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public Double getAverageMtowFactor() {
        return averageMtowFactor;
    }

    public Integer getId() {
        return id;
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

    public void setId(Integer aId) {
        id = aId;
    }

    public void setUpperLimit(Double aUpperLimit) {
        upperLimit = aUpperLimit;
    }

    public void setFactorClass(FactorClass aFactorClass) {
        factorClass = aFactorClass;
    }

    @Override
    public String toString() {
        return "AverageMtowFactor [id=" + id + ", upperLimit=" + upperLimit + ", averageMtowFactor=" + averageMtowFactor
                + "]";
    }
}
