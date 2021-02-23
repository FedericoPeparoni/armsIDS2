package ca.ids.abms.plugins.kcaa.eaip.modules.transactions;

import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionCacheable;
import org.springframework.stereotype.Component;

@Component
public class KcaaEaipTransactionCacheable extends AbstractPluginTransactionCacheable {

    public KcaaEaipTransactionCacheable(final KcaaEaipTransactionService kcaaEaipTransactionService) {
        super(kcaaEaipTransactionService);
    }
}
