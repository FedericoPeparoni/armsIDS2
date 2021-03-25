package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.modules.reports2.invoices.AccountCredit;
import ca.ids.abms.modules.reports2.invoices.AdditionalCharge;
import ca.ids.abms.modules.reports2.invoices.OverduePenaltyInvoice;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import ca.ids.abms.modules.reports2.invoices.BankAccount;
import java.util.List;

@XmlRootElement (name = "generalAviationInvoice")
public class AviationInvoiceData {

    /** Global/singleton properties (totals, etc) */
    public static final class Global {
        public @XmlElement(nillable = true) String realInvoiceNumber;
        public @XmlElement(nillable = true) String invoiceNumber;
        public @XmlElement(nillable = true) String invoiceName;
        public @XmlElement(nillable = true) String invoiceIssueLocation;
        public @XmlElement(nillable = true) String invoiceDateStr;
        public @XmlElement(nillable = true) String invoiceDateOfIssueStr;
        public @XmlElement(nillable = true) String invoiceDueDateStr;
        public @XmlElement(nillable = true) String invoiceBillingPeriod;
        public @XmlElement(nillable = true) Integer accountId;
        public @XmlElement(nillable = true) String accountName;
        public @XmlElement(nillable = true) String accountAlias;
        public @XmlElement(nillable = true) String accountExternalSystemIdentifier;
        public @XmlElement(nillable = true) String fromName;
        public @XmlElement(nillable = true) String fromPosition;
        public @XmlElement(nillable = true) String taspFeeLabel;
    	public @XmlElement(nillable = true) String flightMovementCategoryScope;


        // Billing info
        public @XmlElement(nillable = true) String billingName;
        public @XmlElement(nillable = true) String billingAddress;
        public @XmlElement(nillable = true) String billingContactTel;
        public @XmlElement(nillable = true) String billingEmail;

        // Subtotals
        public @XmlElement(nillable = true) Double enrouteCharges;
        public @XmlElement(nillable = true) String enrouteChargesStr;
        public @XmlElement(nillable = true) String enrouteChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double taspCharges;
        public @XmlElement(nillable = true) String taspChargesStr;
        public @XmlElement(nillable = true) String taspChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Boolean taspChargesEnabled;
        public @XmlElement(nillable = true) Double aerodromeCharges;
        public @XmlElement(nillable = true) String aerodromeChargesStr;
        public @XmlElement(nillable = true) String aerodromeChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double approachCharges;
        public @XmlElement(nillable = true) String approachChargesStr;
        public @XmlElement(nillable = true) String approachChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double landingCharges;
        public @XmlElement(nillable = true) String landingChargesStr;
        public @XmlElement(nillable = true) String landingChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double parkingCharges;
        public @XmlElement(nillable = true) String parkingChargesStr;
        public @XmlElement(nillable = true) String parkingChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double passengerCharges;
        public @XmlElement(nillable = true) String passengerChargesStr;
        public @XmlElement(nillable = true) String passengerChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Boolean passengerChargesEnabled;
        public @XmlElement(nillable = true) Double lateDepartureArrivalCharges;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesStr;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double enrouteChargesAnsp;
        public @XmlElement(nillable = true) String enrouteChargesAnspStr;
        public @XmlElement(nillable = true) Double taspChargesAnsp;
        public @XmlElement(nillable = true) String taspChargesAnspStr;
        public @XmlElement(nillable = true) Double aerodromeChargesAnsp;
        public @XmlElement(nillable = true) String aerodromeChargesAnspStr;
        public @XmlElement(nillable = true) Double approachChargesAnsp;
        public @XmlElement(nillable = true) String approachChargesAnspStr;
        public @XmlElement(nillable = true) Double landingChargesAnsp;
        public @XmlElement(nillable = true) String landingChargesAnspStr;
        public @XmlElement(nillable = true) Double parkingChargesAnsp;
        public @XmlElement(nillable = true) String parkingChargesAnspStr;
        public @XmlElement(nillable = true) Double passengerChargesAnsp;
        public @XmlElement(nillable = true) String passengerChargesAnspStr;
        public @XmlElement(nillable = true) Double lateDepartureArrivalChargesAnsp;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesAnspStr;
        public @XmlElement(nillable = true) Boolean extendedHoursSurchargeEnabled;
        public @XmlElement(nillable = true) Double extendedHoursSurcharge;
        public @XmlElement(nillable = true) String extendedHoursSurchargeStr;
        public @XmlElement(nillable = true) String extendedHoursSurchargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double extendedHoursSurchargesAnsp;
        public @XmlElement(nillable = true) String extendedHoursSurchargesAnspStr;
        
