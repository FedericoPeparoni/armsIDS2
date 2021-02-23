package ca.ids.abms.modules.route;

public enum NominalRouteStatus {

    CALCULATED("calculated"),
    MANUAL("manual");

    private String value;

    NominalRouteStatus(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "NominalRouteStatus{" +
            "value='" + value + '\'' +
            '}';
    }
}
