package ca.ids.abms.modules.transactions;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import ca.ids.abms.config.db.SearchableText;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class TransactionType implements Serializable {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Size(max = 30)
    @Column(unique = true)
    @SearchableText
    private String name;

    @JsonIgnore
    @OneToMany(mappedBy = "transactionType")
    private Set<Transaction> transactions = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        TransactionType that = (TransactionType) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setTransactions(Set<Transaction> aTransactions) {
        transactions = aTransactions;
    }

    @JsonIgnore
    @Transient
    public boolean isCredit() {
        return this.name != null && this.name.toLowerCase (Locale.US).equals("credit");
    }

    @JsonIgnore
    @Transient
    public boolean isDebit() {
        return this.name != null && this.name.toLowerCase (Locale.US).equals("debit");
    }

    @Override
    public String toString() {
        return "SystemDataType [id=" + id + ", name=" + name + "]";
    }
}
