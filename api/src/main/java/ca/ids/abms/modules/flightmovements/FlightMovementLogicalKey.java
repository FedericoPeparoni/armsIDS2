package ca.ids.abms.modules.flightmovements;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Flight logical key: flightId, depAd, dateOfFlight, depTime
 *
 */
public class FlightMovementLogicalKey {
    private final String flightId;
    private final String depAd;
    private final LocalDate dateOfFlight;
    private final String depTime;
    
    public FlightMovementLogicalKey (final String flightId, String depAd, LocalDateTime dateOfFlight, String depTime) {
        this (flightId, depAd, dateOfFlight == null ? null : dateOfFlight.toLocalDate(), depTime);
    }
    
    public FlightMovementLogicalKey (final String flightId, String depAd, LocalDate dateOfFlight, String depTime) {
        this.flightId = flightId;
        this.depAd = depAd;
        this.dateOfFlight = dateOfFlight;
        this.depTime = depTime;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash (getFlightId(), getDepAd(), getDateOfFlight(), getDepTime());
    }
    
    @Override
    public boolean equals (final Object object) {
        if (object instanceof FlightMovementLogicalKey) {
            final FlightMovementLogicalKey other = (FlightMovementLogicalKey)object;
            return
                    Objects.equals (getFlightId(), other.getFlightId()) &&
                    Objects.equals (getDepAd(), other.getDepAd()) &&
                    Objects.equals (getDateOfFlight(), other.getDateOfFlight()) &&
                    Objects.equals (getDepTime(), other.getDepTime());
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format (
                "FlightMovementLogicalKey={flightId=%s,depAd=%s,dateOfFlight=%s,depTime=%s}",
                getFlightId(), getDepAd(), getDateOfFlight(), getDepTime()
        );
    }

    public String getFlightId() {
        return flightId;
    }

    public String getDepAd() {
        return depAd;
    }

    public LocalDate getDateOfFlight() {
        return dateOfFlight;
    }

    public String getDepTime() {
        return depTime;
    }

    
}
