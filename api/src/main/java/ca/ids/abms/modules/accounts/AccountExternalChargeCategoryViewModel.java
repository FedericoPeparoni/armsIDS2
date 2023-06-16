package ca.ids.abms.modules.accounts;

import ca.ids.abms.modules.charges.ExternalChargeCategory;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;

public class AccountExternalChargeCategoryViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private Account account;

    @NotNull
    private ExternalChargeCategory externalChargeCategory;

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
        return "AccountExternalChargeCategoryViewModel{" +
            "id=" + id +
            ", account=" + account +
            ", externalChargeCategory=" + externalChargeCategory +
            ", externalSystemIdentifier='" + externalSystemIdentifier + '\'' +
            '}';
    }
}
