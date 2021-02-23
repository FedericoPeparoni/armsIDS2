package ca.ids.abms.modules.transactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class TransactionTypeService {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionTypeService.class);
    private final TransactionTypeRepository transactionTypeRepository;

    public TransactionTypeService(TransactionTypeRepository aTransactionTypeRepository) {
        transactionTypeRepository = aTransactionTypeRepository;
    }

    @Transactional(readOnly = true)
    public List<TransactionType> findAll() {
        LOG.debug("Request to get transaction types");
        return transactionTypeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TransactionType findOneByName (final String name) {
        TransactionType transactionType = null;
        if (name != null && !name.isEmpty()) {
            transactionType = transactionTypeRepository.findOneByName(name);
        }
        return transactionType;
    }

}
