package ca.ids.abms.modules.reports2.invoices.iata;

/**
 * Constants that specify sorting order of IATA invoice items
 */
public enum IataInvoiceItemOrder {
    
    /** Account, then date/time */
    ACCOUNT_DATETIME,
    
    /** Date/time, then account */
    DATETIME_ACCOUNT;
    
    /**
     * Try to parse a sort order into an enum constant.
     * 
     * The string should be of the form "account,dateTime" or "dateTime,account".
     * Returns null if the string is null or unrecognized.
     */
    public static IataInvoiceItemOrder tryParse (final String s) {
        if (s != null) {
            if (s.equals ("account,dateTime")) {
                return ACCOUNT_DATETIME;
            }
            if (s.equals ("dateTime,account")) {
                return DATETIME_ACCOUNT;
            }
        }
        return null;
    }
}
