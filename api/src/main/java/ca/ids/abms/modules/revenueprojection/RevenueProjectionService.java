package ca.ids.abms.modules.revenueprojection;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ibm.icu.text.DecimalFormat;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.enumerate.CostFormulaVar;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ChargesUtility;
import ca.ids.abms.modules.formulas.FormulaEvaluator;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormula;
import ca.ids.abms.modules.formulas.navigation.NavigationBillingFormulaService;
import ca.ids.abms.modules.mtow.AverageMtowFactor;
import ca.ids.abms.modules.mtow.AverageMtowFactorService;

@Service
@Transactional
public class RevenueProjectionService {

    private final Logger log = LoggerFactory.getLogger(RevenueProjectionService.class);

    private final FlightMovementService flightMovementService;
    private final NavigationBillingFormulaService navigationBillingFormulaService;
    private final FormulaEvaluator formulaEvaluator;
    private AverageMtowFactorService averageMtowFactorService;
    private ChargesUtility chargesUtility;

    boolean modifiedOnly;

    final static String FALSE = "false";
    final static String TRUE = "true";

    public RevenueProjectionService(FlightMovementService flightMovementService, NavigationBillingFormulaService navigationBillingFormulaService,
        FormulaEvaluator formulaEvaluator, AverageMtowFactorService averageMtowFactorService, ChargesUtility chargesUtility) {
        this.flightMovementService = flightMovementService;
        this.navigationBillingFormulaService = navigationBillingFormulaService;
        this.formulaEvaluator = formulaEvaluator;
        this.averageMtowFactorService = averageMtowFactorService;
        this.chargesUtility = chargesUtility;
    }

    /*******************************************************************************************************************************************
     *
     *  ENTRY POINT FROM CONTROLLER
     *
     *******************************************************************************************************************************************/

    /**
     * Calculate revenue projection based on the provided formulas
     *
     * @param revenueProjection revenue projection formulas
     */
    public RevenueProjectionReport createRevenueProjection(RevenueProjection revenueProjection) {
        List<LocalDateTime> dates = getDateRange(revenueProjection.getTimePeriod());
        List<FlightMovement> flightMovements = flightMovementService.findAllByFlightTypeIntervalDate(dates.get(0), dates.get(1));

        modifiedOnly = revenueProjection.getModifiedOnly().equals("yes");

        log.debug("Request for flightMovements between {} and {}, Found={}", dates.get(0), dates.get(1), flightMovements.size());

        return this.calculateRevenueProjection(revenueProjection, flightMovements);
    }

    /*******************************************************************************************************************************************
     *
     *  HELPER FUNCTIONS
     *
     *******************************************************************************************************************************************/

    /**
     * Calculate start and end dates for the provided time period
     *
     * @param timePeriod flight movement
     */
    private List<LocalDateTime> getDateRange(String timePeriod) {
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime previous;

        switch (timePeriod) {
            case "MONTH":
                previous = today.minusDays(30);
                break;
            case "QUARTER":
                previous = today.minusMonths(3);
                break;
            case "YEAR":
                previous = today.minusYears(1);
                break;
            default:
            previous = today.minusYears(10);
        }

        return Arrays.asList(previous, today);
    }

    /**
     * Handle null pointer error for double
     *
     * @param number number
     */
    private Double testDoubleForNull(Double number) {
        return (number == null ? 0.0d : number);
    }

    /**
     * Handle null pointer error for integer
     *
     * @param number number
     */
    private Integer testIntegerForNull(Integer number) {
        return (number == null ? 0 : number);
    }

    /**
     * Convert number (2) to percentage (1.02) for calculation of percentage change
     *
     * @param percent provided percent as an integer (-100  to 100)
     */
    private Double convertToPercentage(Double percent) {
        return 1 + (percent / 100);
    }

    /**
     * Calculate the percentage change to apply to the charges
     *
     * @param newValue Double new value
     * @param oldValue Double old value
     */
    private Double calcPercentChange(Double newValue, Double oldValue) {
        if (oldValue != null && newValue != null && oldValue != 0) {
            if (newValue >= oldValue) {
                return ((newValue - oldValue) / oldValue) * 100;
            } else {
                return ((oldValue- newValue) / oldValue) * 100;
            }
        } else {
            return 0.0d;
        }
    }

    private boolean testCalculateEnroute(RevenueProjection revenueProjection) {
        boolean calc = true;

        if (revenueProjection.getDomesticFormula().isEmpty() && revenueProjection.getDomesticDFactorFormula().isEmpty() &&
                revenueProjection.getRegionalArrivalFormula().isEmpty() && revenueProjection.getRegArrDFactorFormula().isEmpty() &&
                revenueProjection.getRegionalDepartureFormula().isEmpty() && revenueProjection.getRegDepDFactorFormula().isEmpty() &&
                revenueProjection.getRegionalOverflightFormula().isEmpty() && revenueProjection.getRegOvrDFactorFormula().isEmpty() &&
                revenueProjection.getInternationalArrivalFormula().isEmpty() && revenueProjection.getIntArrDFactorFormula().isEmpty() &&
                revenueProjection.getInternationalDepartureFormula().isEmpty() && revenueProjection.getIntDepDFactorFormula().isEmpty() &&
                revenueProjection.getInternationalOverflightFormula().isEmpty() && revenueProjection.getIntOvrDFactorFormula().isEmpty() &&
                revenueProjection.getwFactorFormula().isEmpty()) {
            calc = false;
        }

        return calc;
    }

    /*******************************************************************************************************************************************
     *
     *  REVENUE CALCULATIONS CORE
     *
     *******************************************************************************************************************************************/

