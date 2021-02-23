package ca.ids.abms.modules.flight;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import com.opencsv.bean.CsvBindByPosition;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;

@ControlCSVFile
public class FlightScheduleCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @NotNull
    @CsvBindByPosition(position = 0)
    private String flightServiceNumber;

    @NotNull
    @CsvBindByPosition(position = 1)
    private String depAd;

    @NotNull
    @CsvBindByPosition(position = 2)
    private String depTime;

    @NotNull
    @CsvBindByPosition(position = 3)
    private String destAd;

    @NotNull
    @CsvBindByPosition(position = 4)
    private String destTime;

    @NotNull
    @CsvBindByPosition(position = 5)
    private String dailySchedule;
    
    private LocalDateTime startDate;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlightScheduleCsvViewModel that = (FlightScheduleCsvViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public String getDailySchedule() {
        return dailySchedule;
    }

    public String getDepAd() {
        return depAd;
    }

    public String getDepTime() {
        return depTime;
    }

    public String getDestAd() {
        return destAd;
    }

    public String getDestTime() {
        return destTime;
    }

    public String getFlightServiceNumber() {
        return flightServiceNumber;
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setDailySchedule(String aDailySchedule) {
        dailySchedule = aDailySchedule;
    }

    public void setDepAd(String aDepAd) {
        depAd = aDepAd;
    }

    public void setDepTime(String aDepTime) {
        depTime = aDepTime;
    }

    public void setDestAd(String aDestAd) {
        destAd = aDestAd;
    }

    public void setDestTime(String aDestTime) {
        destTime = aDestTime;
    }

    public void setFlightServiceNumber(String aFlightServiceNumber) {
        flightServiceNumber = aFlightServiceNumber;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setStartDate(LocalDateTime aStartDate) {
        startDate = aStartDate;
    }

    @Override
    public String toString() {
        return "FlightScheduleCsvViewModel [id=" + id + ", flightServiceNumber=" + flightServiceNumber + ", depAd="
                + depAd + ", depTime=" + depTime + ", destAd=" + destAd + ", destTime=" + destTime + ", dailySchedule="
                + dailySchedule + "]";
    }
}
