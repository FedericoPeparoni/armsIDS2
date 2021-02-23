package ca.ids.abms.modules.workflows;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class ApprovalWorkflowCsvExportModel {

    private Integer level;

    private StatusType statusType;

    private String approvalName;

    private String approvalGroup;

    @CsvProperty(precision = 2)
    private Double thresholdAmount;

    private String thresholdCurrency;

    @CsvProperty(value = "Approval Under")
    private Integer approvalUnderLevel;

    @CsvProperty(value = "Approval Equal Or Over")
    private Integer approvalOverLevel;

    @CsvProperty(value = "Rejected")
    private Integer rejectedLevel;

    @CsvProperty(value = "Delete Rejected")
    private Boolean delete;

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    public String getApprovalGroup() {
        return approvalGroup;
    }

    public void setApprovalGroup(String approvalGroup) {
        this.approvalGroup = approvalGroup;
    }

    public Double getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Double thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public String getThresholdCurrency() {
        return thresholdCurrency;
    }

    public void setThresholdCurrency(String thresholdCurrency) {
        this.thresholdCurrency = thresholdCurrency;
    }

    public Integer getApprovalUnderLevel() {
        return approvalUnderLevel;
    }

    public void setApprovalUnderLevel(Integer approvalUnderLevel) {
        this.approvalUnderLevel = approvalUnderLevel;
    }

    public Integer getApprovalOverLevel() {
        return approvalOverLevel;
    }

    public void setApprovalOverLevel(Integer approvalOverLevel) {
        this.approvalOverLevel = approvalOverLevel;
    }

    public Integer getRejectedLevel() {
        return rejectedLevel;
    }

    public void setRejectedLevel(Integer rejectedLevel) {
        this.rejectedLevel = rejectedLevel;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }
}
