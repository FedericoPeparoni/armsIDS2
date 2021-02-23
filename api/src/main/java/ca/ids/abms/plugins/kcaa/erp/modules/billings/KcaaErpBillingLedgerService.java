package ca.ids.abms.plugins.kcaa.erp.modules.billings;

import ca.ids.abms.modules.billings.*;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.reports2.common.CachedAerodromeResolver;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.plugins.common.modules.AbstractPluginBillingLedgerService;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import ca.ids.abms.plugins.kcaa.erp.modules.chargesadjustment.KcaaErpChargesAdjustmentService;
import ca.ids.abms.plugins.kcaa.erp.modules.flightmovement.KcaaErpFlightMovementService;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderMapper;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderService;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderUtility;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLineService;
import ca.ids.abms.plugins.kcaa.erp.modules.transactions.KcaaErpTransactionService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class KcaaErpBillingLedgerService extends AbstractPluginBillingLedgerService {

    private final FlightMovementService flightMovementService;

    private final KcaaErpChargesAdjustmentService kcaaErpChargesAdjustmentService;

    private final KcaaErpFlightMovementService kcaaErpFlightMovementService;

    private final KcaaErpInvoiceLineItemService kcaaErpInvoiceLineItemService;

    private final KcaaErpInvoiceOverduePenaltiesService kcaaErpInvoiceOverduePenaltiesService;

    private final SalesHeaderMapper salesHeaderMapper;

    private final SalesHeaderUtility salesHeaderUtility;

    private final SalesLineService salesLineService;

    private final SalesHeaderService salesHeaderService;

    @SuppressWarnings("squid:S00107")
    public KcaaErpBillingLedgerService(
        final BillingLedgerRepository billingLedgerRepository,
        final CurrencyUtils currencyUtils,
        final FlightMovementService flightMovementService,
        final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
        final KcaaErpChargesAdjustmentService kcaaErpChargesAdjustmentService,
        final KcaaErpFlightMovementService kcaaErpFlightMovementService,
        final KcaaErpInvoiceLineItemService kcaaErpInvoiceLineItemService,
        final KcaaErpInvoiceOverduePenaltiesService kcaaErpInvoiceOverduePenaltiesService,
        final KcaaErpTransactionService kcaaTransactionService,
        final ReportHelper reportHelper,
        final SalesHeaderMapper salesHeaderMapper,
        final SalesHeaderUtility salesHeaderUtility,
        final SalesLineService salesLineService,
        final SalesHeaderService salesHeaderService
    ) {
        super(billingLedgerRepository, invoiceOverduePenaltyRepository, kcaaTransactionService, reportHelper, currencyUtils);
        this.flightMovementService = flightMovementService;
        this.kcaaErpChargesAdjustmentService = kcaaErpChargesAdjustmentService;
        this.kcaaErpFlightMovementService = kcaaErpFlightMovementService;
        this.kcaaErpInvoiceLineItemService = kcaaErpInvoiceLineItemService;
        this.kcaaErpInvoiceOverduePenaltiesService = kcaaErpInvoiceOverduePenaltiesService;
        this.salesHeaderMapper = salesHeaderMapper;
        this.salesHeaderUtility = salesHeaderUtility;
        this.salesLineService = salesLineService;
        this.salesHeaderService = salesHeaderService;
    }

    // region Create Invoice

    /**
     * Perform necessary external database changes for created billing ledger with
     * associated flight movements.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements
     * @param invoiceOverduePenalties overdue penalties
     * @return true if exported else false
     */
    @Override
    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean create(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                          final List<InvoiceOverduePenalty> invoiceOverduePenalties) {
        return super.create(billingLedger, flightMovements, invoiceOverduePenalties);
    }

    /**
     * Cacheable wrapper for `create` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements
     * @param invoiceOverduePenalties overdue penalties
     * @return true if exported else false
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCacheable(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                                   final List<InvoiceOverduePenalty> invoiceOverduePenalties) {
        return super.createCacheable(billingLedger, flightMovements, invoiceOverduePenalties);
    }

    // endregion Create Invoice

    // region Create Header and Entries

    /**
     * Use to create ERP sales header
     *
     * @param billingLedger billing ledger
     */
    @Override
    protected void createHeader(BillingLedger billingLedger) {
        // map billing ledger object to sales header object and insert
        salesHeaderService.insert(salesHeaderMapper.toSalesHeader(billingLedger));
    }

    /**
     * Create sales lines from charges adjustments.
     *
     * @param billingLedger billing ledger with associated charges adjustments
     * @param lineNo line number of last item
     */
    @Override
    protected void createEntriesByChargesAdjustments(BillingLedger billingLedger, Integer[] lineNo) {
        for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            kcaaErpChargesAdjustmentService.create(chargesAdjustment, lineNo[0]);
        }
    }

    /**
     * Create sales lines from flight movements.
     *
     * @param billingLedger billing ledger with flight movements
     * @param flightMovements billed flight movements
     * @param lineNo line number of last item
     */
    @Override
    protected void createEntriesByFlightMovements(BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] lineNo) {

        // attempt to get flight movements if none supplied
        // this is necessary in the event a billing ledger is approved
        // and we must get the flight movements for the invoice
        if (flightMovements == null || flightMovements.isEmpty())
            flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());

        // set cached services to use for all conversions to improve performance
        CachedCurrencyConverter currencyConverter = getCurrencyConverter(billingLedger.getInvoicePeriodOrDate());
        CachedAerodromeResolver aerodromeResolver = getAerodromeResolver();

        // sort flight movements by logical billing order
        // match invoice FlightMovementRepository.findForGeneralAviationInvoiceByAccount
        flightMovements.sort(Comparator
            .comparing(FlightMovement::getBillingDate)
            .thenComparing(FlightMovement::getFlightId)
            .thenComparing(FlightMovement::getId));

        for (FlightMovement flightMovement : flightMovements) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            kcaaErpFlightMovementService.create(flightMovement, billingLedger, lineNo[0], currencyConverter, aerodromeResolver);
        }
    }

    /**
     * Create sales lines from invoice line items.
     *
     * @param billingLedger billing ledger with invoice line items
     * @param lineNo line number of last item
     */
    @Override
    protected void createEntriesByInvoiceLineItems(BillingLedger billingLedger, Integer[] lineNo) {
        for (InvoiceLineItem invoiceLineItem : billingLedger.getInvoiceLineItems()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            kcaaErpInvoiceLineItemService.create(invoiceLineItem, lineNo[0]);
        }
    }

    /**
     * Create sales lines from invoice overdue penalties.
     *
     * @param invoiceOverduePenalties billed invoice overdue penalties
     * @param lineNo line number of last item
     */
    @Override
    protected void createEntriesByInvoiceOverduePenalties(List<InvoiceOverduePenalty> invoiceOverduePenalties, Integer[] lineNo) {

        if (invoiceOverduePenalties == null || invoiceOverduePenalties.isEmpty())
            return;

        for (InvoiceOverduePenalty invoiceOverduePenalty : invoiceOverduePenalties) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            kcaaErpInvoiceOverduePenaltiesService.create(invoiceOverduePenalty, lineNo[0]);
        }
    }

    // endregion Create Header and Entries

    // region Create Header and Entry Statements

    /**
     * Use to get the SQL statements generated when creating a sales header in ERP.
     *
     * @param billingLedger billing ledger
     */
    @Override
    protected List<PluginSqlStatement> createHeaderStatement(BillingLedger billingLedger) {
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(salesHeaderUtility.insertStatement(salesHeaderMapper.toSalesHeader(billingLedger)));
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating charge adjustments in ERP.
     *
     * @param billingLedger billing ledger with charge adjustments to create sales lines
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByChargesAdjustments(BillingLedger billingLedger, Integer[] lineNo) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            statements.add(kcaaErpChargesAdjustmentService.createStatement(chargesAdjustment, lineNo[0]));
        }

        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating flight movements in ERP.
     *
     * @param billingLedger billing ledger related to flight movements
     * @param flightMovements flight movements to create sales lines
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByFlightMovements(BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] lineNo){

        // attempt to get flight movements if none supplied
        // this is necessary in the event a billing ledger is approved
        // and we must get the flight movements for the invoice
        if (flightMovements == null || flightMovements.isEmpty())
            flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());

        // set cached services to use for all conversions to improve performance
        CachedCurrencyConverter currencyConverter = getCurrencyConverter(billingLedger.getInvoicePeriodOrDate());
        CachedAerodromeResolver aerodromeResolver = getAerodromeResolver();

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        for (FlightMovement flightMovement : flightMovements) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            statements.add(kcaaErpFlightMovementService.createStatement(flightMovement, billingLedger, lineNo[0],
                currencyConverter, aerodromeResolver));
        }

        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating invoice line items in ERP.
     *
     * @param billingLedger billing ledger with invoice line items to create sales lines
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByInvoiceLineItems(final BillingLedger billingLedger, Integer[] lineNo) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        for (InvoiceLineItem invoiceLineItem : billingLedger.getInvoiceLineItems()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            statements.add(kcaaErpInvoiceLineItemService.createStatement(invoiceLineItem, lineNo[0]));
        }

        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating invoice overdue penalties in ERP.
     *
     * @param invoiceOverduePenalties invoice overdue penalties associated with billing ledger
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByInvoiceOverduePenalties(final List<InvoiceOverduePenalty> invoiceOverduePenalties, Integer[] lineNo) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        for(InvoiceOverduePenalty invoiceOverduePenalty : invoiceOverduePenalties) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            statements.add(kcaaErpInvoiceOverduePenaltiesService.createStatement(invoiceOverduePenalty, lineNo[0]));
        }

        return statements;
    }

    // endregion Create Header and Entry Statements
}
