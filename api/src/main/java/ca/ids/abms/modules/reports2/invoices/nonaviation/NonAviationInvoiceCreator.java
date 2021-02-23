package ca.ids.abms.modules.reports2.invoices.nonaviation;

import static ca.ids.abms.util.MiscUtils.nvl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;

import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.plugins.kcaa.eaip.modules.requisitionnumbers.KcaaEaipRequisitionNumber;
import ca.ids.abms.util.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.aerodromes.AerodromeService;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.modules.billings.InvoiceLineItemMapper;
import ca.ids.abms.modules.billings.InvoiceLineItemViewModel;
import ca.ids.abms.modules.charges.ExternalChargeCategory;
import ca.ids.abms.modules.charges.RecurringCharge;
import ca.ids.abms.modules.charges.RecurringChargeService;
import ca.ids.abms.modules.charges.ServiceChargeCatalogue;
import ca.ids.abms.modules.charges.ServiceChargeCatalogueService;
import ca.ids.abms.modules.common.enumerators.BasisForCharge;
import ca.ids.abms.modules.common.enumerators.InvoiceCategory;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.currencies.CurrencyService;
import ca.ids.abms.modules.reports2.common.*;
import ca.ids.abms.modules.reports2.invoices.InvoiceReportUtility;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.modules.utilities.schedules.UtilitiesRangeBracket;
import ca.ids.abms.modules.utilities.schedules.UtilitiesSchedule;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillage;
import ca.ids.abms.modules.utilities.towns.UtilitiesTownsAndVillageService;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;

/**
 * Create invoice data, PDF doc, ledger and transaction; one at a time
 */
class NonAviationInvoiceCreator {

    /** List of invoice categories that can be used with monthly non-aviation invoices */
    static final List <InvoiceCategory> MONTHLY_INVOICE_CATEGORIES = Arrays.asList (InvoiceCategory.values());

    /** List of invoice categories that can be used with POS non-aviation invoices */
    static final List <InvoiceCategory> POS_INVOICE_CATEGORIES = Collections.singletonList(InvoiceCategory.POS);

    private static final Logger LOGGER = LoggerFactory.getLogger (NonAviationInvoiceCreator.class);

    private static final String INVOICE_CATEGORY = "invoiceCategory";
    private static final String MISSING_AMOUNT_IN_SERVICE_CHARGE = "Missing amount in service charge '%s'";
    private static final String USER_DISCOUNT_PERCENTAGE = "userDiscountPercentage";
    private static final String TWO_DECIMAL_PLACES = "%,.2f";

    private final ReportHelper reportHelper;
    private final BillingLedgerService billingLedgerService;
    private final NonAviationInvoiceDocumentCreator nonAviationInvoiceDocumentCreator;
    private final TransactionService transactionService;
    private final InvoiceSequenceNumberHelper.Generator invoiceSeqNumGen;
    private final ServiceChargeCatalogueService serviceChargeCatalogueService;
    private final RecurringChargeService recurringChargeService;
    private final UtilitiesTownsAndVillageService utilitiesTownsAndVillageService;
    private final InvoiceLineItemMapper invoiceLineItemMapper;
    private final AerodromeService aerodromeService;
    private final LocalDateTime ldtNow;
    private final boolean preview;
    private final InvoiceReportUtility invoiceReportUtility;
    private final CurrencyService currencyService;
    private final RoundingUtils roundingUtils;
    private final AccountExternalChargeCategoryService accountExternalChargeCategoryService;
    private final LocalDateTime endDateInclusive;
    private final BillingCenter billingCenter;
    private final InvoiceStateType initialLedgerState;
    private final User currentUser;
    private final DateTimeFormatter dateFormatter;
    private final Currency anspCurrency;
    private final Currency usdCurrency;
    private final Currency eurCurrency;
    private final CachedCurrencyConverter cachedCurrencyConverter;

    private final BillingOrgCode billingOrgCode;
    private final Boolean inverseCurrencyRate;
    private final SystemConfiguration applyPenaltyOn;

    private List<AccountExternalChargeCategory> accountExternalChargeCategories = null;

