package ca.ids.abms.util.billingcontext;

import ca.ids.abms.modules.airspaces.AirspaceService;
import ca.ids.abms.modules.currencies.CurrencyService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BillingContextUtility {

    private final AirspaceService airspaceService;
    private final CurrencyService currencyService;

    public BillingContextUtility(
        AirspaceService airspaceService,
        CurrencyService currencyService
    ) {
        this.airspaceService = airspaceService;
        this.currencyService = currencyService;
    }

    /**
     * Perform passed method in try / finally block
     * 1. Sets thread local variables
     * 2. Performs passed lambda method
     * 3. Cleans up thread local variables
     *
     * @param context representing the keys that should be set within the billing context
     * @param runnable method to be wrapped in try / finally block
     */
    public void perform(final Map<BillingContextKey, Object> context, final Runnable runnable){
        try {
            // set billing context
            setBillingContext(context);

            // run lambda method
            runnable.run();

        } finally {
            // clean up to avoid memory leaks and persistence
            BillingContext.clear();
        }
    }

    /**
     * Sets thread local variables by performing methods defined by specifying BillingContextKey.
     */
    private void setBillingContext(final Map<BillingContextKey, Object> context) {
        if (context == null || context.isEmpty()) {
            return;
        }

        for (Map.Entry<BillingContextKey, Object> entry : context.entrySet()) {
            switch (entry.getKey()) {
                case BILLABLE_AIRSPACE:
                    BillingContext.put(BillingContextKey.BILLABLE_AIRSPACE,
                        airspaceService.getBillableAirspacesForRouteParser());
                    break;
                case ANSP_CURRENCY:
                    BillingContext.put(BillingContextKey.ANSP_CURRENCY,
                        currencyService.getANSPCurrency());
                    break;
                default:
                    BillingContext.put(entry.getKey(), entry.getValue());
                    break;
            }
        }
    }
}
