package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderConstants;

public final class SalesLineConstants {

    // representing 'G/L Account' in Kcaa Erp System
    public static final String TYPE = "1";

    static final String CREDIT_DOCUMENT_TYPE = SalesHeaderConstants.CREDIT_DOCUMENT_TYPE;

    static final String DEBIT_DOCUMENT_TYPE = SalesHeaderConstants.DEBIT_DOCUMENT_TYPE;

    static final String SHORTCUT_DIMENSION_CODE_1 = SalesHeaderConstants.SHORTCUT_DIMENSION_CODE_1;

    private SalesLineConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
