package ca.ids.abms.modules.aerodromeoperationalhours;

import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.flightmovements.FlightMovementRepository;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ChargesUtility;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.util.models.ModelUtils;
import ca.ids.abms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.IntStream;

import static java.time.temporal.ChronoUnit.MINUTES;

@Service
@Transactional
public class AerodromeOperationalHoursService {

    private static final Logger LOG = LoggerFactory.getLogger(AerodromeOperationalHoursService.class);
    private final AerodromeOperationalHoursRepository aerodromeOperationalHoursRepository;
    private final SystemConfigurationService systemConfigurationService;
    private final FlightMovementRepository flightMovementRepository;
    private final ChargesUtility chargesUtility;

    public static final String ARRIVAL = "arrival";
    private static final String DEPARTURE = "departure";
    private static final String START = "start";
    private static final String END = "end";

    AerodromeOperationalHoursService(final AerodromeOperationalHoursRepository aerodromeOperationalHoursRepository,
                                     final SystemConfigurationService systemConfigurationService,
                                     final FlightMovementRepository flightMovementRepository,
                                     @Lazy final ChargesUtility chargesUtility) {
        this.aerodromeOperationalHoursRepository = aerodromeOperationalHoursRepository;
        this.systemConfigurationService = systemConfigurationService;
        this.flightMovementRepository = flightMovementRepository;
        this.chargesUtility = chargesUtility;
    }

