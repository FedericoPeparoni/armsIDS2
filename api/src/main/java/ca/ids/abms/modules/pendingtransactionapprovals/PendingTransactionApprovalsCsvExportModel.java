package ca.ids.abms.modules.pendingtransactionapprovals;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

public class PendingTransactionApprovalsCsvExportModel {

    private String action;

    private String approverName;

    private Integer approvalLevel;

    @CsvProperty(value = "Approval Date/Time", dateTime = true)
    private LocalDateTime approvalDateTime;

    private String approvalNotes;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }

    public void setApprovalLevel(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public LocalDateTime getApprovalDateTime() {
        return approvalDateTime;
    }

    public void setApprovalDateTime(LocalDateTime approvalDateTime) {
        this.approvalDateTime = approvalDateTime;
    }

    public String getApprovalNotes() {
        return approvalNotes;
    }

    public void setApprovalNotes(String approvalNotes) {
        this.approvalNotes = approvalNotes;
    }
}
