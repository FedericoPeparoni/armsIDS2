package ca.ids.abms.plugins.amhs;

import ca.ids.abms.amhs.AmhsMessage;
import ca.ids.abms.plugins.amhs.fpl.FlightMessage;

public class AmhsParsedMessage {
    
    public AmhsParsedMessage() {
    }
    
    public AmhsParsedMessage(AmhsMessage m, AmhsMessageType type) {
        this.amhsMessage = m;
        this.amhsMessageType = type;
    }
    
    @Override
    public String toString() {
        return "AmhsParsedMessage [" + (amhsMessage != null ? "amhsMessage=" + amhsMessage + ", " : "")
                + (amhsMessageType != null ? "amhsMessageType=" + amhsMessageType + ", " : "")
                + (flightMessage != null ? "flightMessage=" + flightMessage : "") + "]";
    }

    public AmhsMessage amhsMessage;
    public AmhsMessageType amhsMessageType;
    public FlightMessage flightMessage;

}
