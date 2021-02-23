package ca.ids.abms.config.db.validators;

import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueKey {

    String[] columnNames();

    boolean checkSeparately() default false;

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        UniqueKey[] value();
    }
}