    /**
     * Calculate revenue projection based on the provided formulas
     *
     * @param revenueProjection revenue projection formulas
     * @param flightMovements list of flight movements
     */
    private RevenueProjectionReport calculateRevenueProjection(RevenueProjection revenueProjection, List<FlightMovement> flightMovements) {
        RevenueProjectionData revenueProjectionData = new RevenueProjectionData();
        revenueProjectionData.global = new RevenueProjectionData.Global();

        NavigationBillingFormula nbfOld = navigationBillingFormulaService.getFormulaByUpperLimit(revenueProjection.getUpperLimit());
        NavigationBillingFormula nbfNew = createNewEnrouteBillingFormula(revenueProjection, revenueProjectionData, nbfOld);
        double chargesAerodrome = 0.0d;
        double chargesApproach = 0.0d;
        double chargesLateArrival = 0.0d;
        double chargesLateDeparture = 0.0d;
        double chargesPassenger = 0.0d;
        double totalPassengers = 0.0d;

        // sum all required charges fields for current revenue values
        for (FlightMovement fm: flightMovements) {
            chargesAerodrome += testDoubleForNull(fm.getAerodromeCharges());
            chargesApproach += testDoubleForNull(fm.getApproachCharges());
            chargesLateArrival += testDoubleForNull(fm.getLateArrivalCharges());
            chargesLateDeparture += testDoubleForNull(fm.getLateDepartureCharges());
            chargesPassenger += testDoubleForNull(fm.getDomesticPassengerCharges()) + testDoubleForNull(fm.getInternationalPassengerCharges());

            totalPassengers += testIntegerForNull(fm.getPassengersChargeableDomestic()) + testIntegerForNull(fm.getPassengersChargeableIntern());
        }

        setPercentAerodromeCharges(revenueProjection, revenueProjectionData, chargesAerodrome);
        setPercentApproachCharges(revenueProjection, revenueProjectionData, chargesApproach);
        setPercentLateArrivalCharges(revenueProjection, revenueProjectionData, chargesLateArrival);
        setPercentLateDepartureCharges(revenueProjection, revenueProjectionData, chargesLateDeparture);
        setPercentPassengerCharges(revenueProjection, revenueProjectionData, chargesPassenger);

        setVolumeFlightCharges(revenueProjection, revenueProjectionData, flightMovements.size());
        setVolumePassengerCharges(revenueProjection, revenueProjectionData, totalPassengers);

        if (testCalculateEnroute(revenueProjection)) {
            calculateEnrouteCharges(flightMovements, nbfOld, nbfNew, revenueProjectionData);
        }

        setTotalRevenue(revenueProjectionData);

        return convertToReport(revenueProjectionData);
    }

    /*******************************************************************************************************************************************
     *
     *  REVENUE CALCULATIONS - PERCENTAGE CHANGE CALCULATIONS
     *
     *******************************************************************************************************************************************/

