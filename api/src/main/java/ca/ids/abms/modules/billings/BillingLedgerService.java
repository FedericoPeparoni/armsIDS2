package ca.ids.abms.modules.billings;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;

import javax.persistence.EntityNotFoundException;

import ca.ids.abms.modules.billings.utility.BillingLedgerExportFilterSpecification;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.reports2.common.RoundingUtils;
import ca.ids.abms.modules.transactions.*;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxCharges;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxChargesService;
import ca.ids.abms.util.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ErrorVariables;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountRepository;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustmentService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.common.services.AbstractPluginService;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyRepository;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.overdueinvoices.OverdueInvoiceService;
import ca.ids.abms.modules.roles.Role;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;

@Service
@Transactional
@SuppressWarnings("WeakerAccess")
public class BillingLedgerService extends AbstractPluginService<BillingLedgerServiceProvider> {

    private static final Logger LOG = LoggerFactory.getLogger(BillingLedgerService.class);

    private static final String ACCOUNT = "account";
    private static final String AMOUNT_OWING = "amountOwing";
    private static final String INVOICE_DATE_OF_ISSUE = "invoiceDateOfIssue";
    private static final String INVOICE_STATE_TYPE = "invoiceStateType";
    private static final String PAYMENT_DUE_DATE = "paymentDueDate";
    private static final String INVOICE_PERIOD_OR_DATE = "invoicePeriodOrDate";
    private static final String USER ="user";
    private static final String BILLING_CENTRE = "billingCenter";
    private static final String AVIATION_NONIATA = "aviation-noniata";

    private final BillingLedgerRepository billingLedgerRepository;
    private final TransactionService transactionService;
    private final TransactionTypeService transactionTypeService;
    private final CurrencyRepository currencyRepository;
    private final AccountRepository accountRepository;
    private final CurrencyUtils currencyUtils;
    private final TransactionPaymentRepository transactionPaymentRepository;
    private final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository;
    private final InvoiceLineItemRepository invoiceLineItemRepository;
    private final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper;
    private final UserService userService;
    private final SystemConfigurationService systemConfigurationService;
    private final BillingLedgerFlightUtility billingLedgerFlightUtility;
    private final InvoicesApprovalWorkflow invoicesApprovalWorkflow;
    private final ChargesAdjustmentService chargesAdjustmentService;
    private final OverdueInvoiceService overdueInvoiceService;
    private final RoundingUtils roundingUtils;
    private final FlightMovementService flightMovementService;
    private final UnifiedTaxChargesService unifiedTaxChargesService;

    @SuppressWarnings("squid:S00107")
    public BillingLedgerService(
        final BillingLedgerRepository billingLedgerRepository,
        final TransactionService transactionService,
        TransactionTypeService transactionTypeService, final CurrencyRepository currencyRepository,
        final AccountRepository accountRepository,
        final CurrencyUtils currencyUtils,
        final TransactionPaymentRepository transactionPaymentRepository,
        final InvoiceOverduePenaltyRepository invoiceOverduePenaltyRepository,
        final InvoiceLineItemRepository invoiceLineItemRepository,
        final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
        final UserService userService,
        final SystemConfigurationService systemConfigurationService,
        final BillingLedgerFlightUtility billingLedgerFlightUtility,
        final InvoicesApprovalWorkflow invoicesApprovalWorkflow,
        final ChargesAdjustmentService chargesAdjustmentService,
        final OverdueInvoiceService overdueInvoiceService,
        final RoundingUtils roundingUtils,
        final FlightMovementService flightMovementService,
        final UnifiedTaxChargesService unifiedTaxChargesService) {
        this.billingLedgerRepository = billingLedgerRepository;
        this.transactionService = transactionService;
        this.transactionTypeService = transactionTypeService;
        this.currencyRepository = currencyRepository;
        this.accountRepository = accountRepository;
        this.currencyUtils = currencyUtils;
        this.transactionPaymentRepository = transactionPaymentRepository;
        this.invoiceOverduePenaltyRepository = invoiceOverduePenaltyRepository;
        this.invoiceLineItemRepository = invoiceLineItemRepository;
        this.invoiceSequenceNumberHelper = invoiceSequenceNumberHelper;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.billingLedgerFlightUtility = billingLedgerFlightUtility;
        this.invoicesApprovalWorkflow = invoicesApprovalWorkflow;
        this.chargesAdjustmentService = chargesAdjustmentService;
        this.overdueInvoiceService = overdueInvoiceService;
        this.roundingUtils = roundingUtils;
        this.flightMovementService = flightMovementService;
        this.unifiedTaxChargesService = unifiedTaxChargesService;
    }

