package ca.ids.abms.modules.transactions;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.*;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.*;
import ca.ids.abms.modules.common.dto.MediaDocument;
import ca.ids.abms.modules.flightmovements.FlightMovementRepositoryUtility;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.interestrates.InterestRate;
import ca.ids.abms.modules.interestrates.InterestRateService;
import ca.ids.abms.modules.overdueinvoices.OverdueInvoiceService;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;
import ca.ids.abms.modules.reports2.invoices.interest.InterestInvoiceService;
import ca.ids.abms.modules.reports2.transaction.TransactionReceiptDocumentCreator;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.transactions.enumerate.TransactionDocumentType;
import ca.ids.abms.modules.transactions.error.InterestInvoiceError;
import ca.ids.abms.modules.transactions.utility.TransactionExportFilterSpecification;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustmentService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.common.services.AbstractPluginService;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyExchangeRateService;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.pendingtransactions.PendingChargeAdjustment;
import ca.ids.abms.modules.pendingtransactions.PendingTransaction;
import ca.ids.abms.modules.pendingtransactions.PendingTransactionMapper;
import ca.ids.abms.modules.pendingtransactions.PendingTransactionRepository;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.transaction.TransactionReceiptCreator;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxChargesService;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.Calculation.MathOperator;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.workflows.ApprovalWorkflow;
import ca.ids.abms.modules.workflows.ApprovalWorkflowService;
import ca.ids.abms.util.EnumUtils;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
@Transactional
public class TransactionService extends AbstractPluginService<TransactionServiceProvider> {

    private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

    private static final String ACCOUNT = "account";
    private static final String TRANSACTION_DATE_TIME = "transactionDateTime";
    private static final String CURRENCY = "currency";
    private static final String DEBIT = "debit";
    private static final String CREDIT = "credit";
    private static final String AMOUNT = "amount";
    private static final String BILLING_LEDGER_ID = "billing_ledger_ids";
    private static final String INSTEAD_OF = "instead of";

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;
    private final CurrencyService currencyService;
    private final TransactionPaymentRepository transactionPaymentRepository;
    private final CurrencyUtils currencyUtils;
    private final BillingLedgerFlightUtility billingLedgerFlightUtility;
    private final FlightMovementRepository flightMovementRepository;
    private final CurrencyExchangeRateService currencyExchangeRateService;
    private final SystemConfigurationService systemConfigurationService;
    private final ApprovalWorkflowService approvalWorkflowService;
    private final PendingTransactionMapper pendingTransactionMapper;
    private final PendingTransactionRepository pendingTransactionRepository;
    private final BillingLedgerRepository billingLedgerRepository;
    private final ChargesAdjustmentService chargesAdjustmentService;
    private final ReportHelper reportHelper;
    private final TransactionReceiptDocumentCreator transactionReceiptDocumentCreator;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final BillingLedgerService billingLedgerService;
    private final RoundingUtils roundingUtils;
    private final BankCodeService bankCodeService;
    private final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders;
    private final OverdueInvoiceService overdueInvoiceService;
    private final InterestInvoiceService interestInvoiceService;
    private final InterestRateService interestRateService;
    private final WhitelistingUtils whitelistingUtils;
    private final FlightMovementBuilder flightMovementBuilder;
    private final FlightMovementRepositoryUtility flightMovementRepositoryUtility;
    private final UnifiedTaxChargesService unifiedTaxChargesService;

    @SuppressWarnings("squid:S00107") // Methods should not have too many parameters
    public TransactionService(
        final TransactionRepository transactionRepository,
        final TransactionTypeRepository transactionTypeRepository,
        final AccountRepository accountRepository,
        @Lazy AccountService accountService,
        final CurrencyService currencyService,
        final TransactionPaymentRepository transactionPaymentRepository,
        final CurrencyUtils currencyUtils,
        final BillingLedgerFlightUtility billingLedgerFlightUtility,
        final FlightMovementRepository flightMovementRepository,
        final CurrencyExchangeRateService currencyExchangeRateService,
        final SystemConfigurationService systemConfigurationService,
        final ApprovalWorkflowService approvalWorkflowService,
        final PendingTransactionMapper pendingTransactionMapper,
        final PendingTransactionRepository pendingTransactionRepository,
        final BillingLedgerRepository billingLedgerRepository,
        final ChargesAdjustmentService chargesAdjustmentService,
        final ReportHelper reportHelper,
        final TransactionReceiptDocumentCreator transactionReceiptDocumentCreator,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        @Lazy final BillingLedgerService billingLedgerService,
        final RoundingUtils roundingUtils,
        final BankCodeService bankCodeService,
        final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders,
        final OverdueInvoiceService overdueInvoiceService,
        @Lazy final InterestInvoiceService interestInvoiceService,
        final InterestRateService interestRateService,
        final WhitelistingUtils whitelistingUtils,
        final FlightMovementBuilder flightMovementBuilder,
        final FlightMovementRepositoryUtility flightMovementRepositoryUtility,
        final UnifiedTaxChargesService unifiedTaxChargesService) {

        this.transactionRepository = transactionRepository;
        this.transactionTypeRepository = transactionTypeRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
        this.currencyService = currencyService;
        this.transactionPaymentRepository = transactionPaymentRepository;
        this.currencyUtils = currencyUtils;
        this.billingLedgerFlightUtility = billingLedgerFlightUtility;
        this.flightMovementRepository = flightMovementRepository;
        this.currencyExchangeRateService = currencyExchangeRateService;
        this.systemConfigurationService = systemConfigurationService;
        this.approvalWorkflowService = approvalWorkflowService;
        this.pendingTransactionMapper = pendingTransactionMapper;
        this.pendingTransactionRepository = pendingTransactionRepository;
        this.billingLedgerRepository = billingLedgerRepository;
        this.chargesAdjustmentService = chargesAdjustmentService;
        this.reportHelper = reportHelper;
        this.transactionReceiptDocumentCreator = transactionReceiptDocumentCreator;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.billingLedgerService = billingLedgerService;
        this.roundingUtils = roundingUtils;
        this.bankCodeService = bankCodeService;
        this.aviationInvoiceChargeProviders = aviationInvoiceChargeProviders;
        this.overdueInvoiceService = overdueInvoiceService;
        this.interestInvoiceService = interestInvoiceService;
        this.interestRateService = interestRateService;
        this.whitelistingUtils = whitelistingUtils;
        this.flightMovementBuilder = flightMovementBuilder;
        this.flightMovementRepositoryUtility = flightMovementRepositoryUtility;
        this.unifiedTaxChargesService= unifiedTaxChargesService;
    }

    @Transactional(readOnly = true)
    public List<Transaction> findAll() {
        LOG.debug("Request to get all transactions");
        return transactionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(final Pageable pageable,
                                     final String searchFilter,
                                     final Boolean exportedFilter,
                                     final Integer accountId,
                                     final LocalDate startDate,
                                     final LocalDate endDate) {
        LOG.debug("Request to get transactions");

        // define filter spec with text search
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(searchFilter);

        // filter by account name, null will be skipped
        if (accountId != null)
            filterBuilder.restrictOn(Filter.equals(ACCOUNT, accountId));

        // date filter, null will be skipped
        if (startDate != null && endDate != null) {
            LocalDateTime startAt = startDate.atStartOfDay();
            LocalDateTime endAt = endDate.atTime(LocalTime.MAX);
            filterBuilder.restrictOn(Filter.included(TRANSACTION_DATE_TIME, startAt, endAt));
        }

        return transactionRepository.findAll(new TransactionExportFilterSpecification(
            filterBuilder, exportedFilter, exportSupport(), exportableMechanisms()), pageable);
    }

    @Transactional(readOnly = true)
    public Transaction getOne(Integer id) {
        LOG.debug("Request to get Transaction : {}", id);
        return transactionRepository.getOne(id);
    }

    /**
     * Get transaction media document based on transaction document type.
     */
    MediaDocument getMediaDocument(final Integer id, final TransactionDocumentType documentType) {
        Transaction transaction = transactionRepository.getOne(id);

        if (transaction == null)
            return null;

        MediaDocument document;
        switch (documentType) {
            case APPROVAL:
                document = new MediaDocument(transaction.getApprovalDocumentName(), transaction.getApprovalDocumentType(),
                    transaction.getApprovalDocument());
                break;
            case RECEIPT:
                document = new MediaDocument(transaction.getReceiptDocumentFileName(), transaction.getReceiptDocumentType(),
                    transaction.getReceiptDocument());
                break;
            case SUPPORTING:
                document = new MediaDocument(transaction.getSupportingDocumentName(), transaction.getSupportingDocumentType(),
                    transaction.getSupportingDocument());
                break;
            default:
                document = null;
        }

        return document;
    }

    private void validateTransactionInputData(final Transaction transaction, boolean verifyIfChargesExist) {
        if (transaction.getDescription() == null || transaction.getDescription().isEmpty()) {
            throw new CustomParametrizedException("Description can't be empty", "description");
        }

        TransactionType transactionType = null;
        if (transaction.getTransactionType() != null && (transaction.getTransactionType().getId() != null)) {
            transactionType = transactionTypeRepository.getOne(transaction.getTransactionType().getId());
        }

        if (transactionType == null) {
            throw new CustomParametrizedException("The transaction type provided isn't valid", "transaction_type");
        }

        if (transaction.getAmount() == null || transaction.getAmount() == 0) {
            throw new CustomParametrizedException("Transaction amount can't be 0", AMOUNT);
        }

        if (transactionType.getName().equals(DEBIT) && transaction.getAmount() <= 0) {
            throw new CustomParametrizedException("Transaction amount for debit transaction should be positive value", AMOUNT);
        }

        if (transactionType.getName().equals(CREDIT) && transaction.getAmount() >= 0) {
            throw new CustomParametrizedException("Transaction amount for credit transaction should be negative value", AMOUNT);
        }

        //validate account
        Account accountFromTransaction = null;
        if (transaction.getAccount()!= null && transaction.getAccount().getId() != null) {
            accountFromTransaction = accountRepository.getOne(transaction.getAccount().getId());
        }
        if (accountFromTransaction == null) {
            throw new CustomParametrizedException("Invalid account data", ACCOUNT);
        } else {
            transaction.setAccount(accountFromTransaction);
        }

        //validate transaction currency
        Currency transactionCurrency = getCurrencyEntity(transaction.getCurrency());
        if (transactionCurrency == null) {
            throw new CustomParametrizedException("Invalid transaction currency", CURRENCY);
        }

        //if aviation invoicing is set by FlightmovementCategory, then
        //invoice currency should be USD or be the same as FlightmovementCategory currency
        if (transaction.getFlightmovementCategory() != null && transaction.getFlightmovementCategory().getEnrouteInvoiceCurrency() != null) {
        	Currency flightmovementCategoryCurrency = transaction.getFlightmovementCategory().getEnrouteInvoiceCurrency();

	        if ( !(transactionCurrency.getCurrencyCode().equals("USD") || transactionCurrency.equals(flightmovementCategoryCurrency)) ) {
	            throw new CustomParametrizedException(
	                String.format(
	                    "Transaction currency (%s) should be USD or be the same as currency of the flight movement category (%s)",
	                    transactionCurrency.getCurrencyCode(),flightmovementCategoryCurrency.getCurrencyCode()), CURRENCY);
	        }

        }

        //validate payment mechanism and payment reference number
        //for credit type: ‘cash|credit|debit|cheque|wire|adjustment’
        if (transactionType.getName().equals(CREDIT)
            && (!(transaction.getPaymentMechanism() == TransactionPaymentMechanism.cash
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.credit
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.debit
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.cheque
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.wire
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.adjustment))) {
                throw new CustomParametrizedException("Invalid payment mechanism value for credit transaction", "payment_mechanism");
        }

        // for debit type: ‘invoice|adjustment’
        if (transactionType.getName().equals(DEBIT) && !(transaction.getPaymentMechanism() == TransactionPaymentMechanism.invoice
            || transaction.getPaymentMechanism() == TransactionPaymentMechanism.adjustment)) {
                throw new CustomParametrizedException("Invalid payment mechanism value for debit transaction", "payment_mechanism");
        }

        //validate payment reference number
        //Payment reference number
        //for mechanism credit, debit, check, wire: reference number,
        //for mechanism invoice: invoice number,
        //for mechanism adjustment: N/A,
        //for mechanism cash: N/A
        if (transaction.getPaymentReferenceNumber()==null) {
            throw new CustomParametrizedException("Payment reference number is required", "payment_reference_number");
        }

        switch(transaction.getPaymentMechanism()) {
        case adjustment:
            if (verifyIfChargesExist) {
                if (transaction.getId() == null && transaction.getChargesAdjustment() == null) {
                    throw new CustomParametrizedException("The credit/debit note cannot be created because the adjustments are missing",
                    "charges_adjustment");
                }
                double chargesAmount = 0.0;
                if (transaction.getChargesAdjustment() != null) {
                    for (final ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
                        if (chargesAdjustment == null) continue;
                        chargesAmount = Calculation.operation(chargesAmount, chargesAdjustment.getChargeAmount(),
                            MathOperator.ADD, transactionCurrency);
                    }
                }
                if (!transaction.getAmount().equals(chargesAmount)) {
                    throw new CustomParametrizedException("The amount doesn't  match the total of charges submitted",
                        AMOUNT, "charges_adjustment");
                }
            }
            if (transactionType.getName().equals(CREDIT) && CollectionUtils.isEmpty(transaction.getBillingLedgerIds())) {
                throw new CustomParametrizedException("Cannot create a credit note because the referred invoice is missing");
            }
            break;
        case cash:
            if (!transaction.getPaymentReferenceNumber().equalsIgnoreCase("N/A")) {
                throw new CustomParametrizedException("Payment reference number should be N/A", "payment_reference_number");
            }
            break;
        default:
            break;
        }

        //verify billingLedger IDs in the list are valid
        if (transaction.getBillingLedgerIds() != null && !transaction.getBillingLedgerIds().isEmpty()) {
            if (!transactionType.getName().equals(CREDIT) && !TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism())) {
                throw new CustomParametrizedException("Billing Ledger Ids can be only provided for credit transaction", BILLING_LEDGER_ID);
            }
            validateBillingLedgerIds(transaction);
        }
    }

