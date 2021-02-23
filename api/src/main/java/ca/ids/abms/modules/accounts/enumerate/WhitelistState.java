package ca.ids.abms.modules.accounts.enumerate;

public enum WhitelistState {
    ACTIVE("ACTIVE"),
    INACTIVE("INACTIVE"),
    EXPIRED("EXPIRED");

    private String value;

    WhitelistState(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "WhitelistState{" +
            "value='" + value + '\'' +
            '}';
    }
}