    private void setPercentAerodromeCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double chargesAerodrome) {
        double newChargesAerodrome;

        if (revenueProjection.getChargesAerodrome() != null && revenueProjection.getChargesAerodrome() != 0.0d) {
            newChargesAerodrome = chargesAerodrome * convertToPercentage(revenueProjection.getChargesAerodrome());

            revenueProjectionData.global.chargesAerodrome.set(2, revenueProjection.getChargesAerodrome());
            revenueProjectionData.global.chargesAerodrome.set(3, 1.0d);
        } else {
            newChargesAerodrome = chargesAerodrome;

            revenueProjectionData.global.chargesAerodrome.set(2, 0.0d);
            revenueProjectionData.global.chargesAerodrome.set(3, (modifiedOnly ? 0.0d :  1.0d));
        }

        revenueProjectionData.global.chargesAerodrome.set(0, chargesAerodrome);
        revenueProjectionData.global.chargesAerodrome.set(1, newChargesAerodrome);

        log.debug("setPercentAerodromeCharges {},", revenueProjectionData.global.chargesAerodrome);
    }

    private void setPercentApproachCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double chargesApproach) {
        double newChargesApproach;

        if (revenueProjection.getChargesApproach() != null && revenueProjection.getChargesApproach() != 0.0d) {
            newChargesApproach = chargesApproach * convertToPercentage(revenueProjection.getChargesApproach());

            revenueProjectionData.global.chargesApproach.set(2, revenueProjection.getChargesApproach());
            revenueProjectionData.global.chargesApproach.set(3, 1.0d);
        } else {
            newChargesApproach = chargesApproach;

            revenueProjectionData.global.chargesApproach.set(2, 0.0d);
            revenueProjectionData.global.chargesApproach.set(3, (modifiedOnly ? 0.0d :  1.0d));
        }

        revenueProjectionData.global.chargesApproach.set(0, chargesApproach);
        revenueProjectionData.global.chargesApproach.set(1, newChargesApproach);

        log.debug("setPercentApproachCharges {},", revenueProjectionData.global.chargesApproach);
    }

    private void setPercentLateArrivalCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double chargesLateArrival) {
        double newChargesLateArrival;

        if (revenueProjection.getChargesLateArrival() != null && revenueProjection.getChargesLateArrival() != 0.0d) {
            newChargesLateArrival = chargesLateArrival * convertToPercentage(revenueProjection.getChargesLateArrival());

            revenueProjectionData.global.chargesLateArrival.set(2, revenueProjection.getChargesLateArrival());
            revenueProjectionData.global.chargesLateArrival.set(3, 1.0d);
        } else {
            newChargesLateArrival = chargesLateArrival;

            revenueProjectionData.global.chargesLateArrival.set(2, 0.0d);
            revenueProjectionData.global.chargesLateArrival.set(3, (modifiedOnly ? 0.0d :  1.0d));
        }

        revenueProjectionData.global.chargesLateArrival.set(0, chargesLateArrival);
        revenueProjectionData.global.chargesLateArrival.set(1, newChargesLateArrival);

        log.debug("setPercentLateArrivalCharges {},", revenueProjectionData.global.chargesLateArrival);
    }

    private void setPercentLateDepartureCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double chargesLateDeparture) {
        double newChargesLateDeparture;

        if (revenueProjection.getChargesLateDeparture() != null && revenueProjection.getChargesLateDeparture() != 0.0d) {
            newChargesLateDeparture = chargesLateDeparture * convertToPercentage(revenueProjection.getChargesLateDeparture());

            revenueProjectionData.global.chargesLateDeparture.set(2, revenueProjection.getChargesLateDeparture());
            revenueProjectionData.global.chargesLateDeparture.set(3, 1.0d);
        } else {
            newChargesLateDeparture = chargesLateDeparture;

            revenueProjectionData.global.chargesLateDeparture.set(2, 0.0d);
            revenueProjectionData.global.chargesLateDeparture.set(3, (modifiedOnly ? 0.0d :  1.0d));
        }

        revenueProjectionData.global.chargesLateDeparture.set(0, chargesLateDeparture);
        revenueProjectionData.global.chargesLateDeparture.set(1, newChargesLateDeparture);

        log.debug("setPercentLateDepartureCharges {},", revenueProjectionData.global.chargesLateDeparture);
    }

    private void setPercentPassengerCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double chargesPassenger) {
        double newChargesPassenger;

        if (revenueProjection.getChargesPassenger() != null && revenueProjection.getChargesPassenger() != 0.0d) {
            newChargesPassenger = chargesPassenger * convertToPercentage(revenueProjection.getChargesPassenger());

            revenueProjectionData.global.chargesPassenger.set(2, revenueProjection.getChargesPassenger());
            revenueProjectionData.global.chargesPassenger.set(3, 1.0d);
        } else {
            newChargesPassenger = chargesPassenger;

            revenueProjectionData.global.chargesPassenger.set(2, 0.0d);
            revenueProjectionData.global.chargesPassenger.set(3, (modifiedOnly ? 0.0d :  1.0d));
        }

        revenueProjectionData.global.chargesPassenger.set(0, chargesPassenger);
        revenueProjectionData.global.chargesPassenger.set(1, newChargesPassenger);

        log.debug("setPercentPassengerCharges {},", revenueProjectionData.global.chargesPassenger);
    }

    /*******************************************************************************************************************************************
     *
     *  REVENUE CALCULATIONS - VOLUME CHANGE CALCULATIONS
     *  these calculations are done after increasing percentage to reflect changes to both percentage and volume of flights
     *  also sets new passenger and flight totals based on volume changes (or no change if not set)
     *
     *******************************************************************************************************************************************/

    private void setVolumeFlightCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double flights) {
        double newTotalFlights = flights;

        if (revenueProjection.getChargesVolFlights() != null && revenueProjection.getChargesVolFlights() != 0.0d) {
            newTotalFlights = flights * convertToPercentage(revenueProjection.getChargesVolFlights());

            double newChargesAerodrome = (revenueProjectionData.global.chargesAerodrome.get(1) / flights) * newTotalFlights;
            double newChargesApproach = (revenueProjectionData.global.chargesApproach.get(1) / flights) * newTotalFlights;
            double newChargesLateArrival = (revenueProjectionData.global.chargesLateArrival.get(1) / flights) * newTotalFlights;
            double newChargesLateDeparture = (revenueProjectionData.global.chargesLateDeparture.get(1) / flights) * newTotalFlights;

            revenueProjectionData.global.chargesAerodrome.set(2, calcPercentChange(newChargesAerodrome, revenueProjectionData.global.chargesAerodrome.get(0)));
            revenueProjectionData.global.chargesAerodrome.set(1, newChargesAerodrome);

            revenueProjectionData.global.chargesApproach.set(2, calcPercentChange(newChargesApproach, revenueProjectionData.global.chargesApproach.get(0)));
            revenueProjectionData.global.chargesApproach.set(1, newChargesApproach);

            revenueProjectionData.global.chargesLateArrival.set(2, calcPercentChange(newChargesLateArrival, revenueProjectionData.global.chargesLateArrival.get(0)));
            revenueProjectionData.global.chargesLateArrival.set(1, newChargesLateArrival);

            revenueProjectionData.global.chargesLateDeparture.set(2, calcPercentChange(newChargesLateDeparture, revenueProjectionData.global.chargesLateDeparture.get(0)));
            revenueProjectionData.global.chargesLateDeparture.set(1, newChargesLateDeparture);
        }

        revenueProjectionData.global.totalFlights.set(0, flights);
        revenueProjectionData.global.totalFlights.set(1, newTotalFlights);
        revenueProjectionData.global.totalFlights.set(2, calcPercentChange(newTotalFlights, flights));

        log.debug("setVolumeFlightCharges {},", revenueProjectionData.global.totalFlights);
    }

    private void setVolumePassengerCharges(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, double passengers) {
        double newTotalPassengers = passengers;

        if (revenueProjection.getChargesVolPassengers() != null && revenueProjection.getChargesVolPassengers() != 0.0d) {
            newTotalPassengers = passengers * convertToPercentage(revenueProjection.getChargesVolPassengers());

            double newChargesPassenger = (revenueProjectionData.global.chargesPassenger.get(1) / passengers) * newTotalPassengers;

            revenueProjectionData.global.chargesPassenger.set(2, calcPercentChange(newChargesPassenger, revenueProjectionData.global.chargesPassenger.get(0)));
            revenueProjectionData.global.chargesPassenger.set(1, newChargesPassenger);
        }

        revenueProjectionData.global.totalPassengers.set(0, passengers);
        revenueProjectionData.global.totalPassengers.set(1, newTotalPassengers);
        revenueProjectionData.global.totalPassengers.set(2, calcPercentChange(newTotalPassengers, passengers));

        log.debug("setVolumePassengerCharges {},", revenueProjectionData.global.totalPassengers);
    }

    /*******************************************************************************************************************************************
     *
     *  REVENUE CALCULATIONS - ENROUTE FORMULA CHANGES
     *  create a new nbf with formulas replaced by user entered formulas as needed
     *  calculate per flight charges using old and new formu;as
     *  take into consideration changes in flight volume
     *
     *******************************************************************************************************************************************/

    private NavigationBillingFormula createNewEnrouteBillingFormula(RevenueProjection revenueProjection, RevenueProjectionData revenueProjectionData, NavigationBillingFormula nbfOld) {
        NavigationBillingFormula nbfNew = new NavigationBillingFormula();
        Double include = (modifiedOnly ? 1.0d: 0.0d);

        // domestic charges
        if (StringUtils.isEmpty(revenueProjection.getDomesticFormula()))  {
            nbfNew.setDomesticFormula(nbfOld.getDomesticFormula());
            revenueProjectionData.global.chargesEnrouteDom.set(3, include);
        } else {
            nbfNew.setDomesticFormula(revenueProjection.getDomesticFormula());
            revenueProjectionData.global.chargesEnrouteDom.set(3, 1.0d);
        }

        if (StringUtils.isEmpty(revenueProjection.getDomesticDFactorFormula()))  {
            nbfNew.setDomesticDFactorFormula(nbfOld.getDomesticDFactorFormula());
            revenueProjectionData.global.chargesEnrouteDomD.set(3, include);
        } else {
            nbfNew.setDomesticDFactorFormula(revenueProjection.getDomesticDFactorFormula());
            revenueProjectionData.global.chargesEnrouteDomD.set(3, 1.0d);
        }

        // international arrival charges
        if (StringUtils.isEmpty(revenueProjection.getInternationalArrivalFormula()))  {
            nbfNew.setInternationalArrivalFormula(nbfOld.getInternationalArrivalFormula());
            revenueProjectionData.global.chargesEnrouteIntArr.set(3, include);
        } else {
            nbfNew.setInternationalArrivalFormula(revenueProjection.getInternationalArrivalFormula());
            revenueProjectionData.global.chargesEnrouteIntArr.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getIntArrDFactorFormula()))  {
            nbfNew.setIntArrDFactorFormula(nbfOld.getIntArrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntArrD.set(3, include);
        } else {
            nbfNew.setIntArrDFactorFormula(revenueProjection.getIntArrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntArrD.set(3, 1.0d);
        }

        // international departure charges
        if (StringUtils.isEmpty(revenueProjection.getInternationalDepartureFormula()))  {
            nbfNew.setInternationalDepartureFormula(nbfOld.getInternationalDepartureFormula());
            revenueProjectionData.global.chargesEnrouteIntDep.set(3, include);
        } else {
            nbfNew.setInternationalDepartureFormula(revenueProjection.getInternationalDepartureFormula());
            revenueProjectionData.global.chargesEnrouteIntDep.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getIntDepDFactorFormula()))  {
            nbfNew.setIntDepDFactorFormula(nbfOld.getIntDepDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntDepD.set(3, include);
        } else {
            nbfNew.setIntDepDFactorFormula(revenueProjection.getIntDepDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntDepD.set(3, 1.0d);
        }

        // international overflight charges
        if (StringUtils.isEmpty(revenueProjection.getInternationalOverflightFormula()))  {
            nbfNew.setInternationalOverflightFormula(nbfOld.getInternationalOverflightFormula());
            revenueProjectionData.global.chargesEnrouteIntOvf.set(3, include);
        } else {
            nbfNew.setInternationalOverflightFormula(revenueProjection.getInternationalOverflightFormula());
            revenueProjectionData.global.chargesEnrouteIntOvf.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getIntOvrDFactorFormula()))  {
            nbfNew.setIntOvrDFactorFormula(nbfOld.getIntOvrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntOvfD.set(3, include);
        } else {
            nbfNew.setIntOvrDFactorFormula(revenueProjection.getIntOvrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntOvfD.set(3, 1.0d);
        }

        // regional arrival charges
        if (StringUtils.isEmpty(revenueProjection.getRegionalArrivalFormula()))  {
            nbfNew.setRegionalArrivalFormula(nbfOld.getRegionalArrivalFormula());
            revenueProjectionData.global.chargesEnrouteRegArr.set(3, include);
        } else {
            nbfNew.setRegionalArrivalFormula(revenueProjection.getRegionalArrivalFormula());
            revenueProjectionData.global.chargesEnrouteIntOvf.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getRegArrDFactorFormula()))  {
            nbfNew.setRegArrDFactorFormula(nbfOld.getRegArrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntOvfD.set(3, include);
        } else {
            nbfNew.setRegArrDFactorFormula(revenueProjection.getRegArrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteIntOvfD.set(3, 1.0d);
        }

        // regional departure charges
        if (StringUtils.isEmpty(revenueProjection.getRegionalDepartureFormula()))  {
            nbfNew.setRegionalDepartureFormula(nbfOld.getRegionalDepartureFormula());
            revenueProjectionData.global.chargesEnrouteRegDep.set(3, include);
        } else {
            nbfNew.setRegionalDepartureFormula(revenueProjection.getRegionalDepartureFormula());
            revenueProjectionData.global.chargesEnrouteRegDep.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getRegDepDFactorFormula()))  {
            nbfNew.setRegDepDFactorFormula(nbfOld.getRegDepDFactorFormula());
            revenueProjectionData.global.chargesEnrouteRegDepD.set(3, include);
        } else {
            nbfNew.setRegDepDFactorFormula(revenueProjection.getRegDepDFactorFormula());
            revenueProjectionData.global.chargesEnrouteRegDepD.set(3, 1.0d);
        }

        // regional overflight charges
        if (StringUtils.isEmpty(revenueProjection.getRegionalOverflightFormula()))  {
            nbfNew.setRegionalOverflightFormula(nbfOld.getRegionalOverflightFormula());
            revenueProjectionData.global.chargesEnrouteRegOvf.set(3, include);
        } else {
            nbfNew.setRegionalOverflightFormula(revenueProjection.getRegionalOverflightFormula());
            revenueProjectionData.global.chargesEnrouteRegOvf.set(3, 1.0d);
        }
        if (StringUtils.isEmpty(revenueProjection.getRegOvrDFactorFormula()))  {
            nbfNew.setRegOvrDFactorFormula(nbfOld.getRegOvrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteRegOvfD.set(3, include);
        } else {
            nbfNew.setRegOvrDFactorFormula(revenueProjection.getRegOvrDFactorFormula());
            revenueProjectionData.global.chargesEnrouteRegOvfD.set(3, 1.0d);
        }

        // regional overflight charges
        if (StringUtils.isEmpty(revenueProjection.getwFactorFormula()))  {
            nbfNew.setwFactorFormula(nbfOld.getwFactorFormula());
            revenueProjectionData.global.chargesEnrouteW.set(3, include);
        } else {
            nbfNew.setwFactorFormula(revenueProjection.getwFactorFormula());
            revenueProjectionData.global.chargesEnrouteW.set(3, 1.0d);
        }

        return nbfNew;
    }

    private void calculateEnrouteCharges(List<FlightMovement> flightMovements, NavigationBillingFormula nbfOld, NavigationBillingFormula nbfNew, RevenueProjectionData revenueProjectionData) {
        double chargesEnrouteDomOld = 0.0d;
        double chargesEnrouteDomNew = 0.0d;
        double chargesEnrouteDomDOld = 0.0d;
        double chargesEnrouteDomDNew = 0.0d;
        double chargesEnrouteIntArrOld = 0.0d;
        double chargesEnrouteIntArrNew = 0.0d;
        double chargesEnrouteIntDArrOld = 0.0d;
        double chargesEnrouteIntDArrNew = 0.0d;
        double chargesEnrouteIntDepOld = 0.0d;
        double chargesEnrouteIntDepNew = 0.0d;
        double chargesEnrouteIntDDepOld = 0.0d;
        double chargesEnrouteIntDDepNew = 0.0d;
        double chargesEnrouteIntOvfOld = 0.0d;
        double chargesEnrouteIntOvfNew = 0.0d;
        double chargesEnrouteIntDOvfOld = 0.0d;
        double chargesEnrouteIntDOvfNew = 0.0d;
        double chargesEnrouteRegArrOld = 0.0d;
        double chargesEnrouteRegArrNew = 0.0d;
        double chargesEnrouteRegDArrOld = 0.0d;
        double chargesEnrouteRegDArrNew = 0.0d;
        double chargesEnrouteRegDepOld = 0.0d;
        double chargesEnrouteRegDepNew = 0.0d;
        double chargesEnrouteRegDDepOld = 0.0d;
        double chargesEnrouteRegDDepNew = 0.0d;
        double chargesEnrouteRegOvfOld = 0.0d;
        double chargesEnrouteRegOvfNew = 0.0d;
        double chargesEnrouteRegDOvfOld = 0.0d;
        double chargesEnrouteRegDOvfNew = 0.0d;
        double chargesEnrouteWOld = 0.0d;
        double chargesEnrouteWNew = 0.0d;

        for (FlightMovement fm: flightMovements) {
            if (revenueProjectionData.global.chargesEnrouteDom.get(3) == 1.0d) {
                chargesEnrouteDomOld += calculateEnrouteFormula(fm, nbfOld.getDomesticFormula());
                chargesEnrouteDomNew += calculateEnrouteFormula(fm, nbfNew.getDomesticFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteDomD.get(3) == 1.0d) {
                chargesEnrouteDomDOld += calculateEnrouteFormula(fm, nbfOld.getDomesticDFactorFormula());
                chargesEnrouteDomDNew += calculateEnrouteFormula(fm, nbfNew.getDomesticDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntArr.get(3) == 1.0d) {
                chargesEnrouteIntArrOld += calculateEnrouteFormula(fm, nbfOld.getInternationalArrivalFormula());
                chargesEnrouteIntArrNew += calculateEnrouteFormula(fm, nbfNew.getInternationalArrivalFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntArrD.get(3) == 1.0d) {
                chargesEnrouteIntDArrOld += calculateEnrouteFormula(fm, nbfOld.getIntArrDFactorFormula());
                chargesEnrouteIntDArrNew += calculateEnrouteFormula(fm, nbfNew.getIntArrDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntDep.get(3) == 1.0d) {
                chargesEnrouteIntDepOld += calculateEnrouteFormula(fm, nbfOld.getInternationalDepartureFormula());
                chargesEnrouteIntDepNew += calculateEnrouteFormula(fm, nbfNew.getInternationalDepartureFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntDep.get(3) == 1.0d) {
                chargesEnrouteIntDDepOld += calculateEnrouteFormula(fm, nbfOld.getIntDepDFactorFormula());
                chargesEnrouteIntDDepNew += calculateEnrouteFormula(fm, nbfNew.getIntDepDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntOvf.get(3) == 1.0d) {
                chargesEnrouteIntOvfOld += calculateEnrouteFormula(fm, nbfOld.getInternationalOverflightFormula());
                chargesEnrouteIntOvfNew += calculateEnrouteFormula(fm, nbfNew.getInternationalOverflightFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteIntOvfD.get(3) == 1.0d) {
                chargesEnrouteIntDOvfOld += calculateEnrouteFormula(fm, nbfOld.getIntOvrDFactorFormula());
                chargesEnrouteIntDOvfNew += calculateEnrouteFormula(fm, nbfNew.getIntOvrDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegArr.get(3) == 1.0d) {
                chargesEnrouteRegArrOld += calculateEnrouteFormula(fm, nbfOld.getRegionalArrivalFormula());
                chargesEnrouteRegArrNew += calculateEnrouteFormula(fm, nbfNew.getRegionalArrivalFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegArrD.get(3) == 1.0d) {
                chargesEnrouteRegDArrOld += calculateEnrouteFormula(fm, nbfOld.getRegArrDFactorFormula());
                chargesEnrouteRegDArrNew += calculateEnrouteFormula(fm, nbfNew.getRegArrDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegDep.get(3) == 1.0d) {
                chargesEnrouteRegDepOld += calculateEnrouteFormula(fm, nbfOld.getRegionalDepartureFormula());
                chargesEnrouteRegDepNew += calculateEnrouteFormula(fm, nbfNew.getRegionalDepartureFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegDepD.get(3) == 1.0d) {
                chargesEnrouteRegDDepOld += calculateEnrouteFormula(fm, nbfOld.getRegDepDFactorFormula());
                chargesEnrouteRegDDepNew += calculateEnrouteFormula(fm, nbfNew.getRegDepDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegOvf.get(3) == 1.0d) {
                chargesEnrouteRegOvfOld += calculateEnrouteFormula(fm, nbfOld.getRegionalOverflightFormula());
                chargesEnrouteRegOvfNew += calculateEnrouteFormula(fm, nbfNew.getRegionalOverflightFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteRegOvfD.get(3) == 1.0d) {
                chargesEnrouteRegDOvfOld += calculateEnrouteFormula(fm, nbfOld.getRegOvrDFactorFormula());
                chargesEnrouteRegDOvfNew += calculateEnrouteFormula(fm, nbfNew.getRegOvrDFactorFormula());
            }

            if (revenueProjectionData.global.chargesEnrouteW.get(3) == 1.0d) {
                chargesEnrouteWOld += calculateEnrouteFormula(fm, nbfOld.getwFactorFormula());
                chargesEnrouteWNew += calculateEnrouteFormula(fm, nbfNew.getwFactorFormula());
            }
        }

        chargesEnrouteDomNew = recalcVolumeBasedEnroute(chargesEnrouteDomNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteDom.set(0, chargesEnrouteDomOld);
        revenueProjectionData.global.chargesEnrouteDom.set(1, chargesEnrouteDomNew);
        revenueProjectionData.global.chargesEnrouteDom.set(2, calcPercentChange(chargesEnrouteDomNew, chargesEnrouteDomOld));

        chargesEnrouteDomDNew = recalcVolumeBasedEnroute(chargesEnrouteDomDNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteDomD.set(0, chargesEnrouteDomDOld);
        revenueProjectionData.global.chargesEnrouteDomD.set(1, chargesEnrouteDomDNew);
        revenueProjectionData.global.chargesEnrouteDomD.set(2, calcPercentChange(chargesEnrouteDomDNew, chargesEnrouteDomDOld));

        chargesEnrouteIntArrNew = recalcVolumeBasedEnroute(chargesEnrouteIntArrNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntArr.set(0, chargesEnrouteIntArrOld);
        revenueProjectionData.global.chargesEnrouteIntArr.set(1, chargesEnrouteIntArrNew);
        revenueProjectionData.global.chargesEnrouteIntArr.set(2, calcPercentChange(chargesEnrouteIntArrNew, chargesEnrouteIntArrOld));

        chargesEnrouteIntDArrNew = recalcVolumeBasedEnroute(chargesEnrouteIntDArrNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntArrD.set(0, chargesEnrouteIntDArrOld);
        revenueProjectionData.global.chargesEnrouteIntArrD.set(1, chargesEnrouteIntDArrNew);
        revenueProjectionData.global.chargesEnrouteIntArrD.set(2, calcPercentChange(chargesEnrouteIntDArrNew, chargesEnrouteIntDArrOld));

        chargesEnrouteIntDepNew = recalcVolumeBasedEnroute(chargesEnrouteIntDepNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntDep.set(0, chargesEnrouteIntDepOld);
        revenueProjectionData.global.chargesEnrouteIntDep.set(1, chargesEnrouteIntDepNew);
        revenueProjectionData.global.chargesEnrouteIntDep.set(2, calcPercentChange(chargesEnrouteIntDepNew, chargesEnrouteIntDepOld));

        chargesEnrouteIntDDepNew = recalcVolumeBasedEnroute(chargesEnrouteIntDDepNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntDepD.set(0, chargesEnrouteIntDDepOld);
        revenueProjectionData.global.chargesEnrouteIntDepD.set(1, chargesEnrouteIntDDepNew);
        revenueProjectionData.global.chargesEnrouteIntDepD.set(2, calcPercentChange(chargesEnrouteIntDDepNew, chargesEnrouteIntDDepOld));

        chargesEnrouteIntOvfNew = recalcVolumeBasedEnroute(chargesEnrouteIntOvfNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntOvf.set(0, chargesEnrouteIntOvfOld);
        revenueProjectionData.global.chargesEnrouteIntOvf.set(1, chargesEnrouteIntOvfNew);
        revenueProjectionData.global.chargesEnrouteIntOvf.set(2, calcPercentChange(chargesEnrouteIntOvfNew, chargesEnrouteIntOvfOld));

        chargesEnrouteIntDOvfNew = recalcVolumeBasedEnroute(chargesEnrouteIntDOvfNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteIntOvfD.set(0, chargesEnrouteIntDOvfOld);
        revenueProjectionData.global.chargesEnrouteIntOvfD.set(1, chargesEnrouteIntDOvfNew);
        revenueProjectionData.global.chargesEnrouteIntOvfD.set(2, calcPercentChange(chargesEnrouteIntDOvfNew, chargesEnrouteIntDOvfOld));

        chargesEnrouteRegArrNew = recalcVolumeBasedEnroute(chargesEnrouteRegArrNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegArr.set(0, chargesEnrouteRegArrOld);
        revenueProjectionData.global.chargesEnrouteRegArr.set(1, chargesEnrouteRegArrNew);
        revenueProjectionData.global.chargesEnrouteRegArr.set(2, calcPercentChange(chargesEnrouteRegArrNew, chargesEnrouteRegArrOld));

        chargesEnrouteRegDArrNew = recalcVolumeBasedEnroute(chargesEnrouteRegDArrNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegArrD.set(0, chargesEnrouteRegDArrOld);
        revenueProjectionData.global.chargesEnrouteRegArrD.set(1, chargesEnrouteRegDArrNew);
        revenueProjectionData.global.chargesEnrouteRegArrD.set(2, calcPercentChange(chargesEnrouteRegDArrNew, chargesEnrouteRegDArrOld));

        chargesEnrouteRegDepNew = recalcVolumeBasedEnroute(chargesEnrouteRegDepNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegDep.set(0, chargesEnrouteRegDepOld);
        revenueProjectionData.global.chargesEnrouteRegDep.set(1, chargesEnrouteRegDepNew);
        revenueProjectionData.global.chargesEnrouteRegDep.set(2, calcPercentChange(chargesEnrouteRegDepNew, chargesEnrouteRegDepOld));

        chargesEnrouteRegDDepNew = recalcVolumeBasedEnroute(chargesEnrouteRegDDepNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegDepD.set(0, chargesEnrouteRegDDepOld);
        revenueProjectionData.global.chargesEnrouteRegDepD.set(1, chargesEnrouteRegDDepNew);
        revenueProjectionData.global.chargesEnrouteRegDepD.set(2, calcPercentChange(chargesEnrouteRegDDepNew, chargesEnrouteRegDDepOld));

        chargesEnrouteRegOvfNew = recalcVolumeBasedEnroute(chargesEnrouteRegOvfNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegOvf.set(0, chargesEnrouteRegOvfOld);
        revenueProjectionData.global.chargesEnrouteRegOvf.set(1, chargesEnrouteRegOvfNew);
        revenueProjectionData.global.chargesEnrouteRegOvf.set(2, calcPercentChange(chargesEnrouteRegOvfNew, chargesEnrouteRegOvfOld));

        chargesEnrouteRegDOvfNew = recalcVolumeBasedEnroute(chargesEnrouteRegDOvfNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteRegOvfD.set(0, chargesEnrouteRegDOvfOld);
        revenueProjectionData.global.chargesEnrouteRegOvfD.set(1, chargesEnrouteRegDOvfNew);
        revenueProjectionData.global.chargesEnrouteRegOvfD.set(2, calcPercentChange(chargesEnrouteRegDOvfNew, chargesEnrouteRegDOvfOld));

        chargesEnrouteWNew = recalcVolumeBasedEnroute(chargesEnrouteWNew, revenueProjectionData);
        revenueProjectionData.global.chargesEnrouteW.set(0, chargesEnrouteWOld);
        revenueProjectionData.global.chargesEnrouteW.set(1, chargesEnrouteWNew);
        revenueProjectionData.global.chargesEnrouteW.set(2, calcPercentChange(chargesEnrouteWNew, chargesEnrouteWOld));
    }

    private double recalcVolumeBasedEnroute(double chargesEnrouteDomNew, RevenueProjectionData revenueProjectionData) {
        if (revenueProjectionData.global.totalFlights.get(2) == 0.0d) {
            return chargesEnrouteDomNew;
        } else {
            return (chargesEnrouteDomNew / revenueProjectionData.global.totalFlights.get(0)) * revenueProjectionData.global.totalFlights.get(1);
        }
    }

    /**
     * Build and execute the provided formula for a given flight
     *
     * @param fm FlightMovement provided flight movement
     * @param formula string formula to be evaluated
     */
    private double calculateEnrouteFormula(FlightMovement fm, String formula) {
        try {
            HashMap<String, Object> map = newFormulaMap(fm, formula);

            return formulaEvaluator.evalDouble(formula, map);
        } catch (Exception e) {
            log.error("Cannot calculate Enroute Formula: flight={}, formula={}", fm.getFlightId(), formula);

            return 0.0d;
        }
    }

    /**
     * Create a new formula hash map for calculating enroute formulas
     *
     * @param fm FlightMovement
     */
    private HashMap<String, Object> newFormulaMap(FlightMovement fm, String formula) {
        HashMap<String, Object> map = new HashMap<>();

        if (formula.indexOf("[AccountDiscount]") != -1) {
            map.put(CostFormulaVar.ACCOUNTDISCOUNT.varName(), getDiscount(fm));
        }

        if (formula.indexOf("[AvgMassFactor]") != -1) {
            map.put(CostFormulaVar.AVGMASSFACTOR.varName(), getAvgMassFactor(fm));
        }

        if (formula.indexOf("[EntriesNumber]") != -1) {
            map.put(CostFormulaVar.ENTRIESNUMBER.varName(), 0.0d);
        }

        if (formula.indexOf("[FirEntryFee]") != -1) {
            map.put(CostFormulaVar.FIRENTRYFEE.varName(), 0.0d);
        }

        if (formula.indexOf("[MTOW]") != -1) {
            map.put(CostFormulaVar.MTOW.varName(), getMtow(fm));
        }

        if (formula.indexOf("[CrossDist]") != -1) {
            map.put(CostFormulaVar.SCHEDCROSSDIST.varName(), getCrossingDistance(fm));
        }

        return map;
    }

    /**
     * Get the discount for the flight
     *
     * @param fm FlightMovement
     */
    private Double getDiscount(FlightMovement fm) {
        if (fm.getAccount() != null) {
            Account account = fm.getAccount();

            if (account.getDiscountStructure() == null) {
                return 0.0d;
            } else {
                return account.getDiscountStructure();
            }
        } else {
            return 0.0d;
        }
    }

    /**
     * Get flight movement type
     *
     * @param fm FlightMovement
     */
    private Double getAvgMassFactor(FlightMovement fm) {
        final AverageMtowFactor amf = averageMtowFactorService.findAverageMtowFactorByUpperLimitAndScope (fm.getActualMtow(), fm.getFlightCategoryScope());
        if (amf != null) {
            return amf.getAverageMtowFactor();
        }
        return 0.0d;
    }

    /**
     * Get the flight crossing distance
     *
     * @param fm FlightMovement
     */
    private Double getCrossingDistance(FlightMovement fm) {
        if (fm.getFplCrossingCost() != null) {
            return fm.getFplCrossingCost();
        } else if (fm.getAtcCrossingDistance() != null) {
            return fm.getAtcCrossingDistance();
        } else if (fm.getRadarCrossingDistance() != null) {
            return fm.getRadarCrossingDistance();
        } else if (fm.getBillableCrossingDist() != null) {
            return fm.getBillableCrossingDist();
        } else {
            return 0.0d;
        }
    }

    /**
     * Get the actual MTOW of the flight
     *
     * @param fm FlightMovement
     */
    private Double getMtow(FlightMovement fm) {
        if (fm.getActualMtow() != null) {
            return fm.getActualMtow();
        } else {
            return 0.0d;
        }
    }

    /*******************************************************************************************************************************************
     *
     *  REVENUE CALCULATIONS - TOTAL REVENUE
     *
     *******************************************************************************************************************************************/

    private void setTotalRevenue(RevenueProjectionData revenueProjectionData) {
        double oldTotalRev = 0.0d;
        double newTotalRev = 0.0d;

        if (revenueProjectionData.global.chargesAerodrome.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesAerodrome.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesAerodrome.get(1));
        }

        if (revenueProjectionData.global.chargesApproach.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesApproach.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesApproach.get(1));
        }

        if (revenueProjectionData.global.chargesLateArrival.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesLateArrival.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesLateArrival.get(1));
        }

        if (revenueProjectionData.global.chargesLateDeparture.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesLateDeparture.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesLateDeparture.get(1));
        }

        if (revenueProjectionData.global.chargesPassenger.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesPassenger.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesPassenger.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteDom.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteDom.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteDom.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteDomD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteDomD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteDomD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntArr.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArr.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArr.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntArrD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArrD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArrD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntDep.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDep.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDep.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntDepD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDepD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDepD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntOvf.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvf.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvf.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteIntOvfD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvfD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvfD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegArr.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArr.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArr.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegArrD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArrD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArrD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegDep.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDep.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDep.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegDepD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDepD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDepD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegOvf.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvf.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvf.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteRegOvfD.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvfD.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvfD.get(1));
        }

        if (revenueProjectionData.global.chargesEnrouteW.get(3) == 1.0d) {
            oldTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteW.get(0));
            newTotalRev += testDoubleForNull(revenueProjectionData.global.chargesEnrouteW.get(1));
        }

        revenueProjectionData.global.totalRevenue.set(0, oldTotalRev);
        revenueProjectionData.global.totalRevenue.set(1, newTotalRev);
        revenueProjectionData.global.totalRevenue.set(2, calcPercentChange(newTotalRev, oldTotalRev));
    }

    private RevenueProjectionReport convertToReport(RevenueProjectionData revenueProjectionData) {
        RevenueProjectionReport revenueProjectionReport = new RevenueProjectionReport();
        DecimalFormat df = new DecimalFormat("#.##");

        revenueProjectionReport.global = new RevenueProjectionReport.Global();

        revenueProjectionReport.global.chargesAerodrome_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesAerodrome.get(0)));
        revenueProjectionReport.global.chargesApproach_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesApproach.get(0)));
        revenueProjectionReport.global.chargesLateArrival_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateArrival.get(0)));
        revenueProjectionReport.global.chargesLateDeparture_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateDeparture.get(0)));
        revenueProjectionReport.global.chargesPassenger_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesPassenger.get(0)));

        revenueProjectionReport.global.chargesAerodrome_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesAerodrome.get(1)));
        revenueProjectionReport.global.chargesApproach_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesApproach.get(1)));
        revenueProjectionReport.global.chargesLateArrival_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateArrival.get(1)));
        revenueProjectionReport.global.chargesLateDeparture_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateDeparture.get(1)));
        revenueProjectionReport.global.chargesPassenger_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesPassenger.get(1)));

        revenueProjectionReport.global.chargesAerodrome_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesAerodrome.get(2))) + "%";
        revenueProjectionReport.global.chargesApproach_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesApproach.get(2))) + "%";
        revenueProjectionReport.global.chargesLateArrival_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateArrival.get(2))) + "%";
        revenueProjectionReport.global.chargesLateDeparture_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesLateDeparture.get(2))) + "%";
        revenueProjectionReport.global.chargesPassenger_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesPassenger.get(2))) + "%";

        revenueProjectionReport.global.chargesAerodrome_inc = revenueProjectionData.global.chargesAerodrome.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesApproach_inc = revenueProjectionData.global.chargesApproach.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesLateArrival_inc = revenueProjectionData.global.chargesLateArrival.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesLateDeparture_inc = revenueProjectionData.global.chargesLateDeparture.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesPassenger_inc = revenueProjectionData.global.chargesPassenger.get(3) == 1.0d ? TRUE : FALSE;

        // applied formula to charges (current, projected, % change, included)
        revenueProjectionReport.global.chargesEnrouteDom_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDom.get(0)));
        revenueProjectionReport.global.chargesEnrouteDomD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDomD.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntArr_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArr.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntArrD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArrD.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntDep_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDep.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntDepD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDepD.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntOvf_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvf.get(0)));
        revenueProjectionReport.global.chargesEnrouteIntOvfD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvfD.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegArr_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArr.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegArrD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArrD.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegDep_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDep.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegDepD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDepD.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegOvf_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvf.get(0)));
        revenueProjectionReport.global.chargesEnrouteRegOvfD_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvfD.get(0)));
        revenueProjectionReport.global.chargesEnrouteW_old = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteW.get(0)));

        revenueProjectionReport.global.chargesEnrouteDom_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDom.get(1)));
        revenueProjectionReport.global.chargesEnrouteDomD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDomD.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntArr_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArr.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntArrD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArrD.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntDep_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDep.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntDepD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDepD.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntOvf_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvf.get(1)));
        revenueProjectionReport.global.chargesEnrouteIntOvfD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvfD.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegArr_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArr.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegArrD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArrD.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegDep_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDep.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegDepD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDepD.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegOvf_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvf.get(1)));
        revenueProjectionReport.global.chargesEnrouteRegOvfD_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvfD.get(1)));
        revenueProjectionReport.global.chargesEnrouteW_new = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteW.get(1)));

        revenueProjectionReport.global.chargesEnrouteDom_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDom.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteDomD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteDomD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntArr_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArr.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntArrD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntArrD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntDep_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDep.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntDepD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntDepD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntOvf_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvf.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteIntOvfD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteIntOvfD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegArr_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArr.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegArrD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegArrD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegDep_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDep.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegDepD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegDepD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegOvf_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvf.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteRegOvfD_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteRegOvfD.get(2))) + "%";
        revenueProjectionReport.global.chargesEnrouteW_prc = df.format(testDoubleForNull(revenueProjectionData.global.chargesEnrouteW.get(2))) + "%";

        revenueProjectionReport.global.chargesEnrouteDom_inc = revenueProjectionData.global.chargesEnrouteDom.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteDomD_inc = revenueProjectionData.global.chargesEnrouteDomD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntArr_inc = revenueProjectionData.global.chargesEnrouteIntArr.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntArrD_inc = revenueProjectionData.global.chargesEnrouteIntArrD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntDep_inc = revenueProjectionData.global.chargesEnrouteIntDep.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntDepD_inc = revenueProjectionData.global.chargesEnrouteIntDepD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntOvf_inc = revenueProjectionData.global.chargesEnrouteIntOvf.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteIntOvfD_inc = revenueProjectionData.global.chargesEnrouteIntOvfD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegArr_inc = revenueProjectionData.global.chargesEnrouteRegArr.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegArrD_inc = revenueProjectionData.global.chargesEnrouteRegArrD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegDep_inc = revenueProjectionData.global.chargesEnrouteRegDep.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegDepD_inc = revenueProjectionData.global.chargesEnrouteRegDepD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegOvf_inc = revenueProjectionData.global.chargesEnrouteRegOvf.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteRegOvfD_inc = revenueProjectionData.global.chargesEnrouteRegOvfD.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.chargesEnrouteW_inc = revenueProjectionData.global.chargesEnrouteW.get(3) == 1.0d ? TRUE : FALSE;

        //totals (current, projected, % change, included)
        revenueProjectionReport.global.totalPassengers_old = df.format(testDoubleForNull(revenueProjectionData.global.totalPassengers.get(0)));
        revenueProjectionReport.global.totalFlights_old = df.format(testDoubleForNull(revenueProjectionData.global.totalFlights.get(0)));
        revenueProjectionReport.global.totalRevenue_old = df.format(testDoubleForNull(revenueProjectionData.global.totalRevenue.get(0)));

        revenueProjectionReport.global.totalPassengers_new = df.format(testDoubleForNull(revenueProjectionData.global.totalPassengers.get(1)));
        revenueProjectionReport.global.totalFlights_new = df.format(testDoubleForNull(revenueProjectionData.global.totalFlights.get(1)));
        revenueProjectionReport.global.totalRevenue_new = df.format(testDoubleForNull(revenueProjectionData.global.totalRevenue.get(1)));

        revenueProjectionReport.global.totalPassengers_prc = df.format(testDoubleForNull(revenueProjectionData.global.totalPassengers.get(2))) + "%";
        revenueProjectionReport.global.totalFlights_prc = df.format(testDoubleForNull(revenueProjectionData.global.totalFlights.get(2))) + "%";
        revenueProjectionReport.global.totalRevenue_prc = df.format(testDoubleForNull(revenueProjectionData.global.totalRevenue.get(2))) + "%";

        revenueProjectionReport.global.totalPassengers_inc = revenueProjectionData.global.totalPassengers.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.totalFlights_inc = revenueProjectionData.global.totalFlights.get(3) == 1.0d ? TRUE : FALSE;
        revenueProjectionReport.global.totalRevenue_inc = revenueProjectionData.global.totalRevenue.get(3) == 1.0d ? TRUE : FALSE;

        return revenueProjectionReport;
    }

}
