package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@Table(name = "unified_tax")
public class UnifiedTax extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_manufacture_year")
    @SearchableText
    private LocalDateTime fromManufactureYear;

    @Column(name = "to_manufacture_year")
    @SearchableText
    private LocalDateTime toManufactureYear;

    @ManyToOne
    @JoinColumn(name = "validity_id")
    private UnifiedTaxValidity validity;

    @Column(name = "rate")
    @NotNull
    @SearchableText
    private String rate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFromManufactureYear() {
        return fromManufactureYear;
    }

    public void setFromManufactureYear(LocalDateTime fromManufactureYear) {
        this.fromManufactureYear = fromManufactureYear;
    }

    public LocalDateTime getToManufactureYear() {
        return toManufactureYear;
    }

    public void setToManufactureYear(LocalDateTime toManufactureYear) {
        this.toManufactureYear = toManufactureYear;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public UnifiedTaxValidity getValidity() {
        return validity;
    }

    public void setValidity(UnifiedTaxValidity validity) {
        this.validity = validity;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fromManufactureYear == null) ? 0 : fromManufactureYear.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((rate == null) ? 0 : rate.hashCode());
        result = prime * result + ((toManufactureYear == null) ? 0 : toManufactureYear.hashCode());
        result = prime * result + ((validity == null) ? 0 : validity.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UnifiedTax other = (UnifiedTax) obj;
        if (fromManufactureYear == null) {
            if (other.fromManufactureYear != null)
                return false;
        } else if (!fromManufactureYear.equals(other.fromManufactureYear))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (rate == null) {
            if (other.rate != null)
                return false;
        } else if (!rate.equals(other.rate))
            return false;
        if (toManufactureYear == null) {
            if (other.toManufactureYear != null)
                return false;
        } else if (!toManufactureYear.equals(other.toManufactureYear))
            return false;
        if (validity == null) {
            if (other.validity != null)
                return false;
        } else if (!validity.equals(other.validity))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UnifiedTax [id=" + id + ", fromManufactureYear=" + fromManufactureYear + ", toManufactureYear="
                + toManufactureYear + ", validity=" + validity + ", rate=" + rate + "]";
    }

   

}
