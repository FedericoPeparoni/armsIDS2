package ca.ids.abms.modules.transactionapprovals;

import ca.ids.abms.modules.pendingtransactionapprovals.PendingTransactionApprovals;
import ca.ids.abms.modules.pendingtransactionapprovals.enumerate.PendingTransactionAction;
import ca.ids.abms.modules.transactions.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class TransactionApprovalsService {

    private TransactionApprovalsRepository transactionApprovalsRepository;
    private static final Logger LOG = LoggerFactory.getLogger(TransactionApprovalsService.class);

    public TransactionApprovalsService(TransactionApprovalsRepository transactionApprovalsRepository) {
        this.transactionApprovalsRepository = transactionApprovalsRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionApprovals> findAll() {
        LOG.debug("Request to get all Transaction Approvals");
        return transactionApprovalsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<TransactionApprovals> findAll(Pageable pageable) {
        LOG.debug("Request to get all Transaction Approvals");
        return transactionApprovalsRepository.findAll(pageable);
    }

    public TransactionApprovals save(TransactionApprovals transactionApprovals) {
        LOG.debug("Request to save Transaction Approvals : {}", transactionApprovals);
        return transactionApprovalsRepository.save(transactionApprovals);
    }

    public TransactionApprovals addTransactionApprovalsFromPendingTransactionApprovals (final PendingTransactionApprovals pendingTransactionApprovals, final Transaction transaction) {
        LOG.debug("Request to create TransactionApprovals form PendingTransactionApprovals for the Transaction: {}, Action: {}", transaction.getId(), pendingTransactionApprovals.getAction());
        TransactionApprovals newTransactionApprovals = new TransactionApprovals(
            transaction,
            pendingTransactionApprovals.getAction(),
            pendingTransactionApprovals.getApproverName(),
            pendingTransactionApprovals.getApprovalDateTime(),
            pendingTransactionApprovals.getApprovalLevel(),
            pendingTransactionApprovals.getApprovalNotes());

        return save(newTransactionApprovals);
    }

    public TransactionApprovals addTransactionApprovals (final Transaction transaction, final String userName,
                                                         final String userNotes, final Integer level, final PendingTransactionAction action) {
        LOG.debug("Request to create TransactionApprovals for the Transaction: {}, Action: {}", transaction.getId(), action);
        TransactionApprovals newTransactionApprovals = new TransactionApprovals(
            transaction, action.toValue(), userName, LocalDateTime.now(), level, userNotes);

        return save(newTransactionApprovals);
    }

    @Transactional(readOnly = true)
    public List<TransactionApprovals> getAllTransactionApprovalsByTransactionId(Integer transactionId) {
        LOG.debug("Request to get all Transaction Approvals by transaction id : {}", transactionId);
        return transactionApprovalsRepository.findAllByTransactionId(transactionId);
    }
}
