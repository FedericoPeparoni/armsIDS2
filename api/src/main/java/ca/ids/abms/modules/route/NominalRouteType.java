package ca.ids.abms.modules.route;

public enum NominalRouteType {

    AERODROME_TO_AERODROME("Aerodrome/Aerodrome"),
    FIR_TO_AERODROME("FIR Entry/Exit to Aerodrome"),
    FIR_TO_FIR("FIR / FIR"),
    FIR("FIR"),
    AERODROME("Aerodrome");
    
    private String value;

    NominalRouteType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "NominalRouteType{" +
            "value='" + value + '\'' +
            '}';
    }
}
