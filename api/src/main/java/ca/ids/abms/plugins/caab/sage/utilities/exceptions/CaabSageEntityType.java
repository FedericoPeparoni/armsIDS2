package ca.ids.abms.plugins.caab.sage.utilities.exceptions;

public enum CaabSageEntityType {
    BILLING_CENTER("billing center"),
    BILLING_LEDGER("invoice"),
    CHARGE_ADJUSTMENT("charge adjustment"),
    CURRENCY("currency"),
    FLIGHT_MOVEMENT("flight movement"),
    INVOICE_LINE_ITEM("invoice line item"),
    INVOICE_OVERDUE_PENALTY("penalized invoice"),
    TRANSACTION("receipt"),
    USER("user");

    final String value;

    CaabSageEntityType(final String value) {
        this.value = value;
    }

    public String toValue() {
        return this.value;
    }
}
