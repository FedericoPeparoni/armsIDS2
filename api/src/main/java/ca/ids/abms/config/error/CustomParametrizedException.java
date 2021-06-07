package ca.ids.abms.config.error;

import ca.ids.abms.modules.flight.FlightReassignment;
import ca.ids.abms.modules.flight.FlightSchedule;
import java.util.List;

/**
 * Custom, parameterized exception, which can be translated on the client side.
 * For example:
 *
 * <pre>
 * throw new CustomParameterizedException(&quot;myCustomError&quot;, &quot;hello&quot;, &quot;world&quot;);
 * </pre>
 *
 * Can be translated with:
 *
 * <pre>
 * "error.myCustomError" :  "The server says {{params[0]}} to {{params[1]}}"
 * </pre>
 */
public class CustomParametrizedException extends RuntimeException implements CustomRuntimeException {

    private static final long serialVersionUID = 1L;

    private String[] params;

    private String objectName;

    private String errorDescription = null;

    private List<FieldErrorDTO> fields = null;


    private ErrorVariables errorMessageVariables;
    private ErrorVariables detailMessageVariables;

    public CustomParametrizedException(String message, String... params) {
        super(message);
        this.params = params;
    }

    public CustomParametrizedException(ErrorConstants message, String... params) {
        super(message.toValue());
        this.params = params;
    }

    public CustomParametrizedException(ErrorDTO errorDTO) {
        super(errorDTO.getError());
        this.errorDescription = errorDTO.getErrorDescription();
        this.errorMessageVariables = errorDTO.getErrorMessageVariables();
        this.detailMessageVariables = errorDTO.getDetailMessageVariables();
        this.fields = errorDTO.getFieldErrors();
    }

    public CustomParametrizedException(ErrorConstants message, Throwable cause, String... params) {
        super(message.toValue(), cause);
        this.params = params;
        this.errorDescription = cause.getLocalizedMessage();
    }
    public CustomParametrizedException(ErrorConstants message, Throwable cause,ErrorVariables errorVariables  ) {
        super(message.toValue(), cause);
        this.params = null;
        this.detailMessageVariables = errorVariables;
        this.errorDescription = cause.getLocalizedMessage();
    }

    public CustomParametrizedException(String message, Class<?> origin, String... params) {
        super(message);
        this.objectName = origin.getSimpleName();
        this.params = params;
    }

    public CustomParametrizedException(ErrorConstants message, Class<?> origin, String... params) {
        super(message.toValue());
        this.objectName = origin.getSimpleName();
        this.params = params;
    }

    public CustomParametrizedException(ErrorConstants message, Throwable cause, Class<?> origin, String... params) {
        super(message.toValue(), cause);
        this.objectName = origin.getSimpleName();
        this.params = params;
        this.errorDescription = cause.getLocalizedMessage();
    }

    public CustomParametrizedException(ErrorConstants message, ErrorConstants description, Class<?> origin, String... params) {
        super(message.toValue());
        this.objectName = origin.getSimpleName();
        this.params = params;
        this.errorDescription = description.toValue();
    }

    public CustomParametrizedException(ErrorConstants message, ErrorConstants description, String flight, Class<?> origin, String... params) {
        super(message.toValue());
        this.objectName = origin.getSimpleName();
        this.params = params;
        this.errorDescription = description.toValue() + " : " + flight;
    }


    public CustomParametrizedException(String value, Exception exception) {
        super(value, exception);
        this.errorDescription = value + exception.getLocalizedMessage();
	}

    public CustomParametrizedException(ErrorConstants errorConstants, String aerodromeWrong,
			Class<FlightSchedule> flightSchedule, ErrorConstants errAeroDepText, ErrorConstants errAeroDstText) {
        super(errorConstants.toValue());

        this.errorDescription = aerodromeWrong;
        this.params = new String[]{errAeroDepText.toValue(), errAeroDstText.toValue()};
        this.objectName = flightSchedule.getSimpleName();
	}

