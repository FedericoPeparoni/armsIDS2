package ca.ids.abms.modules.common.mappers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import ca.ids.abms.modules.common.services.TimeValidator;

@Constraint(validatedBy = TimeValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Time4Digits {

    String message() default "time not valid";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
