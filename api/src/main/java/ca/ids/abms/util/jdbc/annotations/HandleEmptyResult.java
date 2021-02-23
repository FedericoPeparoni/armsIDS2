package ca.ids.abms.util.jdbc.annotations;

import java.lang.annotation.*;

/**
 * Maps thrown {@link org.springframework.dao.EmptyResultDataAccessException} to {@code null} result.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HandleEmptyResult {
}
