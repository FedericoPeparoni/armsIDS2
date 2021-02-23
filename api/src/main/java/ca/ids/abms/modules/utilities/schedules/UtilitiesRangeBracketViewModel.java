package ca.ids.abms.modules.utilities.schedules;

import javax.validation.constraints.NotNull;

public class UtilitiesRangeBracketViewModel {

    private Integer id;

    @NotNull
    private Integer scheduleId;

    @NotNull
    private Double rangeTopEnd;

    @NotNull
    private Double unitPrice;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Double getRangeTopEnd() {
        return rangeTopEnd;
    }

    public void setRangeTopEnd(Double rangeTopEnd) {
        this.rangeTopEnd = rangeTopEnd;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesRangeBracketViewModel that = (UtilitiesRangeBracketViewModel) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return scheduleId != null ? scheduleId.equals(that.scheduleId) : that.scheduleId == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (scheduleId != null ? scheduleId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UtilitiesRangeBracketViewModel{" +
                "id=" + id +
                ", scheduleId=" + scheduleId +
                ", rangeTopEnd=" + rangeTopEnd +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
