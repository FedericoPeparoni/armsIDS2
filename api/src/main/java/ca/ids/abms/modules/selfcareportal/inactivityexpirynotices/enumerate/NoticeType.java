package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices.enumerate;

public enum NoticeType {
    INACTIVE("INACTIVE"),
    EXPIRED("EXPIRED");

    private String value;

    NoticeType(String value){
        this.value=value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "NoticeType{" +
            "value='" + value + '\'' +
            '}';
    }
}
