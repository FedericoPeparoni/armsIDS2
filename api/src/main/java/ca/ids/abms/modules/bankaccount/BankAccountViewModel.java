package ca.ids.abms.modules.bankaccount;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class BankAccountViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    @Size(min = 1, max = BankAccount.NAME_MAX_LENGTH)
    private String name;

    @NotNull
    @Size(min = 1, max = BankAccount.NUMBER_MAX_LENGTH)
    private String number;

    @NotNull
    private Currency currency;

    @Size(min = 1, max = BankAccount.EXTERNAL_ACCOUTING_SYSTEM_ID_MAX_LENGTH)
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
}
