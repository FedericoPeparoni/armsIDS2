package ca.ids.abms.plugins.caab.sage.modules.transactions;

import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.plugins.caab.sage.config.CaabSageDatabaseConfig;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeader;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderMapper;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderService;
import ca.ids.abms.plugins.caab.sage.modules.arinvoiceheader.ARInvoiceHeaderUtility;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentheader.ARPaymentHeader;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentheader.ARPaymentHeaderMapper;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentheader.ARPaymentHeaderService;
import ca.ids.abms.plugins.caab.sage.modules.arpaymentheader.ARPaymentHeaderUtility;
import ca.ids.abms.plugins.caab.sage.modules.chargesadjustments.CaabSageChargesAdjustmentService;
import ca.ids.abms.plugins.caab.sage.modules.payment.CaabSageTransactionPaymentService;
import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CaabSageTransactionService extends AbstractPluginTransactionService {

    private final CaabSageChargesAdjustmentService chargesAdjustmentService;

    private final CaabSageTransactionPaymentService transactionPaymentService;

    private final ARInvoiceHeaderMapper arInvoiceHeaderMapper;
    private final ARInvoiceHeaderService arInvoiceHeaderService;
    private final ARInvoiceHeaderUtility arInvoiceHeaderUtility;

    private final ARPaymentHeaderMapper arPaymentHeaderMapper;
    private final ARPaymentHeaderService arPaymentHeaderService;
    private final ARPaymentHeaderUtility arPaymentHeaderUtility;

    @SuppressWarnings("squid:S00107")
    public CaabSageTransactionService(
        final TransactionPaymentRepository transactionPaymentRepository,
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository,
        final CaabSageChargesAdjustmentService chargesAdjustmentService,
        final CaabSageTransactionPaymentService transactionPaymentService,
        final ARInvoiceHeaderMapper arInvoiceHeaderMapper,
        final ARInvoiceHeaderService arInvoiceHeaderService,
        final ARInvoiceHeaderUtility arInvoiceHeaderUtility,
        final ARPaymentHeaderMapper arPaymentHeaderMapper,
        final ARPaymentHeaderService arPaymentHeaderService,
        final ARPaymentHeaderUtility arPaymentHeaderUtility
    ) {
        super(transactionPaymentRepository, transactionRepository, transactionTypeRepository);
        this.chargesAdjustmentService = chargesAdjustmentService;
        this.transactionPaymentService = transactionPaymentService;

        this.arInvoiceHeaderMapper = arInvoiceHeaderMapper;
        this.arInvoiceHeaderService = arInvoiceHeaderService;
        this.arInvoiceHeaderUtility = arInvoiceHeaderUtility;

        this.arPaymentHeaderMapper = arPaymentHeaderMapper;
        this.arPaymentHeaderService = arPaymentHeaderService;
        this.arPaymentHeaderUtility = arPaymentHeaderUtility;
    }

    // region Create Credit Note

    /**
     * Perform necessary external database changes for created credit note transaction.
     *
     * @param transaction credit note transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditNote(final Transaction transaction) {
        return super.createCreditNote(transaction);
    }

    /**
     * Cacheable wrapper for `createCreditNote` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param transaction credit note transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditNoteCacheable(final Transaction transaction) {
        return super.createCreditNoteCacheable(transaction);
    }

    // endregion Create Credit Note

    // region Create Payment

    /**
     * Perform necessary external database changes for created credit payment transaction.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditPayment(final TransactionPayment transactionPayment) {
        return super.createCreditPayment(transactionPayment);
    }

    /**
     * Cacheable wrapper for `createPayment` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = CaabSageDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditPaymentCacheable(final TransactionPayment transactionPayment) {
        return super.createCreditPaymentCacheable(transactionPayment);
    }

    // endregion Create Payment

    // region Create Header and Entries

    /**
     * Create header from transaction.
     *
     * @param transaction transaction for creating header.
     */
    @Override
    protected void createHeaderByCreditNote(final Transaction transaction) {

        // map Transaction to ARInvoiceHeader entity
        ARInvoiceHeader arInvoiceHeader = arInvoiceHeaderMapper.toARInvoiceHeader(transaction);

        // insert newly mapped ARInvoiceHeader from Transaction
        arInvoiceHeaderService.insert(arInvoiceHeader);
    }

    /**
     * Create header from transaction payment.
     *
     * @param transactionPayment transaction payment for creating header.
     */
    @Override
    protected void createHeaderByCreditPayment(final TransactionPayment transactionPayment) {

        // map Transaction to ARPaymentHeader entity
        ARPaymentHeader arPaymentHeader = arPaymentHeaderMapper.toARPaymentHeader(transactionPayment);

        // insert newly mapped ARPaymentHeader from Transaction
        arPaymentHeaderService.insert(arPaymentHeader);
    }

    /**
     * Create sales lines from charges adjustments.
     *
     * @param transaction transaction with associated charges adjustments
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByChargesAdjustments(final Transaction transaction, final Integer[] ignored) {
        for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
            chargesAdjustmentService.create(chargesAdjustment);
        }
    }

    /**
     * Create sales lines from transaction payment.
     *
     * @param transactionPayment transaction payments for creating entries.
     * @param ignored line numbering not used
     */
    @Override
    protected void createEntriesByCreditPayment(final TransactionPayment transactionPayment, final Integer[] ignored) {
        transactionPaymentService.create(transactionPayment);
    }

    // endregion Create Header and Entries

    // region Create Header and Entry Statements

    /**
     * Create header statement from transaction
     *
     * @param transaction transaction for creating header statement.
     */
    @Override
    protected List<PluginSqlStatement> createHeaderByCreditNoteStatement(final Transaction transaction) {
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(arInvoiceHeaderUtility.insertStatement(arInvoiceHeaderMapper.toARInvoiceHeader(transaction)));
        return statements;
    }

    /**
     * Create header statement from transaction pavement.
     *
     * @param transactionPayment transaction payment for creating header statement.
     */
    @Override
    protected List<PluginSqlStatement> createHeaderByCreditPaymentStatement(final TransactionPayment transactionPayment) {
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(arPaymentHeaderUtility.insertStatement(arPaymentHeaderMapper.toARPaymentHeader(transactionPayment)));
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating charge adjustments in SAGE.
     *
     * @param transaction transaction with charge adjustments to create sales lines
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createEntriesByChargesAdjustmentsStatements(final Transaction transaction, final Integer[] ignored) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();
        for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
            statements.add(chargesAdjustmentService.createStatement(chargesAdjustment));
        }

        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating transaction payment in SAGE.
     *
     * @param transactionPayment transaction credit payment to create entries
     * @param ignored line numbering not used
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createEntriesByCreditPaymentStatements(
        final TransactionPayment transactionPayment, final Integer[] ignored
    ) {
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(transactionPaymentService.createStatement(transactionPayment));
        return statements;
    }

    // endregion Create Header and Entry Statements

    // region Validate Credit Note and Payment

    /**
     * Perform necessary validation for created credit note transaction entries.
     *
     * @param transaction credit note transaction to create entries
     * @param ignored line numbering not used
     */
    @Override
    protected boolean validateEntriesByChargesAdjustments(final Transaction transaction, final Integer[] ignored) {
        // validate each charge adjustment for credit note
        for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
            if (!chargesAdjustmentService.validate(chargesAdjustment)) return false;
        }
        return true;
    }

    /**
     * Perform necessary validation for created credit payment transaction header.
     *
     * @param transactionPayment credit payment transaction to create header
     */
    @Override
    protected boolean validateHeaderByCreditPayment(final TransactionPayment transactionPayment) {
        return transactionPaymentService.validate(transactionPayment);
    }

    // endregion Validate Credit Note and Payment
}
