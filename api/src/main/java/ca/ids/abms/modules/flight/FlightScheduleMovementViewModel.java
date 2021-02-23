package ca.ids.abms.modules.flight;

import ca.ids.abms.modules.flightmovements.FlightMovement;

import java.time.LocalDateTime;

public class FlightScheduleMovementViewModel {

    @SuppressWarnings("unused")
    FlightScheduleMovementViewModel() {
        // ignored, required for jackson deserialization
    }

    FlightScheduleMovementViewModel(final FlightMovement flightMovement) {
        this.flightId = flightMovement.getFlightId();
        this.dateOfFlight = flightMovement.getDateOfFlight();
        this.depAd = flightMovement.getDepAd();
        this.depTime = flightMovement.getDepTime();
    }

    FlightScheduleMovementViewModel(final FlightScheduleViewModel flightSchedule, final LocalDateTime dateOfFlight) {
        this.flightId = flightSchedule.getFlightServiceNumber();
        this.dateOfFlight = dateOfFlight;
        this.depAd = flightSchedule.getDepAd();
        this.depTime = flightSchedule.getDepTime();
    }

    private String flightId;

    private LocalDateTime dateOfFlight;

    private String depAd;

    private String depTime;

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    @Override
    public String toString() {
        return "FlightScheduleMovementViewModel [flightId=" + flightId + ", dateOfFlight=" + dateOfFlight
            + ", depAd=" + depAd + ", depTime=" + depTime + "]";
    }
}
