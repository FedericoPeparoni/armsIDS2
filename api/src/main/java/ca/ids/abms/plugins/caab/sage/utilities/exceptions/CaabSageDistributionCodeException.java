package ca.ids.abms.plugins.caab.sage.utilities.exceptions;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class CaabSageDistributionCodeException extends CaabSageException {

    private CaabSageDistributionCodeException(final String message) {
        super(message);
    }

    /**
     * Log distribution code error and throw exception with appropriate message.
     */
    public static CaabSageDistributionCodeException notValid(final ChargesAdjustment chargesAdjustment,
                                                             final BillingCenter billingCenter) {
        return new CaabSageDistributionCodeException(messageNotValid(CaabSageEntityType.CHARGE_ADJUSTMENT.toValue(),
            chargesAdjustment.getChargeDescription(), chargesAdjustment.getExternalAccountingSystemIdentifier(),
            billingCenter));
    }

    /**
     * Log distribution code error and throw exception with appropriate message.
     */
    public static CaabSageDistributionCodeException notValid(final InvoiceLineItem invoiceLineItem,
                                                             final BillingCenter billingCenter, final String chargeCode) {
        String description = invoiceLineItem.getServiceChargeCatalogue() == null
            ? null : invoiceLineItem.getServiceChargeCatalogue().getDescription();
        return new CaabSageDistributionCodeException(messageNotValid(CaabSageEntityType.INVOICE_LINE_ITEM.toValue(),
            description, chargeCode, billingCenter));
    }

    /**
     * Log distribution code error and throw exception with appropriate message.
     */
    public static CaabSageDistributionCodeException notValid(final InvoiceOverduePenalty invoiceOverduePenalty,
                                                             final BillingCenter billingCenter, final String chargeCode) {
        String description = invoiceOverduePenalty.getPenalizedInvoice() == null
            ? null : invoiceOverduePenalty.getPenalizedInvoice().getInvoiceNumber();
        return new CaabSageDistributionCodeException(messageNotValid(CaabSageEntityType.INVOICE_OVERDUE_PENALTY.toValue(),
            description, chargeCode, billingCenter));
    }

    /**
     * Log distribution code error and throw exception with appropriate message.
     */
    public static CaabSageDistributionCodeException notValid(final CaabSageEntityType itemType,
                                                             final BillingCenter billingCenter,
                                                             final String chargeCode, final String itemDescription) {
        return new CaabSageDistributionCodeException(messageNotValid(itemType.toValue(), itemDescription, chargeCode,
            billingCenter));
    }

    private static String messageNotValid(final String itemType, final String itemDescription, final String chargeCode,
                                          final BillingCenter billingCenter) {

        // find appropriate message values, set to UNDEFINED if not found
        String code = chargeCode == null
            ? UNDEFINED : chargeCode;
        String description = itemDescription == null
            ? UNDEFINED : itemDescription;
        String billingCenterName = billingCenter == null || StringUtils.isBlank(billingCenter.getName())
            ? UNDEFINED : billingCenter.getName();
        String operationsCode = billingCenter == null || StringUtils.isBlank(billingCenter.getExternalAccountingSystemIdentifier())
            ? UNDEFINED : billingCenter.getExternalAccountingSystemIdentifier();

        // format message and return
        return String.format("Could not find appropriate distribution code for %s '%s' " +
                "with charge code '%s' and billing center '%s' with operations code '%s'.", itemType, description,
            code, billingCenterName, operationsCode);
    }
}
