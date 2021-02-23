package ca.ids.abms.modules.billings;

/**
 * Created by c.talpa on 12/12/2016.
 */
public class BillingLedgerAccountViewModel {

    private Integer[] accountsID;
    private Integer month;
    private Integer year;

    public Integer[] getAccountsID() {
        return accountsID;
    }

    public void setAccountsID(Integer[] accountsID) {
        this.accountsID = accountsID;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "BillingLedgerAccountViewModel{" +
            "accountsID=" + accountsID +
            ", month=" + month +
            ", year=" + year +
            '}';
    }
}
