package ca.ids.abms.plugins.kcaa.aatis.modules.transactions;

import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionCacheable;
import org.springframework.stereotype.Component;

@Component
public class KcaaAatisTransactionCachable extends AbstractPluginTransactionCacheable {

    public KcaaAatisTransactionCachable(final KcaaAatisTransactionService kcaaAatisTransactionService) {
        super(kcaaAatisTransactionService);
    }
}
