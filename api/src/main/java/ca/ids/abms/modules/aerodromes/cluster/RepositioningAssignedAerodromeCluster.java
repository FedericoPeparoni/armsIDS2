package ca.ids.abms.modules.aerodromes.cluster;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import ca.ids.abms.modules.util.models.AuditedEntity;

@Entity
public class RepositioningAssignedAerodromeCluster extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private RepositioningAerodromeCluster repositioningAerodromeCluster;

    @NotNull
    private String aerodromeIdentifier;

    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        RepositioningAssignedAerodromeCluster item = (RepositioningAssignedAerodromeCluster) o;

        return id != null ? id.equals(item.id) : item.id == null;
    }

    public String getAerodromeIdentifier() {
        return aerodromeIdentifier;
    }

    public Integer getId() {
        return id;
    }

    public RepositioningAerodromeCluster getRepositioningAerodromeCluster() {
        return repositioningAerodromeCluster;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAerodromeIdentifier(String aAerodromeIdentifier) {
        aerodromeIdentifier = aAerodromeIdentifier;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setRepositioningAerodromeCluster(RepositioningAerodromeCluster aRepositioningAerodromeCluster) {
        repositioningAerodromeCluster = aRepositioningAerodromeCluster;
    }

    @Override
    public String toString() {
        return "RepositioningAssignedAerodromeCluster [id=" + id + ", repositioningAerodromeCluster="
                + repositioningAerodromeCluster + ", aerodromeIdentifier=" + aerodromeIdentifier + "]";
    }
}
