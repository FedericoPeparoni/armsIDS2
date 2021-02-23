package ca.ids.abms.modules.routesegments;

/**
 * Created by c.talpa on 07/02/2017.
 */
public enum SegmentType {

    SCHED("SCHED"), RADAR("RADAR"), ATC("ATC"), TOWER("TOWER"), USER("USER"), NOMINAL("NOMINAL");

    private String value;

    SegmentType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SegmentType{" + "value='" + value + '\'' + '}';
    }
}
