package ca.ids.abms.modules.transactionapprovals;

import ca.ids.abms.modules.transactions.Transaction;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

public class TransactionApprovalsViewModel {

    private Integer id;

    @NotNull
    private Transaction transaction;

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

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
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
        TransactionApprovalsViewModel that = (TransactionApprovalsViewModel) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TransactionApprovalsViewModel{" +
            "id=" + id +
            ", transaction=" + transaction +
            ", action=" + action +
            ", approverName='" + approverName + '\'' +
            ", approvalDateTime=" + approvalDateTime +
            ", approvalLevel=" + approvalLevel +
            ", approvalNotes='" + approvalNotes + '\'' +
            '}';
    }
}
