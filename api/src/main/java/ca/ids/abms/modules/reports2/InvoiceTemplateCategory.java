package ca.ids.abms.modules.reports2;

/**
 * Unique name of BIRT report template category
 *
 */
public enum InvoiceTemplateCategory {
	AVIATION_INVOICE("Aviation invoice"),
    AVIATION_INVOICE_CASH("Aviation invoice"),
    AVIATION_INVOICE_CREDIT("Aviation invoice"),
    AVIATION_PAX_INVOICE("Aviation invoice"),
    AVIATION_ENR_INVOICE("Aviation invoice"),
	NON_AVIATION_INVOICE("Non aviation invoice"),
	IATA_INVOICE("Iata invoice"),
	TRANSACTION_RECEIPT("Transaction receipt"),
    AVIATION_DEBIT_NOTE("Debit note"),
    AVIATION_CREDIT_NOTE("Credit note"),
    NON_AVIATION_DEBIT_NOTE("Debit note"),
    NON_AVIATION_CREDIT_NOTE("Credit note"),
	OVERDUE_INVOICE("Overdue invoice"),
    REVENUE_PROJECTION("Revenue projection"),
    INTEREST_INVOICE("Interest invoice");

    private String value;

	InvoiceTemplateCategory(String value){
        this.value=value;
    }

    public String getReadableValue() {
        return value;
    }

    public String getReadableKey() {
        return this.name();
    }

}
