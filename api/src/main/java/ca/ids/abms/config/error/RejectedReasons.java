package ca.ids.abms.config.error;

import ca.ids.abms.modules.translation.Translation;

public enum RejectedReasons {

    CONNECTION_ERROR("CONNECTION_ERROR"),
    READ_ERROR("READ_ERROR"),
    PARSE_ERROR("PARSE_ERROR"),
    VALIDATION_ERROR("VALIDATION_ERROR"),
    FLIGHT_MOVEMENT_BUILDER_ERROR("FLIGHT_MOVEMENT_BUILDER_ERROR"),
    ALREADY_EXISTS("ALREADY_EXISTS"),
    ALREADY_EXISTS_SHOULD_DISCARD("ALREADY_EXISTS_SHOULD_DISCARD"),
    NOT_FOUND("NOT_FOUND"),
    DB_ERROR("DB_ERROR"),
    SYSTEM_ERROR("SYSTEM_ERROR"),
    TIMEOUT_ERROR("TIMEOUT_ERROR"),
    STALE_VERSION("STALE_VERSION"),
    UNKNOWN("UNKNOWN"),
    DUPLICATE_FLIGHT_PLAN("DUPLICATE_FLIGHT_PLAN"),
    PREPAYMENT_REQUIREMENT("PREPAYMENT_REQUIREMENT");

    final String value;

    RejectedReasons(final String value) {
        this.value = value;
    }

    public String toValue() {
        return Translation.getLangByToken(this.value);
    }
}