    @Transactional(readOnly = true)
    public List<AerodromeOperationalHours> findAll() {
        LOG.debug("Request to get all Aerodrome Operational Hours");
        return aerodromeOperationalHoursRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Page<AerodromeOperationalHours> findAll(final Pageable pageable) {
        LOG.debug("Request to get all Aerodrome Operational Hours");
        return aerodromeOperationalHoursRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AerodromeOperationalHours> findAll(final String textSearch, final Pageable pageable) {
        LOG.debug("Request to get Aerodrome Operational Hours by text search. Search: {}", textSearch);
        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification.Builder().lookFor(textSearch);

        return aerodromeOperationalHoursRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    public AerodromeOperationalHours getOne(final Integer id) {
        LOG.debug("Request to get Aerodrome Operational Hours by id : {}", id);
        return aerodromeOperationalHoursRepository.getOne(id);
    }

    public AerodromeOperationalHours save(final AerodromeOperationalHours aerodromeOperationalHours) {
        LOG.debug("Request to save Aerodrome Operational Hours : {}", aerodromeOperationalHours);
        return aerodromeOperationalHoursRepository.save(aerodromeOperationalHours);
    }

    public void delete(final Integer id) {
        LOG.debug("Request to delete Aerodrome Operational Hours : {}", id);
        aerodromeOperationalHoursRepository.delete(id);
    }

    public AerodromeOperationalHours update(final Integer id, final AerodromeOperationalHours aerodromeOperationalHours) {
        LOG.debug("Request to update Aerodrome Operational Hours : {}", aerodromeOperationalHours);
        try {
            AerodromeOperationalHours existingAerodromeOperationalHours = aerodromeOperationalHoursRepository.getOne(id);
            ModelUtils.merge(aerodromeOperationalHours, existingAerodromeOperationalHours);
            return aerodromeOperationalHoursRepository.save(existingAerodromeOperationalHours);
        } catch (RuntimeException e) {
            throw ExceptionFactory.persistenceDataManagement(e, ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }
    }

    private AerodromeOperationalHours getAerodromeOperationalHoursByAerodromeName(final String name) {
        LOG.debug("Request to get Aerodrome Operational Hours by Aerodrome name: {}", name);
        return aerodromeOperationalHoursRepository.getAerodromeOperationalHoursByAerodromeName(name);
    }

    public int getExtendedAerodromeOperationalHours(final FlightMovement flightMovement,
                                                    final String aerodrome,
                                                    final String aerodromeType,
                                                    final LocalTime flightTime) {
        LOG.debug("Calculating Extended Aerodrome Operational Hours for flight={}, on {}, flightTime={}", flightMovement.getId(), aerodromeType, flightTime);

        int nearestOperationalHours = 0;

        LocalTime depTime;
        if (StringUtils.isNotBlank(flightMovement.getActualDepartureTime())) {
            depTime = DateTimeUtils.convertStringToLocalTime(flightMovement.getActualDepartureTime());
        } else {
            depTime = DateTimeUtils.convertStringToLocalTime(flightMovement.getDepTime());
        }
        LocalDate dateOfFlight = flightMovement.getDateOfFlight().toLocalDate();

        if (flightTime.isBefore(depTime)) {
            dateOfFlight = dateOfFlight.plusDays(1);
        }

        AerodromeOperationalHours aerodromeOperationalHours = getAerodromeOperationalHoursByAerodromeName(aerodrome);
        if (aerodromeOperationalHours != null) {
            boolean isItHolidayDay = checkIfDateOfFlightIsHolidayDay(dateOfFlight, aerodromeOperationalHours);
            nearestOperationalHours = isItHolidayDay
                ? getNearestAerodromeOperationalHours(aerodromeOperationalHours, dateOfFlight, flightTime, checkAerodromeOperationalHolidays(dateOfFlight,
                    aerodromeOperationalHours), flightMovement, aerodrome, aerodromeType)
                : getNearestAerodromeOperationalHours(aerodromeOperationalHours, dateOfFlight, flightTime, null, flightMovement, aerodrome, aerodromeType);
        }
        return nearestOperationalHours;
    }

    private List<AerodromeOperationalDateTime> getAerodromeOperationalDateTimeList(final AerodromeOperationalHours aerodromeOperationalHours,
                                                                                   final LocalDate dateOfFlight,
                                                                                   final String holidayHours) {
        List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList = new ArrayList<>();

        LocalDate beforeDateOfFlight = getDateWithOperationalHoursOutsideDateOfFlight(dateOfFlight, "before", aerodromeOperationalHours);
        LocalDate afterDateOfFlight = getDateWithOperationalHoursOutsideDateOfFlight(dateOfFlight, "after", aerodromeOperationalHours);

        String operationalHoursOnFlightDate = holidayHours != null ? holidayHours : getOperationalHoursByDayOfWeek(dateOfFlight.getDayOfWeek(), aerodromeOperationalHours);
        String operationalHoursOnBeforeDateOfFlight = getOperationalHoursByDayOfWeek(beforeDateOfFlight.getDayOfWeek(), aerodromeOperationalHours);
        String operationalHoursOnAfterDateOfFlight = getOperationalHoursByDayOfWeek(afterDateOfFlight.getDayOfWeek(), aerodromeOperationalHours);

        setAerodromeOperationalDateTime(beforeDateOfFlight, operationalHoursOnBeforeDateOfFlight, aerodromeOperationalDateTimeList);
        setAerodromeOperationalDateTime(dateOfFlight, operationalHoursOnFlightDate, aerodromeOperationalDateTimeList);
        setAerodromeOperationalDateTime(afterDateOfFlight, operationalHoursOnAfterDateOfFlight, aerodromeOperationalDateTimeList);

        return aerodromeOperationalDateTimeList;
    }

    private int getNearestAerodromeOperationalHours(final AerodromeOperationalHours aerodromeOperationalHours,
                                                    final LocalDate dateOfFlight,
                                                    final LocalTime timeOfFlight,
                                                    final String holidayHours,
                                                    final FlightMovement flightMovement,
                                                    final String aerodrome,
                                                    final String aerodromeType) {

        LocalDateTime dateTimeOfFlight = dateOfFlight.atTime(timeOfFlight);
        List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList =
            getAerodromeOperationalDateTimeList(aerodromeOperationalHours, dateOfFlight, holidayHours);

        int nearestAerodromeOperationalHours = calculateNearestAerodromeOperationalHours(aerodromeOperationalDateTimeList, dateTimeOfFlight);

        if (nearestAerodromeOperationalHours > 0 && systemConfigurationService.getBillingOrgCode() == BillingOrgCode.EANA
            && flightMovement.getItem18RegNum() != null) {

            return resolveHoursForMultipleFlights(nearestAerodromeOperationalHours, aerodromeOperationalDateTimeList,
                dateTimeOfFlight, flightMovement, aerodrome, aerodromeType);
        }

        return nearestAerodromeOperationalHours;
    }

    private int calculateNearestAerodromeOperationalHours (final List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList,
                                                           final LocalDateTime dateTimeOfFlight) {
        int nearestAerodromeOperationalHours = 0;

        if (!aerodromeOperationalDateTimeList.isEmpty()) {
            double hours = 0;
            for (AerodromeOperationalDateTime dt: aerodromeOperationalDateTimeList) {
                if (dateTimeOfFlight.isEqual(dt.getStart()) || dateTimeOfFlight.isEqual(dt.getEnd()) ||
                    (dateTimeOfFlight.isAfter(dt.getStart()) && dateTimeOfFlight.isBefore(dt.getEnd()))) {
                    nearestAerodromeOperationalHours = 0;
                    break;
                } else if (dateTimeOfFlight.isBefore(dt.getStart())) {
                    hours = Math.ceil((double)MINUTES.between(dateTimeOfFlight, dt.getStart())/60);
                } else if (dateTimeOfFlight.isAfter(dt.getEnd())) {
                    hours = Math.ceil((double)MINUTES.between(dt.getEnd(), dateTimeOfFlight)/60);
                }

                if (nearestAerodromeOperationalHours == 0 || hours < nearestAerodromeOperationalHours) {
                    nearestAerodromeOperationalHours = (int) hours;
                }
            }
        }
        return nearestAerodromeOperationalHours;
    }

    @SuppressWarnings("squid:S3776")
    private int resolveHoursForMultipleFlights(final int nearestAerodromeOperationalHours,
                                               final List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList,
                                               final LocalDateTime dateTimeOfCalculatedFlight,
                                               final FlightMovement calculatedFlightMovement,
                                               final String calculatedAerodrome,
                                               final String aerodromeType) {

        LocalDateTime closingStartDateTime = getClosingDateTime(aerodromeOperationalDateTimeList, dateTimeOfCalculatedFlight, START);
        LocalDateTime closingEndDateTime = getClosingDateTime(aerodromeOperationalDateTimeList, dateTimeOfCalculatedFlight, END);

        if (closingStartDateTime == null || closingEndDateTime == null) {
            LOG.debug("Cannot obtain aerodrome: {} closing hours", calculatedAerodrome);
            return 0;
        }

        Integer id = calculatedFlightMovement.getId();

        List<FlightMovement> flightMovements =
            flightMovementRepository.findAllByDateOfFlightAndRegNumAndAerodrome(LocalDateTime.of(closingStartDateTime.toLocalDate(), LocalTime.MIN),
                LocalDateTime.of(closingEndDateTime.toLocalDate(), LocalTime.MAX), calculatedFlightMovement.getItem18RegNum(), calculatedAerodrome);

        // if the flight in the list, we need to update it with the calculated flight (aerodromes or flight time can be different)
        // if it's a new flight or updated (for example, was without Reg Num, that is why not in the list) - we need to add it
        OptionalInt indexOpt = IntStream.range(0, flightMovements.size()).filter(i -> Objects.equals(id ,flightMovements.get(i).getId())).findFirst();
        if (indexOpt.isPresent()) {
            flightMovements.set(indexOpt.getAsInt(), calculatedFlightMovement);
        } else {
            flightMovements.add(calculatedFlightMovement);
        }

        flightMovements.sort(Comparator.comparing(FlightMovement::getDateOfFlight).thenComparing(FlightMovement::getDepTime));

        // remove flights that were during opening hours
        flightMovements.removeIf(fm -> removeUnmatchedFlights(fm, calculatedAerodrome, closingStartDateTime, closingEndDateTime));

        if (flightMovements.isEmpty()) {
            return nearestAerodromeOperationalHours;
        }

        FlightMovement firstFm = flightMovements.get(0);
        Integer firstFmId = firstFm.getId();
        String firstFmDepAd = firstFm.getDepAd();
        String firstFmDestAd = firstFm.getDestAd();
        String firstFmDepTime = firstFm.getDepTime();
        LocalDateTime firstFmDateOfFlight = firstFm.getDateOfFlight();
        LocalDateTime firstFmDepDateTime = firstFmDateOfFlight.toLocalDate().atTime(DateTimeUtils.convertStringToLocalTime(firstFmDepTime));

        // if it's only one flight movement and arrival and where destDd == depAd and
        // depAd during opening hours
        // or departure where destDd != depAd -
        // there no match and the flight should be calculated as singe landing/takeoff
        if (flightMovements.size() == 1 && aerodromeType.equals(ARRIVAL) && (!firstFmDepAd.equals(firstFmDestAd) || firstFmDepAd.equals(firstFmDestAd) &&
            isFlightWithinOpenHours(firstFmDepDateTime, closingStartDateTime, closingEndDateTime))
            || (aerodromeType.equals(DEPARTURE) && Objects.equals(firstFmId, id) && !firstFmDepAd.equals(firstFmDestAd))) {
            return nearestAerodromeOperationalHours;

        // if it's departure and
        // calculated flight is the first one and depAd == destAd OR
        // calculated flight is the second one and depAd of second flight == destAd of first flight -
        // the charges should be set on this flight
        } else if (aerodromeType.equals(DEPARTURE) &&
            ((Objects.equals(firstFmId, id) && calculatedAerodrome.equals(firstFmDestAd)) ||
                (flightMovements.size() > 1 && Objects.equals(flightMovements.get(1).getId(), id) && firstFmDestAd.equals(calculatedAerodrome)))){
            FlightMovement matchFm = flightMovements.get(0);

            LocalDateTime dateTimeOfMatchedFlight = getDateTimeOfFlight(matchFm, aerodromeType);

            LocalDateTime firstStep = dateTimeOfCalculatedFlight.isBefore(dateTimeOfMatchedFlight) ? dateTimeOfCalculatedFlight : dateTimeOfMatchedFlight;
            LocalDateTime secondStep = dateTimeOfCalculatedFlight.isBefore(dateTimeOfMatchedFlight) ? dateTimeOfMatchedFlight : dateTimeOfCalculatedFlight;

            return calculateExtendedHoursByFlightLegs(firstStep, secondStep, closingStartDateTime, closingEndDateTime);

        // the charge is only set on the first takeoff flight, for other flights it's = 0 for this aerodrome
        } else {
            return 0;
        }
    }

    private int calculateExtendedHoursByFlightLegs(final LocalDateTime firstStep,
                                                   final LocalDateTime secondStep,
                                                   final LocalDateTime closingStartDateTime,
                                                   final LocalDateTime closingEndDateTime) {

        double flightLeg1 = MINUTES.between(closingStartDateTime.minusMinutes(1), firstStep);
        double flightLeg2 = MINUTES.between(firstStep, secondStep);
        double flightLeg3 = MINUTES.between(secondStep, closingEndDateTime.plusMinutes(1));

        // if all three legs are longer than three hours, calculate the extended hours surcharge
        // based on the second leg (arrival to departure/departure to arrival)
        if (flightLeg1 > 180 && flightLeg2 > 180 && flightLeg3 > 180) {
            return (int) Math.ceil(flightLeg2 / 60);

            // if all three legs are shorter than three hours, calculate the extended hours surcharge
            // based on the third leg (arrival to opening/departure to opening)
        } else if (flightLeg1 < 180 && flightLeg2 < 180 && flightLeg3 < 180) {
            return (int) Math.ceil(flightLeg3 / 60);

            // if one or two legs are shorter than or equal three hours,
            // calculate the extended hours surcharge based on the sum of the legs shorter than three hours
        } else {
            double sum = 0;
            if (flightLeg1 <= 180) {
                sum += flightLeg1;
            }

            if (flightLeg2 <= 180) {
                sum += flightLeg2;
            }

            if (flightLeg3 <= 180) {
                sum += flightLeg3;
            }
            return (int) Math.ceil(sum / 60);
        }
    }

    private LocalDateTime getDateTimeOfFlight(final FlightMovement matchFm,
                                              final String aerodromeType) {
        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(matchFm.getDepTime());
        LocalDate dateOfFlight = matchFm.getDateOfFlight().toLocalDate();

        if (aerodromeType.equals(DEPARTURE)) {
            LocalTime arrivalTime = chargesUtility.deriveTimeOfDayForCharge(matchFm, true);
            if (arrivalTime.isBefore(depTime)) {
                dateOfFlight = dateOfFlight.plusDays(1);
            }
            return dateOfFlight.atTime(arrivalTime);
        } else {
            return dateOfFlight.atTime(depTime);
        }
    }

    private boolean removeUnmatchedFlights(final FlightMovement fm,
                                           final String aerodrome,
                                           final LocalDateTime closingStartDateTime,
                                           final LocalDateTime closingEndDateTime) {

        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(fm.getDepTime());
        LocalDate dateOfFlight = fm.getDateOfFlight().toLocalDate();
        LocalDateTime flightDateTime;

        if (aerodrome.equals(fm.getDestAd())) {
            LocalTime arrivalTime = chargesUtility.deriveTimeOfDayForCharge(fm, true);
            if (arrivalTime.isBefore(depTime)) {
                dateOfFlight = dateOfFlight.plusDays(1);
            }
            flightDateTime = dateOfFlight.atTime(arrivalTime);
            return isFlightWithinOpenHours(flightDateTime, closingStartDateTime, closingEndDateTime);
        } else {
            flightDateTime = dateOfFlight.atTime(depTime);
            return isFlightWithinOpenHours(flightDateTime, closingStartDateTime, closingEndDateTime);
        }
    }

    private boolean isFlightWithinOpenHours(final LocalDateTime flightDateTime,
                                            final LocalDateTime closingStartDateTime,
                                            final LocalDateTime closingEndDateTime) {
        if (flightDateTime == null || closingStartDateTime == null || closingEndDateTime == null) {
            return false;
        }
        return flightDateTime.isBefore(closingStartDateTime) || flightDateTime.isAfter(closingEndDateTime);
    }

    private LocalDateTime getClosingDateTime(final List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList,
                                             final LocalDateTime dateTimeOfFlight,
                                             final String startEnd) {
        LocalDateTime dateTime = null;
        for (AerodromeOperationalDateTime dt: aerodromeOperationalDateTimeList) {
            if (startEnd.equals(START) && dt.getEnd().isBefore(dateTimeOfFlight)) {
                dateTime = dateTime == null || dt.getEnd().isAfter(dateTime) ? dt.getEnd().plusMinutes(1) : dateTime;

            } else if (startEnd.equals(END) && dt.getStart().isAfter(dateTimeOfFlight)){
                dateTime = dateTime == null || dt.getStart().isBefore(dateTime) ? dt.getStart().minusMinutes(1) : dateTime;
            }
        }
        return dateTime;
    }

    private void setAerodromeOperationalDateTime(LocalDate dateOfFlight,
                                                 String operationalHoursOnFlightDate,
                                                 List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList) {

        if (dateOfFlight != null && operationalHoursOnFlightDate != null) {
            List<String> operationalHours = Arrays.asList(operationalHoursOnFlightDate.split(";"));
            operationalHours.forEach(h -> {
                String[] hours = h.split("-");
                if (isValidTime(hours[0]) && isValidTime(hours[1])) {
                    LocalTime start = LocalTime.parse(hours[0], DateTimeFormatter.ofPattern("HHmm"));
                    LocalTime end = LocalTime.parse(hours[1], DateTimeFormatter.ofPattern("HHmm"));

                    AerodromeOperationalDateTime dateTime = new AerodromeOperationalDateTime(dateOfFlight.atTime(start), dateOfFlight.atTime(end));
                    aerodromeOperationalDateTimeList.add(dateTime);
                }
            });
        }
    }

    private boolean isValidTime(final String time) {
        boolean isValid = false;
        if (time != null) {
            try {
                LocalTime.parse(time, DateTimeFormatter.ofPattern("HHmm"));
                isValid = true;
            } catch (DateTimeParseException dtpe) {
                // Do nothing
            }
        }
        return isValid;
    }


    private String getOperationalHoursByDayOfWeek(DayOfWeek dayOfWeek, AerodromeOperationalHours aerodromeOperationalHours) {
        String operationalHours;
        if (dayOfWeek.equals(DayOfWeek.MONDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursMonday();
        } else if (dayOfWeek.equals(DayOfWeek.TUESDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursTuesday();
        } else if (dayOfWeek.equals(DayOfWeek.WEDNESDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursWednesday();
        } else if (dayOfWeek.equals(DayOfWeek.THURSDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursThursday();
        } else if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursFriday();
        } else if (dayOfWeek.equals(DayOfWeek.SATURDAY)) {
            operationalHours = aerodromeOperationalHours.getOperationalHoursSaturday();
        } else {
            operationalHours = aerodromeOperationalHours.getOperationalHoursSunday();
        }
        return operationalHours;
    }

    private LocalDate getDateWithOperationalHoursOutsideDateOfFlight(LocalDate dateOfFlight,
                                                                     String period,
                                                                     AerodromeOperationalHours aerodromeOperationalHours) {
        final String before = "before";
        LocalDate date = null;
        long num = period.equals(before) ? -1 : 1;

        while (date == null || num < -7 || num > 7) {
            if (getOperationalHoursByDayOfWeek(dateOfFlight.plusDays(num).getDayOfWeek(), aerodromeOperationalHours) != null) {
                date = dateOfFlight.plusDays(num);
            }
            num += period.equals(before) ? -1 : 1;
        }
        return date;
    }

    private Boolean checkIfDateOfFlightIsHolidayDay(LocalDate dateOfFlight, AerodromeOperationalHours aerodromeOperationalHours) {
        String date = DateTimeFormatter.ofPattern("MM/dd").format(dateOfFlight);
        boolean holidays1 = false;
        boolean holidays2 = false;
        if (aerodromeOperationalHours.getHolidayDatesHolidays1() != null) {
            holidays1 = aerodromeOperationalHours.getHolidayDatesHolidays1().contains(date);
        }
        if (aerodromeOperationalHours.getHolidayDatesHolidays2() != null) {
            holidays2 = aerodromeOperationalHours.getHolidayDatesHolidays2().contains(date);
        }
        return holidays1 || holidays2;
    }

    private String checkAerodromeOperationalHolidays (LocalDate dateOfFlight, AerodromeOperationalHours aerodromeOperationalHours) {
        String date = DateTimeFormatter.ofPattern("MM/dd").format(dateOfFlight);
        String hours = null;
        if (aerodromeOperationalHours.getHolidayDatesHolidays1() != null && aerodromeOperationalHours.getHolidayDatesHolidays1().contains(date)) {
            hours =  aerodromeOperationalHours.getOperationalHoursHolidays1();
        }
        if (aerodromeOperationalHours.getHolidayDatesHolidays2() != null && aerodromeOperationalHours.getHolidayDatesHolidays2().contains(date) && hours == null) {
            hours = aerodromeOperationalHours.getOperationalHoursHolidays2();
        }
        return hours;
    }

    public List<FlightMovement> getListFlightMovementsForRecalculation(FlightMovement flightMovement, String type) {
        List<FlightMovement> list = new ArrayList<>();

        if (flightMovement.getItem18RegNum() == null) {
            return list;
        }

        String aerodrome = type.equals(DEPARTURE) ? flightMovement.getDepAd() : chargesUtility.resolveArrivalAerodrome(flightMovement);
        LocalTime depTime = DateTimeUtils.convertStringToLocalTime(flightMovement.getDepTime());
        LocalDate dateOfFlight = flightMovement.getDateOfFlight().toLocalDate();
        LocalTime flightTime = type.equals(DEPARTURE) ? chargesUtility.deriveDepartureTimeOfDayForCharge(flightMovement) :
            chargesUtility.deriveTimeOfDayForCharge(flightMovement, true);

        if (flightTime.isBefore(depTime)) {
            dateOfFlight = dateOfFlight.plusDays(1);
        }

        LocalDateTime dateTimeOfFlight = dateOfFlight.atTime(flightTime);


        AerodromeOperationalHours aerodromeOperationalHours = getAerodromeOperationalHoursByAerodromeName(aerodrome);
        if (aerodromeOperationalHours == null) {
            return list;
        }

        boolean isItHolidayDay = checkIfDateOfFlightIsHolidayDay(dateOfFlight, aerodromeOperationalHours);
        List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList =
            getAerodromeOperationalDateTimeList(aerodromeOperationalHours, dateOfFlight,
                isItHolidayDay ? checkAerodromeOperationalHolidays(dateOfFlight, aerodromeOperationalHours) : null);

        if (aerodromeOperationalDateTimeList.isEmpty()) {
            return list;
        }

        boolean flightDuringOpenHours = flightDuringOpenHours(aerodromeOperationalDateTimeList, dateTimeOfFlight);
        if (!flightDuringOpenHours) {
            LocalDateTime closingStartDateTime = getClosingDateTime(aerodromeOperationalDateTimeList, dateTimeOfFlight, START);
            LocalDateTime closingEndDateTime = getClosingDateTime(aerodromeOperationalDateTimeList, dateTimeOfFlight, END);

            if (closingStartDateTime == null || closingEndDateTime == null) {
                LOG.debug("Cannot obtain aerodrome: {} closing hours", aerodrome);
                return list;
            }

            list = flightMovementRepository.findAllByDateOfFlightAndRegNumAndAerodrome(LocalDateTime.of(closingStartDateTime.toLocalDate(),
                LocalTime.MIN), LocalDateTime.of(closingEndDateTime.toLocalDate(), LocalTime.MAX), flightMovement.getItem18RegNum(), aerodrome);
            // remove flights that were during opening hours
            list.removeIf(fm -> removeUnmatchedFlights(fm, aerodrome, closingStartDateTime, closingEndDateTime));
        }

        return list;
    }

    private boolean flightDuringOpenHours(final List<AerodromeOperationalDateTime> aerodromeOperationalDateTimeList,
                                          final LocalDateTime dateTimeOfFlight){

        boolean flightDuringOpenHours = false;
        for (AerodromeOperationalDateTime dt: aerodromeOperationalDateTimeList) {
            if (dateTimeOfFlight.isEqual(dt.getStart()) || dateTimeOfFlight.isEqual(dt.getEnd()) ||
                (dateTimeOfFlight.isAfter(dt.getStart()) && dateTimeOfFlight.isBefore(dt.getEnd()))) {
                flightDuringOpenHours = true;
                break;
            }
        }
        return flightDuringOpenHours;
    }

    public long countAll() {
        return aerodromeOperationalHoursRepository.count();
    }
}
