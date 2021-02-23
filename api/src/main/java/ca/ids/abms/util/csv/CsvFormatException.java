package ca.ids.abms.util.csv;

public class CsvFormatException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    
    public CsvFormatException (final String what) {
        super (what);
    }
    
    public CsvFormatException (final String what, final Throwable cause) {
        super (what, cause);
    }
    
}
