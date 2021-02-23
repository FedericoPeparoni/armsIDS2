package ca.ids.abms.plugins.amhs;

import org.springframework.stereotype.Component;

import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.config.error.RejectedReasons;

@Component
public class AmhsRejectedExceptionHelper {
    
    public RejectedException parseError (final Exception x) {
        return new RejectedException (RejectedReasons.PARSE_ERROR, x);
    }
    
    public RejectedException validationError (final Exception x) {
        return new RejectedException (RejectedReasons.VALIDATION_ERROR, x);
    }

}
