package ca.ids.abms.modules.workflows;

import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@UniqueKey(columnNames={"level", "approvalName"}, checkSeparately = true)
public class ApprovalWorkflow extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    private Integer level;

    @NotNull
    @Enumerated(EnumType.STRING)
    private StatusType statusType;

    @NotNull
    @Size(max = 100)
    @SearchableText
    private String approvalName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "approval_group")
    @MergeOnNull
    private Role approvalGroup;

    @MergeOnNull
    private Double thresholdAmount;

    @ManyToOne
    @JoinColumn(name = "thresholdCurrency")
    @MergeOnNull
    private Currency thresholdCurrency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_under")
    @MergeOnNull
    @JsonIgnore
    private ApprovalWorkflow approvalUnder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_over")
    @MergeOnNull
    @JsonIgnore
    private ApprovalWorkflow approvalOver;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rejected")
    @MergeOnNull
    @JsonIgnore
    private ApprovalWorkflow rejected;

    @Transient
    private Integer approvalUnderLevel;

    @Transient
    private Integer approvalOverLevel;

    @Transient
    private Integer rejectedLevel;

    @NotNull
    private Boolean delete;

    @NotNull
    private Boolean approvalDocumentRequired;

    public Integer getId() {
        return id;
    }

    public Boolean getApprovalDocumentRequired() {
        return approvalDocumentRequired;
    }

    public void setApprovalDocumentRequired(Boolean approvalDocumentRequired) {
        this.approvalDocumentRequired = approvalDocumentRequired;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Role getApprovalGroup() {
        return approvalGroup;
    }

    public void setApprovalGroup(Role approvalGroup) {
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

    public ApprovalWorkflow getApprovalUnder() {
        return approvalUnder;
    }

    public void setApprovalUnder(ApprovalWorkflow approvalUnder) {
        this.approvalUnder = approvalUnder;
    }

    public ApprovalWorkflow getApprovalOver() {
        return approvalOver;
    }

    public void setApprovalOver(ApprovalWorkflow approvalOver) {
        this.approvalOver = approvalOver;
    }

    public ApprovalWorkflow getRejected() {
        return rejected;
    }

    public void setRejected(ApprovalWorkflow rejected) {
        this.rejected = rejected;
    }

    public StatusType getStatusType() {
        return statusType;
    }

    public void setStatusType(StatusType statusType) {
        this.statusType = statusType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (!(o instanceof ApprovalWorkflow)) {
            return false;
        }

        ApprovalWorkflow that = (ApprovalWorkflow) o;

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
        return "ApprovalWorkflow{" +
            "id=" + id +
            ", level=" + level +
            ", statusType=" + statusType +
            ", approvalName='" + approvalName + '\'' +
            ", approvalGroup=" + approvalGroup +
            ", thresholdAmount=" + thresholdAmount +
            ", thresholdCurrency=" + thresholdCurrency +
            ", approvalUnder=" + (approvalUnder != null ? approvalUnder.approvalName : '-') +
            ", approvalOver=" + (approvalOver != null ? approvalOver.approvalName : '-') +
            ", rejected=" + (rejected != null ? rejected.approvalName : '-') +
            ", delete=" + delete +
            ", approvalUnderLevel=" + approvalUnderLevel +
            ", approvalOverLevel=" + approvalOverLevel +
            ", rejectedLevel=" + rejectedLevel +
            ", approvalDocumentRequired=" + approvalDocumentRequired +
            "}";
    }
}
