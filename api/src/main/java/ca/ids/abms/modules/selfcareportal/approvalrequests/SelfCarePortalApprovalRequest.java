package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
public class SelfCarePortalApprovalRequest extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @SearchableEntity
    private Account account;

    @ManyToOne
    @NotNull
    @SearchableEntity
    private User user;

    @Size(max = 25)
    @NotNull
    @SearchableText
    private String requestType;

    @Size(max = 50)
    @NotNull
    @SearchableText
    private String requestDataset;

    private Integer objectId;

    @NotNull
    @SearchableText
    private String requestText;

    @Size(max = 25)
    @NotNull
    @SearchableText
    private String status;

    @Size(max = 100)
    @SearchableText
    private String respondersName;

    private LocalDateTime responseDate;

    @Size(max = 100)
    @SearchableText
    private String responseText;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getRequestDataset() {
        return requestDataset;
    }

    public void setRequestDataset(String requestDataset) {
        this.requestDataset = requestDataset;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRespondersName() {
        return respondersName;
    }

    public void setRespondersName(String respondersName) {
        this.respondersName = respondersName;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelfCarePortalApprovalRequest)) return false;

        SelfCarePortalApprovalRequest that = (SelfCarePortalApprovalRequest) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "SelfCarePortalApprovalRequest{" +
            "id=" + id +
            ", account=" + account +
            ", user=" + user +
            ", requestType='" + requestType + '\'' +
            ", requestDataset='" + requestDataset + '\'' +
            ", objectId=" + objectId +
            ", requestText='" + requestText + '\'' +
            ", status='" + status + '\'' +
            ", respondersName='" + respondersName + '\'' +
            ", responseDate=" + responseDate +
            ", responseText='" + responseText + '\'' +
            '}';
    }
}
