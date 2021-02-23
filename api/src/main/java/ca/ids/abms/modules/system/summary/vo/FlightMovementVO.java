package ca.ids.abms.modules.system.summary.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class FlightMovementVO {

    private Long total;

    private Long val;

    private BigDecimal percent;

    private String name;

    private Timestamp date;

    public Timestamp getDate() {
        return date;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPercent() {
        if (percent != null)
            return percent.setScale(2, BigDecimal.ROUND_HALF_UP);
        else return percent;
    }

    public Long getTotal() {
        return total;
    }

    public Long getVal() {
        return val;
    }

    public void setDate(Timestamp aDate) {
        date = aDate;
    }

    public void setName(String aName) {
        name = aName;
    }

    public void setPercent(BigDecimal aPercent) {
        percent = aPercent;
    }

    public void setTotal(Long aTotal) {
        total = aTotal;
    }

    public void setVal(Long aVal) {
        val = aVal;
    }

    @Override
    public String toString() {
        return "FlightMovementVO [total=" + total + ", val=" + val + ", percent=" + percent + ", name=" + name
                + ", date=" + date + "]";
    }
}
