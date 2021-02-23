package ca.ids.abms.modules.radarsummary;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.UseCustomDataMapper;
import ca.ids.abms.modules.radarsummary.enumerators.RadarSummaryFormat;
import ca.ids.abms.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import javax.validation.constraints.Size;

@JsonPropertyOrder({ "id", "flightTravelCategory", "flightIdentifier", "date", "dayOfFlight", "registration",
    "flightType", "flightRule", "aircraftType", "destinationAeroDrome", "destTime", "departureAeroDrome",
    "departureTime", "firEntryPoint", "firEntryTime", "firExitPoint", "firExitTime", "route" })
@UseCustomDataMapper(qualifier = "RadarSummaryCSVMapping",
    header="Flight travel category,Flight ID,Date,Registration,Flight type,Flight Rule,Aircraft Type,,," +
        "Dest. Aerodrome,Dest. Time,Dep. Aerodrome,Dep. Time,,Entry Point,Entry Time,")
public class RadarSummaryCsvViewModel extends DefaultRejectableCsvModel {
    
    private Integer id;

    private String date;

    private String flightIdentifier;

    private String dayOfFlight;

    private String departureTime;

    private String registration;

    private String aircraftType;

    private String departureAeroDrome;

    private String destinationAeroDrome;

    private String route;

    private String firEntryPoint;

    private String firEntryTime;
    
    private String firEntryFlightLevel;

    private String firExitPoint;

    private String firExitTime;
    
    private String firExitFlightLevel;

    private String flightRule;

    private String flightTravelCategory;

    private String flightType;

    private String destTime;

    private String operatorName;

    private String operatorIcaoCode;
    
    private RadarSummaryFormat format;
    
    private String cruisingSpeed;
    
    private String flightLevel;
    
    private String wakeTurb;
    
    private String fixes;
    
    private Integer segment;
    
    private String entryCoordinate;
    private String exitCoordinate;

    public String getEntryCoordinate() {
        return entryCoordinate;
    }

    public void setEntryCoordinate(String entryCoordinate) {
        this.entryCoordinate = entryCoordinate;
    }

    public String getExitCoordinate() {
        return exitCoordinate;
    }

    public void setExitCoordinate(String exitCoordinate) {
        this.exitCoordinate = exitCoordinate;
    }

    public String getFlightIdentifier() {
        return flightIdentifier;
    }

    public void setFlightIdentifier(String flightIdentifier) {
        this.flightIdentifier = flightIdentifier;
    }

    public Integer getSegment() {
        return segment;
    }

    public void setSegment(Integer segment) {
        this.segment = segment;
    }

    public RadarSummaryFormat getFormat() {
        return format;
    }

    public void setFormat(RadarSummaryFormat format) {
        this.format = format;
    }
    
    public String getCruisingSpeed() {
        return cruisingSpeed;
    }

    public void setCruisingSpeed(String cruisingSpeed) {
        this.cruisingSpeed = cruisingSpeed;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }
    
    public String getWakeTurb() {
        return wakeTurb;
    }

