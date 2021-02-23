package ca.ids.abms.plugins.caab.sage.modules.billings;

import ca.ids.abms.plugins.common.modules.AbstractPluginBillingLedgerCacheable;
import org.springframework.stereotype.Component;

@Component
public class CaabSageBillingLedgerCacheable extends AbstractPluginBillingLedgerCacheable {

    public CaabSageBillingLedgerCacheable(final CaabSageBillingLedgerService caabSageBillingLedgerService) {
        super(caabSageBillingLedgerService);
    }
}
