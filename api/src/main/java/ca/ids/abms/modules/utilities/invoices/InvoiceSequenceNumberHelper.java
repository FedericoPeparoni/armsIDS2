package ca.ids.abms.modules.utilities.invoices;

import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;

/**
 * Utility component for safely generating invoice and receipt numbers.
 * It returns a string that contains a prefix and an sequence number, while
 * simultaneously locking and incrementing the sequence number field in the
 * relevant billing center record.
 *
 * <h2>Example</h2>
 * <pre><code>
 * InvoiceSequenceNumberHelper helper = ...;
 * final String invoiceSeqNum = helper.generator().nextInvoiceSequenceNumber();
 * </code></pre>
 *
 * The Generator interface caches some of the database calls, so it is more efficient
 * to construct one generator and obtained new sequence numbers multiple times.
 *
 */
public interface InvoiceSequenceNumberHelper {

    /** Get a generator object */
    Generator generator();

    /** Sequence number generation interface */
    interface Generator {

        /** Safely generate next invoice sequencey number */
        String nextInvoiceSequenceNumber(InvoiceType invoiceType);
        
        /** Safely generate next receipt sequencey number */
        String nextReceiptSequenceNumber(TransactionPaymentMechanism transPayMechanism);

        /** Safely get invoice sequencey number type*/
        String getInvoiceSequenceNumberType(InvoiceType invoiceType);
        
        /** Safely get receipt sequencey number type*/
        String getReceiptSequenceNumberType(TransactionPaymentMechanism transPayMechanism);
    }
}
