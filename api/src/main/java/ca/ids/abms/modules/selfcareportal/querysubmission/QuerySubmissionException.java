package ca.ids.abms.modules.selfcareportal.querysubmission;

import ca.ids.abms.config.error.ErrorConstants;

public class QuerySubmissionException extends RuntimeException {

    /**
     * Specific error constant about the Throwable.
     */
    private final ErrorConstants reason;

    /**
     * Constructs a new exception with the specified detail message and
     * cause.  <p>Note that the detail message associated with
     * {@code cause} is <i>not</i> automatically incorporated in
     * this exception's detail message.
     *
     * @param  reason the error constant (which is saved for later retrieval
     *         by the {@link #getReason()} method).
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     * @since  1.4
     */
    QuerySubmissionException(final ErrorConstants reason, final Throwable cause) {
        super(cause);
        this.reason = reason;
    }

    /**
     * Returns the error constant of this throwable.
     *
     * @return  the error constant of this {@code Throwable} instance.
     */
    public ErrorConstants getReason() {
        return reason;
    }
}
