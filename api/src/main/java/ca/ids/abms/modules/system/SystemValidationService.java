package ca.ids.abms.modules.system;

import ca.ids.abms.modules.system.validation.ConnectionUrlValidator;
import ca.ids.abms.modules.system.validation.InvoiceByFlightMovementValidator;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
class SystemValidationService {

    private final ConnectionUrlValidator connectionUrlValidator;
    private final InvoiceByFlightMovementValidator invoiceByFlightMovementValidator;

    public SystemValidationService(final ConnectionUrlValidator connectionUrlValidator, final InvoiceByFlightMovementValidator invoiceByFlightMovementValidator) {
        this.connectionUrlValidator = connectionUrlValidator;
        this.invoiceByFlightMovementValidator = invoiceByFlightMovementValidator;
    }

    /**
     * Validate system configuration item with valid system validation type. If system validation
     * type is `null` OR not handled, returns true as nothing to validate.
     *
     * @param systemConfiguration item to validate
     * @return true if valid, exception if not (exception handled by ExceptionTranslator)
     */
    @SuppressWarnings("squid:S1301")
    Boolean validateSystemConfiguration(SystemConfiguration systemConfiguration) {

        // return true as nothing to validate
        if (systemConfiguration == null || systemConfiguration.getSystemValidationType() == null)
            return true;

        // run appropriate validation for system configuration type
        switch (systemConfiguration.getSystemValidationType()) {
            case CONNECTION_URL:
                return connectionUrlValidator.isConnectionUrlValid(systemConfiguration.getCurrentValue());
            default:
                return true;
        }
    }

    /**
     * Validate system configuration item with valid system validation type. If system validation
     * type is `null` OR not handled, returns true as nothing to validate.
     *
     * @param systemConfiguration item to validate
     * @return true if valid, exception if not (exception handled by ExceptionTranslator)
     */
    @SuppressWarnings("squid:S1301")
    Boolean validateSystemConfiguration(List<SystemConfigurationViewModel> viewModelList) {
        String validationType = viewModelList.get(1).getSystemValidationType();

        // return true as nothing to validate
        if (validationType == null)
            return true;

        // run appropriate validation for system configuration type
        switch (validationType) {
            case "INVOICE_SPEC":
                return invoiceByFlightMovementValidator.isInvoiceSpecificationSet(viewModelList);
            default:
                return true;
        }
    }

}
