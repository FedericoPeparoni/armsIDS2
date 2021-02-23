package ca.ids.abms.spreadsheets;

/**
 * Exceptions thrown when a spreadsheet doesn't follow the correct format.
 */
public class InvalidSpreadsheetLayoutException extends SSException {

    private static final long serialVersionUID = 5117474377908757180L;

    public InvalidSpreadsheetLayoutException (String message) {
        super(message);
    }
    
    public InvalidSpreadsheetLayoutException (Throwable cause) {
        super(cause);
    }
    
    public InvalidSpreadsheetLayoutException (String message, Throwable cause) {
        super(message, cause);
    }

}
