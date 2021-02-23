package ca.ids.abms.plugins.prototype.modules.transactions;

import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionServiceProvider;
import ca.ids.spring.cache.annotations.CacheableOnException;
import org.springframework.stereotype.Component;

@Component
public class PrototypeTransactionServiceProvider extends TransactionServiceProvider {

    private final PrototypeTransactionMapper prototypeTransactionMapper;

    private final PrototypeTransactionService prototypeTransactionService;

    public PrototypeTransactionServiceProvider(PrototypeTransactionMapper prototypeTransactionMapper,
                                               PrototypeTransactionService prototypeTransactionService) {
        super(PluginKey.PROTOTYPE);
        this.prototypeTransactionMapper = prototypeTransactionMapper;
        this.prototypeTransactionService = prototypeTransactionService;
    }

    /**
     * Handle transaction on save event. All exceptions will be handled and retried
     * appropriately without affecting upstream `@Transactional` methods.
     *
     * @param transaction transaction saved in ABMS
     */
    @CacheableOnException(retry = true)
    @Override
    public void save(Transaction transaction) {
        this.prototypeTransactionService.saveCacheable(prototypeTransactionMapper.toPrototypeTransaction(transaction));
    }
}