        // Subtotal: sum of all subtotals
        public @XmlElement(nillable = true) Double subtotalAmount;
        public @XmlElement(nillable = true) String subtotalAmountStr;
        public @XmlElement(nillable = true) String subtotalAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double subtotalAmountAnsp;
        public @XmlElement(nillable = true) String subtotalAmountAnspStr;

        // Total: sum of all charges
        public @XmlElement(nillable = true) Double totalAmount;
        public @XmlElement(nillable = true) String totalAmountStr;
        public @XmlElement(nillable = true) String totalAmountStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double totalAmountAnsp;
        public @XmlElement(nillable = true) String totalAmountAnspStr;
        public @XmlElement(nillable = true) Double totalAmountUsd;
        public @XmlElement(nillable = true) String totalAmountUsdStr;

        public @XmlElement(nillable = true) String invoiceCurrencyInWords;
        public @XmlElement(nillable = true) String exchageRate;
        public @XmlElement(nillable = true) String exchageRateUsd;
        public @XmlElement(nillable = true) String equivalentAmount;
        public @XmlElement(nillable = true) String totalAccountCredit;

        // Amount due: total - credit
        public @XmlElement(nillable = true) Double amountDue;
        public @XmlElement(nillable = true) String amountDueStr;
        public @XmlElement(nillable = true) String amountDueStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String invoiceCurrencyCode;
        public @XmlElement(nillable = true) String invoiceCurrencyAnspCode;
        public @XmlElement(nillable = true) String invoiceCurrencyUsdCode;
        public @XmlElement(nillable = true) String invoiceCurrencyAltCode;
        public @XmlElement(nillable = true) Double amountDueAnsp;
        public @XmlElement(nillable = true) String amountDueAnspStr;
        public @XmlElement(nillable = true) Double amountDueUsd;
        public @XmlElement(nillable = true) String amountDueUsdStr;
        public @XmlElement(nillable = true) String totalOutstandingAmount;
        public @XmlElement(nillable = true) String totalPenalty;
        public @XmlElement(nillable = true) Double amountDueAlt;
        public @XmlElement(nillable = true) String amountDueAltStr;
        public @XmlElement(nillable = true) String amountDueAltStrWithCurrencySymbol;

        // Total Amount In Words: total amount value spelled out in words
        public @XmlElement(nillable = true) String totalAmountStrInWords;
        public @XmlElement(nillable = true) String totalAmountStrInWordsSpanish;
        public @XmlElement(nillable = true) String totalAmountStrInWordsWithCurrencySymbol;
        public @XmlElement(nillable = true) String totalAmountStrInWordsWithCurrencySymbolSpanish;

        // Amount Due In Words: amount due value spelled out in words
        public @XmlElement(nillable = true) String amountDueStrInWords;
        public @XmlElement(nillable = true) String amountDueStrInWordsSpanish;
        public @XmlElement(nillable = true) String amountDueStrInWordsWithCurrencySymbol;
        public @XmlElement(nillable = true) String amountDueStrInWordsWithCurrencySymbolSpanish;

        /**
         * No longer use this property as it is misleading. This amount is actually the "amount due" and not the
         * "total amount". Leaving as is until it is safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWords} for amount due and {@link #totalAmountStrInWords}
         * for total amount.
         */
        @Deprecated
        public @XmlElement(nillable = true) String totalAmountInWords;