    @Transactional(readOnly = true)
    public List<BillingLedger> getPreviousBillingLedgerByAccountAndType(final Integer accountId, final String invoiceType, final LocalDateTime billingPeriodOrDate) {
        LOG.debug("Request to get all transactions");
        return billingLedgerRepository.findByAccountIdAndInvoiceTypeAndInvoicePeriodOrDate( accountId , invoiceType ,billingPeriodOrDate);
    }
    
    
    @Transactional(readOnly = true)
    @SuppressWarnings({"squid:S3776", "squid:S00107"})
    public Page<BillingLedger>findAll(final String filter,
                                      final String textSearch,
                                      final Pageable pageable,
                                      final Boolean selfCare,
                                      final Integer userId,
                                      final Boolean exported,
                                      final Integer accountId,
                                      final LocalDateTime startDate,
                                      final LocalDateTime endDate,
                                      final LocalDate invoiceDateOfIssue,
                                      final String flightIdOrRegistration,
                                      final Integer createdByUserId,
                                      final Integer billingCentreId) {
        LOG.debug("Attempting to find billing ledgers by filters. " +
                "Search: {}, State: {}, Only Self-Care accounts: {}, For user id: {}, For Account: {}, Start date: {}, End date: {} " +
                "FlightId or Registrtion: {}, Created by user: {}, For Billing Centre: {}",
            textSearch, filter, selfCare, userId, accountId, startDate, endDate, flightIdOrRegistration, createdByUserId, billingCentreId);

        User currentUser = userService.getUserByLogin(SecurityUtils.getCurrentUserLogin());
        String[] permissions = new String[] {"invoices_approve", "invoices_publish", "self_care_access", "self_care_admin"};
        boolean canApproveOrPublish = currentUser.getPermissions().stream().anyMatch(p -> Arrays.asList(permissions).contains(p));

        // define filter spec with text search
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder()
            .lookFor(textSearch);

        if (!canApproveOrPublish) {
            String[] viewStates = new String[] {InvoiceStateType.PAID.toValue(), InvoiceStateType.PUBLISHED.toValue(), InvoiceStateType.VOID.toValue()};
            filterBuilder.restrictOn(Filter.in(INVOICE_STATE_TYPE, viewStates));
        }

        // apply appropriate invoice state related filters
        applyStateFilters(filterBuilder, filter);

        // apply invoice date of issue filter
        if (invoiceDateOfIssue != null) {
            filterBuilder.restrictOn(Filter.included(INVOICE_DATE_OF_ISSUE, invoiceDateOfIssue.atStartOfDay(),
                invoiceDateOfIssue.atTime(LocalTime.MAX)));
        }

        // only self care user accounts
        boolean onlyAccountsWithSelfCareUsers = false;
        if (selfCare != null && selfCare)
        	onlyAccountsWithSelfCareUsers = true;

        // only self care users with specific user id
        Integer onlyAccountsOfUserId = null;
        if (userId != null)
        	onlyAccountsOfUserId = userId;

        // filter by account name, null will be skipped
        if (accountId != null)
            filterBuilder.restrictOn(Filter.equals(ACCOUNT, accountId));

        // filter by billing centre, null will be skipped
        if (billingCentreId != null)
            filterBuilder.restrictOn(Filter.equals(BILLING_CENTRE, billingCentreId));

        // date filter, null will be skipped
        if (startDate != null && endDate != null)
            filterBuilder.restrictOn(Filter.included(INVOICE_PERIOD_OR_DATE, startDate, endDate));

        // filter by created by user, default - current user
        if(createdByUserId != null) {
        	filterBuilder.restrictOn(Filter.equals(USER, createdByUserId));
        }

        return billingLedgerRepository.findAll(new BillingLedgerExportFilterSpecification(
            filterBuilder, exported, exportableTypes(), onlyAccountsWithSelfCareUsers, onlyAccountsOfUserId, flightIdOrRegistration), pageable);
    }

    @Transactional(readOnly = true)
    public Page<BillingLedger> findAll(String filter, Pageable pageable) {
        LOG.debug("Request to get billing ledgers filtered by: {}", filter);
        Page<BillingLedger> invoices;

        String filterStr = filter == null ? "all" : filter;

        if (filterStr.equalsIgnoreCase("unapproved")) {
            LOG.debug("Searching for UNAPPROVED state");

            // unapproved: invoices that have state "NEW"
            invoices = billingLedgerRepository.findByInvoiceStateTypeInList(new String[]{InvoiceStateType.NEW.toValue()}, pageable);
        }
        else if (filterStr.equalsIgnoreCase("unpublished")) {
            LOG.debug("Searching for UNPUBLISHED state");

           // unpublished: invoices that have state "NEW" and "APPROVED"
           invoices = billingLedgerRepository.findByInvoiceStateTypeInList(new String[]{
                InvoiceStateType.NEW.toValue(), InvoiceStateType.APPROVED.toValue()}, pageable);
        }

        else if (filterStr.equalsIgnoreCase("unpaid")) {
            LOG.debug("Searching for UNPAID state");

            // unpaid: invoices that have state == "PUBLISHED"
            // only PUBLISHED invoices can be selected for payment and only these invoices can be considered unpaid.
            invoices = billingLedgerRepository.findByInvoiceStateTypeInList(new String[]{InvoiceStateType.PUBLISHED.toValue()}, pageable);
        }
        else if (filterStr.equalsIgnoreCase("overdue")) {
            LOG.debug("Searching for OVERDUE invoices");

            // overdue: invoices that have current_date > paymentDueDate and amountOwing > 0
           invoices = billingLedgerRepository
                .findByPaymentDueDateBeforeAndAmountOwingGreaterThan (LocalDate.now(ZoneId.of("UTC")).atStartOfDay(), 0.0D, pageable);
        }
        else if (filterStr.equalsIgnoreCase("paid")) {
            LOG.debug("Searching for PAID invoices");

            // paid: invoices that have a invoice_state_type_id that matches paid
            invoices = billingLedgerRepository.findByInvoiceStateTypeInList(new String[]{InvoiceStateType.PAID.toValue()}, pageable);
        }
        else if (filterStr.equalsIgnoreCase("void")) {
            LOG.debug("Searching for VOID invoices");

            // paid: invoices that have a invoice_state_type_id that matches void
            invoices = billingLedgerRepository.findByInvoiceStateTypeInList(new String[]{InvoiceStateType.VOID.toValue()}, pageable);
        }
        else {
            LOG.debug("Searching for ALL invoices");
            return billingLedgerRepository.findAll(pageable);
        }

        return invoices;
    }

