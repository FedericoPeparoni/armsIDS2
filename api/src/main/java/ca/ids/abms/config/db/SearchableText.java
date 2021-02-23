package ca.ids.abms.config.db;

import javax.validation.Payload;
import java.lang.annotation.*;

@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface SearchableText {

    boolean exactMatch() default false;

    Class<? extends Payload>[] payload() default {};

}
