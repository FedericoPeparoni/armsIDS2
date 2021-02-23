package ca.ids.abms.plugins.amhs.fpl;

import java.util.EnumMap;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import ca.ids.abms.plugins.amhs.AmhsMessageCategory;
import ca.ids.abms.plugins.amhs.AmhsMessageContext;
import ca.ids.abms.plugins.amhs.AmhsMessageType;

@Component
public class FlightMessageProcessor {

    public FlightMessageProcessor (
            final FplMessageProcessor fplMessageProcessor,
            final DlaMessageProcessor dlaMessageProcessor,
            final CnlMessageProcessor cnlMessageProcessor,
            final DepMessageProcessor depMessageProcessor,
            final ArrMessageProcessor arrMessageProcessor,
            final ChgMessageProcessor chgMessageProcessor
    ) {
        this.fplMessageProcessor = fplMessageProcessor;
        this.dlaMessageProcessor = dlaMessageProcessor;
        this.cnlMessageProcessor = cnlMessageProcessor;
        this.depMessageProcessor = depMessageProcessor;
        this.arrMessageProcessor = arrMessageProcessor;
        this.chgMessageProcessor = chgMessageProcessor;
        this.processorMap = createProcessorMap();
    }
    
    public void process (final AmhsMessageContext ctx) {
        ctx.checkMessageCategory(AmhsMessageCategory.FPL);
        final AmhsMessageType type = ctx.getAmhsParsedMessage().amhsMessageType;
        final Consumer<AmhsMessageContext> consumer = processorMap.get (type);
        consumer.accept(ctx);
    }
    
    private EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> createProcessorMap() {
        EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> m = new EnumMap<> (AmhsMessageType.class);
        m.put (AmhsMessageType.FPL, fplMessageProcessor::process);
        m.put (AmhsMessageType.DLA, dlaMessageProcessor::process);
        m.put (AmhsMessageType.CNL, cnlMessageProcessor::process);
        m.put (AmhsMessageType.DEP, depMessageProcessor::process);
        m.put (AmhsMessageType.ARR, arrMessageProcessor::process);
        m.put (AmhsMessageType.CHG, chgMessageProcessor::process);
        return m;
    }
    
    private final FplMessageProcessor fplMessageProcessor;
    private final DlaMessageProcessor dlaMessageProcessor;
    private final CnlMessageProcessor cnlMessageProcessor;
    private final DepMessageProcessor depMessageProcessor;
    private final ArrMessageProcessor arrMessageProcessor;
    private final ChgMessageProcessor chgMessageProcessor;
    private final EnumMap <AmhsMessageType, Consumer <AmhsMessageContext>> processorMap;
}
