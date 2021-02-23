package ca.ids.abms.atnmsg.amhs.addr;

import com.google.common.base.Preconditions;

@SuppressWarnings("serial")
public class InvalidAmhsAddressException extends RuntimeException {

    public InvalidAmhsAddressException(final ErrorCodes code, final String what) {
        super(what);
        this.code = code;
        Preconditions.checkNotNull(code);
        Preconditions.checkNotNull(what);
    }

    public ErrorCodes errorCode() {
        return code;
    }

    private final ErrorCodes code;

}
