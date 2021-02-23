package ca.ids.abms.modules.jobs;


import ca.ids.abms.config.error.CustomRuntimeException;

public class JobAlreadyRunningException extends RuntimeException implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

	public JobAlreadyRunningException(final String message) {
        super(message);
    }
}