    @SuppressWarnings("squid:S00107")
    NonAviationInvoiceCreator (final ReportHelper reportHelper,
                               final BillingLedgerService billingLedgerService,
                               final NonAviationInvoiceDocumentCreator nonAviationInvoiceDocumentCreator,
                               final TransactionService transactionService,
                               final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                               final ServiceChargeCatalogueService serviceChargeCatalogueService,
                               final RecurringChargeService recurringChargeService,
                               final UtilitiesTownsAndVillageService utilitiesTownsAndVillageService,
                               final InvoiceLineItemMapper invoiceLineItemMapper,
                               final AerodromeService aerodromeService,
                               final LocalDateTime ldtNow,
                               final boolean preview,
                               final CurrencyUtils currencyUtils,
                               final boolean approvalWorkflow,
                               final CurrencyService currencyService,
                               final LocalDateTime endDateInclusive,
                               final RoundingUtils roundingUtils,
                               final SystemConfigurationService systemConfigurationService,
                               final AccountExternalChargeCategoryService accountExternalChargeCategoryService,
                               final BankCodeService bankCodeService,
                               final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders) {
        this.reportHelper = reportHelper;
        this.billingLedgerService = billingLedgerService;
        this.nonAviationInvoiceDocumentCreator = nonAviationInvoiceDocumentCreator;
        this.transactionService = transactionService;
        this.serviceChargeCatalogueService = serviceChargeCatalogueService;
        this.recurringChargeService = recurringChargeService;
        this.utilitiesTownsAndVillageService = utilitiesTownsAndVillageService;
        this.invoiceLineItemMapper = invoiceLineItemMapper;
        this.aerodromeService = aerodromeService;
        this.ldtNow = ldtNow;
        this.preview = preview;
        this.currencyService = currencyService;
        this.roundingUtils = roundingUtils;
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
        this.endDateInclusive = endDateInclusive;

        this.billingCenter = reportHelper.getBillingCenterOfCurrentUser();
        this.initialLedgerState = reportHelper.getInitialLedgerState(approvalWorkflow);
        this.currentUser = reportHelper.getCurrentUser();
        this.invoiceSeqNumGen = invoiceSequenceNumberHelper.generator();
        this.dateFormatter = reportHelper.getDateFormat();
        this.cachedCurrencyConverter = new CachedCurrencyConverter (currencyUtils, endDateInclusive);
        this.invoiceReportUtility = new InvoiceReportUtility(reportHelper, billingLedgerService, transactionService,
                systemConfigurationService, cachedCurrencyConverter, bankCodeService, aviationInvoiceChargeProviders);

        this.anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        this.usdCurrency = cachedCurrencyConverter.getUsdCurrency();
        this.eurCurrency = cachedCurrencyConverter.getEurCurrency();

        // moved for increased performance, duplicate calls to the database should be done only once
        this.billingOrgCode = systemConfigurationService.getBillingOrgCode();
        this.inverseCurrencyRate = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVERSE_CURRENCY_RATE);
        this.applyPenaltyOn = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON);
    }

    /**
     * Create a POS invoice for the given account and line items
     */
    @SuppressWarnings("squid:S00107") // Method has more parameters than 7 authorized
    NonAviationInvoice createPosInvoice (final Account account,
                                         final List <InvoiceLineItemViewModel> lineItemViewModelList,
                                         final ReportFormat reportFormat,
                                         final InvoicePaymentParameters payment,
                                         final List<KcaaAatisPermitNumber> permitNumbers,
                                         final List<KcaaEaipRequisitionNumber> requisitionNumbers,
                                         final String invoiceCurrencyCode,
                                         final boolean proforma) {
        return do_createInvoice (
            account,
            POS_INVOICE_CATEGORIES,
            lineItemViewModelList,
            null,
            "",
            reportFormat,
            payment,
            permitNumbers,
            requisitionNumbers,
            invoiceCurrencyCode,
            true,
            proforma
        );
    }

    /**
     * Validate a single POS line item
     */
    InvoiceLineItemViewModel validatePosLineItem (final Account account,
                                                  final InvoiceLineItemViewModel lineItem,
                                                  final String invoiceCurrencyCode) {
        final Currency invoiceCurrency = account.getInvoiceCurrency();
        final double exchangeRateToUsd = cachedCurrencyConverter.getExchangeRateToUsd (invoiceCurrency);
        final double exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);

        // Create/validate an entity
        final InvoiceLineItem validatedLineItemEntity = this.do_createLineItem(
            lineItem,
            account,
            POS_INVOICE_CATEGORIES,
            null,
            exchangeRateToUsd,
            exchangeRateToAnsp,
            getInvoiceCurrencyByCode(invoiceCurrencyCode));

        // Convert it to view/model and return
        return this.invoiceLineItemMapper.toViewModel (validatedLineItemEntity);
    }

    /**
     * Create a monthly invoice for the given account and line items
     */
    NonAviationInvoice createMonthlyInvoice (final Account account,
                                             final LocalDateTime ldtStart,
                                             final LocalDateTime ldtEnd,
                                             final List <InvoiceLineItemViewModel> lineItemViewModelList,
                                             final ReportFormat reportFormat) {

        // Find recurring charges that have already been included in another invoice for this account/month
        final Set <RecurringCharge> prohibitedRecurringCharges = do_findChargesIncludedInAccountInvoiceForPeriod (account, ldtStart, ldtEnd);

        // Suffix for the generated output document file name
        final String invoiceNameSuffix = String.format (" - %s", endDateInclusive.format(DateTimeFormatter.ofPattern("MMM yyyy")));

        // Create invoice + output document
        return do_createInvoice(
            account,
            MONTHLY_INVOICE_CATEGORIES,
            lineItemViewModelList,
            prohibitedRecurringCharges,
            invoiceNameSuffix,
            reportFormat,
            null,
            null,
            null,
            account.getInvoiceCurrency().getCurrencyCode(),
            false,
            false);
    }

    /**
     * Validate a single monthly line item
     */
    InvoiceLineItemViewModel validateMonthlyLineItem (final Account account,
                                                      final LocalDateTime ldtStart,
                                                      final LocalDateTime ldtEnd,
                                                      final InvoiceLineItemViewModel lineItem) {
        final Currency invoiceCurrency = account.getInvoiceCurrency();
        final double exchangeRateToUsd = cachedCurrencyConverter.getExchangeRateToUsd (invoiceCurrency);
        final double exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);

        // Find recurring charges that have already been included in another invoice for this account/month
        final Set <RecurringCharge> prohibitedRecurringCharges = do_findChargesIncludedInAccountInvoiceForPeriod (account, ldtStart, ldtEnd);

        // Create/validate an entity
        final InvoiceLineItem validatedLineItemEntity = this.do_createLineItem(
            lineItem,
            account,
            MONTHLY_INVOICE_CATEGORIES,
            prohibitedRecurringCharges,
            exchangeRateToUsd,
            exchangeRateToAnsp,
            account.getInvoiceCurrency());

        // Convert it to view/model and return
        return this.invoiceLineItemMapper.toViewModel (validatedLineItemEntity);
    }

    /**
     * Create payment for the invoice
     */
    void createPayment (final NonAviationInvoice invoice, final InvoicePaymentParameters payment) {
        // Create payment if necessary
        if (payment != null) {
            final Transaction transaction = this.do_createPayment (invoice.account(), invoice.billingLedger(), payment);
			final ReportDocument transactionReceiptDocument = this.transactionService
					.getReportDocumentFromTransaction(transaction);
            invoice.payment (transaction, transactionReceiptDocument);
        }
    }

    InvoiceStateType getInitialInvoiceStateType() {
        return initialLedgerState;
    }

    // ------------------------ private ----------------------------------

    /**
     * Create a line item from a line item view model.
     */
    private InvoiceLineItem do_createLineItem (final InvoiceLineItemViewModel vm,
                                               final Account account,
                                               final List <InvoiceCategory> allowedInvoiceCategories,
                                               final Set <RecurringCharge> prohibitedRecurringCharges,
                                               final double exchangeRateToUsd,
                                               final double exchangeRateToAnsp,
                                               final Currency invoiceCurrency) {

        final InvoiceLineItem lineItem = new InvoiceLineItem();
        final String accountName = String.format ("#%d[name=%s]", account.getId(), account.getName());

        // Service charge
        ServiceChargeCatalogue serviceCharge;
        String serviceChargeName;
        BasisForCharge basisForCharge;
        Currency currencyFromExternalDatabase = null;
        Currency serviceChargeCurrency;

        if (vm.getServiceChargeCatalogue() == null || vm.getServiceChargeCatalogue().getId() == null) {
            throw new CustomParametrizedException("Service charge is required", "serviceChargeCatalogue");
        }

        serviceCharge = serviceChargeCatalogueService.getOne (vm.getServiceChargeCatalogue().getId());
        serviceChargeName = String.format ("#%d[descr=%s]", serviceCharge.getId(), serviceCharge.getDescription());
        basisForCharge = BasisForCharge.forValue(serviceCharge.getChargeBasis());
        serviceChargeCurrency = serviceCharge.getCurrency();

        if (basisForCharge == null) {
            throw new CustomParametrizedException (
                String.format ("Invalid charge basis in service charge '%s'", serviceChargeName), "chargeBasis");
        }

        if (serviceCharge.getInvoiceCategory() == null) {
            throw new CustomParametrizedException (
                String.format ("Missing invoice category in service charge '%s'", serviceChargeName), INVOICE_CATEGORY);
        }

        final InvoiceCategory invoiceCategory = InvoiceCategory.forValue (serviceCharge.getInvoiceCategory());

        if (invoiceCategory == null) {
            throw new CustomParametrizedException (
                String.format ("Invalid invoice category `%s` in service charge %s",
                    serviceCharge.getInvoiceCategory(), serviceChargeName), INVOICE_CATEGORY);
        }

        if (!allowedInvoiceCategories.contains(invoiceCategory)) {
            throw new CustomParametrizedException (
                    String.format (
                        "Unexpected invoice category `%s` in service charge %s expecting  %s",
                            invoiceCategory.toValue(),
                            serviceChargeName,
                            allowedInvoiceCategories.stream()
                                .map(InvoiceCategory::toValue)
                                .collect(Collectors.toList())
                    ), INVOICE_CATEGORY
            );
        }
        lineItem.setServiceChargeCatalogue (serviceCharge);

        // Account
        lineItem.setAccount (account);

        // User Description
        lineItem.setUserDescription(vm.getUserDescription());

        // set account external system identifier
        lineItem.setAccountExternalSystemIdentifier(findAccountExternalSystemIdentifier(vm, account));

        // Aerodrome
        checkAerodrome(vm);

        final Aerodrome aerodrome = aerodromeService.getOne (vm.getAerodrome().getId());
        lineItem.setAerodrome (aerodrome);

        // Currency
        lineItem.setCurrency(invoiceCurrency);

        // Recurring charge
        if (vm.getRecurringCharge() != null && vm.getRecurringCharge().getId() != null) {
            if (prohibitedRecurringCharges == null) {
                throw new CustomParametrizedException ("Line items based on recurring charges are not allowed");
            }
            final RecurringCharge recurringCharge = recurringChargeService.getOne (vm.getRecurringCharge().getId());
            final String recurringChargeName = String.format ("#%d", recurringCharge.getId());
            validateRecurringCharge(recurringChargeName, recurringCharge, account, accountName, lineItem, serviceChargeName, prohibitedRecurringCharges);
            lineItem.setRecurringCharge (recurringCharge);
        }
        else if (prohibitedRecurringCharges != null) {
            throw new CustomParametrizedException ("Only line items based on recurring charges are allowed");
        }

        // basis from service charge, user-entered amounts and line item amount
        Double amount;
        switch (basisForCharge) {

        // fixed - line item cost is the service charge amount
        case FIXED:
            checkServiceChargeAmount(serviceCharge.getAmount(), serviceChargeName);
            amount = serviceCharge.getAmount();
            break;

        // price per unit: cost = userUnitAmount * serivce charge amount
        case UNIT:
            checkServiceChargeAmount(serviceCharge.getAmount(), serviceChargeName);
            checkUserUnitAmount(vm.getUserUnitAmount());
            lineItem.setUserUnitAmount(vm.getUserUnitAmount());
            amount = vm.getUserUnitAmount() * serviceCharge.getAmount();
            break;

        // percentage: user-entered based amount * service charge amount (in percent)
        case PERCENTAGE:
            checkServiceChargeAmount(serviceCharge.getAmount(), serviceChargeName);
            checkUserMarkupAmount(vm.getUserMarkupAmount());
            lineItem.setUserMarkupAmount(vm.getUserMarkupAmount());
            amount = vm.getUserMarkupAmount() * serviceCharge.getAmount() / 100.0d;
            break;

        // amount from external database
        case EXTERNAL_DATABASE:
            checkChargeAmount(vm.getAmount());
            currencyFromExternalDatabase = setCurrencyFromExternalDatabase(vm, invoiceCurrency);
            amount = vm.getAmount();
            break;

        // user entered price
        case USER:
            checkChargeAmount(vm.getUserPrice());
            lineItem.setUserPrice(vm.getUserPrice());
            amount = vm.getUserPrice();
            checkUserEnteredPrice(serviceCharge, amount, serviceChargeCurrency);
            break;

        // water meter reading
        case WATER:
            checkWaterMeterReading(vm);
            final UtilitiesTownsAndVillage town = this.utilitiesTownsAndVillageService.getOne(vm.getUserTown().getId());
            lineItem.setUserTown (town);
            lineItem.setUserWaterMeterReading(vm.getUserWaterMeterReading());
            amount = do_getWaterCharge (vm.getUserWaterMeterReading(), town);
            break;

        // electricity meter reading
        case COMMERCIAL_ELECTRIC:
        case RESIDENTIAL_ELECTRIC:
            checkElectricityMeterReading(vm);
            final UtilitiesTownsAndVillage townVillage = this.utilitiesTownsAndVillageService.getOne(vm.getUserTown().getId());
            lineItem.setUserTown (townVillage);
            lineItem.setUserElectricityMeterReading (vm.getUserElectricityMeterReading());
            amount = this.do_getElectricityCharge (basisForCharge, vm.getUserElectricityMeterReading(), townVillage);
            break;

        // subtract this percentage from subtotal at the end
        case DISCOUNT:
            checkUserDiscountPercentage(vm);
            lineItem.setUserDiscountPercentage(vm.getUserDiscountPercentage());
            // line item amount will be adjusted after calculating the total of all other items
            amount = 0.0d;
            break;

        default:
            throw new CustomParametrizedException ("Internal error - unexpected service charge basis");
        }

        // If the line item is populated by values from an external database
        // and that external database provides a currency
        Currency currencyToConvert = currencyFromExternalDatabase != null ? currencyFromExternalDatabase : serviceChargeCurrency;

        // Convert ANSP amount to account currency
        final Double amountConverted = cachedCurrencyConverter.convertCurrency (amount, currencyToConvert, invoiceCurrency);
        lineItem.setAmount (Calculation.truncate (amountConverted, invoiceCurrency != null ? invoiceCurrency.getDecimalPlaces() : 2));

        // exchange rates are set later
        lineItem.setExchangeRateToUsd(exchangeRateToUsd);
        lineItem.setExchangeRateToAnsp(exchangeRateToAnsp);

        return lineItem;
    }

    private void checkAerodrome(InvoiceLineItemViewModel vm) {
        if (vm.getAerodrome() == null || vm.getAerodrome().getId() == null) {
            throw new CustomParametrizedException ("Aerodrome is required", "aerodrome");
        }
    }

    private void checkUserDiscountPercentage(InvoiceLineItemViewModel vm) {
        if (vm.getUserDiscountPercentage() == null) {
            throw new CustomParametrizedException ("Discount percentage is required", USER_DISCOUNT_PERCENTAGE);
        }
        if (vm.getUserDiscountPercentage() < 0.0d) {
            throw new CustomParametrizedException ("Discount percentage can't be negative", USER_DISCOUNT_PERCENTAGE);
        }
        if (vm.getUserDiscountPercentage() > 100.0d) {
            throw new CustomParametrizedException ("Discount percentage must be <= 100", USER_DISCOUNT_PERCENTAGE);
        }
    }

    private void checkElectricityMeterReading(InvoiceLineItemViewModel vm) {
        if (vm.getUserElectricityMeterReading() == null) {
            throw new CustomParametrizedException ("Electricity meter reading is required", "userElectricityMeterReading");
        }
        if (vm.getUserElectricityMeterReading() < 0.0d) {
            throw new CustomParametrizedException ("Electricity meter reading can't be negative", "userElectricityMeterReading");
        }
        if (vm.getUserTown() == null || vm.getUserTown().getId() == null) {
            throw new CustomParametrizedException ("Town/village is required", "userTown");
        }
    }

    private void checkWaterMeterReading(InvoiceLineItemViewModel vm) {
        if (vm.getUserWaterMeterReading() == null) {
            throw new CustomParametrizedException ("Water meter reading is required", "userWaterMeterReading");
        }
        if (vm.getUserWaterMeterReading() < 0.0d) {
            throw new CustomParametrizedException ("Water meter reading can't be negative", "userWaterMeterReading");
        }
        if (vm.getUserTown() == null || vm.getUserTown().getId() == null) {
            throw new CustomParametrizedException ("Town/village is required", "userTown");
        }
    }

    private void checkUserEnteredPrice(ServiceChargeCatalogue serviceCharge, Double amount, Currency serviceChargeCurrency) {
        if (serviceCharge.getMinimumAmount() != null && amount < serviceCharge.getMinimumAmount()) {
            throw new CustomParametrizedException (
                String.format ("Charge amount must be >= '%s'",
                    reportHelper.formatCurrencyWithSymbol (serviceCharge.getMinimumAmount(), serviceChargeCurrency)));
        }
        if (serviceCharge.getMaximumAmount() != null && amount > serviceCharge.getMaximumAmount()) {
            throw new CustomParametrizedException (
                String.format ("Charge amount must be <= '%s'",
                    reportHelper.formatCurrencyWithSymbol (serviceCharge.getMaximumAmount(), serviceChargeCurrency)));
        }
    }

    private Currency setCurrencyFromExternalDatabase(final InvoiceLineItemViewModel vm, final Currency invoiceCurrency) {
        Currency currencyFromExternalDatabase = null;

        // Some items from external databases come in specific currency
        if (vm.getRequisition() != null && vm.getRequisition().getReqCurrency() != null) {
            // Requisitions (eAIP) can be either KES or USD
            currencyFromExternalDatabase = currencyService.findByCurrencyCode(vm.getRequisition().getReqCurrency());
        } else if (vm.getInvoicePermit() != null && vm.getInvoicePermit().getInvoicePermitNumber() != null) {
            // Invoice permits (AATIS) are USD but should be converted to the invoice currency
            currencyFromExternalDatabase =  invoiceCurrency;
        }
        return currencyFromExternalDatabase;
    }

    private void checkUserMarkupAmount(Double userMarkupAmount) {
        if (userMarkupAmount == null) {
            throw new CustomParametrizedException ("Base amount is required");
        }
    }

    private void checkUserUnitAmount(Double userUnitAmount) {
        if (userUnitAmount == null) {
            throw new CustomParametrizedException ("Unit amount is required");
        }
        if (userUnitAmount < 0.0d) {
            throw new CustomParametrizedException ("Unit amount can't be negative");
        }
    }

    private void checkChargeAmount(Double amount){
        if (amount == null) {
            throw new CustomParametrizedException ("Charge amount is required");
        }
    }

    private void checkServiceChargeAmount(Double amount, String serviceChargeName) {
        if (amount == null) {
            throw new CustomParametrizedException (String.format (MISSING_AMOUNT_IN_SERVICE_CHARGE, serviceChargeName));
        }
    }

    private void validateRecurringCharge(final String recurringChargeName,
                                         final RecurringCharge recurringCharge,
                                         final Account account,
                                         final String accountName,
                                         final InvoiceLineItem lineItem,
                                         final String serviceChargeName,
                                         final Set<RecurringCharge> prohibitedRecurringCharges) {
        if (!recurringCharge.getAccount().equals (account))
            throw new CustomParametrizedException (
                String.format ("Recurring charge %s does not belong to selected account '%s'",
                    recurringChargeName, accountName));
        if (!recurringCharge.getServiceChargeCatalogue().equals(lineItem.getServiceChargeCatalogue())) {
            throw new CustomParametrizedException (
                String.format ("Recurring charge %s does not match service charge '%s'",
                    recurringChargeName, serviceChargeName));
        }
        if (prohibitedRecurringCharges.contains (recurringCharge)) {
            throw new CustomParametrizedException (
                String.format ("Recurring charge %s has already been included in another invoice for the selected time period",
                    recurringChargeName));
        }
        if (recurringCharge.getStartDate().isAfter (endDateInclusive) || recurringCharge.getEndDate().isBefore (endDateInclusive)) {
            throw new CustomParametrizedException (
                String.format ("Recurring charge %s has start/end date is outside of this invoice's billing period",
                    recurringChargeName), "startDate", "endDate");
        }
    }

    @SuppressWarnings("squid:S00107")
    private NonAviationInvoice do_createInvoice (
            final Account account,
            final List <InvoiceCategory> allowedInvoiceCategories,
            final List <InvoiceLineItemViewModel> lineItemViewModelList,
            final Set <RecurringCharge> prohibitedRecurringCharges,
            final String invoiceNameSuffix,
            final ReportFormat reportFormat,
            final InvoicePaymentParameters payment,
            final List<KcaaAatisPermitNumber> permitNumbers,
            final List<KcaaEaipRequisitionNumber> requisitionNumbers,
            final String invoiceCurrencyCode,
            final boolean fromPointOfSale,
            final boolean proforma) {

        // convert line item view models to entities
        final Set <RecurringCharge> recurringChargeSet = new HashSet<> ();
        final List <InvoiceLineItem> lineItemList = new ArrayList<> (lineItemViewModelList.size());
        final Currency invoiceCurrency = getInvoiceCurrencyByCode(invoiceCurrencyCode);
        final double exchangeRateToUsd = cachedCurrencyConverter.getExchangeRateToUsd (invoiceCurrency);
        final double exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);

        lineItemViewModelList.forEach( vm -> {
            final InvoiceLineItem li = do_createLineItem (
                vm,
                account,
                allowedInvoiceCategories,
                prohibitedRecurringCharges,
                exchangeRateToUsd,
                exchangeRateToAnsp,
                invoiceCurrency);
            if (li.getRecurringCharge() != null) {
                if (recurringChargeSet.contains (li.getRecurringCharge())) {
                    final String rcName = String.format ("#%d", li.getRecurringCharge().getId());
                    throw new CustomParametrizedException (String.format ("Recurring charge %s specified more than once", rcName));
                }
                recurringChargeSet.add (li.getRecurringCharge());
            }
            lineItemList.add (li);
        });

        // Calculate total as well as the amounts of DISCOUNT items
        final double invoiceTotal = this.do_calculateDiscount(invoiceCurrency, lineItemList);
        final String realInvoiceNumber = this.invoiceSeqNumGen.nextInvoiceSequenceNumber(InvoiceType.NON_AVIATION);

        // Create billing ledger
        final BillingLedger billingLedger = do_createBillingLedger(
            account,
            invoiceTotal,
            realInvoiceNumber,
            lineItemList,
            exchangeRateToUsd,
            exchangeRateToAnsp,
            permitNumbers,
            requisitionNumbers,
            invoiceCurrency,
            proforma,
            fromPointOfSale);

        // Create invoice
        final NonAviationInvoiceData invoiceData = this.do_createInvoiceData(
            account,
            lineItemList,
            billingLedger,
            invoiceNameSuffix,
            payment,
            exchangeRateToUsd,
            exchangeRateToAnsp,
            fromPointOfSale,
            proforma);

        // Create PDF file
        final ReportDocument reportDocument = this.nonAviationInvoiceDocumentCreator.create (invoiceData, reportFormat);

        // Save PDF file in billing ledger
        reportHelper.setReportDocument (billingLedger, reportDocument);

        // done
        final NonAviationInvoice invoice = new NonAviationInvoice(account, invoiceData, billingLedger, reportDocument);

        // event trigger to indicate that a billing ledger was created
        // should only be triggered if NOT in preview mode
        if (!preview)
            billingLedgerService.created(billingLedger);

        return invoice;
    }

    private double do_calculateDiscount (final Currency invoiceCurrency, final List<InvoiceLineItem> lineItemList) {

        // Add up all invoice items except DISCOUNT
        double subTotal = 0;
        for (final InvoiceLineItem li: lineItemList) {
            final BasisForCharge basis = BasisForCharge.forValue (li.getServiceChargeCatalogue().getChargeBasis());
            if (!basis.equals(BasisForCharge.DISCOUNT)) {
                subTotal += li.getAmount();
            }
        }
        subTotal = Calculation.truncate (subTotal, invoiceCurrency.getDecimalPlaces());

        // Adjust discount amounts
        for (final InvoiceLineItem li: lineItemList) {
            final BasisForCharge basis = BasisForCharge.forValue (li.getServiceChargeCatalogue().getChargeBasis());
            if (basis.equals(BasisForCharge.DISCOUNT)) {
                li.setAmount(- Calculation.truncate (li.getUserDiscountPercentage() / 100.0d * subTotal, invoiceCurrency.getDecimalPlaces()));
            }
        }

        // Calculate invoice total; force to 0 if negative
        return Calculation.truncate (
                Math.max (0.0d, lineItemList.stream()
                    .map(InvoiceLineItem::getAmount)
                    .reduce(Double::sum)
                    .orElse(0.0d)),
                invoiceCurrency.getDecimalPlaces());
    }

    /**
     * Create raw invoice data; to be further formatted into PDF etc.
     */
    @SuppressWarnings("squid:S00107")
    private NonAviationInvoiceData do_createInvoiceData (final Account account,
                                                         final List <InvoiceLineItem> lineItemList,
                                                         final BillingLedger billingLedger,
                                                         final String invoiceNameSuffix,
                                                         final InvoicePaymentParameters payment,
                                                         final Double exchangeRateToUsd,
                                                         final Double exchangeRateToAnsp,
                                                         final boolean fromPointOfSale,
                                                         final boolean proforma) {

        final NonAviationInvoiceData x = new NonAviationInvoiceData();
        Currency currency = billingLedger.getInvoiceCurrency();

        x.global = new NonAviationInvoiceData.Global();
        x.global.realInvoiceNumber = billingLedger.getInvoiceNumber();
        x.global.invoiceNumber = reportHelper.getDisplayInvoiceNumber(x.global.realInvoiceNumber, preview);
        x.global.invoiceName = String.format ("%s %s%s", Translation.getLangByToken("Aviation invoice"),
        x.global.invoiceNumber, invoiceNameSuffix);
        x.global.invoiceIssueLocation = billingCenter.getName();
        x.global.invoiceDateStr = reportHelper.formatDateUtc (endDateInclusive, dateFormatter);
        x.global.invoiceDueDateStr = reportHelper.formatDateUtc(ldtNow.plusDays(account.getPaymentTerms()), dateFormatter);
        x.global.invoiceBillingPeriod = String.format("%s-%s", StringUtils.capitalize(endDateInclusive.getMonth().name().toLowerCase()), endDateInclusive.getYear());
        x.global.accountId = account.getId();
        x.global.accountName = account.getName();
        x.global.fromName = currentUser.getName();
        x.global.fromPosition = currentUser.getJobTitle();
        x.global.isCashAccount = account.getCashAccount();
        x.global.fromPointOfSale = fromPointOfSale;
        x.global.proforma = proforma;
        x.global.invoiceCurrencyCode = currency.getCurrencyCode();
        x.global.invoiceCurrencyAnspCode = anspCurrency.getCurrencyCode();
        x.global.invoiceCurrencyUsdCode = usdCurrency.getCurrencyCode();
        x.global.billingName = account.getAviationBillingContactPersonName();
        x.global.billingAddress = account.getAviationBillingMailingAddress();
        x.global.billingContactTel = account.getAviationBillingPhoneNumber();
        x.global.billingEmail = account.getAviationBillingEmailAddress();
        x.global.kraClerkName = payment != null ? payment.getKraClerkName() : "";
        x.global.kraReceiptNumber = payment != null ? payment.getKraReceiptNumber() : "";
        x.global.invoiceCurrencyInWords = currency.getCurrencyName() != null ? String.format ("%s%s", currency.getCurrencyName(), "s") : "";

        // bank accounts list
        x.bankAccountList = new ArrayList<>();
        x.bankAccountList.addAll(invoiceReportUtility.getBankAccountList(billingLedger.getBillingCenter()));

        x.lineItemList = lineItemList.stream().map (li-> {
            final NonAviationInvoiceData.LineItem lineItem = new NonAviationInvoiceData.LineItem();

            lineItem.id = li.getId();
            lineItem.aerodromeName = li.getAerodrome() == null ? null : li.getAerodrome().getAerodromeName();
            lineItem.dateStr = reportHelper.formatDateUtc(endDateInclusive, dateFormatter);
            lineItem.descr = li.getServiceChargeCatalogue().getDescription();
            lineItem.userDescription = li.getUserDescription();

            // set invoice line item amounts
            lineItem.amount = li.getAmount();
            lineItem.amountStr = reportHelper.formatCurrency(lineItem.amount, currency);
            lineItem.amountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (lineItem.amount, currency);

            lineItem.amountAnsp = cachedCurrencyConverter.toANSPCurrency(li.getAmount(), currency);
            lineItem.amountAnspStr = reportHelper.formatCurrency(lineItem.amountAnsp, anspCurrency);

            return lineItem;
        }).collect(Collectors.toList());

        x.additionalCharges = new ArrayList<>();
        x.accountCredits = new ArrayList<>();
        x.overduePenaltyInvoices = new ArrayList<>();

        x.accountCredits.addAll(invoiceReportUtility.getAppliedAccountCreditAsAdditionalChargeList(billingLedger));
        x.additionalCharges.addAll(invoiceReportUtility.getAppliedPenaltyAsAdditionalChargeList(billingLedger));
        x.overduePenaltyInvoices.addAll(invoiceReportUtility.getOverduePenaltyInvoiceList(billingLedger));

        // calculate total penalties from additional charges amount
        Double totalPenalties = x.additionalCharges.stream().filter(Objects::nonNull)
            .mapToDouble(c -> c.amount).sum();
        x.global.totalPenalty = String.format(TWO_DECIMAL_PLACES, totalPenalties);

        // calculate total account credit from account credits amount
        Double totalAccountCredit = Math.abs(x.accountCredits.stream().mapToDouble(c -> c.amount).sum());
        x.global.totalAccountCredit = String.format(TWO_DECIMAL_PLACES, totalAccountCredit);

        // when previewing an invoice - billing ledger is created but not saved that is why it doesn't
        // have an id, so an account credit cannot be found through a Transaction Payment
        if (preview && x.global.totalAccountCredit.equals("0.00")) {
            Double accountCredit = billingLedger.getAccountCredit();
            if (accountCredit != null) {
                x.global.totalAccountCredit = String.format(TWO_DECIMAL_PLACES, Math.abs(accountCredit));
            }
        }

        // calculate total outstanding amount from overdue invoice penalties
        Double totalOutstandingAmount = 0d;
        Double totalOutstandingAmountAnsp = 0d;
        for (OverduePenaltyInvoice overduePenaltyInvoice : x.overduePenaltyInvoices) {
            if (overduePenaltyInvoice == null)
                continue;
            totalOutstandingAmount += overduePenaltyInvoice.amountOwing;
            totalOutstandingAmountAnsp += overduePenaltyInvoice.amountOwingAnsp;
        }
        x.global.totalOutstandingAmount = String.format(TWO_DECIMAL_PLACES, totalOutstandingAmount);

        // set invoice total amounts
        Double totalAmount = billingLedger.getInvoiceAmount();
        Double totalAmountAnsp = cachedCurrencyConverter.toANSPCurrency(totalAmount, currency);
        Double totalAmountUsd = cachedCurrencyConverter.toUSDCurrency(totalAmount, currency);

        // update invoice data total amount and round appropriately if necessary
        x.global.totalAmount = roundingUtils.calculateSingleRoundedValue(totalAmount, currency, false);
        x.global.totalAmountStr = reportHelper.formatCurrency (x.global.totalAmount, currency);
        x.global.totalAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(x.global.totalAmount, currency);
        x.global.totalAmountAnsp = roundingUtils.calculateSingleRoundedValue(totalAmountAnsp, anspCurrency, false);
        x.global.totalAmountAnspStr = reportHelper.formatCurrency(x.global.totalAmountAnsp, anspCurrency);
        x.global.totalAmountUsd = roundingUtils.calculateSingleRoundedValue(totalAmountUsd, usdCurrency, false);
        x.global.totalAmountUsdStr = reportHelper.formatCurrency(x.global.totalAmountUsd, usdCurrency);

        // total amount in words: total amount spelled out in words using supported locales
        x.global.totalAmountStrInWords = reportHelper.formatAmountInFullText(
            x.global.totalAmount, LocaleUtils.ENGLISH);
        x.global.totalAmountStrInWordsSpanish = reportHelper.formatAmountInFullText(
            x.global.totalAmount, LocaleUtils.SPANISH);
        x.global.totalAmountStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.totalAmount, currency, LocaleUtils.ENGLISH);
        x.global.totalAmountStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.totalAmount, currency, LocaleUtils.SPANISH);

        // set invoice amount dues
        Double amountDue = billingLedger.getAmountOwing();
        Double amountDueAnsp = cachedCurrencyConverter.toANSPCurrency(amountDue, currency);
        Double amountDueUsd = cachedCurrencyConverter.toUSDCurrency(amountDue, currency);

        // KCAA must **DISPLAY** total outstanding amount in amount due
        // cannot be done within invoice template as sys config rounding is required
        if (billingOrgCode == BillingOrgCode.KCAA) {
            amountDue += totalOutstandingAmount;
            amountDueAnsp += totalOutstandingAmountAnsp;
            amountDueUsd += cachedCurrencyConverter.toUSDCurrency(totalOutstandingAmount, currency);
        }

        // update invoice data amount due and round appropriately if necessary
        x.global.amountDue = roundingUtils.calculateSingleRoundedValue(amountDue, currency, false);
        x.global.amountDueStr = reportHelper.formatCurrency(x.global.amountDue, currency);
        x.global.amountDueStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(x.global.amountDue, currency);
        x.global.amountDueAnsp = roundingUtils.calculateSingleRoundedValue(amountDueAnsp, anspCurrency, false);
        x.global.amountDueAnspStr = reportHelper.formatCurrency(x.global.amountDueAnsp, anspCurrency);
        x.global.amountDueUsd = roundingUtils.calculateSingleRoundedValue(amountDueUsd, usdCurrency, false);
        x.global.amountDueUsdStr = reportHelper.formatCurrency(x.global.amountDueUsd, usdCurrency);
        if (billingOrgCode == BillingOrgCode.INAC) {
            double owingInAlternativeCurr = cachedCurrencyConverter.convertCurrency(billingLedger.getAmountOwing(), currency, eurCurrency);
            x.global.amountDueAlt = roundingUtils.calculateSingleRoundedValue(owingInAlternativeCurr, eurCurrency, false);
            x.global.amountDueAltStr = reportHelper.formatCurrency(x.global.amountDueAlt, eurCurrency);
            x.global.amountDueAltStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (x.global.amountDueAlt, eurCurrency);
            x.global.invoiceCurrencyAltCode = eurCurrency.getCurrencyCode();
        }

        // amount due in words: amount due spelled out in words using supported locales
        x.global.amountDueStrInWords = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.ENGLISH);
        x.global.amountDueStrInWordsSpanish = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.SPANISH);
        x.global.amountDueStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.ENGLISH);
        x.global.amountDueStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.SPANISH);

        // deprecated because of misleading name - should use global.amountDueStrInWords
        x.global.totalAmountInWords = reportHelper.formatAmountInFullText(
            x.global.amountDue, LocaleUtils.ENGLISH);
        // deprecated because of misleading name - should use global.amountDueStrInWordsWithCurrencySymbol
        x.global.totalAmountInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            x.global.amountDue, currency, LocaleUtils.ENGLISH);

        if (exchangeRateToAnsp == null || exchangeRateToUsd == null ) {
            LOGGER.debug("Bad request: exchange rate to ANSP or exchange rate to USD is not set");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Exchange rate to ANSP or exchange rate to USD is not set"));
        }

        if (inverseCurrencyRate) {
            x.global.exchageRate = String.format("%.5f", exchangeRateToAnsp);
            x.global.exchageRateUsd = String.format("%.5f", 1/exchangeRateToUsd);
        } else {
            x.global.exchageRate = String.format("%.5f", 1/exchangeRateToAnsp);
            x.global.exchageRateUsd = String.format("%.5f", exchangeRateToUsd);
        }

        return x;
    }

    /**
     * Create a billing ledger record
     */
    @SuppressWarnings("squid:S00107")
    private BillingLedger do_createBillingLedger (final Account account,
                                                  final double invoiceTotal,
                                                  final String realInvoiceNumber,
                                                  final List <InvoiceLineItem> lineItemList,
                                                  final double exchangeRateToUsd,
                                                  final double exchangeRateToAnsp,
                                                  final List<KcaaAatisPermitNumber> permitNumbers,
                                                  final List<KcaaEaipRequisitionNumber> requisitionNumbers,
                                                  final Currency invoiceCurrency,
                                                  final boolean proforma,
                                                  final boolean createdFromPointOfSale) {

        final LocalDateTime dueDate = ldtNow.plusDays (account.getPaymentTerms());

        // create billing ledger
        BillingLedger bl = new BillingLedger();
        bl.setAccount(account);
        bl.setBillingCenter(currentUser != null ? currentUser.getBillingCenter() : null);
        bl.setInvoicePeriodOrDate (endDateInclusive);
        bl.setInvoiceType(InvoiceType.NON_AVIATION.toValue());
        bl.setInvoiceStateType (initialLedgerState.toValue());
        bl.setPaymentDueDate (dueDate);
        bl.setUser (currentUser);
        bl.setInvoiceAmount (invoiceTotal);
        bl.setInvoiceCurrency (invoiceCurrency);
        bl.setTargetCurrency (usdCurrency);
        bl.setInvoiceExchange (exchangeRateToUsd);
        bl.setInvoiceExchangeToAnsp (exchangeRateToAnsp);
        bl.setInvoiceDateOfIssue (ldtNow);
        bl.setExported (false);
        bl.setAmountOwing (invoiceTotal);
        bl.setInvoiceNumber (realInvoiceNumber);
        bl.setProforma(proforma);
        bl.setPointOfSale(createdFromPointOfSale);

        if (account.getCashAccount()) {
            bl.setPaymentMode(TransactionPaymentMechanism.cash.toString());
        } else {
            bl.setPaymentMode(TransactionPaymentMechanism.credit.toString());
        }

        final boolean allowApplyPenalty = this.applyPenaltyOn.getCurrentValue().equalsIgnoreCase("Next invoice");

        // Document name, type and content (PDF) will be set later, penalties will be applied and invoice total
        // amounts rounded based on system configuration settings
        final BillingLedger billingLedger = billingLedgerService.createBillingLedgerAndTransaction(bl, allowApplyPenalty, proforma, preview);

        // link line items to the billing ledger
        lineItemList.forEach(li->{
            li.setBillingLedger (billingLedger);
            li.setExchangeRateToUsd (billingLedger.getInvoiceExchange());
            li.setExchangeRateToAnsp (billingLedger.getInvoiceExchangeToAnsp());
        });

        // if proforma invoice TRUE - line items are not saved and debit transaction is not created
        if (!proforma) {
            billingLedger.setInvoiceLineItems (lineItemList);
        }

        // link extensions to this ledger entry
        handleTransactionExtensions(permitNumbers, requisitionNumbers, billingLedger);

        LOGGER.info ("Created non-aviation billing ledger record id={}, amountOwing={} {}",
                billingLedger.getId(),
                billingLedger.getAmountOwing(),
                invoiceCurrency.getCurrencyCode());
        return billingLedger;
    }

    /**
     * Create a payment transaction
     */
    private Transaction do_createPayment (final Account account,
                                          final BillingLedger billingLedger,
                                          final InvoicePaymentParameters invoicePaymentParameters) {

        final Transaction transaction = new Transaction();

        // account
        transaction.setAccount (account);

        transaction.setKraClerkName(invoicePaymentParameters.getKraClerkName());
        transaction.setKraReceiptNumber(invoicePaymentParameters.getKraReceiptNumber());

        // type = credit
        transaction.setTransactionType (reportHelper.getTransactionTypeCredit());

        // amount: must equal to invoice amount
        if (invoicePaymentParameters.getAmount() == null) {
            throw new CustomParametrizedException ("amount is missing", "amount");
        }

        final Double requiredAmount = roundingUtils.calculateSingleRoundedValue(billingLedger.getAmountOwing(), billingLedger.getInvoiceCurrency(), false);

        if (!requiredAmount.equals (invoicePaymentParameters.getAmount())) {
            throw new CustomParametrizedException ("invalid amount, expecting"+ "\"" + requiredAmount + "\"", "amountOwing");
        }
        transaction.setAmount(-invoicePaymentParameters.getAmount());

        // currency: must equal to invoice currency
        if (invoicePaymentParameters.getCurrency() != null && !invoicePaymentParameters.getCurrency().equals(billingLedger.getInvoiceCurrency())) {
            throw new CustomParametrizedException ("invalid currency, expecting" + "\"" + billingLedger.getInvoiceCurrency().getCurrencyCode() + "\" ", "currency");
        }
        transaction.setCurrency (billingLedger.getInvoiceCurrency());
        transaction.setExchangeRate(billingLedger.getInvoiceExchange());
        transaction.setTargetCurrency(billingLedger.getTargetCurrency());
        transaction.setExchangeRateToAnsp(billingLedger.getInvoiceExchangeToAnsp());

        // billing ledger id
        transaction.setBillingLedgerIds (Collections.singletonList(billingLedger.getId()));

        // description:
        transaction.setDescription(invoicePaymentParameters.getDescription());

        // payment mechanism
        TransactionPaymentMechanism mechanism = null;

        if (invoicePaymentParameters.getPaymentMechanism() != null) {
            switch (invoicePaymentParameters.getPaymentMechanism()) {
            case cash:
            case credit:
            case debit:
            case cheque:
            case wire:
                mechanism = invoicePaymentParameters.getPaymentMechanism();
                break;
            default:
                // ignored
                break;
            }
        }

        if (mechanism == null) {
            throw new CustomParametrizedException ("invalid payment mechanism, must be \"cash\", \"credit\", \"debit\", \"cheque\", or \"wire\"", "paymentMechanism");
        }

        transaction.setPaymentMechanism (mechanism);

        // ref num
        transaction.setPaymentReferenceNumber(invoicePaymentParameters.getPaymentReferenceNumber());

        if (mechanism.equals(TransactionPaymentMechanism.cash)) {
        	transaction.setPaymentReferenceNumber("N/A"); //BUG 72594
        }

        // exported
        transaction.setExported (nvl (invoicePaymentParameters.getExported(), false));

        // sequence number
        transaction.setReceiptNumber(this.invoiceSeqNumGen.nextReceiptSequenceNumber(mechanism));

        // set payment values
        transaction.setPaymentAmount(-invoicePaymentParameters.getPaymentAmount());
        transaction.setPaymentCurrency(invoicePaymentParameters.getPaymentCurrency());
        transaction.setPaymentExchangeRate(invoicePaymentParameters.getPaymentExchangeRate());

        // set bank account values
        transaction.setBankAccountName(invoicePaymentParameters.getBankAccountName());
        transaction.setBankAccountNumber(invoicePaymentParameters.getBankAccountNumber());
        transaction.setBankAccountExternalAccountingSystemId(invoicePaymentParameters.getBankAccountExternalAccountingSystemId());

        // create transaction and return result
        return transactionService.createCreditTransactionByPayments(transaction);
    }

    /**
     * Find recurring charges applicable to a non-aviation monthly invoice for the given account and period
     */
    private Set <RecurringCharge> do_findChargesIncludedInAccountInvoiceForPeriod (final Account account, final LocalDateTime ldtStart, final LocalDateTime ldtEnd) {
        return nvl (this.recurringChargeService.findChargesIncludedInAccountInvoiceForPeriod (account.getId(), ldtStart, ldtEnd), new HashSet());
    }

    private Double do_getElectricityCharge (final BasisForCharge basisForCharge, final double meterReading, final UtilitiesTownsAndVillage town) {
        final String townName = String.format ("%s", town.getTownOrVillageName());

        if (BasisForCharge.COMMERCIAL_ELECTRIC.equals(basisForCharge)) {
                if (town.getCommercialElectricityUtilitySchedule() == null) {
                    throw new CustomParametrizedException (
                            String.format ("Commercial electricity schedule is not defined for town/village '%s'", townName));
                }
                final Double charge = do_getUtilityCharge (meterReading, town.getCommercialElectricityUtilitySchedule());
                if (charge == null) {
                    throw new CustomParametrizedException (
                        String.format("Commercial electricity unit price is not defined for town/village '%s', " +
                            "meter reading = '%g'", townName, meterReading));
                }
                return charge;
        } else  if (BasisForCharge.RESIDENTIAL_ELECTRIC.equals(basisForCharge)) {
                if (town.getResidentialElectricityUtilitySchedule() == null) {
                    throw new CustomParametrizedException (
                            String.format ("Residential electricity schedule is not defined for town/village '%s'", townName));
                }
                final Double charge = do_getUtilityCharge (meterReading, town.getResidentialElectricityUtilitySchedule());
                if (charge == null) {
                    throw new CustomParametrizedException (
                            String.format (
                                "Residential electricity unit price is not defined for town/village" +
                                " %s, meter reading =%f", townName, meterReading));
                }
                return charge;
        } else {
            throw new CustomParametrizedException ("Invalid electricity charge type");
        }
    }

    private Double do_getWaterCharge (final double meterReading, final UtilitiesTownsAndVillage town) {
        final String townName = String.format ("%s", town.getTownOrVillageName());

        if (town.getWaterUtilitySchedule() == null) {
            throw new CustomParametrizedException (
                    String.format ("Water schedule is not defined for town/viallge '%s'", townName));
        }

        final Double charge = do_getUtilityCharge (meterReading, town.getWaterUtilitySchedule());

        if (charge == null) {
            throw new CustomParametrizedException (
                    String.format (
                        "Water unit price is not defined for town/village" +
                        " %s, " +
                        "meter reading" +
                        " =%f",
                            townName, meterReading));
        }
        return charge;
    }

    private Double do_getUtilityCharge (final double meterReading, final UtilitiesSchedule schedule) {
        if (schedule == null) {
            return null;
        }

        final Set<UtilitiesRangeBracket> allBrackets = schedule.getUtilitiesRangeBracket();
        if (allBrackets == null || allBrackets.isEmpty()) {
            return null;
        }

        // sort by range_top_end descending
        final List <UtilitiesRangeBracket> sortedBrackets = allBrackets.stream()
                .filter(s->s != null && s.getRangeTopEnd() != null && s.getUnitPrice() != null)
                .sorted((a,b)->b.getRangeTopEnd().compareTo(a.getRangeTopEnd()))
                .collect(Collectors.toList());

        UtilitiesRangeBracket matchingBracket = null;

        for (final UtilitiesRangeBracket bracket: sortedBrackets) {
            if (bracket.getRangeTopEnd() >= meterReading) {
                matchingBracket = bracket;
                continue;
            }
            break;
        }

        if (matchingBracket == null) {
            return null;
        }

        double charge = matchingBracket.getUnitPrice() * meterReading;

        if (schedule.getMinimumCharge() != null && charge < schedule.getMinimumCharge().doubleValue()) {
            charge = schedule.getMinimumCharge();
        }

        return charge;
    }

    //TODO:  logic should be handled inside a plugin provider
    private void handleTransactionExtensions(final List<KcaaAatisPermitNumber> permitNumbers,
                                             final List<KcaaEaipRequisitionNumber> requisitionNumbers,
                                             final BillingLedger billingLedger) {
        if (permitNumbers != null) {
            permitNumbers.forEach(invoicePermit ->
                invoicePermit.setBillingLedger(billingLedger));
            billingLedger.setKcaaAatisPermitNumbers(new HashSet<>(permitNumbers));
        }

        if (requisitionNumbers != null) {
            requisitionNumbers.forEach(requisitionNumber -> {
                requisitionNumber.setBillingLedger(billingLedger);
                Currency requisitionCurrency = currencyService.findByCurrencyCode(requisitionNumber.getReqCurrency());
                // convert requisition amount to match converted during invoice creation
                Double amountConverted = cachedCurrencyConverter.convertCurrency(
                    requisitionNumber.getReqTotalAmount(), requisitionCurrency, billingLedger.getInvoiceCurrency()
                );
                requisitionNumber.setReqTotalAmountConverted(amountConverted);
            });
            billingLedger.setKcaaEaipRequisitionNumbers(new HashSet<>(requisitionNumbers));
        }
    }

    /**
     * Find account external system identifier either from line item value or
     * account's external charge category.
     *
     * @param lineItem line item to parse
     * @param account account to parse
     * @return account external system identifier
     */
    private String findAccountExternalSystemIdentifier(final InvoiceLineItemViewModel lineItem, final Account account) {

        if (lineItem.getAccountExternalSystemIdentifier() != null
            && !lineItem.getAccountExternalSystemIdentifier().isEmpty())
            return lineItem.getAccountExternalSystemIdentifier();
        else if (lineItem.getServiceChargeCatalogue() == null
            || lineItem.getServiceChargeCatalogue().getExternalChargeCategory() == null)
            return null;

        if (this.accountExternalChargeCategories == null)
            accountExternalChargeCategories = accountExternalChargeCategoryService.findByAccount(account.getId());

        ExternalChargeCategory lineCategory = lineItem.getServiceChargeCatalogue().getExternalChargeCategory();
        for (AccountExternalChargeCategory accountCategory : accountExternalChargeCategories) {
            if (lineCategory.equals(accountCategory.getExternalChargeCategory()))
                return accountCategory.getExternalSystemIdentifier();
        }

        return null;
    }

    /**
     * Attempt to find invoice currency by provided invoice currency code. If not currency found, throws an error message.
     */
    private Currency getInvoiceCurrencyByCode(final String currencyCode) {
        // find invoice currency code and throw exception if not found
        final Currency invoiceCurrency = cachedCurrencyConverter.getCurrencyByCode(currencyCode);
        if (invoiceCurrency == null) {
            throw new CustomParametrizedException("Could not find invoice currency code '{}'.", currencyCode);
        }
        return invoiceCurrency;
    }
}
