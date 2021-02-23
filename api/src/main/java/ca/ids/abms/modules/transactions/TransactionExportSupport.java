package ca.ids.abms.modules.transactions;

/**
 * Used to define supported transaction export types: credit notes, payments.
 */
public class TransactionExportSupport {

    private Boolean creditNotes = false;

    private Boolean payments = false;

    public Boolean getCreditNotes() {
        return creditNotes;
    }

    public void setCreditNotes(Boolean creditNotes) {
        this.creditNotes = creditNotes;
    }

    public Boolean getPayments() {
        return payments;
    }

    public void setPayments(Boolean payments) {
        this.payments = payments;
    }
}
