package ca.ids.abms.plugins.caab.sage.utilities.exceptions;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.currencies.Currency;
import org.apache.commons.lang.StringUtils;

@SuppressWarnings("squid:MaximumInheritanceDepth")
public class CaabSageBankCodeException extends CaabSageException {

    private CaabSageBankCodeException(final String message) {
        super(message);
    }

    public static CaabSageBankCodeException notFound(final CaabSageEntityType entityType) {
        return new CaabSageBankCodeException(messageNotFound(entityType.toValue()));
    }

    public static CaabSageBankCodeException notFound(final BillingCenter billingCenter, final Currency currency) {
        return new CaabSageBankCodeException(messageNotFound(billingCenter, currency));
    }

    public static CaabSageBankCodeException notValid(final CaabSageEntityType invalidType,
                                                     final CaabSageEntityType documentType, final String documentNumber) {
        return new CaabSageBankCodeException(messageNotValid(invalidType.toValue(), documentType.toValue(), documentNumber));
    }

    private static String messageNotFound(final String entityType) {
        return String.format("Could not determine bank code as %s could not be found.", entityType);
    }

    private static String messageNotFound(final BillingCenter billingCenter, final Currency currency) {

        // if no billing center name or currency code, set to UNDEFINED and return formatted message
        String billingCenterName = billingCenter == null || StringUtils.isBlank(billingCenter.getName())
            ? UNDEFINED : billingCenter.getName();
        String currencyCode = currency == null || StringUtils.isBlank(currency.getCurrencyCode())
            ? UNDEFINED : currency.getCurrencyCode();
        return String.format("Could not determine bank code for billing center '%s' and " +
            "currency code '%s'.", billingCenterName, currencyCode);
    }

    private static String messageNotValid(final String invalidType, final String documentType, final String documentNumber) {

        // if no document number, set to UNDEFINED and return formatted message
        String number = StringUtils.isNotBlank(documentNumber) ? documentNumber : UNDEFINED;
        return String.format("Could not determine bank code as %s '%s' could not be " +
            "found with a valid %s.", documentType, number, invalidType);
    }
}
