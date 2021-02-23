package ca.ids.abms.modules.bankcode;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "bank_codes")
public class BankCode extends VersionedAuditedEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", length = 20, unique = true)
    @NotNull
    private String code;

    @Column(name = "description", length = 50)
    private String description;

    @Column(name = "account_number", length = 20)
    @NotNull
    private String accountNumber;

    @Column(name = "branch_code", length = 20)
    @NotNull
    private String branchCode;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    @NotNull
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "billing_center_id")
    @NotNull
    private BillingCenter billingCenter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public void setBillingCenter(BillingCenter billingCenter) {
        this.billingCenter = billingCenter;
    }
}
