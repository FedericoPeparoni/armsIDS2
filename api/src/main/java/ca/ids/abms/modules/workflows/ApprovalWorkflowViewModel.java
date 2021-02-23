package ca.ids.abms.modules.workflows;

import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.roles.RoleViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ApprovalWorkflowViewModel extends VersionedViewModel {

    private Integer id;

    @NotNull
    private Integer level;

    @NotNull
    private StatusType statusType;

    @NotNull
    @Size(max = 100)
    private String approvalName;

    @MergeOnNull
    private RoleViewModel approvalGroup;

    @MergeOnNull
    private Double thresholdAmount;

    @MergeOnNull
    private Currency thresholdCurrency;

    private Integer approvalUnderLevel;

    private Integer approvalOverLevel;

    private Integer rejectedLevel;

    @NotNull
    private Boolean delete;

    @NotNull
    private Boolean approvalDocumentRequired;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getApprovalDocumentRequired() {
        return approvalDocumentRequired;
    }

    public void setApprovalDocumentRequired(Boolean approvalDocumentRequired) {
        this.approvalDocumentRequired = approvalDocumentRequired;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getApprovalName() {
        return approvalName;
    }

    public void setApprovalName(String approvalName) {
        this.approvalName = approvalName;
    }

    public RoleViewModel getApprovalGroup() {
        return approvalGroup;
    }

    public void setApprovalGroup(RoleViewModel approvalGroup) {
        this.approvalGroup = approvalGroup;
    }

    public Double getThresholdAmount() {
        return thresholdAmount;
    }

    public void setThresholdAmount(Double thresholdAmount) {
        this.thresholdAmount = thresholdAmount;
    }

    public Currency getThresholdCurrency() {
        return thresholdCurrency;
    }

    public void setThresholdCurrency(Currency thresholdCurrency) {
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

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
    }

    public Boolean getDelete() {
        return delete;
    }

    public void setDelete(Boolean delete) {
        this.delete = delete;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof ApprovalWorkflowViewModel)) {
            return false;
        }

        ApprovalWorkflowViewModel that = (ApprovalWorkflowViewModel) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (level != null ? level.hashCode() : 0);
        result = 31 * result + (statusType != null ? statusType.hashCode() : 0);
        result = 31 * result + (approvalName != null ? approvalName.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ApprovalWorkflowViewModel{" +
            "id=" + id +
            ", level=" + level +
            ", statusType=" + statusType +
            ", approvalName='" + approvalName + '\'' +
            ", approvalGroup=" + approvalGroup +
            ", thresholdAmount=" + thresholdAmount +
            ", thresholdCurrency=" + thresholdCurrency +
            ", delete=" + delete +
            ", approvalUnderLevel=" + approvalUnderLevel +
            ", approvalOverLevel=" + approvalOverLevel +
            ", rejectedLevel=" + rejectedLevel +
            ", approvalDocumentRequired=" + approvalDocumentRequired +
            "}";
    }
}
