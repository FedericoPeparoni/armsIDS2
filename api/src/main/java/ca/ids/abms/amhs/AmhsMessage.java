package ca.ids.abms.amhs;

import java.time.LocalDateTime;

public class AmhsMessage {

    private String body;
    private LocalDateTime filingDateTime;
    private String filename;
    private String rawMessageText;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getFilingDateTime() {
        return filingDateTime;
    }

    public void setFilingDateTime(LocalDateTime filingDateTime) {
        this.filingDateTime = filingDateTime;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getRawMessageText() {
        return rawMessageText;
    }

    public void setRawMessageText(String rawMessageText) {
        this.rawMessageText = rawMessageText;
    }

    @Override
    public String toString() {
        return "AmhsMessage ["
                + (filename != null ? "filename=" + filename + ", " : "")
                + (filingDateTime != null ? "filingDateTime=" + filingDateTime + ", " : "")
                + (body != null ? "body=" + ca.ids.abms.util.StringUtils.abbrev(body) : "")
                + "]";
    }

}
