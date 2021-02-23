package ca.ids.abms.config.error;

import java.io.Serializable;

public class FieldErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String objectName;

    private final String field;

    private final String message;

    private String currentValue;

    public FieldErrorDTO(String dto, String field, String message) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
    }

    public FieldErrorDTO(String dto, String field, String message, String currentValue) {
        this.objectName = dto;
        this.field = field;
        this.message = message;
        this.currentValue = currentValue;
    }

    public String getObjectName() {
        return objectName;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getCurrentValue() {
        return currentValue;
    }

}