        /**
         * No longer use this property as it is misleading. This amount is actually the "amount due" and not the
         * "total amount". Leaving as is until it is safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWordsWithCurrencySymbol} for amount due and
         * {@link #totalAmountStrInWordsWithCurrencySymbol} for total amount.
         */
        @Deprecated
        public @XmlElement(nillable = true) String totalAmountInWordsWithCurrencySymbol;

        /**
         * No longer use this property as its name is inconsistent with InWords naming. Leaving as is until it is
         * safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWords} for amount due.
         */
        @Deprecated
        public @XmlElement(nillable = true) String amountDueInWords;

        /**
         * No longer use this property as its name is inconsistent with InWords naming. Leaving as is until it is
         * safely removed from all existing invoices.
         *
         * @deprecated Use {@link #amountDueStrInWordsWithCurrencySymbol} for amount due.
         */
        @Deprecated
        public @XmlElement(nillable = true) String amountDueInWordsWithCurrencySymbol;

        public @XmlElement(nillable = true) String kraClerkName;
        public @XmlElement(nillable = true) String kraReceiptNumber;

        public @XmlElement(nillable = true) Integer totalFlightsWithAerodromeCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithApproachCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithLandingCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithEnrouteCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithPassengerCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithTaspCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithLateDepartureArrivalCharges;
        public @XmlElement(nillable = true) Integer totalFlightsWithExtendedHoursCharges;



        // Domestic Flight Category info
        public @XmlElement(nillable = true) String domesticEnrouteChargesStr;
        public @XmlElement(nillable = true) String domesticAerodromeChargesStr;
        public @XmlElement(nillable = true) String domesticParkingChargesStr;
        public @XmlElement(nillable = true) String domesticPassengerChargesStr;
        public @XmlElement(nillable = true) String domesticTotalChargesStr;

        public @XmlElement(nillable = true) Integer domesticEnrouteFlights;
        public @XmlElement(nillable = true) Integer domesticAerodromeFlights;
        public @XmlElement(nillable = true) Integer domesticParkingFlights;
        public @XmlElement(nillable = true) Integer domesticPassengerFlights;
        public @XmlElement(nillable = true) Integer domesticTotalFlights;

        // International Flight Category info
        public @XmlElement(nillable = true) String internationalEnrouteChargesStr;
        public @XmlElement(nillable = true) String internationalAerodromeChargesStr;
        public @XmlElement(nillable = true) String internationalParkingChargesStr;
        public @XmlElement(nillable = true) String internationalPassengerChargesStr;

        public @XmlElement(nillable = true) Integer internationalEnrouteFlights;
        public @XmlElement(nillable = true) Integer internationalAerodromeFlights;
        public @XmlElement(nillable = true) Integer internationalParkingFlights;
        public @XmlElement(nillable = true) Integer internationalPassengerFlights;

        public @XmlElement(nillable = true) String internationalTotalChargesStr;
        public @XmlElement(nillable = true) Integer internationalTotalFlights;

        // Overflight Flight Category info
        public @XmlElement(nillable = true) String overflightEnrouteChargesStr;
        public @XmlElement(nillable = true) String overflightAerodromeChargesStr;
        public @XmlElement(nillable = true) String overflightParkingChargesStr;
        public @XmlElement(nillable = true) String overflightPassengerChargesStr;

        public @XmlElement(nillable = true) Integer overflightEnrouteFlights;
        public @XmlElement(nillable = true) Integer overflightAerodromeFlights;
        public @XmlElement(nillable = true) Integer overflightParkingFlights;
        public @XmlElement(nillable = true) Integer overflightPassengerFlights;

        public @XmlElement(nillable = true) String overflightTotalChargesStr;
        public @XmlElement(nillable = true) Integer overflightTotalFlights;
        
        // Unified Tax category
        public @XmlElement(nillable = true) Double unifiedTaxTotalCharges;
        public @XmlElement(nillable = true) String unifiedTaxChargesStr;
        public @XmlElement(nillable = true) Integer unifiedTaxAircraftTotal;
        

        // FIXME: these should be a booleans, also in all BIRT templates & XML files
        @SuppressWarnings("squid:S1134")
        public @XmlElement(nillable = true) String includePassengerCharges;
        public @XmlElement(nillable = true) String includeTaspCharges;

