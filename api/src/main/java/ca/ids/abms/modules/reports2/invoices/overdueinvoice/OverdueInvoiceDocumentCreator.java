package ca.ids.abms.modules.reports2.invoices.overdueinvoice;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;

@Component
public class OverdueInvoiceDocumentCreator {
    private final Logger logger = LoggerFactory.getLogger (OverdueInvoiceDocumentCreator.class);

    private final ReportDocumentCreator reportDocumentCreator;

    public OverdueInvoiceDocumentCreator (final ReportDocumentCreator reportDocumentCreator) {
        this.reportDocumentCreator = reportDocumentCreator;
    }

    /**
     * Format an aviation invoices given raw data
     */
    public ReportDocument create (final OverdueInvoiceData data, final ReportFormat reportFormat) {
        switch (reportFormat) {
            case json:
                return reportDocumentCreator.createJsonDocument (data.global.invoiceName, data);
            case csv:
            case txt:
                return reportDocumentCreator.createCsvDocument (data.global.invoiceName, null, null, false);
            case pdf:
            case docx:
            case xlsx:
            case xml:
                return reportDocumentCreator.createXmlBasedInvoiceDocument (data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.OVERDUE_INVOICE);
            default:
                return null;
        }
    }

    /**
     * Format multiple invoices (per account) to PDF or some such
     */
    public Map <Account, ReportDocument> create (final Map <Account, OverdueInvoiceData> accountInvoiceDataMap, final ReportFormat reportFormat) {
        final Map <Account, ReportDocument> reportDocumentMap = new HashMap<>();
        accountInvoiceDataMap.forEach((account, invoiceData)->{
            final ReportDocument doc = create (invoiceData, reportFormat);
            reportDocumentMap.put (account, doc);
            logger.debug ("Created non-aviation invoice document \"{}\" for account {}/{}",
                doc.fileName(), account.getId(), account.getName());
        });
        return reportDocumentMap;
    }
}

