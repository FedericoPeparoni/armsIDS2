package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.db.SearchableEntity;
import ca.ids.abms.config.db.SearchableText;
import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.common.mappers.Time4Digits;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.flightmovements.category.FlightmovementCategory;
import ca.ids.abms.modules.flightmovements.enumerate.*;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.util.models.VersionedAuditedEntity;
import ca.ids.abms.modules.util.models.annotations.MergeOnNull;
import ca.ids.abms.util.EnumUtils;
import ca.ids.abms.util.StringUtils;
import ca.ids.abms.util.converter.MultimapConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.vividsolutions.jts.geom.Geometry;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * FlightMovement Entity class for the flight_movement database table.
 */
@Entity
@Table(name = "flight_movements")
@UniqueKey(columnNames = { "flightId", "dateOfFlight", "depTime", "depAd" })
public class FlightMovement extends VersionedAuditedEntity {

    private static final long serialVersionUID = 1778891123834564479L;

    /**
     * Must match the class annotation {@code UniqueKey.columnNames} value. Cannot use this variable
     * directly in {@code @UniqueKey(columnNames = { ... })} as that requiers a constant field.
     */
    static final String[] UNIQUE_KEY_COLUMN_NAMES = { "flightId", "dateOfFlight", "depTime", "depAd" };

