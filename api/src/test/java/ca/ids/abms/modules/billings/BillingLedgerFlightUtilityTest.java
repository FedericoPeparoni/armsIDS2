package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementChargesStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.translation.TranslationRequired;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class BillingLedgerFlightUtilityTest extends TranslationRequired {

    private BillingLedgerFlightUtility billingLedgerFlightUtility;
    private SystemConfigurationService systemConfigurationService;

    @Before
    public void setup() {

        systemConfigurationService = mock(SystemConfigurationService.class);

        billingLedgerFlightUtility = new BillingLedgerFlightUtility(mock(FlightMovementRepository.class),
            systemConfigurationService);

        when(systemConfigurationService.shouldIncludePAXinInvoiceTotal())
            .thenReturn(false);
    }

    @Test
    public void updateStatus() {
        final FlightMovement flm = new FlightMovement();
        flm.setMovementType(FlightMovementType.INT_DEPARTURE);
        flm.setStatus(FlightMovementStatus.INCOMPLETE);

        final BillingLedger invoice = new BillingLedger();
        invoice.setId(1234);
        invoice.setInvoiceStateType(InvoiceStateType.NEW.toValue());

        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);

        assertThat(flm.getEnrouteChargesStatus()).isNull();
        assertThat(flm.getOtherChargesStatus()).isNull();
        assertThat(flm.getPassengerChargesStatus()).isNull();
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.INCOMPLETE);

        flm.setStatus(FlightMovementStatus.PENDING);
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.PENDING);

        flm.setStatus(FlightMovementStatus.INVOICED);
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.PENDING);

        when(systemConfigurationService.shouldIncludePAXinInvoiceTotal())
            .thenReturn(true);

        flm.setEnrouteInvoiceId(7777);
        flm.setEnrouteChargesStatus(FlightMovementChargesStatus.INVOICED);
        flm.setOtherInvoiceId(1234);
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getOtherChargesStatus()).isEqualTo(FlightMovementChargesStatus.INVOICED);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.PENDING);

        flm.setStatus(FlightMovementStatus.PENDING);
        flm.setPassengerChargesStatus(null);
        flm.setPassengerInvoiceId(null);
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, false);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.INVOICED);

        flm.setPassengerInvoiceId(8888);
        flm.setPassengerChargesStatus(FlightMovementChargesStatus.PAID);
        invoice.setInvoiceStateType(InvoiceStateType.PUBLISHED.toValue());
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getOtherChargesStatus()).isEqualTo(FlightMovementChargesStatus.INVOICED);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.INVOICED);

        invoice.setInvoiceStateType(InvoiceStateType.PAID.toValue());
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getOtherChargesStatus()).isEqualTo(FlightMovementChargesStatus.PAID);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.INVOICED);

        flm.setEnrouteChargesStatus(FlightMovementChargesStatus.PAID);
        billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(invoice, flm, true);
        assertThat(flm.getStatus()).isEqualTo(FlightMovementStatus.PAID);
    }
}
