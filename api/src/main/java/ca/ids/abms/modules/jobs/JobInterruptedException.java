package ca.ids.abms.modules.jobs;


import ca.ids.abms.config.error.CustomRuntimeException;

public class JobInterruptedException extends RuntimeException implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

	public JobInterruptedException(final String message) {
        super(message);
    }
}
