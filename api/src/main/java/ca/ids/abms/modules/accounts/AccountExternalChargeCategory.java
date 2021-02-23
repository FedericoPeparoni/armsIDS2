package ca.ids.abms.modules.accounts;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.charges.ExternalChargeCategory;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "account_external_charge_categories")
@UniqueKey(columnNames = { "account", "externalChargeCategory" })
public class AccountExternalChargeCategory extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;
    
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JoinColumn(name = "account_id")
    @ManyToOne
    @NotNull
    private Account account;

    @JoinColumn(name = "external_charge_category_id")
    @ManyToOne
    @NotNull
    private ExternalChargeCategory externalChargeCategory;

    @Column(name = "external_system_identifier", length = 25, nullable = false)
    @NotNull
    private String externalSystemIdentifier;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public ExternalChargeCategory getExternalChargeCategory() {
        return externalChargeCategory;
    }

    public void setExternalChargeCategory(ExternalChargeCategory externalChargeCategory) {
        this.externalChargeCategory = externalChargeCategory;
    }

    public String getExternalSystemIdentifier() {
        return externalSystemIdentifier;
    }

    public void setExternalSystemIdentifier(String externalSystemIdentifier) {
        this.externalSystemIdentifier = externalSystemIdentifier;
    }

    @Override
    public String toString() {
        return "AccountExternalChargeCategory [id=" + id + ", account=" + account.toString()
            + ", externalChargeCategory=" + externalChargeCategory.toString()
            + ", externalSystemIdentifier=" + externalSystemIdentifier + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        else if (!(o instanceof AccountExternalChargeCategory))
            return false;

        AccountExternalChargeCategory that = (AccountExternalChargeCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
