package ca.ids.abms.modules.transactionapprovals;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionApprovalsRepository extends JpaRepository<TransactionApprovals, Integer> {

    List<TransactionApprovals> findAllByTransactionId (Integer transactionId);
}
