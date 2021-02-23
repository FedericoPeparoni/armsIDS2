package ca.ids.abms.modules.reports2.transaction;

import org.springframework.stereotype.Component;

import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;

/**
 * Format a transaction receipt to PDF, etc; given its raw object representation.
 */
@Component
public class TransactionReceiptDocumentCreator {

    private final ReportDocumentCreator reportDocumentCreator;

    public TransactionReceiptDocumentCreator (final ReportDocumentCreator reportDocumentCreator) {
        this.reportDocumentCreator = reportDocumentCreator;
    }

    public ReportDocument create (final TransactionReceiptData data, final ReportFormat format) {
        switch (format) {
        case json:
            return reportDocumentCreator.createJsonDocument (data.global.receiptName, data);
        case csv:
        case txt:
            throw new RuntimeException ("Report format \"" + format.name() + "\" not supported");
        case pdf:
        case docx:
        case xlsx:
        case xml:
            return reportDocumentCreator.createXmlBasedInvoiceDocument (data.global.receiptName, data, format, InvoiceTemplateCategory.TRANSACTION_RECEIPT);
        default:
            return null;
        }
    }

    public ReportDocument create (final CreditDebitNoteData data, final ReportFormat format) {
        switch (format) {
            case json:
                return reportDocumentCreator.createJsonDocument (data.global.transactionName, data);
            case csv:
            case txt:
                throw new RuntimeException ("Report format \"" + format.name() + "\" not supported");
            case pdf:
            case docx:
            case xlsx:
            case xml:
            {
                final InvoiceTemplateCategory template;
                if (data.global.isAviation) {
                    if (data.global.isDebit) {
                        template = InvoiceTemplateCategory.AVIATION_DEBIT_NOTE;
                    } else {
                        template = InvoiceTemplateCategory.AVIATION_CREDIT_NOTE;
                    }
                } else {
                    if (data.global.isDebit) {
                        template = InvoiceTemplateCategory.NON_AVIATION_DEBIT_NOTE;
                    } else {
                        template = InvoiceTemplateCategory.NON_AVIATION_CREDIT_NOTE;
                    }
                }
                return reportDocumentCreator.createXmlBasedInvoiceDocument (data.global.transactionName, data, format, template);
            }
            default:
                return null;
        }
    }

}
