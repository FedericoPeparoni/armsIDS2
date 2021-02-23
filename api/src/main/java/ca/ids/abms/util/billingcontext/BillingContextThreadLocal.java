package ca.ids.abms.util.billingcontext;

import java.util.EnumMap;
import java.util.Map;

class BillingContextThreadLocal extends InheritableThreadLocal<Map<BillingContextKey, Object>> {

    @Override
    protected Map<BillingContextKey, Object> childValue(Map<BillingContextKey, Object> parentValue) {
        return parentValue == null ? null
            : new EnumMap<>(parentValue);
    }
}
