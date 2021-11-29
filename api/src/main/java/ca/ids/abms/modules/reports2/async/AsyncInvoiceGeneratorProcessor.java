package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.jobs.ItemProcessor;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoice;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceService;
import ca.ids.abms.modules.reports2.invoices.aviation.BillingInterval;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxInvoiceError;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxValidity;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxValidityService;
import ca.ids.abms.modules.users.User;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ca.ids.abms.util.MiscUtils.nvl;

@Component
public class AsyncInvoiceGeneratorProcessor implements ItemProcessor<AsyncInvoiceGeneratorScope> {

    private final AviationInvoiceService aviationInvoiceService;
    private final UnifiedTaxValidityService unifiedTaxValidityService;

    public AsyncInvoiceGeneratorProcessor(final AviationInvoiceService aviationInvoiceService,
    									  final UnifiedTaxValidityService unifiedTaxValidityService) {
        this.aviationInvoiceService = aviationInvoiceService;
		this.unifiedTaxValidityService = unifiedTaxValidityService;
    }
    
    public AsyncInvoiceGeneratorScope processItem (final AsyncInvoiceGeneratorScope scope) {
        final User currentUser = scope.getCurrentUser();
        if (currentUser.getBillingCenter() == null) {
            throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class, currentUser.getId().toString(), currentUser.getLogin());
        }

        
    	final boolean unifiedTaxInvoice = scope.getBillingInterval() == BillingInterval.UNIFIED_TAX_ANNUALLY || 
				  scope.getBillingInterval() == BillingInterval.UNIFIED_TAX_PARTIALLY;

    	if (unifiedTaxInvoice) {
    		UnifiedTaxValidity validityDate = unifiedTaxValidityService.findUnifiedTaxValidityByYear(scope.getStartDate());
    		if (validityDate == null) {
    			throw new RuntimeException("The Specified validity year is not available in the database");
    		}
    	}
        
        final FlightmovementCategory flightmovementCategory;
        if (scope.getFlightCategory() != null) {
            flightmovementCategory = aviationInvoiceService.findFlightMovementCategory(scope.getFlightCategory());
        } else {
            flightmovementCategory = null;
        }

        scope.getInvoiceProgressCounter().update();

        // Process an account per time and notify if some error has been found
        final List <AviationInvoice> invoiceList = new ArrayList<>();
        final List <UnifiedTaxInvoiceError> unifiedTaxInvoiceErrorList = new ArrayList<>();
        
        scope.getInvoiceProgressCounter().setMessage("Generating the invoices");
        if(scope.getAccountIdList() != null) {
            scope.getInvoiceProgressCounter().resetAccountsTotal(scope.getAccountIdList().size());
        
            for (final Integer accountId: scope.getAccountIdList()) {
                Boolean result;
                scope.getInvoiceProgressCounter().increaseAccountNumber();
                if(scope.getPreview()) {
                    result  = aviationInvoiceService.previewAccount(scope, accountId, invoiceList, unifiedTaxInvoiceErrorList, flightmovementCategory, currentUser, scope.getDueDateOverrideUnifiedTax());
                } else {
                    result  = aviationInvoiceService.processAccount(scope, accountId, invoiceList, unifiedTaxInvoiceErrorList, flightmovementCategory, currentUser, scope.getDueDateOverrideUnifiedTax());
                }
                if(result) {
                    scope.getInvoiceProgressCounter().increaseProcessed();
                } else {
                    scope.getInvoiceProgressCounter().increaseDiscarded();
                }
            }
        }
        scope.getInvoiceProgressCounter().resetAccountsTotal(0);
        scope.setUnifiedTaxInvoiceErrorList(unifiedTaxInvoiceErrorList);
        
        if (scope.getPreview() || InvoiceStateType.PUBLISHED.equals(aviationInvoiceService.getInitialLedgerState(true))) {
            scope.getInvoiceProgressCounter().setMessage("Joining the invoices in a unique document");
            scope.getInvoiceProgressCounter().update();
            
            final ZonedDateTime zdtStart = ZonedDateTime.of(scope.getStartDate(), ReportHelper.UTC_ZONE_ID);
            final String invoiceNameSuffix = String.format (" - %s", zdtStart.format (DateTimeFormatter.ofPattern("MMM YYYY")));
            // Set the result as a unique PDF in case the user has selected a preview
            scope.setResult(aviationInvoiceService.mergeDocumentsForPreview(nvl (scope.getFormat(), AviationInvoiceService.DFLT_FORMAT),invoiceNameSuffix,currentUser, invoiceList, scope.getPreview()));
        }

        return scope;
    }
}
