package ca.ids.abms.plugins.common.modules;

import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.util.jdbc.PluginSqlStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class AbstractPluginTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPluginTransactionService.class);

    private final TransactionPaymentRepository transactionPaymentRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;

    public AbstractPluginTransactionService(
        final TransactionPaymentRepository transactionPaymentRepository,
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository
    ) {
        this.transactionPaymentRepository = transactionPaymentRepository;
        this.transactionRepository = transactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
    }

    // region create credit note and statement

    /**
     * Perform necessary external database changes for created credit note transaction.
     *
     * @param transaction credit note transaction to create
     * @return true if exported else false
     */
    @Transactional
    public Boolean createCreditNote(final Transaction transaction) {

        // only handle credit notes as per CDR
        if (!transaction.getTransactionType().isCredit()
            || !TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism()))
            return false;

        LOG.debug("Create transaction credit note {}.", transaction);

        // validate that credit note can be created
        if (!validateCreditNote(transaction)) return false;

        // create transaction as a sales header
        createHeaderByCreditNote(transaction);

        // create each charge adjustment as a sales line
        final Integer[] lineNo = new Integer[] { 0 };
        createEntriesByChargesAdjustments(transaction, lineNo);

        // return true indicating create complete
        return true;
    }

    /**
     * Cacheable wrapper for `createCreditNote` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param transaction credit note transaction to create
     * @return true if exported else false
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean createCreditNoteCacheable(final Transaction transaction) {
        return createCreditNote(transaction);
    }

    /**
     * Use to get the SQL statements generated when creating a transaction in the external system.
     *
     * @param transaction transaction to create entries.
     * @return SQL statements generated
     */
    PluginSqlStatement[] createCreditNoteStatement(final Transaction transaction) {
        List<PluginSqlStatement> statements = new ArrayList<>(createHeaderByCreditNoteStatement(transaction));

        Integer[] lineNo = new Integer[] { 0 };
        statements.addAll(createEntriesByChargesAdjustmentsStatements(transaction, lineNo));

        return statements.toArray(new PluginSqlStatement[0]);
    }

    // endregion: create create note and statement

    // region create credit payment and statement

    /**
     * Perform necessary external database changes for created credit payment transaction.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Transactional
    public Boolean createCreditPayment(final TransactionPayment transactionPayment) {

        LOG.debug("Create transaction credit payment {}.", transactionPayment);

        // validate that credit payment can be created
        if (!validateCreditPayment(transactionPayment)) return false;

        // create transaction payment header
        createHeaderByCreditPayment(transactionPayment);

        // create transaction payment entries
        final Integer[] lineNo = new Integer[] { 0 };
        createEntriesByCreditPayment(transactionPayment, lineNo);

        // return true indicating create complete
        return true;
    }

    /**
     * Cacheable wrapper for `createPayment` method. Will suspend transaction manager and create a new
     * one that will not effect the upstream transaction if exception thrown.
     *
     * @param transactionPayment credit payment transaction to create
     * @return true if exported else false
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Boolean createCreditPaymentCacheable(final TransactionPayment transactionPayment) {
        return createCreditPayment(transactionPayment);
    }

    /**
     * Use to get the SQL statements generated when creating a transaction payment in the external system.
     *
     * @param transactionPayment transaction payment to create entries.
     * @return SQL statements generated
     */
    PluginSqlStatement[] createCreditPaymentStatement(final TransactionPayment transactionPayment) {
        List<PluginSqlStatement> statements = new ArrayList<>(createHeaderByCreditPaymentStatement(transactionPayment));

        final Integer[] lineNo = new Integer[] { 0 };
        statements.addAll(createEntriesByCreditPaymentStatements(transactionPayment, lineNo));

        return statements.toArray(new PluginSqlStatement[0]);
    }

    // endregion create payment note statement

    // region create entries and statements

    /**
     * Create entries from charges adjustments.
     *
     * @param transaction transaction with associated charges adjustments
     * @param lineNo line number of last item
     */
    protected void createEntriesByChargesAdjustments(final Transaction transaction, final Integer[] lineNo) {
        // default implementation ignored
    }

    /**
     * Use to get the SQL statements generated when creating charge adjustments in the external system.
     *
     * @param transaction transaction with charge adjustments to create entries
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected List<PluginSqlStatement> createEntriesByChargesAdjustmentsStatements(
        final Transaction transaction, final Integer[] lineNo
    ) {
        // default implementation ignored
        return Collections.emptyList();
    }

    /**
     * Create entries by transaction credit payments for external database.
     *
     * @param transactionPayment transaction payments for creating entries.
     * @param lineNo line number of last item
     */
    protected void createEntriesByCreditPayment(final TransactionPayment transactionPayment, final Integer[] lineNo) {
        // default implementation ignored
    }

    /**
     * Use to get the SQL statements generataed when creating credit payments in the external system.
     *
     * @param transactionPayment transaction credit payment to create entries
     * @param lineNo line number of last item
     * @return SQL statements generated
     */
    protected List<PluginSqlStatement> createEntriesByCreditPaymentStatements(
        final TransactionPayment transactionPayment, final Integer[] lineNo
    ) {
        // default implementation ignored
        return Collections.emptyList();
    }

    // endregion create entries

    // region create header and statements

    /**
     * Create header by transaction credit note for external database.
     *
     * @param transaction transaction for creating header.
     */
    protected void createHeaderByCreditNote(final Transaction transaction) {
        // default implementation ignored
    }

    /**
     * Create header statement by transaction credit note for external database.
     *
     * @param transaction transaction for creating header statement.
     */
    protected List<PluginSqlStatement> createHeaderByCreditNoteStatement(final Transaction transaction) {
        // default implementation ignored
        return Collections.emptyList();
    }

    /**
     * Create header by transaction credit payment for external database.
     *
     * @param transactionPayment transaction payment for creating header.
     */
    protected void createHeaderByCreditPayment(final TransactionPayment transactionPayment) {
        // default implementation ignored
    }

    /**
     * Create header statement by transaction credit payment for external database.
     *
     * @param transactionPayment transaction payment for creating header statement.
     */
    protected List<PluginSqlStatement> createHeaderByCreditPaymentStatement(final TransactionPayment transactionPayment) {
        // default implementation ignored
        return Collections.emptyList();
    }

    // endregion create header and statements

    // region exported

    /**
     * Set transaction as exported.
     *
     * @param transaction that was exported
     */
    @Transactional
    public void exported(final Transaction transaction) {

        if (transaction == null)
            return;

        Transaction existingTransaction = transactionRepository.findOne(transaction.getId());
        if (existingTransaction == null)
            existingTransaction = transaction;

        existingTransaction.setExported(true);
        transactionRepository.save(existingTransaction);
    }

    /**
     * Set transaction payment as exported.
     *
     * @param transactionPayment that was exported
     */
    @Transactional
    public void exported(final TransactionPayment transactionPayment) {

        if (transactionPayment == null)
            return;

        TransactionPayment existingTransactionPayment = transactionPaymentRepository.findOne(transactionPayment.getId());
        if (existingTransactionPayment == null)
            existingTransactionPayment = transactionPayment;

        existingTransactionPayment.setExported(true);
        transactionPaymentRepository.save(existingTransactionPayment);
    }

    // endregion exported

    // region helper methods

    /**
     * Return the debit transaction for an invoice.
     *
     * @param billingLedger invoice to find debit transaction
     * @return debit transaction if exists
     */
    @Transactional(readOnly = true)
    @SuppressWarnings("WeakerAccess")
    public Transaction getDebitTransactionByInvoice(final BillingLedger billingLedger) {

        // get transaction type of debit
        TransactionType transactionType = transactionTypeRepository.findOneByName("debit");

        // determine transaction payment mechanism
        TransactionPaymentMechanism transactionPaymentMechanism;
        String debitNoteType = InvoiceType.DEBIT_NOTE.toValue();

        if (debitNoteType != null && debitNoteType.equals(billingLedger.getInvoiceType())) {
            transactionPaymentMechanism = TransactionPaymentMechanism.adjustment;
        } else {
            transactionPaymentMechanism = TransactionPaymentMechanism.invoice;
        }

        // returns only one transaction
        return transactionRepository.findOneByTransactionTypeAndPaymentMechanismAndPaymentReferenceNumberAndAccount(
            transactionType, transactionPaymentMechanism, billingLedger.getInvoiceNumber(), billingLedger.getAccount());
    }

    // endregion helper methods

    // region validate credit note

    /**
     * Perform necessary validation for created credit note transaction.
     *
     * @param transaction credit note transaction to create
     */
    public boolean validateCreditNote(final Transaction transaction) {

        // validate transaction as a sales header
        if (!validateHeaderByCreditNote(transaction)) return false;

        // validate each charge adjustment as a sales line
        final Integer[] lineNo = new Integer[] { 0 };
        return validateEntriesByChargesAdjustments(transaction, lineNo);

    }

    /**
     * Perform necessary validation for created credit note transaction header.
     *
     * @param transaction credit note transaction to create header
     */
    protected boolean validateHeaderByCreditNote(final Transaction transaction) {
        // default implementation ignored
        return true;
    }

    /**
     * Perform necessary validation for created credit note transaction entries.
     *
     * @param transaction credit note transaction to create entries
     * @param lineNo line number of last item
     */
    protected boolean validateEntriesByChargesAdjustments(final Transaction transaction, final Integer[] lineNo) {
        // default implementation ignored
        return true;
    }

    // endregion validate credit note

    // region validate credit payment

    /**
     * Perform necessary validation for created credit payment transaction.
     *
     * @param transactionPayment credit payment transaction to create
     */
    public boolean validateCreditPayment(final TransactionPayment transactionPayment) {

        // validate transaction payment header
        if (!validateHeaderByCreditPayment(transactionPayment)) return false;

        // validate transaction payment entries
        final Integer[] lineNo = new Integer[] { 0 };
        return validateEntriesByCreditPayment(transactionPayment, lineNo);
    }

    /**
     * Perform necessary validation for created credit payment transaction header.
     *
     * @param transactionPayment credit payment transaction to create header
     */
    protected boolean validateHeaderByCreditPayment(final TransactionPayment transactionPayment) {
        // default implementation ignored
        return true;
    }

    /**
     * Perform necessary validation for created credit payment transaction entries.
     *
     * @param transactionPayment credit payment transaction to create entries
     * @param lineNo line number of last item
     */
    protected boolean validateEntriesByCreditPayment(final TransactionPayment transactionPayment, final Integer[] lineNo) {
        // default implementation ignored
        return true;
    }

    // endregion validate credit payment
}