    @Transactional(readOnly = true)
    public List<BillingLedger> findAll(final List<Integer> ids) {
        LOG.debug("Request to find all billing ledgers by : {}", ids);
        return billingLedgerRepository.findAll(ids);
    }

    @Transactional(readOnly = true)
    public BillingLedger findOne(final Integer id) {
        LOG.trace("Request to find billing ledger : {}", id);
        return billingLedgerRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<Integer> findUnpaidInvoiceIds(final Integer accountId, final Integer invoiceCurrencyId) {
        LOG.debug("Request to find unpaid invoice ids by : accountId {}, invoiceCurrencyId {}",
            accountId, invoiceCurrencyId);
        return billingLedgerRepository.findUnpaidInvoiceIds(accountId, invoiceCurrencyId);
    }

    @Transactional(readOnly = true)
    public BillingLedger findByInvoiceNumber(final String invoiceNumber) {
        LOG.debug("Request to find invoice by number : invoiceNumber {}", invoiceNumber);
        return billingLedgerRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Transactional(readOnly = true)
    public List<BillingLedger> findAllUnexported() {
        return billingLedgerRepository.findAll(new BillingLedgerExportFilterSpecification(
            new FiltersSpecification.Builder(), false, exportableTypes()));
    }

    @Transactional(readOnly = true)
    public BillingLedger getOne(Integer id) {
        LOG.debug("Request to get BillingLedger : {}", id);
        return billingLedgerRepository.getOne(id);
    }

    /**
     * Only invoices with state PUBLISHED can be paid in transaction.
     * Currency and account of the invoice should match the currency and the account of the transaction.
     * This method returns a list of invoices that are available for payment.
     */
    @Transactional(readOnly = true)
    public Page<BillingLedger> getUnpaidBillingLedgersByAccountAndCurrency(Integer accountId, Integer currencyId, Pageable pageable) {
        return billingLedgerRepository.findByAccountIdAndInvoiceCurrencyIdAndInvoiceStateType(accountId, currencyId, InvoiceStateType.PUBLISHED.toValue(), pageable);
    }

    @Transactional(readOnly = true)
    public Page<BillingLedger> getAllBillingLedgersByAccountAndCurrency(final Integer accountId, final Integer currencyId, final Pageable pageable) {
        return billingLedgerRepository.findByAccountIdAndInvoiceCurrencyIdAndInvoiceStateTypeInAndInvoiceTypeNot(
            accountId, currencyId, new String[]{InvoiceStateType.PUBLISHED.toValue(), InvoiceStateType.PAID.toValue()},
            InvoiceType.DEBIT_NOTE.toValue(), pageable);
    }

    public BillingLedger save(BillingLedger billingLedger) {
        LOG.debug("Request to save BillingLedger : {}", billingLedger);
        return saveBillingLedger(billingLedger);
    }

    /**
     *  Get invoice line items by invoice id.
     */
    @Transactional(readOnly = true)
    public List<InvoiceLineItem> getLineItemsByInvoiceId(Integer invoiceId) {

        BillingLedger billingLedger = billingLedgerRepository.getOne(invoiceId);

        return invoiceLineItemRepository.findByBillingLedger(billingLedger);
    }

    public BillingLedger createBillingLedgerAndTransaction(final BillingLedger billingLedger, final Boolean allowApplyPenalty, boolean preview) {
        return createBillingLedgerAndTransaction(billingLedger, allowApplyPenalty, false, preview);
    }

    /**
     * Create a billing ledger and eventually a transaction if the item is published. Billing ledger
     * invoiceAmount and amountOwing are rounded to system configuration specifications depending on
     * billing ledger type (AVIATION_IATA, AVIATION_NONIATA, NON_AVIATION, PASSENGER).
     *
     * @param billingLedger billing ledger to create
     * @param allowApplyPenalty apply penalties to invoice if applicable
     * @return newly created billing ledger
     */
    @Transactional
    public BillingLedger createBillingLedgerAndTransaction(final BillingLedger billingLedger,
                                                           Boolean allowApplyPenalty,
                                                           final boolean proforma,
                                                           final boolean preview)  {
        if (proforma) {
            allowApplyPenalty = false;
            LOG.debug("Request to create a proforma billing ledger without debit transaction: {}", billingLedger);
        } else {
            LOG.debug("Request to create a billing ledger and a transaction if it is required: {}", billingLedger);
        }
        final BillingLedger savedBillingLedger = createBillingLedger(billingLedger, allowApplyPenalty, preview);
        if (!proforma && Math.abs(savedBillingLedger.getInvoiceAmount()) > 0 &&
            InvoiceStateType.PUBLISHED.toValue().equals(savedBillingLedger.getInvoiceStateType())) {
            // create debit transaction
            // transaction can be created only for an invoice with state "published"
            transactionService.createDebitTransactionByInvoice(savedBillingLedger, preview);
        }
        return savedBillingLedger;
    }

    private BillingLedger createBillingLedger (final BillingLedger billingLedger, final boolean allowApplyPenalty, final boolean preview) {

        // validate of input data
        validateBillingLedgerInputData(billingLedger);

        final Currency currencyFromBillingLedger = getCurrencyEntityFromBillingLedger(billingLedger);
        final Currency anspCurrency = currencyUtils.getAnspCurrency();

        // default target currency as invoice currency if not defined
        Currency targetCurrencyFromBillingLedger = getTargetCurrencyEntityFromBillingLedger(billingLedger);
        if (targetCurrencyFromBillingLedger == null && currencyFromBillingLedger != null) {
            targetCurrencyFromBillingLedger = currencyFromBillingLedger;
        }

        //if InvoiceExchangeToUsd is not previously set, calculate and set it
        if (billingLedger.getInvoiceExchange() == null) {
            final Double exchangeRate = currencyUtils.getExchangeRate (currencyFromBillingLedger, targetCurrencyFromBillingLedger, billingLedger.getInvoicePeriodOrDate());
            billingLedger.setInvoiceExchange(exchangeRate);
        }

        if (billingLedger.getInvoiceExchange() == null) {
            throw ExceptionFactory.getInvalidCurrencyRateException(BillingLedger.class, "invoiceExchangeToUsd");
        }

        // if InvoiceExchangeToAnsp is not previously set, calculate and set it
        if (billingLedger.getInvoiceExchangeToAnsp() == null) {
            final Double exchangeRateToAnsp = currencyUtils.getExchangeRate(currencyFromBillingLedger, anspCurrency,
                billingLedger.getInvoicePeriodOrDate());
            billingLedger.setInvoiceExchangeToAnsp(exchangeRateToAnsp);
        }

        // update invoiceAmount and amountOwing
        // amountOwing should equal invoice amount for a new invoice created
        truncateInvoiceAmountValue(billingLedger);
        billingLedger.setAmountOwing(billingLedger.getInvoiceAmount());

        // if amount is 0 set invoice state to PAID
        if (billingLedger.getInvoiceAmount() == 0) {
            billingLedger.setInvoiceStateType(InvoiceStateType.PAID.toValue());
        }

        // make sure InvoiceDateOfIssue is set
        // time should be 00:00. E.g. InvoiceDateOfIssue = 2017-04-12 00:00
        LocalDateTime invoiceDateTimeOfIssue = billingLedger.getInvoiceDateOfIssue();
        if (invoiceDateTimeOfIssue==null) {
            invoiceDateTimeOfIssue = LocalDateTime.now();
        }
        billingLedger.setInvoiceDateOfIssue(LocalDateTime.of(invoiceDateTimeOfIssue.toLocalDate(),LocalTime.of(0, 0)));

        // make sure PaymentDueDate is set
        // time should be 00:00. E.g. PaymentDueDate = 2017-04-12 00:00
        LocalDateTime paymentDueDate = billingLedger.getPaymentDueDate();
        if (paymentDueDate==null) {
        	paymentDueDate = LocalDateTime.now();
        }
        billingLedger.setPaymentDueDate(LocalDateTime.of(paymentDueDate.toLocalDate(),LocalTime.of(0, 0)));

        // make sure InvoicePeriodOrDate is set
        // time should be 00:00. E.g. InvoicePeriodOrDate = 2017-04-12 00:00
        LocalDateTime invoicePeriodOrDate = billingLedger.getInvoicePeriodOrDate();
        if (invoicePeriodOrDate==null) {
        	invoicePeriodOrDate = LocalDateTime.now();
        }
        billingLedger.setInvoicePeriodOrDate(LocalDateTime.of(invoicePeriodOrDate.toLocalDate(),LocalTime.of(0, 0)));

        // create BillingLedger record in DB
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        if (billingLedger.getInvoiceNumber() == null) {

            final String invoiceNumber;
            if(preview) {
                invoiceNumber = "N/A";
            } else {

        		invoiceNumber= invoiceSequenceNumberHelper.generator().nextInvoiceSequenceNumber(invoiceType);
            }
            billingLedger.setInvoiceNumber(invoiceNumber);
        }
        final String invoiceSeqNumberType = invoiceSequenceNumberHelper.generator().getInvoiceSequenceNumberType(invoiceType);
        billingLedger.setInvoiceSequenceNumberType(invoiceSeqNumberType);

        // add overdue invoice penalties if applicable to new billing ledger
        // the conditional penalty logic below seems to require billing ledger be persisted..
        BillingLedger savedBillingLedger = billingLedger;
        String applyInterestPenaltyOn = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON);
        if (allowApplyPenalty && !applyInterestPenaltyOn.equalsIgnoreCase("Invoice final payment")) {
            if (applyInterestPenaltyOn.equalsIgnoreCase("Daily")) {
                /*
                 * Attach penalties to the given invoice that are already applied to the account balance before.
                 */
                savedBillingLedger = overdueInvoiceService.attachInvoiceOverduePenaltyDaily(savedBillingLedger);
            } else {
                /*
                 * Calculate and apply penalty if applicable and change invoice amount and amount owing of the current invoice.
                 * If system configuration is set to calculate penalties daily, penalties will not be applied on new invoice generation.
                 */
                savedBillingLedger = overdueInvoiceService.applyInvoiceOverduePenalty(savedBillingLedger);
            }
        }

        // round billing ledger amounts to comply with rounding settings
        roundInvoiceAmountAndAmountOwing(savedBillingLedger);

        // persist billing ledger to comply with penalties logic below
        if(!preview) {
        	return saveBillingLedger(savedBillingLedger);
        } else {
            return savedBillingLedger;
        }
    }

    private void validateBillingLedgerInputData(BillingLedger billingLedger) {

        if (billingLedger==null) {
            throw new CustomParametrizedException("No BillingLedger data are provided");
        }

        //validate account
        Account accountFromBillingLedger = null;
        if (billingLedger.getAccount()!=null && billingLedger.getAccount().getId()!=null) {
            accountFromBillingLedger = accountRepository.getOne(billingLedger.getAccount().getId());
        }

        if (accountFromBillingLedger==null) {
            throw new CustomParametrizedException("Invalid account data", ACCOUNT);
        }

        //validate transaction currency
        Currency billingLedgerCurrency = getCurrencyEntityFromBillingLedger(billingLedger);
        if (billingLedgerCurrency==null) {
            throw new CustomParametrizedException("Invalid invoice currency", "invoice_currency");
        }

        //if aviation invoicing is set by FlightmovementCategory, then
        //invoice currency should be USD or be the same as FlightmovementCategory currency
        if (billingLedger.getFlightmovementCategory()!=null && billingLedger.getFlightmovementCategory().getEnrouteInvoiceCurrency()!=null) {
        	Currency flightmovementCategoryCurrency = billingLedger.getFlightmovementCategory().getEnrouteInvoiceCurrency();
	        if ( !(billingLedgerCurrency.getCurrencyCode().equals("USD") || billingLedgerCurrency.equals(flightmovementCategoryCurrency)) ) {
	            throw new CustomParametrizedException(
	                String.format(
	                    "Invoice currency (%s) should be USD or be the same as currency of the flightmovement category (%s)", billingLedgerCurrency.getCurrencyCode(),flightmovementCategoryCurrency.getCurrencyCode()),"currency");
	        }

        }

        if (billingLedger.getPaymentDueDate()==null || billingLedger.getPaymentDueDate().toLocalDate().isBefore(LocalDateTime.now().toLocalDate())) {
        	throw new CustomParametrizedException("Invalid payment due date", "payment_due_date");
        }
    }

    private Currency getCurrencyEntityFromBillingLedger(BillingLedger billingLedger) {
        Currency currency = null;

        if (billingLedger != null && billingLedger.getInvoiceCurrency()!=null && billingLedger.getInvoiceCurrency().getId()!=null) {
            currency = currencyRepository.getOne(billingLedger.getInvoiceCurrency().getId());
        }
        return currency;
    }

    private Currency getTargetCurrencyEntityFromBillingLedger(BillingLedger billingLedger) {
        Currency currency = null;

        if (billingLedger != null && billingLedger.getTargetCurrency()!=null && billingLedger.getTargetCurrency().getId()!=null) {
            currency = currencyRepository.getOne(billingLedger.getTargetCurrency().getId());
        }
        return currency;
    }

    /**
     * Truncate the amount value if it has more decimal places than the currency used
     * @param billingLedger the billing ledger with invoice amount
     */
    private void truncateInvoiceAmountValue(final BillingLedger billingLedger) {

        final Double invoiceAmount = billingLedger.getInvoiceAmount();
        if (invoiceAmount != null) {
            final Currency invoiceCurrency = currencyRepository.getOne(billingLedger.getInvoiceCurrency().getId());
            billingLedger.setInvoiceAmount(
                Calculation.truncate(
                    billingLedger.getInvoiceAmount(), invoiceCurrency.getDecimalPlaces()
                ));
        }
    }

    /**
     * Round the billing ledger amount values to system configuration settings for
     * applicable invoice types (AVIATION_IATA, AVIATION_NONIATA, NON_AVIATION, PASSENGER).
     *
     * @param billingLedger the billing ledger with invoice amount and amount owing
     */
    private void roundInvoiceAmountAndAmountOwing(final BillingLedger billingLedger) {

        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());

        boolean aviation;
        if (InvoiceType.AVIATION_IATA.equals(invoiceType) || InvoiceType.AVIATION_NONIATA.equals(invoiceType)
            || InvoiceType.PASSENGER.equals(invoiceType))
            aviation = true;
        else if (InvoiceType.NON_AVIATION.equals(invoiceType))
            aviation = false;
        else return;

        billingLedger.setInvoiceAmount(roundingUtils.calculateSingleRoundedValue(billingLedger.getInvoiceAmount(),
            billingLedger.getInvoiceCurrency(), aviation));
        billingLedger.setAmountOwing(roundingUtils.calculateSingleRoundedValue(billingLedger.getAmountOwing(),
            billingLedger.getInvoiceCurrency(), aviation));
    }

