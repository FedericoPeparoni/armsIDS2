package ca.ids.abms.plugins.caab.sage.utilities;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.charges.ExternalChargeCategory;
import ca.ids.abms.modules.charges.ExternalChargeCategoryService;
import ca.ids.abms.modules.charges.RecurringCharge;
import ca.ids.abms.modules.charges.ServiceChargeCatalogue;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.bankcode.BankCode;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.plugins.caab.sage.modules.charges.CaabSageExternalChargeCategoryConstants;
import ca.ids.abms.plugins.caab.sage.modules.distributioncode.DistributionCode;
import ca.ids.abms.plugins.caab.sage.modules.distributioncode.DistributionCodeService;
import ca.ids.abms.plugins.caab.sage.modules.system.CaabSageConfigurationItemName;
import ca.ids.abms.plugins.caab.sage.utilities.exceptions.CaabSageCustomerCodeException;
import ca.ids.abms.plugins.caab.sage.utilities.exceptions.CaabSageDistributionCodeException;
import ca.ids.abms.plugins.caab.sage.utilities.exceptions.CaabSageEntityType;
import ca.ids.abms.plugins.caab.sage.utilities.exceptions.CaabSageBankCodeException;
import ca.ids.abms.plugins.common.utilities.PluginMapperHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CaabSageMapperHelper extends PluginMapperHelper {

    private static final Logger LOG = LoggerFactory.getLogger(CaabSageMapperHelper.class);

    /**
     * DocumentNumber delimiter for InvoiceNumber and CustomerCode.
     */
    private static final String DOCUMENT_NUMBER_DELIMITER = "-";

    /**
     * Description delimiter for FlightMovement unique field values.
     */
    private static final String FLIGHT_MOVEMENT_DESCRIPTION_DELIMITER = ", ";

    private final AccountExternalChargeCategoryService accountExternalChargeCategoryService;
    private final BankCodeService bankCodeService;
    private final DistributionCodeService distributionCodeService;
    private final ExternalChargeCategoryService externalChargeCategoryService;
    private final SystemConfigurationService systemConfigurationService;
    private final UserService userService;

    public CaabSageMapperHelper(
        final AccountExternalChargeCategoryService accountExternalChargeCategoryService,
        final BankCodeService bankCodeService,
        final DistributionCodeService distributionCodeService,
        final ExternalChargeCategoryService externalChargeCategoryService,
        final SystemConfigurationService systemConfigurationService,
        final UserService userService
    ) {
        this.accountExternalChargeCategoryService = accountExternalChargeCategoryService;
        this.bankCodeService = bankCodeService;
        this.distributionCodeService = distributionCodeService;
        this.externalChargeCategoryService = externalChargeCategoryService;
        this.systemConfigurationService = systemConfigurationService;
        this.userService = userService;
    }

    // TODO: DB calls should be CACHED

    // region BankCode Getters

    public BankCode getBankCode(final Transaction transaction) {

        // transaction must exist with a valid currency
        if (transaction == null)
            throw CaabSageBankCodeException.notFound(CaabSageEntityType.TRANSACTION);
        else if (transaction.getPaymentCurrency() == null)
            throw CaabSageBankCodeException.notValid(CaabSageEntityType.CURRENCY, CaabSageEntityType.TRANSACTION,
                transaction.getReceiptNumber());

        // user must exist with a valid billing center
        User user = userService.getUserByLogin(transaction.getCreatedBy());
        if (user == null)
            throw CaabSageBankCodeException.notFound(CaabSageEntityType.USER);
        else if (user.getBillingCenter() == null)
            throw CaabSageBankCodeException.notValid(CaabSageEntityType.BILLING_CENTER, CaabSageEntityType.USER,
                transaction.getCreatedBy());

        // find and return bank code if exists, else illegal state exception is thrown
        return getBankCode(user.getBillingCenter(), transaction.getPaymentCurrency());
    }

    // endregion BankCode Getters

    // region CustomerCode Getters

    public String getCustomerCode(final BillingLedger billingLedger) {

        if (billingLedger == null || billingLedger.getAccount() == null)
            return null;

        // always return cash customer account when cach account
        if (billingLedger.getAccount().getCashAccount())
            return systemConfigurationService.getValue(CaabSageConfigurationItemName.CASH_CUSTOMER_ACCOUNT);

        // get external charge category based on invoice type
        ExternalChargeCategory externalChargeCategory = getExternalChargeCategory(billingLedger);

        // return external customer id based on account and external charge category
        return getExternalCustomerId(billingLedger.getAccount(), externalChargeCategory);
    }

    public String getCustomerCode(final Transaction transaction) {

        if (transaction == null || transaction.getAccount() == null || transaction.getTransactionType() == null ||
            transaction.getPaymentMechanism() == null)
            return null;

        // always return cash customer account when cash account
        if (transaction.getAccount().getCashAccount())
            return systemConfigurationService.getValue(CaabSageConfigurationItemName.CASH_CUSTOMER_ACCOUNT);

        // only credit adjustments are supported for transaction objects
        if (TransactionPaymentMechanism.adjustment != transaction.getPaymentMechanism() ||
            !transaction.getTransactionType().isCredit())
            return null;

        // return external customer id based on account and external charge category
        return getExternalCustomerId(transaction.getAccount(), getExternalChargeCategory(transaction));
    }

    public String getCustomerCode(final TransactionPayment transactionPayment) {

        // transaction payment billing ledger is required to determine customer code
        if (transactionPayment == null || transactionPayment.getBillingLedger() == null) {
            LOG.error("Customer code could not be determined as no applicable invoice could be found.");
            throw new IllegalArgumentException("Customer code could not be determined as no applicable invoice " +
                "could be found.");
        }

        return getCustomerCode(transactionPayment.getBillingLedger());
    }

    // endregion CustomerCode Getters

    // region DistributionCode Getters

    public DistributionCode getDistributionCode(final ChargesAdjustment source) {

        // attempt to get billing center from billing ledger (debit note) or transaction (credit note)
        final BillingCenter billingCenter = source.getBillingLedger() == null
            ? getBillingCenterByTransactionUser(source.getTransaction())
            : source.getBillingLedger().getBillingCenter();

        // attempt to get distribution code from charge code and billing center
        DistributionCode distributionCode = billingCenter == null ? null
            : distributionCodeService.findByChargeAndOperationCode(
                source.getExternalAccountingSystemIdentifier(), billingCenter.getExternalAccountingSystemIdentifier());

        // throw exception if not found as distribution code is required
        if (distributionCode == null)
            throw CaabSageDistributionCodeException.notValid(source, billingCenter);

        return distributionCode;
    }

    public DistributionCode getDistributionCode(final InvoiceLineItem source) {

        // attempt to get billing center from billing ledger
        final BillingCenter billingCenter = source.getBillingLedger() != null
            ? source.getBillingLedger().getBillingCenter() : null;

        // attempt to get charge code from service charge catalogue or recurring charge
        final String chargeCode = resolveChargeCode(source);

        // attempt to get distribution code from charge code and billing center
        DistributionCode distributionCode = billingCenter == null || chargeCode == null ? null
            : distributionCodeService.findByChargeAndOperationCode(
                chargeCode, billingCenter.getExternalAccountingSystemIdentifier());

        // throw exception if not found as distribution code is required
        if (distributionCode == null)
            throw CaabSageDistributionCodeException.notValid(source,  billingCenter, chargeCode);

        return distributionCode;
    }

    public DistributionCode getDistributionCode(final InvoiceOverduePenalty source) {

        // attempt to get billing center from invoice penalty is applied
        final BillingCenter billingCenter = source.getPenaltyAddedToInvoice() != null
            ? source.getPenaltyAddedToInvoice().getBillingCenter() : null;

        // attempt to get distribution code from charge code and billing center
        DistributionCode distributionCode = billingCenter == null ? null
            : distributionCodeService.findByChargeAndOperationCode(
                CaabSageMapperConstants.CHARGE_CODE_OTHER_INCOME, billingCenter.getExternalAccountingSystemIdentifier());

        // throw exception if not found as distribution code is required
        if (distributionCode == null)
            throw CaabSageDistributionCodeException.notValid(source, billingCenter,
                CaabSageMapperConstants.CHARGE_CODE_OTHER_INCOME);

        return distributionCode;
    }

    public DistributionCode getDistributionCode(final FlightMovement flightMovement, final String chargeCode,
                                                final BillingCenter billingCenterDefault) {

        // determine billing center based on flight movement OR default if undefined | US 92203:
        // BUG - CAAB - Cannot generate IATA invoice - no distribution code for code 1100 billing centre undefined
        BillingCenter billingCenter = flightMovement.getBillingCenter() == null ? billingCenterDefault
            : flightMovement.getBillingCenter();

        // distribution code based on charge code and operation code
        // operation code is the billing center external accounting system identifier
        DistributionCode distributionCode = billingCenter == null ? null
            : distributionCodeService.findByChargeAndOperationCode(
                chargeCode, billingCenter.getExternalAccountingSystemIdentifier());

        // throw exception if not found as distribution code is required
        if (distributionCode == null)
            throw CaabSageDistributionCodeException.notValid(CaabSageEntityType.FLIGHT_MOVEMENT, billingCenter,
                chargeCode, getFlightMovementDescription(flightMovement));

        return distributionCode;
    }

    // endregion DistributionCode Getters

    // region DocumentNumber Getters

    /**
     * Get document number from billing ledger.
     */
    public String getDocumentNumber(final BillingLedger billingLedger) {

        // define customer code by billing ledger
        String customerCode = getCustomerCode(billingLedger);

        // validate that customer code is not null or blank (whitespace)
        if (StringUtils.isBlank(customerCode)) handleCustomerCodeError(billingLedger);

        // return document number from customer code and invoice number
        return getDocumentNumber(billingLedger.getInvoiceNumber(), customerCode);
    }

    /**
     * Get document number from transaction.
     */
    public String getDocumentNumber(final Transaction transaction) {

        // define customer code by transaction
        String customerCode = getCustomerCode(transaction);

        // validate that customer code is not null or blank (whitespace)
        if (StringUtils.isBlank(customerCode)) handleCustomerCodeError(transaction);

        // return document number from customer code and invoice number
        return getDocumentNumber(transaction.getReceiptNumber(), customerCode);
    }

    /**
     * Get document number by concatenating invoice number and customer code.
     */
    public String getDocumentNumber(final String invoiceNumber, final String customerCode) {
        StringJoiner documentNumber = new StringJoiner(DOCUMENT_NUMBER_DELIMITER);
        documentNumber.add(invoiceNumber);
        documentNumber.add(customerCode);
        return documentNumber.toString();
    }

    // endregion DocumentNumber Getters

    // region FlightMovement Getters

    /**
     * Description is combination of flight movement unique identifier data.
     *
     * For example, a description would look similar to the following:
     * 'FlightId: ABC123 | DepAd: WXYZ | DepTime: 0900 | DateOfFlight: 31012018'
     */
    public String getFlightMovementDescription(final FlightMovement flightMovement) {

        StringJoiner description = new StringJoiner(FLIGHT_MOVEMENT_DESCRIPTION_DELIMITER);

        description.add("FlightId: " + flightMovement.getFlightId());
        description.add("DepAd: " + flightMovement.getActualDepAd());
        description.add("DepTime: " + flightMovement.getDepTime());
        description.add("DateOfFlight: " + dateFormat(flightMovement.getDateOfFlight()));

        return description.toString();
    }

    // endregion FlightMovement Getters

    // region CustomerCode Error Handling

    /**
     * Log customer code error and throw custom parametrized exception with appropriate message to prevent document
     * generation and exporting.
     */
    public void handleCustomerCodeError(final BillingLedger billingLedger) {
        throw CaabSageCustomerCodeException.notFound(billingLedger.getAccount(), getExternalChargeCategory(billingLedger));
    }

    /**
     * Log customer code error and throw custom parametrized exception with appropriate message to prevent document
     * generation and exporting.
     */
    public void handleCustomerCodeError(final Transaction transaction) {
        throw CaabSageCustomerCodeException.notFound(transaction.getAccount(), getExternalChargeCategory(transaction));
    }

    /**
     * Log customer code error and throw custom parametrized exception with appropriate message to prevent document
     * generation and exporting.
     */
    public void handleCustomerCodeError(final TransactionPayment transactionPayment) {
        throw CaabSageCustomerCodeException.notFound(transactionPayment.getTransaction().getAccount(),
            getExternalChargeCategory(transactionPayment));
    }

    // endregion CustomerCode Error Handling

    // region STATIC: Resolve FlightMovement Line Items

    /**
     * Create a map of charge type and charge amount to be used when creating line items.
     */
    public static Map<DistributionCode.FlightMovementChargeType, Double> resolveFlightMovementLineItems(
        final BillingLedger billingLedger, final FlightMovement flightMovement
    ) {
        Map<DistributionCode.FlightMovementChargeType, Double> charges = new EnumMap<>(DistributionCode.FlightMovementChargeType.class);
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        switch (invoiceType) {
            case AVIATION_IATA:
                charges.putAll(resolveFlightMovementIataCharges(billingLedger, flightMovement));
                break;
            case AVIATION_NONIATA:
                charges.putAll(resolveFlightMovementNonIataCharges(billingLedger, flightMovement));
                break;
            case PASSENGER:
                charges.putAll(resolveFlightMovementPassengerCharges(billingLedger, flightMovement));
                break;
            default:
                // do nothing
        }
        return charges;
    }

    // endregion STATIC: Resolve FlightMovement Line Items

    // region PRIVATE: BankCode Getters

    private BankCode getBankCode(final BillingCenter billingCenter, final Currency currency) {

        // arguments must never be null at runtime
        if (billingCenter == null || currency == null)
            throw new IllegalArgumentException("Arguments cannot be null.");

        // find and return bank code if exists
        BankCode bankCode = bankCodeService.findOne(billingCenter, currency);
        if (bankCode != null) return bankCode;

        // else throw bank code exception error
        throw CaabSageBankCodeException.notFound(billingCenter, currency);
    }

    // endregion PRIVATE: BankCode Getters

    // region PRIVATE: BillingCenter Getters

    private BillingCenter getBillingCenterByTransactionUser(final Transaction transaction) {
        if (transaction == null) return null;
        User user = userService.getUserByLogin(transaction.getCreatedBy());
        return user != null ? user.getBillingCenter() : null;
    }

    // endregion PRIVATE: BillingCenter Getters

    // region PRIVATE: ExternalChargeCategory Getters

    private ExternalChargeCategory getExternalChargeCategory(final BillingLedger billingLedger) {

        // get external charge category based on invoice type
        ExternalChargeCategory externalChargeCategory;
        InvoiceType invoiceType = InvoiceType.forValue(billingLedger.getInvoiceType());
        switch (invoiceType) {
            case AVIATION_IATA:
            case AVIATION_NONIATA:
            case PASSENGER:
                externalChargeCategory = getExternalChargeCategoryForAviation(billingLedger.getAccount());
                break;
            case DEBIT_NOTE:
                externalChargeCategory = getExternalChargeCategoryForAdjustments(
                    billingLedger.getChargesAdjustment(), billingLedger.getAccount());
                break;
            case NON_AVIATION:
                externalChargeCategory = getExternalChargeCategoryForNonAviation(billingLedger.getInvoiceLineItems());
                break;
            case OVERDUE:
                externalChargeCategory = getExternalChargeCategoryForOverdue();
                break;
            default:
                throw new IllegalStateException(String.format("External charge category could not be found as " +
                    "invoice type '%s' is not supported.", invoiceType));
        }

        // return external charge category
        return externalChargeCategory;
    }

    private ExternalChargeCategory getExternalChargeCategory(final Transaction transaction) {

        // return external charge category based on payment mechanism
        return getExternalChargeCategoryForAdjustments(transaction.getChargesAdjustment(), transaction.getAccount());
    }

    private ExternalChargeCategory getExternalChargeCategory(final TransactionPayment transactionPayment) {

        // return external charge category by billing ledger if exists
        if (transactionPayment == null || transactionPayment.getBillingLedger() == null)
            return null;
        return getExternalChargeCategory(transactionPayment.getBillingLedger());
    }

    private ExternalChargeCategory getExternalChargeCategoryForAdjustments(
        final Set<ChargesAdjustment> chargesAdjustments, final Account account
    ) {

        // charge adjustments MUST exist to determine external charge category
        if (chargesAdjustments == null || chargesAdjustments.isEmpty()) {
            LOG.error("External charge category could not be determined as no charge adjustments could be found.");
            throw new IllegalArgumentException("External charge category could not be determined as no charge " +
                "adjustments could be found.");
        }

        // credit and debit notes always be limited to one external charge category for all adjustments
        StringJoiner chargeAdjustments = new StringJoiner("', '");
        for (ChargesAdjustment item : chargesAdjustments) {

            // if aviation charge adjustment
            if (item.getFlightId() != null && !item.getFlightId().isEmpty())
                return getExternalChargeCategoryForAviation(account);

            // if non-aviation charge adjustment
            if (item.getAerodrome() != null && !item.getAerodrome().isEmpty() &&
                item.getExternalChargeCategoryName() != null && !item.getExternalChargeCategoryName().isEmpty())
                return externalChargeCategoryService.findByName(item.getExternalChargeCategoryName());

            // add to list in case we need to throw an illegal state exception
            if (StringUtils.isNotBlank(item.getChargeDescription()))
                chargeAdjustments.add(item.getChargeDescription());
        }

        // charge adjustment with an external charge category MUST exist to determine external charge category
        String description = chargeAdjustments.toString();
        LOG.error("No external charge category found for charge adjustment(s): '{}'.", description);
        throw new IllegalStateException(String.format("No external charge category found for charge adjustment(s): '%s'.",
            description));
    }

    private ExternalChargeCategory getExternalChargeCategoryForAviation(final Account account) {
        return account != null && account.getIataMember() != null && account.getIataMember()
            ? externalChargeCategoryService.findByName(CaabSageExternalChargeCategoryConstants.IATA)
            : externalChargeCategoryService.findByName(CaabSageExternalChargeCategoryConstants.ENROUTE);
    }

    private ExternalChargeCategory getExternalChargeCategoryForNonAviation(final List<InvoiceLineItem> invoiceLineItems) {

        // line items MUST exist to determine external charge category
        if (invoiceLineItems == null || invoiceLineItems.isEmpty()) {
            LOG.error("External charge category could not be determined as no line items could be found.");
            throw new IllegalArgumentException("External charge category could not be determined as no line items " +
                "could be found.");
        }

        // invoices should always be limited to one external charge category for all line items
        StringJoiner lineItems = new StringJoiner(", ");
        for (InvoiceLineItem item : invoiceLineItems) {

            ServiceChargeCatalogue catalogue = item.getServiceChargeCatalogue();

            // return first line item external charge category found
            if (catalogue != null && catalogue.getExternalChargeCategory() != null)
                return item.getServiceChargeCatalogue().getExternalChargeCategory();

            // add to list in case we need to throw an illegal state exception
            if (catalogue != null && StringUtils.isNotBlank(catalogue.getDescription()))
                lineItems.add(item.getServiceChargeCatalogue().getDescription());
        }

        // line item with an external charge category must exist to determine external charge category
        String description = lineItems.toString();
        LOG.error("No external charge category found for line items: {}.", description);
        throw new IllegalStateException(String.format("No external charge category found for line items: %s.",
            description));
    }

    private ExternalChargeCategory getExternalChargeCategoryForOverdue() {
        return externalChargeCategoryService.findByName(CaabSageExternalChargeCategoryConstants.AERONAUTICAL);
    }

    // endregion PRIVATE: ExternalChargeCategory Getters

    // region PRIVATE: ExternalCustomerId Getters

    private String getExternalCustomerId(final Account account, final ExternalChargeCategory externalChargeCategory) {

        if (account == null || externalChargeCategory == null)
            return null;

        List<AccountExternalChargeCategory> customerIds = accountExternalChargeCategoryService
            .findByAccount(account.getId(), externalChargeCategory.getId());

        for (AccountExternalChargeCategory item : customerIds) {
            if (item.getExternalSystemIdentifier() != null)
                return item.getExternalSystemIdentifier();
        }

        return null;
    }

    // endregion PRIVATE: ExternalCustomerId Getters

    // region PRIVATE STATIC: Charge Code Handling

    /**
     * Resolve charge code from invoice line item's service charge catalogue OR recurring charge.
     */
    private static String resolveChargeCode(final InvoiceLineItem invoiceLineItem) {
        if (invoiceLineItem == null)
            return null;

        final ServiceChargeCatalogue serviceChargeCatalogue = invoiceLineItem.getServiceChargeCatalogue();
        final RecurringCharge recurringCharge = invoiceLineItem.getRecurringCharge();

        String chargeCode;
        if (serviceChargeCatalogue != null && serviceChargeCatalogue.getExternalAccountingSystemIdentifier() != null &&
            !serviceChargeCatalogue.getExternalAccountingSystemIdentifier().isEmpty())
            chargeCode = serviceChargeCatalogue.getExternalAccountingSystemIdentifier();
        else if (recurringCharge != null && recurringCharge.getServiceChargeCatalogue() != null &&
            recurringCharge.getServiceChargeCatalogue().getExternalAccountingSystemIdentifier() != null &&
            !recurringCharge.getServiceChargeCatalogue().getExternalAccountingSystemIdentifier().isEmpty())
            chargeCode = recurringCharge.getServiceChargeCatalogue().getExternalAccountingSystemIdentifier();
        else
            chargeCode = null;

        return chargeCode;
    }

    // endregion PRIVATE STATIC: Charge Code Handling

    // region PRIVATE STATIC: FlightMovement Charge Handling

    private static void addFlightMovementCharge(final DistributionCode.FlightMovementChargeType type,
                                                final Map<DistributionCode.FlightMovementChargeType, Double> charges,
                                                final Double... amounts
    ) {
        double total = Arrays.stream(amounts).filter(Objects::nonNull)
            .mapToDouble(Double::doubleValue).sum();

        if (total > 0)
            charges.put(type, total);
    }

    private static Map<DistributionCode.FlightMovementChargeType, Double> resolveFlightMovementIataCharges(
        final BillingLedger billingLedger, final FlightMovement flightMovement
    ) {
        Integer billingLedgerId = billingLedger.getId();
        Map<DistributionCode.FlightMovementChargeType, Double> charges = new EnumMap<>(DistributionCode.FlightMovementChargeType.class);

        // add enroute charges if invoice id matches
        if (billingLedgerId.equals(flightMovement.getEnrouteInvoiceId())) {
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.ENROUTE_CHARGES, charges,
                flightMovement.getEnrouteCharges());
        }

        return charges;
    }

    /**
     * Create a map of charge type and charge amount to be used when creating line items for Non Iata invoices.
     *
     * @param flightMovement to parse for charges
     */
    private static Map<DistributionCode.FlightMovementChargeType, Double> resolveFlightMovementNonIataCharges(
        final BillingLedger billingLedger, final FlightMovement flightMovement
    ) {
        Integer billingLedgerId = billingLedger.getId();
        Map<DistributionCode.FlightMovementChargeType, Double> charges = new EnumMap<>(DistributionCode.FlightMovementChargeType.class);

        // add enroute charges if invoice id matches
        if (billingLedgerId.equals(flightMovement.getEnrouteInvoiceId())) {
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.ENROUTE_CHARGES, charges,
                flightMovement.getEnrouteCharges());
        }

        // add passenger charges if invoice id matches
        if (billingLedgerId.equals(flightMovement.getPassengerInvoiceId())) {
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.DOMESTIC_PASSENGER_CHARGES, charges,
                flightMovement.getDomesticPassengerCharges());
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.INTERNATIONAL_PASSENGER_CHARGES, charges,
                flightMovement.getInternationalPassengerCharges());
        }

        // add other charges if invoice id matches
        if (billingLedgerId.equals(flightMovement.getOtherInvoiceId())) {
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.LANDING_CHARGES, charges,
                flightMovement.getAerodromeCharges(), flightMovement.getApproachCharges(),
                flightMovement.getLateArrivalCharges(), flightMovement.getLateDepartureCharges(),
                flightMovement.getTaspCharge());
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.PARKING_CHARGES, charges,
                flightMovement.getParkingCharges());
        }

        return charges;
    }

    private static Map<DistributionCode.FlightMovementChargeType, Double> resolveFlightMovementPassengerCharges(
        final BillingLedger billingLedger, final FlightMovement flightMovement
    ) {
        Integer billingLedgerId = billingLedger.getId();
        Map<DistributionCode.FlightMovementChargeType, Double> charges = new EnumMap<>(DistributionCode.FlightMovementChargeType.class);

        // add enroute charges if invoice id matches
        if (billingLedgerId.equals(flightMovement.getEnrouteInvoiceId())) {
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.DOMESTIC_PASSENGER_CHARGES, charges,
                flightMovement.getDomesticPassengerCharges());
            addFlightMovementCharge(DistributionCode.FlightMovementChargeType.INTERNATIONAL_PASSENGER_CHARGES, charges,
                flightMovement.getInternationalPassengerCharges());
        }

        return charges;
    }

    // endregion PRIVATE STATIC: FlightMovement Charge Handling
}
