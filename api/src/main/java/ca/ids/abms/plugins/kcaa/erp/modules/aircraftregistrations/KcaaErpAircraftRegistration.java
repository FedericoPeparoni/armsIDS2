package ca.ids.abms.plugins.kcaa.erp.modules.aircraftregistrations;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "KCAA erp$Sales Invoice Line")
@SuppressWarnings("WeakerAccess")
public class KcaaErpAircraftRegistration {

    static final String ANNUAL_ANS_CHARGE_COLUMN_NAME = "Annual ANS Charge";

    static final String COA_END_DATE_COLUMN_NAME = "CoA End Date";

    static final String COA_START_DATE_COLUMN_NAME = "CoA Start Date";

    static final String MTOW_COLUMN_NAME = "MTOM";

    static final String REG_MARK_COLUMN_NAME = "Reg_ Mark";

    static final String TIMESTAMP_COLUMN_NAME = "timestamp";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="registration_number")
    @NotNull
    private String registrationNumber;

    @Column(name="owner_name")
    @NotNull
    private String ownerName;

    @Column(name="analysis_type")
    @NotNull
    private String analysisType;

    @Column(name="mtow_weight")
    @NotNull
    private Double mtowWeight;

    @Column(name="coa_date_of_renewal")
    @NotNull
    private LocalDateTime coaDateOfRenewal;

    @Column(name="coa_date_of_expiry")
    @NotNull
    private LocalDateTime coaDateOfExpiry;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public Double getMtowWeight() {
        return mtowWeight;
    }

    public void setMtowWeight(Double mtowWeight) {
        this.mtowWeight = mtowWeight;
    }

    public LocalDateTime getCoaDateOfRenewal() {
        return coaDateOfRenewal;
    }

    public void setCoaDateOfRenewal(LocalDateTime coaDateOfRenewal) {
        this.coaDateOfRenewal = coaDateOfRenewal;
    }

    public LocalDateTime getCoaDateOfExpiry() {
        return coaDateOfExpiry;
    }

    public void setCoaDateOfExpiry(LocalDateTime coaDateOfExpiry) {
        this.coaDateOfExpiry = coaDateOfExpiry;
    }
}
