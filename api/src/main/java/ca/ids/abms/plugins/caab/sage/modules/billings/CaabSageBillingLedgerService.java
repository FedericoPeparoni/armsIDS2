package ca.ids.abms.plugins.caab.sage.modules.billings;

import ca.ids.abms.modules.billings.*;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeader;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderUtility;
import ca.ids.abms.plugins.caab.sage.modules.chargesadjustments.CaabSageChargesAdjustmentService;
import ca.ids.abms.plugins.caab.sage.modules.distributioncode.DistributionCode;
import ca.ids.abms.plugins.caab.sage.modules.flightmovements.CaabSageFlightMovementService;
import ca.ids.abms.plugins.caab.sage.modules.overduepenalties.CaabSageOverduePenaltiesService;
import ca.ids.abms.plugins.caab.sage.modules.system.CaabSageConfigurationItemName;
import ca.ids.abms.plugins.caab.sage.modules.transactions.CaabSageTransactionService;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.plugins.common.modules.AbstractPluginBillingLedgerService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class CaabSageBillingLedgerService extends AbstractPluginBillingLedgerService {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageBillingLedgerService.class);

    private final ARInvoiceHeaderMapper arInvoiceHeaderMapper;
    private final ARInvoiceHeaderService arInvoiceHeaderService;
    private final ARInvoiceHeaderUtility arInvoiceHeaderUtility;

    private final CaabSageLineItemService lineItemService;
    private final CaabSageChargesAdjustmentService chargesAdjustmentService;
    private final CaabSageOverduePenaltiesService overduePenaltiesService;
    private final CaabSageFlightMovementService caabSageFlightMovementService;

    private final FlightMovementService flightMovementService;
    private final SystemConfigurationService systemConfigurationService;

    @SuppressWarnings("squid:S00107")
    public CaabSageBillingLedgerService(
        final BillingLedgerRepository billingLedgerRepository,
        final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
        final CaabSageTransactionService caabTransactionService,
        final ReportHelper reportHelper,
        final CurrencyUtils currencyUtils,
        final ARInvoiceHeaderMapper arInvoiceHeaderMapper,
        final ARInvoiceHeaderService arInvoiceHeaderService,
        final ARInvoiceHeaderUtility arInvoiceHeaderUtility,
        final CaabSageLineItemService lineItemService,
        final CaabSageChargesAdjustmentService chargesAdjustmentService,
        final CaabSageOverduePenaltiesService overduePenaltiesService,
        final CaabSageFlightMovementService caabSageFlightMovementService,
        final FlightMovementService flightMovementService,
        final SystemConfigurationService systemConfigurationService
    ) {
        super(billingLedgerRepository, invoiceOverduePenaltyRepository, caabTransactionService,
            reportHelper, currencyUtils);

        this.arInvoiceHeaderMapper = arInvoiceHeaderMapper;
        this.arInvoiceHeaderService = arInvoiceHeaderService;
        this.arInvoiceHeaderUtility  = arInvoiceHeaderUtility;

        this.lineItemService = lineItemService;
        this.chargesAdjustmentService = chargesAdjustmentService;
        this.overduePenaltiesService = overduePenaltiesService;
        this.caabSageFlightMovementService = caabSageFlightMovementService;

        this.flightMovementService = flightMovementService;
        this.systemConfigurationService = systemConfigurationService;
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
    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCacheable(final BillingLedger billingLedger, final List<FlightMovement> flightMovements,
                                   final List<InvoiceOverduePenalty> invoiceOverduePenalties) {
        return super.createCacheable(billingLedger, flightMovements, invoiceOverduePenalties);
    }

    // endregion Create Invoice

    // region Create Header and Entries

    /**
     * Create header entry from billing ledger. A header is an entry that is contrasted by
     * a line item.
     *
     * @param billingLedger billing ledger.
     */
    @Override
    protected void createHeader(final BillingLedger billingLedger) {

        // map BillingLedger to ARInvoiceHeader entity
        ARInvoiceHeader arInvoiceHeader = arInvoiceHeaderMapper.toARInvoiceHeader(billingLedger);

        // insert newly mapped ARInvoiceHeader from BillingLedger
        arInvoiceHeaderService.insert(arInvoiceHeader);
    }

    /**
     * Create sales lines from charges adjustments.
     *
     * @param billingLedger billing ledger with associated charges adjustments
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByChargesAdjustments(final BillingLedger billingLedger, final Integer[] ignored) {
        if (billingLedger == null || billingLedger.getChargesAdjustment() == null) {
            LOG.warn("No charge adjustments to export for BillingLedger: {}", billingLedger);
            return;
        }

        for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
            chargesAdjustmentService.create(chargesAdjustment);
        }
    }

    /**
     * Create sales lines from flight movements.
     *
     * @param billingLedger billing ledger with flight movements
     * @param flightMovements billed flight movements
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByFlightMovements(BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] ignored) {

        // attempt to get flight movements if none supplied
        // this is necessary in the event a billing ledger is approved
        // and we must get the flight movements for the invoice
        if (flightMovements == null || flightMovements.isEmpty())
            flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());

        // get a cached currency convert to use when processing flight movements
        CachedCurrencyConverter currencyConverter = getCurrencyConverter(billingLedger.getInvoicePeriodOrDate());

        // loop through each flight movement and create entry for each charge type
        for (FlightMovement flightMovement : flightMovements) {

            // resolve flight movement into chargeable line items
            // caab sage requires each flight movement charge as a separate line item
            Map<DistributionCode.FlightMovementChargeType, Double> charges = CaabSageMapperHelper
                .resolveFlightMovementLineItems(billingLedger, flightMovement);

            // loop through each flight movement chargeable line item and create entry
            for(Map.Entry<DistributionCode.FlightMovementChargeType, Double> charge : charges.entrySet()) {
                caabSageFlightMovementService.create(
                    flightMovement, determineChargeCode(flightMovement, charge.getKey()),
                    billingLedger.getBillingCenter(), charge.getKey(), billingLedger, currencyConverter);
            }
        }
    }

    /**
     * Create sales lines from invoice line items.
     *
     * @param billingLedger billing ledger with invoice line items
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByInvoiceLineItems(final BillingLedger billingLedger, final Integer[] ignored) {
        if (billingLedger == null || billingLedger.getInvoiceLineItems() == null) {
            LOG.warn("No invoice line items to export for BillingLedger: {}", billingLedger);
            return;
        }

        for (InvoiceLineItem invoiceLineItem : billingLedger.getInvoiceLineItems()) {
            lineItemService.create(invoiceLineItem);
        }
    }

    /**
     * Create sales lines from invoice overdue penalties.
     *
     * @param invoiceOverduePenalties billed invoice overdue penalties
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByInvoiceOverduePenalties(final List<InvoiceOverduePenalty> invoiceOverduePenalties, final Integer[] ignored) {
        if (invoiceOverduePenalties == null) {
            LOG.debug("No invoice overdue penalties to export");
            return;
        }

        for (InvoiceOverduePenalty invoiceOverduePenalty : invoiceOverduePenalties) {
            overduePenaltiesService.create(invoiceOverduePenalty);
        }
    }

    // endregion Create Header and Entries

    // region Create Header and Entry Statements

    /**
     * Use to get the SQL statements generated when creating a "header". A header is an entry that is contrasted by
     * a line item.
     *
     * @param billingLedger billing ledger
     */
    @Override
    protected List<PluginSqlStatement> createHeaderStatement(BillingLedger billingLedger) {
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(arInvoiceHeaderUtility.insertStatement(arInvoiceHeaderMapper.toARInvoiceHeader(billingLedger)));
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating charge adjustments in Sage.
     *
     * @param billingLedger billing ledger with charge adjustments to create sales lines
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByChargesAdjustments(BillingLedger billingLedger, Integer[] ignored) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();
        for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
            statements.add(chargesAdjustmentService.createStatement(chargesAdjustment));
        }
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating invoice line items in the external database
     *
     * @param billingLedger billing ledger with invoice line items to create entries
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByInvoiceLineItems(
        final BillingLedger billingLedger, Integer[] ignored
    ) {
        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();
        for (InvoiceLineItem invoiceLineItem : billingLedger.getInvoiceLineItems()) {
            statements.add(lineItemService.createStatement(invoiceLineItem));
        }
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating invoice overdue penalties in Sage.
     *
     * @param invoiceOverduePenalties invoice overdue penalties associated with billing ledger
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByInvoiceOverduePenalties(final List<InvoiceOverduePenalty> invoiceOverduePenalties, Integer[] ignored) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();
        for(InvoiceOverduePenalty invoiceOverduePenalty : invoiceOverduePenalties) {
            statements.add(overduePenaltiesService.createStatement(invoiceOverduePenalty));
        }
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating flight movements in ERP.
     *
     * @param billingLedger billing ledger related to flight movements
     * @param flightMovements flight movements to create sales lines
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createStatementsByFlightMovements(
        BillingLedger billingLedger, List<FlightMovement> flightMovements, Integer[] ignored
    ){

        // attempt to get flight movements if none supplied
        // this is necessary in the event a billing ledger is approved
        // and we must get the flight movements for the invoice
        if (flightMovements == null || flightMovements.isEmpty())
            flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());

        // get a cached currency convert to use when processing flight movements
        CachedCurrencyConverter currencyConverter = getCurrencyConverter(billingLedger.getInvoicePeriodOrDate());

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        // loop through each flight movement and create statement for each charge type
        for (FlightMovement flightMovement : flightMovements) {

            // resolve flight movement into chargeable line items
            // caab sage requires each flight movement charge as a separate line item
            Map<DistributionCode.FlightMovementChargeType, Double> charges = CaabSageMapperHelper
                .resolveFlightMovementLineItems(billingLedger, flightMovement);

            // loop through each flight movement chargeable line item and create statement
            for(Map.Entry<DistributionCode.FlightMovementChargeType, Double> charge : charges.entrySet()) {
                statements.add(caabSageFlightMovementService.createStatement(
                    flightMovement, determineChargeCode(flightMovement, charge.getKey()),
                    billingLedger.getBillingCenter(), charge.getKey(), billingLedger, currencyConverter));
            }
        }

        return statements;
    }

    // endregion Create Header and Entry Statements

    // region Validate Invoice Header and Entries

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param ignored line numbering not used
     */
    @Override
    protected void validateEntriesByChargesAdjustments(final BillingLedger billingLedger, final Integer[] ignored) {
        if (billingLedger == null || billingLedger.getChargesAdjustment() == null)
            return;

        for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
            chargesAdjustmentService.validate(chargesAdjustment);
        }
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param flightMovements billed flight movements
     * @param ignored line numbering not used
     */
    @Override
    protected void validateEntriesByFlightMovements(final BillingLedger billingLedger,
                                                    List<FlightMovement> flightMovements, final Integer[] ignored) {

        // attempt to get flight movements if none supplied
        // this is necessary in the event a billing ledger is approved
        // and we must get the flight movements for the invoice
        if (flightMovements == null || flightMovements.isEmpty())
            flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());

        // loop through each flight movement and create entry for each charge type
        for (FlightMovement flightMovement : flightMovements) {

            // resolve flight movement into chargeable line items
            // caab sage requires each flight movement charge as a separate line item
            Map<DistributionCode.FlightMovementChargeType, Double> charges = CaabSageMapperHelper
                .resolveFlightMovementLineItems(billingLedger, flightMovement);

            // loop through each flight movement chargeable line item and create entry
            for(Map.Entry<DistributionCode.FlightMovementChargeType, Double> charge : charges.entrySet()) {
                caabSageFlightMovementService.validate(
                    flightMovement, determineChargeCode(flightMovement, charge.getKey()),
                    billingLedger.getBillingCenter());
            }
        }
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param billingLedger billing ledger created
     * @param ignored line numbering not used
     */
    @Override
    protected void validateEntriesByInvoiceLineItems(final BillingLedger billingLedger, final Integer[] ignored) {
        if (billingLedger == null || billingLedger.getInvoiceLineItems() == null)
            return;

        for (InvoiceLineItem invoiceLineItem : billingLedger.getInvoiceLineItems()) {
            lineItemService.validate(invoiceLineItem);
        }
    }

    /**
     * Perform necessary validation for created billing ledger entries.
     *
     * @param invoiceOverduePenalties invoice overdue penalties created
     * @param ignored line numbering not used
     */
    @Override
    protected void validateEntriesByInvoiceOverduePenalties(final List<InvoiceOverduePenalty> invoiceOverduePenalties,
                                                            final Integer[] ignored) {
        if (invoiceOverduePenalties == null)
            return;

        for (InvoiceOverduePenalty invoiceOverduePenalty : invoiceOverduePenalties) {
            overduePenaltiesService.validate(invoiceOverduePenalty);
        }
    }

    // endregion Validate Invoice Header and Entries

    // region PRIVATE: Determine Charge Code

    /**
     * Determine charge code based on flight movement and charge type.
     *
     * @param flightMovement flight movement to determine charge code of
     * @param chargeType is one of the charges associated with the flight movement
     */
    private String determineChargeCode(
        final FlightMovement flightMovement, final DistributionCode.FlightMovementChargeType chargeType
    ) {

        boolean isScheduled = flightMovement.getFlightType() != null && flightMovement
            .getFlightType().toUpperCase(Locale.US).contains("S");

        boolean isDomestic = flightMovement.getMovementType().equals(FlightMovementType.DOMESTIC);

        return isScheduled
            ? determineChargeCodeScheduled(isDomestic, chargeType)
            : determineChargeCodeNonScheduled(isDomestic, chargeType);
    }

    /**
     * Determine charge code for scheduled charge types.
     *
     * @param isDomestic specifies whether the flight is domestic or international
     * @param chargeType is one of the charges associated with the flight movement
     */
    private String determineChargeCodeScheduled(
        final Boolean isDomestic, final DistributionCode.FlightMovementChargeType chargeType
    ) {
        String result;
        switch (chargeType) {
            case ENROUTE_CHARGES:
                result = CaabSageConfigurationItemName.ENROUTE_NAVIGATION_CHARGES_SCHEDULED_CODE;
                break;
            case DOMESTIC_PASSENGER_CHARGES:
                result = CaabSageConfigurationItemName.DOMESTIC_PASSENGER_SERVICE_CHARGES_CODE;
                break;
            case INTERNATIONAL_PASSENGER_CHARGES:
                result = CaabSageConfigurationItemName.INTERNAIONAL_PASSENGER_SERVICE_CHARGES_CODE;
                break;
            case LANDING_CHARGES:
                result = isDomestic
                    ? CaabSageConfigurationItemName.DOMESTIC_LANDING_CHARGES_SCHEDULED_CODE
                    : CaabSageConfigurationItemName.INTERNATIONAL_LANDING_CHARGES_SCHEDULED_CODE;
                break;
            case PARKING_CHARGES:
                result = CaabSageConfigurationItemName.PARKING_CHARGES_CODE;
                break;
            default:
                result = null;
                break;
        }

        return systemConfigurationService.getValue(result);
    }

    /**
     * Determine charge code for non-scheduled charge types.
     *
     * @param isDomestic specifies whether the flight is domestic or international
     * @param chargeType is one of the charges associated with the flight movement
     */
    private String determineChargeCodeNonScheduled(
        final Boolean isDomestic, final DistributionCode.FlightMovementChargeType chargeType
    ) {
        String result;
        switch (chargeType) {
            case ENROUTE_CHARGES:
                result = CaabSageConfigurationItemName.ENROUTE_NAVIGATION_CHARGES_NONSCHEDULED_CODE;
                break;
            case DOMESTIC_PASSENGER_CHARGES:
                result = CaabSageConfigurationItemName.DOMESTIC_PASSENGER_SERVICE_CHARGES_CODE;
                break;
            case INTERNATIONAL_PASSENGER_CHARGES:
                result = CaabSageConfigurationItemName.INTERNAIONAL_PASSENGER_SERVICE_CHARGES_CODE;
                break;
            case LANDING_CHARGES:
                result = isDomestic
                    ? CaabSageConfigurationItemName.DOMESTIC_LANDING_CHARGES_NONSCHEDULED_CODE
                    : CaabSageConfigurationItemName.INTERNATIONAL_LANDING_CHARGES_NONSCHEDULED_CODE;
                break;
            case PARKING_CHARGES:
                result = CaabSageConfigurationItemName.PARKING_CHARGES_CODE;
                break;
            default:
                result = null;
                break;
        }

        return systemConfigurationService.getValue(result);
    }

    // endregion Determine Charge Code
}
