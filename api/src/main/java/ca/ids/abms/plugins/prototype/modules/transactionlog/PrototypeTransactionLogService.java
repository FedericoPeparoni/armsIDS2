package ca.ids.abms.plugins.prototype.modules.transactionlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PrototypeTransactionLogService {

    final private Logger LOG = LoggerFactory.getLogger(PrototypeTransactionLogService.class);

    private final PrototypeTransactionLogRepository prototypeTransactionLogRepository;

    public PrototypeTransactionLogService(PrototypeTransactionLogRepository prototypeTransactionLogRepository) {
        this.prototypeTransactionLogRepository = prototypeTransactionLogRepository;
    }

    public void save(PrototypeTransactionLog transactionLog) {
        LOG.debug("Attempting to 'save' with transaction log {} for mock plugin.", transactionLog);
        prototypeTransactionLogRepository.insert(transactionLog);
        LOG.debug("Successfully completed 'save' with transaction log {} for mock plugin.", transactionLog);
    }
}
