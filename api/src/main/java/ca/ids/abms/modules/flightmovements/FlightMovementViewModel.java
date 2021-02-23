package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.routesegments.RouteSegmentViewModel;
import ca.ids.abms.modules.util.models.VersionedViewModel;
import ca.ids.abms.plugins.kcaa.aatis.modules.invoicepermit.InvoicePermit;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FlightMovementViewModel extends VersionedViewModel {

    @Id
    private Integer id;

    @Time4Digits
    private String actualDepartureTime;

    private Double actualMtow;

    private String aircraftType;

    private String arrivalAd;

    @Time4Digits
    private String arrivalTime;

    private String associatedAccount;

    private Integer associatedAccountID;

    private String associatedAccountName;

    private Boolean associatedAccountIataMember;

    private Boolean associatedAccountBlackListedIndicator;

    private Boolean associatedAccountBlackListedOverride;

    private String associatedAircraft;

    private Double averageMassFactor;

    @JsonIgnore
    private Geometry billableRoute;

    private Integer crewMembers;

    @NotNull
    private LocalDateTime dateOfFlight;

    private LocalDateTime billingDate;

    @NotNull
    private String depAd;

    @NotNull
    @Time4Digits
    private String depTime;

    @Time4Digits
    private String estimatedElapsedTime;

    @NotNull
    private String destAd;

    private Double enrouteCharges;

    private String billableEntryPoint;

    private LocalDateTime entryTime;

    private String billableExitPoint;

    private LocalDateTime exitTime;

    @NotNull
    @Size(min = 1, max = 10)
    private String flightId;

    private String flightNotes;

    @NotNull
    private String flightType;

    private Double fplCrossingCost;

    private Double fplCrossingDistance;

    private String fplRoute;

    private String initialFplData;

    private String item18Dep;

    private String item18Dest;

    private String item18RegNum;

    private String item18Status;

    private String item18AircraftType;

    private String item18Operator;

    private String movementType;

    private Double nominalCrossingCost;

    private Double nominalCrossingDistance;

    private String otherInfo;

    private Double parkingCharges;

    private Integer passengersChargeableDomestic;

    private Integer passengersChargeableIntern;

    private Integer passengersJoiningAdult;

    private Integer passengersChild;

    private Integer passengersTransitAdult;

    private Double prepaidAmount;

    private Double radarCrossingCost;

    private Double radarCrossingDistance;

    private String radarRouteText;

    private Long spatiaFplObjectId;

    private String status;

    private Double totalCharges;

    private Double totalChargesUsd;

    private String wakeTurb;

    private String item18Rmk;

    private Double atcCrossingDistance;

    private Double towerCrossingDistance;

    private Double userCrossingDistance;

    private Double atcCrossingDistanceCost;

    private Double towerCrossingDistanceCost;

    private Double userCrossingDistanceCost;

    private String enrouteChargesBasis;

    private Double lateCharges;

    private Double parkingTime;

    private Double domesticPassengerCharges;

    private Double internationalPassengerCharges;

    private Double exemptEnrouteCharges;

    private Double exemptApprochCharges;

    private Double exemptDepCharges;

    private Double exemptLateCharges;

    private Double exemptParkingCharges;

    private Double exemptDomesticPassengerCharges;

    private Double exemptInternationalPassengerCharges;

    private String enrouteChargesStatus;

    private String passengerChargesStatus;

    private String otherChargesStatus;

    private Integer enrouteInvoiceId;

    private Integer passengerInvoiceId;

    private Integer otherInvoiceId;

    private Boolean deltaFlight;

    private Set<FlightMovementValidatorIssue> resolutionErrorsSet;

    private String source;

    private String manuallyChangedFields;

    private List<RouteSegmentViewModel> routeSegments = new ArrayList<>();

    private String cruisingSpeedOrMachNumber;

    private Double approachCharges;

    private Double aerodromeCharges;

    private Double lateArrivalCharges;

    private Double lateDepartureCharges;

    private Double exemptAerodromeCharges;

    private Double exemptLateArrivalCharges;

    private Double exemptLateDepartureCharges;

    private Double taspCharge;

    private String flightRules;

    private String flightLevel;

    private Double dFactor;

    private Double wFactor;

    private boolean markedAsDuplicate = false;

    private boolean markedAsMissingBeforeThis = false;

    private boolean markedAsFirstMissing = false;

    private boolean markedAsLastMissing = false;

    private String actualDepAd;

    private String actualDestAd;

    private boolean adhocChargeRequired = false;

    private InvoicePermit invoicePermit;

    private String flightCategoryScope;

    private String flightCategoryNationality;

    private String flightCategoryType;

    private String flightmovementCategoryName;

    private Currency enrouteResultCurrency;

    private Currency enrouteInvoiceCurrency;

    private Integer arrivingPaxDomesticAirport;

    private Integer landingPaxDomesticAirport;

    private Integer transferPaxDomesticAirport;

    private Integer departingPaxDomesticAirport;

    private Integer arrivingChildDomesticAirport;

    private Integer landingChildDomesticAirport;

    private Integer transferChildDomesticAirport;

    private Integer departingChildDomesticAirport;

    private Integer exemptArrivingPaxDomesticAirport;

    private Integer exemptLandingPaxDomesticAirport;

    private Integer exemptTransferPaxDomesticAirport;

    private Integer exemptDepartingPaxDomesticAirport;

    private Double loadedGoods;

    private Double dischargedGoods;

    private Double loadedMail;

    private Double dischargedMail;

    private Currency  aerodromeChargesCurrency;

    private Currency approachChargesCurrency;

    private Currency lateArrivalDepartureChargesCurrency;

    private Boolean thruFlight;

    private String statusNotes;

    private Double billableCrossingDist;

    private Double extendedHoursSurcharge;

    private Currency extendedHoursSurchargeCurrency;

    private Double exemptExtendedHoursSurcharge;

    private Currency taspChargeCurrency;

    private Double enrouteCostToMinimum;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        FlightMovementViewModel that = (FlightMovementViewModel) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    public Currency getEnrouteResultCurrency() {
        return enrouteResultCurrency;
    }

    public Currency getEnrouteInvoiceCurrency() {
        return enrouteInvoiceCurrency;
    }

    public String getActualDepartureTime() {
        return actualDepartureTime;
    }

    public Double getActualMtow() {
        return actualMtow;
    }

    public Double getAerodromeCharges() {
        return aerodromeCharges;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public Double getApproachCharges() {
        return approachCharges;
    }

    public String getArrivalAd() {
        return arrivalAd;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getAssociatedAccount() {
        return associatedAccount;
    }

    public Boolean getAssociatedAccountBlackListedIndicator() {
        return associatedAccountBlackListedIndicator;
    }

    public Boolean getAssociatedAccountBlackListedOverride() {
        return associatedAccountBlackListedOverride;
    }

    public Integer getAssociatedAccountID() {
        return associatedAccountID;
    }

    public String getAssociatedAccountName() {
        return associatedAccountName;
    }

    public Boolean getAssociatedAccountIataMember() {
        return associatedAccountIataMember;
    }

    public String getAssociatedAircraft() {
        return associatedAircraft;
    }

    public Double getAtcCrossingDistance() {
        return atcCrossingDistance;
    }

    public Double getAtcCrossingDistanceCost() {
        return atcCrossingDistanceCost;
    }

    public Double getAverageMassFactor() {
        return averageMassFactor;
    }

    public String getBillableEntryPoint() {
        return billableEntryPoint;
    }

    public String getBillableExitPoint() {
        return billableExitPoint;
    }

    public Geometry getBillableRoute() {
        return billableRoute;
    }

    public Integer getCrewMembers() {
        return crewMembers;
    }

    public String getCruisingSpeedOrMachNumber() {
        return cruisingSpeedOrMachNumber;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public LocalDateTime getBillingDate() {
        return billingDate;
    }

    public Boolean getDeltaFlight() {
        return deltaFlight;
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

    public Double getDomesticPassengerCharges() {
        return domesticPassengerCharges;
    }

    public Double getEnrouteCharges() {
        return enrouteCharges;
    }

    public String getEnrouteChargesBasis() {
        return enrouteChargesBasis;
    }

    public String getEnrouteChargesStatus() {
        return enrouteChargesStatus;
    }

    public Integer getEnrouteInvoiceId() {
        return enrouteInvoiceId;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getEstimatedElapsedTime() {
        return estimatedElapsedTime;
    }

    public Double getExemptAerodromeCharges() {
        return exemptAerodromeCharges;
    }

    public Double getExemptApprochCharges() {
        return exemptApprochCharges;
    }

    public Double getExemptDepCharges() {
        return exemptDepCharges;
    }

    public Double getExemptDomesticPassengerCharges() {
        return exemptDomesticPassengerCharges;
    }

    public Double getExemptEnrouteCharges() {
        return exemptEnrouteCharges;
    }

    public Double getExemptInternationalPassengerCharges() {
        return exemptInternationalPassengerCharges;
    }

    public Double getExemptLateArrivalCharges() {
        return exemptLateArrivalCharges;
    }

    public Double getExemptLateCharges() {
        return exemptLateCharges;
    }

    public Double getExemptLateDepartureCharges() {
        return exemptLateDepartureCharges;
    }

    public Double getExemptParkingCharges() {
        return exemptParkingCharges;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public String getFlightId() {
        return flightId;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public String getFlightRules() {
        return flightRules;
    }

    public String getFlightType() {
        return flightType;
    }

    public Double getFplCrossingCost() {
        return fplCrossingCost;
    }

    public Double getFplCrossingDistance() {
        return fplCrossingDistance;
    }

    public String getFplRoute() {
        return fplRoute;
    }

    public Integer getId() {
        return id;
    }

    public String getInitialFplData() {
        return initialFplData;
    }

    public Double getInternationalPassengerCharges() {
        return internationalPassengerCharges;
    }

    public String getItem18Dep() {
        return item18Dep;
    }

    public String getItem18Dest() {
        return item18Dest;
    }

    public String getItem18RegNum() {
        return item18RegNum;
    }

    public String getItem18Rmk() {
        return item18Rmk;
    }

    public String getItem18Status() {
        return item18Status;
    }

    public String getItem18AircraftType() {
        return  item18AircraftType;
    }

    public String getItem18Operator() {
        return item18Operator;
    }

    public Double getLateArrivalCharges() {
        return lateArrivalCharges;
    }

    public Double getLateCharges() {
        return lateCharges;
    }

    public Double getLateDepartureCharges() {
        return lateDepartureCharges;
    }

    public String getManuallyChangedFields() {
        return manuallyChangedFields;
    }

    public String getMovementType() {
        return movementType;
    }

    public Double getNominalCrossingCost() {
        return nominalCrossingCost;
    }

    public Double getNominalCrossingDistance() {
        return nominalCrossingDistance;
    }

    public String getOtherChargesStatus() {
        return otherChargesStatus;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public Integer getOtherInvoiceId() {
        return otherInvoiceId;
    }

    public Double getParkingCharges() {
        return parkingCharges;
    }

    public Double getParkingTime() {
        return parkingTime;
    }

    public String getPassengerChargesStatus() {
        return passengerChargesStatus;
    }

    public Integer getPassengerInvoiceId() {
        return passengerInvoiceId;
    }

    public Integer getPassengersChargeableDomestic() {
        return passengersChargeableDomestic;
    }

    public Integer getPassengersChargeableIntern() {
        return passengersChargeableIntern;
    }

    public Integer getPassengersChild() {
        return passengersChild;
    }

    public Integer getPassengersJoiningAdult() {
        return passengersJoiningAdult;
    }

    public Integer getPassengersTransitAdult() {
        return passengersTransitAdult;
    }

    public Double getPrepaidAmount() {
        return prepaidAmount;
    }

    public Double getRadarCrossingCost() {
        return radarCrossingCost;
    }

    public Double getRadarCrossingDistance() {
        return radarCrossingDistance;
    }

    public String getRadarRouteText() {
        return radarRouteText;
    }

    public void setRadarRouteText(String radarRouteText) {
        this.radarRouteText = radarRouteText;
    }

    public Set<FlightMovementValidatorIssue> getResolutionErrorsSet() {
        return resolutionErrorsSet;
    }

    public List<RouteSegmentViewModel> getRouteSegments() {
        return routeSegments;
    }

    public String getSource() {
        return source;
    }

    public Long getSpatiaFplObjectId() {
        return spatiaFplObjectId;
    }

    public String getStatus() {
        return status;
    }

    public Double getTotalCharges() {
        return totalCharges;
    }

    public Double getTowerCrossingDistance() {
        return towerCrossingDistance;
    }

    public Double getTowerCrossingDistanceCost() {
        return towerCrossingDistanceCost;
    }

    public Double getUserCrossingDistance() {
        return userCrossingDistance;
    }

    public Double getUserCrossingDistanceCost() {
        return userCrossingDistanceCost;
    }

    public String getWakeTurb() {
        return wakeTurb;
    }

    public Double getTaspCharge() { return taspCharge; }

    public Boolean getAdhocChargeRequired() {
        return adhocChargeRequired;
    }

    public InvoicePermit getInvoicePermit() {
        return invoicePermit != null ? invoicePermit : new InvoicePermit();
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void setActualDepartureTime(String actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public void setActualMtow(Double actualMtow) {
        this.actualMtow = actualMtow;
    }

    public void setAerodromeCharges(Double aerodromeCharges) {
        this.aerodromeCharges = aerodromeCharges;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public void setApproachCharges(Double approachCharges) {
        this.approachCharges = approachCharges;
    }

    public void setArrivalAd(String arrivalAd) {
        this.arrivalAd = arrivalAd;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public void setAssociatedAccount(String associatedAccount) {
        this.associatedAccount = associatedAccount;
    }

    public void setAssociatedAccountBlackListedIndicator(Boolean associatedAccountBlackListedIndicator) {
        this.associatedAccountBlackListedIndicator = associatedAccountBlackListedIndicator;
    }

    public void setAssociatedAccountBlackListedOverride(Boolean associatedAccountBlackListedOverride) {
        this.associatedAccountBlackListedOverride = associatedAccountBlackListedOverride;
    }

    public void setAssociatedAccountID(Integer associatedAccountID) {
        this.associatedAccountID = associatedAccountID;
    }

    public void setAssociatedAccountName(String associatedAccountName) {
        this.associatedAccountName = associatedAccountName;
    }

    public void setAssociatedAccountIataMember(Boolean iataMember) {
        this.associatedAccountIataMember = iataMember;
    }

    public void setAssociatedAircraft(String associatedAircraft) {
        this.associatedAircraft = associatedAircraft;
    }

    public void setAtcCrossingDistance(Double atcCrossingDistance) {
        this.atcCrossingDistance = atcCrossingDistance;
    }

    public void setAtcCrossingDistanceCost(Double atcCrossingDistanceCost) {
        this.atcCrossingDistanceCost = atcCrossingDistanceCost;
    }

    public void setAverageMassFactor(Double averageMassFactor) {
        this.averageMassFactor = averageMassFactor;
    }

    public void setBillableEntryPoint(String billableEntryPoint) {
        this.billableEntryPoint = billableEntryPoint;
    }

    public void setBillableExitPoint(String billableExitPoint) {
        this.billableExitPoint = billableExitPoint;
    }

    public void setBillableRoute(Geometry billableRoute) {
        this.billableRoute = billableRoute;
    }

    public void setCrewMembers(Integer crewMembers) {
        this.crewMembers = crewMembers;
    }

    public void setCruisingSpeedOrMachNumber(String cruisingSpeedOrMachNumber) {
        this.cruisingSpeedOrMachNumber = cruisingSpeedOrMachNumber;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public void setBillingDate(LocalDateTime billingDate) { this.billingDate = billingDate; }

    public void setDeltaFlight(Boolean deltaFlight) {
        this.deltaFlight = deltaFlight;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public void setDomesticPassengerCharges(Double domesticPassengerCharges) {
        this.domesticPassengerCharges = domesticPassengerCharges;
    }

    public void setEnrouteCharges(Double enrouteCharges) {
        this.enrouteCharges = enrouteCharges;
    }

    public void setEnrouteChargesBasis(String enrouteChargesBasis) {
        this.enrouteChargesBasis = enrouteChargesBasis;
    }

    public void setEnrouteChargesStatus(String enrouteChargesStatus) {
        this.enrouteChargesStatus = enrouteChargesStatus;
    }

    public void setEnrouteInvoiceId(Integer enrouteInvoiceId) {
        this.enrouteInvoiceId = enrouteInvoiceId;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public void setEstimatedElapsedTime(String estimatedElapsedTime) {
        this.estimatedElapsedTime = estimatedElapsedTime;
    }

    public void setExemptAerodromeCharges(Double exemptAerodromeCharges) {
        this.exemptAerodromeCharges = exemptAerodromeCharges;
    }

    public void setExemptApprochCharges(Double exemptApprochCharges) {
        this.exemptApprochCharges = exemptApprochCharges;
    }

    public void setExemptDepCharges(Double exemptDepCharges) {
        this.exemptDepCharges = exemptDepCharges;
    }

    public void setExemptDomesticPassengerCharges(Double exemptDomesticPassengerCharges) {
        this.exemptDomesticPassengerCharges = exemptDomesticPassengerCharges;
    }

    public void setExemptEnrouteCharges(Double exemptEnrouteCharges) {
        this.exemptEnrouteCharges = exemptEnrouteCharges;
    }

    public void setExemptInternationalPassengerCharges(Double exemptInternationalPassengerCharges) {
        this.exemptInternationalPassengerCharges = exemptInternationalPassengerCharges;
    }

    public void setExemptLateArrivalCharges(Double exemptLateArrivalCharges) {
        this.exemptLateArrivalCharges = exemptLateArrivalCharges;
    }

    public void setExemptLateCharges(Double exemptLateCharges) {
        this.exemptLateCharges = exemptLateCharges;
    }

    public void setExemptLateDepartureCharges(Double exemptLateDepartureCharges) {
        this.exemptLateDepartureCharges = exemptLateDepartureCharges;
    }

    public void setExemptParkingCharges(Double exemptParkingCharges) {
        this.exemptParkingCharges = exemptParkingCharges;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public void setFlightLevel(String aFlightLevel) {
        flightLevel = aFlightLevel;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    public void setFlightRules(String flightRules) {
        this.flightRules = flightRules;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public void setFplCrossingCost(Double fplCrossingCost) {
        this.fplCrossingCost = fplCrossingCost;
    }

    public void setFplCrossingDistance(Double fplCrossingDistance) {
        this.fplCrossingDistance = fplCrossingDistance;
    }

    public void setFplRoute(String fplRoute) {
        this.fplRoute = fplRoute;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setInitialFplData(String initialFplData) {
        this.initialFplData = initialFplData;
    }

    public void setInternationalPassengerCharges(Double internationalPassengerCharges) {
        this.internationalPassengerCharges = internationalPassengerCharges;
    }

    public void setItem18Dep(String item18Dep) {
        this.item18Dep = item18Dep;
    }

    public void setItem18Dest(String item18Dest) {
        this.item18Dest = item18Dest;
    }

    public void setItem18RegNum(String item18RegNum) {
        this.item18RegNum = item18RegNum;
    }

    public void setItem18Rmk(String item18Rmk) {
        this.item18Rmk = item18Rmk;
    }

    public void setItem18Status(String item18Status) {
        this.item18Status = item18Status;
    }

    public void setItem18AircraftType(String item18AircraftType) {
        this.item18AircraftType = item18AircraftType;
    }

    public void setItem18Operator (String item18Operator) {
        this.item18Operator = item18Operator;
    }

    public void setLateArrivalCharges(Double lateArrivalCharges) {
        this.lateArrivalCharges = lateArrivalCharges;
    }

    public void setLateCharges(Double lateCharges) {
        this.lateCharges = lateCharges;
    }

    public void setLateDepartureCharges(Double lateDepartureCharges) {
        this.lateDepartureCharges = lateDepartureCharges;
    }

    public void setManuallyChangedFields(String manuallyChangedFields) {
        this.manuallyChangedFields = manuallyChangedFields;
    }

    public void setMovementType(String movementType) {
        this.movementType = movementType;
    }

    public void setNominalCrossingCost(Double nominalCrossingCost) {
        this.nominalCrossingCost = nominalCrossingCost;
    }

    public void setNominalCrossingDistance(Double nominalCrossingDistance) {
        this.nominalCrossingDistance = nominalCrossingDistance;
    }

    public void setOtherChargesStatus(String otherChargesStatus) {
        this.otherChargesStatus = otherChargesStatus;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public void setOtherInvoiceId(Integer otherInvoiceId) {
        this.otherInvoiceId = otherInvoiceId;
    }

    public void setParkingCharges(Double parkingCharges) {
        this.parkingCharges = parkingCharges;
    }

    public void setParkingTime(Double parkingTime) {
        this.parkingTime = parkingTime;
    }

    public void setPassengerChargesStatus(String passengerChargesStatus) {
        this.passengerChargesStatus = passengerChargesStatus;
    }

    public void setPassengerInvoiceId(Integer passengerInvoiceId) {
        this.passengerInvoiceId = passengerInvoiceId;
    }

    public void setPassengersChargeableDomestic(Integer passengersChargeableDomestic) {
        this.passengersChargeableDomestic = passengersChargeableDomestic;
    }

    public void setPassengersChargeableIntern(Integer passengersChargeableIntern) {
        this.passengersChargeableIntern = passengersChargeableIntern;
    }

    public void setPassengersChild(Integer passengersJoiningChild) {
        this.passengersChild = passengersJoiningChild;
    }

    public void setPassengersJoiningAdult(Integer passengersJoiningAdult) {
        this.passengersJoiningAdult = passengersJoiningAdult;
    }

    public void setPassengersTransitAdult(Integer passengersTransitAdult) {
        this.passengersTransitAdult = passengersTransitAdult;
    }

    public void setPrepaidAmount(Double prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public void setRadarCrossingCost(Double radarCrossingCost) {
        this.radarCrossingCost = radarCrossingCost;
    }

    public void setRadarCrossingDistance(Double radarCrossingDistance) {
        this.radarCrossingDistance = radarCrossingDistance;
    }

    public void setResolutionErrorsSet(Set<FlightMovementValidatorIssue> resolutionErrorsSet) {
        this.resolutionErrorsSet = resolutionErrorsSet;
    }

    public void setRouteSegments(List<RouteSegmentViewModel> routeSegments) {
        this.routeSegments = routeSegments;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setSpatiaFplObjectId(Long spatiaFplObjectId) {
        this.spatiaFplObjectId = spatiaFplObjectId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalCharges(Double totalCharges) {
        this.totalCharges = totalCharges;
    }

    public void setTowerCrossingDistance(Double towerCrossingDistance) {
        this.towerCrossingDistance = towerCrossingDistance;
    }

    public void setTowerCrossingDistanceCost(Double towerCrossingDistanceCost) {
        this.towerCrossingDistanceCost = towerCrossingDistanceCost;
    }

    public void setUserCrossingDistance(Double userCrossingDistance) {
        this.userCrossingDistance = userCrossingDistance;
    }

    public void setUserCrossingDistanceCost(Double userCrossingDistanceCost) {
        this.userCrossingDistanceCost = userCrossingDistanceCost;
    }

    public void setWakeTurb(String wakeTurb) {
        this.wakeTurb = wakeTurb;
    }

    public void setTaspCharge(Double taspCharge) {
        this.taspCharge = taspCharge;
    }

    public Double getDFactor() {
        return dFactor;
    }

    public void setDFactor(Double aDFactor) {
        dFactor = aDFactor;
    }

    public Double getWFactor() {
        return wFactor;
    }

    public void setWFactor(Double aWFactor) {
        wFactor = aWFactor;
    }

    public boolean isMarkedAsDuplicate() {
        return markedAsDuplicate;
    }

    public void setMarkedAsDuplicate(boolean markedAsDuplicate) {
        this.markedAsDuplicate = markedAsDuplicate;
    }

    public boolean isMarkedAsMissingBeforeThis() {
        return markedAsMissingBeforeThis;
    }

    public void setMarkedAsMissingBeforeThis(boolean markedAsMissingBeforeThis) {
        this.markedAsMissingBeforeThis = markedAsMissingBeforeThis;
    }

    public boolean isMarkedAsFirstMissing() {
        return markedAsFirstMissing;
    }

    public void setMarkedAsFirstMissing(boolean markedAsFirstMissing) {
        this.markedAsFirstMissing = markedAsFirstMissing;
    }

    public boolean isMarkedAsLastMissing() {
        return markedAsLastMissing;
    }

    public void setMarkedAsLastMissing(boolean markedAsLastMissing) {
        this.markedAsLastMissing = markedAsLastMissing;
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

    public void setAdhocChargeRequired(Boolean adhocChargeRequired) {
        // adhocChargeRequired is a virtual attribute that may not always be set
        if (adhocChargeRequired != null) {
            this.adhocChargeRequired = adhocChargeRequired;
        }
    }

    public void setInvoicePermit(InvoicePermit invoicePermit) {
        this.invoicePermit = invoicePermit;
    }

    public String getFlightCategoryScope() {
		return flightCategoryScope;
	}

	public void setFlightCategoryScope(String flightCategoryScope) {
		this.flightCategoryScope = flightCategoryScope;
	}

	public String getFlightCategoryNationality() {
		return flightCategoryNationality;
	}

	public void setFlightCategoryNationality(String flightCategoryNationality) {
		this.flightCategoryNationality = flightCategoryNationality;
	}

	public String getFlightCategoryType() {
		return flightCategoryType;
	}

	public void setFlightCategoryType(String flightCategoryType) {
		this.flightCategoryType = flightCategoryType;
	}


	public String getFlightmovementCategoryName() {
		return flightmovementCategoryName;
	}

	public void setFlightmovementCategoryName(String flightmovementCategoryName) {
		this.flightmovementCategoryName = flightmovementCategoryName;
	}

    public void setEnrouteResultCurrency(Currency enrouteResultCurrency) {
        this.enrouteResultCurrency = enrouteResultCurrency;
    }

    public void setEnrouteInvoiceCurrency(Currency enrouteInvoiceCurrency) {
        this.enrouteInvoiceCurrency = enrouteInvoiceCurrency;
    }

    public Integer getArrivingPaxDomesticAirport() {
        return arrivingPaxDomesticAirport;
    }

    public void setArrivingPaxDomesticAirport(Integer arrivingPaxDomesticAirport) {
        this.arrivingPaxDomesticAirport = arrivingPaxDomesticAirport;
    }

    public Integer getLandingPaxDomesticAirport() {
        return landingPaxDomesticAirport;
    }

    public void setLandingPaxDomesticAirport(Integer landingPaxDomesticAirport) {
        this.landingPaxDomesticAirport = landingPaxDomesticAirport;
    }

    public Integer getTransferPaxDomesticAirport() {
        return transferPaxDomesticAirport;
    }

    public void setTransferPaxDomesticAirport(Integer transferPaxDomesticAirport) {
        this.transferPaxDomesticAirport = transferPaxDomesticAirport;
    }

    public Integer getDepartingPaxDomesticAirport() {
        return departingPaxDomesticAirport;
    }

    public void setDepartingPaxDomesticAirport(Integer departingPaxDomesticAirport) {
        this.departingPaxDomesticAirport = departingPaxDomesticAirport;
    }

    public Integer getArrivingChildDomesticAirport() {
        return arrivingChildDomesticAirport;
    }

    public void setArrivingChildDomesticAirport(Integer arrivingChildDomesticAirport) {
        this.arrivingChildDomesticAirport = arrivingChildDomesticAirport;
    }

    public Integer getLandingChildDomesticAirport() {
        return landingChildDomesticAirport;
    }

    public void setLandingChildDomesticAirport(Integer landingChildDomesticAirport) {
        this.landingChildDomesticAirport = landingChildDomesticAirport;
    }

    public Integer getTransferChildDomesticAirport() {
        return transferChildDomesticAirport;
    }

    public void setTransferChildDomesticAirport(Integer transferChildDomesticAirport) {
        this.transferChildDomesticAirport = transferChildDomesticAirport;
    }

    public Integer getDepartingChildDomesticAirport() {
        return departingChildDomesticAirport;
    }

    public void setDepartingChildDomesticAirport(Integer departingChildDomesticAirport) {
        this.departingChildDomesticAirport = departingChildDomesticAirport;
    }

    public Integer getExemptArrivingPaxDomesticAirport() {
        return exemptArrivingPaxDomesticAirport;
    }

    public void setExemptArrivingPaxDomesticAirport(Integer exemptArrivingPaxDomesticAirport) {
        this.exemptArrivingPaxDomesticAirport = exemptArrivingPaxDomesticAirport;
    }

    public Integer getExemptLandingPaxDomesticAirport() {
        return exemptLandingPaxDomesticAirport;
    }

    public void setExemptLandingPaxDomesticAirport(Integer exemptLandingPaxDomesticAirport) {
        this.exemptLandingPaxDomesticAirport = exemptLandingPaxDomesticAirport;
    }

    public Integer getExemptTransferPaxDomesticAirport() {
        return exemptTransferPaxDomesticAirport;
    }

    public void setExemptTransferPaxDomesticAirport(Integer exemptTransferPaxDomesticAirport) {
        this.exemptTransferPaxDomesticAirport = exemptTransferPaxDomesticAirport;
    }

    public Integer getExemptDepartingPaxDomesticAirport() {
        return exemptDepartingPaxDomesticAirport;
    }

    public void setExemptDepartingPaxDomesticAirport(Integer exemptDepartingPaxDomesticAirport) {
        this.exemptDepartingPaxDomesticAirport = exemptDepartingPaxDomesticAirport;
    }

    public Double getLoadedGoods() {
        return loadedGoods;
    }

    public void setLoadedGoods(Double loadedGoods) {
        this.loadedGoods = loadedGoods;
    }

    public Double getDischargedGoods() {
        return dischargedGoods;
    }

    public void setDischargedGoods(Double dischargedGoods) {
        this.dischargedGoods = dischargedGoods;
    }

    public Double getLoadedMail() {
        return loadedMail;
    }

    public void setLoadedMail(Double loadedMail) {
        this.loadedMail = loadedMail;
    }

    public Double getDischargedMail() {
        return dischargedMail;
    }

    public void setDischargedMail(Double dischargedMail) {
        this.dischargedMail = dischargedMail;
    }

    public Currency getAerodromeChargesCurrency() {
		return aerodromeChargesCurrency;
	}

	public void setAerodromeChargesCurrency(Currency aerodromeChargesCurrency) {
		this.aerodromeChargesCurrency = aerodromeChargesCurrency;
	}

	public Currency getApproachChargesCurrency() {
		return approachChargesCurrency;
	}

	public void setApproachChargesCurrency(Currency approachChargesCurrency) {
		this.approachChargesCurrency = approachChargesCurrency;
	}

	public Currency getLateArrivalDepartureChargesCurrency() {
		return lateArrivalDepartureChargesCurrency;
	}

	public void setLateArrivalDepartureChargesCurrency(Currency lateArrivalDepartureChargesCurrency) {
		this.lateArrivalDepartureChargesCurrency = lateArrivalDepartureChargesCurrency;
	}

    public Boolean getThruFlight() {
        return thruFlight;
    }

    public void setThruFlight(Boolean thruFlight) {
        this.thruFlight = thruFlight;
    }

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    public Double getBillableCrossingDist() {
        return billableCrossingDist;
    }

    public void setBillableCrossingDist(Double billableCrossingDist) {
        this.billableCrossingDist = billableCrossingDist;
    }

    public Double getExtendedHoursSurcharge() {
        return extendedHoursSurcharge;
    }

    public void setExtendedHoursSurcharge(Double extendedHoursSurcharge) {
        this.extendedHoursSurcharge = extendedHoursSurcharge;
    }

    public Currency getExtendedHoursSurchargeCurrency() {
        return extendedHoursSurchargeCurrency;
    }

    public void setExtendedHoursSurchargeCurrency(Currency extendedHoursSurchargeCurrency) {
        this.extendedHoursSurchargeCurrency = extendedHoursSurchargeCurrency;
    }

    public Double getExemptExtendedHoursSurcharge() {
        return exemptExtendedHoursSurcharge;
    }

    public void setExemptExtendedHoursSurcharge(Double exemptExtendedHoursSurcharge) {
        this.exemptExtendedHoursSurcharge = exemptExtendedHoursSurcharge;
    }

    public Currency getTaspChargeCurrency() {
        return taspChargeCurrency;
    }

    public void setTaspChargeCurrency(Currency taspChargeCurrency) {
        this.taspChargeCurrency = taspChargeCurrency;
    }

    public Double getEnrouteCostToMinimum() {
        return enrouteCostToMinimum;
    }

    public void setEnrouteCostToMinimum(Double enrouteCostToMinimum) {
        this.enrouteCostToMinimum = enrouteCostToMinimum;
    }

    public Double getTotalChargesUsd() {
        return totalChargesUsd;
    }

    public void setTotalChargesUsd(Double totalChargesUsd) {
        this.totalChargesUsd = totalChargesUsd;
    }

    @Override
    public String toString() {
        return "FlightMovementViewModel{" +
                "id=" + id +
                ", depAd='" + depAd + '\'' +
                ", depTime='" + depTime + '\'' +
                ", destAd='" + destAd + '\'' +
                ", flightId='" + flightId + '\'' +
                ", movementType='" + movementType + '\'' +
                ", status='" + status + '\'' +
                ", enrouteResultCurrency=" + enrouteResultCurrency +
                ", enrouteInvoiceCurrency=" + enrouteInvoiceCurrency +
                '}';
    }
}
