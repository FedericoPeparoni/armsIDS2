package ca.ids.abms.modules.flightmovementsbuilder.utility;


public enum Item18Field {

    STS("STS/"),
    REG("REG/"),
    DEST("DEST/"),
    DEP("DEP/"),
    OPR("OPR/"),
    RMK("RMK/"),
	TYP("TYP/");

    private String value;

    Item18Field(String value){
        this.value=value;
    }


    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Item18Field{" +
            "value='" + value + '\'' +
            '}';
    }
}
