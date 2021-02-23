package ca.ids.abms.modules.flightmovementsbuilder;

    import java.time.LocalDate;
    import java.time.LocalDateTime;
    import java.time.LocalTime;

    import ca.ids.abms.modules.atcmovements.AtcMovementLog;
    import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
    import ca.ids.abms.modules.common.enumerators.FlightCategory;
    import ca.ids.abms.modules.flightmovements.FlightMovement;
    import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementSource;
    import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
    import ca.ids.abms.modules.flightmovementsbuilder.utility.RouteUtility;
    import ca.ids.abms.modules.radarsummary.RadarSummary;
    import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
    import ca.ids.abms.modules.towermovements.TowerMovementLog;
    import ca.ids.abms.util.converter.JSR310DateConverters;
    import org.apache.commons.lang.StringUtils;

/**
 * This class mange the mapper from the following Object and FlightMovement. Map
 * from FplObject to FlightMovement Map from RadarSummary to FlightMovement Map
 * from TowerMovementLog to FlightMovement Map from AtcMovementLog to
 * FlightMovement
 *
 * Created by c.talpa on 22/11/2016.
 */
class FlightMovementObjectMapper {

    static FlightMovement atcMovementLogToFlightMovement(AtcMovementLog atcMovementLog, String depTime, LocalDateTime dateOfFlight) {
        FlightMovement flightMovement = null;

        if (atcMovementLog != null) {
            flightMovement = new FlightMovement();
            flightMovement.setFlightId(atcMovementLog.getFlightId());
            if (dateOfFlight != null) {
                flightMovement.setDateOfFlight(dateOfFlight);
            } else {
                flightMovement.setDateOfFlight(atcMovementLog.getDateOfContact());
            }
            flightMovement.setBillingDate(atcMovementLog.getDateOfContact());
            flightMovement.setItem18RegNum(atcMovementLog.getRegistration());
            if (StringUtils.isNotBlank(depTime)) {
                flightMovement.setDepTime(depTime);
            } else {
                flightMovement.setDepTime(atcMovementLog.getDepartureTime());
            }
            flightMovement.setAtcLogRouteText(atcMovementLog.getRoute());
            flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
            flightMovement.setSource(FlightMovementSource.ATC_LOG);
            flightMovement.setAircraftType(atcMovementLog.getAircraftType());
            flightMovement.setDepAd(atcMovementLog.getDepartureAerodrome());
            flightMovement.setDestAd(atcMovementLog.getDestinationAerodrome());
            flightMovement.setAircraftType(atcMovementLog.getAircraftType());
            if (FlightCategory.SCH.equals(atcMovementLog.getFlightCategory())) {
                flightMovement.setFlightType("S");
            } else {
                flightMovement.setFlightType("N");
            }
            flightMovement.setFlightLevel(atcMovementLog.getFlightLevel());
            flightMovement.setArrivalAd(atcMovementLog.getDestinationAerodrome());
            flightMovement.setItem18Operator(atcMovementLog.getOperatorIdentifier());
        }
        return flightMovement;
    }

    /**
     *  2019-09-25 Always set the date of flight from fpl object
     * This method map a FplObject to FlightMovement
     *
     * @return flight movement or null
     */
    static FlightMovement fplObjectToFlightMovement(FplObjectDto fplObject) {
        FlightMovement flightMovement = null;

        if (fplObject != null) {
            flightMovement = new FlightMovement();
            flightMovement.setSpatiaFplObjectId(fplObject.getCatalogueFplObjectId());
            flightMovement.setDateOfFlight(fplObject.getDayOfFlight() != null ? fplObject.getDayOfFlight().atStartOfDay() : null);
            flightMovement.setDepTime(normalizeTimeValue(fplObject.getDepartureTime()));
            flightMovement.setBillingDate(flightMovement.getDateOfFlight());
            flightMovement.setFlightId(fplObject.getFlightId());
            flightMovement.setFlightType(fplObject.getFlightType());
            flightMovement.setDepAd(fplObject.getDepartureAd());
            flightMovement.setDestAd(fplObject.getDestinationAd());
            flightMovement.setFplRoute(fplObject.getRoute());
            flightMovement.setAircraftType(fplObject.getAircraftType());
            flightMovement.setWakeTurb(fplObject.getWakeTurb());
            flightMovement.setOtherInfo(fplObject.getOtherInfo());
            flightMovement.setInitialFplData(fplObject.getRawFpl());
            flightMovement.setActualDepartureTime(normalizeTimeValue(fplObject.getMsgDepartureTime()));
            flightMovement.setArrivalTime(normalizeTimeValue(fplObject.getArrivalTime()));
            flightMovement.setArrivalAd(fplObject.getArrivalAd());
            flightMovement.setSource(FlightMovementSource.NETWORK);
            flightMovement.setCruisingSpeedOrMachNumber(fplObject.getSpeed());
            flightMovement.setStatus(determineFlightMovementStatusFromSpatiaFplObject(fplObject));
            flightMovement.setEstimatedElapsedTime(normalizeTimeValue(fplObject.getTotalEet()));
            flightMovement.setFlightRules(fplObject.getFlightRules());
            flightMovement.setFlightLevel(fplObject.getFlightLevel());
        }

        return flightMovement;
    }

