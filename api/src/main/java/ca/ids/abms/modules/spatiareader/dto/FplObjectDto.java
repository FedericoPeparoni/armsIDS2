package ca.ids.abms.modules.spatiareader.dto;

import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.flightmovements.FlightMovementLogicalKey;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FplObjectDto implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

	//@NotNull
	private Long catalogueFplObjectId;
	
	private LocalDateTime catalogueDate;

    @NotNull
    @Size(max = 1)
    private String cataloguePrcStatus;

    @NotNull
    private LocalDate dayOfFlight;

    @NotNull
    @Time4Digits
    private String departureTime;

    @NotNull
    @Size(max = 7)
    private String flightId;

    @NotNull
    @Size(max = 4)
    private String departureAd;

    @NotNull
    private String destinationAd;

    @Size(max = 1)
	private String flightRules;

    @Size(max = 2)
	private String flightType;

    @Size(max = 4)
	private String aircraftType;

    @Size(max = 1)
	private String wakeTurb;

    @Size(max = 4)
    private String msgDepartureTime;

    @Size(max = 5)
    private String speed;

    @Size(max = 5)
	private String flightLevel;

    @Size(max = 1800)
    private String route;

    @Size(max = 4)
	private String totalEet;

    @Size(max = 4)
	private String arrivalAd;

    @Size(max = 1800)
	private String arrivalTime;

    @Size(max = 1800)
	private String otherInfo;

	@JsonIgnore
	private transient LocalDateTime childMessageMaxCatalogueDate;

    @Size(max = 4000)
    private String rawFpl;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "child_message_max_catalogue_date", nullable = false, length = 22)
	public LocalDateTime getChildMessageMaxCatalogueDate() {
		return childMessageMaxCatalogueDate;
	}

	public void setChildMessageMaxCatalogueDate(LocalDateTime childMessageMaxCatalogueDate) {
		this.childMessageMaxCatalogueDate = childMessageMaxCatalogueDate;
	}

	public Long getCatalogueFplObjectId() {
		return this.catalogueFplObjectId;
	}

	public void setCatalogueFplObjectId (Long catalogueFplObjectId) {
		this.catalogueFplObjectId = catalogueFplObjectId;
	}

	public String getCataloguePrcStatus() {
		return this.cataloguePrcStatus;
	}

	public void setCataloguePrcStatus(String cataloguePrcStatus) {
		this.cataloguePrcStatus = cataloguePrcStatus;
	}

	public String getFlightId() {
		return this.flightId;
	}

	public void setFlightId(String flightId) {
		this.flightId = flightId;
	}

	public String getFlightRules() {
		return this.flightRules;
	}

	public void setFlightRules(String flightRules) {
		this.flightRules = flightRules;
	}

	public String getFlightType() {
		return this.flightType;
	}

	public void setFlightType(String flightType) {
		this.flightType = flightType;
	}

	public String getAircraftType() {
		return this.aircraftType;
	}

	public void setAircraftType(String aircraftType) {
		this.aircraftType = aircraftType;
	}

	public String getWakeTurb() {
		return this.wakeTurb;
	}

	public void setWakeTurb(String wakeTurb) {
		this.wakeTurb = wakeTurb;
	}

	public String getDepartureAd() {
		return this.departureAd;
	}

	public void setDepartureAd(String departureAd) {
		this.departureAd = departureAd;
	}

	public String getDepartureTime() {
		return this.departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getMsgDepartureTime() {
		return this.msgDepartureTime;
	}

	public void setMsgDepartureTime(String msgDepartureTime) {
		this.msgDepartureTime = msgDepartureTime;
	}

	public String getSpeed() {
		return this.speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getFlightLevel() {
		return this.flightLevel;
	}

	public void setFlightLevel(String flightLevel) {
		this.flightLevel = flightLevel;
	}

	public String getRoute() {
		return this.route;
	}

	public void setRoute(String route) {
		this.route = route;
	}

	public String getDestinationAd() {
		return this.destinationAd;
	}

	public void setDestinationAd(String destinationAd) {
		this.destinationAd = destinationAd;
	}

	public String getTotalEet() {
		return this.totalEet;
	}

	public void setTotalEet(String totalEet) {
		this.totalEet = totalEet;
	}

	public String getArrivalAd() {
		return this.arrivalAd;
	}

	public void setArrivalAd(String arrivalAd) {
		this.arrivalAd = arrivalAd;
	}

	public String getArrivalTime() {
		return this.arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public String getOtherInfo() {
		return this.otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	public LocalDate getDayOfFlight() {
		return this.dayOfFlight;
	}

	public void setDayOfFlight(LocalDate dayOfFlight) {
		this.dayOfFlight = dayOfFlight;
	}

    public String getRawFpl() {
        return rawFpl;
    }

    public void setRawFpl(String rawFpl) {
        this.rawFpl = rawFpl;
    }

    public LocalDateTime getCatalogueDate() {
        return catalogueDate;
    }

    public void setCatalogueDate(LocalDateTime catalogueDate) {
        this.catalogueDate = catalogueDate;
    }

    @Override
    public int hashCode() {
        int result = catalogueFplObjectId == null ? 17 : (int) (catalogueFplObjectId ^ (catalogueFplObjectId >>> 32));
        result = 31 * result + (flightId != null ? flightId.hashCode() : 0);
        result = 31 * result + (departureAd != null ? departureAd.hashCode() : 0);
        result = 31 * result + (departureTime != null ? departureTime.hashCode() : 0);
        result = 31 * result + (dayOfFlight != null ? dayOfFlight.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FplObjectDto)) {
            return false;
        }
        return catalogueFplObjectId == ((FplObjectDto) o).getCatalogueFplObjectId();
    }

    @Override
    public String toString() {
        return "FplObjectDto{" +
            "catalogueFplObjectId=" + catalogueFplObjectId +
            ", flightId='" + flightId + '\'' +
            ", dayOfFlight=" + dayOfFlight +
            ", departureAd='" + departureAd + '\'' +
            ", departureTime='" + departureTime + '\'' +
            '}';
    }
    
    @JsonIgnore
    public FlightMovementLogicalKey getLogicalKey() {
        return new FlightMovementLogicalKey (getFlightId(), getDepartureAd(), getDayOfFlight(), getDepartureTime());
    }
}
