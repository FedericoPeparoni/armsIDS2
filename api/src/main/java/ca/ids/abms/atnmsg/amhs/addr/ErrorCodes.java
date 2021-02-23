package ca.ids.abms.atnmsg.amhs.addr;

import java.text.MessageFormat;

public enum ErrorCodes {
    EMPTY, BAD_CHARS, MISSING_EQ, EMPTY_KEY, EMPTY_VALUE, BAD_EQ, DUP_KEY, UNKNOWN_KEY, MISSING_KEY, BAD_AFTN_ADDR,
    BAD_WS, ADDR_TOO_LONG;

    RuntimeException error(final String format, final Object... args) {
        return new InvalidAmhsAddressException(this, MessageFormat.format(format, args));
    }
}
