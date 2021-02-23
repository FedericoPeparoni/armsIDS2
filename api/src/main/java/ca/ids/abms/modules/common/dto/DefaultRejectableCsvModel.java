package ca.ids.abms.modules.common.dto;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.modules.dataimport.RejectableCsvModel;

public class DefaultRejectableCsvModel implements RejectableCsvModel {

    transient private String rawText;

    transient private boolean isParsed;

    transient private long line;

    transient private ErrorDTO errorMessage;

    @Override
    public String getRawText() {
        return rawText;
    }

    @Override
    public void setRawText(String rawText) {
        this.rawText = rawText;
    }

    @Override
    public boolean isParsed() {
        return isParsed;
    }

    @Override
    public void setParsed(boolean parsed) {
        isParsed = parsed;
    }

    @Override
    public long getLine() {
        return line;
    }

    @Override
    public void setLine(long line) {
        this.line = line;
    }

    @Override
    public ErrorDTO getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(ErrorDTO errorMessage) {
        this.errorMessage = errorMessage;
    }
}
