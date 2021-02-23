package ca.ids.abms.plugins.kcaa.erp.modules.salesheader;

public final class SalesHeaderConstants {

    // representing 'Credit Memo' in Kcaa Erp System
    public static final String CREDIT_DOCUMENT_TYPE = "3";

    // representing 'Invoices' in Kcaa Erp System
    public static final String DEBIT_DOCUMENT_TYPE = "2";

    // representing 'ANS Posting' in Kcaa Erp System
    public static final String SHORTCUT_DIMENSION_CODE_1 = "220";

    // representing 'Brms' in Kcaa Erp System
    static final String INVOICE_TYPE = "2";

    private SalesHeaderConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
