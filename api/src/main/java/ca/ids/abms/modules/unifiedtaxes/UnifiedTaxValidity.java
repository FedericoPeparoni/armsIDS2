package ca.ids.abms.modules.unifiedtaxes;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

@Entity
@Table(name = "unified_tax_validity", uniqueConstraints = @UniqueConstraint(columnNames = { "from_validity_year",
        "to_validity_year" }))
public class UnifiedTaxValidity extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "from_validity_year")
    @SearchableText
    private LocalDateTime fromValidityYear;

    @Column(name = "to_validity_year")
    @SearchableText
    private LocalDateTime toValidityYear;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "validity")
    private List<UnifiedTax> listUnifiedTax;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getFromValidityYear() {
        return fromValidityYear;
    }

    public void setFromValidityYear(LocalDateTime fromValidityYear) {
        this.fromValidityYear = fromValidityYear;
    }

    public LocalDateTime getToValidityYear() {
        return toValidityYear;
    }

    public void setToValidityYear(LocalDateTime toValidityYear) {
        this.toValidityYear = toValidityYear;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fromValidityYear == null) ? 0 : fromValidityYear.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((toValidityYear == null) ? 0 : toValidityYear.hashCode());
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
        UnifiedTaxValidity other = (UnifiedTaxValidity) obj;
        if (fromValidityYear == null) {
            if (other.fromValidityYear != null)
                return false;
        } else if (!fromValidityYear.equals(other.fromValidityYear))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (toValidityYear == null) {
            if (other.toValidityYear != null)
                return false;
        } else if (!toValidityYear.equals(other.toValidityYear))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UnifiedTaxValidity [id=" + id + ", fromValidityYear=" + fromValidityYear + ", toValidityYear="
                + toValidityYear + "]";
    }

}
