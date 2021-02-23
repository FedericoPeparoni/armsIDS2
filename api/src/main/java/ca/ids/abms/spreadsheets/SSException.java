package ca.ids.abms.spreadsheets;

/**
 * Exceptions thrown when using the {@link SSService} and friends.
 */
public class SSException extends RuntimeException {
    
    private static final long serialVersionUID = -1214292360272161304L;

    public SSException (final String message) {
        super (message);
    }
    
    public SSException (final Throwable cause) {
        super (cause);
    }
    
    public SSException (final String message, final Throwable cause) {
        super (message, cause);
    }

}
