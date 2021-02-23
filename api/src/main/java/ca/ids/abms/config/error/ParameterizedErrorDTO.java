package ca.ids.abms.config.error;

import java.io.Serializable;

public class ParameterizedErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String errorMessage;
    private final String[] errorFields;

    public ParameterizedErrorDTO(String errorMessage, String... errorFields) {
        this.errorMessage = errorMessage;
        this.errorFields = errorFields;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String[] getErrorFields() {
        return errorFields;
    }

}
