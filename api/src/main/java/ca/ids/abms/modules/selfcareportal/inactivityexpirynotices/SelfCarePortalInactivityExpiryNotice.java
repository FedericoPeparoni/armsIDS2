package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.enumerate.NoticeType;
import ca.ids.abms.modules.util.models.AuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class SelfCarePortalInactivityExpiryNotice extends AuditedEntity implements AbmsCrudEntity<Integer> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "account_id")
    @SearchableEntity(searchableField = "name")
    private Account account;

    @NotNull
    @Enumerated(EnumType.STRING)
    @SearchableText
    private NoticeType noticeType;

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

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SelfCarePortalInactivityExpiryNotice that = (SelfCarePortalInactivityExpiryNotice) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SelfCarePortalInactivityExpiryNotice{" +
            "id=" + id +
            ", account=" + account +
            ", noticeType=" + noticeType +
            ", dateTime=" + dateTime +
            ", messageText='" + messageText + '\'' +
            '}';
    }
}
