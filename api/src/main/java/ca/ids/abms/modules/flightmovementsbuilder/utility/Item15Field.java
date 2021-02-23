package ca.ids.abms.modules.flightmovementsbuilder.utility;

public enum Item15Field {
    THRU("THRUPLAN"),
    CRUISING_SPEED("CS"),
    EET("EET"),
    AERODROME("AD"),
    FLIGHT_LEVEL("FL");

    private String value;

    Item15Field(String value){
        this.value=value;
    }


    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Item15Field{" +
            "value='" + value + '\'' +
            '}';
    }
}
