package ca.ids.abms.plugins.amhs;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.error.RejectedException;
import ca.ids.abms.modules.rejected.RejectedItemService;
import ca.ids.abms.modules.rejected.RejectedItemType;
import ca.ids.abms.plugins.amhs.fpl.FlightMessageProcessor;
import ca.ids.abms.util.json.JsonHelper;

@Component
public class AmhsMessageProcessor {
    
    public AmhsMessageProcessor (final FlightMessageProcessor flightMessageProcessor, final RejectedItemService rejectedItemService,
            final JsonHelper jsonHelper) {
        this.flightMessageProcessor = flightMessageProcessor;
        this.rejectedItemService = rejectedItemService;
        this.jsonHelper = jsonHelper;
    }

    @Transactional
    public void process (final AmhsMessageContext ctx) {
        LOG.info ("processing AMHS message {}", ctx.getAmhsParsedMessage());
        final AmhsMessageCategory cat = ctx.getAmhsParsedMessage().amhsMessageType.getCategory();
        if (cat == AmhsMessageCategory.FPL) {
            flightMessageProcessor.process (ctx);
            return;
        }
        ctx.invalidMessageTypeError();
    }
    
    @Transactional
    public void reject (final AmhsParsedMessage p, final RejectedException rejectedException) {
        LOG.info ("rejecting AMHS message [{}] - {}", rejectedException.getMessage(), p);
        final String jsonText = jsonHelper.toJsonString(p);
        final byte[] jsonData = jsonText.getBytes(StandardCharsets.UTF_8);
        this.rejectedItemService.create (
                RejectedItemType.AMHS_MSG,         // recordType
                rejectedException,
                null,                              // originator
                p.amhsMessage.getFilename(),       // filename
                null,                              // rawText
                null,                              // header
                jsonData
        );
    }


    private static final Logger LOG = LoggerFactory.getLogger(AmhsMessageProcessor.class);
    private final FlightMessageProcessor flightMessageProcessor;
    private final RejectedItemService rejectedItemService;
    private final JsonHelper jsonHelper;
    
}
