package ca.ids.abms.modules.reports2.invoices.interest;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class InterestInvoiceDocumentCreator {

    private static final Logger LOG = LoggerFactory.getLogger (InterestInvoiceDocumentCreator.class);
    private final ReportDocumentCreator reportDocumentCreator;

    public InterestInvoiceDocumentCreator (final ReportDocumentCreator reportDocumentCreator) {
        this.reportDocumentCreator = reportDocumentCreator;
    }

    public ReportDocument create (final InterestInvoiceData data, final ReportFormat reportFormat) {
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
                return reportDocumentCreator.createXmlBasedInvoiceDocument (data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.INTEREST_INVOICE);
            default:
                return null;
        }
    }

    /**
     * Format multiple invoices (per account) to PDF or some such
     */
    public Map<Account, ReportDocument> create (final Map <Account, InterestInvoiceData> accountInvoiceDataMap, final ReportFormat reportFormat) {
        final Map <Account, ReportDocument> reportDocumentMap = new HashMap<>();
        accountInvoiceDataMap.forEach((account, invoiceData)->{
            final ReportDocument doc = create(invoiceData, reportFormat);
            reportDocumentMap.put (account, doc);
            LOG.debug ("Created interest invoice document \"{}\" for account {}/{}",
                doc.fileName(), account.getId(), account.getName());
        });
        return reportDocumentMap;
    }
}