    static FlightMovement radarSummaryToFlightMovement(RadarSummary radarSummary, String depTime, LocalDateTime dateOfFlight) {
        FlightMovement flightMovement = null;

        if (radarSummary != null) {
            flightMovement = new FlightMovement();
            flightMovement.setFlightId(radarSummary.getFlightIdentifier());
            if (dateOfFlight != null) {
                flightMovement.setDateOfFlight(dateOfFlight);
            } else {
                flightMovement.setDateOfFlight(radarSummary.getDayOfFlight());
            }
            flightMovement.setBillingDate(radarSummary.getDate());
            flightMovement.setItem18RegNum(radarSummary.getRegistration());
            flightMovement.setAircraftType(radarSummary.getAircraftType());
            flightMovement.setDepAd(radarSummary.getDepartureAeroDrome());
            if (StringUtils.isNotBlank(depTime)) {
                flightMovement.setDepTime(depTime);
            } else {
                flightMovement.setDepTime(radarSummary.getDepartureTime());
            }
            flightMovement.setDestAd(radarSummary.getDestinationAeroDrome());
            flightMovement.setArrivalTime(radarSummary.getDestTime());
            flightMovement.setArrivalAd(radarSummary.getDestinationAeroDrome());
            flightMovement.setRadarRouteText (RouteUtility.addRouteEndPoints (
                radarSummary.getRoute(), radarSummary.getFirEntryPoint(), radarSummary.getFirExitPoint()));

            // radarSummary stores it as `IFV` or `VFR`. Where in
            // `flight_movements` table it is `I` or `V`. We convert to that
            // format
            if (radarSummary.getFlightRule() != null) {
                if (radarSummary.getFlightRule().equals("IFR") || radarSummary.getFlightRule().equals ("I")) {
                    flightMovement.setFlightRules("I");
                } else if (radarSummary.getFlightRule().equals("VFR") || radarSummary.getFlightRule().equals ("V")) {
                    flightMovement.setFlightRules("V");
                }
            }

            // wake turbulence
            flightMovement.setWakeTurb (radarSummary.getWakeTurb());
            // flight level
            flightMovement.setFlightLevel (radarSummary.getFlightLevel());
            // cruising speed
            flightMovement.setCruisingSpeedOrMachNumber(radarSummary.getCruisingSpeed());

            LocalTime localEntryTime = JSR310DateConverters.convertStringToLocalTime(radarSummary.getFirEntryTime(),
                JSR310DateConverters.DEFAULT_PATTERN_TIME);
            LocalTime localExitTime = JSR310DateConverters.convertStringToLocalTime(radarSummary.getFirExitTime(),
                JSR310DateConverters.DEFAULT_PATTERN_TIME);
            if (radarSummary.getDayOfFlight() != null) {
                LocalDate localDate = radarSummary.getDayOfFlight().toLocalDate();
                if (localEntryTime != null) {
                    LocalDateTime entry = LocalDateTime.of(localDate, localEntryTime);
                    flightMovement.setEntryTime(entry);
                }
                if (localExitTime != null) {
                    LocalDateTime exit = LocalDateTime.of(localDate, localExitTime);
                    flightMovement.setExitTime(exit);
                }
            }
            flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
            flightMovement.setSource(FlightMovementSource.RADAR_SUMMARY);
            if (radarSummary.getFlightType() != null) {
                flightMovement.setFlightType(radarSummary.getFlightType().trim().toUpperCase());
            }
        }
        return flightMovement;
    }

