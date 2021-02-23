package ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader;

class ARInvoiceHeaderConstants {

    /**
     * CreditNote DocumentType as per integration document (1=invoice, 2=credit-note, 3=debit-note).
     */
    static final String DOCUMENT_TYPE_CREDIT_NOTE = "2";

    /**
     * DebitNote DocumentType as per integration document (1=invoice, 2=credit-note, 3=debit-note).
     */
    static final String DOCUMENT_TYPE_DEBIT_NOTE = "3";

    /**
     * Invoice DocumentType as per integration document (1=invoice, 2=credit-note, 3=debit-note).
     */
    static final String DOCUMENT_TYPE_INVOICE = "1";

    /**
     * InvoiceType as per integration document (1=items, 2=summary).
      */
    static final String INVOICE_TYPE = "2";

    private ARInvoiceHeaderConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
