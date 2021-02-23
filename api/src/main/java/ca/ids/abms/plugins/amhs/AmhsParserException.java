package ca.ids.abms.plugins.amhs;

@SuppressWarnings("serial")
public class AmhsParserException extends RuntimeException {
    
    public AmhsParserException (final AmhsParsedMessage amhsParsedMessage, final String what) {
        super (what);
        this.amhsParsedMessage = amhsParsedMessage;
    }
    
    public AmhsParsedMessage getAmhsParsedMessage() {
        return amhsParsedMessage;
    }
    
    private final AmhsParsedMessage amhsParsedMessage;
    
}