        public @XmlElement(nillable = true) String displayInverse;

        static final class FlightCategoryInfo {
            double enrouteCharges = 0d;
            double aerodromeCharges = 0d;
            double parkingCharges = 0d;
            double passengerCharges = 0d;

            int enrouteFlights = 0;
            int aerodromeFlights = 0;
            int parkingFlights = 0;
            int passengerFlights = 0;
            int totalFlights = 0;
        }
    }
    public Global global;

    /** Information about each individual flight in this invoice */
    public static final class FlightInfo {
        public @XmlElement boolean enrouteChargesIncluded = false;
        public @XmlElement boolean passengerChargesIncluded = false;
        public @XmlElement boolean otherChargesIncluded = false;
        public @XmlElement(nillable = true) Integer accountId;
        public @XmlElement(nillable = true) String accountName;
        public @XmlElement(nillable = true) String accountIcaoCode;
        public @XmlElement(nillable = true) Integer flightMovementId;
        public @XmlElement(nillable = true) String flightDateStr;
        public @XmlElement(nillable = true) String billingDateStr;
        public @XmlElement(nillable = true) String billingEntryDateStr;
        public @XmlElement(nillable = true) String billingExitDateStr;
        public @XmlElement(nillable = true) String regNum;
        public @XmlElement(nillable = true) String flightId;
        public @XmlElement(nillable = true) String flightType;
        public @XmlElement(nillable = true) String aircraftType;
        public @XmlElement(nillable = true) Double mtow;             // short tons
        public @XmlElement(nillable = true) String mtowStr;
        public @XmlElement(nillable = true) Double mtowKg;           // kilograms
        public @XmlElement(nillable = true) String mtowKgStr;
        public @XmlElement(nillable = true) String entryTime;
        public @XmlElement(nillable = true) String exitTime;
        public @XmlElement(nillable = true) String entryPoint;
        public @XmlElement(nillable = true) String exitPoint;
        public @XmlElement(nillable = true) String midPoints;
        public @XmlElement(nillable = true) Double crossDist;
        public @XmlElement(nillable = true) String crossDistStr;
        public @XmlElement(nillable = true) Boolean taspTypeScheduled;
        public @XmlElement(nillable = true) Boolean taspTypeNonScheduled;
        public @XmlElement(nillable = true) Boolean taspTypeMilitary;
        public @XmlElement(nillable = true) Boolean taspTypePrivate;
        public @XmlElement(nillable = true) String departureLocation;
        public @XmlElement(nillable = true) String departureTimeStr;
        public @XmlElement(nillable = true) String actualDepartureTimeStr;
        public @XmlElement(nillable = true) String arrivalLocation;
        public @XmlElement(nillable = true) String arrivalTimeStr;
        public @XmlElement(nillable = true) Integer departurePassengerCount;
        public @XmlElement(nillable = true) Integer arrivalPassengerCount;
        public @XmlElement(nillable = true) Integer transitPassengerCount;
        public @XmlElement(nillable = true) Integer infantPassengerCount;
        public @XmlElement(nillable = true) Double enrouteCharges;
        public @XmlElement(nillable = true) String enrouteChargesStr;
        public @XmlElement(nillable = true) String enrouteChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double taspCharges;
        public @XmlElement(nillable = true) String taspChargesStr;
        public @XmlElement(nillable = true) String taspChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double aerodromeCharges;
        public @XmlElement(nillable = true) String aerodromeChargesStr;
        public @XmlElement(nillable = true) String aerodromeChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double approachCharges;
        public @XmlElement(nillable = true) String approachChargesStr;
        public @XmlElement(nillable = true) String approachChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double landingCharges;
        public @XmlElement(nillable = true) String landingChargesStr;
        public @XmlElement(nillable = true) String landingChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double parkingCharges;
        public @XmlElement(nillable = true) String parkingChargesStr;
        public @XmlElement(nillable = true) String parkingChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double passengerCharges;
        public @XmlElement(nillable = true) String passengerChargesStr;
        public @XmlElement(nillable = true) String passengerChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double lateDepartureArrivalCharges;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesStr;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double totalCharges;
        public @XmlElement(nillable = true) String totalChargesStr;
        public @XmlElement(nillable = true) String totalChargesStrWithCurrencySymbol;
        public @XmlElement(nillable = true) String distanceUnitOfMeasure;
        public @XmlElement(nillable = true) String mtowUnitOfMeasure;
        public @XmlElement(nillable = true) Double taspChargesAnsp;
        public @XmlElement(nillable = true) String taspChargesAnspStr;
        public @XmlElement(nillable = true) Double enrouteChargesAnsp;
        public @XmlElement(nillable = true) String enrouteChargesAnspStr;
        public @XmlElement(nillable = true) Double aerodromeChargesAnsp;
        public @XmlElement(nillable = true) String aerodromeChargesAnspStr;
        public @XmlElement(nillable = true) Double approachChargesAnsp;
        public @XmlElement(nillable = true) String approachChargesAnspStr;
        public @XmlElement(nillable = true) Double landingChargesAnsp;
        public @XmlElement(nillable = true) String landingChargesAnspStr;
        public @XmlElement(nillable = true) Double parkingChargesAnsp;
        public @XmlElement(nillable = true) String parkingChargesAnspStr;
        public @XmlElement(nillable = true) Double lateDepartureArrivalChargesAnsp;
        public @XmlElement(nillable = true) String lateDepartureArrivalChargesAnspStr;
        public @XmlElement(nillable = true) Double passengerChargesAnsp;
        public @XmlElement(nillable = true) String passengerChargesAnspStr;
        public @XmlElement(nillable = true) Double totalChargesAnsp;
        public @XmlElement(nillable = true) String totalChargesAnspStr;
        public @XmlElement(nillable = true) String flightCategory;
        public @XmlElement(nillable = true) String flightMovementCategoryScope;
        public @XmlElement(nillable = true) String aatisNumber;
        public @XmlElement(nillable = true) Double crossingDistanceToMinimum;
        public @XmlElement(nillable = true) String domesticPassengerCharges;
        public @XmlElement(nillable = true) String internationalPassengerCharges;
        public @XmlElement(nillable = true) Double extendedHoursSurcharge;
        public @XmlElement(nillable = true) String extendedHoursSurchargeStr;
        public @XmlElement(nillable = true) String extendedHoursSurchargeStrWithCurrencySymbol;
        public @XmlElement(nillable = true) Double extendedHoursSurchargeAnsp;
        public @XmlElement(nillable = true) String extendedHoursSurchargeAnspStr;

