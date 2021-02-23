package ca.ids.abms.modules.pendingtransactionapprovals;

import ca.ids.abms.modules.pendingtransactions.PendingTransaction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

public class PendingTransactionApprovalsViewModel {

    private Integer id;

    @NotNull
    private PendingTransaction pendingTransaction;

    @NotNull
    private String action;

    @NotNull
    @Size(max = 100)
    private String approverName;

    @NotNull
    private LocalDateTime approvalDateTime;

    @NotNull
    private Integer approvalLevel;

    @Size(max = 255)
    private String approvalNotes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public PendingTransaction getPendingTransaction() {
        return pendingTransaction;
    }

    public void setPendingTransaction(PendingTransaction pendingTransaction) {
        this.pendingTransaction = pendingTransaction;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public LocalDateTime getApprovalDateTime() {
        return approvalDateTime;
    }

    public void setApprovalDateTime(LocalDateTime approvalDateTime) {
        this.approvalDateTime = approvalDateTime;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingTransactionApprovalsViewModel that = (PendingTransactionApprovalsViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PendingTransactionApprovalsViewModel{" +
            "id=" + id +
            ", pendingTransaction=" + pendingTransaction +
            ", action=" + action +
            ", approverName='" + approverName + '\'' +
            ", approvalDateTime=" + approvalDateTime +
            ", approvalLevel=" + approvalLevel +
            ", approvalNotes='" + approvalNotes + '\'' +
            '}';
    }
}
