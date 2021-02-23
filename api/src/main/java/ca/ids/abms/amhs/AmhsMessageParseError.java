package ca.ids.abms.amhs;

@SuppressWarnings("serial")
public class AmhsMessageParseError extends AmhsException {
	public AmhsMessageParseError (final Throwable cause) {
		super (cause);
	}
	public AmhsMessageParseError (final String what) {
		super (what);
	}
}
