package ca.ids.abms.modules.jobs;

import ca.ids.abms.util.StringUtils;

public class JobMessage {

    private final String message;

    private final String variables;

    private JobMessage(final String message, final String variables) {
        this.message = message;
        this.variables = variables;
    }

    public String getMessage() {
        return message;
    }

    public String getVariables() {
        return variables;
    }

    public static class Builder {

        private String message;

        private StringBuilder variables;

        public Builder setMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder addVariable(final String key, final String value) {
            if (variables == null) {
                variables = new StringBuilder();
            } else {
                variables.append(';');
            }
            variables.append(key).append('=').append(value);
            return this;
        }

        public Builder addVariable(final String key, final Number value) {
            return addVariable(key, String.valueOf(value));
        }

        public JobMessage build() {
            if (this.variables != null) {
                return new JobMessage(this.message, this.variables.toString());
            }
        return new JobMessage(this.message, StringUtils.EMPTY_STRING);
        }
    }

    @Override
    public String toString() {
        return "JobMessage{" +
            "message='" + message + '\'' +
            ", variables=" + variables +
            '}';
    }
}
