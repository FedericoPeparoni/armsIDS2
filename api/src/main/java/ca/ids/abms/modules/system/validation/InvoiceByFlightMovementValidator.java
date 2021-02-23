package ca.ids.abms.modules.system.validation;

import java.util.List;

import org.springframework.stereotype.Component;

import ca.ids.abms.modules.system.SystemConfigurationViewModel;

@Component
public class InvoiceByFlightMovementValidator {

    /**
     * enroute invoice currency selection (either account or flight movement category).
     * This can only be set to flight movement category if invoices are flight movement category specific.
     *
     * @param List<SystemConfigurationViewModel> sys configs to parse
     * @return true if successfully set sysconfig values
     */
    public Boolean isInvoiceSpecificationSet(List<SystemConfigurationViewModel> viewModelList) {
        final String invoiceByFMCatValue = viewModelList.get(0).getCurrentValue(); // boolean: t/f
        final String invoiceCurrencyValue = viewModelList.get(1).getCurrentValue(); // string: account, flight movemement category
        final Boolean set;

        if (invoiceByFMCatValue.equals("f") && invoiceCurrencyValue.equals("flight movement category")) {
            set = false;
        } else {
            set = true;
        }

        return set;
    }

}
