package ca.ids.abms.modules.flightmovementsbuilder;

public class FlightMovementNoteMessage {

    static final String EXEMPT_FLIGHTS_DISTANCE = "Flight shorter than minimum crossing distance";
    static final String MIN_DOMESTIC_CROSSING_DISTANCE = "Distance < Min Dom Distance";
    static final String MIN_REGIONAL_CROSSING_DISTANCE = "Distance < Min Reg Distance";
    static final String MIN_INTERNATIONAL_CROSSING_DISTANCE = "Distance < Min Int Distance";
    static final String MAX_DOMESTIC_CROSSING_DISTANCE = "Distance > Max Dom Distance";
    static final String MAX_REGIONAL_CROSSING_DISTANCE = "Distance > Max Reg Distance";
    static final String MAX_INTERNATIONAL_CROSSING_DISTANCE = "Distance > Max Int Distance";

    public static final int MIN = 0;

    public static final int MAX = 1;

    private FlightMovementNoteMessage() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