    @Id
    @Column(name = "id", unique = true, nullable = false, precision = 10)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "actual_departure_time", length = 4)
    @Time4Digits
    private String actualDepartureTime;

    @Column(name = "actual_mtow")
    private Double actualMtow;

    @SearchableText
    @Column(name = "aircraft_type", length = 4)
    private String aircraftType;

    @Column(name = "arrival_ad")
    private String arrivalAd;

    @Column(name = "arrival_time", length = 4)
    @Time4Digits
    private String arrivalTime;

    @ManyToOne
    @JoinColumn(name = "account")
    @SearchableEntity
    private Account account;

    @Column(name = "associated_aircraft", length = 20)
    private String associatedAircraft;

    @Column(name = "average_mass_factor")
    private Double averageMassFactor;

    @Column(name = "billable_route")
    @JsonIgnore
    private Geometry billableRoute;

    @Column(name = "crew_members")
    private Integer crewMembers;

    @NotNull
    @Column(name = "date_of_flight")
    private LocalDateTime dateOfFlight;

    @Column(name = "billing_date")
    private LocalDateTime billingDate;

    @SearchableText
    @NotNull
    @Column(name = "dep_ad", length = 50)
    private String depAd;

    @NotNull
    @Column(name = "dep_time", length = 4)
    @Time4Digits
    private String depTime;

    @SearchableText
    @NotNull
    @Column(name = "dest_ad", length = 50)
    private String destAd;

    @Column(name = "enroute_charges")
    @MergeOnNull
    private Double enrouteCharges;

    @Column(name="billable_entry_point", length = 20)
    private String billableEntryPoint;

    @Column(name = "entry_time")
    private LocalDateTime entryTime;

    @Column(name = "billable_exit_point", length = 20)
    private String billableExitPoint;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @SearchableText
    @NotNull
    @Column(name = "flight_id", length = 8)
    @Size(min = 1, max = 10)
    private String flightId;

    @Column(name = "flight_notes")
    private String flightNotes;

    @Column(name = "flight_type", length = 2)
    private String flightType;

    @Column(name = "fpl_crossing_cost")
    private Double fplCrossingCost;

    @Column(name = "fpl_crossing_distance")
    private Double fplCrossingDistance;

    @Column(name = "fpl_route", length = 2000)
    private String fplRoute;

    @Column(name = "fpl_route_geom")
    @JsonIgnore
    private Geometry fplRouteGeom;

    @Column(name = "initial_fpl_data", length = 4000)
    private String initialFplData;

    @Column(name = "item18_dep", length = 20)
    private String item18Dep;

    @Column(name = "item18_dest", length = 20)
    private String item18Dest;

    @SearchableText
    @Column(name = "item18_reg_num", length = 20)
    private String item18RegNum;

    @Column(name = "item18_status", length = 50)
    private String item18Status;

    @Column(name = "item18_aircraft_type", length = 50)
    private String item18AircraftType;

    @Column(name = "item18_operator", length = 50)
    private String item18Operator;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "movement_type", length = 30)
    private FlightMovementType movementType;

    @Column(name = "nominal_crossing_cost")
    private Double nominalCrossingCost;

    @Column(name = "nominal_crossing_distance")
    private Double nominalCrossingDistance;

    @Column(name = "other_info", length = 1800)
    private String otherInfo;

    @Column(name = "parking_charges")
    private Double parkingCharges;

    @Column(name = "passengers_chargeable_domestic")
    @MergeOnNull
    private Integer passengersChargeableDomestic;

    @Column(name = "passengers_chargeable_intern")
    @MergeOnNull
    private Integer passengersChargeableIntern;

    @Column(name = "passengers_joining_adult")
    private Integer passengersJoiningAdult;

    @Column(name = "passengers_child")
    @MergeOnNull
    private Integer passengersChild;

    @Column(name = "passengers_transit_adult")
    private Integer passengersTransitAdult;

    @Column(name = "prepaid_amount")
    private Double prepaidAmount;

    @Column(name = "radar_crossing_cost")
    private Double radarCrossingCost;

    @Column(name = "radar_crossing_distance")
    private Double radarCrossingDistance;

    @Column(name = "radar_route")
    @JsonIgnore
    private Geometry radarRoute;

    @Column(name = "spatia_fpl_object_id")
    private Long spatiaFplObjectId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private FlightMovementStatus status;

    @Column(name = "total_charges")
    private Double totalCharges;

    @Column(name = "total_charges_usd")
    private Double totalChargesUsd;

    @SearchableText
    @Column(name = "wake_turb", length = 1)
    private String wakeTurb;

    @Column(name = "item18_rmk")
    private String item18Rmk;

    @Column(name = "atc_log_track")
    @JsonIgnore
    private Geometry atcLogTrack;

    @Column(name = "tower_log_track")
    @JsonIgnore
    private Geometry towerLogTrack;

    @Column(name = "atc_crossing_distance")
    private Double atcCrossingDistance;

    @Column(name = "tower_crossing_distance")
    private Double towerCrossingDistance;

    @Column(name = "user_crossing_distance")
    @MergeOnNull
    private Double userCrossingDistance;

    @Column(name = "atc_crossing_distance_cost")
    private Double atcCrossingDistanceCost;

    @Column(name = "tower_crossing_distance_cost")
    private Double towerCrossingDistanceCost;

    @Column(name = "user_crossing_distance_cost")
    private Double userCrossingDistanceCost;

    @SearchableText
    @Column(name = "enroute_charges_basis", length = 30)
    @MergeOnNull
    private String enrouteChargesBasis;

    @Column(name = "late_charges")
    private Double lateCharges;

    @Column(name = "parking_time")
    @MergeOnNull
    private Double parkingTime;

    @Column(name = "domestic_passenger_charges")
    @MergeOnNull
    private Double domesticPassengerCharges;

    @Column(name = "international_passenger_charges")
    @MergeOnNull
    private Double internationalPassengerCharges;

    @Column(name = "exempt_enroute_charges")
    private Double exemptEnrouteCharges;

    @Column(name = "exempt_approch_charges")
    private Double exemptApprochCharges;

    @Column(name = "exempt_dep_charges")
    private Double exemptDepCharges;

    @Column(name = "exempt_late_charges")
    private Double exemptLateCharges;

    @Column(name = "exempt_parking_charges")
    private Double exemptParkingCharges;

    @Column(name = "exempt_domestic_passenger_charges")
    private Double exemptDomesticPassengerCharges;

    @Column(name = "exempt_international_passenger_charges")
    private Double exemptInternationalPassengerCharges;

    @Enumerated(EnumType.STRING)
    @Column(name = "enroute_charges_status", length = 20)
    private FlightMovementChargesStatus enrouteChargesStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "passenger_charges_status", length = 20)
    private FlightMovementChargesStatus passengerChargesStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "other_charges_status", length = 20)
    private FlightMovementChargesStatus otherChargesStatus;

    @Column(name = "enroute_invoice_id")
    private Integer enrouteInvoiceId;

    @Column(name = "passenger_invoice_id")
    private Integer passengerInvoiceId;

    @Column(name = "other_invoice_id")
    private Integer otherInvoiceId;

    @Column(name = "delta_flight")
    private Boolean deltaFlight;

    @Column(name = "resolution_errors")
    private String resolutionErrors;

    @Column(name = "source", length = 20)
    @Convert(converter = FlightMovementSourceConverter.class)
    private FlightMovementSource source;

    @Column(name = "manually_changed_fields")
    private String manuallyChangedFields;

    @Column(name = "radar_route_text")
    private String radarRouteText;

    @Column(name = "tower_log_route_text")
    private String radarTowerLogRouteText;

    @Column(name = "atc_log_route_text")
    private String atcLogRouteText;

    @Column(name = "estimated_elapsed_time")
    private String estimatedElapsedTime;

    @Column(name = "flight_level")
    private String flightLevel;

    @Column(name = "billable_crossing_dist")
    private Double billableCrossingDist;

    @Column(name = "billable_crossing_cost")
    private Double billableCrossingCost;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flightMovement", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<RouteSegment> routeSegments = new ArrayList<>();

    @Column(name = "tasp_charge")
    @MergeOnNull
    private Double taspCharge;

    @Column(name="approach_charges")
    private Double approachCharges;

    @Column(name="aerodrome_charges")
    private Double aerodromeCharges;

    @Column(name="late_arrival_charges")
    private Double lateArrivalCharges;

    @Column(name="late_departure_charges")
    private Double lateDepartureCharges;

    @Column(name="exempt_aerodrome_charges")
    private Double exemptAerodromeCharges;

    @Column(name="exempt_late_arrival_charges")
    private Double exemptLateArrivalCharges;

    @Column(name="exempt_late_departure_charges")
    private Double exemptLateDepartureCharges;

    @Column(name="delta_dest_ad")
    private String deltaDestAd;

    @Column(name="cruising_speed")
    private String cruisingSpeedOrMachNumber;

    @Column(name="flight_rules", length = 1)
    private String flightRules;

    @Column(name = "d_factor")
    private Double dFactor;

    @Column(name = "w_factor")
    private Double wFactor;

    @SearchableText
    private String actualDepAd;

    @SearchableText
    @Column(name = "actual_dest_ad")
    private String actualDestAd;

    /**
     * List of arrival charge at a specific percentage. This is used to determine which
     * stops have been charged the full amount.
     *
     * Persisted as delimiter separated values in a similar format: `IDNTA-100,IDNTB-100,IDNTA-50`
     *
     * Arrivals are separated by `FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_ENTRY_SEPERATOR`
     * well each identifier and percentage charged is separated by
     * `FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR`.
     */
    @Column(name = "arrival_charge_discounts")
    private String arrivalChargeDiscounts;

    @ManyToOne
    @JoinColumn(name = "billing_center_id")
    private BillingCenter billingCenter;

    @Column(name = "thru_flight")
    private Boolean thruFlight;

    private transient boolean isExemptFlightDistance = false;

    @Column(name="flight_category_scope", length = 2)
    @Convert(converter = FlightmovementCategoryScopeConverter.class)
    private FlightmovementCategoryScope flightCategoryScope;

    @Column(name="flight_category_nationality", length = 2)
    @Convert(converter = FlightmovementCategoryNationalityConverter.class)
    private FlightmovementCategoryNationality flightCategoryNationality;

    @Column(name="flight_category_type", length = 2)
    @Convert(converter = FlightmovementCategoryTypeConverter.class)
    private FlightmovementCategoryType flightCategoryType;

    @ManyToOne
    @JoinColumn(name = "enroute_result_currency_id")
    private Currency enrouteResultCurrency;

    @ManyToOne
    @JoinColumn(name = "enroute_invoice_currency_id")
    private Currency enrouteInvoiceCurrency;

    @ManyToOne
    @JoinColumn(name = "flight_category_id")
    private FlightmovementCategory flightmovementCategory;

    @Transient
    private boolean enrouteFormulaNotValid;

    @MergeOnNull
    private Integer arrivingPaxDomesticAirport;

    @MergeOnNull
    private Integer landingPaxDomesticAirport;

    @MergeOnNull
    private Integer transferPaxDomesticAirport;

    @MergeOnNull
    private Integer departingPaxDomesticAirport;

    @MergeOnNull
    private Integer arrivingChildDomesticAirport;

    @MergeOnNull
    private Integer landingChildDomesticAirport;

    @MergeOnNull
    private Integer transferChildDomesticAirport;

    @MergeOnNull
    private Integer departingChildDomesticAirport;

    @MergeOnNull
    private Integer exemptArrivingPaxDomesticAirport;

    @MergeOnNull
    private Integer exemptLandingPaxDomesticAirport;

    @MergeOnNull
    private Integer exemptTransferPaxDomesticAirport;

    @MergeOnNull
    private Integer exemptDepartingPaxDomesticAirport;

    @MergeOnNull
    private Double loadedGoods;

    @MergeOnNull
    private Double dischargedGoods;

    @MergeOnNull
    private Double loadedMail;

    @MergeOnNull
    private Double dischargedMail;

    private String statusNotes;

    @ManyToOne
    @JoinColumn(name = "aerodrome_charges_currency_id")
    private Currency  aerodromeChargesCurrency;

    @ManyToOne
    @JoinColumn(name = "approach_charges_currency_id")
    private Currency approachChargesCurrency;

    @ManyToOne
    @JoinColumn(name = "late_arrival_departure_charges_currency_id")
    private Currency lateArrivalDepartureChargesCurrency;

    @Column(name ="crossing_distance_to_minimum")
    private Double crossingDistanceToMinimum;

    @Column(name ="enroute_cost_to_minimum")
    private Double enrouteCostToMinimum;

    @Column(name="extended_hours_surcharge")
    private Double extendedHoursSurcharge;

    @ManyToOne
    @JoinColumn(name = "extended_hours_surcharge_currency_id")
    private Currency extendedHoursSurchargeCurrency;

    @Column(name="exempt_extended_hours_surcharge")
    private Double exemptExtendedHoursSurcharge;

    @ManyToOne
    @JoinColumn(name = "tasp_charge_currency_id")
    private Currency taspChargeCurrency;

    /**
     * Used within enroute charge formula and is not persisted to the database layer. Must be full amount before
     * any discounts are applied. That is the reason we cannot simply use {@link FlightMovement#aerodromeCharges}.
     */
    @Transient
    private Double aerodromeChargesWithoutDiscount;

    /**
     * Used within enroute charge formula and is not persisted to the database layer. Must be full amount before
     * any discounts are applied. That is the reason we cannot simply use {@link FlightMovement#approachCharges}.
     */
    @Transient
    private Double approachChargesWithoutDiscount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getActualDepartureTime() {
        return actualDepartureTime;
    }

    public void setActualDepartureTime(String actualDepartureTime) {
        this.actualDepartureTime = actualDepartureTime;
    }

    public Double getActualMtow() {
        return actualMtow;
    }

    public void setActualMtow(Double actualMtow) {
        this.actualMtow = actualMtow;
    }

    public String getAircraftType() {
        return aircraftType;
    }

    public void setAircraftType(String aircraftType) {
        this.aircraftType = aircraftType;
    }

    public String getArrivalAd() {
        return arrivalAd;
    }

    public void setArrivalAd(String arrivalAd) {
        this.arrivalAd = arrivalAd;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getAssociatedAircraft() {
        return associatedAircraft;
    }

    public void setAssociatedAircraft(String associatedAircraft) {
        this.associatedAircraft = associatedAircraft;
    }

    public Double getAverageMassFactor() {
        return averageMassFactor;
    }

    public void setAverageMassFactor(Double averageMassFactor) {
        this.averageMassFactor = averageMassFactor;
    }

    public Geometry getBillableRoute() {
        return billableRoute;
    }

    public void setBillableRoute(Geometry billableRoute) {
        this.billableRoute = billableRoute;
    }

    public Integer getCrewMembers() {
        return crewMembers;
    }

    public void setCrewMembers(Integer crewMembers) {
        this.crewMembers = crewMembers;
    }

    public LocalDateTime getDateOfFlight() {
        return dateOfFlight;
    }

    public void setDateOfFlight(LocalDateTime dateOfFlight) {
        this.dateOfFlight = dateOfFlight;
    }

    public LocalDateTime getBillingDate() { return billingDate; }

    public void setBillingDate(LocalDateTime billingDate) {
        this.billingDate = billingDate;
    }

    public String getDepAd() {
        return depAd;
    }

    public void setDepAd(String depAd) {
        this.depAd = depAd;
    }

    public String getDepTime() {
        return depTime;
    }

    public void setDepTime(String depTime) {
        this.depTime = depTime;
    }

    public String getDestAd() {
        return destAd;
    }

    public void setDestAd(String destAd) {
        this.destAd = destAd;
    }

    public Double getEnrouteCharges() {
        return enrouteCharges;
    }

    public void setEnrouteCharges(Double enrouteCharges) {
        this.enrouteCharges = enrouteCharges;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
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

    public boolean isExemptFlightDistance() {
        return isExemptFlightDistance;
    }

    public void setExemptFlightDistance(boolean aIsExemptFlightDistance) {
        isExemptFlightDistance = aIsExemptFlightDistance;
    }

    @Transient
    public String getBillableRouteString() {
        StringJoiner routingForInvoice = new StringJoiner(", ");

        if (StringUtils.isNotBlank(billableEntryPoint)) { routingForInvoice.add(billableEntryPoint); }
        if (StringUtils.isNotBlank(billableExitPoint)) { routingForInvoice.add(billableExitPoint); }

        return routingForInvoice.toString();
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getFlightNotes() {
        return flightNotes;
    }

    public void setFlightNotes(String flightNotes) {
        this.flightNotes = flightNotes;
    }

    public String getFlightType() {
        return flightType;
    }

    public void setFlightType(String flightType) {
        this.flightType = flightType;
    }

    public Double getFplCrossingCost() {
        return fplCrossingCost;
    }

    public void setFplCrossingCost(Double fplCrossingCost) {
        this.fplCrossingCost = fplCrossingCost;
    }

    public Double getFplCrossingDistance() {
        return fplCrossingDistance;
    }

    public void setFplCrossingDistance(Double fplCrossingDistance) {
        this.fplCrossingDistance = fplCrossingDistance;
    }

    public String getFplRoute() {
        return fplRoute;
    }

    public void setFplRoute(String fplRoute) {
        this.fplRoute = fplRoute;
    }

    public Geometry getFplRouteGeom() {
        return fplRouteGeom;
    }

    public void setFplRouteGeom(Geometry fplRouteGeom) {
        this.fplRouteGeom = fplRouteGeom;
    }

    public String getInitialFplData() {
        return initialFplData;
    }

    public void setInitialFplData(String initialFplData) {
        this.initialFplData = initialFplData;
    }

    public String getItem18Dep() {
        return item18Dep;
    }

    public void setItem18Dep(String item18Dep) {
        this.item18Dep = item18Dep;
    }

    public String getItem18Dest() {
        return item18Dest;
    }

    public void setItem18Dest(String item18Dest) {
        this.item18Dest = item18Dest;
    }

    public String getItem18RegNum() {
        return item18RegNum;
    }

    public void setItem18RegNum(String item18RegNum) {
        this.item18RegNum = item18RegNum;
    }

    public String getItem18Status() {
        return item18Status;
    }

    public void setItem18Status(String item18Status) {
        if(item18Status != null && item18Status.length() > 50) {
            item18Status= item18Status.substring(0, 49);
        }
        this.item18Status = item18Status;
    }

    public String getItem18AircraftType() {
        return item18AircraftType;
    }

    public void setItem18AircraftType(String item18AircraftType) {
        if(item18AircraftType != null && item18AircraftType.length() > 50) {
            item18AircraftType= item18AircraftType.substring(0, 49);
        }
        this.item18AircraftType = item18AircraftType;
    }

    public String getItem18Operator() {
        return item18Operator;
    }

    public void setItem18Operator(String item18Operator) {
        if (item18Operator != null && item18Operator.length() > 50) {
            item18Operator = item18Operator.substring(0, 49);
        }
        this.item18Operator = item18Operator;
    }

    /**
     * @deprecated - use category type, scope, nationality instead
     */
    @Deprecated
    public FlightMovementType getMovementType() {
        return movementType;
    }

    @Deprecated
    public void setMovementType(FlightMovementType movementType) {
        this.movementType = movementType;
    }

    public Double getNominalCrossingCost() {
        return nominalCrossingCost;
    }

    public void setNominalCrossingCost(Double nominalCrossingCost) {
        this.nominalCrossingCost = nominalCrossingCost;
    }

    public Double getNominalCrossingDistance() {
        return nominalCrossingDistance;
    }

    public void setNominalCrossingDistance(Double nominalCrossingDistance) {
        this.nominalCrossingDistance = nominalCrossingDistance;
    }

    public String getOtherInfo() {
        return otherInfo;
    }

    public void setOtherInfo(String otherInfo) {
        this.otherInfo = otherInfo;
    }

    public Double getParkingCharges() {
        return parkingCharges;
    }

    public void setParkingCharges(Double parkingCharges) {
        this.parkingCharges = parkingCharges;
    }

    public Integer getPassengersChargeableDomestic() {
        return passengersChargeableDomestic;
    }

    public void setPassengersChargeableDomestic(Integer passengersChargeableDomestic) {
        this.passengersChargeableDomestic = passengersChargeableDomestic;
    }

    public Integer getPassengersChargeableIntern() {
        return passengersChargeableIntern;
    }

    public void setPassengersChargeableIntern(Integer passengersChargeableIntern) {
        this.passengersChargeableIntern = passengersChargeableIntern;
    }

    public Integer getPassengersJoiningAdult() {
        return passengersJoiningAdult;
    }

    public void setPassengersJoiningAdult(Integer passengersJoiningAdult) {
        this.passengersJoiningAdult = passengersJoiningAdult;
    }

    public Integer getPassengersChild() {
        return passengersChild;
    }

    public void setPassengersChild(Integer passengersChild) {
        this.passengersChild = passengersChild;
    }

    public Integer getPassengersTransitAdult() {
        return passengersTransitAdult;
    }

    public void setPassengersTransitAdult(Integer passengersTransitAdult) {
        this.passengersTransitAdult = passengersTransitAdult;
    }

    public Double getPrepaidAmount() {
        return prepaidAmount;
    }

    public void setPrepaidAmount(Double prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }

    public Double getRadarCrossingCost() {
        return radarCrossingCost;
    }

    public void setRadarCrossingCost(Double radarCrossingCost) {
        this.radarCrossingCost = radarCrossingCost;
    }

    public Double getRadarCrossingDistance() {
        return radarCrossingDistance;
    }

    public void setRadarCrossingDistance(Double radarCrossingDistance) {
        this.radarCrossingDistance = radarCrossingDistance;
    }

    public Geometry getRadarRoute() {
        return radarRoute;
    }

    public void setRadarRoute(Geometry radarRoute) {
        this.radarRoute = radarRoute;
    }

    public Long getSpatiaFplObjectId() {
        return spatiaFplObjectId;
    }

    public void setSpatiaFplObjectId(Long spatiaFplObjectId) {
        this.spatiaFplObjectId = spatiaFplObjectId;
    }

    public FlightMovementStatus getStatus() {
        return status;
    }

    public void setStatus(FlightMovementStatus status) {
        this.status = status;
    }

    public Double getTotalCharges() {
        return totalCharges;
    }

    public void setTotalCharges(Double totalCharges) {
        this.totalCharges = totalCharges;
    }

    public String getWakeTurb() {
        return wakeTurb;
    }

    public void setWakeTurb(String wakeTurb) {
        this.wakeTurb = wakeTurb;
    }


    public String getItem18Rmk() {
        return item18Rmk;
    }

    public void setItem18Rmk(String item18Rmk) {
        this.item18Rmk = item18Rmk;
    }

    public Geometry getAtcLogTrack() {
        return atcLogTrack;
    }

    public void setAtcLogTrack(Geometry atcLogTrack) {
        this.atcLogTrack = atcLogTrack;
    }

    public Geometry getTowerLogTrack() {
        return towerLogTrack;
    }

    public void setTowerLogTrack(Geometry towerLogTrack) {
        this.towerLogTrack = towerLogTrack;
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

    public Double getAtcCrossingDistanceCost() {
        return atcCrossingDistanceCost;
    }

    public void setAtcCrossingDistanceCost(Double atcCrossingDistanceCost) {
        this.atcCrossingDistanceCost = atcCrossingDistanceCost;
    }

    public Double getTowerCrossingDistanceCost() {
        return towerCrossingDistanceCost;
    }

    public void setTowerCrossingDistanceCost(Double towerCrossingDistanceCost) {
        this.towerCrossingDistanceCost = towerCrossingDistanceCost;
    }

    public Double getUserCrossingDistanceCost() {
        return userCrossingDistanceCost;
    }

    public void setUserCrossingDistanceCost(Double userCrossingDistanceCost) {
        this.userCrossingDistanceCost = userCrossingDistanceCost;
    }

    public String getEnrouteChargesBasis() {
        return enrouteChargesBasis;
    }

    public void setEnrouteChargesBasis(String enrouteChargesBasis) {
        this.enrouteChargesBasis = enrouteChargesBasis;
    }

    public Double getLateCharges() {
        return lateCharges;
    }

    public void setLateCharges(Double lateCharges) {
        this.lateCharges = lateCharges;
    }

    public Double getParkingTime() {
        return parkingTime;
    }

    public void setParkingTime(Double parkingTime) {
        this.parkingTime = parkingTime;
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

    public Double getExemptEnrouteCharges() {
        return exemptEnrouteCharges;
    }

    public void setExemptEnrouteCharges(Double exemptEnrouteCharges) {
        this.exemptEnrouteCharges = exemptEnrouteCharges;
    }

    public Double getExemptApprochCharges() {
        return exemptApprochCharges;
    }

    public void setExemptApprochCharges(Double exemptApprochCharges) {
        this.exemptApprochCharges = exemptApprochCharges;
    }

    public Double getExemptDepCharges() {
        return exemptDepCharges;
    }

    public void setExemptDepCharges(Double exemptDepCharges) {
        this.exemptDepCharges = exemptDepCharges;
    }

    public Double getExemptLateCharges() {
        return exemptLateCharges;
    }

    public void setExemptLateCharges(Double exemptLateCharges) {
        this.exemptLateCharges = exemptLateCharges;
    }

    public Double getExemptParkingCharges() {
        return exemptParkingCharges;
    }

    public void setExemptParkingCharges(Double exemptParkingCharges) {
        this.exemptParkingCharges = exemptParkingCharges;
    }

    public Double getExemptDomesticPassengerCharges() {
        return exemptDomesticPassengerCharges;
    }

    public void setExemptDomesticPassengerCharges(Double exemptDomesticPassengerCharges) {
        this.exemptDomesticPassengerCharges = exemptDomesticPassengerCharges;
    }

    public Double getExemptInternationalPassengerCharges() {
        return exemptInternationalPassengerCharges;
    }

    public void setExemptInternationalPassengerCharges(Double exemptInternationalPassengerCharges) {
        this.exemptInternationalPassengerCharges = exemptInternationalPassengerCharges;
    }

    public FlightMovementChargesStatus getEnrouteChargesStatus() {
        return enrouteChargesStatus;
    }

    public void setEnrouteChargesStatus(FlightMovementChargesStatus enrouteChargesStatus) {
        this.enrouteChargesStatus = enrouteChargesStatus;
    }

    public FlightMovementChargesStatus getPassengerChargesStatus() {
        return passengerChargesStatus;
    }

    public void setPassengerChargesStatus(FlightMovementChargesStatus passengerChargesStatus) {
        this.passengerChargesStatus = passengerChargesStatus;
    }

    public FlightMovementChargesStatus getOtherChargesStatus() {
        return otherChargesStatus;
    }

    public void setOtherChargesStatus(FlightMovementChargesStatus otherChargesStatus) {
        this.otherChargesStatus = otherChargesStatus;
    }

    public Integer getEnrouteInvoiceId() {
        return enrouteInvoiceId;
    }

    public void setEnrouteInvoiceId(Integer enrouteInvoiceId) {
        this.enrouteInvoiceId = enrouteInvoiceId;
    }

    public Integer getPassengerInvoiceId() {
        return passengerInvoiceId;
    }

    public void setPassengerInvoiceId(Integer passengerInvoiceId) {
        this.passengerInvoiceId = passengerInvoiceId;
    }

    public Integer getOtherInvoiceId() {
        return otherInvoiceId;
    }

    public void setOtherInvoiceId(Integer otherInvoiceId) {
        this.otherInvoiceId = otherInvoiceId;
    }

    public Boolean getDeltaFlight() {
        return deltaFlight;
    }

    public void setDeltaFlight(Boolean deltaFlight) {
        this.deltaFlight = deltaFlight;
    }

    public FlightMovementSource getSource() {

        return source;
    }

    public void setSource(FlightMovementSource source) {

        this.source = source;
    }

    public String getRadarRouteText() {
        return radarRouteText;
    }

    public void setRadarRouteText(String radarRouteText) {
        this.radarRouteText = radarRouteText;
    }

    public String getRadarTowerLogRouteText() {
        return radarTowerLogRouteText;
    }

    public void setRadarTowerLogRouteText(String radarTowerLogRouteText) {
        this.radarTowerLogRouteText = radarTowerLogRouteText;
    }

    public String getAtcLogRouteText() {
        return atcLogRouteText;
    }

    public void setAtcLogRouteText(String atcLogRouteText) {
        this.atcLogRouteText = atcLogRouteText;
    }

    public String getEstimatedElapsedTime() {
        return estimatedElapsedTime;
    }

    public void setEstimatedElapsedTime(String estimatedElapsedTime) {
        this.estimatedElapsedTime = estimatedElapsedTime;
    }

    public String getResolutionErrors() {
        /*
        only getter for this property is used
        we do not directly set resolutionErrors as String value.
        it should be set as EnumSet<FlightMovementValidatorIssue>
        */

        return resolutionErrors;
    }

    public void setResolutionErrors(String resolutionErrors) {
        this.resolutionErrors = resolutionErrors;
    }

    @Transient
    public Set<FlightMovementValidatorIssue> getResolutionErrorsSet() {
        return EnumUtils.convertStringToEnumSet(FlightMovementValidatorIssue.class, this.resolutionErrors);
    }

    public void setResolutionErrorsSet(Set<FlightMovementValidatorIssue> resolutionErrorsSet) {
        //convert to list of coma separated values OR set to null
        if (resolutionErrorsSet != null && !resolutionErrorsSet.isEmpty()) {
                this.resolutionErrors = EnumUtils.convertEnumSetToComaSeparatedString((EnumSet<FlightMovementValidatorIssue>) resolutionErrorsSet);

        } else {
                this.resolutionErrors = null;
        }
    }

    public String getManuallyChangedFields() {
        return manuallyChangedFields;
    }

    public void setManuallyChangedFields(String manuallyChangedFields) {
        this.manuallyChangedFields = manuallyChangedFields;
    }

    public String getFlightLevel() {
        return flightLevel;
    }

    public void setFlightLevel(String flightLevel) {
        this.flightLevel = flightLevel;
    }

    public List<RouteSegment> getRouteSegments() {
        return routeSegments;
    }

    public void setRouteSegments(List<RouteSegment> routeSegments) {
        this.routeSegments = routeSegments;
    }

    public Double getTaspCharge() {
        return taspCharge;
    }

    public void setTaspCharge(Double taspCharge) {
        this.taspCharge = taspCharge;
    }

    public Double getBillableCrossingDist() {
        return billableCrossingDist;
    }

    public void setBillableCrossingDist(Double billableCrossingDist) {
        this.billableCrossingDist = billableCrossingDist;
    }

    public Double getBillableCrossingCost() {
        return billableCrossingCost;
    }

    public void setBillableCrossingCost(Double billableCrossingCost) {
        this.billableCrossingCost = billableCrossingCost;
    }

    public Double getApproachCharges() {
        return approachCharges;
    }

    public void setApproachCharges(Double approachCharges) {
        this.approachCharges = approachCharges;
    }

    public Double getAerodromeCharges() {
        return aerodromeCharges;
    }

    public void setAerodromeCharges(Double aerodromeCharges) {
        this.aerodromeCharges = aerodromeCharges;
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

    public Double getExemptAerodromeCharges() {
        return exemptAerodromeCharges;
    }

    public void setExemptAerodromeCharges(Double exemptAerodromeCharges) {
        this.exemptAerodromeCharges = exemptAerodromeCharges;
    }

    public Double getExemptLateArrivalCharges() {
        return exemptLateArrivalCharges;
    }

    public void setExemptLateArrivalCharges(Double exemptLateArrivalCharges) {
        this.exemptLateArrivalCharges = exemptLateArrivalCharges;
    }

    public Double getExemptLateDepartureCharges() {
        return exemptLateDepartureCharges;
    }

    public void setExemptLateDepartureCharges(Double exemptLateDepartureCharges) {
        this.exemptLateDepartureCharges = exemptLateDepartureCharges;
    }

    public String getDeltaDestAd() {
        return deltaDestAd;
    }

    public void setDeltaDestAd(String deltaDestAd) {
        this.deltaDestAd = deltaDestAd;
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

    public String getActualDepAd() {
        return actualDepAd;
    }

    public String getActualDestAd() {
        return actualDestAd;
    }

    public void setActualDepAd(String actualDepAd) {
        this.actualDepAd = actualDepAd;
    }

    public void setActualDestAd(String actualDestAd) {
        this.actualDestAd = actualDestAd;
    }

    public Multimap<String, Integer> getArrivalChargeDiscounts() {
        if (arrivalChargeDiscounts != null)
            return MultimapConverter
                .split(
                    FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_ENTRY_SEPERATOR,
                    FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR)
                .apply(arrivalChargeDiscounts);
        else
            return ImmutableMultimap.of();
    }

    public void setArrivalChargeDiscounts(Multimap<String, Integer> arrivalChargeDiscounts) {
        if (arrivalChargeDiscounts != null)
            this.arrivalChargeDiscounts = MultimapConverter
                .join(
                    FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_ENTRY_SEPERATOR,
                    FlightMovementConstants.ARRIVAL_CHARGE_DISCOUNTS_VALUE_SEPERATOR)
                .apply(arrivalChargeDiscounts);
        else
            this.arrivalChargeDiscounts = null;
    }

    public boolean isEnrouteFormulaNotValid() {
        return enrouteFormulaNotValid;
    }

    public void setEnrouteFormulaNotValid(boolean enrouteFormulaNotValid) {
        this.enrouteFormulaNotValid = enrouteFormulaNotValid;
    }

    public Boolean getThruFlight() {
        return thruFlight;
    }

    public void setThruFlight(Boolean thruFlight) {
        this.thruFlight = thruFlight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof FlightMovement)) {
            return false;
        }
        final FlightMovement that = (FlightMovement) o;

        return id != null ? id.equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "FlightMovement{" +
            "id=" + id +
            ", spatiaFplObjectId=" + spatiaFplObjectId +
            ", flightId='" + flightId + '\'' +
            ", dateOfFlight='" + dateOfFlight + '\'' +
            ", depTime='" + depTime + '\'' +
            ", depAd='" + depAd + '\'' +
            ", destAd='" + destAd + '\'' +
            ", movementType='" + movementType + '\'' +
            ", status='" + status + '\'' +
            ", billingCenter='" + (billingCenter != null ? String.valueOf(billingCenter.getId()) : "none") + '\'' +
            '}';
    }

    public String getFlightName() {
        return String.format ("#%d[flightId=%s,dateOfFlight=%s]",
            getId(),
            getFlightId(),
            getDateOfFlight() == null ? "null" : getDateOfFlight().format (DateTimeFormatter.ISO_DATE));
    }

    public BillingCenter getBillingCenter() {
        return billingCenter;
    }

    public void setBillingCenter(BillingCenter billingCenter) {
        this.billingCenter = billingCenter;
    }

    public FlightmovementCategoryScope getFlightCategoryScope() {
        return flightCategoryScope;
    }

    public void setFlightCategoryScope(FlightmovementCategoryScope flightCategoryScope) {
        this.flightCategoryScope = flightCategoryScope;
    }

    public FlightmovementCategoryNationality getFlightCategoryNationality() {
        return flightCategoryNationality;
    }

    public void setFlightCategoryNationality(FlightmovementCategoryNationality flightCategoryNationality) {
        this.flightCategoryNationality = flightCategoryNationality;
    }

    public FlightmovementCategoryType getFlightCategoryType() {
        return flightCategoryType;
    }

    public void setFlightCategoryType(FlightmovementCategoryType flightCategoryType) {
        this.flightCategoryType = flightCategoryType;
    }

    public Currency getEnrouteResultCurrency() {
        return enrouteResultCurrency;
    }

    public void setEnrouteResultCurrency(Currency enrouteResultCurrency) {
        this.enrouteResultCurrency = enrouteResultCurrency;
    }

    public Currency getEnrouteInvoiceCurrency() {
        return enrouteInvoiceCurrency;
    }

    public void setEnrouteInvoiceCurrency(Currency enrouteInvoiceCurrency) {
        this.enrouteInvoiceCurrency = enrouteInvoiceCurrency;
    }

    public FlightmovementCategory getFlightmovementCategory() {
        return flightmovementCategory;
    }

    public void setFlightmovementCategory(FlightmovementCategory flightmovementCategory) {
        this.flightmovementCategory = flightmovementCategory;
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

    public String getStatusNotes() {
        return statusNotes;
    }

    public void setStatusNotes(String statusNotes) {
        this.statusNotes = statusNotes;
    }

    public Double getCrossingDistanceToMinimum() {
        return crossingDistanceToMinimum;
    }

    public void setCrossingDistanceToMinimum(Double crossingDistanceToMinimum) {
        this.crossingDistanceToMinimum = crossingDistanceToMinimum;
    }

    public Double getEnrouteCostToMinimum() {
        return enrouteCostToMinimum;
    }

    public void setEnrouteCostToMinimum(Double enrouteCostToMinimum) {
        this.enrouteCostToMinimum = enrouteCostToMinimum;
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

    public Double getAerodromeChargesWithoutDiscount() {
        return aerodromeChargesWithoutDiscount;
    }

    public void setAerodromeChargesWithoutDiscount(Double aerodromeChargesWithoutDiscount) {
        this.aerodromeChargesWithoutDiscount = aerodromeChargesWithoutDiscount;
    }

    public Double getApproachChargesWithoutDiscount() {
        return approachChargesWithoutDiscount;
    }

    public void setApproachChargesWithoutDiscount(Double approachChargesWithoutDiscount) {
        this.approachChargesWithoutDiscount = approachChargesWithoutDiscount;
    }

    public Currency getTaspChargeCurrency() {
        return taspChargeCurrency;
    }

    public void setTaspChargeCurrency(Currency taspChargeCurrency) {
        this.taspChargeCurrency = taspChargeCurrency;
    }

    public Double getTotalChargesUsd() {
        return totalChargesUsd;
    }

    public void setTotalChargesUsd(Double totalChargesUsd) {
        this.totalChargesUsd = totalChargesUsd;
    }

    /**
     * check if the flight movement is OTHER
     */
    public boolean isOTHER() {
        boolean result = true;
        if(this.getFlightmovementCategory() != null &&
            this.getFlightmovementCategory().getId() != 0 &&
            this.getFlightCategoryType() !=null &&
            !this.getFlightCategoryType().equals(FlightmovementCategoryType.OTHER)) {
            result = false;
        }
        return result;
    }

    @Transient
    public FlightMovementLogicalKey getLogicalKey() {
        return new FlightMovementLogicalKey (getFlightId(), getDepAd(), getDateOfFlight(), getDepTime());
    }

    @Transient
    public String getDisplayName() {
        return String.format ("FlightMovement {flightId=%s, dateOfFlight=%s, departureAerodrome=%s, arrivalAerodrome=%s, depTime=%s}",
            getFlightId(), dateOfFlight == null ? null :dateOfFlight.toLocalDate().toString(), this.getDepAd(),this.getArrivalAd(),this.getDepTime());
    }

    /**
     * If actual departure and/or destination has not been resolved during flight movement calculations
     * the default values are the departure and destination aerodromes
     */
    @Override
    @PrePersist
    public void onCreate() {
        super.onCreate();

        if (StringUtils.isBlank(actualDepAd))
           actualDepAd = depAd;

        if (StringUtils.isBlank(actualDestAd))
           actualDestAd = destAd;

        if (billingDate == null)
            billingDate = dateOfFlight;
        
        if(movementType == null)
            movementType = FlightMovementType.OTHER;
        
     }

    @Override
    @PreUpdate
    public void onPersist() {
        super.onPersist();

        if  (StringUtils.isBlank(actualDepAd))
            actualDepAd = depAd;

        if(StringUtils.isBlank(actualDestAd))
            actualDestAd = destAd;

        if (billingDate == null)
            billingDate = dateOfFlight;
        
        if(movementType == null)
            movementType = FlightMovementType.OTHER;

    }
}
