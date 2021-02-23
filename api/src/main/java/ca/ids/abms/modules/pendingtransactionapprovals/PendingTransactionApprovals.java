package ca.ids.abms.modules.pendingtransactionapprovals;

import ca.ids.abms.modules.pendingtransactions.PendingTransaction;
import ca.ids.abms.modules.util.models.AuditedEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class PendingTransactionApprovals extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @JoinColumn(name = "pending_transaction_id")
    @ManyToOne
    @JsonIgnore
    private PendingTransaction pendingTransaction;

    @NotNull
    @Size(max = 100)
    private String approverName;

    @NotNull
    private LocalDateTime approvalDateTime;

    @NotNull
    private Integer approvalLevel;

    @Size(max = 255)
    private String approvalNotes;

    @NotNull
    private String action;

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

    public PendingTransactionApprovals (){

    }

    public PendingTransactionApprovals (PendingTransaction pendingTransaction,
                                        String action,
                                        String approverName,
                                        LocalDateTime approvalDateTime,
                                        Integer approvalLevel,
                                        String approvalNotes){
        this.pendingTransaction = pendingTransaction;
        this.action = action;
        this.approverName = approverName;
        this.approvalDateTime = approvalDateTime;
        this.approvalLevel = approvalLevel;
        this.approvalNotes = approvalNotes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PendingTransactionApprovals that = (PendingTransactionApprovals) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PendingTransactionApprovals{" +
            "id=" + id +
            ", pendingTransaction=" + pendingTransaction +
            ", approverName='" + approverName + '\'' +
            ", approvalDateTime=" + approvalDateTime +
            ", approvalLevel=" + approvalLevel +
            ", approvalNotes='" + approvalNotes + '\'' +
            ", action=" + action +
            '}';
    }
}
