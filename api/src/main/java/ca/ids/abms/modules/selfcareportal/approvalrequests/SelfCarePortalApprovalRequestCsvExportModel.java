package ca.ids.abms.modules.selfcareportal.approvalrequests;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class SelfCarePortalApprovalRequestCsvExportModel {

    private String status;

    @CsvProperty(value = "Date/Time of Request", dateTime = true)
    private LocalDateTime createdAt;

    private String requestType;

    private String requestDataset;

    private String account;

    private String user;

    private Integer objectId;

    private String respondersName;

    @CsvProperty(dateTime = true)
    private LocalDateTime responseDate;

    private String responseText;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
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
}
