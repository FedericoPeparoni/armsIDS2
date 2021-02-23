package ca.ids.abms.plugins.amhs.fpl;

@SuppressWarnings("serial")
public class FlightMessageException extends RuntimeException {
    
    public FlightMessageException (final String s) {
        super (s);
    }
    
    public FlightMessageException (final String s, final Throwable cause) {
        super (s, cause);
    }

    public FlightMessageException (final Throwable cause) {
        super (cause);
    }
    
}
