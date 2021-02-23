package ca.ids.abms.plugins.amhs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Helper class for FPL parsers
 */
public class AmhsMessageContext {

    public AmhsMessageContext (final AmhsParsedMessage amhsParsedMessage) {
        Preconditions.checkNotNull (amhsParsedMessage);
        Preconditions.checkNotNull (amhsParsedMessage.amhsMessage);
        Preconditions.checkNotNull (amhsParsedMessage.amhsMessageType);
        this.amhsParsedMessage = amhsParsedMessage;
    }
    
    public AmhsParsedMessage getAmhsParsedMessage() {
        return amhsParsedMessage;
    }
    
    public AmhsMessageType getType() {
        return amhsParsedMessage.amhsMessageType;
    }
    
    public String getBody() {
        return amhsParsedMessage.amhsMessage.getBody();
    }
    
    public void error (final String msg, final Object... args) {
        addError (msg, args);
    }
    
    public void check (boolean ok, final String msg, final Object... args) {
        if (!ok) {
            addError (msg, args);
            throw exception();
        }
    }
    
    public void invalidMessageTypeError() {
        final AmhsMessageType type = amhsParsedMessage.amhsMessageType;
        if (type == AmhsMessageType.UNKNOWN) {
            addError ("Unknown message type");
        }
        else {
            addError ("Unsupported message type {0}", type);
        }
        check();
    }
    
    public void checkMessageCategory (final AmhsMessageCategory category) {
        final AmhsMessageType type = amhsParsedMessage.amhsMessageType;
        if (type.getCategory() != category) {
            invalidMessageTypeError();
        }
    }
    
    public void checkMessageType (final AmhsMessageType type) {
        final AmhsMessageType t = amhsParsedMessage.amhsMessageType;
        if (type != t) {
            invalidMessageTypeError();
        }
    }
    
    public void check() {
        if (!errorList.isEmpty()) {
            throw exception();
        }
    }
    
    public boolean hasErrors() {
        return !errorList.isEmpty();
    }
    
    public void clear() {
        errorList.clear();
    }
    
    // -------------- private -------------------
    
    private String prefix() {
        return "";
    }
    
    private AmhsParserException exception() {
        return new AmhsParserException (amhsParsedMessage, errorList.get(0));
    }

    private void addError (final String msg, final Object... args) {
        errorList.add (prefix() + MessageFormat.format(msg, args));
    }
    
    private final AmhsParsedMessage amhsParsedMessage;
    private final List <String> errorList = new ArrayList<>();
}
