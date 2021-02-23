package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.invoices.ChargeSelection;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.springframework.stereotype.Component;

import static ca.ids.abms.modules.reports2.invoices.ChargeSelection.ONLY_ENROUTE;
import static ca.ids.abms.modules.reports2.invoices.ChargeSelection.ONLY_PAX;

/**
 * Format an aviation invoices as PDF, etc given raw data
 */
@Component
public class AviationInvoiceDocumentCreator {

    private final ReportDocumentCreator reportDocumentCreator;
    private final SystemConfigurationService systemConfigurationService;

    public AviationInvoiceDocumentCreator (final ReportDocumentCreator reportDocumentCreator,
                                           final SystemConfigurationService systemConfigurationService) {
        this.reportDocumentCreator = reportDocumentCreator;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Format an aviation invoices given raw data
     */
    @SuppressWarnings("squid:S3776")
    public ReportDocument create (final AviationInvoiceData data, final ReportFormat reportFormat,
                                  final ChargeSelection chargeSelection, final Boolean cashAccount, final Boolean pointOfSale) {
        switch (reportFormat) {
        case json:
            return reportDocumentCreator.createJsonDocument (data.global.invoiceName, data);
        case csv:
        case txt:
            return reportDocumentCreator.createCsvDocument (data.global.invoiceName, data.flightInfoList, AviationInvoiceData.FlightInfo.class, false);
        case pdf:
        case docx:
        case xlsx:
        case xml:
            BillingOrgCode billingOrgCode = systemConfigurationService.getBillingOrgCode();
            boolean isKCAA = billingOrgCode == BillingOrgCode.KCAA;
            boolean isEANA = billingOrgCode == BillingOrgCode.EANA;
            boolean isTTCAA = billingOrgCode == BillingOrgCode.TTCAA;
            boolean isCADSUR = billingOrgCode == BillingOrgCode.CADSUR;
            if (isKCAA || isEANA) {
                if (cashAccount || (isKCAA && pointOfSale)) {
                    return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_INVOICE_CASH);
                } else {
                    return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_INVOICE_CREDIT);
                }
            }
            if (isTTCAA) {
                return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_INVOICE);
            }
            if (chargeSelection == ONLY_PAX) {
                return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_PAX_INVOICE);
            } else if (chargeSelection == ONLY_ENROUTE && !isCADSUR) {
                return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_ENR_INVOICE);
            } else {
                return reportDocumentCreator.createXmlBasedInvoiceDocument(data.global.invoiceName, data, reportFormat, InvoiceTemplateCategory.AVIATION_INVOICE);
            }
        default:
            return null;
        }
    }
}
