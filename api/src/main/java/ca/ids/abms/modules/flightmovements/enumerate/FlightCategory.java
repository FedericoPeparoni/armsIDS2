package ca.ids.abms.modules.flightmovements.enumerate;

public enum FlightCategory {
	SHEDULED("SCH"),
	NONSHEDULED("nonsch");


    private String value;

    FlightCategory(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "FlightCategory{" +
            "value='" + value + '\'' +
            '}';
    }
}
