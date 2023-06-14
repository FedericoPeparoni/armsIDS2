package ca.ids.abms.config.error;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.translation.Translation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ErrorDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String error;
    private final String errorDescription;
    private final String reference;
    private final RejectedReasons rejectedReasons;

    private ArrayList<FieldErrorDTO> fieldErrors;
    private ErrorVariables errorMessageVariables;
    private ErrorVariables detailMessageVariables;

    /**
     * errorMessage and errorDetails are received as a StringBuilder
     * For single a single errorMessage and/or errorDetail the StringBuilder is first translated
     * and then interpolated with variables.
     * For multiple, appended, errorMessages and errorDetails, the individual messages are translated, appended, and
     * then interpolated with variables.
     */

    private ErrorDTO(final Builder builder) {
        this.error = (builder.reference != null ? builder.errorMessage.append(' ').append(builder.reference).toString()
                : builder.errorMessage.toString());
        this.errorDescription = builder.errorDetails.toString();
        this.fieldErrors = builder.fields;
        this.rejectedReasons = builder.rejectedReasons;
        this.reference = builder.reference;
        this.errorMessageVariables = builder.errorMessageVariables;
        this.detailMessageVariables = builder.detailMessageVariables;
    }

    public static class Builder {
        private final StringBuilder errorMessage;

        private ArrayList<String> errorMessageList;
        private ErrorVariables errorMessageVariables;

        private ErrorVariables detailMessageVariables;
        private ArrayList<String> detailMessageList;

        private String reference;
        private final StringBuilder errorDetails;
        private final ArrayList<FieldErrorDTO> fields;
        private RejectedReasons rejectedReasons;

        public Builder() {
            this.errorMessage = new StringBuilder();
            this.errorMessageList = new ArrayList<>();
            this.errorDetails = new StringBuilder();
            this.detailMessageList = new ArrayList<>();
            this.fields = new ArrayList<>();
            this.rejectedReasons = RejectedReasons.UNKNOWN;
            this.errorMessageVariables = new ErrorVariables();
            this.detailMessageVariables = new ErrorVariables();
        }

        public Builder(final String errorMessage) {
            this();
            this.errorMessage.append(errorMessage);
            this.errorMessageList.add(errorMessage);
        }

        public Builder(final ErrorConstants errorConstants) {
            this();
            this.errorMessage.append(errorConstants.toValue());
            this.errorMessageList.add(errorConstants.toValue());
        }

        public Builder(final String errorMessage, int capacityOfFieldsList) {
            this.errorMessage = new StringBuilder().append(errorMessage);
            this.errorMessageList = new ArrayList<>();
            this.errorMessageList.add(errorMessage);
            this.errorMessageVariables = new ErrorVariables();
            this.detailMessageVariables = new ErrorVariables();
            this.errorDetails = new StringBuilder();
            this.detailMessageList = new ArrayList<>();
            this.fields = new ArrayList<>(capacityOfFieldsList);
            this.rejectedReasons = RejectedReasons.UNKNOWN;
        }

        public Builder(final ErrorConstants errorConstants, String reference, int capacityOfFieldsList) {
            final String message = (reference != null ? ErrorConstants.ERR_VALIDATION.toValue() + reference : ErrorConstants.ERR_VALIDATION.toValue());

            this.errorMessage = new StringBuilder().append(message);
            this.errorMessageList = new ArrayList<>();
            this.errorMessageList.add(message);
            this.errorMessageVariables = new ErrorVariables();
            this.detailMessageVariables = new ErrorVariables();
            this.errorDetails = new StringBuilder();
            this.detailMessageList = new ArrayList<>();
            this.fields = new ArrayList<>(capacityOfFieldsList);
            this.rejectedReasons = RejectedReasons.UNKNOWN;
        }

        public Builder setErrorMessage(final ErrorConstants errorConstants) {
            this.errorMessage.append(errorConstants.toValue());
            this.errorMessageList.add(String.valueOf(errorConstants.toValue()));
            return this;
        }

        public Builder setErrorMessage(final String errorMessage) {
            this.errorMessage.append(errorMessage);
            this.errorMessageList.add(String.valueOf(errorMessage));
            return this;
        }

        public Builder setErrorMessage(final Character errorMessage) {
            this.errorMessage.append(errorMessage);
            this.errorMessageList.add(errorMessage.toString());
            return this;
        }

        public Builder setErrorMessage(final Number errorMessage) {
            this.errorMessage.append(errorMessage);
            this.errorMessageList.add(errorMessage.toString());
            return this;
        }

        public Builder setErrorMessageVariables(final ErrorVariables errorVariables) {
            this.errorMessageVariables = errorVariables;
            return this;
        }

        public Builder addErrorMessageVariable(final String key, final String value) {
            if (this.errorMessageVariables == null) {
                this.errorMessageVariables = new ErrorVariables();
            }
            this.errorMessageVariables.addEntry(key, value);
            return this;
        }

        public Builder setDetailMessageVariables(final ErrorVariables errorVariables) {
            this.detailMessageVariables = errorVariables;
            return this;
        }

        public Builder setReference(final String reference) {
            this.reference = reference;
            return this;
        }

        public Builder appendDetails(final String errorDetail) {
            this.errorDetails.append(errorDetail);
            this.detailMessageList = new ArrayList<>();
            return this;
        }

        public Builder appendDetails(final Number errorDetail) {
            this.errorDetails.append(errorDetail);
            this.detailMessageList = new ArrayList<>();
            return this;
        }

        public Builder appendDetails(final boolean errorDetail) {
            this.errorDetails.append(errorDetail);
            this.detailMessageList = new ArrayList<>();
            return this;
        }

        public Builder appendDetails(final char errorDetail) {
            this.errorDetails.append(errorDetail);
            this.detailMessageList = new ArrayList<>();
            return this;
        }

        public Builder addInvalidField(final Class objectType, final String fieldName, final String message) {
            fields.add(new FieldErrorDTO(objectType.getSimpleName(), fieldName, message));
            return this;
        }

        public Builder addInvalidField(final String objectName, final String fieldName, final String message) {
            fields.add(new FieldErrorDTO(objectName, fieldName, message));
            return this;
        }

        public Builder addInvalidField(final Class objectType, final String fieldName, final String message,
                                       final Object currentValue) {
            fields.add(new FieldErrorDTO(objectType.getSimpleName(), fieldName, message, String.valueOf(currentValue)));
            return this;
        }

        public Builder addInvalidField(final String objectName, final String fieldName, final String message,
                                       final Object currentValue) {
            fields.add(new FieldErrorDTO(objectName, fieldName, message, String.valueOf(currentValue)));
            return this;
        }

        public Builder addInvalidField(final FieldErrorDTO fieldError) {
            fields.add(fieldError);
            return this;
        }

        public Builder addInvalidFields(final List<FieldErrorDTO> fieldErrors) {
            fields.addAll(fieldErrors);
            return this;
        }

        public Builder addInvalidField(Class<? extends FlightMovement> flightMovement, String fieldName,
                                       ErrorConstants errorConstants, FlightMovementStatus flightMovementStatus) {

            fields.add(new FieldErrorDTO(flightMovement.toString(), fieldName, errorConstants.toValue(), flightMovementStatus.toString()));

            return this;
        }

        public Builder addInvalidField(Class<? extends FlightMovement> flightMovement, String fieldName,
                                       ErrorConstants errorConstants, String aerodrome) {

            fields.add(new FieldErrorDTO(flightMovement.toString(), fieldName, errorConstants.toValue(), aerodrome));

            return this;
        }

        public Builder addInvalidField(Class<? extends FlightMovement> flightMovement, String fieldName,
                                       ErrorConstants errorConstants) {

            fields.add(new FieldErrorDTO(flightMovement.toString(), fieldName, errorConstants.toValue()));

            return this;
        }

        public Builder addRejectedReason(final RejectedReasons rejectedReasons) {
            this.rejectedReasons = rejectedReasons;
            return this;
        }

        public ErrorDTO build() {
            // handle variables in message and details
            this.interpolate();
            return new ErrorDTO(this);
        }

        Builder translate() {
            if (this.errorMessageList.size() > 1) {
                translateAndJoinMessages(this.errorMessage, this.errorMessageList);
            } else if (this.errorMessage != null) {
                String translation = Translation.getLangByToken(this.errorMessage.toString());
                if (translation != null) {
                    this.errorMessage.replace(0, errorMessage.length(), translation);
                }
            }

            if (this.detailMessageList.size() > 1) {
                translateAndJoinMessages(this.errorDetails, this.detailMessageList);
            } else if (this.errorMessage != null) {
                String translation = Translation.getLangByToken(this.errorDetails.toString());
                if (translation != null) {
                    this.errorDetails.replace(0, this.errorDetails.length(), translation);
                }
            }

            return this;
        }

        private void interpolate() {
            if (this.errorMessageVariables != null) {
                replaceKeysWithVariables(this.errorMessage, this.errorMessageVariables);
            }

            if (this.detailMessageVariables != null) {
                replaceKeysWithVariables(this.errorDetails, this.detailMessageVariables);
            }
        }

        public CustomParametrizedException buildInvalidDataException() {
            if (rejectedReasons.equals(RejectedReasons.UNKNOWN)) {
                rejectedReasons = RejectedReasons.VALIDATION_ERROR;
            }
            this.interpolate();
            return ExceptionFactory.getInvalidDataException(new ErrorDTO(this));
        }

        public CustomParametrizedException buildFailedPersistenceException() {
            return ExceptionFactory.getInvalidDataException(new ErrorDTO(this));
        }

        public RejectedException buildRejectedException() {
            this.interpolate();
            return new RejectedException(new ErrorDTO(this));
        }

        public void throwInvalidDataException() {
            throw buildInvalidDataException();
        }

        /**
         * Replaces message with translated and joined message list.
         */
        private void translateAndJoinMessages(StringBuilder message, ArrayList<String> messageList) {
            message.replace(
                0,
                message.length(),
                messageList
                    .stream()
                    .map(Translation::getLangByToken)
                    .collect(Collectors.joining(", ")));
        }

        /**
         * Replaces message with message containing interpolated variables
         */
        private void replaceKeysWithVariables(StringBuilder message, ErrorVariables variables) {
            for (String key : variables.getEntries().keySet()) {
                String pattern = "\\{\\{" + Pattern.quote(key) + "\\}\\}";
                Pattern p = Pattern.compile(pattern);
                Matcher m = p.matcher(message);

                String replacementText = variables.getEntries().get(key);

                StringBuffer sb = new StringBuffer();
                while (m.find()) {
                    String replacement = replacementText != null ? Matcher.quoteReplacement(replacementText) : "null";
                    m.appendReplacement(sb, replacement);
                }
                m.appendTail(sb);
                message.setLength(0);
                message.append(sb.toString());
            }
        }
    }

    public void add(String objectName, String field, String message) {
        if (fieldErrors == null) {
            fieldErrors = new ArrayList<>();
        }
        fieldErrors.add(new FieldErrorDTO(objectName, field, message));
    }

    public void add(FieldErrorDTO field) {
        if (field != null) {
            if (fieldErrors == null) {
                fieldErrors = new ArrayList<>();
            }
            fieldErrors.add(field);
        }
    }

    public String getError() {
        return error;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public String getReference() {
        return (reference != null ? reference : "");
    }

    public RejectedReasons getRejectedReasons() {
        return rejectedReasons;
    }

    List<FieldErrorDTO> getFieldErrors() {
        return fieldErrors;
    }

    ErrorVariables getErrorMessageVariables() {
        return errorMessageVariables;
    }

    ErrorVariables getDetailMessageVariables() {
        return detailMessageVariables;
    }

    public String toString() {
        final StringBuilder messageError = new StringBuilder();
        if (error != null) {
            messageError.append(error);
            if (errorDescription != null) {
                messageError.append('/').append(errorDescription);
            }
            if (!fieldErrors.isEmpty()) {
                messageError.append(" > ");
                for (final FieldErrorDTO fieldErrorDTO : fieldErrors) {
                    messageError.append(fieldErrorDTO.getObjectName()).append('.').append(fieldErrorDTO.getField())
                            .append(": ").append(fieldErrorDTO.getMessage()).append("(\"")
                            .append(fieldErrorDTO.getCurrentValue()).append("\"); ");
                }
            }
        } else {
            messageError.append("no errors");
        }
        return messageError.toString();
    }

}
