package ca.ids.abms.amhs;

@SuppressWarnings("serial")
public class AmhsAgentException extends AmhsException {
    public AmhsAgentException (final String msg) {
        super (msg);
    }
    public AmhsAgentException (final String msg, final Throwable cause) {
        super (msg, cause);
    }
    public AmhsAgentException (final Throwable cause) {
        super (cause);
    }
}
