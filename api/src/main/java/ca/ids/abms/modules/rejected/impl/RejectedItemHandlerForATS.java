package ca.ids.abms.modules.rejected.impl;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.rejected.AbstractRejectedItemHandler;
import ca.ids.abms.modules.rejected.RejectedItem;
import ca.ids.abms.modules.rejected.RejectedItemType;

@Service
public class RejectedItemHandlerForATS extends AbstractRejectedItemHandler {

    private final Logger log = LoggerFactory.getLogger(RejectedItemHandlerForATS.class);

    public RejectedItemHandlerForATS(EntityValidator validator,
                                     ObjectMapper jacksonObjectMapper) {
        super(validator, jacksonObjectMapper);
    }

    @Override
    protected void fixItemWithRawText (final RejectedItem rejectedItem) throws CustomParametrizedException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    protected void fixItemWithJsonText (final RejectedItem rejectedItem) throws CustomParametrizedException {
        throw new RuntimeException("Not implemented yet");
    }


    @Override
    protected byte[] convertRawTextIntoJsonText(final String rawText) throws CustomParametrizedException {
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    protected RejectedItemType resposibleForRecordType () {
        return RejectedItemType.ATS_MESSAGES;
    }
}
