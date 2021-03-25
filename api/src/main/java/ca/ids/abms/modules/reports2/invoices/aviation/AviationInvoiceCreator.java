package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.bankcode.BankCodeService;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.BillingLedgerService;
import ca.ids.abms.modules.common.enumerators.InvoiceStateType;
import ca.ids.abms.modules.common.enumerators.InvoiceType;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementType;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.flightmovementsbuilder.vo.DeltaFlightVO;
import ca.ids.abms.modules.formulas.unifiedtax.FormulaEvaluatorUTImpl;
import ca.ids.abms.modules.formulas.unifiedtax.JavascriptEngineUTFactory;
import ca.ids.abms.modules.jobs.impl.InvoiceProgressCounter;
import ca.ids.abms.modules.reports2.common.*;
import ca.ids.abms.modules.reports2.invoices.ChargeSelection;
import ca.ids.abms.modules.reports2.invoices.InvoiceReportUtility;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;
import ca.ids.abms.modules.reports2.invoices.aviation.AviationInvoiceData.AircraftInfo;
import ca.ids.abms.modules.reports2.invoices.aviation.charges.AviationInvoiceChargeProvider;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPaymentMechanism;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTax;
import ca.ids.abms.modules.unifiedtaxes.UnifiedTaxService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.utilities.invoices.InvoiceSequenceNumberHelper;
import ca.ids.abms.plugins.kcaa.aatis.modules.permitnumber.KcaaAatisPermitNumber;
import ca.ids.abms.util.LocaleUtils;
import ca.ids.abms.util.MiscUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static ca.ids.abms.modules.reports2.invoices.ChargeSelection.*;
import static ca.ids.abms.util.MiscUtils.nvl;

/**
 * Create invoice data, PDF doc, lidger and transaction; one at a time
 */
public class AviationInvoiceCreator {
    private static final Logger LOG = LoggerFactory.getLogger(AviationInvoiceCreator.class);
    private static final String TWO_DECIMALS = "%,.2f";

    private final ReportHelper reportHelper;
    private final BillingLedgerService billingLedgerService;
    private final UnifiedTaxService unifiedTaxService;
    private final AircraftRegistrationService aircraftRegistrationService;
    private final AviationInvoiceDocumentCreator aviationInvoiceDocumentCreator;
    private final TransactionService transactionService;
    private final LocalDateTime ldtNow;
    private final BillingCenter billingCenter;
    private final ReportFormat reportFormat;
    private final InvoiceStateType initialLedgerState;
    private final User currentUser;
    private final InvoiceSequenceNumberHelper.Generator invoiceSeqNumGen;
    private final String invoiceNameSuffix;
    private final DateTimeFormatter dateFormatter;
    private final boolean preview;
    private final RoundingUtils roundingUtils;
    private final LocalDateTime startDate;
    private final LocalDateTime endDateInclusive;
    private final BillingInterval billingInterval;
    private final SystemConfigurationService systemConfigurationService;
    private final boolean pointOfSale;

    private final Currency anspCurrency;
    private final Currency eurCurrency;
    private final Currency usdCurrency;
    private final InvoiceReportUtility invoiceReportUtility;
    private final CachedCurrencyConverter cachedCurrencyConverter;
    private final CurrencyUtils currencyUtils;

    private final String distanceUnitOfMeasure;
    private final String mtowUnitOfMeasure;
    private final Boolean taspChargesEnabled;

    private final String taspFeeLabel;
    private final Boolean isPAXIncluded;
    private final SystemConfiguration includePassengerCharges;
    private final SystemConfiguration includeTaspCharges;
    private final Boolean inverseCurrencyRate;
    private final SystemConfiguration applyPenaltyOn;

    private final BillingOrgCode billingOrgCode;

    AviationInvoiceCreator(final ReportHelper reportHelper,
                           final BillingLedgerService billingLedgerService,
                           final UnifiedTaxService unifiedTaxService,
                           final AircraftRegistrationService aircraftRegistrationService,
                           final AviationInvoiceDocumentCreator aviationInvoiceDocumentCreator,
                           final TransactionService transactionService,
                           final InvoiceSequenceNumberHelper invoiceSequenceNumberHelper,
                           final LocalDateTime ldtNow,
                           final ReportFormat reportFormat,
                           final String invoiceNameSuffix,
                           final boolean preview,
                           final CurrencyUtils currencyUtils,
                           final boolean approvalWorkflow,
                           final LocalDateTime startDate,
                           final LocalDateTime endDateInclusive,
                           final RoundingUtils roundingUtils,
                           final SystemConfigurationService systemConfigurationService,
                           final BankCodeService bankCodeService,
                           final BillingInterval billingInterval,
                           final boolean pointOfSale,
                           final User currentUser,
                           final List<AviationInvoiceChargeProvider> aviationInvoiceChargeProviders) {

        this.reportHelper = reportHelper;
        this.billingLedgerService = billingLedgerService;
        this.unifiedTaxService = unifiedTaxService;
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.aviationInvoiceDocumentCreator = aviationInvoiceDocumentCreator;
        this.transactionService = transactionService;
        this.ldtNow = ldtNow;
        this.billingCenter = reportHelper.getBillingCenterOfCurrentUser(currentUser);
        this.reportFormat = reportFormat;
        this.initialLedgerState = reportHelper.getInitialLedgerState(approvalWorkflow);
        this.currentUser = currentUser;
        this.invoiceSeqNumGen = invoiceSequenceNumberHelper.generator();
        this.invoiceNameSuffix = invoiceNameSuffix;
        this.dateFormatter = reportHelper.getDateFormat();
        this.preview = preview;
        this.pointOfSale = pointOfSale;
        this.startDate = startDate;
        this.endDateInclusive = endDateInclusive;
        this.billingInterval = billingInterval;

            if(billingInterval == BillingInterval.PARTIALLY || billingInterval == BillingInterval.ANNUALLY){
                this.cachedCurrencyConverter = new CachedCurrencyConverter(currencyUtils, startDate);
            }else{
                this.cachedCurrencyConverter = new CachedCurrencyConverter(currencyUtils, endDateInclusive);

            }


        this.invoiceReportUtility = new InvoiceReportUtility(reportHelper, billingLedgerService, transactionService,
                systemConfigurationService, cachedCurrencyConverter, bankCodeService, aviationInvoiceChargeProviders);
        this.systemConfigurationService = systemConfigurationService;
        this.currencyUtils = currencyUtils;

        this.distanceUnitOfMeasure = reportHelper.getDistanceUnitOfMeasure();
        this.mtowUnitOfMeasure = reportHelper.getMTOWUnitOfMeasure();
        this.roundingUtils = roundingUtils;

        this.billingOrgCode = systemConfigurationService.getBillingOrgCode();

        this.anspCurrency = cachedCurrencyConverter.getAnspCurrency();
        this.usdCurrency = cachedCurrencyConverter.getUsdCurrency();
        this.eurCurrency = cachedCurrencyConverter.getEurCurrency();
        this.taspChargesEnabled = reportHelper.getTASPChargesEnabled();
        this.taspFeeLabel = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.TASP_FEES_LABEL).getCurrentValue();

