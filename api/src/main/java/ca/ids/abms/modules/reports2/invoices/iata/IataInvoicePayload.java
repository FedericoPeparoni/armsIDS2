package ca.ids.abms.modules.reports2.invoices.iata;

import java.util.List;

public class IataInvoicePayload {

    private List<Integer> accountIdList;

    public List<Integer> getAccountIdList() {
        return accountIdList;
    }

    public void setAccountIdList(List<Integer> accountIdList) {
        this.accountIdList = accountIdList;
    }
}
