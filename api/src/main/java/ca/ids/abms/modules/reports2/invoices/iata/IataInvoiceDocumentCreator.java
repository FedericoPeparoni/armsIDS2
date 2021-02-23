package ca.ids.abms.modules.reports2.invoices.iata;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;

/**
 * Format an IATA invoice to PDF etc, given raw data
 */
@Component
public class IataInvoiceDocumentCreator {

    private static final Logger LOG = LoggerFactory.getLogger(IataInvoiceDocumentCreator.class);

    private final ReportDocumentCreator reportDocumentCreator;

    public IataInvoiceDocumentCreator (final ReportDocumentCreator reportDocumentCreator) {
        this.reportDocumentCreator = reportDocumentCreator;
    }

    /**
     * Format an IATA invoice to PDF etc, given raw data
     */
    public ReportDocument create (final IataInvoiceData data, final ReportFormat format) {
        LOG.debug("Creating iata invoice report {}; there are {} flights", data.props.name, data.flightMovements.size());
        switch (format) {
        case json:
            return reportDocumentCreator.createJsonDocument (data.props.name, data);
        case csv:
        case txt:
            return reportDocumentCreator.createCsvDocument (data.props.name, data.flightMovements, IataInvoiceData.FlightMovementInfo.class, false);
        case pdf:
        case docx:
        case xlsx:
        case xml:
            return reportDocumentCreator.createXmlBasedInvoiceDocument (data.props.name, data, format, InvoiceTemplateCategory.IATA_INVOICE);
        default:
            return null;
        }
    }

}
