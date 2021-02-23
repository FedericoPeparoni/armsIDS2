package ca.ids.abms.plugins.kcaa.erp.modules.transactions;

import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.plugins.common.modules.AbstractPluginTransactionService;
import ca.ids.abms.plugins.kcaa.erp.config.KcaaErpDatabaseConfig;
import ca.ids.abms.plugins.kcaa.erp.modules.chargesadjustment.KcaaErpChargesAdjustmentService;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderMapper;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderService;
import ca.ids.abms.plugins.kcaa.erp.modules.salesheader.SalesHeaderUtility;
import ca.ids.abms.plugins.kcaa.erp.modules.salesline.SalesLineService;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class KcaaErpTransactionService extends AbstractPluginTransactionService {

    private final KcaaErpChargesAdjustmentService kcaaErpChargesAdjustmentService;

    private final SalesHeaderMapper salesHeaderMapper;

    private final SalesHeaderService salesHeaderService;

    private final SalesHeaderUtility salesHeaderUtility;

    private final SalesLineService salesLineService;

    @SuppressWarnings("squid:S00107")
    public KcaaErpTransactionService(
        final KcaaErpChargesAdjustmentService kcaaErpChargesAdjustmentService,
        final SalesHeaderMapper salesHeaderMapper,
        final SalesHeaderService salesHeaderService,
        final SalesHeaderUtility salesHeaderUtility,
        final SalesLineService salesLineService,
        final TransactionPaymentRepository transactionPaymentRepository,
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository
    ) {
        super(transactionPaymentRepository, transactionRepository, transactionTypeRepository);
        this.kcaaErpChargesAdjustmentService = kcaaErpChargesAdjustmentService;
        this.salesHeaderMapper = salesHeaderMapper;
        this.salesHeaderService = salesHeaderService;
        this.salesHeaderUtility = salesHeaderUtility;
        this.salesLineService = salesLineService;
    }

    // region Create Credit Note

    /**
     * Perform necessary external database changes for created credit note transaction.
     *
     * @param transaction credit note transaction to create
     * @return true if exported else false
     */
    @Override
    @Transactional(value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
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
    @Transactional(propagation = Propagation.REQUIRES_NEW, value = KcaaErpDatabaseConfig.TRANSACTION_MANAGER)
    public Boolean createCreditNoteCacheable(final Transaction transaction) {
        return super.createCreditNoteCacheable(transaction);
    }

    // endregion Create Credit Note

    // region Create Header and Entries

    /**
     * Create header from transaction
     *
     * @param transaction transaction for creating header.
     */
    @Override
    protected void createHeaderByCreditNote(final Transaction transaction) {
        // create transaction as a sales header
        salesHeaderService.insert(salesHeaderMapper.toSalesHeader(transaction));
    }

    /**
     * Create sales lines from charges adjustments.
     *
     * @param transaction transaction with associated charges adjustments
     * @param lineNo line number of last item
     */
    @Override
    protected void createEntriesByChargesAdjustments(final Transaction transaction, final Integer[] lineNo) {
        for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            kcaaErpChargesAdjustmentService.create(chargesAdjustment, lineNo[0]);
        }
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
        // create transaction as a sales header statement
        List<PluginSqlStatement> statements = new ArrayList<>();
        statements.add(salesHeaderUtility.insertStatement(salesHeaderMapper.toSalesHeader(transaction)));
        return statements;
    }

    /**
     * Use to get the SQL statements generated when creating charge adjustments in ERP.
     *
     * @param transaction transaction with charge adjustments to create sales lines
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    @Override
    protected List<PluginSqlStatement> createEntriesByChargesAdjustmentsStatements(final Transaction transaction, final Integer[] lineNo) {

        // hold statements in list and return
        List<PluginSqlStatement> statements = new ArrayList<>();

        for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
            lineNo[0] = salesLineService.nextLineNumber(lineNo[0]);
            statements.add(kcaaErpChargesAdjustmentService.createStatement(chargesAdjustment, lineNo[0]));
        }

        return statements;
    }

    // endregion Create Header and Entry Statements
}