        public boolean invoicePaxAllowed;
    }
    @XmlElementWrapper
    @XmlElement (name="flightInfo", nillable = true)
    public List <FlightInfo> flightInfoList;

    /** Additional charges */
    @XmlElementWrapper
    @XmlElement (name="additionalCharge", nillable = true)
    public List <AdditionalCharge> additionalCharges;

    /** Account credits */
    @XmlElementWrapper
    @XmlElement (name="accountCredit", nillable = true)
    public List <AccountCredit> accountCredits;

    /** Overdue invoice */
    @XmlElementWrapper
    @XmlElement (name="overduePenaltyInvoice", nillable = true)
    public List <OverduePenaltyInvoice> overduePenaltyInvoices;

    /** Bank accounts */
    @XmlElementWrapper
    @XmlElement (name="bankAccount", nillable = true)
    public List <BankAccount> bankAccountList;

    /** Information about each individual "unified tax" aircraft registration in this invoice */
    public static final class AircraftInfo {
        public @XmlElement String customerName;
        public @XmlElement String company;
        public @XmlElement String invoicePeriod;
        public @XmlElement String invoiceExpiration;

        public @XmlElement Double unifiedTaxCharges = 0d;
        public @XmlElement String manufacturer;
        public @XmlElement String aircraftType
        public @XmlElement Double mtow = 0d;
        public @XmlElement String manufactureYearStr;
    }

    @XmlElementWrapper
    @XmlElement (name="aircraftInfo", nillable = true)
    public List <AircraftInfo> aircraftInfoList;

    public boolean invoiceGenerationAllowed;
}
