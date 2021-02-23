package ca.ids.abms.plugins.kcaa.erp.modules.system;

public class KcaaErpConfigurationItemName {

    public static final String ENROUTE_FEES_ID = "KCAA ERP Enroute Fees Id";

    public static final String DATABASE_CONNECTION_URL = "KCAA ERP Database Connection URL";

    public static final String AIRCRAFT_REGISTRATION_PROCESSOR_STARTING_TIMESTAMP = "KCAA ERP aircraft registration processor starting timestamp";

    public static final String RECEIPT_PROCESSOR_STARTING_TIMESTAMP = "KCAA ERP receipt processor starting timestamp";

    private KcaaErpConfigurationItemName() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
