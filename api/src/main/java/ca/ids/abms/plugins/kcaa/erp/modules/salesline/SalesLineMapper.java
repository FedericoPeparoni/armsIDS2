package ca.ids.abms.plugins.kcaa.erp.modules.salesline;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategory;
import ca.ids.abms.modules.accounts.AccountExternalChargeCategoryService;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.reports2.common.CachedAerodromeResolver;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.plugins.kcaa.erp.modules.currency.KcaaErpCurrencyConstants;
import ca.ids.abms.plugins.kcaa.erp.modules.system.KcaaErpConfigurationItemName;
import ca.ids.abms.plugins.kcaa.erp.utilities.DefaultValue;
import ca.ids.abms.util.MiscUtils;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.StringJoiner;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class SalesLineMapper {

    // never set this currency in ERP database, blank is considered Kenya Shillings
    private static final String DEFAULT_CURRENCY_CODE = KcaaErpCurrencyConstants.BLANK_CURRENCY_CODE;

    @Autowired
    protected AccountExternalChargeCategoryService accountExternalChargeCategoryService;

    @Autowired
    protected ReportHelper reportHelper;

    @Autowired
    protected SystemConfigurationService systemConfigurationService;

    @Autowired
    protected UserService userService;

    @Mapping(target = "description", source = "chargeDescription")
    @Mapping(target = "type", constant = SalesLineConstants.TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesLineConstants.SHORTCUT_DIMENSION_CODE_1)
    public abstract SalesLine toSalesLine(ChargesAdjustment chargesAdjustment);

    @Mapping(target = "type", constant = SalesLineConstants.TYPE)
    @Mapping(target = "documentType", constant = SalesLineConstants.DEBIT_DOCUMENT_TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesLineConstants.SHORTCUT_DIMENSION_CODE_1)
    public abstract SalesLine toSalesLine(FlightMovement flightMovement);

    @Mapping(target = "documentNo", source = "billingLedger.invoiceNumber")
    @Mapping(target = "description", source = "serviceChargeCatalogue.description")
    @Mapping(target = "type", constant = SalesLineConstants.TYPE)
    @Mapping(target = "documentType", constant = SalesLineConstants.DEBIT_DOCUMENT_TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesLineConstants.SHORTCUT_DIMENSION_CODE_1)
    @Mapping(target = "amount", ignore = true)
    public abstract SalesLine toSalesLine(InvoiceLineItem invoiceLineItem);

    @Mapping(target = "documentNo", source = "penaltyAddedToInvoice.invoiceNumber")
    @Mapping(target = "shipmentDate", source = "penaltyAppliedDate")
    @Mapping(target = "type", constant = SalesLineConstants.TYPE)
    @Mapping(target = "documentType", constant = SalesLineConstants.DEBIT_DOCUMENT_TYPE)
    @Mapping(target = "shortcutDimension1Code", constant = SalesLineConstants.SHORTCUT_DIMENSION_CODE_1)
    public abstract SalesLine toSalesLine(InvoiceOverduePenalty invoiceOverduePenalty);

    public SalesLine toSalesLine(FlightMovement flightMovement, BillingLedger billingLedger,
                                 final CachedCurrencyConverter currencyConverter,
                                 final CachedAerodromeResolver aerodromeResolver) {
        SalesLine salesLine = toSalesLine(flightMovement);

        toSalesLineCurrency(flightMovement, salesLine, billingLedger);
        toSalesLineCustomerNo(flightMovement, salesLine, billingLedger);
        toSalesLineDescription(flightMovement, salesLine, aerodromeResolver);
        toSalesLineDocumentNo(flightMovement, salesLine, billingLedger);
        toSalesLineShortcutDimensionCode(flightMovement, salesLine, billingLedger);
        toSalesLineUnitAmount(flightMovement, salesLine, billingLedger, currencyConverter);

        return salesLine;
    }

    @AfterMapping
    void toSalesLineCurrency(final ChargesAdjustment source, @MappingTarget SalesLine target) {

        // leave bank if KES currency
        String currencCode;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null
            && source.getBillingLedger().getInvoiceCurrency() != null
            && source.getBillingLedger().getInvoiceCurrency().getCurrencyCode() != null
            && !source.getBillingLedger().getInvoiceCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE)
            && source.getBillingLedger().getInvoiceCurrency().getExternalAccountingSystemIdentifier() != null)
            currencCode = source.getBillingLedger().getInvoiceCurrency().getExternalAccountingSystemIdentifier();

        // else if transaction must be credit note
        else if (source.getTransaction() != null
            && source.getTransaction().getCurrency() != null
            && source.getTransaction().getCurrency().getCurrencyCode() != null
            && !source.getTransaction().getCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE)
            && source.getTransaction().getCurrency().getExternalAccountingSystemIdentifier() != null)
            currencCode = source.getTransaction().getCurrency().getExternalAccountingSystemIdentifier();

        else
            currencCode = DefaultValue.STRING;

        target.setCurrencyCode(currencCode);
    }

    private void toSalesLineCurrency(final FlightMovement source, SalesLine target, BillingLedger billingLedger) {

        // set currency code if exists and not KES
        if (source != null
            && billingLedger != null
            && billingLedger.getInvoiceCurrency() != null
            && billingLedger.getInvoiceCurrency().getCurrencyCode() != null
            && !billingLedger.getInvoiceCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE)
            && billingLedger.getInvoiceCurrency().getExternalAccountingSystemIdentifier() != null)
            target.setCurrencyCode(billingLedger.getInvoiceCurrency().getExternalAccountingSystemIdentifier());
        else
            target.setCurrencyCode(DefaultValue.STRING);
    }

    @AfterMapping
    void toSalesLineCurrency(final InvoiceLineItem source, @MappingTarget SalesLine target) {

        // leave blank if KES currency
        String currencyCode;

        if (source.getCurrency() != null
            && source.getCurrency().getCurrencyCode() != null
            && !source.getCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE)
            && source.getCurrency().getExternalAccountingSystemIdentifier() != null)
            currencyCode = source.getCurrency().getExternalAccountingSystemIdentifier();
        else
            currencyCode = DefaultValue.STRING;

        target.setCurrencyCode(currencyCode);
    }

    @AfterMapping
    void toSalesLineCurrency(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {

        // leave blank if KES currency
        String currencyCode;

        if (source.getPenaltyAddedToInvoice() != null
            && source.getPenaltyAddedToInvoice().getInvoiceCurrency() != null
            && !source.getPenaltyAddedToInvoice().getInvoiceCurrency().getCurrencyCode().equals(DEFAULT_CURRENCY_CODE)
            && source.getPenaltyAddedToInvoice().getInvoiceCurrency().getExternalAccountingSystemIdentifier() != null)
            currencyCode = source.getPenaltyAddedToInvoice().getInvoiceCurrency().getExternalAccountingSystemIdentifier();
        else
            currencyCode = DefaultValue.STRING;

        target.setCurrencyCode(currencyCode);
    }

    @AfterMapping
    void toSalesLineCustomerNo(final ChargesAdjustment source, @MappingTarget SalesLine target) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = null;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null
            && source.getBillingLedger().getAccount() != null)
            customerNo = findCustomerNo(source.getBillingLedger().getAccount());

        // else if transaction, must be credit note
        else if (source.getTransaction() != null
            && source.getTransaction().getAccount() != null)
            customerNo = findCustomerNo(source.getTransaction().getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setSellToCustomerNo(customerNo);
        target.setBillToCustomerNo(customerNo);
    }

    private void toSalesLineCustomerNo(final FlightMovement source, SalesLine target, BillingLedger billingLedger) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = null;

        // must be source and billing ledger
        if (source != null && billingLedger != null)
            customerNo = findCustomerNo(billingLedger.getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setBillToCustomerNo(customerNo);
        target.setSellToCustomerNo(customerNo);
    }

    @AfterMapping
    void toSalesLineCustomerNo(final InvoiceLineItem source, @MappingTarget SalesLine target) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = findCustomerNo(source.getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setSellToCustomerNo(customerNo);
        target.setBillToCustomerNo(customerNo);
    }

    @AfterMapping
    void toSalesLineCustomerNo(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {

        // attempt to get customer no from account external identifier if exists
        String customerNo = null;

        // must be penalty added to invoice
        if (source.getPenaltyAddedToInvoice() != null)
            customerNo = findCustomerNo(source.getPenaltyAddedToInvoice().getAccount());

        // set to default string value if none found or empty
        if (customerNo == null || customerNo.isEmpty())
            customerNo = DefaultValue.STRING;

        // set necessary customer no related values
        target.setSellToCustomerNo(customerNo);
        target.setBillToCustomerNo(customerNo);
    }

    @AfterMapping
    void toSalesLineDates(final ChargesAdjustment source, @MappingTarget SalesLine target) {
        LocalDateTime date;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null
            && source.getBillingLedger().getInvoiceDateOfIssue() != null)
            date = source.getBillingLedger().getInvoiceDateOfIssue().toLocalDate().atStartOfDay();

        // else if transaction, must be credit note
        else if (source.getTransaction() != null
            && source.getTransaction().getTransactionDateTime() != null)
            date = source.getTransaction().getTransactionDateTime().toLocalDate().atStartOfDay();

        else
            date = DefaultValue.LOCAL_DATE_TIME.toLocalDate().atStartOfDay();

        target.setShipmentDate(date);
    }

    @AfterMapping
    void toSalesLineDates(final FlightMovement source, @MappingTarget SalesLine target) {
        LocalDateTime date;
        if (source.getBillingDate() != null)
            date = source.getBillingDate().toLocalDate().atStartOfDay();
        else
            date = DefaultValue.LOCAL_DATE_TIME.toLocalDate().atStartOfDay();
        target.setShipmentDate(date);
    }

    @AfterMapping
    void toSalesLineDates(final InvoiceLineItem source, @MappingTarget SalesLine target) {
        LocalDateTime date;
        if (source.getBillingLedger() != null
            && source.getBillingLedger().getInvoiceDateOfIssue() != null)
            date = source.getBillingLedger().getInvoiceDateOfIssue().toLocalDate().atStartOfDay();
        else
            date = DefaultValue.LOCAL_DATE_TIME.toLocalDate().atStartOfDay();
        target.setShipmentDate(date);
    }

    private void toSalesLineDescription(final FlightMovement source, SalesLine target, final CachedAerodromeResolver cachedAerodromeResolver) {

        // get flight details
        StringJoiner details = new StringJoiner(", ");
        if (source.getFlightId() != null)
            details.add("Flight Id: " + source.getFlightId());
        if (source.getDepAd() != null || source.getItem18Dep() != null || source.getDepTime() != null || source.getActualDepartureTime() != null)
            details.add("Dep: " + formatAdTime(source.getDepAd(), source.getItem18Dep(), source.getDepTime(),
                source.getActualDepartureTime(), cachedAerodromeResolver));
        if (source.getDestAd() != null || source.getItem18Dest() != null || source.getArrivalTime() != null)
            details.add("Arr: " + formatAdTime(source.getDestAd(), source.getItem18Dest(), source.getArrivalTime(),
                null, cachedAerodromeResolver));

        // set flight description from details and date
        String description = details.toString();
        if (source.getDateOfFlight() != null)
            description += " (" + reportHelper.formatDateUtc(source.getDateOfFlight(), reportHelper.getDateFormat()) + ")";

        // set sales line description
        target.setDescription(description);
    }

    @AfterMapping
    void toSalesLineDescription(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {
        String description = "Penalty for overdue invoice";
        if (source.getPenalizedInvoice() != null
            && source.getPenalizedInvoice().getInvoiceNumber() != null)
            description += " - #" + source.getPenalizedInvoice().getInvoiceNumber();
        target.setDescription(description);
    }

    @AfterMapping
    void toSalesLineDocumentNo(final ChargesAdjustment source, @MappingTarget SalesLine target) {
        String documentNo;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null
            && source.getBillingLedger().getInvoiceNumber() != null)
            documentNo = source.getBillingLedger().getInvoiceNumber();

        // else if transaction, must be credit note
        else if (source.getTransaction() != null
            && source.getTransaction().getReceiptNumber() != null)
            documentNo = source.getTransaction().getReceiptNumber();

        else
            documentNo = DefaultValue.STRING;

        target.setDocumentNo(documentNo);

    }

    private void toSalesLineDocumentNo(final FlightMovement source, SalesLine target, BillingLedger billingLedger) {
        if (source != null && billingLedger != null)
            target.setDocumentNo(billingLedger.getInvoiceNumber());
    }

    @AfterMapping
    void toSalesLineDocumentType(final ChargesAdjustment source, @MappingTarget SalesLine target) {
        String documentType;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null)
            documentType = SalesLineConstants.DEBIT_DOCUMENT_TYPE;

        // else if transaction, must be credit note
        else if (source.getTransaction() != null)
            documentType = SalesLineConstants.CREDIT_DOCUMENT_TYPE;

        else
            documentType = DefaultValue.STRING;

        target.setDocumentType(new Integer(documentType));
    }

    @AfterMapping
    void toSalesLineNo(final ChargesAdjustment source, @MappingTarget SalesLine target) {
        target.setNo(
            source.getExternalAccountingSystemIdentifier() != null
                && !source.getExternalAccountingSystemIdentifier().isEmpty()
            ? source.getExternalAccountingSystemIdentifier()
                : systemConfigurationService.getValue(KcaaErpConfigurationItemName.ENROUTE_FEES_ID));
    }

    @AfterMapping
    void toSalesLineNo(final FlightMovement source, @MappingTarget SalesLine target) {
        target.setNo(systemConfigurationService.getValue(KcaaErpConfigurationItemName.ENROUTE_FEES_ID));
    }

    @AfterMapping
    void toSalesLineNo(final InvoiceLineItem source, @MappingTarget SalesLine target) {
        String no;
        if (source.getServiceChargeCatalogue() != null && source.getServiceChargeCatalogue().getExternalAccountingSystemIdentifier() != null)
            no = source.getServiceChargeCatalogue().getExternalAccountingSystemIdentifier();
        else
            no = DefaultValue.STRING;
        target.setNo(no);
    }

    @AfterMapping
    void toSalesLineNo(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {
        target.setNo(systemConfigurationService.getValue(KcaaErpConfigurationItemName.ENROUTE_FEES_ID));
    }

    @AfterMapping
    void toSalesLineShortcutDimensionCode(final ChargesAdjustment source, @MappingTarget SalesLine target) {

        // set shortcut dimension code by user's billing center external account system identifier
        User user = null;

        // if billing ledger, must be debit note
        if (source.getBillingLedger() != null)
            user = source.getBillingLedger().getUser();

        // else if transaction, must be credit note
        else if (source.getTransaction() != null)
            user = userService.getUserByLogin(source.getTransaction().getCreatedBy());

        String code2;
        if (user != null &&
            user.getBillingCenter() != null &&
            user.getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            code2 = user.getBillingCenter().getExternalAccountingSystemIdentifier();
        else
            code2 = DefaultValue.STRING;
        target.setShortcutDimension2Code(code2);
    }

    private void toSalesLineShortcutDimensionCode(final FlightMovement source, SalesLine target, BillingLedger billingLedger) {
        if (source != null
            && billingLedger != null
            && billingLedger.getUser() != null
            && billingLedger.getUser().getBillingCenter() != null
            && billingLedger.getUser().getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            target.setShortcutDimension2Code(billingLedger.getUser().getBillingCenter().getExternalAccountingSystemIdentifier());
        else
            target.setShortcutDimension2Code(DefaultValue.STRING);
    }

    @AfterMapping
    void toSalesLineShortcutDimensionCode(final InvoiceLineItem source, @MappingTarget SalesLine target) {
        String code2;
        if (source.getBillingLedger() != null &&
            source.getBillingLedger().getUser() != null &&
            source.getBillingLedger().getUser().getBillingCenter() != null &&
            source.getBillingLedger().getUser().getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            code2 = source.getBillingLedger().getUser().getBillingCenter().getExternalAccountingSystemIdentifier();
        else
            code2 = DefaultValue.STRING;
        target.setShortcutDimension2Code(code2);
    }

    @AfterMapping
    void toSalesLineShortcutDimensionCode(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {
        String code2;
        if (source.getPenaltyAddedToInvoice() != null
            && source.getPenaltyAddedToInvoice().getUser() != null
            && source.getPenaltyAddedToInvoice().getUser().getBillingCenter() != null
            && source.getPenaltyAddedToInvoice().getUser().getBillingCenter().getExternalAccountingSystemIdentifier() != null)
            code2 = source.getPenaltyAddedToInvoice().getUser().getBillingCenter().getExternalAccountingSystemIdentifier();
        else
            code2 = DefaultValue.STRING;
        target.setShortcutDimension2Code(code2);
    }

    @AfterMapping
    void toSalesLineUnitAmount(final ChargesAdjustment source, @MappingTarget SalesLine target) {

        // get amount as absolute value
        Double amount = Math.abs(source.getChargeAmount());

        // set target properties based on amount
        target.setQuantity(1d);
        target.setUnitPrice(amount);
        target.setLineAmount(amount); // optional, Kcaa Erp self populates amount (quantity * unitPrice)
    }

    private void toSalesLineUnitAmount(final FlightMovement source, SalesLine target, BillingLedger billingLedger,
                                       final CachedCurrencyConverter cachedCurrencyConverter) {

        // different stored currencies
        Currency anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        Currency enrouteCurrency = source.getFlightmovementCategory().getEnrouteResultCurrency();
        Currency paxDomCurrency = cachedCurrencyConverter.getPaxDomCurrency();
        Currency paxIntlCurrency = cachedCurrencyConverter.getPaxIntlCurrency();

        Currency aerodromeCurrency;
        Currency approachCurrency;
        Currency lateArrDepCurrency;
        if (FlightmovementCategoryScope.DOMESTIC == source.getFlightCategoryScope()) {
            aerodromeCurrency = cachedCurrencyConverter.getAerodromeDomCurrency();
            approachCurrency = cachedCurrencyConverter.getApproachDomCurrency();
            lateArrDepCurrency = cachedCurrencyConverter.getLateArrDepDomCurrency();
        } else if (FlightmovementCategoryScope.REGIONAL == source.getFlightCategoryScope()) {
            aerodromeCurrency = cachedCurrencyConverter.getAerodromeRegCurrency();
            approachCurrency = cachedCurrencyConverter.getApproachRegCurrency();
            lateArrDepCurrency = cachedCurrencyConverter.getLateArrDepRegCurrency();
        } else {
            aerodromeCurrency = cachedCurrencyConverter.getAerodromeIntlCurrency();
            approachCurrency = cachedCurrencyConverter.getApproachIntlCurrency();
            lateArrDepCurrency = cachedCurrencyConverter.getLateArrDepIntlCurrency();
        }

        // invoice currency used
        Currency invoiceCurrency = null;
        if (billingLedger != null
            && billingLedger.getInvoiceCurrency() != null)
            invoiceCurrency = billingLedger.getInvoiceCurrency();

        // set amount depending on flight movement invoice types
        Double amount = 0.0;

        // enroute invoice amount, currency defined by flight movement category
        if (billingLedger != null && source.getEnrouteInvoiceId() != null && source.getEnrouteInvoiceId().equals(billingLedger.getId()))
            amount = addToAmount(amount, source.getEnrouteCharges(), invoiceCurrency, enrouteCurrency, cachedCurrencyConverter);

        // passenger invoice amounts, currency set in system configuration
        if (billingLedger != null && source.getPassengerInvoiceId() != null && source.getPassengerInvoiceId().equals(billingLedger.getId())) {
            amount = addToAmount(amount, source.getDomesticPassengerCharges(), invoiceCurrency, paxDomCurrency, cachedCurrencyConverter);
            amount = addToAmount(amount, source.getInternationalPassengerCharges(), invoiceCurrency, paxIntlCurrency, cachedCurrencyConverter);
        }

        // other invoice amounts, currency set as air navigation or ansp
        if (billingLedger != null && source.getOtherInvoiceId() != null && source.getOtherInvoiceId().equals(billingLedger.getId())) {

            // these are stored in ANSP currency
            amount = addToAmount(amount, source.getTaspCharge(), invoiceCurrency, anspCurrency, cachedCurrencyConverter);
            amount = addToAmount(amount, source.getParkingCharges(), invoiceCurrency, anspCurrency, cachedCurrencyConverter);

            // approach, aerodrome, late arrival/departure currency are set in system configuration
            amount = addToAmount(amount, source.getApproachCharges(), invoiceCurrency, approachCurrency, cachedCurrencyConverter);
            amount = addToAmount(amount, source.getAerodromeCharges(), invoiceCurrency, aerodromeCurrency, cachedCurrencyConverter);
            amount = addToAmount(amount, source.getLateArrivalCharges(), invoiceCurrency, lateArrDepCurrency, cachedCurrencyConverter);
            amount = addToAmount(amount, source.getLateDepartureCharges(), invoiceCurrency, lateArrDepCurrency, cachedCurrencyConverter);
        }

        // get absolute amount
        Double totalAmount = Math.abs(amount);

        // set target properties based on total amount
        target.setQuantity(1d);
        target.setUnitPrice(totalAmount);
        target.setLineAmount(totalAmount);
    }

    @AfterMapping
    void toSalesLineUnitAmount(final InvoiceLineItem source, @MappingTarget SalesLine target) {

        // get amount as absolute value
        Double amount = Math.abs(source.getAmount());

        // set target properties based on amount
        target.setUnitPrice(amount);
        target.setQuantity(1d);
        target.setLineAmount(amount); // optional, Kcaa Erp self populates (quantity * unitPrice)
    }

    @AfterMapping
    void toSalesLineUnitAmount(final InvoiceOverduePenalty source, @MappingTarget SalesLine target) {

        // get amount as absolute value
        Double amount = Math.abs(source.getDefaultPenaltyAmount() + source.getPunitivePenaltyAmount());

        // set target properties based on amount
        target.setUnitPrice(amount);
        target.setQuantity(1d);
        target.setLineAmount(amount); // optional, Kcaa Erp self populates (quantity * unitPrice)
    }

    private Double addToAmount(Double amount, Double chargeToAdd, Currency amountCurrency, Currency chargeToAddCurrency,
                               final CachedCurrencyConverter currencyConverter) {
        if (chargeToAdd == null || chargeToAdd == 0d)
            return amount;
        else if (amountCurrency != null && chargeToAddCurrency != null && !amountCurrency.equals(chargeToAddCurrency))
            return amount + currencyConverter.convertCurrency(chargeToAdd, chargeToAddCurrency, amountCurrency);
        else if (amountCurrency != null && amountCurrency.getDecimalPlaces() != null)
            return amount + Calculation.truncate(chargeToAdd, amountCurrency.getDecimalPlaces());
        else
            return amount + chargeToAdd;
    }

    private String formatAdTime(String ad, String item18, String time, String actualTime,
                                final CachedAerodromeResolver aerodromeResolver) {
        StringJoiner result = new StringJoiner(" ");
        if (ad != null || item18 != null)
            result.add(reportHelper.formatAerodromeCode(aerodromeResolver.resolve(ad, item18)));
        if (time != null || actualTime != null)
            result.add(reportHelper.formatFlightTime(MiscUtils.evl(actualTime, time)));
        return result.toString();
    }

    /**
     * Get valid external system identifier from account that should be used as
     * the customer number.
     *
     * @param account account
     * @return external system identifier
     */
    private String findCustomerNo(final Account account) {

        // assert that account is not null and has an id value
        if (account == null || account.getId() == null)
            return null;

        // find all account external charge categories and return first
        // valid external system identifier value
        for (AccountExternalChargeCategory external : accountExternalChargeCategoryService.findByAccount(account.getId())) {
            String identifier = external.getExternalSystemIdentifier();
            if (identifier != null && !identifier.isEmpty())
                return identifier;
        }

        // return null if no valid external system identifiers found
        return null;
    }
}
