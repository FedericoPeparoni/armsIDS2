package ca.ids.abms.config.error;

import javax.validation.ConstraintViolationException;

import ca.ids.abms.modules.common.services.EntityValidator;

public class RejectedException extends RuntimeException implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

    private RejectedReasons reason;

    final private ErrorDTO errorDTO;

    public RejectedException(final RejectedReasons reason, final String message, final String details) {
        super(message);
        this.reason = reason;
        this.errorDTO = new ErrorDTO.Builder(message).appendDetails(details).build();
    }
    public RejectedException(final RejectedReasons reason, final ErrorDTO errorDto) {
        super(errorDto.getError());
        this.reason = reason;
        this.errorDTO = errorDto;
    }
    public RejectedException(final ErrorDTO errorDto) {
        super(errorDto.getError());
        this.reason = errorDto.getRejectedReasons();
        this.errorDTO = errorDto;
    }
    public RejectedException(RejectedReasons reason, Throwable origin) {
        super(origin.getMessage(), origin);
        this.reason = reason;
        if (origin instanceof ConstraintViolationException) {
            this.errorDTO = EntityValidator.getMessageFromConstraintViolations((ConstraintViolationException)origin);
        } else if (origin.getCause() != null) {
            this.errorDTO = new ErrorDTO.Builder(origin.getMessage()).appendDetails(origin.getCause().getMessage()).build();
        } else {
            this.errorDTO = new ErrorDTO.Builder(origin.getMessage()).build();
        }
    }

    public String getMessage() {
       return this.errorDTO.getError();
    }

    public String getReason() {
        return this.reason.toValue();
    }
    
    public RejectedReasons getReasonCode() {
        return this.reason;
    }

    public String getDetails() {
        return this.errorDTO.getErrorDescription();
    }

    public ErrorDTO getErrorDto() {
        return this.errorDTO;
    }
}
