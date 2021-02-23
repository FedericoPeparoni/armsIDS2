package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementChargesStatus;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.system.SystemConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

import static ca.ids.abms.util.MiscUtils.integerEquals;

@Service
@Transactional
public class BillingLedgerFlightUtility {

    private static final Logger LOG = LoggerFactory.getLogger (BillingLedgerFlightUtility.class);

    private final FlightMovementRepository flightMovementRepository;
    private final SystemConfigurationService systemConfigurationService;

    public BillingLedgerFlightUtility(
        final FlightMovementRepository flightMovementRepository,
        final SystemConfigurationService systemConfigurationService
    ) {
        this.flightMovementRepository = flightMovementRepository;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Update the status of each flight connected with an invoice, aligning the related charge states
     *
     * @param billingLedger -- the invoice marked as NEW, APPROVED, PUBLISHED, PAID, VOID
     * @param flightMovement -- the related flight movement record connected to the invoice via either of
     *                          the {enroute,other,passenger}_charges_status fields.
     */
    public void updateFlightStatusToMatchInvoice (final BillingLedger billingLedger, final FlightMovement flightMovement, final boolean passengerChargesSupport) {
        final String billingLedgerState = billingLedger.getInvoiceStateType();
        assert (billingLedgerState != null);

        FlightMovementChargesStatus flightMovementChargesStatus;

        if (billingLedgerState.equals (InvoiceStateType.NEW.toValue())
            || billingLedgerState.equals(InvoiceStateType.APPROVED.toValue())
            || billingLedgerState.equals(InvoiceStateType.PUBLISHED.toValue())) {

            /* In case the invoice has been created, approved or published, the related charges will be marked as INVOICED */
            flightMovementChargesStatus = FlightMovementChargesStatus.INVOICED;
        } else if (billingLedgerState.equals(InvoiceStateType.PAID.toValue())) {

            /* In case the invoice has been paid, the related charges will be marked as PAID */
            flightMovementChargesStatus = FlightMovementChargesStatus.PAID;
        } else {

            /* In case the invoice has been canceled, the charge statuses will come back in PENDING */
            flightMovementChargesStatus = FlightMovementChargesStatus.PENDING;
        }

        /* If needed, update the status of each charges type (en route, pax, others) for the current invoice */

        switch (flightMovement.getMovementType()) {
            case OTHER: {
                break;
            }
            case DOMESTIC:
            case REG_DEPARTURE:
            case REG_ARRIVAL:
            case INT_DEPARTURE:
            case INT_ARRIVAL: 
            case INT_OVERFLIGHT:
            case REG_OVERFLIGHT:{
                if (integerEquals(flightMovement.getPassengerInvoiceId(), billingLedger.getId()) &&
                    !(FlightMovementChargesStatus.PAID == flightMovement.getPassengerChargesStatus())) {
                    if (flightMovementChargesStatus == FlightMovementChargesStatus.PENDING) {
                        flightMovement.setPassengerInvoiceId(null);
                    }
                    flightMovement.setPassengerChargesStatus(flightMovementChargesStatus);
                }
                if (integerEquals(flightMovement.getOtherInvoiceId(), billingLedger.getId()) &&
                    !(FlightMovementChargesStatus.PAID == flightMovement.getOtherChargesStatus())) {
                    if (flightMovementChargesStatus == FlightMovementChargesStatus.PENDING) {
                        flightMovement.setOtherInvoiceId(null);
                    }
                    flightMovement.setOtherChargesStatus(flightMovementChargesStatus);
                }
                // don't break here pls
            }
            default: {
                if (integerEquals(flightMovement.getEnrouteInvoiceId(), billingLedger.getId()) &&
                    !(FlightMovementChargesStatus.PAID == flightMovement.getEnrouteChargesStatus())) {
                    if (flightMovementChargesStatus == FlightMovementChargesStatus.PENDING) {
                        flightMovement.setEnrouteInvoiceId(null);
                    }
                    flightMovement.setEnrouteChargesStatus(flightMovementChargesStatus);
                }
            }
        }
        updateTheFlightStatus(flightMovement, passengerChargesSupport);
    }

    @Transactional(propagation=Propagation.REQUIRED)
    public void updateFlightStatusToMatchInvoice (final BillingLedger billingLedger, final boolean passengerChargesSupport) {
        // Find flights connected to this billing ledger
        final List<FlightMovement> flights = flightMovementRepository.findByBillingLedger (billingLedger.getId());

        // Update status of each flight
        if (flights != null && !flights.isEmpty()) {
            flights.forEach(fm->updateFlightStatusToMatchInvoice (billingLedger, fm, passengerChargesSupport));
            flightMovementRepository.save(flights);
            flightMovementRepository.flush();
        }
    }


    private static boolean invoicedOrPaid (final FlightMovementChargesStatus x) {
        return x == FlightMovementChargesStatus.INVOICED || x == FlightMovementChargesStatus.PAID;
    }

    public void updateTheFlightStatus (final FlightMovement flightMovement, final boolean passengerChargesSupport) {
        final FlightMovementStatus flightMovementStatus = flightMovement.getStatus();
        assert (flightMovementStatus != null);

        /* Align the overall status of the current flight movement, if its status is not PAID, CANCELED or INCOMPLETE */

        // overall status is PENDING or INVOICED
        if (!(FlightMovementStatus.PAID == flightMovementStatus ||
            FlightMovementStatus.CANCELED == flightMovementStatus ||
            FlightMovementStatus.DECLINED == flightMovementStatus ||
            FlightMovementStatus.DELETED == flightMovementStatus ||
            FlightMovementStatus.INCOMPLETE == flightMovementStatus)) {

            switch (flightMovement.getMovementType()) {
                case OTHER: {
                    break;
                }
                case DOMESTIC:
                case REG_DEPARTURE:
                case REG_ARRIVAL:
                case INT_DEPARTURE:
                case INT_ARRIVAL: {

                    boolean shouldInvoicePaxCharges = systemConfigurationService.shouldIncludePAXinInvoiceTotal();

                    // all individual invoice states are PAID, KCAA has special condition to support passenger charges
                    // but ignore when setting flight movement status
                    if (FlightMovementChargesStatus.PAID == flightMovement.getEnrouteChargesStatus() &&
                        FlightMovementChargesStatus.PAID == flightMovement.getOtherChargesStatus() &&
                        (FlightMovementChargesStatus.PAID == flightMovement.getPassengerChargesStatus()
                            || !passengerChargesSupport || !shouldInvoicePaxCharges)
                    ) {

                        /* If all charges are paid, the FLM is PAID */
                        flightMovement.setStatus(FlightMovementStatus.PAID);

                    // all individual invoice states are INVOICED or PAID
                    } else if (
                            invoicedOrPaid (flightMovement.getEnrouteChargesStatus()) &&
                            invoicedOrPaid (flightMovement.getOtherChargesStatus()) &&
                            (invoicedOrPaid (flightMovement.getPassengerChargesStatus())
                                || !passengerChargesSupport || !shouldInvoicePaxCharges)
                    ) {
                        // TODO: We're waiting for clarifications about the passenger invoices:
                        // if (fm.getPassengersChargeableDomestic() + fm.getPassengersChargeableIntern() = 0) then
                        // we can consider to ignore the passenger charge status
                        // Please see User Story 79258:BUG - KCAA - Separate Passenger Invoice not generated

                        /* Here not all charges are paid but no one is pending OR null, so the FLM is INVOICED */
                        flightMovement.setStatus(FlightMovementStatus.INVOICED);

                    } else {

                        /* Otherwise the FLM is PENDING */
                        flightMovement.setStatus((FlightMovementStatus.PENDING));
                    }
                    break;
                }
                default: {
                    /* As the previous cases, but simply looking at en route charges only for the over flights */

                    if (FlightMovementChargesStatus.PAID == flightMovement.getEnrouteChargesStatus()) {
                        flightMovement.setStatus(FlightMovementStatus.PAID);
                    } else if (FlightMovementChargesStatus.INVOICED == flightMovement.getEnrouteChargesStatus()) {
                        flightMovement.setStatus(FlightMovementStatus.INVOICED);
                    } else {
                        flightMovement.setStatus(FlightMovementStatus.PENDING);
                    }
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(DEBUG_MSG, flightMovement.getId(), flightMovement.getDateOfFlight(),
                flightMovement.getDepTime(), flightMovement.getDepAd(), flightMovement.getMovementType(),
                flightMovement.getStatus(), flightMovement.getEnrouteChargesStatus(),
                flightMovement.getOtherChargesStatus(), flightMovement.getPassengerChargesStatus());
        }
    }

    private static final String DEBUG_MSG = "The FLM with flight Id {}, date {}, time {}, departure {}, type {}, "
                    + "has been updated with overall state {}, enroute state {}, other state {}, pax state {}";
}
