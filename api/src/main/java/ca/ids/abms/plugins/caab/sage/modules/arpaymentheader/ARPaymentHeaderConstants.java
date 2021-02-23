package ca.ids.abms.plugins.caab.sage.modules.arpaymentheader;

@SuppressWarnings("unused")
class ARPaymentHeaderConstants {

    /**
     * Misc DocumentType as per integration document (1=receipt, 2=prepayment, 3=misc).
     */
    static final String DOCUMENT_TYPE_MISC = "3";

    /**
     * Receipt DocumentType as per integration document (1=receipt, 2=prepayment, 3=misc).
     */
    static final String DOCUMENT_TYPE_RECEIPT = "1";

    /**
     * Prepayment DocumentType as per integration document (1=receipt, 2=prepayment, 3=misc).
     */
    static final String DOCUMENT_TYPE_PREPAYMENT = "2";

    private ARPaymentHeaderConstants() {
        throw new IllegalStateException("Constants class should never be instantiated!");
    }
}
