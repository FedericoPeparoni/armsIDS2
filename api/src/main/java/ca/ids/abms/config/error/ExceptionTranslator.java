package ca.ids.abms.config.error;

import ca.ids.abms.modules.common.services.EntityValidator;

import ca.ids.abms.modules.selfcareportal.querysubmission.QuerySubmissionException;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ResponseEntity.BodyBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartException;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.sql.SQLIntegrityConstraintViolationException;

@ControllerAdvice
public class ExceptionTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(ExceptionTranslator.class);

    private static final String USER_AGENT_HEADER = "User-Agent";

    @Value("${spring.http.multipart.max-file-size}")
    @SuppressWarnings("FieldCanBeLocal")
    private String maxFileSize = "1024KB";

    @ExceptionHandler(ConcurrencyFailureException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO processConcurencyError(ConcurrencyFailureException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_CONCURRENCY_FAILURE).translate().build();
    }

    @ExceptionHandler(StaleVersionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO processStaleVersionError(StaleVersionException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_CONCURRENCY_FAILURE).translate().build();
    }

    @ExceptionHandler(StaleStateException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public ErrorDTO processStaleStateError(StaleStateException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_CONCURRENCY_FAILURE).translate().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processValidationError(MethodArgumentNotValidException ex) {
        LOG.trace ("validation exception: {}", ex.getMessage(), ex);
        return EntityValidator.getMessageFromArgumentNotValid(ex);
    }

    @ExceptionHandler(CustomParametrizedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorDTO processCustomParametrizedException(CustomParametrizedException ex) {
        return ex.getTranslatedErrorDTO();
    }

    @ExceptionHandler(QuerySubmissionException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDTO processQuerySubmissionException(QuerySubmissionException ex) {
        return new ErrorDTO.Builder(ex.getReason()).translate().build();
    }

    @ExceptionHandler(PartialParametersException.class)
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    @ResponseBody
    public ErrorDTO processPartialParametersException(PartialParametersException ex) {
        return ex.getTranslatedErrorDTO();
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ResponseBody
    public ErrorDTO processAccessDeniedExcpetion(AccessDeniedException e) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_ACCESS_DENIED).appendDetails(e.getLocalizedMessage()).translate().build();
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorDTO processMethodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_METHOD_NOT_SUPPORTED).appendDetails(exception.getLocalizedMessage()).translate().build();
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO processMethodNotSupportedException(EmptyResultDataAccessException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_NOT_FOUND).appendDetails(ex.getMessage()).translate().build();
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO processEntityNotFoundException(EntityNotFoundException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_NOT_FOUND).appendDetails(ex.getMessage()).translate().build();
    }

    @ExceptionHandler(NoSuchFileException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDTO processEntityNotFoundException(NoSuchFileException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_NOT_FOUND).appendDetails(ex.getMessage()).translate().build();
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO processUniqueConstantViolationException(SQLIntegrityConstraintViolationException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_UNIQUENESS_VIOLATION).appendDetails(ex.getMessage()).translate().build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDTO processDataIntegrityViolationException(DataIntegrityViolationException ex) {
        return new ErrorDTO.Builder(ErrorConstants.ERR_CONSTRAINT_VIOLATION).appendDetails( getFirstErrorMessage(ex, 5)).translate().build();
    }

    private String getFirstErrorMessage (final Throwable t, int level) {
        if (level > 0 && t.getCause() != null) {
            return getFirstErrorMessage(t.getCause(), --level);
        } else {
            return t.getMessage();
        }
    }

    @ExceptionHandler(IOException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDTO processIOException(IOException ex) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Generic exception found", ex);
        } else {
            LOG.warn(ex.getMessage());
        }
        return new ErrorDTO.Builder(ErrorConstants.ERR_IO).appendDetails(ex.getMessage()).translate().build();
    }

    @ExceptionHandler(Exception.class)
    @SuppressWarnings("squid:S3457")
    public ResponseEntity<ErrorDTO> processRuntimeException(Exception ex) {
        BodyBuilder builder;
        ErrorDTO errorDTO;
        ResponseStatus responseStatus = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            builder = ResponseEntity.status(responseStatus.value());
            errorDTO = new ErrorDTO.Builder("error."
                + responseStatus.value().value()).appendDetails(responseStatus.reason()).translate().build();
        } else {
            builder = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
            ErrorDTO.Builder error = new ErrorDTO.Builder(ErrorConstants.ERR_INTERNAL_SERVER_ERROR);
            if (StringUtils.isNotBlank(ex.getMessage())) error.appendDetails(ex.getMessage());
            errorDTO = error.translate().build();
        }
        if (LOG.isDebugEnabled()) {
        	LOG.debug(ex.getMessage(), ex);
        } else {
            LOG.warn(ex.getMessage());
        }
        return builder.body(errorDTO);
    }

    @ExceptionHandler(ClientAbortException.class)
    public void processClientAbortConnection(HttpServletRequest request, ClientAbortException ignored) {
        final String msg = "ClientAbortException: remoteAddr={}, userAgent={}, requestURL={}";
        if (LOG.isDebugEnabled())
            LOG.debug(msg, request.getRemoteAddr(), request.getHeader(USER_AGENT_HEADER), request.getRequestURL());
        else
            LOG.warn(msg, request.getRemoteAddr(), request.getHeader(USER_AGENT_HEADER), request.getRequestURL());
    }

    @ExceptionHandler(MultipartException.class)
    @ResponseBody
    @ResponseStatus(value = HttpStatus.PAYLOAD_TOO_LARGE)
    public ErrorDTO processPayloadTooLargeError(MultipartException ignored) {
        LOG.debug("MultipartException: Upload exceeds the maximum size of {}.", maxFileSize);
        ErrorVariables errorVariables = new ErrorVariables();
        errorVariables.addEntry("max_size", maxFileSize);
        return new ErrorDTO.Builder(ErrorConstants.ERR_PAYLOAD_TOO_LARGE).setErrorMessageVariables(errorVariables)
            .translate().build();
    }
}
