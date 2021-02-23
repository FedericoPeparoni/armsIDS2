package ca.ids.abms.modules.aircraft;

import javax.validation.constraints.NotNull;

import com.opencsv.bean.CsvBindByPosition;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;

@ControlCSVFile(ignoreFirstRows = 1)
public class AircraftRegistrationCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @NotNull
    @CsvBindByPosition(position = 0)
    private String registrationNumber;

    @NotNull
    @CsvBindByPosition(position = 1)
    private String accountName;

    @NotNull
    @CsvBindByPosition(position = 2)
    private String startDate;

    @NotNull
    @CsvBindByPosition(position = 3)
    private String expiryDate;

    @NotNull
    @CsvBindByPosition(position = 4)
    private String aircraftType;

    @NotNull
    @CsvBindByPosition(position = 5)
    private String mtowOverride;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AircraftRegistrationCsvViewModel that = (AircraftRegistrationCsvViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;

    }

    public String getAccountName() {
        return accountName;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public Integer getId() {
        return id;
    }

    public String getMtowOverride() {
        return mtowOverride;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setAccountName(String aAccountName) {
        accountName = aAccountName;
    }

    public void setAircraftType(String aAircraftType) {
        aircraftType = aAircraftType;
    }

    public void setExpiryDate(String aExpiryDate) {
        expiryDate = aExpiryDate;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setMtowOverride(String aMtowOverride) {
        mtowOverride = aMtowOverride;
    }

    public void setRegistrationNumber(String aRegistrationNumber) {
        registrationNumber = aRegistrationNumber;
    }

    public void setStartDate(String aStartDate) {
        startDate = aStartDate;
    }

    @Override
    public String toString() {
        return "AircraftRegistrationCsvViewModel [id=" + id + ", registrationNumber=" + registrationNumber
                + ", accountName=" + accountName + ", startDate=" + startDate + ", expiryDate=" + expiryDate
                + ", aircraftType=" + aircraftType + ", mtowOverride=" + mtowOverride + "]";
    }
}
