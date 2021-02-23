package ca.ids.abms.modules.common.services;

import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.util.converter.JSR310DateConverters;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimeValidator implements ConstraintValidator<Time4Digits, String> {

    @Override
    public void initialize(Time4Digits s) {}

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        boolean isValid = true;
        if (StringUtils.isNotBlank(s)) {
            if (s.length() != 4) {
                isValid = false;
            } else {
                try {
                    LocalTime.parse(s, DateTimeFormatter.ofPattern(JSR310DateConverters.DEFAULT_PATTERN_TIME));
                } catch (DateTimeParseException dtpe) {
                    isValid = false;
                }
            }
        }
        return isValid;
    }
}