        // moved for increased performance, duplicate calls to the database should be done only once
        this.isPAXIncluded = systemConfigurationService.shouldIncludePAXinInvoiceTotal();
        this.includePassengerCharges = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INCLUDE_PASSENGER_FEES_ON_INVOICE);
        this.includeTaspCharges = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.TASP_CHARGES_SUPPORT);
        this.inverseCurrencyRate = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVERSE_CURRENCY_RATE);
        this.applyPenaltyOn = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.APPLY_INTEREST_PENALTY_ON);
    }

    /**
     * Create an invoice for the given account and flights
     * If payInvoice is true, the approval workflow won't be used.
     */
    public AviationInvoice createInvoice(final Account account,
                                         final List <FlightMovement> accountFlights,
                                         final List <AircraftRegistration> aircraftRegistrationsToInvoiceByUnifiedTax,
                                         final InvoicePaymentParameters payment,
                                         final ChargeSelection chargeSelection,
                                         final FlightmovementCategory flightmovementCategory,
                                         final InvoiceProgressCounter counter,
                                         final List<KcaaAatisPermitNumber> invoicePermits,
                                         final String selectedInvoiceCurrency) {

        AviationInvoice aviationInvoice = null;
    	Currency aviationInvoiceCurrency = null;
    	Currency targetCurrency;

    	if (counter != null) {
    	    counter.setAccountName(account.getName());
    	    //unifiedTaxChargesStrNullPointer
    	    if(accountFlights != null){
                counter.setFlightsTotal(accountFlights.size());
            }

    	    counter.update();
        }

    	if (do_checkIfAviationInvoicingIsByFlightmovementCategory()) {

    		if (flightmovementCategory == null) {
    			throw new CustomParametrizedException("Flightmovement Category can't be null");
    		}

    		aviationInvoiceCurrency = flightmovementCategory.getEnrouteInvoiceCurrency();
    		targetCurrency = flightmovementCategory.getEnrouteResultCurrency();

    	} else {

    	    // user select invoice currency in POS
    	    if (StringUtils.isNotBlank(selectedInvoiceCurrency)) {
    	        Currency userSelectedCurrency = currencyUtils.getCurrency(selectedInvoiceCurrency);
    	        if (userSelectedCurrency != null) {
    	            aviationInvoiceCurrency = userSelectedCurrency;
                }
            } else {
                // set invoice currency based on account
                aviationInvoiceCurrency = account.getInvoiceCurrency();
            }
    		targetCurrency = usdCurrency;
    	}

    	if (aviationInvoiceCurrency == null) {
    		throw new CustomParametrizedException("Aviation invoice currency can't be null");
    	}

        // create invoice data, total amount and amount due formatted below
        final AviationInvoiceData invoiceData = this.do_createInvoiceData(
            account, accountFlights, aircraftRegistrationsToInvoiceByUnifiedTax, chargeSelection, payment, aviationInvoiceCurrency, counter, invoicePermits
        );

        if(chargeSelection != ONLY_PAX || invoiceData.invoiceGenerationAllowed) {

            final boolean cashAccount = account.getCashAccount();
            final boolean unifiedTaxAccount = account.getAccountType().getName().equals("Unified Tax");

            // create billing ledger, non-rounded total amount required in invoiceData
            final BillingLedger billingLedger = do_createBillingLedger(account, invoiceData, accountFlights,
                chargeSelection, aviationInvoiceCurrency, targetCurrency, flightmovementCategory, invoicePermits, this.pointOfSale);

            // set credit, penalty, total amount and amount due in invoiceData
            do_setCreditAndPenaltyAndTotalAmountAndAmountDue(invoiceData, billingLedger, preview);

            if (this.pointOfSale) {
            	//reference month (billing end period for credit invoice, current date for cash invoice)
            	//Werner: Cash invoice is an invoice generated from point of sale
            	//Credit invoice is an invoice generated from aviation billing
            	invoiceData.global.referenceMonthStr = reportHelper.formatMonth(ldtNow);
            } else {
            	//billing end period for credit invoice
            	//we can get this information only after BillingLedger was created
            	invoiceData.global.referenceMonthStr = reportHelper.formatMonth (billingLedger.getInvoicePeriodOrDate());
            }

            // Create PDF file
            final ReportDocument reportDocument = this.aviationInvoiceDocumentCreator.create(invoiceData, reportFormat, chargeSelection, cashAccount, unifiedTaxAccount, pointOfSale);

            // save PDF file in billing ledger
            reportHelper.setReportDocument(billingLedger, reportDocument);

            aviationInvoice = new AviationInvoice(account, accountFlights, invoiceData, billingLedger, reportDocument);

            // event trigger to indicate that a billing ledger was created with associated flight movements
            // should only be triggered if NOT in preview mode
            if(!preview)
                billingLedgerService.created(billingLedger, accountFlights);
        }
        return aviationInvoice;
    }

    /**
     * Create payment for the invoice
     */
    void createPayment(final AviationInvoice invoice, final InvoicePaymentParameters payment) {
        assert(payment != null);

        // Create payment if necessary
        final Transaction transaction = this.do_createPayment(invoice.account(), invoice.billingLedger(), payment);

        final ReportDocument transactionReceiptDocument = this.transactionService.getReportDocumentFromTransaction(
                    transaction);

        invoice.flightList().forEach(fm->reportHelper.updateFlightStatusToMatchInvoice(invoice.billingLedger(), fm));
        invoice.payment(transaction, transactionReceiptDocument);
    }


    // ------------------------ private ----------------------------------


    private boolean do_checkIfAviationInvoicingIsByFlightmovementCategory() {
    	final SystemConfiguration aviationInvoiceCurrencyItem = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.INVOICE_CURRENCY_ENROUTE);
    	boolean invoiceByFlightMovementCategory = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_FLIGHT_MOVEMENT_CATEGORY);

    	return invoiceByFlightMovementCategory && aviationInvoiceCurrencyItem!=null;
    }

    /**
     * Create raw invoice data; to be further formatted into PDF etc.
     */
    private AviationInvoiceData do_createInvoiceData(final Account account,
                                                     final List <FlightMovement> accountFlights,
                                                     final List <AircraftRegistration> aircraftRegistrationsToInvoiceByUnifiedTax,
                                                     final ChargeSelection chargesIncluded,
                                                     final InvoicePaymentParameters payment,
                                                     final Currency aviationInvoiceCurrency,
                                                     final InvoiceProgressCounter counter,
                                                     final List<KcaaAatisPermitNumber> invoicePermits) {

        final AviationInvoiceData invoiceData = new AviationInvoiceData();

        invoiceData.global = new AviationInvoiceData.Global();
        if(!preview) {
            invoiceData.global.realInvoiceNumber = invoiceSeqNumGen.nextInvoiceSequenceNumber(InvoiceType.AVIATION_NONIATA);
        }
        invoiceData.global.invoiceNumber = reportHelper.getDisplayInvoiceNumber(invoiceData.global.realInvoiceNumber, preview);
        invoiceData.global.invoiceName = String.format("%s %s%s", Translation.getLangByToken("Aviation invoice"),
        invoiceData.global.invoiceNumber, invoiceNameSuffix);
        invoiceData.global.invoiceIssueLocation = billingCenter.getName();
        invoiceData.global.invoiceDateStr = reportHelper.formatDateUtc(endDateInclusive, dateFormatter);
        invoiceData.global.invoiceDateOfIssueStr = reportHelper.formatDateUtc(ldtNow, dateFormatter);
        invoiceData.global.invoiceDueDateStr = reportHelper.formatDateUtc(ldtNow.plusDays(account.getPaymentTerms()), dateFormatter);
        if (billingInterval != null) {
            switch (billingInterval) {
            case MONTHLY:
                invoiceData.global.invoiceBillingPeriod = String.format("%s-%s", StringUtils.capitalize(endDateInclusive.getMonth().name().toLowerCase()), endDateInclusive.getYear());
                break;
            case WEEKLY:
                invoiceData.global.invoiceBillingPeriod = String.format("%s - %s", reportHelper.formatDateUtc(endDateInclusive.minusDays(6), dateFormatter), invoiceData.global.invoiceDateStr);
                break;
            case OPEN:
                invoiceData.global.invoiceBillingPeriod = String.format("%s - %s", reportHelper.formatDateUtc(startDate, dateFormatter), invoiceData.global.invoiceDateStr);
                break;
            case ANNUALLY:
            case PARTIALLY:
                invoiceData.global.invoiceBillingPeriod = String.format("%s - %s", reportHelper.formatDateUtc(startDate, dateFormatter), invoiceData.global.invoiceDateStr);
                break;
            default:
                invoiceData.global.invoiceBillingPeriod = invoiceData.global.invoiceDateStr;
                break;
            }
        } else {
            invoiceData.global.invoiceBillingPeriod = invoiceData.global.invoiceDateStr;
        }

        // operator account data
        invoiceData.global.accountId = account.getId();
        invoiceData.global.accountName = account.getName();
        invoiceData.global.accountAlias = account.getAlias();
        invoiceData.global.accountExternalSystemIdentifier = reportHelper.getAccountExternalSystemIdentifiers(account);

        invoiceData.global.fromName = currentUser.getName();
        invoiceData.global.fromPosition = currentUser.getJobTitle();
        invoiceData.global.invoiceCurrencyCode = aviationInvoiceCurrency.getCurrencyCode();
        invoiceData.global.invoiceCurrencyAnspCode = anspCurrency.getCurrencyCode();
        invoiceData.global.invoiceCurrencyUsdCode = usdCurrency.getCurrencyCode();
        invoiceData.global.billingName = account.getAviationBillingContactPersonName();
        invoiceData.global.billingAddress = account.getAviationBillingMailingAddress();
        invoiceData.global.billingContactTel = account.getAviationBillingPhoneNumber();
        invoiceData.global.billingEmail = account.getAviationBillingEmailAddress();
        invoiceData.global.passengerChargesEnabled = reportHelper.passengerChargesEnabled();
        invoiceData.global.extendedHoursSurchargeEnabled = reportHelper.extendedChargesEnabled();
        invoiceData.global.taspChargesEnabled = taspChargesEnabled;
        invoiceData.global.taspFeeLabel = taspFeeLabel;
        invoiceData.global.includePassengerCharges = this.includePassengerCharges.getCurrentValue();
        invoiceData.global.includeTaspCharges = this.includeTaspCharges.getCurrentValue();

        // bank accounts list
        invoiceData.bankAccountList = new ArrayList<>();
        invoiceData.bankAccountList.addAll(invoiceReportUtility.getBankAccountList(billingCenter));

        if (payment != null) {
            invoiceData.global.kraClerkName = payment.getKraClerkName();
            invoiceData.global.kraReceiptNumber = payment.getKraReceiptNumber();
        }

        final Currency airNavigationChargesCurrency = cachedCurrencyConverter.getSystemConfigurationCurrency(SystemConfigurationItemName.AIR_NAVIGATION_CHARGES_CURRENCY);
        final Currency domesticPassengerChargesCurrency = cachedCurrencyConverter.getSystemConfigurationCurrency(SystemConfigurationItemName.DOMESTIC_PAX_FEE_CURRENCY);
        final Currency internationalPassengerChargesCurrency = cachedCurrencyConverter.getSystemConfigurationCurrency(SystemConfigurationItemName.INTERNATIONAL_PAX_FEE_CURRENCY);

        invoiceData.flightInfoList = new ArrayList<>();

        invoiceData.invoiceGenerationAllowed = chargesIncluded != ONLY_PAX;

        //FIXME Helen said that FlightMovementType will not be used in the future
if(accountFlights!= null){
    for (final FlightMovement flight : accountFlights) {
        if (counter != null) {
            counter.increaseFlightNumber();
            counter.update();
        }
        if (chargesIncluded != ONLY_PAX
            || flight.getMovementType() == FlightMovementType.DOMESTIC
            || flight.getMovementType() == FlightMovementType.REG_DEPARTURE
            || flight.getMovementType() == FlightMovementType.INT_DEPARTURE) {


            final AviationInvoiceData.FlightInfo flightInfo = processFlight(flight, account, chargesIncluded, aviationInvoiceCurrency,
                airNavigationChargesCurrency, domesticPassengerChargesCurrency, internationalPassengerChargesCurrency, invoicePermits);
            invoiceData.flightInfoList.add(flightInfo);

            /* The passenger invoice should be generated only when the invoice includes at least a domestic or departure
             * flight with passenger counters not null(previously calculated through the "invoicePaxAllowed" boolean.
             * That rule is not applicable when the invoice to generate contains only/even other charges.
             */
            if(!invoiceData.invoiceGenerationAllowed) {
                invoiceData.invoiceGenerationAllowed |=(chargesIncluded == ONLY_PAX) && flightInfo.invoicePaxAllowed;
            }
        }
    }
}


        invoiceData.aircraftInfoList = new ArrayList<>();

        if (aircraftRegistrationsToInvoiceByUnifiedTax != null) {
	        for (final AircraftRegistration ar: aircraftRegistrationsToInvoiceByUnifiedTax) {
	        	// TODO: manage counter update

	        	if (ar.getAircraftServiceDate()!=null) {
	        		final AviationInvoiceData.AircraftInfo aircraftInfo = processAircraftRegistration(ar, account, startDate, endDateInclusive, aviationInvoiceCurrency);
	        		invoiceData.aircraftInfoList.add(aircraftInfo);
	        	}
	        }
        }

        if (invoiceData.invoiceGenerationAllowed) {

            // additional charges
            invoiceData.additionalCharges = new ArrayList<>();

            // accountCredits
            invoiceData.accountCredits = new ArrayList<>();

            // overdueInvoices
            invoiceData.overduePenaltyInvoices = new ArrayList<>();

            // sub-totals
            Double totalEnrouteCharges = 0d;
            Double totalTaspCharges = 0d;
            Double totalAerodromeCharges = 0d;
            Double totalApproachCharges = 0d;
            Double totalLandingCharges = 0d;
            Double totalParkingCharges = 0d;
            Double totalPassengerCharges = 0d;
            Double totalLateDepartureArrivalCharges = 0d;
            Double totalExtendedHoursSurcharges = 0d;

            Double totalEnrouteChargesAnsp = 0d;
            Double totalTaspChargesAnsp = 0d;
            Double totalAerodromeChargesAnsp = 0d;
            Double totalApproachChargesAnsp = 0d;
            Double totalLandingChargesAnsp = 0d;
            Double totalParkingChargesAnsp = 0d;
            Double totalPassengerChargesAnsp = 0d;
            Double totalLateDepartureArrivalChargesAnsp = 0d;
            Double totalExtendedHoursSurchargesAnsp = 0d;



            Integer totalFlightsWithEnrouteCharges = 0;
            Integer totalFlightsWithAerodromeCharges = 0;
            Integer totalFlightsWithApproachCharges = 0;
            Integer totalFlightsWithLandingCharges = 0;
            Integer totalFlightsWithPassengerCharges = 0;
            Integer totalFlightsWithTaspCharges = 0;
            Integer totalFlightsWithLateDepartureArrivalCharges = 0;
            Integer totalFlightsWithExtendedHoursCharges = 0;

            Integer totalAirctaftRegistration = 0;

            Double unifiedTaxCharges = 0d;

            // sub-totals for Domestic Flight Category
            AviationInvoiceData.Global.FlightCategoryInfo domesticData = new AviationInvoiceData.Global.FlightCategoryInfo();
            Double domesticTotalCharges;

            // sub-totals for  International Flight Category
            AviationInvoiceData.Global.FlightCategoryInfo internationalData = new AviationInvoiceData.Global.FlightCategoryInfo();
            Double internationalTotalCharges;

            // sub-totals for Overflight Flight Category
            AviationInvoiceData.Global.FlightCategoryInfo overflightData = new AviationInvoiceData.Global.FlightCategoryInfo();
            Double overflightTotalCharges;

            List<String> flightMovementCategoryScope = new ArrayList<>();

            // loop through each flight info and add to sub totals
            for (AviationInvoiceData.FlightInfo fi : invoiceData.flightInfoList) {

                // invoice currency sub-totals
                totalEnrouteCharges += nvl(fi.enrouteCharges, 0d);
                totalAerodromeCharges += nvl(fi.aerodromeCharges, 0d);
                totalApproachCharges += nvl(fi.approachCharges, 0d);
                totalLandingCharges += nvl(fi.landingCharges, 0d);
                totalParkingCharges += nvl(fi.parkingCharges, 0d);
                totalPassengerCharges += nvl(fi.passengerCharges, 0d);
                totalTaspCharges += nvl(fi.taspCharges, 0d);
                totalLateDepartureArrivalCharges += nvl(fi.lateDepartureArrivalCharges, 0d);
                totalExtendedHoursSurcharges += nvl(fi.extendedHoursSurcharge, 0d);

                // ansp currency sub-totals
                totalEnrouteChargesAnsp += nvl(fi.enrouteChargesAnsp, 0d);
                totalAerodromeChargesAnsp += nvl(fi.aerodromeCharges, 0d);
                totalApproachChargesAnsp += nvl(fi.approachCharges, 0d);
                totalLandingChargesAnsp += nvl(fi.landingChargesAnsp, 0d);
                totalParkingChargesAnsp += nvl(fi.parkingChargesAnsp, 0d);
                totalPassengerChargesAnsp += nvl(fi.passengerChargesAnsp, 0d);
                totalTaspChargesAnsp += nvl(fi.passengerChargesAnsp, 0d);
                totalLateDepartureArrivalChargesAnsp += nvl(fi.lateDepartureArrivalChargesAnsp, 0d);
                totalExtendedHoursSurchargesAnsp += nvl(fi.extendedHoursSurchargeAnsp, 0d);

                totalFlightsWithEnrouteCharges += fi.enrouteCharges != null && fi.enrouteCharges > 0d ? 1 : 0;
                totalFlightsWithAerodromeCharges += fi.aerodromeCharges != null && fi.aerodromeCharges > 0d ? 1 : 0;
                totalFlightsWithApproachCharges += fi.approachCharges != null && fi.approachCharges > 0d ? 1 : 0;
                totalFlightsWithLandingCharges += fi.landingCharges != null && fi.landingCharges > 0d ? 1 : 0;
                totalFlightsWithPassengerCharges += fi.passengerCharges != null && fi.passengerCharges > 0d ? 1 : 0;
                totalFlightsWithTaspCharges += fi.taspCharges != null && fi.taspCharges > 0d ? 1 : 0;
                totalFlightsWithLateDepartureArrivalCharges += fi.lateDepartureArrivalCharges != null
                    && fi.lateDepartureArrivalCharges > 0d ? 1 : 0;
                totalFlightsWithExtendedHoursCharges += fi.extendedHoursSurcharge != null && fi.extendedHoursSurcharge > 0d ? 1 : 0;

                if (fi.flightCategory.equals("DO")) {
                    setSubtotalsByFlightCategories(fi, domesticData);
                }

                if (fi.flightCategory.equals("IA") || fi.flightCategory.equals("ID")) {
                    setSubtotalsByFlightCategories(fi, internationalData);
                }

                if (fi.flightCategory.equals("OV")) {
                    setSubtotalsByFlightCategories(fi, overflightData);
                }

                if (fi.flightMovementCategoryScope != null) {
                	flightMovementCategoryScope.add(fi.flightMovementCategoryScope);
                }
            }

	        if (billingOrgCode == BillingOrgCode.CADSUR) {
	            invoiceData.global.flightMovementCategoryScope = flightMovementCategoryScope.stream().distinct()
	                .sorted().collect(Collectors.toList()).toString().replaceAll("[\\[\\]]", "");
	        }

            domesticTotalCharges = domesticData.enrouteCharges + domesticData.aerodromeCharges + domesticData.parkingCharges + domesticData.passengerCharges;
            internationalTotalCharges = internationalData.enrouteCharges + internationalData.aerodromeCharges + internationalData.parkingCharges + internationalData.passengerCharges;
            overflightTotalCharges = overflightData.enrouteCharges + overflightData.aerodromeCharges + overflightData.parkingCharges + overflightData.passengerCharges;

            // sum all sub-totals to form total charges
            // landing charges includes aerodrome and approach charges
            Double totalCharges;
            Double totalChargesAnsp;
            if (isPAXIncluded && invoiceData.global.includePassengerCharges.equalsIgnoreCase("t")) {
                totalCharges = totalEnrouteCharges + totalTaspCharges + totalLandingCharges + totalParkingCharges +
                    totalPassengerCharges + totalLateDepartureArrivalCharges + totalExtendedHoursSurcharges;

                totalChargesAnsp = totalEnrouteChargesAnsp + totalTaspChargesAnsp + totalLandingChargesAnsp +
                    totalParkingChargesAnsp + totalPassengerChargesAnsp + totalLateDepartureArrivalChargesAnsp + totalExtendedHoursSurchargesAnsp;
            }
            else {
                totalCharges = totalEnrouteCharges + totalTaspCharges + totalLandingCharges + totalParkingCharges +
                    totalLateDepartureArrivalCharges + totalExtendedHoursSurcharges;

                totalChargesAnsp = totalEnrouteChargesAnsp + totalTaspChargesAnsp + totalLandingChargesAnsp +
                    totalParkingChargesAnsp + totalLateDepartureArrivalChargesAnsp + totalExtendedHoursSurchargesAnsp;
            }

            invoiceData.global.enrouteCharges = totalEnrouteCharges;
            invoiceData.global.enrouteChargesStr = reportHelper.formatCurrency(invoiceData.global.enrouteCharges, aviationInvoiceCurrency);
            invoiceData.global.enrouteChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.enrouteCharges, aviationInvoiceCurrency);
            invoiceData.global.enrouteChargesAnsp = totalEnrouteChargesAnsp;
            invoiceData.global.enrouteChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.enrouteChargesAnsp, anspCurrency);

            invoiceData.global.taspCharges = totalTaspCharges;
            invoiceData.global.taspChargesStr = reportHelper.formatCurrency(invoiceData.global.taspCharges, aviationInvoiceCurrency);
            invoiceData.global.taspChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.taspCharges, aviationInvoiceCurrency);
            invoiceData.global.taspChargesAnsp = totalTaspChargesAnsp;
            invoiceData.global.taspChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.taspChargesAnsp, anspCurrency);

            invoiceData.global.aerodromeCharges = totalAerodromeCharges;
            invoiceData.global.aerodromeChargesStr = reportHelper.formatCurrency(invoiceData.global.aerodromeCharges, aviationInvoiceCurrency);
            invoiceData.global.aerodromeChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.aerodromeCharges, aviationInvoiceCurrency);
            invoiceData.global.aerodromeChargesAnsp = totalAerodromeChargesAnsp;
            invoiceData.global.aerodromeChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.aerodromeChargesAnsp, anspCurrency);

            invoiceData.global.approachCharges = totalApproachCharges;
            invoiceData.global.approachChargesStr = reportHelper.formatCurrency(invoiceData.global.approachCharges, aviationInvoiceCurrency);
            invoiceData.global.approachChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.approachCharges, aviationInvoiceCurrency);
            invoiceData.global.approachChargesAnsp = totalApproachChargesAnsp;
            invoiceData.global.approachChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.approachChargesAnsp, anspCurrency);

            invoiceData.global.landingCharges = totalLandingCharges;
            invoiceData.global.landingChargesStr = reportHelper.formatCurrency(invoiceData.global.landingCharges, aviationInvoiceCurrency);
            invoiceData.global.landingChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.landingCharges, aviationInvoiceCurrency);
            invoiceData.global.landingChargesAnsp = totalLandingChargesAnsp;
            invoiceData.global.landingChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.landingChargesAnsp, anspCurrency);

            invoiceData.global.parkingCharges = totalParkingCharges;
            invoiceData.global.parkingChargesStr = reportHelper.formatCurrency(invoiceData.global.parkingCharges, aviationInvoiceCurrency);
            invoiceData.global.parkingChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.parkingCharges, aviationInvoiceCurrency);
            invoiceData.global.parkingChargesAnsp = totalParkingChargesAnsp;
            invoiceData.global.parkingChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.parkingChargesAnsp , anspCurrency);

            invoiceData.global.passengerCharges = totalPassengerCharges;
            invoiceData.global.passengerChargesStr = reportHelper.formatCurrency(invoiceData.global.passengerCharges, aviationInvoiceCurrency);
            invoiceData.global.passengerChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.passengerCharges, aviationInvoiceCurrency);
            invoiceData.global.passengerChargesAnsp = totalPassengerChargesAnsp;
            invoiceData.global.passengerChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.passengerChargesAnsp, anspCurrency);

            /*
         //public @XmlElement(nillable = true) Double amountOfTheInvoiceCharges;
        //public @XmlElement(nillable = true) String amountOfTheInvoiceChargesStr;
             */


            invoiceData.global.unifiedTaxCharges = unifiedTaxCharges;
            invoiceData.global.unifiedTaxChargesStr = reportHelper.formatCurrency(invoiceData.global.unifiedTaxCharges, aviationInvoiceCurrency);


            invoiceData.global.lateDepartureArrivalCharges = totalLateDepartureArrivalCharges;
            invoiceData.global.lateDepartureArrivalChargesStr = reportHelper.formatCurrency(invoiceData.global.lateDepartureArrivalCharges, aviationInvoiceCurrency);
            invoiceData.global.lateDepartureArrivalChargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.lateDepartureArrivalCharges, aviationInvoiceCurrency);
            invoiceData.global.lateDepartureArrivalChargesAnsp = totalLateDepartureArrivalChargesAnsp;
            invoiceData.global.lateDepartureArrivalChargesAnspStr = reportHelper.formatCurrency(invoiceData.global.lateDepartureArrivalChargesAnsp, anspCurrency);

            invoiceData.global.extendedHoursSurcharge = totalExtendedHoursSurcharges;
            invoiceData.global.extendedHoursSurchargeStr = reportHelper.formatCurrency(invoiceData.global.extendedHoursSurcharge, aviationInvoiceCurrency);
            invoiceData.global.extendedHoursSurchargesStrWithCurrencySymbol = reportHelper.formatCurrency(invoiceData.global.extendedHoursSurcharge, aviationInvoiceCurrency);
            invoiceData.global.extendedHoursSurchargesAnsp = totalExtendedHoursSurchargesAnsp;
            invoiceData.global.extendedHoursSurchargesAnspStr = reportHelper.formatCurrency(invoiceData.global.extendedHoursSurchargesAnsp, anspCurrency);

            invoiceData.global.totalFlightsWithEnrouteCharges = totalFlightsWithEnrouteCharges;
            invoiceData.global.totalFlightsWithAerodromeCharges = totalFlightsWithAerodromeCharges;
            invoiceData.global.totalFlightsWithApproachCharges = totalFlightsWithApproachCharges;
            invoiceData.global.totalFlightsWithLandingCharges = totalFlightsWithLandingCharges;
            invoiceData.global.totalFlightsWithPassengerCharges = totalFlightsWithPassengerCharges;
            invoiceData.global.totalFlightsWithTaspCharges = totalFlightsWithTaspCharges;
            invoiceData.global.totalFlightsWithLateDepartureArrivalCharges = totalFlightsWithLateDepartureArrivalCharges;
            invoiceData.global.totalFlightsWithExtendedHoursCharges = totalFlightsWithExtendedHoursCharges;

            invoiceData.global.totalAirctaftRegistration = totalAirctaftRegistration;


            invoiceData.global.domesticEnrouteChargesStr = reportHelper.formatCurrency(domesticData.enrouteCharges, aviationInvoiceCurrency);
            invoiceData.global.domesticAerodromeChargesStr = reportHelper.formatCurrency(domesticData.aerodromeCharges, aviationInvoiceCurrency);
            invoiceData.global.domesticParkingChargesStr = reportHelper.formatCurrency(domesticData.parkingCharges, aviationInvoiceCurrency);
            invoiceData.global.domesticPassengerChargesStr = reportHelper.formatCurrency(domesticData.passengerCharges, aviationInvoiceCurrency);
            invoiceData.global.domesticTotalChargesStr = reportHelper.formatCurrency(domesticTotalCharges, aviationInvoiceCurrency);

            invoiceData.global.domesticEnrouteFlights = domesticData.enrouteFlights;
            invoiceData.global.domesticAerodromeFlights = domesticData.aerodromeFlights;
            invoiceData.global.domesticParkingFlights = domesticData.parkingFlights;
            invoiceData.global.domesticPassengerFlights = domesticData.passengerFlights;
            invoiceData.global.domesticTotalFlights = domesticData.totalFlights;

            invoiceData.global.internationalEnrouteChargesStr = reportHelper.formatCurrency(internationalData.enrouteCharges, aviationInvoiceCurrency);
            invoiceData.global.internationalAerodromeChargesStr = reportHelper.formatCurrency(internationalData.aerodromeCharges, aviationInvoiceCurrency);
            invoiceData.global.internationalParkingChargesStr = reportHelper.formatCurrency(internationalData.parkingCharges, aviationInvoiceCurrency);
            invoiceData.global.internationalPassengerChargesStr = reportHelper.formatCurrency(internationalData.passengerCharges, aviationInvoiceCurrency);
            invoiceData.global.internationalTotalChargesStr = reportHelper.formatCurrency(internationalTotalCharges, aviationInvoiceCurrency);

            invoiceData.global.internationalEnrouteFlights = internationalData.enrouteFlights;
            invoiceData.global.internationalAerodromeFlights = internationalData.aerodromeFlights;
            invoiceData.global.internationalParkingFlights = internationalData.parkingFlights;
            invoiceData.global.internationalPassengerFlights = internationalData.passengerFlights;
            invoiceData.global.internationalTotalFlights = internationalData.totalFlights;

            invoiceData.global.overflightEnrouteChargesStr = reportHelper.formatCurrency(overflightData.enrouteCharges, aviationInvoiceCurrency);
            invoiceData.global.overflightAerodromeChargesStr = reportHelper.formatCurrency(overflightData.aerodromeCharges, aviationInvoiceCurrency);
            invoiceData.global.overflightParkingChargesStr = reportHelper.formatCurrency(overflightData.parkingCharges, aviationInvoiceCurrency);
            invoiceData.global.overflightPassengerChargesStr = reportHelper.formatCurrency(overflightData.passengerCharges, aviationInvoiceCurrency);
            invoiceData.global.overflightTotalChargesStr = reportHelper.formatCurrency(overflightTotalCharges, aviationInvoiceCurrency);

            invoiceData.global.overflightEnrouteFlights = overflightData.enrouteFlights;
            invoiceData.global.overflightAerodromeFlights = overflightData.aerodromeFlights;
            invoiceData.global.overflightParkingFlights = overflightData.parkingFlights;
            invoiceData.global.overflightPassengerFlights = overflightData.passengerFlights;
            invoiceData.global.overflightTotalFlights = overflightData.totalFlights;

            // total amount, due NOT round yet as additional charges not applied until billing ledger created
            invoiceData.global.totalAmount = totalCharges;
            invoiceData.global.totalAmountAnsp = totalChargesAnsp;
        }

        return invoiceData;
    }

    private AircraftInfo processAircraftRegistration(final AircraftRegistration ar,
    												 final Account account,
    												 final LocalDateTime startDate,
    												 final LocalDateTime endDateInclusive,
    												 final Currency aviationInvoiceCurrency) {

    	AviationInvoiceData.AircraftInfo aircraftInfo = new AviationInvoiceData.AircraftInfo();
        aircraftInfo.manufacturer = ar.getAircraftType().getManufacturer();
        aircraftInfo.manufactureYearStr = reportHelper.formatYear(ar.getAircraftServiceDate());
        aircraftInfo.weight = ar.getMtowOverride();

        aircraftInfo.unifiedTaxCharges = 0.;

        try {

        	LocalDateTime yearManufacture = ar.getAircraftServiceDate();
            UnifiedTax ut = unifiedTaxService.findUnifiedTaxByValidityYearAndManufactureYear(startDate, yearManufacture);

            String rate = ut.getRate();

            Double taxAmount = null;
            FormulaEvaluatorUTImpl fe = new FormulaEvaluatorUTImpl(new JavascriptEngineUTFactory());
            try {
                taxAmount = fe.evalDouble(rate);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            if (taxAmount != null) {
                aircraftInfo.unifiedTaxCharges = (aircraftInfo.weight / 1000) * taxAmount;

                aircraftRegistrationService.updateAircraftRegistrationCOAByIdAndDates(
                    ar.getId(), startDate, endDateInclusive);
            }
        }
        catch(Exception e) {
        }

        return aircraftInfo;
    }

	String getInvoiceNameSuffix() {
        return invoiceNameSuffix;
    }

    public ReportFormat getReportFormat() {
        return reportFormat;
    }

    private AviationInvoiceData.FlightInfo processFlight(final FlightMovement fm,
                                                         final Account account,
                                                         final ChargeSelection chargesIncluded,
                                                         final Currency aviationInvoiceCurrency,
                                                         final Currency airNavigationChargesCurrency,
                                                         final Currency domesticPassengerChargesCurrency,
                                                         final Currency internationalPassengerChargesCurrency,
                                                         final List<KcaaAatisPermitNumber> invoicePermits) {

        Currency aerodromeChargesCurrency = cachedCurrencyConverter.getAerodromeCurrency(
            fm.getFlightCategoryScope(), fm.getFlightCategoryNationality());
        Currency approachChargesCurrency = cachedCurrencyConverter.getApproachCurrency(
            fm.getFlightCategoryScope(), fm.getFlightCategoryNationality());
        Currency lateArrivalDepartureChargesCurrency = cachedCurrencyConverter.getLateArrivalDepartureCurrency(
            fm.getFlightCategoryScope(), fm.getFlightCategoryNationality());
        Currency extendedHoursSurchargesCurrency = cachedCurrencyConverter.getExtendedHoursSurchargeCurrency(
            fm.getFlightCategoryScope(), fm.getFlightCategoryNationality());
        Currency taspChargeCurrency = fm.getTaspChargeCurrency();

    	//For each flight movement in the invoice, convert FM enroute charges from result currency to the invoice currency
    	//using the invoice currency conversion date specified
    	//determine aviation invoice currency conversion date
    	//Werner advised that the same currency conversion date should be used for other currency conversions in this method
    	//becuase they are flight movement related
    	SystemConfiguration flightMovementCurrencyConversionDate = null;

    	if (this.pointOfSale) {
        	//Werner: Cash invoice is an invoice generated from point of sale
        	//Credit invoice is an invoice generated from aviation billing
    		flightMovementCurrencyConversionDate = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.AVIATION_CASH_INVOICE_CONVERSION_DATE);

    	} else {
    		flightMovementCurrencyConversionDate = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.AVIATION_CREDIT_INVOICE_CONVERSTION_DATE);
    	}

    	LocalDateTime currencyConversionDate = null;
    	//day of flight,invoice date,current date
    	if (flightMovementCurrencyConversionDate.getCurrentValue().equals("day of flight")) {

    		currencyConversionDate = fm.getDateOfFlight();
    	} else if (flightMovementCurrencyConversionDate.getCurrentValue().equals("invoice date")) {
    		//at this point BillingLedger object is not yet created,
    		//but we know that for new BillingLedger InvoiceDateOfIssue will be ldtNow in do_createBillingLedger method
    		currencyConversionDate = ldtNow;
    	} else if (flightMovementCurrencyConversionDate.getCurrentValue().equals("current date")) {
    		currencyConversionDate = ldtNow;
    	}

    	CachedCurrencyConverter flightMovementCurrencyConverter = new CachedCurrencyConverter (this.currencyUtils, currencyConversionDate);

        final AviationInvoiceData.FlightInfo flightInfo = new AviationInvoiceData.FlightInfo();
        flightInfo.accountId = account.getId();
        flightInfo.accountName = account.getName();
        flightInfo.accountIcaoCode = account.getIcaoCode();
        flightInfo.flightMovementId = fm.getId();
        flightInfo.flightDateStr = fm.getDateOfFlight().format(dateFormatter);
        flightInfo.billingDateStr = fm.getBillingDate() != null ? fm.getBillingDate().format(dateFormatter) : fm.getDateOfFlight().format(dateFormatter);
        flightInfo.flightId = fm.getFlightId();
        flightInfo.regNum = fm.getItem18RegNum();
        flightInfo.flightId = fm.getFlightId();
        flightInfo.flightType = fm.getFlightType();
        flightInfo.aircraftType = fm.getAircraftType();
        flightInfo.distanceUnitOfMeasure = distanceUnitOfMeasure;
        flightInfo.mtowUnitOfMeasure = mtowUnitOfMeasure;

        if (fm.getFlightmovementCategory()!=null) {
        	flightInfo.flightCategory = fm.getFlightmovementCategory().getShortName();

        }

        // entry, exit, and mid points must consider the flight movement type
        String entryPoint;
        String exitPoint;
        String midPoints = null;
        if (fm.getMovementType().equals(FlightMovementType.INT_ARRIVAL) || fm.getMovementType().equals(FlightMovementType.REG_ARRIVAL)) {
            entryPoint = fm.getBillableEntryPoint();
            exitPoint = fm.getActualDestAd() != null ? fm.getActualDestAd() : fm.getBillableExitPoint();
        } else if (fm.getMovementType().equals(FlightMovementType.INT_DEPARTURE) || fm.getMovementType().equals(FlightMovementType.REG_DEPARTURE)) {
            entryPoint = fm.getActualDepAd() != null ? fm.getActualDepAd() : fm.getBillableEntryPoint();
            exitPoint = fm.getBillableExitPoint();
        } else if (fm.getDeltaFlight()) {
            List<DeltaFlightVO> item18Dep = Item18Parser.destFieldToMap(fm.getItem18Dep());
            List<DeltaFlightVO> item18Dest = Item18Parser.destFieldToMap(fm.getItem18Dest());
            entryPoint = resolveDeltaEntryPoint(fm.getDepAd(), item18Dep);
            exitPoint = resolveDeltaExitPoint(fm.getDestAd(), item18Dest);
            midPoints = resolveDeltaMidPoints(item18Dep, item18Dest);
        } else {
            if (billingOrgCode == BillingOrgCode.INAC) {
                entryPoint = fm.getBillableEntryPoint() != null ? fm.getBillableEntryPoint() : fm.getActualDepAd();
                exitPoint = fm.getBillableExitPoint() != null ? fm.getBillableExitPoint() : fm.getActualDestAd();
            } else {
                entryPoint = fm.getActualDepAd() != null ? fm.getActualDepAd() : fm.getBillableEntryPoint();
                exitPoint = fm.getActualDestAd() != null ? fm.getActualDestAd() : fm.getBillableExitPoint();
            }
        }
        flightInfo.entryPoint = entryPoint;
        flightInfo.exitPoint = exitPoint;
        flightInfo.midPoints = midPoints;

        String entryTime = null;
        String exitTime = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        if (fm.getEntryTime() != null) {
            entryTime = fm.getEntryTime().format(formatter);
        }

        if (fm.getExitTime() != null) {
            exitTime = fm.getEntryTime().format(formatter);
        }
        flightInfo.entryTime = entryTime;
        flightInfo.exitTime = exitTime;

        flightInfo.crossDist = fm.getBillableCrossingDist();
        flightInfo.crossingDistanceToMinimum = fm.getCrossingDistanceToMinimum();
        if (flightInfo.crossDist != null && distanceUnitOfMeasure.equalsIgnoreCase("NM")) {
            flightInfo.crossDist = flightInfo.crossDist * ReportHelper.TO_NM;
        }
        flightInfo.crossDistStr
            = String.format("%.2f", flightInfo.crossDist);

        flightInfo.mtow = fm.getActualMtow();
        if (flightInfo.mtow != null && mtowUnitOfMeasure.equalsIgnoreCase("KG")) {
            flightInfo.mtow = flightInfo.mtow * ReportHelper.TO_KG;
        }

        // TASP details(from flight_type)
        //   S if scheduled air transport
        //   N if non-scheduled air transport
        //   G if general aviation
        //   M if military
        //   X other flights
        final String ft = fm.getFlightType() == null ? "" : fm.getFlightType().toUpperCase(Locale.US);
        flightInfo.taspTypeScheduled = ft.contains("S");
        flightInfo.taspTypeNonScheduled = ft.contains("N");
        flightInfo.taspTypePrivate = ft.contains("G");
        flightInfo.taspTypeMilitary = ft.contains("M");

        flightInfo.departureLocation = reportHelper.getLocation(fm, true);

        flightInfo.departureTimeStr = reportHelper.formatFlightTime(fm.getDepTime());
        flightInfo.actualDepartureTimeStr = reportHelper.formatFlightTime(fm.getActualDepartureTime());

        // destination aerodrome/time OR first item18 DEST/ point if destination aerodrome is 'ZZZZ' OR last if delta
        final DeltaFlightVO item18DeltaDest = fm.getDeltaFlight() ? Item18Parser.mapDeltaDest(fm.getItem18Dest()) : null;
        flightInfo.arrivalLocation = reportHelper.getLocation(fm, false);

        flightInfo.arrivalTimeStr = reportHelper.formatFlightTime(MiscUtils.evl(
            fm.getArrivalTime(), item18DeltaDest != null ? item18DeltaDest.getArrivaAt() : null));

        flightInfo.billingEntryDateStr = flightInfo.billingDateStr;
        flightInfo.billingExitDateStr = flightInfo.billingDateStr;

        // departure passenger count
        //TODO Helen advised that fm.getMovementType will not be used in the future
        switch (fm.getMovementType()) {
            case DOMESTIC:
            case REG_DEPARTURE:
            case INT_DEPARTURE:
                flightInfo.invoicePaxAllowed = fm.getPassengersChargeableDomestic() != null || fm.getPassengersChargeableIntern() != null;
                break;
            default:
                flightInfo.invoicePaxAllowed = false;
        }
        flightInfo.departurePassengerCount = nvl(fm.getPassengersChargeableDomestic(), 0) + nvl(fm.getPassengersChargeableIntern(), 0);
        if (flightInfo.departurePassengerCount == 0) {
            flightInfo.departurePassengerCount = null;
        }

        // FIXME: can't calculate without correlating departure and arrival flights
        flightInfo.arrivalPassengerCount = null;

        // transit passenger count
        flightInfo.transitPassengerCount = fm.getPassengersTransitAdult();

        // child count
        flightInfo.infantPassengerCount = fm.getPassengersChild();

        // false warning, these variables are used in two places below
        double totalFlightCharges = 0d;
        double totalFlightChargesAnsp = 0d;

        if(chargesIncluded == ALL || chargesIncluded == ONLY_ENROUTE) {
            boolean iataInvoiceEnabled = systemConfigurationService.getBoolean(SystemConfigurationItemName.IATA_INVOICING_SUPPORT);

            // Enroute charges: include only non-IATA members that don't yet have an invoice
            // These charges are in USD in FM table
            // if iata invoice support is false we have to ignore the iata member check
            if (((!iataInvoiceEnabled) || (account.getIataMember() != null && !account.getIataMember())) && fm.getEnrouteInvoiceId() == null) {

                //before enrouteCharges were calculated in USD. Now they are calculated in FlightmovementCategory's enrouteResultCurrency
                //so now we need to convert from enrouteResultCurrency to invoice currency

            	// 2019-04-30 EANA requirement: en-route charges are calculated as the sum of the flight en-route charge and
            	// the charge for the remainder between the charges for the cumulative flights and minimum charged distance of 200 km
            	Double enrouteChargeCumulative = fm.getEnrouteCharges();
            	if(enrouteChargeCumulative != null && fm.getEnrouteCostToMinimum() != null) {
            		enrouteChargeCumulative = enrouteChargeCumulative + fm.getEnrouteCostToMinimum();
            	}
                Currency enrouteResultCurrency = fm.getFlightmovementCategory().getEnrouteResultCurrency();
                flightInfo.enrouteCharges = zeroToNull(flightMovementCurrencyConverter.convertCurrency(enrouteChargeCumulative, enrouteResultCurrency, aviationInvoiceCurrency));
                flightInfo.enrouteChargesStr = reportHelper.formatCurrency(flightInfo.enrouteCharges, aviationInvoiceCurrency);
                flightInfo.enrouteChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.enrouteCharges, aviationInvoiceCurrency);

                flightInfo.enrouteChargesAnsp = zeroToNull(flightMovementCurrencyConverter.toANSPCurrency(enrouteChargeCumulative, enrouteResultCurrency));
                flightInfo.enrouteChargesAnspStr = reportHelper.formatCurrency(flightInfo.enrouteChargesAnsp, anspCurrency);

                flightInfo.enrouteChargesIncluded = true;
            } else {
                LOG.debug("Flight movement #{} regNum={}: this is an IATA flight, enroute charges will not be included in this invoice; generate IATA invoice to finalize the invoicing of this flight movement", fm.getId(), fm.getItem18RegNum());
            }

            // Other charges: include flights that don't yet have an invoice
            if (fm.getOtherInvoiceId() == null) {
                flightInfo.otherChargesIncluded = true;

                // flight movement tasp charges
                // tasp charges are stored in ANSP or USD
                flightInfo.taspCharges = zeroToNull(flightMovementCurrencyConverter.convertCurrency(fm.getTaspCharge(), taspChargeCurrency, aviationInvoiceCurrency));
                flightInfo.taspChargesStr = reportHelper.formatCurrency(flightInfo.taspCharges, aviationInvoiceCurrency);
                flightInfo.taspChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.taspCharges, aviationInvoiceCurrency);
                flightInfo.taspChargesAnsp = zeroToNull(flightMovementCurrencyConverter.convertCurrency(fm.getTaspCharge(), taspChargeCurrency, anspCurrency));
                flightInfo.taspChargesAnspStr = reportHelper.formatCurrency(flightInfo.taspChargesAnsp, anspCurrency);

                // flight movement aerodrome charges
                // aerodrome charges are stored in air navigation charges currency
                flightInfo.aerodromeCharges = zeroToNull(cachedCurrencyConverter.convertCurrency(fm.getAerodromeCharges(), aerodromeChargesCurrency, aviationInvoiceCurrency));
                flightInfo.aerodromeChargesStr = reportHelper.formatCurrency(flightInfo.aerodromeCharges, aviationInvoiceCurrency);
                flightInfo.aerodromeChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.aerodromeCharges, aviationInvoiceCurrency);

                flightInfo.aerodromeChargesAnsp = cachedCurrencyConverter.toANSPCurrency(fm.getAerodromeCharges(), aerodromeChargesCurrency);
                flightInfo.aerodromeChargesAnspStr = reportHelper.formatCurrency(flightInfo.aerodromeChargesAnsp, anspCurrency);

                // flight movement approach charges
                // approach charges are stored in air navigation charges currency
                flightInfo.approachCharges = cachedCurrencyConverter.convertCurrency(fm.getApproachCharges(), approachChargesCurrency, aviationInvoiceCurrency);
                flightInfo.approachChargesStr = reportHelper.formatCurrency(flightInfo.approachCharges, aviationInvoiceCurrency);
                flightInfo.approachChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.approachCharges, aviationInvoiceCurrency);

                flightInfo.approachChargesAnsp = cachedCurrencyConverter.toANSPCurrency(fm.getApproachCharges(), approachChargesCurrency);
                flightInfo.approachChargesAnspStr = reportHelper.formatCurrency(flightInfo.approachChargesAnsp, anspCurrency);

                // flight movement landing charges
                // landing charges are sum of of approach and aerodrome charges
                flightInfo.landingCharges = zeroToNull(nvl(flightInfo.aerodromeCharges, 0d) + nvl(flightInfo.approachCharges, 0d));
                flightInfo.landingChargesStr = reportHelper.formatCurrency(flightInfo.landingCharges, aviationInvoiceCurrency);
                flightInfo.landingChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.landingCharges, aviationInvoiceCurrency);

                flightInfo.landingChargesAnsp = zeroToNull(nvl(flightInfo.aerodromeChargesAnsp, 0d) + nvl(flightInfo.approachChargesAnsp, 0d));
                flightInfo.landingChargesAnspStr = reportHelper.formatCurrency(flightInfo.landingChargesAnsp, anspCurrency);

                // flight movement parking charges
                // parking charges are stored in ANSP
                flightInfo.parkingCharges = zeroToNull(flightMovementCurrencyConverter.convertCurrency(fm.getParkingCharges(), anspCurrency, aviationInvoiceCurrency));
                flightInfo.parkingChargesStr = reportHelper.formatCurrency(flightInfo.parkingCharges, aviationInvoiceCurrency);
                flightInfo.parkingChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.parkingCharges, aviationInvoiceCurrency);
                flightInfo.parkingChargesAnsp = zeroToNull(fm.getParkingCharges());
                flightInfo.parkingChargesAnspStr = reportHelper.formatCurrency(flightInfo.parkingChargesAnsp, anspCurrency);

                // flight movement late departure/arrival charges
                // late departure/arrival charges are stored in air navigation charges currency
                final Double lateDepartureArrivalCharges = zeroToNull(nvl(fm.getLateArrivalCharges(), 0d) + nvl(fm.getLateDepartureCharges(), 0d));
                flightInfo.lateDepartureArrivalCharges = flightMovementCurrencyConverter.convertCurrency(lateDepartureArrivalCharges, lateArrivalDepartureChargesCurrency, aviationInvoiceCurrency);
                flightInfo.lateDepartureArrivalChargesStr = reportHelper.formatCurrency(flightInfo.lateDepartureArrivalCharges, aviationInvoiceCurrency);
                flightInfo.lateDepartureArrivalChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.lateDepartureArrivalCharges, aviationInvoiceCurrency);
                flightInfo.lateDepartureArrivalChargesAnsp = zeroToNull(flightMovementCurrencyConverter.toANSPCurrency(lateDepartureArrivalCharges, lateArrivalDepartureChargesCurrency));
                flightInfo.lateDepartureArrivalChargesAnspStr = reportHelper.formatCurrency(flightInfo.lateDepartureArrivalChargesAnsp, anspCurrency);

                // flight movement extended hours charges
                // extended hours charges are stored in air navigation charges currency
                final Double extendedHoursSurcharges = zeroToNull(nvl(fm.getExtendedHoursSurcharge(), 0d));
                flightInfo.extendedHoursSurcharge = flightMovementCurrencyConverter.convertCurrency(extendedHoursSurcharges, extendedHoursSurchargesCurrency, aviationInvoiceCurrency);
                flightInfo.extendedHoursSurchargeStr = reportHelper.formatCurrency(flightInfo.extendedHoursSurcharge, aviationInvoiceCurrency);
                flightInfo.extendedHoursSurchargeStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.extendedHoursSurcharge, aviationInvoiceCurrency);
                flightInfo.extendedHoursSurchargeAnsp = zeroToNull(flightMovementCurrencyConverter.toANSPCurrency(extendedHoursSurcharges, extendedHoursSurchargesCurrency));
                flightInfo.extendedHoursSurchargeAnspStr = reportHelper.formatCurrency(flightInfo.extendedHoursSurchargeAnsp, anspCurrency);
            }

            // sum up total flight movement charges
            // landing charges includes aerodrome and approach charges
            totalFlightCharges += nvl(flightInfo.enrouteCharges, 0d) +
                nvl(flightInfo.taspCharges, 0d) +
                nvl(flightInfo.landingCharges, 0d) +
                nvl(flightInfo.parkingCharges, 0d) +
                nvl(flightInfo.lateDepartureArrivalCharges, 0d) +
                nvl(flightInfo.extendedHoursSurcharge, 0d);

            totalFlightChargesAnsp += nvl(flightInfo.enrouteChargesAnsp, 0d) +
                nvl(flightInfo.taspChargesAnsp, 0d) +
                nvl(flightInfo.landingChargesAnsp, 0d) +
                nvl(flightInfo.parkingChargesAnsp, 0d) +
                nvl(flightInfo.lateDepartureArrivalChargesAnsp, 0d) +
                nvl(flightInfo.extendedHoursSurchargeAnsp, 0d);
        }

        if (fm.getPassengerInvoiceId() == null && (chargesIncluded == ALL || chargesIncluded == ONLY_PAX)) {

            // Passenger charges: include flights that don't yet have an invoice
            // These are stored in USD in the database
            if (fm.getPassengerInvoiceId() == null) {
                flightInfo.passengerChargesIncluded = true;

                // flight movement passenger charges
                // passenger charges are sum of of domestic and international passenger charges
                // domestic/international charges are stored in domestic/international passenger charges currencies
                final Double domesticPassengerCharges = flightMovementCurrencyConverter
                    .convertCurrency(fm.getDomesticPassengerCharges(), domesticPassengerChargesCurrency, aviationInvoiceCurrency);
                final Double internationalPassengerCharges = flightMovementCurrencyConverter
                    .convertCurrency(fm.getInternationalPassengerCharges(), internationalPassengerChargesCurrency, aviationInvoiceCurrency);
                final Double domesticPanssgerChargesAnsp = flightMovementCurrencyConverter
                    .toANSPCurrency(fm.getDomesticPassengerCharges(), domesticPassengerChargesCurrency);
                final Double internationalPassengerChargesAnsp = flightMovementCurrencyConverter
                    .toANSPCurrency(fm.getInternationalPassengerCharges(), internationalPassengerChargesCurrency);

                flightInfo.passengerCharges = zeroToNull(nvl(domesticPassengerCharges, 0d) + nvl(internationalPassengerCharges, 0d));
                flightInfo.passengerChargesStr = reportHelper.formatCurrency(flightInfo.passengerCharges, aviationInvoiceCurrency);
                flightInfo.passengerChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.passengerCharges, aviationInvoiceCurrency);
                flightInfo.passengerChargesAnsp = zeroToNull(nvl(domesticPanssgerChargesAnsp, 0d) + nvl(internationalPassengerChargesAnsp, 0d));
                flightInfo.passengerChargesAnspStr = reportHelper.formatCurrency(flightInfo.passengerChargesAnsp, anspCurrency);

                flightInfo.domesticPassengerCharges = reportHelper.formatCurrency(zeroToNull(domesticPassengerCharges), aviationInvoiceCurrency);
                flightInfo.internationalPassengerCharges = reportHelper.formatCurrency(zeroToNull(internationalPassengerCharges), aviationInvoiceCurrency);
            }

            if(flightInfo.passengerCharges != null &&(chargesIncluded == ONLY_PAX || reportHelper.passengerChargesInInvoiceTotal())) {
                totalFlightCharges += flightInfo.passengerCharges;
                totalFlightChargesAnsp += flightInfo.passengerChargesAnsp;
            }
        }

        // Total charges for this flight
        flightInfo.totalCharges = totalFlightCharges;
        flightInfo.totalChargesStr = reportHelper.formatCurrency(flightInfo.totalCharges, aviationInvoiceCurrency);
        flightInfo.totalChargesStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(flightInfo.totalCharges, aviationInvoiceCurrency);

        flightInfo.totalChargesAnsp = totalFlightChargesAnsp;
        flightInfo.totalChargesAnspStr = reportHelper.formatCurrency(flightInfo.totalChargesAnsp, anspCurrency);

        if (invoicePermits != null) {
            invoicePermits.stream().filter(permit ->
                permit.getFlightMovement().getId().equals(flightInfo.flightMovementId)
            ).findFirst().ifPresent(invoicePermit -> flightInfo.aatisNumber = invoicePermit.getInvoicePermitNumber());
        }

        return flightInfo;
    }

    /**
     * Calculate account credits, invoice overdue penalties, and amount due after billing ledger creation
     */
    private void do_setCreditAndPenaltyAndTotalAmountAndAmountDue(AviationInvoiceData invoiceData, BillingLedger billingLedger, final boolean preview) {
        final Currency invoiceCurrency = billingLedger.getInvoiceCurrency();
        final double exchangeRateToUsd = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, usdCurrency);
        final double exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);

        invoiceData.global.invoiceCurrencyInWords = invoiceCurrency.getCurrencyName() != null ? String.format ("%s%s", invoiceCurrency.getCurrencyName(), "s") : "";

        //account credits
        invoiceData.accountCredits.addAll(invoiceReportUtility.getAppliedAccountCreditAsAdditionalChargeList(billingLedger));

        // calculate total account credit from account credits amount
        Double totalAccountCredit = Math.abs(invoiceData.accountCredits.stream().mapToDouble(c -> c.amount).sum());
        invoiceData.global.totalAccountCredit = String.format(TWO_DECIMALS, totalAccountCredit);

        // when previewing an invoice - billing ledger is created but not saved that is why it doesn't
        // have an id, so an account credit cannot be found through a Transaction Payment
        if (preview && invoiceData.global.totalAccountCredit.equals("0.00")) {
            Double accountCredit = billingLedger.getAccountCredit();
            if (accountCredit != null) {
                invoiceData.global.totalAccountCredit = String.format(TWO_DECIMALS, Math.abs(accountCredit));
            }
        }

        //add invoice overdue penalties
        invoiceData.additionalCharges.addAll(invoiceReportUtility.getAppliedPenaltyAsAdditionalChargeList(billingLedger));

        //penalized invoices
        invoiceData.overduePenaltyInvoices = invoiceReportUtility.getOverduePenaltyInvoiceList(billingLedger);

        // calculate total penalties from additional charges amount
        Double totalPenalties = invoiceData.additionalCharges.stream().filter(Objects::nonNull)
            .mapToDouble(c -> c.amount).sum();
        invoiceData.global.totalPenalty = String.format(TWO_DECIMALS, totalPenalties);

        // calculate total outstanding amount from overdue invoice penalties
        Double totalOutstandingAmount = 0d;
        Double totalOutstandingAmountAnsp = 0d;
        for (OverduePenaltyInvoice overduePenaltyInvoice : invoiceData.overduePenaltyInvoices) {
            if (overduePenaltyInvoice == null)
                continue;
            totalOutstandingAmount += overduePenaltyInvoice.amountOwing;
            totalOutstandingAmountAnsp += overduePenaltyInvoice.amountOwingAnsp;
        }
        invoiceData.global.totalOutstandingAmount = String.format(TWO_DECIMALS, totalOutstandingAmount);

        // set invoice total amounts
        Double totalAmount = billingLedger.getInvoiceAmount();
        Double totalAmountAnsp = cachedCurrencyConverter.toANSPCurrency(totalAmount, invoiceCurrency);
        Double totalAmountUsd = cachedCurrencyConverter.toUSDCurrency(totalAmount, invoiceCurrency);

        // update invoice data total amount and round appropriately if necessary
        invoiceData.global.totalAmount = roundingUtils.calculateSingleRoundedValue(totalAmount, invoiceCurrency, true);
        invoiceData.global.totalAmountStr = reportHelper.formatCurrency(invoiceData.global.totalAmount, invoiceCurrency);
        invoiceData.global.totalAmountStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(invoiceData.global.totalAmount, invoiceCurrency);
        invoiceData.global.totalAmountAnsp =  roundingUtils.calculateSingleRoundedValue(totalAmountAnsp, anspCurrency, true);
        invoiceData.global.totalAmountAnspStr = reportHelper.formatCurrency(invoiceData.global.totalAmountAnsp, anspCurrency);
        invoiceData.global.totalAmountUsd =  roundingUtils.calculateSingleRoundedValue(totalAmountUsd, usdCurrency, true);
        invoiceData.global.totalAmountUsdStr = reportHelper.formatCurrency(invoiceData.global.totalAmountUsd, usdCurrency);

        // total amount in words: total amount spelled out in words using supported locales
        invoiceData.global.totalAmountStrInWords = reportHelper.formatAmountInFullText(
            invoiceData.global.totalAmount, LocaleUtils.ENGLISH);
        invoiceData.global.totalAmountStrInWordsSpanish = reportHelper.formatAmountInFullText(
            invoiceData.global.totalAmount, LocaleUtils.SPANISH);
        invoiceData.global.totalAmountStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.totalAmount, invoiceCurrency, LocaleUtils.ENGLISH);
        invoiceData.global.totalAmountStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.totalAmount, invoiceCurrency, LocaleUtils.SPANISH);

        // set invoice amount dues
        Double amountDue = billingLedger.getAmountOwing();

        Double amountDueAnsp = null;
        Double amountDueUsd = null;
        if (billingInterval == BillingInterval.ANNUALLY || billingInterval == BillingInterval.PARTIALLY) {
            amountDueAnsp = cachedCurrencyConverter.toANSPCurrency(amountDue, invoiceCurrency);
            amountDueUsd = cachedCurrencyConverter.toANSPCurrency(amountDue, invoiceCurrency);
        }else{
            amountDueAnsp = cachedCurrencyConverter.toANSPCurrency(amountDue, invoiceCurrency);
            amountDueUsd = cachedCurrencyConverter.toUSDCurrency(amountDue, invoiceCurrency);
        }


        // KCAA must **DISPLAY** total outstanding amount in amount due
        if (billingOrgCode == BillingOrgCode.KCAA) {
            amountDue += totalOutstandingAmount;
            amountDueAnsp += totalOutstandingAmountAnsp;
            amountDueUsd += cachedCurrencyConverter.toUSDCurrency(totalOutstandingAmount, invoiceCurrency);
        }

        // update invoice data amount due and round appropriately if necessary
        invoiceData.global.amountDue = roundingUtils.calculateSingleRoundedValue(amountDue, invoiceCurrency, true);
        invoiceData.global.amountDueStr = reportHelper.formatCurrency(invoiceData.global.amountDue, invoiceCurrency);
        invoiceData.global.amountDueStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol(invoiceData.global.amountDue, invoiceCurrency);
        invoiceData.global.amountDueAnsp = roundingUtils.calculateSingleRoundedValue(amountDueAnsp, anspCurrency, true);
        invoiceData.global.amountDueAnspStr = reportHelper.formatCurrency(invoiceData.global.amountDueAnsp, anspCurrency);
        invoiceData.global.amountDueUsd = roundingUtils.calculateSingleRoundedValue(amountDueUsd, usdCurrency, true);
        invoiceData.global.amountDueUsdStr = reportHelper.formatCurrency(invoiceData.global.amountDueUsd, usdCurrency);

        // amount due in words: amount due spelled out in words using supported locales
        invoiceData.global.amountDueStrInWords = reportHelper.formatAmountInFullText(
            invoiceData.global.amountDue, LocaleUtils.ENGLISH);
        invoiceData.global.amountDueStrInWordsSpanish = reportHelper.formatAmountInFullText(
            invoiceData.global.amountDue, LocaleUtils.SPANISH);
        invoiceData.global.amountDueStrInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.amountDue, invoiceCurrency, LocaleUtils.ENGLISH);
        invoiceData.global.amountDueStrInWordsWithCurrencySymbolSpanish = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.amountDue, invoiceCurrency, LocaleUtils.SPANISH);

        // deprecated because of misleading name - should use global.amountDueStrInWords
        invoiceData.global.totalAmountInWords = reportHelper.formatAmountInFullText(
            invoiceData.global.amountDue, LocaleUtils.ENGLISH);
        // deprecated because of misleading name - should use global.amountDueStrInWordsWithCurrencySymbol
        invoiceData.global.totalAmountInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.amountDue, invoiceCurrency, LocaleUtils.ENGLISH);
        // deprecated because of inconsistent name - should use global.amountDueStrInWords
        invoiceData.global.amountDueInWords = reportHelper.formatAmountInFullText(
            invoiceData.global.amountDue, LocaleUtils.ENGLISH);
        // deprecated because of inconsistent name - should use global.amountDueStrInWordsWithCurrencySymbol
        invoiceData.global.amountDueInWordsWithCurrencySymbol = reportHelper.formatAmountInFullTextWithCurrencyCode(
            invoiceData.global.amountDue, invoiceCurrency, LocaleUtils.ENGLISH);

        if (inverseCurrencyRate) {
            invoiceData.global.exchageRate = String.format("%.5f", exchangeRateToAnsp);
            invoiceData.global.exchageRateUsd = String.format("%.5f", 1/exchangeRateToUsd);
        } else {
            invoiceData.global.exchageRate = String.format("%.5f", 1/exchangeRateToAnsp);
            invoiceData.global.exchageRateUsd = String.format("%.5f", exchangeRateToUsd);
        }

        if (billingOrgCode == BillingOrgCode.INAC && invoiceCurrency.getCurrencyCode().equals("USD")) {
            double owingInAlternativeCurr = cachedCurrencyConverter.convertCurrency (invoiceData.global.amountDue, invoiceCurrency, eurCurrency);
            invoiceData.global.amountDueAlt = roundingUtils.calculateSingleRoundedValue(owingInAlternativeCurr, eurCurrency, false);
            invoiceData.global.amountDueAltStr = reportHelper.formatCurrency(invoiceData.global.amountDueAlt, eurCurrency);
            invoiceData.global.amountDueAltStrWithCurrencySymbol = reportHelper.formatCurrencyWithSymbol (invoiceData.global.amountDueAlt, eurCurrency);
            invoiceData.global.invoiceCurrencyAltCode = eurCurrency.getCurrencyCode();
        }
    }

    /**
     * Create a billing ledger record
     */
    @SuppressWarnings("squid:S00107") // Methods should not have too many parameters
    private BillingLedger do_createBillingLedger(
            final Account account,
            final AviationInvoiceData invoiceData,
            final List <FlightMovement> accountFlights,
            final ChargeSelection chargeSelection,
            final Currency invoiceCurrency,
            final Currency targetCurrency,
            final FlightmovementCategory flightmovementCategory,
            final List<KcaaAatisPermitNumber> invoicePermits,
            final boolean createdFromPointOfSale) {

        final LocalDateTime dueDate = ldtNow.plusDays (account.getPaymentTerms());

		final double exchangeRate;
		final double exchangeRateToAnsp;

		if (billingInterval == BillingInterval.ANNUALLY || billingInterval == BillingInterval.PARTIALLY) {
 			exchangeRate = currencyUtils.getApplicableRate(invoiceCurrency, targetCurrency, startDate);
			exchangeRateToAnsp = currencyUtils.getApplicableRate (invoiceCurrency, anspCurrency, startDate);
		}
		else {
 			exchangeRate = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, targetCurrency);
			exchangeRateToAnsp = cachedCurrencyConverter.getExchangeRate (invoiceCurrency, anspCurrency);
		}

        // Map of invoice flight data indexed by flight ID
        final Map <Integer, AviationInvoiceData.FlightInfo> flightInfoMap = new HashMap<>();
        invoiceData.flightInfoList.forEach(fi-> flightInfoMap.put(fi.flightMovementId, fi));

        // create billing ledger
        final BillingLedger bl = new BillingLedger();
        bl.setAccount(account);
        bl.setBillingCenter(currentUser != null ? currentUser.getBillingCenter() : null);
        bl.setInvoicePeriodOrDate(endDateInclusive);
        bl.setInvoiceType(ChargeSelection.ONLY_PAX == chargeSelection ?
            InvoiceType.PASSENGER.toValue() : InvoiceType.AVIATION_NONIATA.toValue());
        bl.setInvoiceStateType (initialLedgerState.toValue());
        bl.setPaymentDueDate (dueDate);
        bl.setUser (currentUser);
        bl.setInvoiceAmount (invoiceData.global.totalAmount);
        bl.setInvoiceCurrency (invoiceCurrency);
        bl.setInvoiceExchange (exchangeRate);
        bl.setTargetCurrency(targetCurrency);
        bl.setInvoiceExchangeToAnsp (exchangeRateToAnsp);
        bl.setInvoiceDateOfIssue (ldtNow);
        bl.setExported (false);
        bl.setAmountOwing (invoiceData.global.totalAmount);
        if(!preview) {
            bl.setInvoiceNumber (invoiceData.global.realInvoiceNumber);
        }
        bl.setPointOfSale(createdFromPointOfSale);
        if (account.getCashAccount()) {
            bl.setPaymentMode(TransactionPaymentMechanism.cash.toString());
        } else {
            bl.setPaymentMode(TransactionPaymentMechanism.credit.toString());
        }

        if (do_checkIfAviationInvoicingIsByFlightmovementCategory()) {
        	//it's important to set FlightmovementCategory property if aviation invoicing is set by FlightmovementCategory
        	bl.setFlightmovementCategory(flightmovementCategory);
        } else {
        	//it's important to set FlightmovementCategory property to null if aviation invoicing is by account
        	bl.setFlightmovementCategory(null);
        }

        final boolean allowApplyPenalty = this.applyPenaltyOn.getCurrentValue().equalsIgnoreCase("Next invoice");

        // document name, type and content(PDF) will be set later, penalties will be applied and invoice total
        // amounts rounded based on system configuration settings
        final BillingLedger billingLedger = billingLedgerService.createBillingLedgerAndTransaction(bl, allowApplyPenalty, preview);

        // if not in preview mode, link flights to this ledger entry
        if (!preview) accountFlights.forEach(fm -> linkFlightMovementAndUpdateStatus(fm, billingLedger, flightInfoMap));

        // link extensions to this ledger entry
        handleBillingLedgerExtensions(invoicePermits, billingLedger);

        LOG.info("Created aviation billing ledger record id={}, amount={} {}",
                billingLedger.getId(), billingLedger.getInvoiceAmount(), billingLedger.getInvoiceCurrency().getCurrencyCode());
        return billingLedger;
    }

    /**
     * Link flight movement to billing ledger and update flight movement status.
     */
    private void linkFlightMovementAndUpdateStatus(
        final FlightMovement flightMovement, final BillingLedger billingLedger,
        final Map <Integer, AviationInvoiceData.FlightInfo> flightInfoMap
    ) {
        if (!flightInfoMap.containsKey(flightMovement.getId())) return;

        final AviationInvoiceData.FlightInfo fi = flightInfoMap.get(flightMovement.getId());

        if(fi.enrouteChargesIncluded) flightMovement.setEnrouteInvoiceId(billingLedger.getId());
        if(fi.passengerChargesIncluded) flightMovement.setPassengerInvoiceId(billingLedger.getId());
        if(fi.otherChargesIncluded) flightMovement.setOtherInvoiceId(billingLedger.getId());

        reportHelper.updateFlightStatusToMatchInvoice(billingLedger, flightMovement);
        LOG.debug("Flight movement #{} regNum={}, updated status={}", flightMovement.getId(),
            flightMovement.getItem18RegNum(), flightMovement.getStatus());
    }

    /**
     * Create a payment transaction
     */
    private Transaction do_createPayment(
            final Account account,
            final BillingLedger billingLedger,
            final InvoicePaymentParameters invoicePaymentParameters) {

        final Transaction transaction = new Transaction();

        // account
        transaction.setAccount(account);

        transaction.setKraClerkName(invoicePaymentParameters.getKraClerkName());
        transaction.setKraReceiptNumber(invoicePaymentParameters.getKraReceiptNumber());

        // type = credit
        transaction.setTransactionType(reportHelper.getTransactionTypeCredit());

        // amount: must equal to invoice amount
        if(invoicePaymentParameters.getAmount() == null) {
            throw new CustomParametrizedException("amount is missing", "amount");
        }

        final Double requiredAmount = roundingUtils.calculateSingleRoundedValue(billingLedger.getAmountOwing(), billingLedger.getInvoiceCurrency(), true);
        if(!requiredAmount.equals(invoicePaymentParameters.getAmount())) {
            throw new CustomParametrizedException("invalid amount, expecting" + "\"" + requiredAmount + "\"", "amount");
        }
        transaction.setAmount(-invoicePaymentParameters.getAmount());

        // currency: must equal to invoice currency
        if(invoicePaymentParameters.getCurrency() != null && !invoicePaymentParameters.getCurrency().equals(billingLedger.getInvoiceCurrency())) {
            throw new CustomParametrizedException("invalid currency, expecting" + "\"" + billingLedger.getInvoiceCurrency().getCurrencyCode() + "\"", "currency");
        }
        transaction.setCurrency (billingLedger.getInvoiceCurrency());
        transaction.setExchangeRate(billingLedger.getInvoiceExchange());
        transaction.setTargetCurrency(billingLedger.getTargetCurrency());
        transaction.setExchangeRateToAnsp(billingLedger.getInvoiceExchangeToAnsp());

        // billing ledger id
        transaction.setBillingLedgerIds(Arrays.asList(billingLedger.getId()));

        // description:
        transaction.setDescription(invoicePaymentParameters.getDescription());

        // payment mechanism
        TransactionPaymentMechanism mechanism = null;
        if(invoicePaymentParameters.getPaymentMechanism() != null) {
            switch(invoicePaymentParameters.getPaymentMechanism()) {
                case cash:
                case credit:
                case debit:
                case cheque:
                case wire:
                    mechanism = invoicePaymentParameters.getPaymentMechanism();
                    break;
                default:
                    // ignore
                    break;
            }
        }
        if(mechanism == null) {
            throw new CustomParametrizedException("invalid payment mechanism, must be \"cash\", \"credit\", \"debit\", \"cheque\", or \"wire\"", "paymentMechanism");
        }
        transaction.setPaymentMechanism(mechanism);

        // ref num
        transaction.setPaymentReferenceNumber(invoicePaymentParameters.getPaymentReferenceNumber());
        if(mechanism.equals(TransactionPaymentMechanism.cash)) {
        	transaction.setPaymentReferenceNumber("N/A"); //BUG 72594
        }

        // exported
        transaction.setExported(nvl(invoicePaymentParameters.getExported(), false));

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

    public InvoiceStateType getInitialInvoiceStateType() {
        return initialLedgerState;
    }

    private static Double zeroToNull(final Double v) {
        if(v != null && v.equals(0.0d))
            return null;
        return v;
    }

    private static String resolveDeltaEntryPoint(final String depAd, final List<DeltaFlightVO> item18Dep) {

        // delta flights must always use item18 DEP/ value if exist, else default to depAd
        return item18Dep != null && !item18Dep.isEmpty()
            ? item18Dep.get(0).getIdent()
            : depAd;
    }

    private static String resolveDeltaExitPoint(final String destAd, final List<DeltaFlightVO> item18Dest) {

        // delta flights must always use item18 DEST/ value if exist, else default to destAd
        return item18Dest != null && !item18Dest.isEmpty()
            ? item18Dest.get(item18Dest.size() - 1).getIdent()
            : destAd;
    }

    private static String resolveDeltaMidPoints(final List<DeltaFlightVO> item18Dep, final List<DeltaFlightVO> item18Dest) {

        // delta flight mid points are any remain stops between entry and exit
        StringJoiner midPoints = new StringJoiner(" ");

        // add each item18 DEP/ identify after first
        if (item18Dep != null && item18Dep.size() > 1)
            for (int i = 1; i < item18Dep.size(); i++)
                midPoints.add(item18Dep.get(i).getIdent());

        // add each item18 DEST/ identify up to last
        if (item18Dest != null && item18Dest.size() > 1)
            for (int i = 0; i < item18Dest.size() - 1; i++)
                midPoints.add(item18Dest.get(i).getIdent());

        return midPoints.length() > 0 ? midPoints.toString() : null;
    }

    //TODO:  logic should be handled inside a plugin provider
    private void handleBillingLedgerExtensions(List<KcaaAatisPermitNumber> invoicePermits, BillingLedger billingLedger) {
        if (invoicePermits == null || billingLedger == null) return;

        invoicePermits.forEach(invoicePermit -> invoicePermit.setBillingLedger(billingLedger));
        billingLedger.setKcaaAatisPermitNumbers(new HashSet<>(invoicePermits));
    }

    private void setSubtotalsByFlightCategories (AviationInvoiceData.FlightInfo fi,
                                                 AviationInvoiceData.Global.FlightCategoryInfo flightCategoryInfo) {

        // these doubles can never be null. Suppression due to java limitation of the return value
        @SuppressWarnings("ConstantConditions") double enrouteCharges = nvl(fi.enrouteCharges, 0d);
        @SuppressWarnings("ConstantConditions") double aerodromeCharges = nvl(fi.aerodromeCharges, 0d);
        @SuppressWarnings("ConstantConditions") double parkingCharges = nvl(fi.parkingCharges, 0d);
        @SuppressWarnings("ConstantConditions") double passengerCharges = nvl(fi.passengerCharges, 0d);

        boolean isEnrouteCharges = enrouteCharges > 0d;
        boolean isAerodromeCharges = aerodromeCharges  > 0d;
        boolean isParkingCharges = parkingCharges > 0d;
        boolean isPassengerCharges = passengerCharges > 0d;

        flightCategoryInfo.enrouteCharges += enrouteCharges;
        flightCategoryInfo.aerodromeCharges += aerodromeCharges;
        flightCategoryInfo.parkingCharges += parkingCharges;
        flightCategoryInfo.passengerCharges += passengerCharges;

        if (isEnrouteCharges) flightCategoryInfo.enrouteFlights++;
        if (isAerodromeCharges) flightCategoryInfo.aerodromeFlights ++;
        if (isParkingCharges) flightCategoryInfo.parkingFlights ++;
        if (isPassengerCharges) flightCategoryInfo.passengerFlights ++;

        if (isEnrouteCharges || isAerodromeCharges || isParkingCharges || isPassengerCharges) flightCategoryInfo.totalFlights++;
    }
}
