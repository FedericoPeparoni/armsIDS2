package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.modules.accounts.AccountComboViewModel;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class SelfCarePortalInactivityExpiryNoticesViewModel {

    private Integer id;

    @NotNull
    private AccountComboViewModel account;

    @NotNull
    private String noticeType;

    @NotNull
    private LocalDateTime dateTime;

    @NotNull
    @Column(length = 500)
    private String messageText;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AccountComboViewModel getAccount() {
        return account;
    }

    public void setAccount(AccountComboViewModel account) {
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

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
