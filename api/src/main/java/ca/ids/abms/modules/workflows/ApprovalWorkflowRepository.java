package ca.ids.abms.modules.workflows;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Integer> {

    ApprovalWorkflow findOneByLevel(Integer level);

    List<ApprovalWorkflow> findAllByStatusType(StatusType statusType);

    List<ApprovalWorkflow> findAllByDelete(Boolean delete);

    List<ApprovalWorkflow> findAllByApprovalName(String approvalName);

    List<ApprovalWorkflow> findAllByStatusTypeOrderByLevelAsc(StatusType statusType);

    ApprovalWorkflow findByApprovalDocumentRequiredTrue();
}
