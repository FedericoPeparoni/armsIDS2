package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.util.csv.annotations.CsvProperty;

import java.time.LocalDateTime;

class FlightMovementCsvExportModel {

    @CsvProperty(date = true)
    private LocalDateTime dateOfFlight;

    private String depTime;

    private String depAd;

    private String destAd;

    private String actualDepAd;

    private String actualDestAd;

    private String flightId;

    @CsvProperty(value = "Reg Number")
    private String item18RegNum;

    @CsvProperty(value = "Account Name")
    private String associatedAccount;

    private String status;

    @CsvProperty(value = "A/C Type")
    private String aircraftType;

    @CsvProperty(value = "WTC")
    private String wakeTurb;

    @CsvProperty(value = "MTOW", mtow = true, precision = 2)
    private Double actualMtow;

    @CsvProperty(value = "W Factor")
    private Double wFactor;

    @CsvProperty(value = "Type")
    private String flightCategoryType;

    @CsvProperty(value = "Nationality")
    private String flightCategoryNationality;

    @CsvProperty(value = "Scope")
    private String flightCategoryScope;

    @CsvProperty(value = "Speed")
    private String cruisingSpeedOrMachNumber;

    @CsvProperty(value = "Flight Rules")
    private String flightRules;

    @CsvProperty(value = "Flight Type")
    private String flightType;

    @CsvProperty(value = "Category Name")
    private String flightmovementCategoryName;

    @CsvProperty(value = "BlackListed Indicator")
    private Boolean associatedAccountBlackListedIndicator;

    @CsvProperty(value = "BlackListed Override")
    private Boolean associatedAccountBlackListedOverride;

    @CsvProperty(value = "Enroute Basis")
    private String enrouteChargesBasis;

    @CsvProperty(value = "Billable Cros Dist", distance = true, precision = 2)
    private Double billableCrossingDist;


    @CsvProperty(value = "D Factor")
    private Double dFactor;

    @CsvProperty(value = "Entry Point")
    private String billableEntryPoint;

    @CsvProperty(value = "Exit Point")
    private String billableExitPoint;

    @CsvProperty(value = "Entry Time")
    private String entryTime;

    @CsvProperty(value = "Exit Time")
    private String exitTime;

    @CsvProperty(value = "Flight Level")
    private String flightLevel;

    @CsvProperty(precision = 2)
    private Double enrouteCharges;

    @CsvProperty(value = "Enroute Charges Currency")
    private String enrouteResultCurrency;

    @CsvProperty(value = "Approach Charges", precision = 2)
    private Double approachCharges;

    @CsvProperty(value = "Approach Currency")
    private String approachChargesCurrency;

    @CsvProperty(value = "Aerodrome", precision = 2)
    private Double aerodromeCharges;

    @CsvProperty(value = "Aerodrome Currency")
    private String  aerodromeChargesCurrency;

    @CsvProperty(value = "Dom PAX", precision = 2)
    private Double domesticPassengerCharges;

    @CsvProperty(value = "Intl PAX", precision = 2)
    private Double internationalPassengerCharges;

    @CsvProperty(precision = 2)
    private Double lateArrivalCharges;

    @CsvProperty(precision = 2)
    private Double lateDepartureCharges;

    @CsvProperty(value = "Late Arrival Departure Currency")
    private String lateArrivalDepartureChargesCurrency;

    @CsvProperty(precision = 2)
    private Double extendedHoursSurcharge;

    @CsvProperty(value = "Extended Hours Currency")
    private String extendedHoursSurchargeCurrency;

    @CsvProperty(precision = 2)
    private Double parkingCharges;

    @CsvProperty(value = "Prepaid", precision = 2)
    private Double prepaidAmount;

    @CsvProperty(value = "Sched Dist", distance = true, precision = 2)
    private Double fplCrossingDistance;

    @CsvProperty(value = "Radar Dist", distance = true, precision = 2)
    private Double radarCrossingDistance;

    @CsvProperty(value = "Nominal Dist", distance = true, precision = 2)
    private Double nominalCrossingDistance;

    @CsvProperty(value = "ATC Dist", distance = true, precision = 2)
    private Double atcCrossingDistance;

    @CsvProperty(value = "Tower Dist", distance = true, precision = 2)
    private Double towerCrossingDistance;

    @CsvProperty(value = "User Dist", distance = true, precision = 2)
    private Double userCrossingDistance;


    @CsvProperty(value = "Resolution Errors")
    private String resolutionErrorsSet;



    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public String getActualDepAd() {
        return actualDepAd;
    }

