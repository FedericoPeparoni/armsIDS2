package ca.ids.abms.modules.pendingtransactionapprovals;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PendingTransactionApprovalsRepository extends JpaRepository<PendingTransactionApprovals, Integer> {

    List<PendingTransactionApprovals> findAllByPendingTransactionId(Integer pendingTransactionId);
}
