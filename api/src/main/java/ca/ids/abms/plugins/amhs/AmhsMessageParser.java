package ca.ids.abms.plugins.amhs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.plugins.amhs.fpl.FlightMessageParser;

@Component
public class AmhsMessageParser {
    
    public AmhsMessageParser (final FlightMessageParser flightMessageParser) {
        this.flightMessageParser = flightMessageParser;
    }
    
    public void parse (final AmhsMessageContext ctx) {
        final AmhsMessageType type = ctx.getAmhsParsedMessage().amhsMessageType;
        LOG.info ("parsing AMHS message type={}", type);
        if (type.getCategory() == AmhsMessageCategory.FPL) {
            flightMessageParser.parse (ctx);
            return;
        }
        ctx.invalidMessageTypeError();
    }
    
    private static final Logger LOG = LoggerFactory.getLogger (AmhsMessageParser.class);
    
    private final FlightMessageParser flightMessageParser;

}
