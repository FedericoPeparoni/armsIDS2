package ca.ids.abms.modules.currencies;

import java.util.List;

/**
 * Extra read-only information about a currency record
 */
public class CurrencyInfoViewModel {
    
    /** Currency ID (primary key) */
    private Integer currencyId;
    
    /** Top few accounts that use this currency */
    private List <String> refAccounts;
    
    /** Total number of accounts that use this currency */
    private Integer refAccountTotal;
    
    private boolean usedAsExchangeTargetByAnotherActiveCurrency;

    public Integer getCurrencyId() {
        return currencyId;
    }

    public void setCurrencyId(Integer currencyId) {
        this.currencyId = currencyId;
    }

    public List<String> getRefAccounts() {
        return refAccounts;
    }

    public void setRefAccounts(List<String> refAccounts) {
        this.refAccounts = refAccounts;
    }

    public Integer getRefAccountTotal() {
        return refAccountTotal;
    }

    public void setRefAccountTotal(Integer refAccountTotal) {
        this.refAccountTotal = refAccountTotal;
    }

    public boolean isUsedAsExchangeTargetByAnotherActiveCurrency() {
        return usedAsExchangeTargetByAnotherActiveCurrency;
    }

    public void setUsedAsExchangeTargetByAnotherActiveCurrency(boolean usedAsExchangeTargetByAnotherActiveCurrency) {
        this.usedAsExchangeTargetByAnotherActiveCurrency = usedAsExchangeTargetByAnotherActiveCurrency;
    }


}
