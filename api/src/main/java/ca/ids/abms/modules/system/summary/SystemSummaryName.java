package ca.ids.abms.modules.system.summary;

public enum SystemSummaryName {

    DOMESTIC("DOMESTIC"),
    INTERNATIONAL_ARRIVAL("INTERNATIONAL_ARRIVAL"),
    INTERNATIONAL_DEPARTURE("INTERNATIONAL_DEPARTURE"),
    TOTAL("TOTAL"),
    TOTAL_FLIGHTS("TOTAL_FLIGHTS"),
    OVERFLIGHT("OVERFLIGHT"),
    OUTSIDE_BILLING_AREA("OUTSIDE_BILLING_AREA"),
    INSIDE_BILLING_AREA("INSIDE_BILLING_AREA"),
    ALL_FLIGHT("ALL_FLIGHT"),
    UNKNOWN_AIRCRAFT_TYPE("UNKNOWN_AIRCRAFT_TYPE"),
    BLACKLISTED_ACCOUNT("BLACKLISTED_ACCOUNT"),
    BLACKLISTED_MOVEMENT("BLACKLISTED_MOVEMENT"),
    INTERNATIONAL_ACCOUNT("INTERNATIONAL_ACCOUNT"),
    DOMESTIC_ACCOUNT("DOMESTIC_ACCOUNT"),
    REJECTED("REJECTED"),
    OUSTANDING_BILL("OUSTANDING_BILL"),
    OVERDUE_BILL("OVERDUE_BILL"),
    LATEST_FLIGHT("LATEST_FLIGHT");

    private String value;

    SystemSummaryName(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SystemSummaryName{" + "value='" + value + '\'' + '}';
    }

}
