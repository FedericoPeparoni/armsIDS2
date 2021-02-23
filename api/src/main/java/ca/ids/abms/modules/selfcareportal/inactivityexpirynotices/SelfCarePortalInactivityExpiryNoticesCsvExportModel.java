package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class SelfCarePortalInactivityExpiryNoticesCsvExportModel {

    private String account;

    private String noticeType;

    @CsvProperty(value = "Date/Time", dateTime = true)
    private LocalDateTime dateTime;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }
}
