package ca.ids.abms.plugins.caab.sage.utilities.exceptions;

import ca.ids.abms.config.error.CustomParametrizedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class CaabSageException extends CustomParametrizedException {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageException.class);

    /**
     * Undefined value for error message handling.
     */
    static final String UNDEFINED = "undefined";

    CaabSageException(final String message) {
        super(message);
        LOG.error(message);
    }
}
