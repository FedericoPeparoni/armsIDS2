package ca.ids.abms.plugins.caab.sage.modules.transactions;

import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionCacheable;
import org.springframework.stereotype.Component;

@Component
public class CaabSageTransactionCacheable extends AbstractPluginTransactionCacheable {

    public CaabSageTransactionCacheable(final CaabSageTransactionService caabSageTransactionService) {
        super(caabSageTransactionService);
    }
}