    /**
     * Get currency from provided transaction if currency found in repository.
     *
     * @deprecated Use method getCurrencyEntity instead.
     *
     * @param transaction Transaction value to use
     * @return Currency entity if found in repository
     */
    @Deprecated
    private Currency getCurrencyEntityFromTransaction(final Transaction transaction) {
        if (transaction != null)
            return getCurrencyEntity(transaction.getCurrency());
        else
            return null;
    }

    private Currency getCurrencyEntity(final Currency currency) {
        if (currency != null && currency.getId() != null)
            return currencyService.getOne(currency.getId());
        else
            return null;
    }

    /**
     * Validate billingLedger IDs
     */
    private void validateBillingLedgerIds(final Transaction transaction) {

        List<Integer> billingLedgerIds = transaction.getBillingLedgerIds();

        //validate duplicate IDs
        HashSet<Integer> billingLedgerIdsHashSet = new HashSet<>();
        if (billingLedgerIds !=null && !billingLedgerIds.isEmpty()) {
            billingLedgerIdsHashSet.addAll(billingLedgerIds);
            if (billingLedgerIds.size() > billingLedgerIdsHashSet.size()) {
                throw new CustomParametrizedException("Billing Ledger list can't contain duplicate IDs", BILLING_LEDGER_ID);
            }
        }

        //validate ID
        //make sure BillingLedger currency match Transaction currency
        if (CollectionUtils.isNotEmpty(billingLedgerIds)) {
            final Integer invoiceToAdjust =  billingLedgerIds.get(0);
            double totalAmountForAllBillingLedgers = 0.00;
            for (Integer billingLedgerId : billingLedgerIds) {
                BillingLedger billingLedgerFromDb = billingLedgerRepository.findOne(billingLedgerId);
                if (billingLedgerFromDb == null) {
                    LOG.error("Cannot find the invoice with ID {} related to the transaction with ID {}, Receipt no. {}, Description: {}",
                        billingLedgerId, transaction.getId(), transaction.getReceiptNumber(), transaction.getDescription());
                    String errorMsg = String.format("The related invoice (id %d) to this transaction cannot be found. " +
                        "Void the transaction and insert a new one", billingLedgerId);
                    throw new CustomParametrizedException(errorMsg);
                }

                // validate billing ledger adjustment's billing center
                if (billingLedgerId.equals(invoiceToAdjust) && TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism()))
                    ensureBillingCenterValid(billingLedgerFromDb, reportHelper.getBillingCenterOfCurrentUser());

                //validate currency
                Currency transactionCurrency = getCurrencyEntity(transaction.getCurrency());

                if (!billingLedgerFromDb.getInvoiceCurrency().equals(transactionCurrency)) {
                    String transactionCurrencyCode = transactionCurrency != null ? transactionCurrency.getCurrencyCode() : "";
                    throw new CustomParametrizedException(
                        String.format(
                            "The currency (%s) of the Billing Ledger (id %d) " +
                            "is different from the currency of the transaction (%s)",
                            billingLedgerFromDb.getInvoiceCurrency().getCurrencyCode(), billingLedgerId, transactionCurrencyCode),
                            BILLING_LEDGER_ID);
                }
                //validate InvoiceStateType
                // only invoices with state "published" can be paid. But if the invoice is referred by a credit note, the flow must continue
                if (!billingLedgerFromDb.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
                    if (billingLedgerId.equals(invoiceToAdjust)
                        && TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism())) {
                        continue;
                    } else {
                        ErrorVariables errorVariables = new ErrorVariables();
                        errorVariables.addEntry("number", billingLedgerFromDb.getInvoiceNumber());
                        errorVariables.addEntry("state", Translation.getLangByToken(billingLedgerFromDb.getInvoiceStateType()));
                        throw new ErrorDTO.Builder()
                            .setErrorMessage("Invoice '{{number}}' cannot be paid with a state type of '{{state}}'.")
                            .setErrorMessageVariables(errorVariables)
                            .buildFailedPersistenceException();
                    }
                }
                totalAmountForAllBillingLedgers = Calculation.operation(Math.abs(totalAmountForAllBillingLedgers), Math.abs(billingLedgerFromDb.getAmountOwing()),
                MathOperator.ADD, transactionCurrency);

                // validate backdate payment
                if (transaction.getPaymentDate() != null && systemConfigurationService.getBoolean(SystemConfigurationItemName.BACKDATE_PAYMENT_ALLOWED)) {

                    // payment date should not be before invoice period
                    if (billingLedgerFromDb.getInvoicePeriodOrDate() != null &&
                        billingLedgerFromDb.getInvoicePeriodOrDate().isAfter(transaction.getPaymentDate())) {
                        throw new CustomParametrizedException(ErrorConstants.ERR_INVOICE_PAYMENT_DATE_BEFORE_ISSUE_DATE);
                    }

                    // payment date should NOT be after current date time
                    if (transaction.getPaymentDate().isAfter(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime())) {
                        throw new CustomParametrizedException(ErrorConstants.ERR_INVOICE_PAYMENT_DATE_IN_FUTURE);
                    }
                }
            }
        }
    }

    /**
     * This method is used by the web service
     *
     *  NOTE: transaction payments only created for credit transaction
     */
    HashMap<Integer,Double> calculateAmountForTransactionPayments(final Transaction transaction) {

        //BillingLedgerIds are validated in validateTransactionInputData method
        validateTransactionInputData(transaction, false);

        return doCalculateAmountForTransactionPayments(transaction);
    }

    long countAllTransactions() {
        return transactionRepository.count();
    }

    /**
     * This helper method calculates amount for each invoice based on total amount provided in Transaction
     *
     *  NOTE: transaction payments only created for credit transaction
     *  (!) Before calling this method make sure that transaction data has been validated by validateTransactionInputData method
     *
     */
    private HashMap<Integer,Double> doCalculateAmountForTransactionPayments(final Transaction transaction) {

        HashMap<Integer,Double> result = new HashMap<>();

        if (!transaction.getTransactionType().getName().equals(CREDIT) ||
            CollectionUtils.isEmpty(transaction.getBillingLedgerIds())) {
            return result;
        }

        final List<Integer> billingLedgerIds = transaction.getBillingLedgerIds();
        final double totalAbsAmountFromTransaction = Math.abs(transaction.getAmount()); //make amount positive just for simplicity of calculations
        final Currency currency = currencyService.getOne(transaction.getCurrency().getId());

        if (totalAbsAmountFromTransaction <= 0.0d) {
            return result;
        }

        double totalAmountLeft = totalAbsAmountFromTransaction;

        for (Integer billingLedgerId : billingLedgerIds) {
            BillingLedger billingLedgerFromDb = billingLedgerRepository.findOne(billingLedgerId);
            double invoiceAmountOwing = Math.abs(billingLedgerFromDb.getAmountOwing()); // just in case make sure that amount positive for simplicity of calculations
            if (invoiceAmountOwing > 0.0d) {
                //if the amount > 0, then we can do a payment
                if (totalAmountLeft > 0.0d) {
                    double billingLedgerPayment;

                    if (totalAmountLeft >= invoiceAmountOwing) {
                        billingLedgerPayment = invoiceAmountOwing;

                        //totalAmountLeft - billingLedgerPayment
                        totalAmountLeft = Calculation.operation(totalAmountLeft, billingLedgerPayment, MathOperator.SUBTRACT, currency);
                        //totalAmountLeft = totalAmountLeft - billingLedgerPayment;

                    } else {
                        // use all total amount left to do a partial payment of the invoice
                        billingLedgerPayment = totalAmountLeft;
                        totalAmountLeft = 0.00;
                    }

                    billingLedgerPayment = billingLedgerPayment * -1; //convert back to negative because payment amount for credit transaction should be negative value
                    result.put(billingLedgerId, billingLedgerPayment);

                } else {
                    String errorMsg = String.format(
                        "The amount left" +
                        " %,.2f " +
                        "is not enough for payment of the invoice" +
                        " # %d (%,.2f)",
                        totalAmountLeft, billingLedgerId, invoiceAmountOwing);
                    throw new CustomParametrizedException(errorMsg, AMOUNT);
                }
            }
        }
        return result;
    }

    /**
     * Create transaction payments by credit transaction
     *
     * @param transaction credit transaction
     * @param amountForTransactionPayments payment amount
     */
    private List<TransactionPayment> doCreateTransactionPaymentsByTransaction(final Transaction transaction,
                                                                              final HashMap<Integer,Double> amountForTransactionPayments, boolean isCreditNote) {
        assert (transaction != null);
        final List<Integer> billingLedgerIds = transaction.getBillingLedgerIds();

        // result list of transaction payments created
        List<TransactionPayment> transactionPayments = new ArrayList<>();

        //if billingLedger Ids are provided, we need to create transaction payments
        if (transaction.getId()!=null && CollectionUtils.isNotEmpty(billingLedgerIds)) {
            for (final Integer billingLedgerId : billingLedgerIds) {

                final BillingLedger billingLedgerFromDb = billingLedgerRepository.findOne(billingLedgerId);

                if (billingLedgerFromDb != null && billingLedgerFromDb.getAmountOwing() > 0.0d &&
                    InvoiceStateType.PUBLISHED.toValue().equals(billingLedgerFromDb.getInvoiceStateType())) {
                    Double amountForTransactionPayment = amountForTransactionPayments.get(billingLedgerFromDb.getId());
                    if (amountForTransactionPayment == null || Math.abs(amountForTransactionPayment) <= 0) {
                        String errorMsg = String.format("Can't create transaction payment for invoice # %d", billingLedgerFromDb.getId());
                        throw new CustomParametrizedException(errorMsg, BILLING_LEDGER_ID);
                    }
                    LOG.debug("The transaction {} of type {} will be used to pay the amount of {} {} for the invoice {}",
                        transaction.getId(), transaction.getTransactionType(), amountForTransactionPayment,
                        billingLedgerFromDb.getInvoiceCurrency().getCurrencyCode(), billingLedgerFromDb.getInvoiceNumber());

                    // create transaction payment and add to list
                    transactionPayments.add(doCreateTransactionPaymentForBillingLedger(amountForTransactionPayment,
                        billingLedgerFromDb, transaction,false, isCreditNote));
                }
            }
        }

        // return result of transaction payments created
        return transactionPayments;
    }

    private TransactionPayment doCreateTransactionPaymentForBillingLedger(final Double amountForTransactionPayment,
                                                                          final BillingLedger providedBillingLedger,
                                                                          final Transaction providedTransaction,
                                                                          final Boolean isAccountCredit, boolean isCreditNote) {
        return doCreateTransactionPaymentForBillingLedger(amountForTransactionPayment, providedBillingLedger, providedTransaction, isAccountCredit, false, isCreditNote);
    }

    /**
     * Create TransactionPayment record in DB and update amountOwing of corresponding BillingLedger
     *
     * @param amountForTransactionPayment payment amount
     * @param providedBillingLedger billing ledger
     * @param providedTransaction transaction
     * @param isAccountCredit boolean if account credit
     */
    private TransactionPayment doCreateTransactionPaymentForBillingLedger(final Double amountForTransactionPayment,
                                                                          final BillingLedger providedBillingLedger,
                                                                          final Transaction providedTransaction,
                                                                          final Boolean isAccountCredit,
                                                                          final boolean preview, boolean isCreditNote) {

        TransactionPayment transactionPayment = new TransactionPayment();
        transactionPayment.setTransaction(providedTransaction);
        transactionPayment.setBillingLedger(providedBillingLedger);
        transactionPayment.setAmount(amountForTransactionPayment);
        transactionPayment.setCurrency(providedTransaction.getCurrency());
        transactionPayment.setExchangeRate(providedTransaction.getExchangeRate());
        transactionPayment.setTargetCurrency(providedTransaction.getTargetCurrency());
        transactionPayment.setExchangeRateToAnsp(providedTransaction.getExchangeRateToAnsp());
        transactionPayment.setIsAccountCredit(isAccountCredit);
        transactionPayment.setKraClerkName(providedTransaction.getKraClerkName());
        transactionPayment.setKraReceiptNumber(providedTransaction.getKraReceiptNumber());

        if (!preview) {
            transactionPaymentRepository.save(transactionPayment);
        }

        //update BillingLedger
        doUpdateBillingLedgerByTransactionPayment(transactionPayment, preview, isCreditNote);

        return transactionPayment;
    }

    /**
     * When TransactionPayment is created an invoice needs to be updated
     */
    private void doUpdateBillingLedgerByTransactionPayment(final TransactionPayment transactionPayment, final boolean preview, boolean isCreditNote) {
        BillingLedger billingLedgerFromDb;

        if (preview) {
            billingLedgerFromDb = transactionPayment.getBillingLedger();
        } else {
            Integer billingLedgerId = transactionPayment.getBillingLedger().getId();
            billingLedgerFromDb = billingLedgerRepository.findOne(billingLedgerId);
        }

        Currency currencyFromDb = currencyService.findOne(billingLedgerFromDb.getInvoiceCurrency().getId());

        Double updatedAmountOwing = Calculation.operation(Math.abs(billingLedgerFromDb.getAmountOwing()),
            Math.abs(transactionPayment.getAmount()), MathOperator.SUBTRACT, currencyFromDb);

        if (updatedAmountOwing < 0) {
            throw new CustomParametrizedException(
                String.format(
                    "Can't create transaction payment for invoice" +
                    " # %d " +
                    "because amount of payment can't be greater than owing amount",
                    billingLedgerFromDb.getId()), BILLING_LEDGER_ID);
        }

        if (updatedAmountOwing == 0.00) {
            //set paid
            billingLedgerFromDb.setInvoiceStateType(InvoiceStateType.PAID.toValue());

            setBillingLedgerFinalPaymentDate(billingLedgerFromDb, transactionPayment);
            //se isCreditNote è true evitare di generare fatture con interessi
            if(!isCreditNote) {
                checkAndCreateInterestInvoice(billingLedgerFromDb);
            }
        }

        billingLedgerFromDb.setAmountOwing(updatedAmountOwing);

        // set kraClerkName and kraReceiptNumber
        billingLedgerFromDb.setClerkName(transactionPayment.getKraClerkName());
        billingLedgerFromDb.setReceiptNumber(transactionPayment.getKraReceiptNumber());

        if (!preview) {
            // save billing ledger record
            final BillingLedger savedBillingLedger = billingLedgerRepository.save(billingLedgerFromDb);

            // find flights connected to this billing ledger
            final List <FlightMovement> flights = this.flightMovementRepository.findByBillingLedger (savedBillingLedger.getId());

            // Update status of each flight
            if (flights != null) {
                flights.forEach(fm->this.billingLedgerFlightUtility.updateFlightStatusToMatchInvoice (savedBillingLedger, fm,
                    systemConfigurationService.shouldDisplayPaxCharges()));
            }
        }
    }

    private void setBillingLedgerFinalPaymentDate(final BillingLedger billingLedgerFromDb, final TransactionPayment transactionPayment) {
        //if backdate payment is allowed and there is a date from transaction, set this date as final payment date
        if (systemConfigurationService.getOneByItemName(SystemConfigurationItemName.BACKDATE_PAYMENT_ALLOWED) != null &&
            systemConfigurationService.getOneByItemName(SystemConfigurationItemName.BACKDATE_PAYMENT_ALLOWED).getCurrentValue() != null &&
            systemConfigurationService.getOneByItemName(SystemConfigurationItemName.BACKDATE_PAYMENT_ALLOWED).getCurrentValue().
                equalsIgnoreCase(SystemConfigurationItemName.SYSTEM_CONFIG_TRUE)){

            if (transactionPayment.getTransaction().getPaymentDate() != null) {
                billingLedgerFromDb.setFinalPaymentDate(transactionPayment.getTransaction().getPaymentDate());
            } else {
                billingLedgerFromDb.setFinalPaymentDate(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
            }
        } else {
            billingLedgerFromDb.setFinalPaymentDate(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        }

    }

    private void checkAndCreateInterestInvoice(BillingLedger billingLedger) {

        LocalDate finalPaymentDate = billingLedger.getFinalPaymentDate().toLocalDate();
        LocalDate paymentDueDate = billingLedger.getPaymentDueDate().toLocalDate();

        if (finalPaymentDate.isBefore(paymentDueDate) || finalPaymentDate.isEqual(paymentDueDate)) {
            return;
        }

        // if the system configuration "Apply interest penalty on" is set to "Invoice final payment", an interest invoice should be created
        boolean createInterestInvoice = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON)
            .equalsIgnoreCase("Invoice final payment");

        if (!createInterestInvoice) {
            return;
        }

        InvoiceOverduePenalty overduePenalty = overdueInvoiceService.createNewPenalty(billingLedger, finalPaymentDate);

        if (overduePenalty != null && (overduePenalty.getDefaultPenaltyAmount() + overduePenalty.getPunitivePenaltyAmount() > 0)) {
            double defaultPenalty = overduePenalty.getDefaultPenaltyAmount();
            double punitivePenalty = overduePenalty.getPunitivePenaltyAmount();

            // define exchange rate date based on payment date
            LocalDateTime exchangeRateDate = billingLedger.getFinalPaymentDate();
            // EANA expects to use the previous day for exchange rates
            if (BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode())
                exchangeRateDate = exchangeRateDate.minusDays(1);

            long overdueDays = DAYS.between(paymentDueDate, finalPaymentDate);

            interestInvoiceService.createInterestInvoice(billingLedger.getAccount().getId(), defaultPenalty + punitivePenalty,
                defaultPenalty, punitivePenalty, exchangeRateDate, overdueDays, billingLedger.getInvoiceAmount(), billingLedger.getInvoiceNumber());
        }

    }

    private void setExchangeRatesAndAmount(final Transaction transaction) {
        final Currency transactionCurrency = getCurrencyEntity(transaction.getCurrency());
        if (transactionCurrency == null) {
            throw new ErrorDTO.Builder("Currency not found for transaction ")
                .setErrorMessage(transaction.getDescription())
                .buildInvalidDataException();
        }
        transaction.setCurrency(transactionCurrency);
        transaction.setAmount(Calculation.truncate(transaction.getAmount(), transactionCurrency.getDecimalPlaces()));
        final Currency anspCurrency = currencyUtils.getAnspCurrency();
        final Currency usdCurrency = currencyUtils.getCurrencyUSD();

        //default value for target value is USD currency
        if (transaction.getTargetCurrency() == null) {
            transaction.setTargetCurrency(usdCurrency);
        }

        // set exchange rates for transaction
        if (transaction.getExchangeRate() == null || transaction.getExchangeRateToAnsp() == null) {

            // define exchange rate date based on payment date OR transaction date time if undefined
            LocalDateTime exchangeRateDate = resolveExchangeRateDate(transaction);

            // set exchange rate based on exchange rate date if transaction exchange rate is null
            if (transaction.getExchangeRate() == null) {
                transaction.setExchangeRate(currencyUtils.getExchangeRate(transactionCurrency,
                    transaction.getTargetCurrency(), exchangeRateDate));
            }

            // set ANSP exchange rate based on exchange rate date if transaction ANSP exchange rate is null
            if (transaction.getExchangeRateToAnsp() == null) {
                transaction.setExchangeRateToAnsp(currencyUtils.getExchangeRate(transactionCurrency, anspCurrency,
                    exchangeRateDate));
            }
        }

        // if transaction exchange rate is still null, throw appropriate exception
        if (transaction.getExchangeRate() == null) {
            throw ExceptionFactory.getInvalidCurrencyRateException(Transaction.class, "exchangeRate");
        }

        // set payment values if empty
        if (transaction.getPaymentAmount() == null || transaction.getPaymentCurrency() == null
            || transaction.getPaymentExchangeRate() == null) {
            transaction.setPaymentAmount(transaction.getAmount());
            transaction.setPaymentCurrency(transactionCurrency);
            transaction.setPaymentExchangeRate(1d);
        } else {
            // set payment amount by currency
            final Currency transactionPaymentCurrency = getCurrencyEntity(transaction.getPaymentCurrency());
            transaction.setPaymentCurrency(transactionPaymentCurrency);
            final double paymentAmount;
            if (transactionPaymentCurrency != null) {
                paymentAmount = Calculation.truncate(transaction.getPaymentAmount(), transactionPaymentCurrency.getDecimalPlaces());
            } else {
                paymentAmount = Calculation.truncate(transaction.getPaymentAmount(), 2);
            }
            transaction.setPaymentAmount(paymentAmount);
        }
    }

    private Transaction doCreateTransaction(final Transaction transaction) {
        return doCreateTransaction(transaction, null, false);
    }

    /**
     * This is a helper method to create/preview transaction record in DB. To create a transaction,
     * calculating the right current transaction balance.
     *
     * @param transaction provided transaction
     * @param invoiceToPay invoice transaction pays, should be null if not a payment transaction
     * @param preview boolean if preview invoice
     *
     * @return the entity just saved
     */
    private Transaction doCreateTransaction(final Transaction transaction,
                                            final BillingLedger invoiceToPay,
                                            final boolean preview) {
        LOG.debug("Request to save Transaction: {}", transaction.getDescription());

        this.setExchangeRatesAndAmount(transaction);

        if (transaction.getTransactionDateTime() == null) {
            transaction.setTransactionDateTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());
        }

        double lastBalance = calculateTransactionBalance(transaction);

        /* In case of debit transaction, let's try to pay the invoice, if provided */
        if (invoiceToPay != null && DEBIT.equals(transaction.getTransactionType().getName())) {
            /*
             * Apply account credit if applicable.
             * Account credit can be applied only when a new invoice and debit transaction is created.
             */
            applyAccountCreditAutomaticallyToPayInvoice(invoiceToPay, lastBalance, preview);
        }

        if(!preview) {
        	final String receiptSeqNumberType = invoiceSequenceNumberHelper.generator().getReceiptSequenceNumberType(transaction.getPaymentMechanism());
        	transaction.setReceiptSequenceNumberType(receiptSeqNumberType);
            return save(transaction);
        } else {
            return transaction;
        }
    }

    private boolean canBeManuallyManaged (final Transaction transaction) {
        return systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_CREDIT_TRANSACTION_MANUAL_APPROVAL, false)
            && transaction.getPaymentMechanism() == TransactionPaymentMechanism.adjustment
            && transaction.getTransactionType().getName().equals(CREDIT);
    }

    private PendingTransaction createPendingTransaction (final Transaction transaction) {
        assert (transaction != null);

        final ApprovalWorkflow initialStatus = approvalWorkflowService.getInitialStatus();

        if (initialStatus == null) {
            throw new ErrorDTO.Builder()
                .setErrorMessage("Cannot process the transaction because the workflow is not configured properly")
                .appendDetails("The approval workflow is enabled but not configured")
                .buildInvalidDataException();
        }
        LOG.debug("The transaction {}/{} will be marked as a pending transaction to be processed by the approval workflow",
            transaction.getId(), transaction.getDescription());

        this.setExchangeRatesAndAmount(transaction);

        final PendingTransaction pendingTransaction = pendingTransactionMapper.transactionToPendingTransaction(transaction);

        if (transaction.getChargesAdjustment() != null) {
            final List<PendingChargeAdjustment> adjustments = new ArrayList<>(transaction.getChargesAdjustment().size());
            for (final ChargesAdjustment adjustment : transaction.getChargesAdjustment()) {
                final PendingChargeAdjustment pendingAdjustment = pendingTransactionMapper.chargesToPendingCharges(adjustment);
                pendingAdjustment.setPendingTransaction(pendingTransaction);
                pendingAdjustment.setInvoiceType(transaction.getTransactionType().getName());
                adjustments.add(pendingAdjustment);
            }
            pendingTransaction.setPendingChargeAdjustments(adjustments);
        }

        pendingTransaction.setCurrentApprovalLevel(initialStatus);

        if (transaction.getBillingLedgerIds() != null) {
            final String csvIDs = StringUtils.join(transaction.getBillingLedgerIds().toArray(), ',');
            pendingTransaction.setRelatedInvoices(csvIDs);
        }

        return pendingTransactionRepository.save(pendingTransaction);
    }

    private void validateTheInvoiceForDebitTransaction (final BillingLedger billingLedger) {
        assert (billingLedger != null && billingLedger.getId() != null);

        if (billingLedger.getInvoiceStateType() == null
            || billingLedger.getAccount() == null
            || billingLedger.getAccount().getId() == null
            || billingLedger.getInvoiceCurrency() == null
            || billingLedger.getInvoiceCurrency().getId() == null) {
            throw new ErrorDTO.Builder(String.format("Invoice #%d with invalid data",billingLedger.getId()))
                .buildInvalidDataException();
        }

        //invoice state must be "published"
        if (!billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
            throw new ErrorDTO.Builder(String.format("Invoice #%d not in the PUBLISHED state",billingLedger.getId()))
                .buildInvalidDataException();
        }

        //invoice amount_owing should be greater than 0
        if (billingLedger.getAmountOwing() == null || billingLedger.getAmountOwing() <= 0) {
            throw new ErrorDTO.Builder(String.format("Invoice #%d doesn't have amount owing but it isn't in the PAID state.",billingLedger.getId()))
                .buildInvalidDataException();
        }
    }

    /**
     * Account credit (if it exists) should be automatically applied to pay an invoice.
     * As a result of payment amount_owing should be reduced by the credit amount applied.
     *
     * A list of credit transactions that have any unused account credit is determined.
     * After that single or multiple transaction payments are created.
     *
     * @param billingLedger new Billing Leger
     * @param lastBalance transaction balance
     * @param preview preview of the invoice
     */
    private void applyAccountCreditAutomaticallyToPayInvoice(final BillingLedger billingLedger,
                                                             final Double lastBalance,
                                                             final boolean preview) {
        assert (billingLedger != null && lastBalance != null);

    	//check if any unpaid invoices exist for this account and invoice currency
		List<Integer> allUnpaidInvoiceIds = billingLedgerRepository.findUnpaidInvoiceIds(billingLedger.getAccount().getId(), billingLedger.getInvoiceCurrency().getId());
        HashSet<Integer> unpaidFilteredList = new HashSet<>();
        if (allUnpaidInvoiceIds != null && !allUnpaidInvoiceIds.isEmpty()) {
        	unpaidFilteredList.addAll(allUnpaidInvoiceIds);
        }
        unpaidFilteredList.remove(billingLedger.getId()); //remove current invoice id. It should be unpaid at this moment.

        /* Check if there are invoices not paid yet before to pay automatically the current */
		if (!unpaidFilteredList.isEmpty()) {
			final String allUnpaidInvoiceIdsStr = do_formatInvoiceIdList (unpaidFilteredList);
            LOG.warn("Account credit can't be applied. The following unpaid invoices need to be paid: {}", allUnpaidInvoiceIdsStr);
			return;
		}

        final String currencyCode = billingLedger.getInvoiceCurrency().getCurrencyCode();
        if (lastBalance < 0.0d) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("The last balance of {} {} can be used to pay the invoice for the account {}",
                    lastBalance, currencyCode, billingLedger.getAccount().getName());
            }

            //find a list of all previous credit transactions that has unused account credit
            final List<Transaction> transactionsWithUnusedAccountCredit = transactionRepository
                .findCreditTransactionsWithUnusedAccountCredit(billingLedger.getAccount().getId(), billingLedger.getInvoiceCurrency().getId());
            final StringBuilder transactionsChecked = new StringBuilder();

            if (transactionsWithUnusedAccountCredit != null && !transactionsWithUnusedAccountCredit.isEmpty()) {
                double totalUnusedCredit = 0.0d;
                final HashMap<Integer, Double> availableAccountCreditAmounts = new HashMap<>();
                for (Transaction creditTransactionWithUnusedAccountCredit : transactionsWithUnusedAccountCredit) {
                    Double unusedCreditPerTransaction = getUnusedAccountCreditAmountForCreditTransaction(creditTransactionWithUnusedAccountCredit);
                    if (unusedCreditPerTransaction != null) {
                        availableAccountCreditAmounts.put(creditTransactionWithUnusedAccountCredit.getId(), unusedCreditPerTransaction);
                        totalUnusedCredit += unusedCreditPerTransaction;
                    }
                    transactionsChecked.append(creditTransactionWithUnusedAccountCredit.getId()).append(' ');
                }
                if (lastBalance == totalUnusedCredit) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("The following credit transactions have enough unused credit of {} {} to pay (partially or not) " +
                                "the invoice {} with amount {} {} for the account {}: {}",
                            totalUnusedCredit, currencyCode, billingLedger.getInvoiceNumber(),
                            billingLedger.getAmountOwing(), currencyCode, billingLedger.getAccount().getName(),
                            transactionsChecked);
                    }
                    double accountCreditApplied = 0.0d;
                    for (Transaction creditTransactionWithUnusedAccountCredit : transactionsWithUnusedAccountCredit) {

                        //we need to retrieve up to date invoice from DB each time
                        // after we create a transaction payment for this invoice (if preview there no billing ledger id)
                        BillingLedger mostRecentBillingLedgerFromDb;
                        if (billingLedger.getId() != null) {
                            mostRecentBillingLedgerFromDb = billingLedgerRepository.findOne(billingLedger.getId());
                        } else {
                            mostRecentBillingLedgerFromDb = billingLedger;
                        }

                        Double upToDateAmountOwing = mostRecentBillingLedgerFromDb.getAmountOwing();

                        if (Math.abs(upToDateAmountOwing) > 0 && mostRecentBillingLedgerFromDb.getInvoiceStateType().
                            equals(InvoiceStateType.PUBLISHED.toValue())) {
                            //determine amount of available account credit from credit transaction

                            Double availableAccountCreditAmount = availableAccountCreditAmounts.get(creditTransactionWithUnusedAccountCredit.getId());

                            //determine amount for transaction payment
                            Double amountForTransactionPayment;

                            //CASE 1
                            if (Math.abs(availableAccountCreditAmount) > Math.abs(upToDateAmountOwing)) {

                                amountForTransactionPayment = Math.abs(upToDateAmountOwing) * -1; //make sure we have negative value
                            } else {
                                //CASE 2
                                amountForTransactionPayment = availableAccountCreditAmount; //it should be already a negative value
                            }
                            accountCreditApplied += amountForTransactionPayment;

                            //Used for account credit when a billing ledger is created but not saved during invoice preview
                            billingLedger.setAccountCredit(accountCreditApplied);

                            //Create transaction payment based on account credit amount. amountOwing of the invoice will be reduced after that.
                            TransactionPayment transactionPayment = doCreateTransactionPaymentForBillingLedger(
                                amountForTransactionPayment, mostRecentBillingLedgerFromDb,
                                creditTransactionWithUnusedAccountCredit, true, preview, false);

                            // broadcast credit payment created event
                            if (!preview) {
                                creditPaymentCreated(transactionPayment);
                            }

                        } else {
                            //we no longer need to apply existing account credits to this invoice if amountOwing became 0 and becomes "paid"
                            //in this case we use state!=published, because it's more safe and to make sure we can only create transaction payments for "published" invoices
                            break;
                        }
                    }
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("The credit applied of {} {} on a total credit of {} {} has been used to pay the invoice {} with " +
                                "amount {} {} for the account {}",
                            accountCreditApplied, currencyCode, totalUnusedCredit, currencyCode, billingLedger.getInvoiceNumber(),
                            billingLedger.getInvoiceAmount(), currencyCode, billingLedger.getAccount().getName());
                    }
                } else {
                    /* The account credit available is not congruent to the current balance. The invoice cannot be paid automatically. */
                    LOG.error("Cannot try to pay the invoice {} because the unused credit of {} {} doesn't match the last balance " +
                            "of {} {} for the account {} with ID {}. Please review the credit transactions: {}",
                        billingLedger.getInvoiceNumber(), totalUnusedCredit, currencyCode, lastBalance, currencyCode,
                        billingLedger.getAccount().getName(), billingLedger.getAccount().getId(), transactionsChecked);
                }
            } else {
                /* The balance is in credit but the account credits hasn't been found in the system. The invoice cannot be paid automatically. */
                LOG.error("Cannot try to pay the invoice {} because, even if the last balance is {} {}, the unused account credits " +
                        "hasn't been found in the system for the account {}",
                    billingLedger.getInvoiceNumber(), lastBalance, billingLedger.getInvoiceCurrency().getCurrencyCode(),
                    billingLedger.getAccount().getName());
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("The last balance is {} {}, so the account doesn't have credit to pay automatically the invoice {} for the account {}",
                lastBalance, billingLedger.getInvoiceCurrency().getCurrencyCode(), billingLedger.getInvoiceNumber(), billingLedger.getAccount().getName());
        }
    }

    /**
     * Calculate unused account credit amount for credit transactions as:
     * account credit amount = transaction amount - total transaction payments amount for this transaction
     */
     private Double getUnusedAccountCreditAmountForCreditTransaction(final Transaction transactionFromDb) {
    	double result = 0.00;

    	//account credit can be calculated only from credit transactions
    	if (transactionFromDb != null && transactionFromDb.getTransactionType().getName().equals(CREDIT)) {
    		Double totalTransactionPaymentsAmount = transactionRepository.getTransactionPaymentsTotalAmount(transactionFromDb.getId());
    		Double transactionAmount = transactionFromDb.getAmount();

			Double absAccountCreditAmount = Calculation.operation(
					Math.abs(transactionAmount),
					Math.abs(totalTransactionPaymentsAmount),
					MathOperator.SUBTRACT, transactionFromDb.getCurrency());

			// check if transaction payment was created for PROFORMA invoice - in this case
            // the transaction amount should be used as account credit
            double totalTransactionPaymentsAmountForProformaInvoice = getTransactionPaymentsForProformaInvoice(transactionFromDb.getId());

            absAccountCreditAmount = Calculation.operation(
                absAccountCreditAmount,
                Math.abs(totalTransactionPaymentsAmountForProformaInvoice),
                MathOperator.ADD, transactionFromDb.getCurrency());


    		if (absAccountCreditAmount > 0) {

    			result = absAccountCreditAmount * -1; //account credit amount should be a negative value
    		}
    	}

    	return result;
    }

    private double getTransactionPaymentsForProformaInvoice(Integer transactionId) {
         double amount = 0.0;
         if (transactionId == null) {
             return amount;
         }

         List<TransactionPayment> transactionPayments = transactionPaymentRepository.getAllTransactionPaymentsByTransactionId(transactionId);
         if (transactionPayments == null || transactionPayments.isEmpty()) {
             return amount;
         }

        for (TransactionPayment transactionPayment : transactionPayments) {
            if (transactionPayment.getBillingLedger().getProforma()) {
                amount += transactionPayment.getAmount();
            }
        }

         return amount;
    }

     /**
      * Check if the credit transaction will create account credit
      * @param transaction transaction
      * @param billingLedgersPaymentAmounts amount
      * @return boolean
      */
     private boolean isAccountCredit(final Transaction transaction, final HashMap<Integer,Double> billingLedgersPaymentAmounts) {

     	TransactionType transactionType = transactionTypeRepository.getOne(transaction.getTransactionType().getId());
        if (transactionType == null || !transactionType.getName().equals(CREDIT)) {
            return false;
        }

        //Account credit should be applied when:

        //Scenario #1 - credit transaction, billingLedgerIds is not empty, no outstanding invoices,
        //transaction amount > totalAmount of all billing ledgers specified in BillingLedgerIds
        if (transaction.getBillingLedgerIds() != null && !transaction.getBillingLedgerIds().isEmpty()) {
	        if (billingLedgersPaymentAmounts == null || billingLedgersPaymentAmounts.isEmpty()) {
	        	return false;
	        }
	       	double totalAmountForAllBillingLedgers = 0.00;
	       	Currency transactionCurrency = getCurrencyEntity(transaction.getCurrency());
	       	for (Map.Entry<Integer,Double> entry : billingLedgersPaymentAmounts.entrySet()) {
	       		totalAmountForAllBillingLedgers = Calculation.operation(Math.abs(totalAmountForAllBillingLedgers),
                    Math.abs(entry.getValue()), MathOperator.ADD, transactionCurrency);
	       	}
            /*
            if amount of credit transaction is greater than total outstanding amount_owing amount of selected invoices,
            this means account credit will be created.
            */
	       	if (Math.abs(transaction.getAmount()) <= Math.abs(totalAmountForAllBillingLedgers)) {
                return false;
            }
        }

        //Scenario #2 - credit transaction, billingLedgerIds is null or empty
    	return true;
     }

    /**
     * Two scenarios how to create account credit:
     * Scenario #1: Create a credit transaction to pay selected invoices.
     * Scenario #2: Create a credit transaction without specifiyng billingLedgerIds (no invoices selected for payments).
     * Amount of transaction is greater that total outstanding amount_owing of selected ivocies.
     *
     * @param transaction Transaction
     */
    @SuppressWarnings("unchecked")
	private void validateIfAccountCreditCanBeCreatedByCreditTransaction(final Transaction transaction) {

    	String errMsg1 = "Account credit can't be created. The following unpaid invoices need to be paid:" + " %s";

    	List<Integer> billingLedgerIdsToBePaidByCreditTransaction = transaction.getBillingLedgerIds();
    	List<Integer> allUnpaidInvoiceIds = billingLedgerRepository.findUnpaidInvoiceIds(transaction.getAccount().getId(), transaction.getCurrency().getId());

    	if (billingLedgerIdsToBePaidByCreditTransaction==null || billingLedgerIdsToBePaidByCreditTransaction.isEmpty()) {
   		//Scenario 1: Credit transaction without billingLedgerIds
    		if (allUnpaidInvoiceIds!=null && !allUnpaidInvoiceIds.isEmpty()) {
    			String allUnpaidInvoiceIdsStr = do_formatInvoiceIdList (allUnpaidInvoiceIds);
                throw new CustomParametrizedException(String.format(errMsg1,allUnpaidInvoiceIdsStr),
                    BILLING_LEDGER_ID);
    		}

    	} else if (!TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism())) {
   		//Scenario 2: Credit transaction has billingLedgerIds selected for payment
    		//check if there are any unpaid invoices that were not selected for payment
    		//account credit can be applied when all unpaid invoices will be paid

    		final Collection<Integer> unpaidInvoicesLeft = CollectionUtils.subtract(allUnpaidInvoiceIds, billingLedgerIdsToBePaidByCreditTransaction);
    		if (!unpaidInvoicesLeft.isEmpty()) {
    			String unpaidInvoicesLeftStr = do_formatInvoiceIdList (allUnpaidInvoiceIds);
                throw new CustomParametrizedException(String.format(errMsg1,unpaidInvoicesLeftStr),
                    BILLING_LEDGER_ID);
    		}
    	}
    }

    /**
     * Create a string that contains a comma-separated list of invoice IDs/numbers.
     * <p>
     * Each entry will contain both the integer ID (primary key) and the invoice_number field.
     */
    private String do_formatInvoiceIdList (Collection<Integer> invoiceList) {
        final Collection <String> parts = invoiceList
            .stream()
            .map(id-> String.format ("#%d[%s]", id, billingLedgerRepository.getOne(id).getInvoiceNumber()))
            .collect(Collectors.toList());
        return String.join (", ", parts);
    }

    /**
     * Get a list of allowed currencies.
     * This list should always contain USD and account currency.
     */
    @Transactional(readOnly = true)
    public List<Currency> getCurrencyListForTransactionByAccountId(final Integer accountId) {
        Preconditions.checkArgument(accountId != null);

        final Account account = accountRepository.getOne(accountId);
        if (account == null) throw new IllegalStateException("Could not find account by id");

        // add all active account transaction currencies
        List<Currency> results = transactionRepository.findAllActiveAccountCurrencies(account);

        // add account currency if it is not already active
        final Currency accountCurrency = account.getInvoiceCurrency();
        if (!results.contains(accountCurrency)) results.add(accountCurrency);

        // add usd currency if it is not already active
        if (results.stream().noneMatch(c -> StringUtils.equalsIgnoreCase(c.getCurrencyCode(), "USD"))) {
            Currency usdCurrency = currencyService.findByCurrencyCode("USD");
            if (usdCurrency != null) results.add(usdCurrency);
        }

        return results;
    }

    /**
     * Return TransactionPaymentMechanism enum as a list of Strings
     */
    List<String>getPaymentMechanismList() {
        List<String> paymentMechs = EnumUtils.convertEnumToListOfStrings(TransactionPaymentMechanism.values());
        // there should be no 'invoice'
        // selection for the front-end
        paymentMechs.remove(TransactionPaymentMechanism.invoice.name());
        return paymentMechs;
    }

    List<Transaction> getTransactionByBillingLedgerId (Integer billingLedgerId) {
        LOG.debug("get Transactions that have a invoice id: {}", billingLedgerId);
        return transactionPaymentRepository.getAllTransactionByBillingLedgerId(billingLedgerId);
    }

    List<TransactionPayment> getTransactionPaymentsByBillingLedgerId(Integer billingLedgerId) {
        LOG.debug("get Transaction Payments that have a invoice id: {}", billingLedgerId);
        return transactionPaymentRepository.findByBillingLedgerId(billingLedgerId);
    }

    public Page<TransactionPayment> getBillingLedgersByTransactionId(Integer transactionId, Pageable pageable) {
        LOG.debug("get Transaction Payments that have a transaction id: {}", transactionId);
        return transactionPaymentRepository.findByTransactionId(transactionId, pageable);
    }

    Page<TransactionPayment> getAllTransactionPaymentsByTransactionId(Integer transactionId, Pageable pageable) {
        LOG.debug("get Transaction Payments that have a transaction id: {}", transactionId);
        return transactionPaymentRepository.getAllTransactionPaymentsByTransactionId(transactionId, pageable);
    }

    BillingLedger getDebitNoteBillingLedgerByTransactionId(Integer transactionId) {
        LOG.debug("get Debit Note Billing Ledger that have a transaction id: {}", transactionId);
        return billingLedgerRepository.getDebitNoteBillingLedgerByTransactionId(transactionId);
    }

    Page<BillingLedger> getAllBillingLedgerByTransactionId(Integer transactionId, Pageable pageable) {
        LOG.debug("get Billing Ledgers that have a transaction id: {}", transactionId);
        return transactionPaymentRepository.getAllBillingLedgerByTransactionId(transactionId, pageable);
    }

    double calculateTransactionBalance(final Transaction currentTransaction) {
        Double transactionAmount = currentTransaction.getAmount();
        Double transactionBalance;
        final Transaction lastTransaction = transactionRepository
            .findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(currentTransaction.getAccount().getId(),
                currentTransaction.getCurrency().getId());
        Double lastBalance;
        if (lastTransaction != null) {
            lastBalance = lastTransaction.getBalance();
            transactionBalance = transactionAmount + lastBalance;
            LOG.debug("The transaction #{} ({}) for the account #{} will be used to update the current balance of {}",
                lastTransaction.getId(), lastTransaction.getDescription(),
                lastTransaction.getAccount().getId(), lastTransaction.getBalance());
        } else {
            lastBalance = 0.0d;
            transactionBalance = transactionAmount;
            LOG.debug("No transactions for the account {} with currency of {} has been found: a new transaction will be created with balance {}",
                currentTransaction.getAccount().getId(), currentTransaction.getCurrency(), currentTransaction.getAmount());
        }

        Currency currentTransactionCurrency = getCurrencyEntity(currentTransaction.getCurrency());
        int currentCurrencyDecimalPlaces = 2;
        if (currentTransactionCurrency != null) {
            currentCurrencyDecimalPlaces = currentTransactionCurrency.getDecimalPlaces();
        }
        currentTransaction.setBalance(Calculation.truncate(transactionBalance, currentCurrencyDecimalPlaces));
        LOG.debug("A new transaction ({}) with the amount {} for the account #{} will be created with a balance of {} ({} not rounded)",
            currentTransaction.getDescription(), currentTransaction.getAmount(), currentTransaction.getAccount().getId(), currentTransaction.getBalance(), transactionBalance);
        return lastBalance;
    }

    public List<BillingLedger> getTransactionBillingLedgers (final Integer transactionId) {
        final Transaction transaction = transactionRepository.getOne(transactionId);
        final List <Integer> idList = transaction.getBillingLedgerIds();
        if (idList == null) {
            return new ArrayList<>();
        }
        return this.billingLedgerRepository.findAll(idList);
    }

    //----- TRANSACTION RECEIPT  -----------------------------------------

	public ReportDocument getTransactionReceipt(Integer transactionId) {
        Transaction transaction = reportHelper.getTransaction (transactionId);
        if (transaction != null) {
           return new ReportDocument (
                transaction.getReceiptDocumentFileName(),
                transaction.getReceiptDocumentType(),
                transaction.getReceiptDocument()
            );
        }
        return null;
	}

	public ReportDocument getReportDocumentFromTransaction(
			final Transaction transaction) {
		if (transaction == null
				|| transaction.getReceiptDocumentFileName() == null
				|| transaction.getReceiptDocumentType() == null
				|| transaction.getReceiptDocument() == null) {
			return null;

		}
        return new ReportDocument(transaction.getReceiptDocumentFileName(),
            transaction.getReceiptDocumentType(), transaction.getReceiptDocument());
	}

    public Transaction createTransactionByUI(final Transaction transaction, final boolean useApprovalWorkflow) {
        if (TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism())) {
            if (transaction.getTransactionType().isDebit()) {
                return createDebitNote(transaction);
            } else {
                return createCreditNote(transaction, useApprovalWorkflow);
            }
        }
        return createCreditTransactionByPayments(transaction);
    }

    public Transaction createTransactionByPendingTransaction(final Transaction transaction) {
        return createCreditNote(transaction, false);
    }

    public Transaction createPenaltiesTransaction(final InvoiceOverduePenalty invoiceOverduePenalty) {
        final BillingLedger billingLedger = invoiceOverduePenalty.getPenalizedInvoice();

        LOG.debug("Creating a DEBIT TRANSACTION for penalties of the invoice {}", billingLedger.getInvoiceNumber());

        //transaction can be created only for invoice with state "published"
        if (!billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
            throw new CustomParametrizedException("Transaction can only be created for an invoice with state PUBLISHED", "invoice_state_type");
        }
        validateTheInvoiceForDebitTransaction(billingLedger);

        //populate transaction entity from provided BillingLedger
        final Transaction transaction = new Transaction();

        //set account
        transaction.setAccount(billingLedger.getAccount());
        transaction.setTransactionDateTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());

        //set currency and exchange rates
        transaction.setCurrency(billingLedger.getInvoiceCurrency()); // invoice and transaction should have the same currency
        transaction.setExchangeRate(billingLedger.getInvoiceExchange());
        transaction.setTargetCurrency(billingLedger.getTargetCurrency());
        transaction.setExchangeRateToAnsp(billingLedger.getInvoiceExchangeToAnsp());

        //transaction type and amount
        transaction.setTransactionType(transactionTypeRepository.findOneByName(DEBIT)); //DEBIT TRANSACTION
        final Double amountForTransaction = invoiceOverduePenalty.getDefaultPenaltyAmount() + invoiceOverduePenalty.getPunitivePenaltyAmount();
        if (amountForTransaction < 0.0) { //debit transaction should be a positive amount
            throw new CustomParametrizedException("The billing ledger is generating a credit transaction", AMOUNT);
        }
        transaction.setAmount(amountForTransaction); //debit transaction is a value >0
        transaction.setExported(false);
        transaction.setPaymentMechanism(TransactionPaymentMechanism.invoice);

        // Set the transaction description from the billing ledger description if it exists
        transaction.setDescription(SystemConfigurationItemName.OVERDUE_INVOICE + billingLedger.getInvoiceNumber());
        transaction.setPaymentReferenceNumber("N/A");

        LOG.debug("Request to create a transaction through a billing ledger already published: {}", transaction);

        //run custom validation
        validateTransactionInputData(transaction, false);

        //create transaction in DB, but not for penalized invoices
        return doCreateTransaction(transaction);

    }

    private Transaction createDebitNote(final Transaction transaction) {
        LOG.debug("Creating the DEBIT NOTE '{}'", transaction.getDescription());
        validateAndVerifyAmounts(transaction);
        this.validateTransactionInputData(transaction, true);
        newInvoiceCreator().createTransactionDebitNote(transaction);
        return transaction;
    }

    private Transaction createCreditNote(final Transaction transaction, boolean useTheWorkflow) {
        LOG.debug("Creating the CREDIT NOTE '{}'", transaction.getDescription());
        Transaction resultTransaction = null;

        // us 84742: before validation, remove any billing ledger ids that do not have status
        // of PUBLISHED and all surplus should go into account balance as a credit
        removeInvalidCreditNoteBillingLedgerIds(transaction);

        validateAndVerifyAmounts(transaction);
        validateTransactionInputData(transaction, true);

        if (canBeManuallyManaged(transaction) && useTheWorkflow) {
            /* Create a pending transaction against this transaction to be used manually, i.e. through the
             * transaction approval workflow configured.
             */
            return pendingTransactionMapper.pendingTransactionToTransaction(
                this.createPendingTransaction(transaction));

        } else {

            final HashMap<Integer, Double> billingLedgersPaymentAmounts = doCalculateAmountForTransactionPayments(transaction);

            //validate if account credit can be created by credit transaction
            if (isAccountCredit(transaction, billingLedgersPaymentAmounts))
                validateIfAccountCreditCanBeCreatedByCreditTransaction(transaction);

            resultTransaction = doCreateTransaction(transaction);
            resultTransaction.setBillingLedgerIds(transaction.getBillingLedgerIds());
            resultTransaction.setChargesAdjustment(transaction.getChargesAdjustment());

            List<TransactionPayment> transactionPayments = doCreateTransactionPaymentsByTransaction(
                resultTransaction, billingLedgersPaymentAmounts, true);

            newInvoiceCreator().createTransactionCreditNote(resultTransaction, transaction.getChargesAdjustment(),
                transaction.getBillingLedgerIds());

            // EANA 1.5.3 SAT issue:
            // When voiding a unified Tax Invoice with a Credit Note
            // (No other payments against the invoice, just the Credit Note for the total amount)
            // the dates of the CoA needs to be null
            if (billingLedgersPaymentAmounts.size() == 1) {

            	Iterator<Map.Entry<Integer, Double>> iterator = billingLedgersPaymentAmounts.entrySet().iterator();
            	Map.Entry<Integer, Double> billingLedgerPaymentAmount = iterator.next();

            	BillingLedger bl = billingLedgerService.findOne(billingLedgerPaymentAmount.getKey());
            	if (bl != null) {
            		if (bl.getInvoiceType().equals(InvoiceType.UNIFIED_TAX.toValue())) {
            			if (bl.getInvoiceAmount() == -billingLedgerPaymentAmount.getValue()) {
            				List<AircraftRegistration> aircraftRegistrations =
            						unifiedTaxChargesService.getAircraftRegistrationsByBillingLedgerId(bl.getId());

            				for (AircraftRegistration ar : aircraftRegistrations) {
            					ar.setCoaIssueDate(null);
            					ar.setCoaExpiryDate(null);
            				}
            			}
            		}
            	}
            }

            // broadcast created note created event
            creditNoteCreated(resultTransaction);

            // broadcast credit payment created event for each
            for (TransactionPayment transactionPayment : transactionPayments) {
                creditPaymentCreated(transactionPayment);
            }
        }

        return resultTransaction;
    }

    @Transactional
    public Transaction createCreditTransactionByPayments(final Transaction transaction) {
        LOG.debug("Creating a CREDIT TRANSACTION with ref.nr. {} for payments given", transaction.getPaymentReferenceNumber());
        validateAndVerifyAmounts(transaction);

        Transaction resultTransaction;
        validateTransactionInputData(transaction, false);

        // billingLedgersPaymentAmounts are in the transaction currency
        final HashMap<Integer,Double> billingLedgersPaymentAmounts = doCalculateAmountForTransactionPayments(transaction);

        //validate if account credit can be created by credit transaction
        if (isAccountCredit(transaction, billingLedgersPaymentAmounts)) {
            validateIfAccountCreditCanBeCreatedByCreditTransaction(transaction);
        }
        resultTransaction = doCreateTransaction(transaction);
        resultTransaction.setBillingLedgerIds(transaction.getBillingLedgerIds());
        resultTransaction.setChargesAdjustment(transaction.getChargesAdjustment());
        List<TransactionPayment> transactionPayments = doCreateTransactionPaymentsByTransaction(resultTransaction, billingLedgersPaymentAmounts, false);
        newInvoiceCreator(resolveExchangeRateDate(transaction)).createTransactionReceipt(transaction);

        // broadcast credit payment created event for each
        for (TransactionPayment transactionPayment : transactionPayments) {
            creditPaymentCreated(transactionPayment);
        }

        return resultTransaction;
    }

    /**
     * To create automatically a debit transaction made by a billing ledger, calculating the right transaction amount.
     * NOTE: transaction currency should be the same as the currency of the invoice.
     *
     * @param billingLedger the bill that generates a debit transaction.
     */
    @Transactional
    public Transaction createDebitTransactionByInvoice(final BillingLedger billingLedger, final boolean preview) {
        LOG.debug("Creating a DEBIT TRANSACTION for the invoice {}", billingLedger.getInvoiceNumber());

        //transaction can be created only for invoice with state "published"
        if (!billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
            throw new CustomParametrizedException("Transaction can only be created for an invoice with state PUBLISHED", "invoice_state_type");
        }
        validateTheInvoiceForDebitTransaction(billingLedger);

        //populate transaction entity from provided BillingLedger
        final Transaction transaction = new Transaction();

        //set account
        transaction.setAccount(billingLedger.getAccount());
        transaction.setTransactionDateTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());

        //set currency and exchange rates
        transaction.setCurrency(billingLedger.getInvoiceCurrency()); // invoice and transaction should have the same currency
        transaction.setExchangeRate(billingLedger.getInvoiceExchange());
        transaction.setTargetCurrency(billingLedger.getTargetCurrency());
        transaction.setExchangeRateToAnsp(billingLedger.getInvoiceExchangeToAnsp());
        transaction.setFlightmovementCategory(billingLedger.getFlightmovementCategory());

        //transaction type and amount
        transaction.setTransactionType(transactionTypeRepository.findOneByName(DEBIT)); //DEBIT TRANSACTION
        Double amountForTransaction = billingLedger.getInvoiceAmount(); //debit transaction amount should be invoiceAmount and NOT amountOwing
        if (amountForTransaction < 0.0) { //debit transaction should be a positive amount
            throw new CustomParametrizedException("The billing ledger is generating a credit transaction", AMOUNT);
        }
        transaction.setAmount(amountForTransaction); //debit transaction is a value >0
        transaction.setExported(billingLedger.getExported());
        final String invoiceNumber = billingLedger.getInvoiceNumber();
        if (InvoiceType.DEBIT_NOTE.toValue().equals(billingLedger.getInvoiceType())) {
            transaction.setPaymentMechanism(TransactionPaymentMechanism.adjustment);
        } else {
            transaction.setPaymentMechanism(TransactionPaymentMechanism.invoice);
        }
        // Set the transaction description from the billing ledger description if it exists
        transaction.setDescription(StringUtils.defaultIfBlank(billingLedger.getTransactionDescription(), invoiceNumber));
        transaction.setPaymentReferenceNumber(invoiceNumber != null ? invoiceNumber : "N/A");
        transaction.setChargesAdjustment(new HashSet<>(billingLedger.getChargesAdjustment()));

        LOG.debug("Request to create a transaction through a billing ledger already published: {}", transaction);

        //run custom validation
        validateTransactionInputData(transaction, false);

        //create transaction in DB, but not for debit notes
        return doCreateTransaction(transaction, billingLedger, preview);
     }

    /**
     * Validate and calculate local amount from payment values to ensure proper exchange rate was used.
     *
     * Note: Although exchange is done on the front-end, we still want to validate that the correct
     * exchange rate was used and malicious tampering on the front-end wasn't done to 'hack' a preferable
     * exchange rate.
     *
     * @param transaction transaction to validate
     */
	private void validateAndVerifyAmounts (final Transaction transaction) {

        if (transaction == null)
            throw new CustomParametrizedException("No transaction data is provided");

        if (transaction.getPaymentAmount() == null || transaction.getPaymentAmount().equals(0d))
            throw new CustomParametrizedException("Transaction payment amount can't be 0","payment_amount");

        // get and validate transaction currency
        final Currency transactionCurrency = getCurrencyEntity(transaction.getCurrency());
        if (transactionCurrency == null)
            throw new CustomParametrizedException("Invalid transaction currency",CURRENCY);

        // get and validate transaction payment currency
        final Currency transactionPaymentCurrency = getCurrencyEntity(transaction.getPaymentCurrency());
        if (transactionPaymentCurrency == null)
            throw new CustomParametrizedException("Invalid transaction payment currency", "payment_currency");

	    // set datetime of transaction to current date time
        transaction.setTransactionDateTime(ZonedDateTime.now(ZoneOffset.UTC).toLocalDateTime());

        // define exchange rate date based on payment date OR transaction date time if undefined
        LocalDateTime exchangeRateDate;
        if (transaction.getPaymentDate() != null)
            exchangeRateDate = transaction.getPaymentDate();
        else
            exchangeRateDate = transaction.getTransactionDateTime();

        // EANA expects to use the previous day for exchange rates
        if (BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode())
            exchangeRateDate = exchangeRateDate.minusDays(1);

        // set exchange rate based on exchange rate date from payment to local currency
        double exchangeRate = currencyUtils.getExchangeRate(transactionPaymentCurrency, transactionCurrency,
            exchangeRateDate);

        // validate exchange rate exists between currencies by checking for zero value result
        if (exchangeRate == 0d)
            throw new CustomParametrizedException(String.format(
                "No exchange rate found between" +
                " (%s) " +
                "and" +
                " (%s) " +
                "on" +
                " (%s)",
                transactionPaymentCurrency.getCurrencyCode(), transactionCurrency.getCurrencyCode(),
                transaction.getTransactionDateTime()),
                "payment_currency",
                CURRENCY);

        // validate if payment exchange rate differs then supplied value
        if (!transaction.getPaymentExchangeRate().equals(exchangeRate))
            throw new CustomParametrizedException(String.format(
                "The exchange rate has changed. It is now" +
                " %s " + INSTEAD_OF + " %s. " +
                "Please update your form and try again if you'd like to proceed.",
                exchangeRate, transaction.getPaymentExchangeRate()),
                "payment_exchange_rate");

        // resolve transaction amounts so they contain appropriate decimal places for defined currency
        transaction.setPaymentAmount(Calculation.truncate(transaction.getPaymentAmount(),
            transactionPaymentCurrency.getDecimalPlaces() != null ? transactionPaymentCurrency.getDecimalPlaces() : 2));

/*
        transaction.setAmount(Calculation.truncate(transaction.getAmount(),
            transactionCurrency.getDecimalPlaces() != null ? transactionCurrency.getDecimalPlaces() : 2));
*/
        Double transactionAmount = Calculation.truncate(transaction.getAmount(),
                transactionCurrency.getDecimalPlaces() != null ? transactionCurrency.getDecimalPlaces() : 2);

        // calculate transaction amounts using exchange amount service (same method used by front-end)
        // truncate to currency decimal places for even comparison
        final Double localAmount = currencyExchangeRateService.getExchangeAmount(exchangeRate, transaction.getPaymentAmount(),
            transactionCurrency.getDecimalPlaces());
        final Double paymentAmount = currencyExchangeRateService.getExchangeAmount(exchangeRate, transaction.getAmount(),
            transactionPaymentCurrency.getDecimalPlaces(), true);

        // validate if both calculated amounts differ from supplied amount
        // only need one to match for validation
        if (/*!transaction.getAmount().equals(localAmount) */ !transactionAmount.equals(localAmount) && !transaction.getPaymentAmount().equals(paymentAmount))
            throw new CustomParametrizedException(String.format(
                "The system has calculated that the local amount should be" +
                " %s " + INSTEAD_OF + " %s " +
                "OR the payment amount should be" +
                " %s " + INSTEAD_OF + " %s " +
                "from what was displayed when the transaction was submitted. Please contact your system administrator if this problem continues.",
                localAmount, transaction.getAmount(), paymentAmount, transaction.getPaymentAmount()), AMOUNT);
    }

    @Transactional(readOnly = true)
	public Page<Transaction> findAllTransactionsForSelfCareAccounts(final Pageable pageable,
                                                                    final String searchFilter,
                                                                    final Integer accountId,
                                                                    final LocalDate startDate,
                                                                    final LocalDate endDate,
                                                                    final Integer userId){
        LOG.debug("Request to get transactions by searchFilter {}, accountId {}, startDate {}, endDate {}, userId {}",
            searchFilter, accountId, startDate, endDate, userId);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(searchFilter);

        List<Integer> accountUsersMap = accountRepository.getAllAccountsFromAccountUsersMap();

        filterBuilder.restrictOn(JoinFilter.in(ACCOUNT, "id", accountUsersMap));

        if (accountId != null)
            filterBuilder.restrictOn(Filter.equals(ACCOUNT, accountId));

        if (startDate != null && endDate != null) {
            LocalDateTime startAt = startDate.atStartOfDay();
            LocalDateTime endAt = endDate.atTime(LocalTime.MAX);
            filterBuilder.restrictOn(Filter.included(TRANSACTION_DATE_TIME, startAt, endAt));
        }

        if (userId != null) {
            filterBuilder.restrictOn(JoinFilter.equal(ACCOUNT, "accountUsers", "id", userId));
        }

        return transactionRepository.findAll(filterBuilder.build(), pageable);
	}



	@Transactional(readOnly = true)
	public List<Transaction> findAllUnexported() {
        return transactionRepository.findAll(new TransactionExportFilterSpecification(
            new FiltersSpecification.Builder(), false, exportSupport(), exportableMechanisms()));
    }

    private Transaction save(Transaction transaction) {
	    Transaction savedTransaction = transactionRepository.save(transaction);
	    if (transaction.getChargesAdjustment() != null) {
            for (ChargesAdjustment chargesAdjustment : transaction.getChargesAdjustment()) {
                chargesAdjustment.setTransaction(savedTransaction);
                chargesAdjustmentService.save(chargesAdjustment);
            }
        }

        // run transaction service provider save method if any
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            provider.save(savedTransaction);
        }

        //update account last activity date/time for Whitelisting
        accountService.updateAccountLastActivityDateTimeForWhitelisting(savedTransaction.getAccount());

        return savedTransaction;
    }

    /**
     * Used solely for sending created flag to service providers that a credit note transaction
     * has been created.
     *
     * This is required as other classes create transactions in pieces and injecting into
     * the save method isn't possible in all cases.
     *
     * @param transaction transaction that was created
     */
    private void creditNoteCreated(Transaction transaction) {

        // run transaction service provider creditNoteCreated methods if any
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            provider.creditNoteCreated(transaction);
        }
    }

    /**
     * Used solely for sending created flag to service providers that a credit payment transaction
     * has been created.
     *
     * This is required as other classes create transactions in pieces and injecting into
     * the save method isn't possible in all cases.
     *
     * @param transactionPayment transaction payment that was created
     */
    private void creditPaymentCreated(TransactionPayment transactionPayment) {

        // run transaction service provider creditPaymentCreated methods if any
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            provider.creditPaymentCreated(transactionPayment);
        }
    }

    /**
     * Remove invalid billing ledgers as per user story 84742. This is required
     * for transaction approval workflow if credits are being applied to a now
     * PAID invoice by the time this transaction is approved.
     *
     * @param transaction transaction to clean
     */
    private void removeInvalidCreditNoteBillingLedgerIds(Transaction transaction) {

        // only apply if credit transaction adjustment, aka credit-note
        if (transaction.getTransactionType() == null
            || !transaction.getTransactionType().isCredit()
            || !TransactionPaymentMechanism.adjustment.equals(transaction.getPaymentMechanism()))
            return;

        // loop through each billing ledger and remove any without status of PUBLISHED
        // the first is ignored and it is the credit adjustment
        Integer adjustmentId = transaction.getBillingLedgerIds().get(0);
        for (Iterator<Integer> iterator = transaction.getBillingLedgerIds().iterator(); iterator.hasNext();) {
            Integer billingLedgerId = iterator.next();

            // skip over adjustment billing ledger id
            if (billingLedgerId.equals(adjustmentId))
                continue;

            // find billing ledger status and remove if not PUBLISHED
            BillingLedger billingLedger = billingLedgerRepository.findOne(billingLedgerId);
            if (!billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue())) {
                iterator.remove();
            }
        }
    }

    /**
     * Returns true support if one or more plugin service providers supports exporting of transactions.
     */
    TransactionExportSupport exportSupport() {

        TransactionExportSupport support = new TransactionExportSupport();

        // set true only if one or more active plugin providers support exporting
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupportCreditNote()) support.setCreditNotes(true);
            if (provider.isExportSupportPayment()) support.setPayments(true);
        }

        return support;
    }

    /**
     * Returns true if one or moer plugin service providers support exporting of transactions
     * for provided mechanism.
     *
     * @param mechanism transaction mechanism to check
     * @return true if supported
     */
    Boolean isExportSupport(TransactionPaymentMechanism mechanism) {

        // return true only if one or more active plugins support exporting mechanism
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupport(mechanism)) return true;
        }

        // if no active providers support exporting mechanism, return false
        return false;
    }

    /**
     * Returns list of exportable transaction payment mechanisms.
     *
     * @return list of exportable mechanisms
     */
    private List<TransactionPaymentMechanism> exportableMechanisms() {

        // loop through each provider and get all supported mechanisms
        List<TransactionPaymentMechanism> supported = new ArrayList<>();
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {

            // get provider's exportable mechanisms and continue if null or empty
            List<TransactionPaymentMechanism> mechanisms = provider.exportableMechanisms();
            if (mechanisms == null || mechanisms.isEmpty())
                continue;

            for (TransactionPaymentMechanism mechanism : mechanisms) {
                if (!supported.contains(mechanism))
                    supported.add(mechanism);
            }
        }
        return supported;
    }

    /**
     * Export transaction to service provider.
     *
     * @param transaction transaction to export
     */
    @Transactional
    public void export(final Transaction transaction) {

        if (transaction == null || transaction.getId() == null)
            return;

        // find all transaction payments
        List<TransactionPayment> transactionPayments = transactionPaymentRepository
            .findByTransactionId(transaction.getId());

        // run billing ledger service provider export methods if any
        for (TransactionServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupport()) provider.export(transaction, transactionPayments);
        }
    }

    /**
     * If system configuration item "Prohibit aviation charges to HQ billing centre" enabled,
     * HQ Billing Center cannot create aviation charges.
     *
     * @param billingLedger billing ledger with billing center to validate
     */
    private void ensureBillingCenterValid(final BillingLedger billingLedger, final BillingCenter billingCenter) {

        //  only validate if billing ledger and hq billing center
        if (billingLedger == null || billingCenter == null || !billingCenter.getHq())
            return;

        // if set, prohibit charges to hq billing center if aviation noniata adjustment
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        if (invoiceType != null && invoiceType.equals(InvoiceType.AVIATION_NONIATA)
            && systemConfigurationService.getBoolean(SystemConfigurationItemName.PROHIBIT_HQ_AVIATION_CHARGES)) {
            LOG.warn("Invalid adjustment billing centre '{}', cannot be HQ.", billingCenter.getName());
            throw new ErrorDTO.Builder("Cannot generate aviation adjustment for HQ billing centre.")
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildInvalidDataException();
        }
    }

    private TransactionReceiptCreator newInvoiceCreator() {
        return newInvoiceCreator(LocalDateTime.now());
    }

    private TransactionReceiptCreator newInvoiceCreator(final LocalDateTime exchangeRateDate) {
        return new TransactionReceiptCreator(
            reportHelper,
            currencyUtils,
            transactionReceiptDocumentCreator,
            invoiceSequenceNumberHelper,
            transactionPaymentRepository,
            billingLedgerService,
            roundingUtils,
            systemConfigurationService,
            bankCodeService,
            this,
            aviationInvoiceChargeProviders,
            exchangeRateDate
        );
    }

    public List<Transaction> getTransactionsByInvoiceId(Integer id) {
        return transactionRepository.getTransactionsByInvoiceId(id);
    }

    List<InterestInvoiceError> checkIfInterestInvoiceNeeded(List<Integer> billingLedgerIds) {

        // only if the system configuration "Apply interest penalty on" is set to "Invoice final payment", an interest invoice should be created
        if (!systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON)
                .equalsIgnoreCase("Invoice final payment")) {
            return Collections.emptyList();
        }

        Currency interestInvoiceCurrency = getInterestInvoiceCurrency();

        List<InterestInvoiceError> interestInvoiceError = new ArrayList<>();

        for (Integer id : billingLedgerIds) {
            BillingLedger billingLedger = billingLedgerService.getOne(id);

            InterestInvoiceError invoiceError = new InterestInvoiceError();
            invoiceError.setInvoiceNumber(billingLedger.getInvoiceNumber());

            Double accountMonthlyOverduePenaltyRate = billingLedger.getAccount().getMonthlyOverduePenaltyRate();

            if (billingLedger.getInvoiceStateType().equalsIgnoreCase("PAID") && accountMonthlyOverduePenaltyRate <= 0.00) {

                LocalDateTime finalPaymentDate = billingLedger.getFinalPaymentDate();

                checkIfInterestRateIsMissing(invoiceError, billingLedger);
                checkIfExchangeRateIsMissing(finalPaymentDate, invoiceError, billingLedger.getInvoiceCurrency(), interestInvoiceCurrency);

                if (invoiceError.getMissingExchangeRateOnDate() != null || invoiceError.getMissingInterestRateOnDate() != null) {
                    interestInvoiceError.add(invoiceError);
                }
            }
        }
        return interestInvoiceError;
    }

    private Currency getInterestInvoiceCurrency() {
        String interestPenaltyInvoiceCurrency = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.INTEREST_PENALTY_INVOICE_CURRENCY);

        Currency interestInvoiceCurrency = null;
        if (interestPenaltyInvoiceCurrency.equalsIgnoreCase("USD")) {
            interestInvoiceCurrency = currencyUtils.getCurrencyUSD();
        } else if (interestPenaltyInvoiceCurrency.equalsIgnoreCase("ANSP")) {
            interestInvoiceCurrency = currencyUtils.getAnspCurrency();
        }
        return interestInvoiceCurrency;
    }

    private void checkIfInterestRateIsMissing(final InterestInvoiceError invoiceError,
                                              final BillingLedger overdueInvoice){

        LocalDateTime paymentDueDate = overdueInvoice.getPaymentDueDate();
        LocalDateTime finalPaymentDate = overdueInvoice.getFinalPaymentDate();
        InterestRate interestRate = null;
        LocalDate missingInterestRateOnDate = null;

        if (finalPaymentDate != null && finalPaymentDate.isAfter(paymentDueDate)) {

            LocalDate interestStartDate = paymentDueDate.toLocalDate().plusDays(1);

            List<Transaction> invoiceTransactions = transactionRepository.getTransactionsByInvoiceId(overdueInvoice.getId());
            if (invoiceTransactions == null || invoiceTransactions.isEmpty()) {
                interestRate = interestRateService.getInterestRateByStartDate(interestStartDate);
                missingInterestRateOnDate = interestStartDate;
            } else {
                for (Transaction transaction: invoiceTransactions) {
                    LocalDate transactionDate = transaction.getPaymentDate() != null ? transaction.getPaymentDate().toLocalDate()
                        : transaction.getTransactionDateTime().toLocalDate();

                    if (!transactionDate.isAfter(interestStartDate)) {
                        continue;
                    }
                    interestRate = interestRateService.getInterestRateByStartDate(transactionDate.minusDays(1));
                    missingInterestRateOnDate = transactionDate.minusDays(1);

                    if (interestRate == null) {
                        break;
                    }
                }
            }
            if (interestRate == null) {
                invoiceError.setMissingInterestRateOnDate(missingInterestRateOnDate);
            }
        }
    }

    private void checkIfExchangeRateIsMissing(final LocalDateTime finalPaymentDate,
                                              final InterestInvoiceError invoiceError,
                                              final Currency invoiceCurrency,
                                              final Currency interestInvoiceCurrency) {

        if (interestInvoiceCurrency == null || invoiceCurrency.equals(interestInvoiceCurrency)) {
            return;
        }

        // define exchange rate date based on payment date
        LocalDateTime exchangeRateDate = finalPaymentDate;

        // EANA expects to use the previous day for exchange rates
        if (BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode())
            exchangeRateDate = exchangeRateDate.minusDays(1);

        double exchangeRate = currencyUtils.getExchangeRate(invoiceCurrency, interestInvoiceCurrency, exchangeRateDate);

        if (exchangeRate == 0d) {
            invoiceError.setMissingExchangeRateOnDate(exchangeRateDate.toLocalDate());
            invoiceError.setFromCurrency(invoiceCurrency.getCurrencyName());
            invoiceError.setToCurrency(interestInvoiceCurrency.getCurrencyName());
        }
    }

    private LocalDateTime resolveExchangeRateDate(final Transaction transaction) {

        // define exchange rate date based on payment date OR transaction date time if undefined
        LocalDateTime exchangeRateDate;
        if (transaction.getPaymentDate() != null)
            exchangeRateDate = transaction.getPaymentDate();
        else
            exchangeRateDate = transaction.getTransactionDateTime();

        // EANA expects to use the previous day for exchange rates
         return BillingOrgCode.EANA == systemConfigurationService.getBillingOrgCode()
             ? exchangeRateDate.minusDays(1)
             : exchangeRateDate;
    }

    public long countAllForSelfCareAccounts() {
        return transactionRepository.countAllForSelfCareAccounts();
    }

    public long countAllForSelfCareUser(int userId) {
        return transactionRepository.countAllForSelfCareUser(userId);
    }

    public Transaction findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(int accountId, int currencyId) {
        return transactionRepository.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(accountId, currencyId);
    }

    /**
     * Scheduled task will run at 1am every day
     *
     * Check for whitelisting flights that were scheduled and weren't cancelled.
     * The system will debit the operator’s account for the flight
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void checkWhitelistingFlightsThatNeedToBePaid () {
        LOG.debug("Check for Whitelisting Flights that need to be paid");
        LocalDate whitelistingStartDate = whitelistingUtils.getWhitelistingStartDate();

        if (whitelistingStartDate == null) {
            LOG.debug("Whitelisting Retroactive Payments are not applied because whitelistingStartDate is null");
            return;
        }

        LocalDateTime whitelistingStart = LocalDateTime.of(whitelistingStartDate, LocalTime.MIN);
        LocalDateTime yesterday = LocalDateTime.of(LocalDate.now().minusDays(1), LocalTime.MAX);

        List<Account> accounts = flightMovementRepository.findCashAccountsThatHaveWhitelistingFlights(whitelistingStart, yesterday);

        if (accounts == null || accounts.isEmpty()) {
            LOG.debug("There are no Whitelisting Flights that need to be paid");
            return;
        }

        for (Account account: accounts) {
            checkWhitelistingRetroactivePayments(account, yesterday);
        }
    }

    /**
     * When whitelisting start date is reached, the system will begin deducting flight cost from prepayment balances.
     * Flights prior to the implementation date will not be charged.
     *
     * When a flight is completed which should have been prepaid, but was not due to insufficient funds,
     * when the next prepayment is made, the funds will be applied first to flights already made but not paid for,
     * back to the system implementation date.
     * @param account Account
     */
    @SuppressWarnings("squid:S3776")
    public void checkWhitelistingRetroactivePayments(Account account, LocalDateTime dateTime) {
        LOG.debug("Check for Whitelisting Retroactive Payments");
        LocalDate whitelistingStartDate = whitelistingUtils.getWhitelistingStartDate();

        if (whitelistingStartDate == null) {
            LOG.debug("Whitelisting Retroactive Payments are not applied because whitelistingStartDate is null");
            return;
        }

        if (!account.getCashAccount()) {
            LOG.debug("Whitelisting Retroactive Payments are not applied because '{}' is a Credit account", account.getName());
            return;
        }

        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        int accountId = account.getId();
        String accountName = account.getName();

        final Transaction lastTransaction = findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(accountId, usdCurrency.getId());
        double lastBalance = lastTransaction != null ? lastTransaction.getBalance() : 0d;

        if (lastBalance >= 0) {
            LOG.debug("Whitelisting Retroactive Payments are not applied for the Account id: {}, name: {}", accountId, accountName);
            return;
        }

        LocalDateTime whitelistingStart = LocalDateTime.of(whitelistingStartDate, LocalTime.MIN);
        if (dateTime == null) {
            dateTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        }

        double transactionAmount = 0d;
        List<FlightMovement> flightMovements = flightMovementRepository.
            getAllFlightMovementsByAccountForWhitelistingRetroactivePayments(accountId, whitelistingStart, dateTime);
        List<FlightMovement> flightsForTransaction = new ArrayList<>();

        for (FlightMovement fm: flightMovements) {
            if (whitelistingUtils.ifWhitelistingFlightIsBillable(fm)) {
                flightMovementBuilder.recalculateCharges(fm);
                flightMovementBuilder.handleStatus(fm, false);
                flightMovementRepositoryUtility.persist(fm);
                if (fm.getStatus().equals(FlightMovementStatus.PENDING)) {
                    double totalChargeAmount = whitelistingUtils.getTotalChargesAmountInUSD(fm);
                    if (lastBalance + totalChargeAmount <= 0) {
                        lastBalance += totalChargeAmount;
                        flightsForTransaction.add(fm);
                        transactionAmount += totalChargeAmount;
                    } else {
                        LOG.debug("There are no enough money to pay for the flight id: {}, total charge amount for this flight: {} USD",
                            fm.getId(), totalChargeAmount);
                    }
                }
            }
        }

        if (flightsForTransaction.isEmpty()) {
            LOG.debug("There are no flights for the Account id: {}, name: {} to include for Whitelisting Retroactive Payments", accountId, accountName);
            return;
        }

        createDebitTransactionForWhitelistingFlights(account, usdCurrency, transactionAmount);
        updateFlightMovementStatus(flightsForTransaction);
    }

    private void createDebitTransactionForWhitelistingFlights(final Account account,
                                                              final Currency usdCurrency,
                                                              final double transactionAmount) {
        TransactionType transactionType = transactionTypeRepository.findOneByName(DEBIT);
        Transaction transaction = new Transaction();
        transaction.setAmount(transactionAmount);
        transaction.setPaymentAmount(transactionAmount);
        transaction.setAccount(account);
        transaction.setTransactionDateTime(LocalDateTime.now());
        transaction.setDescription("Payment for Whitelisting flights");
        transaction.setTransactionType(transactionType);
        transaction.setExported(false);
        transaction.setPaymentMechanism(TransactionPaymentMechanism.credit);
        transaction.setCurrency(usdCurrency);
        transaction.setPaymentCurrency(usdCurrency);
        transaction.setPaymentReferenceNumber("N/A");
        transaction.setPaymentExchangeRate(1d);
        doCreateTransaction(transaction);
    }

    private void updateFlightMovementStatus(List<FlightMovement> flightMovements) {
        for (FlightMovement flightMovement: flightMovements) {
            flightMovement.setStatus(FlightMovementStatus.PAID);
            flightMovementRepositoryUtility.persist(flightMovement);
        }
    }

}