    static FlightMovement towerMovementLogToFlightMovement(TowerMovementLog towerMovementLog, String depTime, LocalDateTime dateOfFlight) {
        FlightMovement flightMovement = null;

        if (towerMovementLog != null) {
            flightMovement = new FlightMovement();
            flightMovement.setFlightId(towerMovementLog.getFlightId());
            if (dateOfFlight != null) {
                flightMovement.setDateOfFlight(dateOfFlight);
            } else {
                flightMovement.setDateOfFlight(towerMovementLog.getDateOfContact());
            }
            flightMovement.setBillingDate(towerMovementLog.getDateOfContact());
            flightMovement.setItem18RegNum(towerMovementLog.getRegistration());
            if (StringUtils.isNotBlank(depTime)) {
                flightMovement.setDepTime(depTime);
            } else {
                flightMovement.setDepTime(towerMovementLog.getDepartureContactTime());
            }
            flightMovement.setRadarTowerLogRouteText(towerMovementLog.getRoute());
            flightMovement.setStatus(FlightMovementStatus.INCOMPLETE);
            flightMovement.setSource(FlightMovementSource.TOWER_LOG);
            flightMovement.setDepAd(towerMovementLog.getDepartureAerodrome());
            flightMovement.setDestAd(towerMovementLog.getDestinationAerodrome());
            flightMovement.setArrivalTime(towerMovementLog.getDestinationContactTime());
            flightMovement.setArrivalAd(towerMovementLog.getDestinationAerodrome());
            flightMovement.setAircraftType(towerMovementLog.getAircraftType());
            if (FlightCategory.SCH.equals(towerMovementLog.getFlightCategory())) {
                flightMovement.setFlightType("S");
            } else {
                flightMovement.setFlightType("N");
            }
            flightMovement.setFlightLevel(towerMovementLog.getFlightLevel());
            flightMovement.setItem18Operator(towerMovementLog.getOperatorName());
        }
        return flightMovement;
    }

    /**
     * Passenger service return does not contain enough information to create flight movements. This method should
     * only be used to update existing flight movements.
     */
    static FlightMovement passengerServiceReturnToFlightMovement(PassengerServiceChargeReturn passengerServiceChargeReturn) {
        if (passengerServiceChargeReturn == null) return null;

        FlightMovement flightMovement = new FlightMovement();

        flightMovement.setFlightId(passengerServiceChargeReturn.getFlightId());
        flightMovement.setDateOfFlight(passengerServiceChargeReturn.getDayOfFlight());
        flightMovement.setDepTime(passengerServiceChargeReturn.getDepartureTime());
        flightMovement.setPassengersChargeableDomestic(passengerServiceChargeReturn.getChargeableDomesticPassengers());
        flightMovement.setPassengersChargeableIntern(passengerServiceChargeReturn.getChargeableItlPassengers());

        // 2017-05-09: LR Talking with LVP, we decided to set passenger child filed
        flightMovement.setPassengersChild(passengerServiceChargeReturn.getChildren());

        // After talk with Werner (15 March - 02:20 PM) for the moment we don't use these information on FlightMovement
        //
        // --> flightMovement.setPassengersJoiningAdult(passengerServiceChargeReturn.getJoiningPassengers())
        // --> flightMovement.setPassengersTransitAdult(passengerServiceChargeReturn.getTransitPassengers())

        return flightMovement;
    }

    /**
     * Calculate FlightMovementStatus
     */
    private static FlightMovementStatus determineFlightMovementStatusFromSpatiaFplObject(FplObjectDto fplObject) {
        FlightMovementStatus result = FlightMovementStatus.INCOMPLETE; // default
        // status

        if (fplObject.getCataloguePrcStatus() != null && fplObject.getCataloguePrcStatus().equalsIgnoreCase("C")) {
            result = FlightMovementStatus.CANCELED;

        }

        return result;
    }

    /**
     * This method normalizes a time value into AT LEAST 4 characters to match HHmm pattern.
     * It will left pad any missing characters as '0'. For example, '123' would be normalized as '0123'.
     *
     * It DOES NOT validate any provided value or trim excess characters. For example,
     * null, 'ABC' and 'ABC123' would be returned as null, '0ABC' and 'ABC123' respectively.
     */
    private static String normalizeTimeValue(String time) {
        return org.apache.commons.lang.StringUtils.isBlank(time) ? time
            : org.apache.commons.lang.StringUtils.leftPad(time, 4, '0');
    }

    private FlightMovementObjectMapper() {
        throw new IllegalStateException("Utility class, do not instantiate a new instance.");
    }
}
