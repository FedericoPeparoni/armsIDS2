package ca.ids.abms.config.error;


public class NonRejectedException extends RuntimeException implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

    public NonRejectedException( final String message) {
        super(message);
    }
    
}
