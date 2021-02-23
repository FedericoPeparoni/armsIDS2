package ca.ids.abms.modules.reports2.invoices.nonaviation;

import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.reports2.common.InvoicePaymentParameters;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;

import java.util.List;

public class NonAviationInvoicePayload {

    /**
     * List of line items to use when generating point-of-sale invoice.
     */
    private List<InvoiceLineItemViewModel> lineItems;

    /**
     * Payment parameters used when generating and paying point-of-sale invoice.
     */
    private InvoicePaymentParameters payment;

    /**
     * KCAA AATIS Permit Numbers used when generating point-of-sale invoices.
     */
    private List<KcaaAatisPermitNumber> permitNumbers;

    /**
     * KCAA eAIP Requisition Numbers used when generating point-of-sale invoices.
     */
    private List<KcaaEaipRequisitionNumber> requisitionNumbers;

    public List<InvoiceLineItemViewModel> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<InvoiceLineItemViewModel> lineItems) {
        this.lineItems = lineItems;
    }

    public InvoicePaymentParameters getPayment() {
        return payment;
    }

    public void setPayment(InvoicePaymentParameters payment) {
        this.payment = payment;
    }

    public List<KcaaAatisPermitNumber> getPermitNumbers() {
        return permitNumbers;
    }

    public void setPermitNumbers(List<KcaaAatisPermitNumber> permitNumbers) {
        this.permitNumbers = permitNumbers;
    }

    public List<KcaaEaipRequisitionNumber> getRequisitionNumbers() {
        return requisitionNumbers;
    }

    public void setRequisitionNumbers(List<KcaaEaipRequisitionNumber> requisitionNumbers) {
        this.requisitionNumbers = requisitionNumbers;
    }
}
