package ca.ids.abms.modules.charges;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "external_charge_categories")
public class ExternalChargeCategory extends AuditedEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = 50, nullable = false)
    @NotNull
    @SearchableText
    private String name;

    @Column(name = "unique", nullable = false)
    @NotNull
    private Boolean unique;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getUnique() {
        return unique;
    }

    public void setUnique(Boolean unique) {
        this.unique = unique;
    }

    @Override
    public String toString() {
        return "ExternalChargeCategory [id=" + id + ", name=" + name + ", unique=" + unique + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (!(o instanceof ExternalChargeCategory))
            return false;

        ExternalChargeCategory that = (ExternalChargeCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
