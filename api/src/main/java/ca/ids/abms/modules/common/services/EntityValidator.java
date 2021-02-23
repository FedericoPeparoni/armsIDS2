package ca.ids.abms.modules.common.services;

import ca.ids.abms.config.error.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import javax.validation.Validator;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

@Component
public class EntityValidator {

    @Autowired
    private Validator validator;

    public <X extends Serializable> void validateItem (final X entity){
        if(entity == null) {
            throw new IllegalArgumentException("Entity is null");
        }
        final Set<ConstraintViolation<X>> constraints = validator.validate(entity);
        final ErrorDTO errorDto = getMessageFromConstraintViolations(constraints);
        if (errorDto != null) {
            throw new RejectedException(errorDto);
        }
        
        // throw non repeatable exception if entity is empty, only loops through top level fields
        boolean isEmpty = true;
        for (Field field : entity.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                if (!field.getName().equalsIgnoreCase("serialVersionUID") && field.get(entity) != null) {
                    isEmpty = false;
                    break;
                }
            } catch (IllegalAccessException ignored) {
                // ignore any non-accessible fields
            }
        }
        if (isEmpty) throw new NonRejectedException(" File is unparsable. Item is empty");
    }
    
    public static ErrorDTO getMessageFromArgumentNotValid(final MethodArgumentNotValidException manv) {
        final BindingResult result = manv.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        return EntityValidator.processFieldErrors(fieldErrors);
    }

    public static ErrorDTO processFieldErrors(List<FieldError> fieldErrors) {
        final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION);
        for (FieldError fieldError : fieldErrors) {
            errorBuilder.addInvalidField(fieldError.getObjectName(), fieldError.getField(), fieldError.getDefaultMessage(),
                fieldError.getRejectedValue());
        }
        return errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR).build();
    }

    public static ErrorDTO getMessageFromConstraintViolations (
        final ConstraintViolationException cve) {
        return getMessageFromConstraintViolations(cve, null);
    }

    public static <X extends Serializable> ErrorDTO getMessageFromConstraintViolations (
        final Set<ConstraintViolation<X>> violations) {
        return getMessageFromConstraintViolations(violations, null);
    }

    public static ErrorDTO getMessageFromConstraintViolations (
        final ConstraintViolationException cve, final String reference) {
        final Set<ConstraintViolation<?>> violations = cve.getConstraintViolations();
        ErrorDTO errorDto = null;
        if (violations != null && !violations.isEmpty()) {
            final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION, reference, violations.size());

            for (ConstraintViolation violation : violations) {
                translateViolation(violation, errorBuilder);
            }
            errorDto = errorBuilder.addRejectedReason(RejectedReasons.VALIDATION_ERROR).build();
        }
        return errorDto;
    }

    public static <X extends Serializable> ErrorDTO getMessageFromConstraintViolations (
        final Set<ConstraintViolation<X>> violations, final String reference) {
        ErrorDTO errorDto = null;
        if (violations != null && !violations.isEmpty()) {
            final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_VALIDATION, reference, violations.size());
            errorBuilder.setErrorMessage(" \" ");
            for (ConstraintViolation violation : violations) {
                translateViolation(violation, errorBuilder);
            }
            errorDto = errorBuilder.setErrorMessage(" \" ").addRejectedReason(RejectedReasons.VALIDATION_ERROR).build();
        }
        return errorDto;
    }

    private static void translateViolation (final ConstraintViolation violation, final ErrorDTO.Builder errorBuilder) {
        if (violation.getPropertyPath() != null) {
            errorBuilder
                .setErrorMessage(violation.getPropertyPath().toString())
                .setErrorMessage(": ");
        }

        // used to check if null to avoid nullPointerException when calling toString()
        Path propertyPath = violation.getPropertyPath();
        Object invalidValue = violation.getInvalidValue();

        errorBuilder
            .setErrorMessage(violation.getMessage()).setErrorMessage("; ")
            .addInvalidField(
                violation.getRootBeanClass(),
                propertyPath != null ? propertyPath.toString() : null,
                violation.getMessage(),
                invalidValue != null ? invalidValue.toString() : null);
    }
}
