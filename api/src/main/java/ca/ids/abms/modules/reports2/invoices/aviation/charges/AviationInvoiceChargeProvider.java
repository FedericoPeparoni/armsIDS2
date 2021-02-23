package ca.ids.abms.modules.reports2.invoices.aviation.charges;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceData;

import java.util.Collections;
import java.util.List;

public interface AviationInvoiceChargeProvider {

    /**
     * Apply charge provider specific value(s) to aviation invoice additional charge data.
     *
     * @param flightMovements flight movements to process and resolve values
     * @param invoiceData aviation invoice data to apply
     * @param aviationInvoiceChargeHelper aviation invoice charge utility
     * @return additional charges for charge provider specific value(s)
     */
    default List<AdditionalCharge> processAdditionalCharges(
        final List<FlightMovement> flightMovements, final AviationInvoiceData invoiceData,
        final AviationInvoiceChargeHelper aviationInvoiceChargeHelper
    ) {
        // default implementation ignored
        return Collections.emptyList();
    }
}
