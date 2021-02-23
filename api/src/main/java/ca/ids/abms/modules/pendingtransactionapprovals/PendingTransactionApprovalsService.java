package ca.ids.abms.modules.pendingtransactionapprovals;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PendingTransactionApprovalsService {

    private PendingTransactionApprovalsRepository pendingTransactionApprovalsRepository;
    private static final Logger LOG = LoggerFactory.getLogger(PendingTransactionApprovalsService.class);

    public PendingTransactionApprovalsService(PendingTransactionApprovalsRepository pendingTransactionApprovalsRepository) {
        this.pendingTransactionApprovalsRepository = pendingTransactionApprovalsRepository;
    }

    @Transactional(readOnly = true)
    public List<PendingTransactionApprovals> findAll() {
        LOG.debug("Request to get all Pending Transaction Approvals");
        return pendingTransactionApprovalsRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<PendingTransactionApprovals> findAll(Pageable pageable) {
        LOG.debug("Request to get all Pending Transaction Approvals");
        return pendingTransactionApprovalsRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<PendingTransactionApprovals> getByPendingTransactionId(Integer pendingTransactionId) {
        LOG.debug("Request to get all Pending Transaction Approvals by pending transaction id : {}", pendingTransactionId);
        return pendingTransactionApprovalsRepository.findAllByPendingTransactionId(pendingTransactionId);
    }

    public PendingTransactionApprovals save(PendingTransactionApprovals pendingTransactionApprovals) {
        LOG.debug("Request to save Pending Transaction Approvals : {}", pendingTransactionApprovals);
        return pendingTransactionApprovalsRepository.save(pendingTransactionApprovals);
    }
}
