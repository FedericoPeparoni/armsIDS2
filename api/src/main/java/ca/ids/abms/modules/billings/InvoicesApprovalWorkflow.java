package ca.ids.abms.modules.billings;

import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InvoicesApprovalWorkflow {

    private final SystemConfigurationService systemConfigurationService;

    public InvoicesApprovalWorkflow(final SystemConfigurationService systemConfigurationService) {
        this.systemConfigurationService = systemConfigurationService;
    }

    public InvoiceStateType getInitialLedgerState(boolean approvalWorkflowRequired) {
        if (approvalWorkflowRequired) {
            return getLedgerStatesList().get(0);
        } else {
            return InvoiceStateType.PUBLISHED;
        }
    }

    public List<InvoiceStateType> getLedgerStatesList() {
        final ArrayList<InvoiceStateType> states = new ArrayList<>(5);
        boolean manualApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.MANUAL_APPROVAL, false);
        boolean manualPublishing = systemConfigurationService.getBoolean(SystemConfigurationItemName.MANUAL_PUBLISHING, false);
        if (manualApproval) {
            states.add(InvoiceStateType.NEW);
        }
        if (manualPublishing) {
            states.add(InvoiceStateType.APPROVED);
        }
        states.add(InvoiceStateType.PUBLISHED);
        states.add(InvoiceStateType.PAID);
        if (manualApproval || manualPublishing) {
            states.add(InvoiceStateType.VOID);
        }
        return states;
    }

    public InvoiceStateType getNextStateType (final InvoiceStateType currentStateType) {
        InvoiceStateType next;
        if (currentStateType != null) {
            switch (currentStateType) {
                case NEW:
                    next = publishingState();
                    break;
                case APPROVED:
                    next = InvoiceStateType.PUBLISHED;
                    break;
                case PUBLISHED:
                    next = InvoiceStateType.PAID;
                    break;
                default:
                    next = null;
            }
        } else {
            next = null;
        }
        return next;
    }

    private InvoiceStateType publishingState () {
        if (systemConfigurationService.getBoolean(SystemConfigurationItemName.MANUAL_PUBLISHING, false)) {
            return InvoiceStateType.APPROVED;
        } else {
            return InvoiceStateType.PUBLISHED;
        }
    }

    boolean canBeVoid (final BillingLedger billingLedger, final boolean publishedInvoiceFromPointOfSale) {
        final InvoiceStateType state = InvoiceStateType.forValue(billingLedger.getInvoiceStateType());
        return publishedInvoiceFromPointOfSale ? InvoiceStateType.PUBLISHED.equals(state) : InvoiceStateType.NEW.equals(state) || InvoiceStateType.APPROVED.equals(state);
    }
}
