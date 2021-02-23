package ca.ids.abms.plugins.common.utilities;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Component
public class PluginMapperHelper {

    @Autowired
    protected ReportHelper reportHelper;

    /**
     * Generate billing ledger description by type.
     */
    public String billingLedgerDescriptionByInvoiceType(final BillingLedger billingLedger) {

        StringJoiner description = new StringJoiner(" ");
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());

        if (InvoiceType.NON_AVIATION.equals(invoiceType) &&
            billingLedger.getInvoiceLineItems() != null && billingLedger.getInvoiceLineItems().size() == 1 &&
            billingLedger.getInvoiceLineItems().get(0).getServiceChargeCatalogue() != null &&
            billingLedger.getInvoiceLineItems().get(0).getServiceChargeCatalogue().getDescription() != null &&
            !billingLedger.getInvoiceLineItems().get(0).getServiceChargeCatalogue().getDescription().isEmpty())
            description.add(billingLedger.getInvoiceLineItems().get(0).getServiceChargeCatalogue().getDescription());
        else if (InvoiceType.DEBIT_NOTE.equals(invoiceType))
            description.add("Debit Memo");
        else if (InvoiceType.OVERDUE.equals(invoiceType))
            description.add("Overdue Invoice Penalties");
        else
            description.add("Flight Operations");

        // append description with date of invoice
        if (billingLedger.getInvoicePeriodOrDate() != null && reportHelper != null)
            description.add("(" + dateFormat(billingLedger.getInvoicePeriodOrDate()) + ")");

        return description.toString();
    }

    /**
     * Format date by system configuration settings.
     */
    public String dateFormat(final LocalDateTime dateTime) {
        return reportHelper.formatDateUtc(dateTime, reportHelper.getDateFormat());
    }
}
