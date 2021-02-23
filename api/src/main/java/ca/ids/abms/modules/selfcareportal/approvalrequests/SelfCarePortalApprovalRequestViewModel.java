package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.VersionedViewModel;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class SelfCarePortalApprovalRequestViewModel extends VersionedViewModel {

    private Integer id;

    private Account account;

    @NotNull
    private User user;

    @Size(max = 25)
    @NotNull
    private String requestType;

    @Size(max = 50)
    @NotNull
    private String requestDataset;

    private Integer objectId;

    @NotNull
    private String requestText;

    @Size(max = 25)
    @NotNull
    private String status;

    @Size(max = 100)
    private String respondersName;

    private LocalDateTime responseDate;

    @Size(max = 100)
    private String responseText;

    private LocalDateTime createdAt;

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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SelfCarePortalApprovalRequestViewModel)) return false;

        SelfCarePortalApprovalRequestViewModel that = (SelfCarePortalApprovalRequestViewModel) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "SelfCarePortalApprovalRequestViewModel{" +
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
