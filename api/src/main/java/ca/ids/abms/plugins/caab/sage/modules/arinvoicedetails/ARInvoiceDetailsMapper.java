package ca.ids.abms.plugins.caab.sage.modules.arinvoicedetails;

import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.billings.BillingLedger;
import ca.ids.abms.modules.billings.InvoiceLineItem;
import ca.ids.abms.modules.billings.InvoiceOverduePenalty;
import ca.ids.abms.modules.chargesadjustment.ChargesAdjustment;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.enumerate.FlightmovementCategoryScope;
import ca.ids.abms.modules.reports2.common.CachedCurrencyConverter;
import ca.ids.abms.modules.util.models.Calculation;
import ca.ids.abms.plugins.caab.sage.modules.distributioncode.DistributionCode;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDateFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageDecimalFormat;
import ca.ids.abms.plugins.caab.sage.utilities.CaabSageMapperHelper;
import ca.ids.abms.util.mapper.column.SimpleColumnMapper;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    uses = { CaabSageDateFormat.class, CaabSageDecimalFormat.class },
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class ARInvoiceDetailsMapper extends SimpleColumnMapper<ARInvoiceDetails> {

    @Autowired
    CaabSageMapperHelper caabSageMapperHelper;

    // region ChargesAdjustment @Mapping

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "descriptions", source = "chargeDescription")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "itemCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "revenueAccount", ignore = true)
    @Mapping(target = "uom", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    public abstract ARInvoiceDetails toARInvoiceDetails(final ChargesAdjustment chargesAdjustment);
    
    // endregion ChargesAdjustment @Mapping

    // region FlightMovement @Mapping

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "itemCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "revenueAccount", ignore = true)
    @Mapping(target = "uom", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    abstract ARInvoiceDetails toARInvoiceDetails(final FlightMovement flightMovement);

    public ARInvoiceDetails toARInvoiceDetails(
        final FlightMovement flightMovement, final BillingLedger billingLedger, final BillingCenter billingCenter,
        final String chargeCode, final DistributionCode.FlightMovementChargeType chargeType,
        final CachedCurrencyConverter currencyConverter
    ) {
        ARInvoiceDetails arInvoiceDetails = toARInvoiceDetails(flightMovement);

        toARInvoiceDetailsAmount(flightMovement, arInvoiceDetails, billingLedger, chargeType, currencyConverter);
        toARInvoiceDetailsDistributionCode(flightMovement, arInvoiceDetails, chargeCode, billingCenter);
        toARInvoiceDetailsDocumentNumber(billingLedger, arInvoiceDetails);

        return arInvoiceDetails;
    }

    // endregion FlightMovement @Mapping

    // region InvoiceLineItem @Mapping

    @Mapping(target = "amount", source = "amount")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "descriptions", source = "serviceChargeCatalogue.description")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "itemCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "revenueAccount", ignore = true)
    @Mapping(target = "uom", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    public abstract ARInvoiceDetails toARInvoiceDetails(final InvoiceLineItem invoiceLineItem);

    // endregion InvoiceLineItem @Mapping

    // region InvoiceOverduePenalty @Mapping

    @Mapping(target = "amount", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "descriptions", source = "penalizedInvoice.invoiceFileName")
    @Mapping(target = "flag", ignore = true)
    @Mapping(target = "itemCode", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "revenueAccount", ignore = true)
    @Mapping(target = "uom", ignore = true)
    @Mapping(target = "uploadedOn", ignore = true)
    @Mapping(target = "unitPrice", ignore = true)
    public abstract ARInvoiceDetails toARInvoiceDetails(final InvoiceOverduePenalty invoiceOverduePenalty);

    // endregion InvoiceOverduePenalty @Mapping

    // region ChargesAdjustment @AfterMapping

    /**
     * Amount, always positive.
     */
    @AfterMapping
    void toARInvoiceDetailsAmount(final ChargesAdjustment source, @MappingTarget ARInvoiceDetails target) {
        target.setAmount(CaabSageDecimalFormat.format(Math.abs(source.getChargeAmount())));
    }

    /**
     * Distribution code (i.e. 1000-17-1100-00-00)
     */
    @AfterMapping
    void toARInvoiceDetailsDistributionCode(final ChargesAdjustment source, @MappingTarget ARInvoiceDetails target) {
        // set distribution code if it could be found
        DistributionCode distributionCode = caabSageMapperHelper.getDistributionCode(source);
        target.setDistributionCode(distributionCode.getCode());
    }

    /**
     * Document Number, defined by charge adjustment billing ledger.
     */
    @AfterMapping
    void toARInvoiceDetailsDocumentNumber(final ChargesAdjustment source, @MappingTarget ARInvoiceDetails target) {

        // if billing ledger, assumed charge adjustment is a debit note else credit note
        String documentNumber = source.getBillingLedger() != null
            ? caabSageMapperHelper.getDocumentNumber(source.getBillingLedger())
            : caabSageMapperHelper.getDocumentNumber(source.getTransaction());

        // set document number from customer code and record number
        target.setDocumentNumber(documentNumber);
    }

    // endregion ChargesAdjustment @AfterMapping

    // region FlightMovement @AfterMapping

    /**
     * Description is combination of flight movement unique identifier data.
     *
     * For example, a description would look similar to the following:
     * 'FlightId: ABC123 | DepAd: WXYZ | DepTime: 0900 | DateOfFlight: 31012018'
     */
    @AfterMapping
    void toARInvoiceDetailsDescription(final FlightMovement source, @MappingTarget ARInvoiceDetails target) {
        target.setDescriptions(caabSageMapperHelper.getFlightMovementDescription(source));
    }

    // endregion FlightMovement @AfterMapping

    // region InvoiceLineItem @AfterMapping

    /**
     * Distribution code (i.e. 1000-17-1100-00-00)
     */
    @AfterMapping
    void toARInvoiceDetailsDistributionCode(final InvoiceLineItem source, @MappingTarget ARInvoiceDetails target) {
        // set distribution code if it could be found
        DistributionCode distributionCode = caabSageMapperHelper.getDistributionCode(source);
        target.setDistributionCode(distributionCode.getCode());
    }

    /**
     * DocumentNumber, defined by invoice line item billing ledger.
     */
    @AfterMapping
    void toARInvoiceDetailsDocumentNumber(final InvoiceLineItem source, @MappingTarget ARInvoiceDetails target) {
        // define document number from invoice line item billing ledger
        target.setDocumentNumber(caabSageMapperHelper.getDocumentNumber(source.getBillingLedger()));
    }

    // endregion InvoiceLineItem @AfterMapping

    // region InvoiceOverduePenalty @AfterMapping

    /**
     * Amount, defined by default plus punitive overdue invoice amounts.
     */
    @AfterMapping
    void toARInvoiceDetailsAmount(final InvoiceOverduePenalty source, @MappingTarget ARInvoiceDetails target) {
        // set amount based on invoice overdue penalty default and punitive amounts
        Double amount = Double.sum(source.getDefaultPenaltyAmount(), source.getPunitivePenaltyAmount());
        target.setAmount(CaabSageDecimalFormat.format(amount));
    }

    /**
     * Distribution code (i.e. 1000-17-1100-00-00)
     */
    @AfterMapping
    void toARInvoiceDetailsDistributionCode(final InvoiceOverduePenalty source, @MappingTarget ARInvoiceDetails target) {
        // set distribution code if it could be found
        DistributionCode distributionCode = caabSageMapperHelper.getDistributionCode(source);
        target.setDistributionCode(distributionCode.getCode());
    }

    /**
     * DocumentNumber, defined by invoice overdue penalty added to invoice.
     */
    @AfterMapping
    void toARInvoiceDetailsDocumentNumber(final InvoiceOverduePenalty source, @MappingTarget ARInvoiceDetails target) {
        // define document number from invoice overdue penalty added to invoice
        target.setDocumentNumber(caabSageMapperHelper.getDocumentNumber(source.getPenaltyAddedToInvoice()));
    }

    // endregion InvoiceOverduePenalty @AfterMapping

    // region PRIVATE: FlightMovement AfterMapping

    /**
     * Amount is combination of flight movement amounts based on charge type.
     */
    private void toARInvoiceDetailsAmount(
        final FlightMovement source, final ARInvoiceDetails target, final BillingLedger billingLedger,
        final DistributionCode.FlightMovementChargeType chargeType, final CachedCurrencyConverter currencyConverter
    ) {
        // different stored currencies
        Currency anspCurrency = currencyConverter.getAnspCurrency();
        Currency enrouteCurrency = source.getFlightmovementCategory().getEnrouteResultCurrency();
        Currency paxDomCurrency = currencyConverter.getPaxDomCurrency();
        Currency paxIntlCurrency = currencyConverter.getPaxIntlCurrency();

        Currency aerodromeCurrency;
        Currency approachCurrency;
        Currency lateArrDepCurrency;
        if (FlightmovementCategoryScope.DOMESTIC == source.getFlightCategoryScope()) {
            aerodromeCurrency = currencyConverter.getAerodromeDomCurrency();
            approachCurrency = currencyConverter.getApproachDomCurrency();
            lateArrDepCurrency = currencyConverter.getLateArrDepDomCurrency();
        } else if (FlightmovementCategoryScope.REGIONAL == source.getFlightCategoryScope()) {
            aerodromeCurrency = currencyConverter.getAerodromeRegCurrency();
            approachCurrency = currencyConverter.getApproachRegCurrency();
            lateArrDepCurrency = currencyConverter.getLateArrDepRegCurrency();
        } else {
            aerodromeCurrency = currencyConverter.getAerodromeIntlCurrency();
            approachCurrency = currencyConverter.getApproachIntlCurrency();
            lateArrDepCurrency = currencyConverter.getLateArrDepIntlCurrency();
        }

        // invoice currency used
        Currency invoiceCurrency = null;
        if (billingLedger != null
            && billingLedger.getInvoiceCurrency() != null)
            invoiceCurrency = billingLedger.getInvoiceCurrency();

        // set amount equal to the appropriate charge type
        double amount = 0d;
        switch(chargeType) {
            case ENROUTE_CHARGES:
                amount += convertAmount(source.getEnrouteCharges(), enrouteCurrency, invoiceCurrency, currencyConverter);
                break;
            case DOMESTIC_PASSENGER_CHARGES:
                amount += convertAmount(source.getDomesticPassengerCharges(), paxDomCurrency, invoiceCurrency, currencyConverter);
                break;
            case INTERNATIONAL_PASSENGER_CHARGES:
                amount += convertAmount(source.getInternationalPassengerCharges(), paxIntlCurrency, invoiceCurrency, currencyConverter);
                break;
            case LANDING_CHARGES:
                amount += convertAmount(source.getAerodromeCharges(), aerodromeCurrency, invoiceCurrency, currencyConverter);
                amount += convertAmount(source.getApproachCharges(), approachCurrency, invoiceCurrency, currencyConverter);
                amount += convertAmount(source.getLateArrivalCharges(), lateArrDepCurrency, invoiceCurrency, currencyConverter);
                amount += convertAmount(source.getLateDepartureCharges(), lateArrDepCurrency, invoiceCurrency, currencyConverter);
                amount += convertAmount(source.getTaspCharge(), anspCurrency, invoiceCurrency, currencyConverter);
                break;
            case PARKING_CHARGES:
                amount += convertAmount(source.getParkingCharges(), anspCurrency, invoiceCurrency, currencyConverter);
                break;
            default:
                // ignored
        }

        // format double value as string
        target.setAmount(CaabSageDecimalFormat.format(amount));
    }

    /**
     * Set distribution code based on charge code and the flight movement's billing centre.
     *
     * @param flightMovement used for line
     * @param arInvoiceDetails is the record to be saved in SAGE
     * @param chargeCode to be used to find the appropriate distribution code
     * @param billingCenterDefault to be used to find the appropriate distributing code if
     *                             flight movement billing center is undefined
     */
    private void toARInvoiceDetailsDistributionCode(
        final FlightMovement flightMovement, final ARInvoiceDetails arInvoiceDetails, final String chargeCode,
        final BillingCenter billingCenterDefault
    ) {
        // set distribution code if it could be found
        DistributionCode distributionCode = caabSageMapperHelper.getDistributionCode(
            flightMovement, chargeCode, billingCenterDefault);
        arInvoiceDetails.setDistributionCode(distributionCode.getCode());
    }

    /**
     * Set DocumentNumber from concatenation of invoice number and customer code based on billing ledger.
     */
    private void toARInvoiceDetailsDocumentNumber(final BillingLedger source, final ARInvoiceDetails target) {
        // define document number from invoice line item billing ledger
        target.setDocumentNumber(caabSageMapperHelper.getDocumentNumber(source));
    }

    // endregion PRIVATE: FlightMovement AfterMapping

    // region PRIVATE: Currency Conversion Handling

    /**
     * Wrapper to convert value from one currency to another.
     */
    private double convertAmount(
        final Double value, final Currency fromCurrency, final Currency toCurrency,
        final CachedCurrencyConverter currencyConverter
    ) {
        if (value == null || value == 0d)
            return 0d;
        else if (fromCurrency != null && toCurrency != null && !fromCurrency.equals(toCurrency))
            return currencyConverter.convertCurrency(value, fromCurrency, toCurrency);
        else if (toCurrency != null && toCurrency.getDecimalPlaces() != null)
            return Calculation.truncate(value, toCurrency.getDecimalPlaces());
        else
            return value;
    }

    // endregion PRIVATE: Currency Conversion Handling
}
