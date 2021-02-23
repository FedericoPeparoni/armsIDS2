package ca.ids.abms.plugins.kcaa.erp.modules.transactions;

import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionCacheable;
import org.springframework.stereotype.Component;

@Component
public class KcaaErpTransactionCacheable extends AbstractPluginTransactionCacheable {

    public KcaaErpTransactionCacheable(
        final KcaaErpTransactionService kcaaErpTransactionService
    ) {
        super(kcaaErpTransactionService);
    }
}
