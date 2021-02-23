package ca.ids.abms.modules.rejected;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.services.EntityValidator;

@Service
@Transactional
public abstract class AbstractRejectedItemHandler {

    protected AbstractRejectedItemHandler(EntityValidator validator,
                                          ObjectMapper jacksonObjectMapper) {
        this.validator = validator;
        this.jacksonObjectMapper = jacksonObjectMapper;
    }


    final private ObjectMapper jacksonObjectMapper;

    final protected EntityValidator validator;

    protected abstract byte[] convertRawTextIntoJsonText(final String rawText) throws CustomParametrizedException;

    protected abstract void fixItemWithRawText (final RejectedItem rejectedItem) throws CustomParametrizedException;

    protected abstract void fixItemWithJsonText (final RejectedItem rejectedItem) throws CustomParametrizedException;

    protected abstract RejectedItemType resposibleForRecordType ();

    final protected ObjectMapper getJsonMapper() {
        return jacksonObjectMapper;
    }

}
