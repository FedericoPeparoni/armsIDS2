package ca.ids.abms.plugins.amhs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.amhs.AmhsMessage;

@Component
public class AmhsMessageConsumer {
    
    public AmhsMessageConsumer (
            final AmhsMessageParser amhsMessageParser,
            final AmhsMessageProcessor amhsMessageProcessor,
            final AmhsRejectedExceptionHelper amhsRejectedExceptionHelper) {
        this.amhsMessageParser = amhsMessageParser;
        this.amhsMessageProcessor = amhsMessageProcessor;
        this.amhsRejectedExceptionHelper = amhsRejectedExceptionHelper;
    }

    public void consume(final AmhsMessage m) {
        LOG.info("processing {}", m);
        final AmhsMessageType type = AmhsMessageTypeDetector.guessType (m.getBody());
        final AmhsParsedMessage p = new AmhsParsedMessage (m, type);
        final AmhsMessageContext ctx = new AmhsMessageContext (p);
        if (!tryParse (ctx))
            return;
        tryProcess (ctx);
    }
    
    private boolean tryParse (final AmhsMessageContext ctx) {
        try {
            amhsMessageParser.parse (ctx);
            ctx.check();
            return true;
        }
        catch (final Exception x) {
            LOG.error ("Failed to parse message: {}", x.getMessage(), x);
            // FIXME: there's no GUI for correcting these rejects
            //amhsMessageProcessor.reject (ctx.getAmhsParsedMessage(), amhsRejectedExceptionHelper.parseError(x));
            if (ctx.getType() == AmhsMessageType.FPL) {
                return true;
            }
            return false;
        }
    }
    
    private boolean tryProcess (final AmhsMessageContext ctx) {
        try {
            amhsMessageProcessor.process (ctx);
            ctx.check();
            return true;
        }
        catch (final Exception x) {
            LOG.error ("Failed to process message: {}", x.getMessage(), x);
            //amhsMessageProcessor.reject (ctx.getAmhsParsedMessage(), amhsRejectedExceptionHelper.validationError(x));
            return false;
        }
    }
    
    private static final Logger LOG = LoggerFactory.getLogger(AmhsMessageConsumer.class);
    private final AmhsMessageParser amhsMessageParser;
    private final AmhsMessageProcessor amhsMessageProcessor;
    @SuppressWarnings("unused")
    private final AmhsRejectedExceptionHelper amhsRejectedExceptionHelper;
}
