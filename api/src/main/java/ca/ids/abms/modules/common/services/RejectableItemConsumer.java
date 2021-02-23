package ca.ids.abms.modules.common.services;

import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class RejectableItemConsumer {

    private final Logger log = LoggerFactory.getLogger(RejectableItemConsumer.class);

    @Autowired
    private RejectedItemService rejectedItemService;

    @Autowired
    protected EntityValidator validator;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    final protected void rejectItem(final FplObjectDto dto, final RejectedException re) {
        log.debug("Rejecting the item {} because {}", dto, re.getErrorDto());
        byte[] jsonText = null;
        try {
            jsonText = jacksonObjectMapper.writeValueAsBytes(dto);
        } catch (Exception e) {
            log.error("Cannot reject the {} with ID {} because a JSON object has not been created. Error found: {}",
                dto.getClass().getName(), dto.getFlightId(), e);
        }
        if (jsonText != null) {
            rejectedItemService.create(RejectedItemType.FLIGHT_MOVEMENTS, re, jsonText);
        }
    }
}
