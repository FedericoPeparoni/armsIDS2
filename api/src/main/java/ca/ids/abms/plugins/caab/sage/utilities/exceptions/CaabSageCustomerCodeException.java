package ca.ids.abms.plugins.caab.sage.utilities.exceptions;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.charges.ExternalChargeCategory;
import org.apache.commons.lang.StringUtils;

/**
 * Extends `IllegalStateException` as notFound CustomerCode exceptions
 * should be cached and thus NOT inheritance of `CustomParametrizedException`.
 */
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class CaabSageCustomerCodeException extends IllegalStateException {

    /**
     * Undefined value for error message handling.
     */
    private static final String UNDEFINED = CaabSageException.UNDEFINED;

    private CaabSageCustomerCodeException(final String message) {
        super(message);
    }

    /**
     * Log customer code error and throw exception with appropriate message to prevent document
     * generation and exporting.
     */
    public static CaabSageCustomerCodeException notFound(final Account account, final ExternalChargeCategory externalChargeCategory) {
        return new CaabSageCustomerCodeException(messageNotFound(account, externalChargeCategory));
    }

    private static String messageNotFound(final Account account, final ExternalChargeCategory externalChargeCategory) {

        // if no account or external charge category names, set to UNDEFINED and return formatted message
        String categoryName = externalChargeCategory == null || StringUtils.isBlank(externalChargeCategory.getName())
            ? UNDEFINED : externalChargeCategory.getName();
        String customerName = account == null || org.apache.commons.lang.StringUtils.isBlank(account.getName())
            ? UNDEFINED : account.getName();

        return String.format("Could not find appropriate '%s' external charge category identifier for the account '%s'.",
            categoryName, customerName);
    }
}
