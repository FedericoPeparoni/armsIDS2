package ca.ids.abms.modules.utilities.schedules;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class UtilitiesRangeBracket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    @NotNull
    private UtilitiesSchedule schedule;

    @NotNull
    private Double rangeTopEnd;

    @NotNull
    private Double unitPrice;

    public UtilitiesSchedule getSchedule() {
        return schedule;
    }

    public void setSchedule(UtilitiesSchedule schedule) {
        this.schedule = schedule;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UtilitiesRangeBracket that = (UtilitiesRangeBracket) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
