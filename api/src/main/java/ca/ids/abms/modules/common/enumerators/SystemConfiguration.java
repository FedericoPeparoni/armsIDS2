package ca.ids.abms.modules.common.enumerators;

public enum SystemConfiguration {

    CROSSING_DISTANCE_PRECEDENCE("Crossing distance precedence");
    
    private String value;

    SystemConfiguration(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }


    @Override
    public String toString() {
        return "SystemConfiguration{" +
            "value='" + value + '\'' +
            '}';
    }
}
