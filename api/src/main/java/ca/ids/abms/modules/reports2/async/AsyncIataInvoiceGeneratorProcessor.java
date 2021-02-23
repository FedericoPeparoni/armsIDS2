package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.billings.InvoicesApprovalWorkflow;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.jobs.ItemProcessor;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.iata.IataInvoiceDocumentCreator;
import ca.ids.abms.modules.reports2.invoices.iata.IataInvoiceService;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AsyncIataInvoiceGeneratorProcessor extends IataInvoiceService implements ItemProcessor<AsyncInvoiceGeneratorScope> {

    public AsyncIataInvoiceGeneratorProcessor(final ReportHelper reportHelper,
                                              final IataInvoiceDocumentCreator iataInvoiceDocumentCreator,
                                              final FlightMovementRepository flightMovementRepository,
                                              final BillingLedgerService billingLedgerService,
                                              final UserService userService,
                                              final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                                              final CurrencyUtils currencyUtils,
                                              final UserEventLogService userEventLogService,
                                              final InvoicesApprovalWorkflow invoicesApprovalWorkflow,
                                              final RoundingUtils roundingUtils,
                                              final SystemConfigurationService systemConfigurationService)
    {
        super(reportHelper, iataInvoiceDocumentCreator, flightMovementRepository, billingLedgerService,
            userService, invoiceSequenceNumberHelper, currencyUtils, userEventLogService, invoicesApprovalWorkflow, roundingUtils, systemConfigurationService);
    }

    @Transactional
    public AsyncInvoiceGeneratorScope processItem (final AsyncInvoiceGeneratorScope scope) {
        if (scope.getPreview()) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        final ZonedDateTime zdNow = ZonedDateTime.now(ReportHelper.UTC_ZONE_ID);
        final LocalDateTime ldtNow = zdNow.toLocalDateTime();
        final User currentUser = scope.getCurrentUser();
        validate(scope.getBillingInterval(), scope.getStartDate(), scope.getEndDateInclusive(), ldtNow,
            getReportHelper().getBillingCenterOfCurrentUser(currentUser), scope.getAccountIdList(), true, scope.getPreview());

        final List<FlightMovement> flightMovements = loadFlightMovements(scope.getBillingInterval(), scope.getStartDate(),
            scope.getEndDateInclusive(), scope.getFormat(), getReportHelper().getBillingCenterOfCurrentUser(currentUser), scope.getAccountIdList(), true,
            scope.getPreview(), scope.getIataInvoiceItemOrder(), scope.getUserBillingCenterOnly(),  null);

        final ReportDocument result = generateInvoice(scope.getBillingInterval(),  scope.getStartDate(),
            scope.getEndDateInclusive(), scope.getFormat(), getReportHelper().getBillingCenterOfCurrentUser(currentUser),
            scope.getAccountIdList(), true, scope.getPreview(),
            scope.getIataInvoiceItemOrder(), scope.getUserBillingCenterOnly(), scope.getIpAddress(), scope.getInvoiceProgressCounter(),
            flightMovements, ldtNow, currentUser);

        scope.getInvoiceProgressCounter().resetAccountsTotal(0);
        if (scope.getPreview() || InvoiceStateType.PUBLISHED.equals(getInitialInvoiceStateType())) {
            scope.getInvoiceProgressCounter().setMessage("Returning the report created");
            scope.getInvoiceProgressCounter().update();
            scope.setResult(result);
        }
        return scope;
    }
}
