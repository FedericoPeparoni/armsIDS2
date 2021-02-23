package ca.ids.abms.modules.selfcareportal.querysubmission;

import javax.validation.constraints.NotNull;

public class QuerySubmission {

    @NotNull
    private String senderEmail;

    @NotNull
    private String subject;

    @NotNull
    private String message;

    public String getSenderEmail() {
        return senderEmail;
    }

    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
