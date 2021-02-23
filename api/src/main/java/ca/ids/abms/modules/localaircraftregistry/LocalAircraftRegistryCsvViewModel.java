package ca.ids.abms.modules.localaircraftregistry;

import ca.ids.abms.modules.common.dto.DefaultRejectableCsvModel;
import ca.ids.abms.modules.common.mappers.ControlCSVFile;
import com.opencsv.bean.CsvBindByPosition;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@ControlCSVFile(ignoreFirstRows = 17)
public class LocalAircraftRegistryCsvViewModel extends DefaultRejectableCsvModel {

    private Integer id;

    @CsvBindByPosition(position = 3)
    @NotNull
    @Size(max = 50)
    private String registrationNumber;

    @CsvBindByPosition(position = 11)
    @NotNull
    private String ownerName;

    @CsvBindByPosition(position = 14)
    @NotNull
    @Size(max = 100)
    private String analysisType;

    @CsvBindByPosition(position = 18)
    @NotNull
    private String mtowWeight;

    @CsvBindByPosition(position = 25)
    @NotNull
    private String coaDateOfRenewal;

    @CsvBindByPosition(position = 26)
    @NotNull
    private String coaDateOfExpiry;

    @Size(min = 4, max = 50)
    private String createdBy;

    @Size(min = 4, max = 50)
    private String updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Integer getId() {
        return id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public String getMtowWeight() {
        return mtowWeight;
    }

    public String getCoaDateOfRenewal() {
        return coaDateOfRenewal;
    }

    public String getCoaDateOfExpiry() {
        return coaDateOfExpiry;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Integer aId) {
        id = aId;
    }

    public void setRegistrationNumber(String aRegistrationNumber) {
        registrationNumber = aRegistrationNumber.toUpperCase();
    }

    public void setOwnerName(String aOwnerName) {
        ownerName = aOwnerName;
    }

    public void setAnalysisType(String aAnalysisType) {
        analysisType = aAnalysisType;
    }

    public void setMtowWeight(String aMtowWeight) {
        mtowWeight = aMtowWeight;
    }

    public void setCoaDateOfRenewal(String aCoaDateOfRenewal) {
        coaDateOfRenewal = aCoaDateOfRenewal;
    }

    public void setCoaDateOfExpiry(String aCoaDateOfExpiry) {
        coaDateOfExpiry = aCoaDateOfExpiry;
    }

    public void setCreatedBy(String aCreatedBy) {
        createdBy = aCreatedBy;
    }

    public void setUpdatedBy(String aUpdatedBy) {
        updatedBy = aUpdatedBy;
    }

    public void setCreatedAt(LocalDateTime aCreatedAt) {
        createdAt = aCreatedAt;
    }

    public void setUpdatedAt(LocalDateTime aUpdatedAt) {
        updatedAt = aUpdatedAt;
    }
}
