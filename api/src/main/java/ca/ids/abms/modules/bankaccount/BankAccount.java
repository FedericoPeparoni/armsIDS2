package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "bank_accounts")
@UniqueKey(columnNames = { "name", "number" })
public class BankAccount extends VersionedAuditedEntity implements AbmsCrudEntity<Integer> {

    public static final int NAME_MAX_LENGTH = 60;
    public static final int NUMBER_MAX_LENGTH = 30;
    public static final int EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH = 20;

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", length = NAME_MAX_LENGTH)
    @NotNull
    @SearchableText
    private String name;

    @Column(name = "number", length = NUMBER_MAX_LENGTH)
    @NotNull
    @SearchableText
    private String number;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "currency_id")
    @SearchableEntity
    private Currency currency;

    @Column(name = "external_accounting_system_id", length = EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH)
    @SearchableText
    private String externalAccountingSystemId;

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

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getExternalAccountingSystemId() {
        return externalAccountingSystemId;
    }

    public void setExternalAccountingSystemId(String externalAccountingSystemId) {
        this.externalAccountingSystemId = externalAccountingSystemId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BankAccount that = (BankAccount) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BankAccount{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", number='" + number + '\'' +
            ", currency=" + currency +
            ", externalAccountingSystemId='" + externalAccountingSystemId + '\'' +
            '}';
    }
}
