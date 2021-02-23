package ca.ids.abms.modules.aerodromeoperationalhours;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.aerodromes.Aerodrome;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Entity
@Table(name = "aerodrome_operational_hours")
@UniqueKey(columnNames = "aerodrome")
public class AerodromeOperationalHours extends VersionedAuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name="aerodrome_id")
    @SearchableEntity
    private Aerodrome aerodrome;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursMonday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursTuesday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursWednesday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursThursday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursFriday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursSaturday;

    @Size(max = 255)
    @MergeOnNull
    @SearchableText
    private String operationalHoursSunday;

    @Size(max = 255)
    @Column(name = "operational_hours_holidays_1")
    @MergeOnNull
    @SearchableText
    private String operationalHoursHolidays1;

    @Size(max = 255)
    @Column(name = "operational_hours_holidays_2")
    @MergeOnNull
    @SearchableText
    private String operationalHoursHolidays2;

    @Size(max = 255)
    @Column(name = "holiday_dates_holidays_1")
    @MergeOnNull
    @SearchableText
    private String holidayDatesHolidays1;

    @Size(max = 255)
    @Column(name = "holiday_dates_holidays_2")
    @MergeOnNull
    @SearchableText
    private String holidayDatesHolidays2;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Aerodrome getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(Aerodrome aerodrome) {
        this.aerodrome = aerodrome;
    }

    public String getOperationalHoursMonday() {
        return operationalHoursMonday;
    }

    public void setOperationalHoursMonday(String operationalHoursMonday) {
        this.operationalHoursMonday = operationalHoursMonday;
    }

    public String getOperationalHoursTuesday() {
        return operationalHoursTuesday;
    }

    public void setOperationalHoursTuesday(String operationalHoursTuesday) {
        this.operationalHoursTuesday = operationalHoursTuesday;
    }

    public String getOperationalHoursWednesday() {
        return operationalHoursWednesday;
    }

    public void setOperationalHoursWednesday(String operationalHoursWednesday) {
        this.operationalHoursWednesday = operationalHoursWednesday;
    }

    public String getOperationalHoursThursday() {
        return operationalHoursThursday;
    }

    public void setOperationalHoursThursday(String operationalHoursThursday) {
        this.operationalHoursThursday = operationalHoursThursday;
    }

    public String getOperationalHoursFriday() {
        return operationalHoursFriday;
    }

    public void setOperationalHoursFriday(String operationalHoursFriday) {
        this.operationalHoursFriday = operationalHoursFriday;
    }

    public String getOperationalHoursSaturday() {
        return operationalHoursSaturday;
    }

    public void setOperationalHoursSaturday(String operationalHoursSaturday) {
        this.operationalHoursSaturday = operationalHoursSaturday;
    }

    public String getOperationalHoursSunday() {
        return operationalHoursSunday;
    }

    public void setOperationalHoursSunday(String operationalHoursSunday) {
        this.operationalHoursSunday = operationalHoursSunday;
    }

    public String getOperationalHoursHolidays1() {
        return operationalHoursHolidays1;
    }

    public void setOperationalHoursHolidays1(String operationalHoursHolidays1) {
        this.operationalHoursHolidays1 = operationalHoursHolidays1;
    }

    public String getOperationalHoursHolidays2() {
        return operationalHoursHolidays2;
    }

    public void setOperationalHoursHolidays2(String operationalHoursHolidays2) {
        this.operationalHoursHolidays2 = operationalHoursHolidays2;
    }

    public String getHolidayDatesHolidays1() {
        return holidayDatesHolidays1;
    }

    public void setHolidayDatesHolidays1(String holidayDatesHolidays1) {
        this.holidayDatesHolidays1 = holidayDatesHolidays1;
    }

    public String getHolidayDatesHolidays2() {
        return holidayDatesHolidays2;
    }

    public void setHolidayDatesHolidays2(String holidayDatesHolidays2) {
        this.holidayDatesHolidays2 = holidayDatesHolidays2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AerodromeOperationalHours that = (AerodromeOperationalHours) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AerodromeOperationalHours{" +
            "id=" + id +
            ", aerodrome=" + aerodrome +
            ", operationalHoursMonday='" + operationalHoursMonday + '\'' +
            ", operationalHoursTuesday='" + operationalHoursTuesday + '\'' +
            ", operationalHoursWednesday='" + operationalHoursWednesday + '\'' +
            ", operationalHoursThursday='" + operationalHoursThursday + '\'' +
            ", operationalHoursFriday='" + operationalHoursFriday + '\'' +
            ", operationalHoursSaturday='" + operationalHoursSaturday + '\'' +
            ", operationalHoursSunday='" + operationalHoursSunday + '\'' +
            ", operationalHoursHolidays1='" + operationalHoursHolidays1 + '\'' +
            ", operationalHoursHolidays2='" + operationalHoursHolidays2 + '\'' +
            ", holidayDatesHolidays1='" + holidayDatesHolidays1 + '\'' +
            ", holidayDatesHolidays2='" + holidayDatesHolidays2 + '\'' +
            '}';
    }
}
