package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.modules.flightmovements.FlightMovementViewModel;
import ca.ids.abms.modules.reports2.common.InvoicePaymentParameters;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;

import java.util.List;

public class AviationInvoicePayload {

    /**
     * List of account ids to use when generating monthly/weekly invoice(s).
     */
    private List<Integer> accountIdList;

    /**
     * List of flight movement ids to use when generating point-of-sale invoice.
     */
    private List <FlightMovementViewModel> flightItems;

    /**
     * Payment parameters used when generating and paying point-of-sale invoice.
     */
    private InvoicePaymentParameters payment;

    /**
     * KCAA AATIS Permit Numbers used when generating point-of-sale invoices.
     */
    private List <KcaaAatisPermitNumber> invoicePermits;

    public List<Integer> getAccountIdList() {
        return accountIdList;
    }

    public void setAccountIdList(List<Integer> accountIdList) {
        this.accountIdList = accountIdList;
    }

    public List<FlightMovementViewModel> getFlightItems() {
        return flightItems;
    }

    public void setFlightItems(List<FlightMovementViewModel> flightItems) {
        this.flightItems = flightItems;
    }

    public InvoicePaymentParameters getPayment() {
        return payment;
    }

    public void setPayment(InvoicePaymentParameters payment) {
        this.payment = payment;
    }

    public List<KcaaAatisPermitNumber> getInvoicePermits() {
        return invoicePermits;
    }

    public void setInvoicePermits(List<KcaaAatisPermitNumber> invoicePermits) {
        this.invoicePermits = invoicePermits;
    }
}