    public void setWakeTurb(String wakeTurb) {
        this.wakeTurb = wakeTurb;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("flightTravelCategory")
    public String getFlightTravelCategory() {
        return flightTravelCategory;
    }

    @JsonProperty("flightTravelCategory")
    public void setFlightTravelCategory(String flightTravelCategory) {
        this.flightTravelCategory = flightTravelCategory;
    }

    @Size(max = 10)
    @JsonProperty("flightIdentifier")
    public String getFlightId() {return flightIdentifier;}

    @JsonProperty("flightIdentifier")
    public void setFlightId(String flightIdentifier) {
        this.flightIdentifier = flightIdentifier;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("date")
    public void setDate(String date) {
        this.date = date;
    }

    @JsonProperty("registration")
    public String getRegistration() {
        return registration;
    }

    @JsonProperty("registration")
    public void setRegistration(String registration) {
        this.registration = registration;
    }

    @JsonProperty("flightType")
    public String getFlightType() {
        return flightType;
    }

    @JsonProperty("flightType")
    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    @JsonProperty("flightRule")
    public String getFlightRule() {
        return flightRule;
    }

    @JsonProperty("flightRule")
    public void setFlightRule(String flightRule) {
        this.flightRule = flightRule;
    }

    @JsonProperty("aircraftType")
    public String getAircraftType() {
        return aircraftType;
    }

    @JsonProperty("aircraftType")
    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    @JsonProperty("dayOfFlight")
    public String getDayOfFlight() {
        return dayOfFlight;
    }

    @JsonProperty("dayOfFlight")
    public void setDayOfFlight(String dayOfFlight) {
        this.dayOfFlight = dayOfFlight;
    }

    @JsonProperty("route")
    public String getRoute() {
        return route;
    }

    @JsonProperty("route")
    public void setRoute(String route) {
        this.route = route;
    }

    @JsonProperty("destinationAeroDrome")
    public String getDestinationAeroDrome() {
        return destinationAeroDrome;
    }

    @JsonProperty("destinationAeroDrome")
    public void setDestinationAeroDrome(String destinationAeroDrome) {
        this.destinationAeroDrome = destinationAeroDrome;
    }

    @JsonProperty("destTime")
    public String getDestTime() {
        return destTime;
    }

    @JsonProperty("destTime")
    public void setDestTime(String destTime) {
        this.destTime = destTime;
    }

    @JsonProperty("departureAeroDrome")
    public String getDepartureAeroDrome() {
        return departureAeroDrome;
    }

    @JsonProperty("departureAeroDrome")
    public void setDepartureAeroDrome(String departureAeroDrome) {
        this.departureAeroDrome = departureAeroDrome;
    }

    @JsonProperty("departureTime")
    public String getDepartureTime() {
        return departureTime;
    }

    @JsonProperty("departureTime")
    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    @JsonProperty("firEntryPoint")
    public String getFirEntryPoint() {
        return firEntryPoint;
    }

    @JsonProperty("firEntryPoint")
    public void setFirEntryPoint(String firEntryPoint) {
        this.firEntryPoint = firEntryPoint;
    }

    @JsonProperty("firEntryTime")
    public String getFirEntryTime() {
        return firEntryTime;
    }

    @JsonProperty("firEntryTime")
    public void setFirEntryTime(String firEntryTime) {
        this.firEntryTime = firEntryTime;
    }
    
    public String getFirEntryFlightLevel() {
        return firEntryFlightLevel;
    }

    public void setFirEntryFlightLevel(String firEntryFlightLevel) {
        this.firEntryFlightLevel = firEntryFlightLevel;
    }

    @JsonProperty("firExitPoint")
    public String getFirExitPoint() {
        return firExitPoint;
    }

    @JsonProperty("firExitPoint")
    public void setFirExitPoint(String firExitPoint) {
        this.firExitPoint = firExitPoint;
    }

    @JsonProperty("firExitTime")
    public String getFirExitTime() {
        return firExitTime;
    }

    @JsonProperty("firExitTime")
    public void setFirExitTime(String firExitTime) {
        this.firExitTime = firExitTime;
    }
    
    public String getFirExitFlightLevel() {
        return firExitFlightLevel;
    }

    public void setFirExitFlightLevel(String firExitFlightLevel) {
        this.firExitFlightLevel = firExitFlightLevel;
    }

    @JsonProperty("operatorName")
    public String getOperatorName() { return operatorName; }

    @JsonProperty("operatorName")
    public void setOperatorName(String operatorName) { this.operatorName = operatorName; }

    @JsonProperty("operatorIcaoCode")
    public String getOperatorIcaoCode() { return operatorIcaoCode; }

    @JsonProperty("operatorIcaoCode")
    public void setOperatorIcaoCode(String operatorIcaoCode) { this.operatorIcaoCode = operatorIcaoCode; }

    public String getFixes() {
        return fixes;
    }
    public void setFixes(String fixes) {
        this.fixes = fixes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RadarSummaryCsvViewModel that = (RadarSummaryCsvViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "RadarSummary{" +
            "flightTravelCategory='" + flightTravelCategory + '\'' +
            ", flightIdentifier='" + flightIdentifier + '\'' +
            ", date=" + date +
            ", registration='" + registration + '\'' +
            ", flightType='" + flightType + '\'' +
            ", flightRule='" + flightRule + '\'' +
            ", aircraftType='" + aircraftType + '\'' +
            ", dayOfFlight=" + dayOfFlight +
            ", route='" + route + '\'' +
            ", destinationAeroDrome='" + destinationAeroDrome + '\'' +
            ", destTime='" + destTime + '\'' +
            ", departureAeroDrome='" + departureAeroDrome + '\'' +
            ", departureTime='" + departureTime + '\'' +
            ", firEntryPoint='" + firEntryPoint + '\'' +
            ", firEntryTime='" + firEntryTime + '\'' +
            ", firExitPoint='" + firExitPoint + '\'' +
            ", firExitTime='" + firExitTime + '\'' +
            '}';
    }


    public Boolean isValid() {

        Boolean returnValue = Boolean.TRUE;

        if (StringUtils.isBlank(this.getDepartureAeroDrome())) {

            returnValue = Boolean.FALSE;
        }

        if (StringUtils.isBlank(this.getDestinationAeroDrome())) {

            returnValue = Boolean.FALSE;
        }

        if (StringUtils.isBlank(this.getDepartureTime())) {
            returnValue = Boolean.FALSE;
        }

        if (StringUtils.isBlank(this.getFlightId())) {
            returnValue = Boolean.FALSE;
        }

        if (this.getDayOfFlight() == null) {
            returnValue = Boolean.FALSE;
        }
        return returnValue;

    }

    public String getHeader() {
        final StringBuilder headerBuilder = new StringBuilder()
            .append("Flight travel category").append(',')
            .append("Flight ID").append(',')
            .append("Date").append(',')
            .append("Registration").append(',')
            .append("Flight type").append(',')
            .append("Flight Rule").append(',')
            .append("Aircraft Type").append(',')
            .append("").append(',')
            .append("").append(',')
            .append("Dest. Aerodrome").append(',')
            .append("Dest. Time").append(',')
            .append("Dep. Aerodrome").append(',')
            .append("Dep. Time").append(',')
            .append("").append(',')
            .append("Entry Point").append(',')
            .append("Entry Time").append(',');
        return headerBuilder.toString();
    }
}
