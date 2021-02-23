package ca.ids.abms.plugins.amhs.fpl;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@SuppressWarnings ({ "squid:ClassVariableVisibilityCheck", "squid:S3776" })
public class FlightMessage {
    
    public Item3 item3;     // type, refNum
    public Item7 item7;     // callsign, ssrMode
    public Item8 item8;     // flightRules, flightType
    public Item9 item9;     // aircraftNumber, aircraftType, wakeTurb
    public Item10 item10;   // equipment
    public Item13 item13;   // departureAirport, departureTime
    public Item15 item15;   // cruisingSpeed, flightLevel, route
    public Item16 item16;   // destinationAirport, altDestinationAirport, altDestinationAirport2, totalEet
    public Item17 item17;   // arrivalAirport, arrivalTime, arrivalAirportName
    public Item18 item18;   // dayOfFlight, otherInfo
    public Item22 item22;   // amendments
    
    public FlightMessage() {
    }

    @JsonIgnore
    public String getCallsign() {
        return item7 == null ? null : item7.callsign;
    }
    
    @JsonIgnore
    public String getDepartureAirport() {
        return item13 == null ? null : item13.departureAirport;
    }
    
    @JsonIgnore
    public String getDepartureTime() {
        return item13 == null ? null : item13.departureTime;
    }
    
    @JsonIgnore
    public LocalDate getDayOfFlight() {
        return item18 == null ? null : item18.dayOfFlight;
    }
    
    @JsonIgnore
    public String getDestinationAirport() {
        return item16 == null ? null : item16.destinationAirport;
    }
    
    @JsonIgnore
    public String getTotalEet() {
        return item16 == null ? null : item16.totalEet;
    }

    @Override
    public String toString() {
        return "FlightMessage [" + (item3 != null ? "item3=" + item3 + ", " : "")
                + (item7 != null ? "item7=" + item7 + ", " : "") + (item8 != null ? "item8=" + item8 + ", " : "")
                + (item9 != null ? "item9=" + item9 + ", " : "") + (item10 != null ? "item10=" + item10 + ", " : "")
                + (item13 != null ? "item13=" + item13 + ", " : "") + (item15 != null ? "item15=" + item15 + ", " : "")
                + (item16 != null ? "item16=" + item16 + ", " : "") + (item17 != null ? "item17=" + item17 + ", " : "")
                + (item18 != null ? "item18=" + item18 + ", " : "") + (item22 != null ? "item22=" + item22 : "") + "]";
    }


}
