package ca.ids.abms.modules.rejected.impl;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.services.EntityValidator;
import ca.ids.abms.modules.rejected.AbstractRejectedItemHandler;
import ca.ids.abms.modules.rejected.RejectedItem;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.plugins.amhs.fpl.FlightMessage;

@Service
public class RejectedItemHandlerForAmhs extends AbstractRejectedItemHandler {

    private static final Logger LOG = LoggerFactory.getLogger(RejectedItemHandlerForAmhs.class);

    public RejectedItemHandlerForAmhs (
            final EntityValidator validator,
            final ObjectMapper jacksonObjectMapper) {
        super(validator, jacksonObjectMapper);
    }

    @Override
    protected void fixItemWithRawText (final RejectedItem rejectedItem) {
        throw new CustomParametrizedException("Not allowed");
    }

    @Override
    protected byte[] convertRawTextIntoJsonText(final String rawText) {
        throw new CustomParametrizedException("Not allowed");
    }

    @Override
    protected void fixItemWithJsonText (final RejectedItem rejectedItem) {
        final byte[] jsonText = rejectedItem.getJsonText();
        LOG.debug("Starting to fix the rejected item {} using the jsonText field.", rejectedItem.getId());
        try {
            final FlightMessage m = getJsonMapper().readValue (jsonText, FlightMessage.class);
        } catch (JsonMappingException jme) {
            final Collection<String> fields = jme.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.toList());
            throw ExceptionFactory.getInvalidDataException(FplObjectDto.class, jme,
                fields.toArray(new String[fields.size()]));
        } catch (Exception generic) {
            LOG.debug("Rejected item {}: cannot fix the item because {}; maybe the json object or the values are wrong",
                rejectedItem.getId(), generic.getMessage(), generic);
            throw ExceptionFactory.getInvalidDataExceptionFromCause(RejectedItem.class, generic, "jsonText");
        }
    }

    @Override
    protected RejectedItemType resposibleForRecordType () {
        return RejectedItemType.AMHS_MSG;
    }
}
