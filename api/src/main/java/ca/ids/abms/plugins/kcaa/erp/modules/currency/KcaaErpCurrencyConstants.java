package ca.ids.abms.plugins.kcaa.erp.modules.currency;

public class KcaaErpCurrencyConstants {

    // never set this currency in ERP database, blank is considered KES
    public static final String BLANK_CURRENCY_CODE = "KES";

    private KcaaErpCurrencyConstants() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
