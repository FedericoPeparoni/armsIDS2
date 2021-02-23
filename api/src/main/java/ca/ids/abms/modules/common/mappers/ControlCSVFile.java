package ca.ids.abms.modules.common.mappers;

import org.apache.commons.lang.StringUtils;
import org.mapstruct.Qualifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Qualifier
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ControlCSVFile {

    int ignoreFirstRows() default 0;

    String stopAtThisToken() default StringUtils.EMPTY;
}
