package ca.ids.abms.amhs;

@SuppressWarnings("serial")
public class ShellException extends AmhsException {
    
    public ShellException (final String msg) {
        super (msg);
    }

    public ShellException (final Throwable cause) {
        super (cause);
    }
}
