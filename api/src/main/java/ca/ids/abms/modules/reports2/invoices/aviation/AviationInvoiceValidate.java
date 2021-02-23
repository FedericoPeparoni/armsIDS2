package ca.ids.abms.modules.reports2.invoices.aviation;

import ca.ids.abms.modules.flightmovements.FlightMovementViewModel;

import java.util.Arrays;


/**
 * For validating a flight against
 * temporary flight movements
 */
public class AviationInvoiceValidate {
    private FlightMovementViewModel flight;

    private FlightMovementViewModel[] temporaryFlights;

    public FlightMovementViewModel getFlight() {
        return flight;
    }

    public void setFlight(FlightMovementViewModel flight) {
        this.flight = flight;
    }

    public FlightMovementViewModel[] getTemporaryFlights() {
        return temporaryFlights;
    }

    public void setTemporaryFlights(FlightMovementViewModel[] temporaryFlights) {
        this.temporaryFlights = temporaryFlights;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AviationInvoiceValidate that = (AviationInvoiceValidate) o;

        if (flight != null ? !flight.equals(that.flight) : that.flight != null) return false;
        return Arrays.equals(temporaryFlights, that.temporaryFlights);
    }

    @Override
    public int hashCode() {
        int result = flight != null ? flight.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(temporaryFlights);
        return result;
    }
}