    /**
     * Upload InvoiceDocument and update the invoice record.
     */
    public BillingLedger uploadInvoiceDocument (final Integer id, final MultipartFile document)  throws IOException {
        final BillingLedger billingLedger = billingLedgerRepository.getOne(id);
        final byte[] file = document.getBytes();
        billingLedger.setInvoiceDocument(file);
        billingLedger.setInvoiceDocumentType(document.getContentType());

        BillingLedger savedBillingLedger = billingLedgerRepository.saveAndFlush(billingLedger);

        // run billing ledger service provider save methods if any
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            provider.save(savedBillingLedger);
        }

        return savedBillingLedger;
    }

    public BillingLedger approve (final Integer id) {
        final BillingLedger billingLedger = billingLedgerRepository.getOne(id);
        if (billingLedger != null) {
            final InvoiceStateType currentLevel = InvoiceStateType.forValue(billingLedger.getInvoiceStateType());
            final InvoiceStateType nextLevel = invoicesApprovalWorkflow.getNextStateType(currentLevel);
            if (nextLevel != null && nextLevel != InvoiceStateType.PAID) {
                checkMaximumAmountApprovalLimit(billingLedger);
                billingLedger.setInvoiceStateType(nextLevel.toValue());
                final BillingLedger savedBillingLedger = saveBillingLedger(billingLedger);
                if (InvoiceStateType.PUBLISHED.equals(nextLevel)) {
                    transactionService.createDebitTransactionByInvoice(savedBillingLedger,false);
                }

                // run billing ledger service provider approve methods if any
                for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
                    provider.approve(savedBillingLedger);
                }

                return savedBillingLedger;
            } else {
                ErrorVariables errorVariables = new ErrorVariables();
                errorVariables.addEntry("state", billingLedger.getInvoiceStateType());

                throw new ErrorDTO.Builder()
                    .setErrorMessage(ErrorConstants.ERR_CANNOT_UPDATE_STATE.toValue())
                    .setErrorMessageVariables(errorVariables)
                    .buildInvalidDataException();
            }
        }
        throw new EntityNotFoundException("Not found the invoice with ID " + id);
    }

    private BillingLedger doReject (final Integer id) {
        final BillingLedger billingLedger = billingLedgerRepository.getOne(id);

        if (billingLedger == null) {
            throw new EntityNotFoundException("Not found the invoice with ID " + id);
        }

        final boolean publishedInvoiceFromPointOfSale = checkIfPublishedInvoiceFromPointOfSale(billingLedger);

        if (!invoicesApprovalWorkflow.canBeVoid(billingLedger, publishedInvoiceFromPointOfSale)) {
            throw new ErrorDTO.Builder("Invoice state type is not \"new\" or \"approved\", cannot update to \"void\"")
                .buildInvalidDataException();
        }

        billingLedger.setInvoiceStateType(InvoiceStateType.VOID.toValue());
        final BillingLedger savedBillingLedger = saveBillingLedger(billingLedger);

        if (publishedInvoiceFromPointOfSale) {
            voidAviationPublishedCashInvoice(billingLedger.getId());
        } else {
            billingLedgerFlightUtility.updateFlightStatusToMatchInvoice(savedBillingLedger, this.systemConfigurationService.shouldDisplayPaxCharges());
        }


        boolean penaltyCheckedDaily = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON).equalsIgnoreCase("Daily");
        overdueInvoiceService.removeAttachedPenalties(id, penaltyCheckedDaily);

        return savedBillingLedger;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BillingLedger reject(final Integer id) {
        return doReject(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BillingLedger rejectPublishedInvoice(Integer id) {
        BillingLedger billingLedger = doReject(id);

        Double invoiceAmount = -billingLedger.getInvoiceAmount();
        Currency invoiceCurrency = billingLedger.getInvoiceCurrency();

        List<Integer> billingLedgersIds = new ArrayList<>();
        billingLedgersIds.add(billingLedger.getId());

        Set<ChargesAdjustment> chargesAdjustments = new HashSet<>();
        ChargesAdjustment adjustment = new ChargesAdjustment();
        adjustment.setDate(LocalDateTime.now());

        if (billingLedger.getInvoiceType().equals(AVIATION_NONIATA)) {
            List<FlightMovement> flightMovementList = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedger.getId());
            if (flightMovementList != null && !flightMovementList.isEmpty()) {
                FlightMovement fm = flightMovementList.get(0);
                String regNum = fm.getItem18RegNum() != null ? fm.getItem18RegNum() : "-";
                String flightId = String.format("%s/%s/%s/%s/%s-%s", fm.getFlightId(), fm.getDateOfFlight().toLocalDate(), fm.getDepTime(), regNum, fm.getDepAd(), fm.getDestAd());
                adjustment.setFlightId(flightId);
            }
        } else {
            List<InvoiceLineItem> lineItemsList = getLineItemsByInvoiceId(billingLedger.getId());
            if (lineItemsList != null && !lineItemsList.isEmpty()) {
                InvoiceLineItem lineItem = lineItemsList.get(0);
                adjustment.setAerodrome(lineItem.getAerodrome().getAerodromeName());
            }
        }
        adjustment.setChargeDescription("All charges");
        adjustment.setChargeAmount(invoiceAmount);
        chargesAdjustments.add(adjustment);

        Transaction transactionEntity = new Transaction();
        transactionEntity.setBillingLedgerIds(billingLedgersIds);
        transactionEntity.setAccount(billingLedger.getAccount());
        transactionEntity.setDescription("Voided invoice");
        transactionEntity.setTransactionType(transactionTypeService.findOneByName("credit"));
        transactionEntity.setAmount(invoiceAmount);
        transactionEntity.setPaymentAmount(invoiceAmount);
        transactionEntity.setChargesAdjustment(chargesAdjustments);
        transactionEntity.setCurrency(invoiceCurrency);
        transactionEntity.setPaymentCurrency(invoiceCurrency);
        transactionEntity.setPaymentExchangeRate(currencyUtils.getExchangeRate(transactionEntity.getPaymentCurrency(), transactionEntity.getCurrency(), LocalDateTime.now()));
        transactionEntity.setExported(false);
        transactionEntity.setPaymentMechanism(TransactionPaymentMechanism.adjustment);
        transactionEntity.setPaymentReferenceNumber("N/A");
        transactionService.createTransactionByUI(transactionEntity, false);
        return billingLedger;
    }

    /**
     *  Reset all invoice number of each Billing Ledger after a given date
     *
     * @param invoiceDate the date after which all billing ledgers should have their invoice number reset
     */
    public void resetInvoiceNumber(LocalDate invoiceDate) {
        for (BillingLedger billingLedger : billingLedgerRepository.findByAfterInvoiceDate(invoiceDate)) {
        	InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
            billingLedger.setInvoiceNumber(invoiceSequenceNumberHelper.generator().nextInvoiceSequenceNumber(invoiceType));
        }
    }

    private void voidAviationPublishedCashInvoice(final int billingLedgerId) {
        List<FlightMovement> relatedFlightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(billingLedgerId);
        String notes = "Invoice was voided at " + LocalDateTime.now().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT, FormatStyle.SHORT));
        relatedFlightMovements.forEach(fm -> flightMovementService.deleteFlightMovementFromUI(fm.getId(), notes, true));
    }

    private boolean checkIfPublishedInvoiceFromPointOfSale(final BillingLedger billingLedger) {
        return billingLedger.getPointOfSale() && billingLedger.getInvoiceStateType().equals(InvoiceStateType.PUBLISHED.toValue()) &&
            (billingLedger.getInvoiceType().equals(AVIATION_NONIATA) || billingLedger.getInvoiceType().equals("non-aviation")) &&
            billingLedger.getInvoiceAmount().equals(billingLedger.getAmountOwing());
    }

    private void checkMaximumAmountApprovalLimit (final BillingLedger billingLedger) {
        Double maxThreshold = 0.0;
        final String userName = SecurityUtils.getCurrentUserLogin();
        if (userName != null) {
            final User currentUser = userService.getUserByLogin(userName);
            final Collection<Role> roles = currentUser.getRoles();
            if (roles != null) {
                for (final Role role : roles) {
                    final Double threshold = role.getMaxDebitNoteAmountApprovalLimit();
                    if (threshold > maxThreshold) {
                        maxThreshold = threshold;
                    }
                }
            }
        }
        final Currency currencyFromBillingLedger =  getCurrencyEntityFromBillingLedger(billingLedger);
        final Currency anspCurrency = currencyUtils.getAnspCurrency();

        Double amount = billingLedger.getInvoiceAmount();
        if (currencyFromBillingLedger!=null && currencyFromBillingLedger.getId() != null && !currencyFromBillingLedger.getId().equals(anspCurrency.getId())) {
            Double exchangeRateToAnsp = billingLedger.getInvoiceExchangeToAnsp();
            if (exchangeRateToAnsp == null) {
                exchangeRateToAnsp = currencyUtils.getExchangeRate(currencyFromBillingLedger, anspCurrency, billingLedger.getInvoicePeriodOrDate());
            }
            amount = currencyUtils.convertCurrency(amount, exchangeRateToAnsp, anspCurrency.getDecimalPlaces());
        }

        if (amount > maxThreshold) {
            ErrorVariables errorVariables = new ErrorVariables();

            errorVariables.addEntry("amount", NumberUtils.formatCurrency(amount, anspCurrency));
            errorVariables.addEntry("currencyCode", anspCurrency.getCurrencyCode());
            errorVariables.addEntry("maxThreshold", NumberUtils.formatCurrency(maxThreshold, anspCurrency));

            throw new ErrorDTO.Builder()
                .setErrorMessage("The invoice amount of {{amount}}{{currencyCode}} exceeds the maximum approval limit of {{maxThreshold}}{{currencyCode}}")
                .setErrorMessageVariables(errorVariables)
                .buildInvalidDataException();
        }
    }

    public List<TransactionPayment> getAllAccountCreditPaymentsForInvoice(final Integer id) {
    	if (id==null) {
    		return new ArrayList<>();
    	}

    	return transactionPaymentRepository.getAllAccountCreditPaymentsForInvoice(id);
    }

    public List<InvoiceOverduePenalty> getAllPenaltiesAppliedToInvoice(final Integer id) {
    	if (id==null) {
    		return new ArrayList<>();
    	}

    	return invoiceOverduePenaltyRepository.getAllPenaltiesAppliedToInvoice(id);
    }

    public Double getTotalAmountForInvoicesByAccountIdAndCurrency(final Integer accountId, final Integer currencyId){
        return billingLedgerRepository.getTotalAmountForInvoicesByAccountIdAndCurrency(accountId, currencyId);
    }

    /**
     * Save billing ledger to repository and run save method for all service providers.
     *
     * @param billingLedger billing ledger to save
     * @return saved billing ledger
     */
    private BillingLedger saveBillingLedger(final BillingLedger billingLedger) {
        final BillingLedger savedBillingLedger = billingLedgerRepository.save(billingLedger);
        if (billingLedger.getChargesAdjustment() != null) {
            for (ChargesAdjustment chargesAdjustment : billingLedger.getChargesAdjustment()) {
                chargesAdjustment.setBillingLedger(billingLedger);
                chargesAdjustmentService.save(chargesAdjustment);
            }
        }

        if (billingLedger.getUnifiedTaxCharges() != null) {
            for (UnifiedTaxCharges unifiedTaxCharge : billingLedger.getUnifiedTaxCharges()) {
            	unifiedTaxCharge.setBillingLedger(billingLedger);
            	unifiedTaxChargesService.save(unifiedTaxCharge);
            }
        }
        return savedBillingLedger;
    }

    /**
     * Used solely for sending created flag to service providers that a billing ledger
     * has been created.
     *
     * This is required as other classes create billing ledgers in pieces and injecting into
     * the save method isn't reliably possible.
     *
     * @param billingLedger billing ledger that was created
     */
    public void created(BillingLedger billingLedger) {

        // run billing ledger service provider created methods if any
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            provider.created(billingLedger);
        }
    }

    /**
     * Used solely for sending created flag to service providers that a billing ledger with
     * associated flight movements has been created.
     *
     * This is required as other classes create billing ledgers in pieces and injecting into
     * the save method isn't reliably possible.
     *
     * @param billingLedger billing ledger that was created
     * @param flightMovements associated billing ledger flight movements
     */
    public void created(BillingLedger billingLedger, List<FlightMovement> flightMovements) {

        // run billing ledger service provider created methods if any
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            provider.created(billingLedger, flightMovements);
        }
    }

    /**
     * Returns true if one or more plugin service provider supports exporting of billing ledgers.
     *
     * @return true if active plugin service provider supports exporting
     */
    Boolean isExportSupport() {

        // return true only if one or more active plugin providers support exporting
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupport()) return true;
        }

        // if no active providers support exporting, return false
        return false;
    }

    Boolean isExportSupport(InvoiceType type) {

        // return true only if one or more active plugin providers support exporting type
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupport(type)) return true;
        }

        // if no active providers support exporting type, return false
        return false;
    }

    /**
     * Returns list of exportable invoice types.
     *
     * @return list of exportable types
     */
    private List<InvoiceType> exportableTypes() {

        // loop through each provider and get all supported types
        List<InvoiceType> supported = new ArrayList<>();
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {

            // get provider's exportable types and continue if null or empty
            List<InvoiceType> types = provider.exportableTypes();
            if (types == null || types.isEmpty())
                continue;

            for (InvoiceType type : types) {
                if (!supported.contains(type))
                    supported.add(type);
            }
        }
        return supported;
    }

    /**
     * Export billing ledger to service provider.
     *
     * @param billingLedger billing ledger to export
     */
    @Transactional
    public void export(final BillingLedger billingLedger) {

        // run billing ledger service provider export methods if any
        for (BillingLedgerServiceProvider provider : super.getPluginServiceProviders()) {
            if (provider.isExportSupport()) provider.export(billingLedger);
        }
    }

    public long countAllBillingLedgers() {
        return billingLedgerRepository.count();
    }

    private void applyStateFilters(
        final FiltersSpecification.Builder filterBuilder, final String invoiceState
    ) {
        if (invoiceState == null)
            return;

        switch (invoiceState) {

            case "unapproved":
                // unapproved: invoices that have state "NEW"
                filterBuilder.restrictOn(Filter.equals(INVOICE_STATE_TYPE, InvoiceStateType.NEW.toValue()));
                break;

            case "unpublished":
                // unpublished: invoices that have state "NEW" or "APPROVED"
                filterBuilder.restrictOn(Filter.orEqual(INVOICE_STATE_TYPE, InvoiceStateType.NEW.toValue(), InvoiceStateType.APPROVED.toValue()));
                break;

            case "unpaid":
                // unpaid: invoices that have state == "PUBLISHED"
                // only PUBLISHED invoices can be selected for payment and only these invoices can be considered unpaid
                filterBuilder.restrictOn(Filter.equals(INVOICE_STATE_TYPE, InvoiceStateType.PUBLISHED.toValue()));
                break;

            case "overdue":
                // overdue: invoices that have current_date > paymentDueDate and amountOwing > 0
                filterBuilder.restrictOn(Filter.lessThan(PAYMENT_DUE_DATE, LocalDate.now(ZoneId.of("UTC")).atStartOfDay()));
                filterBuilder.restrictOn(Filter.greaterThan(AMOUNT_OWING, 0));
                break;

            case "paid":
                // paid: invoices that have a invoice_state_type_id that matches paid
                filterBuilder.restrictOn(Filter.equals(INVOICE_STATE_TYPE, InvoiceStateType.PAID.toValue()));
                break;

            case "void":
                // void: invoices that have a invoice_state_type_id that matches void
                filterBuilder.restrictOn(Filter.equals(INVOICE_STATE_TYPE, InvoiceStateType.VOID.toValue()));
                break;

            default: // ignored
        }
    }

    //@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public void updateBillingLedgerByIdAndInvoiceAmount(Integer id, Double invoiceAmount) {
        billingLedgerRepository.updateBillingLedgerByIdAndInvoiceAmount(id, invoiceAmount);
    }

    public long countAllForSelfCareUser(Integer id) {
        return billingLedgerRepository.countAllForSelfCareUser(id);
    }

    public long countAllForSelfCareAccounts() {
        return billingLedgerRepository.countAllForSelfCareAccounts();
    }

    public List<BillingLedger> findIssuedInvoicesAccountsIdsByTypeAndDate (String invoiceType, Date fromDate, Date toDate ){
        return billingLedgerRepository.findIssuedInvoicesAccountsIdsByTypeAndDate(invoiceType, fromDate, toDate);
    }

	public List<Integer> findAccountsIdByTypeAndDate(String invoiceType, Date fromDate, Date toDate) {
		return billingLedgerRepository.findAccountsIdByTypeAndDate(invoiceType, fromDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), toDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
	}
	
}
