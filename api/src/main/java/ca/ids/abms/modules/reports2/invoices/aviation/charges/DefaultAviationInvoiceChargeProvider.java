package ca.ids.abms.modules.reports2.invoices.aviation.charges;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class DefaultAviationInvoiceChargeProvider implements AviationInvoiceChargeProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAviationInvoiceChargeProvider.class);
    @Override
    public List<AdditionalCharge> processAdditionalCharges(
        final List<FlightMovement> flightMovements, final AviationInvoiceData invoiceData,
        final AviationInvoiceChargeHelper aviationInvoiceChargeHelper
    ) {
        LOG.trace("Using default implementation of processAdditionalCharges(); nothing will happen");
        return Collections.emptyList();
    }
}
