package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationRepository;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.jobs.ItemProcessor;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoice;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceService;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxRepository;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxService;
import ca.ids.abms.modules.users.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static ca.ids.abms.util.MiscUtils.nvl;

@Component
public class AsyncInvoiceGeneratorProcessor implements ItemProcessor<AsyncInvoiceGeneratorScope> {

    private static final Logger LOG = LoggerFactory.getLogger(AviationInvoiceService.class);

    private final AviationInvoiceService aviationInvoiceService;
    private final AccountRepository accountRepository;
    private final BillingLedgerService billingLedgerService;
    private final AircraftRegistrationRepository aircraftRegistrationRepository;
    private final UnifiedTaxRepository unifiedTaxRepository;
    private final AccountService accountService;
    private final UnifiedTaxService unifiedTaxService;
    private final AircraftRegistrationService aircraftRegistrationService;

//    public AsyncInvoiceGeneratorProcessor(final AviationInvoiceService aviationInvoiceService) {
//        this.aviationInvoiceService = aviationInvoiceService;
//    }
    
    public AsyncInvoiceGeneratorProcessor(final AccountRepository accountRepository,
            final AccountService accountService, final AviationInvoiceService aviationInvoiceService,
            final BillingLedgerService billingLedgerService,
            final AircraftRegistrationRepository aircraftRegistrationRepository,
            final UnifiedTaxRepository unifiedTaxRepository, final UnifiedTaxService unifiedTaxService,
            final AircraftRegistrationService aircraftRegistrationService) {
        this.aviationInvoiceService = aviationInvoiceService;
        this.accountService = accountService;
        this.accountRepository = accountRepository;
        this.billingLedgerService = billingLedgerService;
        this.aircraftRegistrationRepository = aircraftRegistrationRepository;
        this.unifiedTaxRepository = unifiedTaxRepository;
        this.unifiedTaxService = unifiedTaxService;
        this.aircraftRegistrationService = aircraftRegistrationService;
    }
    
    public AsyncInvoiceGeneratorScope processItem (final AsyncInvoiceGeneratorScope scope) {
        final User currentUser = scope.getCurrentUser();
        if (currentUser.getBillingCenter() == null) {
            throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class, currentUser.getId().toString(), currentUser.getLogin());
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
        scope.getInvoiceProgressCounter().setMessage("Generating the invoices");
        if (scope.getAccountIdList() != null) {
            List<Integer> accountUTlist = new ArrayList<>();
            List<Integer> accountNotUTlist = new ArrayList<>();
            // List <Account> accountList = new ArrayList<>();
            Account account = null;
            scope.getInvoiceProgressCounter().resetAccountsTotal(scope.getAccountIdList().size());

            for (Integer accountId : scope.getAccountIdList()) {
                account = accountRepository.findAccountById(accountId);
                if (account != null) {
                    if (account.getAccountType().getName().equals("Unified Tax"))
                        accountUTlist.add(accountId);
                } else {
                    accountNotUTlist.add(accountId);
                }
            }
            Account a = null;
            BillingLedger bl = null;
            for (final Integer accountId : accountUTlist) {
                Boolean result;
                Integer aI = accountId.intValue();
                // a = accountRepository.findAccountById(aI);
                a = accountService.findAccountByIdwithBillingLedgerAndAircraft(aI);
                if (scope.getPreview()) {
                    result = aviationInvoiceService.previewAccountUT(scope, accountRepository, billingLedgerService,
                            aircraftRegistrationService, a, unifiedTaxService, invoiceList, flightmovementCategory,
                            currentUser);
                } else {

                    result = aviationInvoiceService.processAccountUT(scope, accountRepository, billingLedgerService,
                            aircraftRegistrationService, a, unifiedTaxService, invoiceList, flightmovementCategory,
                            currentUser);
                }
                if (result) {
                    scope.getInvoiceProgressCounter().increaseProcessed();
                } else {
                    scope.getInvoiceProgressCounter().increaseDiscarded();
                }
            }

            for (final Integer accountId : accountNotUTlist) {
                Boolean result;
                scope.getInvoiceProgressCounter().increaseAccountNumber();
                try {
                    if (scope.getPreview()) {
                        result = aviationInvoiceService.previewAccount(scope, accountId, invoiceList,
                                flightmovementCategory, currentUser);
                    } else {
                        result = aviationInvoiceService.processAccount(scope, accountId, invoiceList,
                                flightmovementCategory, currentUser);
                    }
                    if (result) {
                        scope.getInvoiceProgressCounter().increaseProcessed();
                    } else {
                        scope.getInvoiceProgressCounter().increaseDiscarded();
                    }
                } catch (Exception e) {
                    LOG.debug("Il processo non è andato a buon fine");
                }
            }
        }
        scope.getInvoiceProgressCounter().resetAccountsTotal(0);
        
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
