package ca.ids.abms.plugins.kcaa.erp.modules.billings;

import ca.ids.abms.plugins.common.modules.AbstractPluginBillingLedgerCacheable;
import org.springframework.stereotype.Component;

@Component
public class KcaaErpBillingLedgerCacheable extends AbstractPluginBillingLedgerCacheable {

    public KcaaErpBillingLedgerCacheable(
        final KcaaErpBillingLedgerService kcaaErpBillingLedgerService
    ) {
        super(kcaaErpBillingLedgerService);
    }
}
