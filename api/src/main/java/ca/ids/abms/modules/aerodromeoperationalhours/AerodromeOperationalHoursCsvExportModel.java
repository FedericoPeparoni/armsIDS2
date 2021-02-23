package ca.ids.abms.modules.aerodromeoperationalhours;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class AerodromeOperationalHoursCsvExportModel {

    private String aerodrome;

    private String operationalHoursMonday;

    private String operationalHoursTuesday;

    private String operationalHoursWednesday;

    private String operationalHoursThursday;

    private String operationalHoursFriday;

    private String operationalHoursSaturday;

    private String operationalHoursSunday;

    @CsvProperty(value = "Holiday Dates Holidays A")
    private String holidayDatesHolidays1;

    @CsvProperty(value = "Operational Hours Holidays A")
    private String operationalHoursHolidays1;

    @CsvProperty(value = "Holiday Dates Holidays B")
    private String holidayDatesHolidays2;

    @CsvProperty(value = "Operational Hours Holidays B")
    private String operationalHoursHolidays2;

    public String getAerodrome() {
        return aerodrome;
    }

    public void setAerodrome(String aerodrome) {
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

    public String getHolidayDatesHolidays1() {
        return holidayDatesHolidays1;
    }

    public void setHolidayDatesHolidays1(String holidayDatesHolidays1) {
        this.holidayDatesHolidays1 = holidayDatesHolidays1;
    }

    public String getOperationalHoursHolidays1() {
        return operationalHoursHolidays1;
    }

    public void setOperationalHoursHolidays1(String operationalHoursHolidays1) {
        this.operationalHoursHolidays1 = operationalHoursHolidays1;
    }

    public String getHolidayDatesHolidays2() {
        return holidayDatesHolidays2;
    }

    public void setHolidayDatesHolidays2(String holidayDatesHolidays2) {
        this.holidayDatesHolidays2 = holidayDatesHolidays2;
    }

    public String getOperationalHoursHolidays2() {
        return operationalHoursHolidays2;
    }

    public void setOperationalHoursHolidays2(String operationalHoursHolidays2) {
        this.operationalHoursHolidays2 = operationalHoursHolidays2;
    }
}
