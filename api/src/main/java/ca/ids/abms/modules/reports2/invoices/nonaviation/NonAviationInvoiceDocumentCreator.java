package ca.ids.abms.modules.reports2.invoices.nonaviation;

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

/**
 * Format an aviation invoices as PDF, etc given raw data
 */
@Component
class NonAviationInvoiceDocumentCreator {

    private static final Logger LOGGER = LoggerFactory.getLogger (NonAviationInvoiceDocumentCreator.class);
    
    private final ReportDocumentCreator reportDocumentCreator;
    
    public NonAviationInvoiceDocumentCreator (final ReportDocumentCreator reportDocumentCreator) {
        this.reportDocumentCreator = reportDocumentCreator;
    }
    
    /**
     * Format an aviation invoices given raw data
     */
    public ReportDocument create (final NonAviationInvoiceData data, final ReportFormat reportFormat) {
        switch (reportFormat) {
        case json:
            return reportDocumentCreator.createJsonDocument (data.global.invoiceName, data);
        case csv:
        case txt:
            return reportDocumentCreator.createCsvDocument (data.global.invoiceName, data.lineItemList, NonAviationInvoiceData.LineItem.class, false);
        case pdf:
        case docx:
        case xlsx:
        case xml:
            return reportDocumentCreator.createXmlBasedInvoiceDocument (data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.NON_AVIATION_INVOICE);
        default:
            return null;
        }
    }
    
    /**
     * Format multiple invoices (per account) to PDF or some such
     */
    public Map <Account, ReportDocument> create (final Map <Account, NonAviationInvoiceData> accountInvoiceDataMap, final ReportFormat reportFormat) {
        final Map <Account, ReportDocument> reportDocumentMap = new HashMap<>();
        accountInvoiceDataMap.forEach((account, invoiceData)->{
            final ReportDocument doc = create (invoiceData, reportFormat);
            reportDocumentMap.put (account, doc);
            LOGGER.debug ("Created non-aviation invoice document \"{}\" for account {}/{}",
                    doc.fileName(), account.getId(), account.getName());
        });
        return reportDocumentMap;
    }
}
