package ca.ids.abms.amhs;

@SuppressWarnings("serial")
public class AmhsException extends RuntimeException {
    public AmhsException (final String msg) {
        super (msg);
    }
    public AmhsException (final String msg, final Throwable cause) {
        super (msg, cause);
    }
    public AmhsException (final Throwable cause) {
        super (cause);
    }
}
