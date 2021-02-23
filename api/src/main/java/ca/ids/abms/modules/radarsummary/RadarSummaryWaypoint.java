package ca.ids.abms.modules.radarsummary;

import java.time.LocalDateTime;
import java.util.Objects;

public class RadarSummaryWaypoint {

    private LocalDateTime dateTime;

    private String point;

    private String level;
    
    public RadarSummaryWaypoint () {
    }
    
    public RadarSummaryWaypoint (final LocalDateTime dateTime, final String point, final String level) {
        this.dateTime = dateTime;
        this.point = point;
        this.level = level;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RadarSummaryWaypoint that = (RadarSummaryWaypoint) o;
        return Objects.equals(dateTime, that.dateTime) &&
            Objects.equals(point, that.point) &&
            Objects.equals(level, that.level);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dateTime, point, level);
    }

    @Override
    public String toString() {
        return "RadarSummaryWaypoint{" +
            "dateTime=" + dateTime +
            ", point='" + point + '\'' +
            ", level='" + level + '\'' +
            '}';
    }
}