    public CustomParametrizedException(String value, Exception exception, String originalFilename) {
        super(value, exception);
        this.errorDescription = value + exception.getLocalizedMessage();
        this.objectName = originalFilename;
	}

	/**
     * Create an instance of an exception that describes a file-related error
	 */
    public static CustomParametrizedException createFileException(final String exceptionMessage, final Exception cause, final String originalFilename) {
        final CustomParametrizedException ex = new CustomParametrizedException(exceptionMessage, cause);
        ex.errorDescription = exceptionMessage + ": " + cause.getLocalizedMessage();
        ex.objectName = originalFilename;
        return ex;
    }

    public CustomParametrizedException(String message, String description, Class<?> origin, String... params) {
        super(message);
        this.objectName = origin.getSimpleName();
        this.params = params;
        this.errorDescription = description;
    }

    public CustomParametrizedException(String message, Throwable cause, String... params) {
        super(message, cause);
        this.params = params;
        this.errorDescription = cause.getLocalizedMessage();
	}

    public CustomParametrizedException(ErrorConstants errorConstants, String aerodromeWrong,
                                       Class<FlightReassignment> flightReassignment, ErrorConstants errAerodromeText) {
        super(errorConstants.toValue());

        this.errorDescription = aerodromeWrong;
        this.params = new String[]{errAerodromeText.toValue()};
        this.objectName = flightReassignment.getSimpleName();
    }

    public ParameterizedErrorDTO getParameterizedErrorDTO() {
        return new ParameterizedErrorDTO(getMessage(), params);
    }

    public String getDescription() {
        return this.errorDescription;
    }

    public String getObjectName() { return this.objectName; }

    public ErrorDTO getErrorDTO() {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(getMessage());

        if (this.errorDescription != null) {
            errorBuilder.appendDetails(getDescription());
        }

        if (this.fields != null) {
            for (FieldErrorDTO field : this.fields) {
                errorBuilder.addInvalidField(field);
            }
        }
        if (this.params != null) {
            for (String param : this.params) {
                errorBuilder.addInvalidField(this.objectName, param, null);
            }
        }
        // required to use "toValue" as this is already mistakenly translated due to commit a3f5dd9
        if (ErrorConstants.ERR_UNIQUENESS_VIOLATION.value.equals(getMessage()) ||
            ErrorConstants.ERR_UNIQUENESS_VIOLATION.toValue().equals(getMessage())
        ) {
            errorBuilder.addRejectedReason(RejectedReasons.ALREADY_EXISTS);
        } else {
            errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR);
        }

        if(this.detailMessageVariables != null) {
        	errorBuilder.setDetailMessageVariables(this.detailMessageVariables);
        }

        if(this.errorMessageVariables != null) {
        	errorBuilder.setErrorMessageVariables(this.errorMessageVariables);
        }
        return errorBuilder.build();
    }

    public ErrorDTO getTranslatedErrorDTO() {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(getMessage());

        if (this.errorDescription != null) {
            errorBuilder.appendDetails(getDescription());
        }

        if (this.fields != null) {
            for (FieldErrorDTO field : this.fields) {
                errorBuilder.addInvalidField(field);
            }
        }
        if (this.params != null) {
            for (String param : this.params) {
                errorBuilder.addInvalidField(this.objectName, param, null);
            }
        }
        // required to use "toValue" as this is already mistakenly translated due to commit a3f5dd9
        if (ErrorConstants.ERR_UNIQUENESS_VIOLATION.value.equals(getMessage()) ||
            ErrorConstants.ERR_UNIQUENESS_VIOLATION.toValue().equals(getMessage())
        ) {
            errorBuilder.addRejectedReason(RejectedReasons.ALREADY_EXISTS);
        } else {
            errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR);
        }

        errorBuilder.setErrorMessageVariables(this.errorMessageVariables);
        errorBuilder.setDetailMessageVariables(this.detailMessageVariables);

        return errorBuilder.translate().build();
    }


}
