package ca.ids.abms.modules.utilities.schedules;

import ca.ids.abms.util.csv.annotations.CsvProperty;

public class UtilitiesScheduleCsvExportModel {

    @CsvProperty(value = "Schedule Identifier", precision = 0)
    private Integer scheduleId;

    private String scheduleType;

    @CsvProperty(precision = 2)
    private Integer  minimumCharge;

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public void setScheduleType(String scheduleType) {
        this.scheduleType = scheduleType;
    }

    public Integer getMinimumCharge() {
        return minimumCharge;
    }

    public void setMinimumCharge(Integer minimumCharge) {
        this.minimumCharge = minimumCharge;
    }
}
