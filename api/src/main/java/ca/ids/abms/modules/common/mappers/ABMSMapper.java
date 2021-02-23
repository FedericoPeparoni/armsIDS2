package ca.ids.abms.modules.common.mappers;


import ca.ids.abms.modules.common.enumerators.*;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;

/**
 * This is a base mapper helpful to centralize the converters of all enumerators in common to several models in ABMS
 */
public abstract class ABMSMapper extends DateTimeMapper {

    public WakeTurbulence mapWakeTurbulence(final String wakeTurbulence) {
        return WakeTurbulence.forValue(wakeTurbulence);
    }

    public String mapWakeTurbolence(final WakeTurbulence wakeTurbulence) {
        return wakeTurbulence != null ? wakeTurbulence.toValue() : null;
    }

    public FlightCategory mapFlightCategory(final String flightCategory) {
        return FlightCategory.mapFromValue(flightCategory);
    }

    public String mapFlightCategory(final FlightCategory flightCategory) {
        if (flightCategory != null) {
            return flightCategory.toValue();
        }
        return FlightCategory.getDefaultToValue();
    }

    public FlightType mapFlightType(final String flightType) {
        return FlightType.forValue(flightType);
    }

    public String mapFlightType(final FlightType flightType) {
        return flightType != null ? flightType.toValue() : null;
    }

    public FlightItemType mapFlightItemType(final String flightItemType) {
        return FlightItemType.forValue(flightItemType);
    }

    public String mapFlightItemType(final FlightItemType flightItemType) {
        return flightItemType != null ? flightItemType.toValue() : null;
    }

    public Item8Values mapItem8Values(final String item8Values) {
        return Item8Values.forValue(item8Values);
    }

    public String mapItem8Values(final Item8Values item8Values) {
        return item8Values != null ? item8Values.toValue() : null;
    }

    public Item18STSValues mapItem18STSValues(final String item18STSValues) {
        return Item18STSValues.forValue(item18STSValues);
    }

    public String mapItem18STSValues(final Item18STSValues item18STSValues) {
        return item18STSValues != null ? item18STSValues.toValue() : null;
    }

    public BasisForCharge mapBasisForCharge(final String basisForCharge) {
        return BasisForCharge.forValue(basisForCharge);
    }

    public String mapBasisForCharge(final BasisForCharge basisForCharge) {
        return basisForCharge != null ? basisForCharge.toValue() : null;
    }

    public ExternalDatabaseForCharge mapExternalDatabaseForCharge(final String externalDatabaseForCharge) {
        return ExternalDatabaseForCharge.forValue(externalDatabaseForCharge);
    }

    public String mapExternalDatabaseForCharge(final ExternalDatabaseForCharge externalDatabaseForCharge) {
        return externalDatabaseForCharge != null ? externalDatabaseForCharge.toValue() : null;
    }

    public InvoiceCategory mapInvoiceCategory(final String invoiceCategory) {
        return InvoiceCategory.forValue(invoiceCategory);
    }

    public String mapInvoiceCategory(final InvoiceCategory invoiceCategory) {
        return invoiceCategory != null ? invoiceCategory.toValue() : null;
    }

    public InvoiceType mapInvoiceType(final String invoiceType) {
        return InvoiceType.forValue(invoiceType);
    }

    public String mapInvoiceType(final InvoiceType invoiceType) {
        return invoiceType != null ? invoiceType.toValue() : null;
    }

    public String mapFlightMovementStatus(final FlightMovementStatus flightMovementStatus){
        return flightMovementStatus != null ? flightMovementStatus.toValue() : null;
    }
}
