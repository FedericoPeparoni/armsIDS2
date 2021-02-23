package ca.ids.abms.config.error;

import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.flight.FlightReassignment;
import ca.ids.abms.modules.flight.FlightSchedule;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Preconditions;
import com.opencsv.exceptions.CsvException;
import org.apache.catalina.connector.ClientAbortException;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.concurrent.TimeoutException;

public class ExceptionFactory {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionFactory.class);

    private ExceptionFactory() {
    }

    public static CustomParametrizedException getDepencencyViolationException(final Class<?> origin,
            final Class<?> reference) {
        return new CustomParametrizedException(ErrorConstants.ERR_DEPENDENCY_VIOLATION, origin,
                reference.getSimpleName());
    }

    public static CustomParametrizedException getInvalidFileException(final Class<?> origin) {
        return new CustomParametrizedException(ErrorConstants.ERR_FILE_VALIDATION, origin, "document_contents");
    }

    public static CustomParametrizedException getInvalidFileException(final Class<?> origin, final Throwable cause) {
        return new CustomParametrizedException(ErrorConstants.ERR_FILE_VALIDATION, cause, origin, "document_contents");
    }

    public static CustomParametrizedException getInvalidDataException(final String msg, final String... parameters) {
        return new CustomParametrizedException(msg, parameters);
    }

    public static CustomParametrizedException getInvalidDataException(final String msg, final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(msg, origin, parameters);
    }

    public static CustomParametrizedException getInvalidDataException(final ErrorConstants msg, final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(msg.toValue(), origin, parameters);
    }

    public static CustomParametrizedException getInvalidDataException(final RejectedException rejectedException) {
        return new CustomParametrizedException(rejectedException.getErrorDto());
    }

    public static CustomParametrizedException getInvalidDataException(
            final FlightMovementBuilderException flightMovementBuilderException) {
        return new CustomParametrizedException(flightMovementBuilderException.getErrorDTO());
    }

    public static CustomParametrizedException getInvalidDataException(final ConstraintViolationException cve) {
        final ErrorDTO errorDto = EntityValidator.getMessageFromConstraintViolations(cve);
        return new CustomParametrizedException(errorDto);
    }

    public static CustomParametrizedException getInvalidDataException(final ErrorDTO errorDto) {
        return new CustomParametrizedException(errorDto);
    }

    public static CustomParametrizedException getInvalidDataException(final Class<?> origin, final Throwable cause,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, cause, origin, parameters);
    }

    public static Exception getInvalidDataException(ErrorConstants errorConstants, String aerodromeWrong,
                                                    Class<FlightReassignment> flightReassignment, ErrorConstants aerodrome) {
        return new CustomParametrizedException(errorConstants, aerodromeWrong, flightReassignment, aerodrome);
    }

    public static Exception getInvalidDataException(ErrorConstants errorConstants, String aerodromeWrong,
            Class<FlightSchedule> flightSchedule, ErrorConstants errAeroDepText, ErrorConstants errAeroDstText) {
        return new CustomParametrizedException(errorConstants, aerodromeWrong, flightSchedule, errAeroDepText, errAeroDstText);
    }

    public static CustomParametrizedException getInvalidDataExceptionFromCause(final Class<?> origin,
            final Throwable cause, final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, discoverMainCause(cause), origin,
                parameters);
    }

    public static CustomParametrizedException getNullablePasswordException(final Class<?> origin) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_NULL, origin);
    }

    public static CustomParametrizedException getMinLengthPasswordException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_MINIMUM_LENGTH, origin, parameters);
    }

    public static CustomParametrizedException getLowercasePasswordException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_LOWERCASE, origin, parameters);
    }

    public static CustomParametrizedException getNumericPasswordException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_NUMERIC, origin, parameters);
    }

    public static CustomParametrizedException getSpecialCharacterPasswordException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_SPECIAL, origin, parameters);
    }

    public static CustomParametrizedException getUppercasePasswordException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_PASSWORD_UPPERCASE, origin, parameters);
    }

    public static CustomParametrizedException getAdministratorException(final Class<?> origin) {
        return new CustomParametrizedException(ErrorConstants.ERR_ADMINISTRATOR, origin);
    }

    public static CustomParametrizedException getInvalidCurrencyRateException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_CURRENCY_RATE, origin, parameters);
    }

    public static CustomParametrizedException getInvalidCurrencyUpdating(final Class<?> origin) {
        return new CustomParametrizedException(ErrorConstants.ERR_CURRENCY_UPDATING, origin);
    }

    public static CustomParametrizedException getOverlappedCurrencyRateException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_OVERLAPPED_EXC_RATE, origin, parameters);
    }

    public static CustomParametrizedException getStartEndDateException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_START_END_DATE, origin, parameters);
    }

    public static CustomParametrizedException getMissingBillingCenterOfCurrentUserException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_MISSING_BILLING_CENTER_OF_CURRENT_USER, origin,
                parameters);
    }

    public static CustomParametrizedException getMissingAerodromesForBillingCenterException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_BILLING_CENTER_HAS_NO_AERODROMES, origin, parameters);
    }

    public static CustomParametrizedException getMissingReportTemplateException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_MISSINDG_REPORT_TEMPLATE, origin, parameters);
    }

    public static CustomParametrizedException getMissingHqBillingCenterException(final Class<?> origin,
            final String... parameters) {
        return new CustomParametrizedException(ErrorConstants.ERR_HQ_BILLING_CENTER_NOT_FOUND, origin, parameters);
    }
    
    public static RejectedException resolveRejectedException(final Throwable throwable, final RejectedReasons reason) {
        if (reason != null) {
            return new RejectedException(reason, resolveManagedErrors(throwable));
        } else {
            return new RejectedException(resolveManagedErrors(throwable));
        }
    }

    public static ErrorDTO resolveManagedErrors(final Throwable origin, final String reference) {
        Preconditions.checkNotNull(origin);
        ErrorDTO error = resolveValidationErrors(origin, reference);
        if (error == null) {
            final Throwable cause = discoverMainCause(origin);
            error = resolveValidationErrors(cause, reference);
            if (error == null) {
                error = getGenericErrorMessage(cause, reference);
            }
        }
        return error;
    }

    public static RuntimeException persistenceDataManagement(final RuntimeException origin, ErrorConstants errorConst) {
        Preconditions.checkNotNull(origin);
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder();
        ErrorDTO error = null;
        if (origin instanceof EntityNotFoundException || origin instanceof EmptyResultDataAccessException) {
            error = errorBuilder.setErrorMessage(ErrorConstants.ERR_NOT_FOUND.toValue())
                    .appendDetails(errorConst.toValue()).build();
        } else {
            final Throwable cause = discoverMainCause(origin);
            if (cause instanceof EntityNotFoundException) {
                error = errorBuilder.setErrorMessage(ErrorConstants.ERR_NOT_FOUND.toValue())
                        .appendDetails(errorConst.toValue()).build();
            }
        }
        RuntimeException exception;
        if (error != null) {
            exception = new CustomParametrizedException(error);
        } else {
            exception = origin;
        }
        return exception;
    }

    public static ErrorDTO resolveManagedErrors(final Throwable origin) {
        Preconditions.checkNotNull(origin);
        ErrorDTO error = resolveValidationErrors(origin, null);
        if (error == null) {
            final Throwable cause = discoverMainCause(origin);
            error = resolveValidationErrors(cause, null);
            if (error == null) {
                error = getGenericErrorMessage(cause, null);
            }
        }
        return error;
    }

    public static Throwable resolveMainCause(final Throwable throwable) {
        return discoverMainCause(throwable);
    }

    private static ErrorDTO resolveValidationErrors(final Throwable origin, final String reference) {
        ErrorDTO error = null;
        if (origin instanceof ConstraintViolationException) {
            error = EntityValidator.getMessageFromConstraintViolations((ConstraintViolationException) origin,
                    reference);
        } else if (origin instanceof CustomParametrizedException) {
            error = ((CustomParametrizedException) origin).getErrorDTO();
        } else if (origin instanceof MethodArgumentNotValidException) {
            error = EntityValidator.getMessageFromArgumentNotValid((MethodArgumentNotValidException) origin);
        }
        return error;
    }

    private static ErrorDTO getGenericErrorMessage(final Throwable exception, final String reference) {
        assert exception != null;
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder().setReference(reference);

        if (exception instanceof FlightMovementBuilderException) {
            errorBuilder.setErrorMessage(String.valueOf(exception.getLocalizedMessage()))
                    .addRejectedReason(RejectedReasons.VALIDATION_ERROR).addInvalidField("",
                            ((FlightMovementBuilderException) exception).getParam(),
                            ((FlightMovementBuilderException) exception).getFlightMovementBuilderIssue().toValue());
        } else if (exception instanceof ValidationException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_VALIDATION + String.valueOf(exception.getLocalizedMessage()))
                    .addRejectedReason(RejectedReasons.VALIDATION_ERROR);
        } else if (exception instanceof DateTimeParseException) {
            errorBuilder
                    .setErrorMessage(ErrorConstants.DEF_ERR_DATE_TIME_PARSE
                            + ((DateTimeParseException) exception).getParsedString())
                    .addRejectedReason(RejectedReasons.PARSE_ERROR);
        } else if (exception instanceof DateTimeException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_DATE_TIME)
                    .addRejectedReason(RejectedReasons.PARSE_ERROR);
        } else if (exception instanceof IllegalArgumentException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_ILLEGAL_ARG)
                    .addRejectedReason(RejectedReasons.VALIDATION_ERROR);
        } else if (exception instanceof CsvException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_VALIDATION + String.valueOf(exception.getLocalizedMessage()))
                    .addRejectedReason(RejectedReasons.VALIDATION_ERROR);
        } else if (exception instanceof JsonMappingException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_MAPPING).addRejectedReason(RejectedReasons.PARSE_ERROR);
        } else if (exception instanceof DataIntegrityViolationException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_ALREADY_EXISTS)
                    .addRejectedReason(RejectedReasons.ALREADY_EXISTS);
        } else if (exception instanceof SQLIntegrityConstraintViolationException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_ALREADY_EXISTS)
                    .addRejectedReason(RejectedReasons.ALREADY_EXISTS);
        } else if (exception instanceof EmptyResultDataAccessException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_NOT_FOUND).addRejectedReason(RejectedReasons.NOT_FOUND);
        } else if (exception instanceof EntityNotFoundException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_NOT_FOUND).addRejectedReason(RejectedReasons.NOT_FOUND);
        } else if (exception instanceof StaleVersionException || exception instanceof StaleStateException
            || exception instanceof ConcurrencyFailureException) {
            errorBuilder.setErrorMessage(ErrorConstants.ERR_CONCURRENCY_FAILURE)
                .addRejectedReason(RejectedReasons.STALE_VERSION);
        } else if (exception instanceof DataAccessException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_DB).addRejectedReason(RejectedReasons.DB_ERROR);
        } else if (exception instanceof SQLException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_DB).addRejectedReason(RejectedReasons.DB_ERROR);
        } else if (exception instanceof SocketException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_CONNECTION)
                .appendDetails(exception.getLocalizedMessage())
                .addRejectedReason(RejectedReasons.CONNECTION_ERROR);
            LOG.debug("(DEF_ERR_CONNECTION) Connection error!", exception);
        } else if (exception instanceof UnknownHostException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_CONNECTION)
                .appendDetails(exception.getMessage())
                .addRejectedReason(RejectedReasons.CONNECTION_ERROR);
            LOG.debug("(DEF_ERR_CONNECTION) Connection error!", exception);
        } else if (exception instanceof ClientAbortException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_TIMEOUT)
                .addRejectedReason(RejectedReasons.SYSTEM_ERROR);
        } else if (exception instanceof IOException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_IO).addRejectedReason(RejectedReasons.READ_ERROR);
        } else if (exception instanceof TimeoutException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_TIMEOUT)
                    .addRejectedReason(RejectedReasons.TIMEOUT_ERROR);
        } else if (exception instanceof AccessDeniedException) {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_SECURITY)
                    .addRejectedReason(RejectedReasons.SYSTEM_ERROR);
        } else if (exception instanceof RuntimeException) {
            if (exception.getLocalizedMessage() != null) {
                errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_RUNTIME);
            } else {
                errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_RUNTIME_UNK);
            }
            errorBuilder.addRejectedReason(RejectedReasons.SYSTEM_ERROR);
        } else {
            errorBuilder.setErrorMessage(ErrorConstants.DEF_ERR_GENERIC + String.valueOf(exception.getLocalizedMessage()))
                    .addRejectedReason(RejectedReasons.SYSTEM_ERROR);
        }
        return errorBuilder.appendDetails(String.valueOf(exception.getLocalizedMessage())).appendDetails(" (ref. ")
                .appendDetails(exception.getClass().getSimpleName()).appendDetails(')').build();
    }

    private static final int MAX_DISCOVERING_DEPTH = 10;

    private static Throwable discoverMainCause(final Throwable main) {
        assert (main != null);
        Throwable causeFound = main.getCause();
        if (causeFound != null) {
            int depth = MAX_DISCOVERING_DEPTH;
            while (causeFound.getCause() != null && (depth--) > 0) {
                causeFound = causeFound.getCause();
            }
            return causeFound;
        } else {
            return main;
        }
    }
}
