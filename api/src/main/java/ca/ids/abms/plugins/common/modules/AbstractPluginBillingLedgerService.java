package ca.ids.abms.plugins.common.modules;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerRepository;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.billings.InvoiceOverduePenaltyRepository;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.CachedAerodromeResolver;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPluginBillingLedgerService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPluginBillingLedgerService.class);

    private final BillingLedgerRepository billingLedgerRepository;
    private final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository;
    private final AbstractPluginTransactionService abstractTransactionService;
    private final ReportHelper reportHelper;
    private final CurrencyUtils currencyUtils;

    public AbstractPluginBillingLedgerService(
        final BillingLedgerRepository billingLedgerRepository,
        final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
        final AbstractPluginTransactionService abstractTransactionService,
        final ReportHelper reportHelper,
        final CurrencyUtils currencyUtils
    ) {
        this.billingLedgerRepository = billingLedgerRepository;
        this.invoiceOverduePenaltyRepository = invoiceOverduePenaltyRepository;
        this.abstractTransactionService = abstractTransactionService;
        this.reportHelper = reportHelper;
        this.currencyUtils = currencyUtils;
    }

    // region Create Invoice

    /**
     * Perform necessary external database changes for created billing ledger with
     * associated flight movements.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements (if applicable)
     * @param invoiceOverduePenalties overdue penalties (if applicable)
     * @return true if exported else false
     */
    @Transactional
    public Boolean create(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                       final List<InvoiceOverduePenalty> invoiceOverduePenalties) {
        LOG.debug("Create entries with saved billing ledger {}", billingLedger);

        // validate that billing ledger is PUBLISHED or PAID before updating the external system
        if (!InvoiceStateType.PUBLISHED.toValue().equals(billingLedger.getInvoiceStateType())
            && !InvoiceStateType.PAID.toValue().equals(billingLedger.getInvoiceStateType()))
            return false;

        // validate that billing ledger can be created
        validate(billingLedger, flightMovements, invoiceOverduePenalties);

        // map billing ledger object to sales header object and insert
        createHeader(billingLedger);

        // create appropriate sales lines based on invoice type
        Integer[] lineNo = new Integer[] { 0 };
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        switch(invoiceType) {
            case AVIATION_IATA:
            case AVIATION_NONIATA:
            case PASSENGER:
                createEntriesByFlightMovements(billingLedger, flightMovements, lineNo);
                break;
            case DEBIT_NOTE:
                createEntriesByChargesAdjustments(billingLedger, lineNo);
                break;
            case NON_AVIATION:
                createEntriesByInvoiceLineItems(billingLedger, lineNo);
                break;
            default:
                // ignore as billing ledger invoice type is not supported here
        }

        // create appropriate sales lines based on invoice overdue penalties
        createEntriesByInvoiceOverduePenalties(invoiceOverduePenalties, lineNo);

        // return true indicating create complete
        return true;
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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean createCacheable(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                                final List<InvoiceOverduePenalty> invoiceOverduePenalties) {
        return create(billingLedger, flightMovements, invoiceOverduePenalties);
    }

    /**
     * Use to get the SQL statements generated when creating a billing ledger with
     * flight movements in the exteral database.
     *
     * @param billingLedger billing ledger to create entries
     * @param flightMovements flight movements to create entries
     * @param invoiceOverduePenalties overdue penalties to create entries
     * @return SQL statements generated
     */
    public PluginSqlStatement[] createStatement(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                                                final List<InvoiceOverduePenalty> invoiceOverduePenalties) {

        List<PluginSqlStatement> statements = new ArrayList<>(createHeaderStatement(billingLedger));

        // create appropriate sales lines based on invoice type
        Integer[] lineNo = new Integer[] { 0 };
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        switch(invoiceType) {
            case AVIATION_IATA:
            case AVIATION_NONIATA:
            case PASSENGER:
                statements.addAll(createStatementsByFlightMovements(billingLedger, flightMovements, lineNo));
                break;
            case DEBIT_NOTE:
                statements.addAll(createStatementsByChargesAdjustments(billingLedger, lineNo));
                break;
            case NON_AVIATION:
                statements.addAll(createStatementsByInvoiceLineItems(billingLedger, lineNo));
                break;
            default:
                // ignore as billing ledger invoice type is not supported here
        }

        // create appropriate sales lines based on invoice overdue penalties
        statements.addAll(createStatementsByInvoiceOverduePenalties(invoiceOverduePenalties, lineNo));

        return statements.toArray(new PluginSqlStatement[0]);
    }

    // endregion

    // region Create Header and Entries

    /**
     *
     * Create header entry.
     *
     * Currently only used by KCAA ERP plugin.
     *
     * @param billingLedger billing ledger.
     */
    protected abstract void createHeader(BillingLedger billingLedger);

    /**
     * Create sales lines from charges adjustments.
     *
     * @param billingLedger billing ledger with associated charges adjustments
     * @param lineNo line number of last item
     */
    protected abstract void createEntriesByChargesAdjustments(BillingLedger billingLedger, Integer[] lineNo);

    /**
     * Create sales lines from flight movements.
     *
     * @param billingLedger billing ledger with flight movements
     * @param flightMovements billed flight movements
     * @param lineNo line number of last item
     */
    protected abstract void createEntriesByFlightMovements(BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] lineNo);

    /**
     * Create sales lines from invoice line items.
     *
     * @param billingLedger billing ledger with invoice line items
     * @param lineNo line number of last item
     */
    protected abstract void createEntriesByInvoiceLineItems(BillingLedger billingLedger, Integer[] lineNo);

    /**
     * Create sales lines from invoice overdue penalties.
     *
     * @param invoiceOverduePenalties billed invoice overdue penalties
     * @param lineNo line number of last item
     */
    protected abstract void createEntriesByInvoiceOverduePenalties(List<InvoiceOverduePenalty> invoiceOverduePenalties, Integer[] lineNo);

    // endregion

    // region Create Header and Entry Statements

    /**
     * Use to get the SQL statements generated when creating a "header". A header is an entry that is contrasted by
     * a line item.
     *
     * Currently only used by KCAA ERP plugin.
     *
     * @param billingLedger billing ledger with flight movements
     */
    protected abstract List<PluginSqlStatement> createHeaderStatement(BillingLedger billingLedger);

    /**
     * Use to get the SQL statements generated when creating charge adjustments in plugin database.
     *
     * @param billingLedger billing ledger with charge adjustments to create sales lines
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected abstract List<PluginSqlStatement> createStatementsByChargesAdjustments(
        BillingLedger billingLedger, Integer[] lineNo
    );

    /**
     * Use to get the SQL statements generated when creating flight movements in the external database
     *
     * @param billingLedger billing ledger related to flight movements
     * @param flightMovements flight movements to create entries
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected abstract List<PluginSqlStatement> createStatementsByFlightMovements(
        BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] lineNo
    );

    /**
     * Use to get the SQL statements generated when creating invoice line items in the external database
     *
     * @param billingLedger billing ledger with invoice line items to create entries
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected abstract List<PluginSqlStatement> createStatementsByInvoiceLineItems(
        final BillingLedger billingLedger, Integer[] lineNo
    );

    /**
     * Use to get the SQL statements generated when creating invoice overdue penalties in the external database
     *
     * @param invoiceOverduePenalties invoice overdue penalties associated with billing ledger
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected abstract List<PluginSqlStatement> createStatementsByInvoiceOverduePenalties(
        final List<InvoiceOverduePenalty> invoiceOverduePenalties, Integer[] lineNo
    );

    // endregion

    // region Exported

    /**
     * Set billing ledger as exported.
     *
     * @param billingLedger that was exported
     */
    @Transactional
    public void exported(final BillingLedger billingLedger) {

        if (billingLedger == null)
            return;

        BillingLedger existingBillingLedger = billingLedgerRepository.findOne(billingLedger.getId());
        if (existingBillingLedger == null)
            existingBillingLedger = billingLedger;

        // update billing ledger as exported
        existingBillingLedger.setExported(true);
        billingLedgerRepository.save(existingBillingLedger);

        // update transaction as exported
        Transaction transaction = abstractTransactionService.getDebitTransactionByInvoice(billingLedger);
        abstractTransactionService.exported(transaction);
    }

    // endregion

    // region Helper

    /**
     * Retrieve overdue penalties applied to this invoice.
     *
     * @param billingLedger billing ledger to check
     * @return overdue penalties applied to billing ledger
     */
    @Transactional(readOnly = true)
    public List<InvoiceOverduePenalty> overduePenalties(final BillingLedger billingLedger) {
        return invoiceOverduePenaltyRepository.getAllPenaltiesAppliedToInvoice(billingLedger.getId());
    }

    /**
     * Return a cached currency convert class that can be used to increase currency conversion performance.
     *
     * @param exchangeRateDate date of exchange rates
     * @return cached currency converter
     */
    protected CachedCurrencyConverter getCurrencyConverter(final LocalDateTime exchangeRateDate) {
        return new CachedCurrencyConverter(currencyUtils, exchangeRateDate);
    }

    /**
     * Return a cached aerodrome resolver class that can be used to increase aerodrome format resolution performance.
     *
     * @return cached aerodrome resolver
     */
    protected CachedAerodromeResolver getAerodromeResolver() {
        return new CachedAerodromeResolver(reportHelper);
    }

    // endregion

    // region Validate Invoice Header and Entries

    /**
     * Perform necessary validation for created billing ledger.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements (if applicable)
     * @param invoiceOverduePenalties overdue penalties (if applicable)
     */
    public void validate(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                         final List<InvoiceOverduePenalty> invoiceOverduePenalties) {

        // validate billing ledger as a sales header
        validateHeader(billingLedger);

        // create appropriate sales lines based on invoice type
        Integer[] lineNo = new Integer[] { 0 };
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        switch(invoiceType) {
            case AVIATION_IATA:
            case AVIATION_NONIATA:
            case PASSENGER:
                validateEntriesByFlightMovements(billingLedger, flightMovements, lineNo);
                break;
            case DEBIT_NOTE:
                validateEntriesByChargesAdjustments(billingLedger, lineNo);
                break;
            case NON_AVIATION:
                validateEntriesByInvoiceLineItems(billingLedger, lineNo);
                break;
            default:
                // ignore as billing ledger invoice type is not supported here
        }

        // validate appropriate entries based on invoice overdue penalties
        validateEntriesByInvoiceOverduePenalties(invoiceOverduePenalties, lineNo);
    }

    /**
     * Perform necessary validation for created billing ledger header.
     *
     * @param billingLedger billing ledger created
     */
    protected void validateHeader(final BillingLedger billingLedger) {
        // default implementation ignored
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param lineNo line number of last item
     */
    protected void validateEntriesByChargesAdjustments(final BillingLedger billingLedger, final Integer[] lineNo) {
        // default implementation ignored
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements
     * @param lineNo line number of last item
     */
    protected void validateEntriesByFlightMovements(final BillingLedger billingLedger,
                                                    final List<FlightMovement> flightMovements, final Integer[] lineNo) {
        // default implementation ignored
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param lineNo line number of last item
     */
    protected void validateEntriesByInvoiceLineItems(final BillingLedger billingLedger, final Integer[] lineNo) {
        // default implementation ignored
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param invoiceOverduePenalties invoice overdue penalties created
     * @param lineNo line number of last item
     */
    protected void validateEntriesByInvoiceOverduePenalties(final List<InvoiceOverduePenalty> invoiceOverduePenalties,
                                                            final Integer[] lineNo) {
        // default implementation ignored
    }

    // endregion
}