    public void setActualDepAd(String actualDepAd) {
        this.actualDepAd = actualDepAd;
    }

    public String getActualDestAd() {
        return actualDestAd;
    }

    public void setActualDestAd(String actualDestAd) {
        this.actualDestAd = actualDestAd;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getItem18RegNum() {
        return item18RegNum;
    }

    public void setItem18RegNum(String item18RegNum) {
        this.item18RegNum = item18RegNum;
    }

    public String getAssociatedAccount() {
        return associatedAccount;
    }

    public void setAssociatedAccount(String associatedAccount) {
        this.associatedAccount = associatedAccount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getWakeTurb() {
        return wakeTurb;
    }

    public void setWakeTurb(String wakeTurb) {
        this.wakeTurb = wakeTurb;
    }

    public Double getActualMtow() {
        return actualMtow;
    }

    public void setActualMtow(Double actualMtow) {
        this.actualMtow = actualMtow;
    }

    public String getFlightCategoryType() {
        return flightCategoryType;
    }

    public void setFlightCategoryType(String flightCategoryType) {
        this.flightCategoryType = flightCategoryType;
    }

    public String getFlightCategoryNationality() {
        return flightCategoryNationality;
    }

    public void setFlightCategoryNationality(String flightCategoryNationality) {
        this.flightCategoryNationality = flightCategoryNationality;
    }

    public String getFlightCategoryScope() {
        return flightCategoryScope;
    }

    public void setFlightCategoryScope(String flightCategoryScope) {
        this.flightCategoryScope = flightCategoryScope;
    }

    public String getFlightmovementCategoryName() {
        return flightmovementCategoryName;
    }

    public void setFlightmovementCategoryName(String flightmovementCategoryName) {
        this.flightmovementCategoryName = flightmovementCategoryName;
    }

    public Boolean getAssociatedAccountBlackListedIndicator() {
        return associatedAccountBlackListedIndicator;
    }

    public void setAssociatedAccountBlackListedIndicator(Boolean associatedAccountBlackListedIndicator) {
        this.associatedAccountBlackListedIndicator = associatedAccountBlackListedIndicator;
    }

    public Boolean getAssociatedAccountBlackListedOverride() {
        return associatedAccountBlackListedOverride;
    }

    public void setAssociatedAccountBlackListedOverride(Boolean associatedAccountBlackListedOverride) {
        this.associatedAccountBlackListedOverride = associatedAccountBlackListedOverride;
    }

    public String getEnrouteChargesBasis() {
        return enrouteChargesBasis;
    }

    public void setEnrouteChargesBasis(String enrouteChargesBasis) {
        this.enrouteChargesBasis = enrouteChargesBasis;
    }

    public Double getEnrouteCharges() {
        return enrouteCharges;
    }

    public void setEnrouteCharges(Double enrouteCharges) {
        this.enrouteCharges = enrouteCharges;
    }

    public String getEnrouteResultCurrency() {
        return enrouteResultCurrency;
    }

    public void setEnrouteResultCurrency(String enrouteResultCurrency) {
        this.enrouteResultCurrency = enrouteResultCurrency;
    }

    public Double getApproachCharges() {
        return approachCharges;
    }

    public void setApproachCharges(Double approachCharges) {
        this.approachCharges = approachCharges;
    }

    public String getApproachChargesCurrency() {
        return approachChargesCurrency;
    }

    public void setApproachChargesCurrency(String approachChargesCurrency) {
        this.approachChargesCurrency = approachChargesCurrency;
    }

    public Double getAerodromeCharges() {
        return aerodromeCharges;
    }

    public void setAerodromeCharges(Double aerodromeCharges) {
        this.aerodromeCharges = aerodromeCharges;
    }

    public String getAerodromeChargesCurrency() {
        return aerodromeChargesCurrency;
    }

    public void setAerodromeChargesCurrency(String aerodromeChargesCurrency) {
        this.aerodromeChargesCurrency = aerodromeChargesCurrency;
    }

    public Double getDomesticPassengerCharges() {
        return domesticPassengerCharges;
    }

    public void setDomesticPassengerCharges(Double domesticPassengerCharges) {
        this.domesticPassengerCharges = domesticPassengerCharges;
    }

    public Double getInternationalPassengerCharges() {
        return internationalPassengerCharges;
    }

    public void setInternationalPassengerCharges(Double internationalPassengerCharges) {
        this.internationalPassengerCharges = internationalPassengerCharges;
    }

    public Double getLateArrivalCharges() {
        return lateArrivalCharges;
    }

    public void setLateArrivalCharges(Double lateArrivalCharges) {
        this.lateArrivalCharges = lateArrivalCharges;
    }

    public Double getLateDepartureCharges() {
        return lateDepartureCharges;
    }

    public void setLateDepartureCharges(Double lateDepartureCharges) {
        this.lateDepartureCharges = lateDepartureCharges;
    }

    public String getLateArrivalDepartureChargesCurrency() {
        return lateArrivalDepartureChargesCurrency;
    }

    public void setLateArrivalDepartureChargesCurrency(String lateArrivalDepartureChargesCurrency) {
        this.lateArrivalDepartureChargesCurrency = lateArrivalDepartureChargesCurrency;
    }

    public Double getExtendedHoursSurcharge() {
        return extendedHoursSurcharge;
    }

    public void setExtendedHoursSurcharge(Double extendedHoursSurcharge) {
        this.extendedHoursSurcharge = extendedHoursSurcharge;
    }

    public String getExtendedHoursSurchargeCurrency() {
        return extendedHoursSurchargeCurrency;
    }

    public void setExtendedHoursSurchargeCurrency(String extendedHoursSurchargeCurrency) {
        this.extendedHoursSurchargeCurrency = extendedHoursSurchargeCurrency;
    }

    public Double getParkingCharges() {
        return parkingCharges;
    }

    public void setParkingCharges(Double parkingCharges) {
        this.parkingCharges = parkingCharges;
    }

    public Double getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(Double prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public Double getFplCrossingDistance() {
        return fplCrossingDistance;
    }

    public void setFplCrossingDistance(Double fplCrossingDistance) {
        this.fplCrossingDistance = fplCrossingDistance;
    }

    public Double getRadarCrossingDistance() {
        return radarCrossingDistance;
    }

    public void setRadarCrossingDistance(Double radarCrossingDistance) {
        this.radarCrossingDistance = radarCrossingDistance;
    }

    public Double getNominalCrossingDistance() {
        return nominalCrossingDistance;
    }

    public void setNominalCrossingDistance(Double nominalCrossingDistance) {
        this.nominalCrossingDistance = nominalCrossingDistance;
    }

    public Double getAtcCrossingDistance() {
        return atcCrossingDistance;
    }

    public void setAtcCrossingDistance(Double atcCrossingDistance) {
        this.atcCrossingDistance = atcCrossingDistance;
    }

    public Double getTowerCrossingDistance() {
        return towerCrossingDistance;
    }

    public void setTowerCrossingDistance(Double towerCrossingDistance) {
        this.towerCrossingDistance = towerCrossingDistance;
    }

    public Double getUserCrossingDistance() {
        return userCrossingDistance;
    }

    public void setUserCrossingDistance(Double userCrossingDistance) {
        this.userCrossingDistance = userCrossingDistance;
    }

    public String getResolutionErrorsSet() {
        return resolutionErrorsSet;
    }

    public void setResolutionErrorsSet(String resolutionErrorsSet) {
        this.resolutionErrorsSet = resolutionErrorsSet;
    }

    public Double getBillableCrossingDist() {
        return billableCrossingDist;
    }

    public void setBillableCrossingDist(Double billableCrossingDist) {
        this.billableCrossingDist = billableCrossingDist;
    }

    public String getBillableEntryPoint() {
        return billableEntryPoint;
    }

    public void setBillableEntryPoint(String billableEntryPoint) {
        this.billableEntryPoint = billableEntryPoint;
    }

    public String getBillableExitPoint() {
        return billableExitPoint;
    }

    public void setBillableExitPoint(String billableExitPoint) {
        this.billableExitPoint = billableExitPoint;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public String getCruisingSpeedOrMachNumber() {
        return cruisingSpeedOrMachNumber;
    }

    public void setCruisingSpeedOrMachNumber(String cruisingSpeedOrMachNumber) {
        this.cruisingSpeedOrMachNumber = cruisingSpeedOrMachNumber;
    }

    public String getFlightRules() {
        return flightRules;
    }

    public void setFlightRules(String flightRules) {
        this.flightRules = flightRules;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public Double getWFactor() {
        return wFactor;
    }

    public void  setWFactor(Double wFactor) {
        this.wFactor = wFactor;
    }

    public Double getDFactor() {
        return dFactor;
    }

    public void  setDFactor(Double dFactor) {
        this.dFactor = dFactor;
    }
}
