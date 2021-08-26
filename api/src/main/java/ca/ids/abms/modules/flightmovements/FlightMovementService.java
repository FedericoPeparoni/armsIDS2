package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.db.Filter;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.db.JoinFilter;
import ca.ids.abms.config.error.*;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aerodromeoperationalhours.AerodromeOperationalHoursService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.aircraft.AircraftTypeService;
import ca.ids.abms.modules.atcmovements.AtcMovementLog;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.charges.PassengerServiceChargeReturn;
import ca.ids.abms.modules.common.dto.ItemLoaderObserver;
import ca.ids.abms.modules.common.enumerators.ItemLoaderResult;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
import ca.ids.abms.modules.currencies.Currency;
import ca.ids.abms.modules.exemptions.FlightNotesUtility;
import ca.ids.abms.modules.flightmovements.enumerate.*;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilder;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementMerge;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementValidator;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementBuilderIssue;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.flightmovementsbuilder.utility.FlightMovementBuilderUtility;
import ca.ids.abms.modules.flightmovementsbuilder.utility.Item18Parser;
import ca.ids.abms.modules.flightmovementsbuilder.utility.ThruFlightPlanUtility;
import ca.ids.abms.modules.flightmovementsbuilder.vo.ThruFlightPlanVO;
import ca.ids.abms.modules.plugins.PluginService;
import ca.ids.abms.modules.radarsummary.RadarSummary;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.routesegments.RouteSegment;
import ca.ids.abms.modules.routesegments.SegmentType;
import ca.ids.abms.modules.spatiareader.dto.FplObjectDto;
import ca.ids.abms.modules.system.BillingOrgCode;
import ca.ids.abms.modules.system.SystemConfiguration;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.towermovements.TowerMovementLog;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionService;
import ca.ids.abms.modules.translation.Translation;
import ca.ids.abms.modules.util.models.ApplicationConstants;
import ca.ids.abms.modules.util.models.CurrencyUtils;
import ca.ids.abms.modules.util.models.DateTimeUtils;
import ca.ids.abms.modules.util.models.WhitelistingUtils;
import ca.ids.abms.plugins.PluginKey;
import ca.ids.abms.util.billingcontext.BillingContext;
import ca.ids.abms.util.billingcontext.BillingContextKey;
import ca.ids.abms.util.converter.JSR310DateConverters;
import com.google.common.base.Preconditions;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ca.ids.abms.util.MiscUtils.nvl;

/**
 * FlightMovementService
 */
@Service
@Transactional
public class FlightMovementService {

    //https://stackoverflow.com/questions/54147047/how-spring-data-clean-persited-entities-in-transactional-method
    //Spring will ensure this entityManager is the same as the one that start transaction due to  @Transactional
    @PersistenceContext
    private EntityManager em;

    private static final String KEY_FLIGHT_MOVEMENT_CATEGORY = "flightmovementCategory";

    private static final String KEY_STATUS = "status";

    private static final String KEY_DATE_OF_FLIGHT = "dateOfFlight";

    private static final String KEY_BILLING_DATE = "billingDate";

    private static final String KEY_RESOLUTION_ERRORS = "resolutionErrors";

    private static final String KEY_ENROUTE_CHARGES_STATUS = "enrouteChargesStatus";

    private static final String KEY_PASSENGER_CHARGES_STATUS = "passengerChargesStatus";

    private static final String KEY_OTHER_CHARGES_STATUS = "otherChargesStatus";

    private static final String IATA = "iataMember";

    private static final String KEY_FLIGHT_ID = "flightId";

    private static final String KEY_ITEM18_REG_NUM = "item18RegNum";

    private static final String KEY_SPATIA_FPL_OBJECT_ID = "spatiaFplObjectId";

    private static final String KEY_ID = "id";

    private static final String KEY_NAME = "name";

    private static final String KEY_ACCOUNT = "account";

    private static final String KEY_BILLING_CENTER = "billingCenter";

    private static final String MSG_IGNORE_SURVEILLANCE_LOG = "Ignoring surveillance log for already INVOICED flight movement: {}";

    private static final String ARRIVAL = "arrival";

    private static final String DEPARTURE  = "departure";

    private static final String STATE = "STATE";

    private static final String AMBULANCE = "AMBULANCE";

    private String jSessionId = "";

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementService.class);

    private final FlightMovementRepository flightMovementRepository;
    private final FlightMovementAerodromeService flightMovementAerodromeService;
    private final AircraftTypeService aircraftTypeService;
    private final FlightMovementBuilder flightMovementBuilder;
    private final FlightMovementValidator flightMovementValidator;
    private final FlightMovementBuilderUtility flightMovementBuilderUtility;
    private final FlightMovementMerge flightMovementMerge;
    private final SystemConfigurationService systemConfigurationService;
    private final ThruFlightPlanUtility thruFlightPlanUtility;
    private final FlightMovementRepositoryUtility flightMovementRepositoryUtility;
    private final AerodromeOperationalHoursService aerodromeOperationalHoursService;
    private final CurrencyUtils currencyUtils;
    private final TransactionService transactionService;
    private final WhitelistingUtils whitelistingUtils;
    private final PluginService pluginService;
    private HttpClient httpclient = null;
    private final AircraftRegistrationService aircraftRegistrationService;

    @SuppressWarnings("squid:S00107")
    public FlightMovementService(final FlightMovementRepository flightMovementRepository,
                                 final FlightMovementAerodromeService flightMovementAerodromeService,
                                 final AircraftTypeService aircraftTypeService,
                                 final FlightMovementBuilder flightMovementBuilder,
                                 final FlightMovementValidator flightMovementValidator,
                                 final FlightMovementBuilderUtility flightMovementBuilderUtility,
                                 final ThruFlightPlanUtility thruFlightPlanUtility,
                                 final SystemConfigurationService systemConfigurationService,
                                 final FlightMovementMerge flightMovementMerge,
                                 final FlightMovementRepositoryUtility flightMovementRepositoryUtility,
                                 final AerodromeOperationalHoursService aerodromeOperationalHoursService,
                                 final CurrencyUtils currencyUtils,
                                 final TransactionService transactionService,
                                 final WhitelistingUtils whitelistingUtils,
                                 final PluginService pluginService,
                                 final AircraftRegistrationService aircraftRegistrationService) {
        this.flightMovementRepository = flightMovementRepository;
        this.flightMovementAerodromeService = flightMovementAerodromeService;
        this.aircraftTypeService = aircraftTypeService;
        this.flightMovementBuilder = flightMovementBuilder;
        this.flightMovementValidator = flightMovementValidator;
        this.flightMovementBuilderUtility = flightMovementBuilderUtility;
        this.thruFlightPlanUtility = thruFlightPlanUtility;
        this.systemConfigurationService = systemConfigurationService;
        this.flightMovementMerge = flightMovementMerge;
        this.flightMovementRepositoryUtility = flightMovementRepositoryUtility;
        this.aerodromeOperationalHoursService = aerodromeOperationalHoursService;
        this.currencyUtils = currencyUtils;
        this.transactionService = transactionService;
        this.whitelistingUtils = whitelistingUtils;
        this.pluginService = pluginService;
        this.aircraftRegistrationService = aircraftRegistrationService;
    }

    public SystemConfigurationService getSystemConfigurationService() {
        return systemConfigurationService;
    }

    @Transactional(readOnly = true)
    public FlightMovement findFlightMovementById(final Integer id){
        return flightMovementRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<FlightMovement> findFlightMovementsByIds(final List<Integer> ids) {
        return flightMovementRepository.findAllByIdIn(ids);
    }

    @Transactional
    public FlightMovement reconcileFlightMovementById(final Integer id) throws FlightMovementBuilderException{
        final FlightMovement flightMovement = flightMovementRepository.findOne(id);
        if (flightMovement != null) {
            return updateFlightMovementFromJobs(id);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public FlightMovementStatus getFlightMovementStatusById(final Integer id){
        FlightMovementStatus status=null;
        FlightMovement flightMovement= flightMovementRepository.findOne(id);
        if(flightMovement!=null){
            status=flightMovement.getStatus();
        }
        return status;
    }

    @Transactional(readOnly = true)
    public Page<FlightMovement> getAllFlightMovement(final Pageable pageable, final String textSearch){
        return  flightMovementRepository.findAll(pageable, textSearch);
    }

    @Transactional(readOnly = true)
    public Page<FlightMovement> findAllFlightMovementByStatus(final String status, final Pageable pageable) {
        FlightMovementStatus st = FlightMovementStatus.forValue(status);
        return flightMovementRepository.findAllByStatus(pageable, st);
    }

    /**
     * Returns the aircraft type by the latest registration number
     * @param   registrationNumber    registration number of a flight movement
     * @return  {AircraftType}
     */
    public AircraftType getAircraftTypeByLatestRegistrationNumber(String registrationNumber){
        final FlightMovement flightMovement = flightMovementRepository.findTop1ByItem18RegNumOrderByIdDesc(registrationNumber);

        AircraftType aircraftTypeResult = null;

        if (flightMovement != null) {
            String aircraftType = flightMovement.getAircraftType();
            aircraftTypeResult = aircraftTypeService.findByAircraftType(aircraftType);
        }

        return aircraftTypeResult;
    }

    @Deprecated
    @Transactional(readOnly = true)
    public Page<FlightMovement> findAllFlightMovementByIssue(final Pageable pageable, final String issue){

        Page<FlightMovement> issueFlightMovementList=null;
        if(issue!=null){
            issueFlightMovementList=flightMovementRepository.findAllByResolutionErrorsContaining(issue, pageable);
        }
        return  issueFlightMovementList;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("squid:S00107")
    public Page<FlightMovement> findAllFlightMovementByFilter(Pageable pageable, String textSearch, Integer flightMovementCategoryId, String status,
                                                              Boolean iata, String issue, String invoiceStatus, LocalDate startDate, LocalDate endDate, Boolean duplicatesOrMissing) {
        final FlightMovementStatus flightMovementStatus = FlightMovementStatus.forValue(status);

        final FiltersSpecification.Builder filterBuilder = new FiltersSpecification
            .Builder()
            .lookFor(textSearch);

        if (invoiceStatus != null) {
            String[] charge = invoiceStatus.split("_");

            String key;
            switch (charge[0]) {
                case "ENROUTE": key = KEY_ENROUTE_CHARGES_STATUS; break;
                case "PASSENGER": key = KEY_PASSENGER_CHARGES_STATUS; break;
                case "OTHER": key = KEY_OTHER_CHARGES_STATUS; break;
                default: key = null;
            }

            filterBuilder.restrictOn(Filter.equals(key, FlightMovementChargesStatus.valueOf(charge[1])));
        }

        if (flightMovementCategoryId != null) {
            filterBuilder.restrictOn(JoinFilter.equal(KEY_FLIGHT_MOVEMENT_CATEGORY, KEY_ID, flightMovementCategoryId));
        }
        if (flightMovementStatus != null) {
            if (flightMovementStatus.equals(FlightMovementStatus.ACTIVE)) {
                filterBuilder.restrictOn(Filter.orEqual(KEY_STATUS, FlightMovementStatus.PENDING, FlightMovementStatus.INCOMPLETE));
            } else if (flightMovementStatus.equals(FlightMovementStatus.INVPAID)) {
                filterBuilder.restrictOn(Filter.orEqual(KEY_STATUS, FlightMovementStatus.INVOICED, FlightMovementStatus.PAID));
            } else if (flightMovementStatus.equals(FlightMovementStatus.REJECTED)) {
                filterBuilder.restrictOn(Filter.orEqual(KEY_STATUS, FlightMovementStatus.CANCELED, FlightMovementStatus.DELETED));
            } else {
                filterBuilder.restrictOn(Filter.equals(KEY_STATUS, flightMovementStatus));
            }
        }
        if (duplicatesOrMissing != null && duplicatesOrMissing && flightMovementStatus == null) {
            filterBuilder.restrictOn(Filter.notEqual(KEY_STATUS, FlightMovementStatus.CANCELED));
            filterBuilder.restrictOn(Filter.notEqual(KEY_STATUS, FlightMovementStatus.DELETED));
            filterBuilder.restrictOn(Filter.notEqual(KEY_STATUS, FlightMovementStatus.DECLINED));
        }
        if (iata != null) {
            filterBuilder.restrictOn(JoinFilter.equal(KEY_ACCOUNT, IATA, iata));
        }
        if (startDate != null || endDate != null) {
            LocalDateTime startAt;
            LocalDateTime endAt;
            if (startDate == null) {
                startAt = (LocalDateTime.now()).minusYears(1000);
            } else {
                startAt = startDate.atStartOfDay();
            }
            if (endDate == null) {
                endAt = (LocalDateTime.now()).plusYears(1000);
            } else {
                endAt = endDate.atTime(LocalTime.MAX);
            }
            filterBuilder.restrictOn(Filter.included(KEY_DATE_OF_FLIGHT, startAt, endAt));
        }
        if (org.apache.commons.lang.StringUtils.isNotBlank(issue)) {
            if (!issue.equals(FlightMovementValidatorIssue.ALL.toValue())) {
                filterBuilder.restrictOn(Filter.like(KEY_RESOLUTION_ERRORS, issue));
            } else {
                filterBuilder.restrictOn(Filter.notEqual(KEY_RESOLUTION_ERRORS, StringUtils.EMPTY));
            }
        }
        return flightMovementRepository.findAll(filterBuilder.build(), pageable);
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("squid:S00107")
    public Page<FlightMovement> findAllFlightMovementByFilterForDownload(Pageable pageable, String textSearch, Integer flightMovementCategoryId, String status,
                                                              Boolean iata, String issue, String invoiceStatus, LocalDate startDate, LocalDate endDate, Boolean duplicatesOrMissing) {

        Page<FlightMovement> flightMovementPage =  findAllFlightMovementByFilter(pageable, textSearch, flightMovementCategoryId, status,
            iata, issue, invoiceStatus, startDate, endDate, duplicatesOrMissing);
        /*****************************
         The reason to flush is send the update SQL to DB .
         Otherwise ,the update will lost if we clear the entity manager
         afterward.
         ******************************/
        em.flush();
        em.clear();
        return flightMovementPage;
    }

    /**
     * Delete flight movement from UI
     */
    public void deleteFlightMovementFromUI(final Integer id, final String notes, final boolean voidedCashAviationInvoice){

        // flight movement id must NOT be null
        if (id == null)
            return;

        // get flight movement by id
        FlightMovement flightMovement = flightMovementRepository.findOne(id);

        // if null, no flight movement exists
        if (flightMovement == null) {
            throw new CustomParametrizedException("Invalid flight movement");
        }

        // Only incomplete or pending may be deleted
        if (!(flightMovement.getStatus().equals(FlightMovementStatus.INCOMPLETE) || flightMovement.getStatus().equals(FlightMovementStatus.PENDING) ||
            (flightMovement.getStatus().equals(FlightMovementStatus.INVOICED) && voidedCashAviationInvoice))) {
            throw new CustomParametrizedException("Only INCOMPLETE or PENDING flight movements may be deleted");
        }

        if (notes == null || notes.isEmpty()) {
            throw new CustomParametrizedException("Please provide notes", "notes");
        }

        String deletingReason = String.format("Deleted: %s; ", notes);
        if (flightMovement.getStatusNotes() != null && !flightMovement.getStatusNotes().isEmpty()) {
            boolean hasSemicolon = flightMovement.getStatusNotes().trim().endsWith(";");
            deletingReason = String.format("%s%s%s", flightMovement.getStatusNotes(), hasSemicolon ? "" : "; ", deletingReason);
        }

        flightMovement.setStatusNotes(deletingReason.substring(0, Math.min(deletingReason.length(), 300)));
        flightMovement.setStatus(FlightMovementStatus.DELETED);

        // update flight movement
        flightMovementRepository.save(flightMovement);

        // run methods after flight movement is created/updated
        postCreateUpdate(flightMovement, null);
    }

    public void deleteFlightMovement(final Integer id){

        // flight movement id must NOT be null
        if (id == null)
            return;

        // get flight movement by id
        FlightMovement flightMovement = flightMovementRepository.findOne(id);

        // if null, no flight movement exists
        if (flightMovement == null)
            return;

        // delete flight movement by id
        flightMovementRepository.delete(id);

        // run methods after flight movement is created/updated
        postCreateUpdate(flightMovement, null);
    }

    @Transactional(readOnly = true)
    public FlightMovement findFlightMovementByFlightId(String flightId){
        return flightMovementRepository.findByFlightId(flightId);
    }

    @Transactional(readOnly = true)
    public FlightMovementValidationViewModel validateFlightMovementByID(Integer flightMovementID){
        LOG.debug("Validation a Flight Movement: {}", flightMovementID);
        final FlightMovement existingFlightMovement=flightMovementRepository.findOne(flightMovementID);
        FlightMovementValidationViewModel flightMovementValidationViewModel=null;
        if(existingFlightMovement!=null){
            flightMovementValidationViewModel=flightMovementValidator.validateFlightMovementStatus(existingFlightMovement,false);
        }
        return flightMovementValidationViewModel;
    }

    public FlightMovementValidationViewModel getValidableFlightMovementModel(FlightMovement fm) {
        FlightMovementValidationViewModel flightMovementValidationViewModel = new FlightMovementValidationViewModel();
        flightMovementValidationViewModel.setFlightMovementID(fm.getId());
        flightMovementValidationViewModel.setFlightId(fm.getFlightId());
        flightMovementValidationViewModel.setDayOfFlight(fm.getDateOfFlight());
        flightMovementValidationViewModel.setDepartureTime(fm.getDepTime());
        flightMovementValidationViewModel.setRegistration(fm.getItem18RegNum());
        flightMovementValidationViewModel.setStatus(fm.getStatus());
        flightMovementValidationViewModel.setIssuesString(fm.getResolutionErrors());
        if (fm.getAccount() != null) {
            flightMovementValidationViewModel.setAccountName(fm.getAccount().getName());
        }
        return flightMovementValidationViewModel;
    }

    /**
     * Recalculate charges for a list of flight movement IDs.
     * The method returns two lists: successfully and unsuccessfully calculated flight movement IDs.
     *
     * @param id flight movement identification number
     * @return Boolean
     */
    public Boolean calculateCharges(Integer id){
        LOG.debug("Calculate charges for a Flight Movement");
        Boolean returnValue = Boolean.FALSE;
        FlightMovement flightMovement = findFlightMovementById(id);
        if(flightMovement!=null && flightMovement.getId()!=null){
            //charges are automatically calculated in FlightMovementBuilder
            returnValue= flightMovementBuilder.recalculateCharges(flightMovement);
            doPersistFlightMovement(flightMovement);
        }else{
            LOG.error("Charges couldn't be re-calculated");
        }
        return returnValue;
    }

    @Transactional(readOnly = true)
    public List<FlightMovement> findAllFlightMovementByAccount(Integer accountID) {
        List<FlightMovement> flightMovements = null;
        if(accountID != null) {
            flightMovements = flightMovementRepository.findAllByAccount(accountID);
        }
        return flightMovements;
    }

    @Transactional(readOnly = true)
    public List<FlightMovement> findAllFlightMovementByAccountAndDate(Integer accountID, String status, LocalDateTime startInterval, LocalDateTime endInterval){
        List<FlightMovement> flightMovements=null;
        if(accountID!=null && startInterval!=null && endInterval!=null && startInterval.isBefore(endInterval)){
            flightMovements=flightMovementRepository.findAllByAccountIntervalDate(accountID,status,JSR310DateConverters.convertLocalDateTimeToDate(startInterval),JSR310DateConverters.convertLocalDateTimeToDate(endInterval));
        }
        return flightMovements;
    }

    @Transactional(readOnly = true)
    public List<FlightMovement> findAllByAssociatedBillingLedgerId(Integer invoiceId) {
        LOG.debug("Finding Flight Movements from Invoice Id {}", invoiceId);
        return flightMovementRepository.findAllByAssociatedBillingLedgerId(invoiceId);
    }

    @Transactional(readOnly = true)
    public Page<FlightMovement> findAllByAssociatedBillingLedgerId(Integer invoiceId, Pageable pageable) {
        LOG.debug("Finding Flight Movements from Invoice Id {}", invoiceId);
        return flightMovementRepository.findAllByAssociatedBillingLedgerId(invoiceId, pageable);
    }

    public FlightMovement getFlightMovementByLogicalKey(final String flightId,
                                                         final LocalDateTime dateOfFlight,
                                                         final String depTime,
                                                         final String depAd,
                                                         final String estimatedElapsedTime) {
        final List <FlightMovement> list = doFindAllByLogicalKey(flightId, dateOfFlight, depTime, depAd, estimatedElapsedTime);
        if (list == null || list.isEmpty()) {
            return null;
        }
        final FlightMovementLogicalKey logicalKey = new FlightMovementLogicalKey (flightId, depAd, dateOfFlight, depTime);
        // First look for the one that is not CANCELED/DELETED/DECLINED
        FlightMovement result = list.stream()
                .filter (x->x.getStatus() != FlightMovementStatus.CANCELED && x.getStatus() != FlightMovementStatus.DELETED && x.getStatus() != FlightMovementStatus.DECLINED)
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // Otherwise, look for one that matches logical key exactly
        result = list.stream()
                .filter (x->Objects.equals (x.getLogicalKey(), logicalKey))
                .findFirst().orElse(null);
        if (result != null) {
            return result;
        }
        // Otherwise, just return the first one
        return list.get(0);
    }

    /**
     * Werner, Helen and Carmine discuss on February 23 2017.
     * LogicalKey is
     * FlightID  - Mandatory
     * DepAd    - Mandatory
     * DayOfFlight  - Mandatory
     * DepTime - Mandatory (check if we have a DepartureTimeRange )
     *
     * @param flightId flight movement flight id
     * @param dateOfFlight flight movement date of flight
     * @param depTime flight movement departure time
     * @param depAd flight movement departure aerodrome
     * @return list of flight movements found
     */
    @Transactional(readOnly = true)
    public List<FlightMovement> findAllByLogicalKey( final String flightId,
            final LocalDateTime dateOfFlight, final String depTime, final String depAd) {
        return doFindAllByLogicalKey (flightId, dateOfFlight, depTime, depAd, "0000");
    }

    /**
     * Werner, Helen and Carmine discuss on February 23 2017.
     * LogicalKey is
     * FlightID  - Mandatory
     * DepAd    - Mandatory
     * DayOfFlight  - Mandatory
     * DepTime - Mandatory (check if we have a DepartureTimeRange )
     *
     * TFS US 82974 Modified matching logic on March 27, 2018.
     * EstimatedElapsedTime - Optional (match prior flights by percentage of EET)
     *
     * @param flightId flight movement flight id
     * @param dateOfFlight flight movement date of flight
     * @param departureTime flight movement departure time
     * @param departureAerodrome flight movement departure aerodrome
     * @param estimatedElapsedTime flight movement estimated elapsed time
     * @return list of flight movements found
     */
    private List<FlightMovement> doFindAllByLogicalKey(final String flightId,
                                                       final LocalDateTime dateOfFlight,
                                                       final String departureTime,
                                                       final String departureAerodrome,
                                                       final String estimatedElapsedTime) {
        LOG.debug("Finding Flight Movements from LogicalKey flighId: {}, dateOfFlight: {}, depTime: {}, depAd: {} ",
            flightId, dateOfFlight, departureTime, departureAerodrome);

        List<FlightMovement> flightMovements = new ArrayList<>();
        if (flightId != null && dateOfFlight != null && departureTime != null && departureAerodrome != null) {

            final Integer configPrctValue = getDepTimeRangeConfig(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE);
            final Integer configMinValue = getDepTimeRangeConfig(SystemConfigurationItemName.DEP_TIME_RANGE_MIN);

            // ensure both departure time and and estimated elapsed time are 4 digits long if defined
            // default estimated elapsed time to "0000" if not defined
            String depTime = StringUtils.isBlank(departureTime) ? departureTime
                : StringUtils.leftPad(departureTime, 4, '0');
            String eet = StringUtils.isBlank(estimatedElapsedTime) ? "0000"
                : StringUtils.leftPad(estimatedElapsedTime, 4, '0');

            if (configPrctValue != null && configPrctValue > 0 && configMinValue != null && configMinValue > 0) {
                flightMovements = flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(
                    flightId, departureAerodrome, dateOfFlight.toLocalDate(), depTime, eet, configPrctValue, configMinValue);
            } else {
                flightMovements = flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTimeAndDepAd(
                    orderByDateOfFlightAndDepTime(), flightId, dateOfFlight, depTime, departureAerodrome);
            }
        }
        return flightMovements;
    }

    @Transactional(readOnly = true)
    public Page <FlightMovement> findForCronosByDateOfFlight (final Pageable pageable, final LocalDate dateOfFlight, final String flightId, final String regNum) {
        Preconditions.checkNotNull (dateOfFlight);

        // See also: https://docs.spring.io/spring-data/jpa/docs/1.10.3.RELEASE/reference/html/#specifications
        return this.flightMovementRepository.findAll((root, query, criteriaBuilder)->{
            final List <Predicate> andList = new ArrayList<>();

            // Required parameter [dateOfFlight = ...]
            andList.add (criteriaBuilder.equal(root.get (KEY_DATE_OF_FLIGHT), LocalDateTime.of (dateOfFlight, LocalTime.MIN)));

            // Optional parameter [flightId = ...]
            final String flightIdNormalized = StringUtils.trimToNull (flightId);
            if (flightIdNormalized != null) {
                andList.add (criteriaBuilder.equal(root.get (KEY_FLIGHT_ID), flightIdNormalized));
            }

            // Optional parameter: [item18RegNum = ...]
            final String regNumNormalized = StringUtils.trimToNull (regNum);
            if (regNumNormalized != null) {
                andList.add (criteriaBuilder.equal(root.get (KEY_ITEM18_REG_NUM), regNumNormalized));
            }

            // spatiaFplObjectId is null
            andList.add (criteriaBuilder.isNull (root.get (KEY_SPATIA_FPL_OBJECT_ID)));

            // status in ('INVOICED', 'PAID')
            andList.add (criteriaBuilder.in (root.get (KEY_STATUS))
                    .value(FlightMovementStatus.INVOICED)
                    .value(FlightMovementStatus.PAID));

            // Combine all predicates
            return criteriaBuilder.and (andList.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional(readOnly = true)
    public Page <FlightMovement> findForCronosByInvoiceNum (final Pageable pageable, final String invoiceNum) {
        return this.flightMovementRepository.findForCronosByInvoiceNum (pageable, invoiceNum);
    }

    /**
     * Werner, Helen and Carmine discuss on February 23 2017.
     * LogicalKey is
     * Radar.FlightID = FlightMovement.FlightID  - Mandatory
     * Radar.DepAd = FlightMovement.DepAd        - Mandatory
     * Radar.DayOfFlight = FlightMovement.DayOfFlight  - Mandatory
     * Radar.DepTime = FlightMovement.DepTime - Mandatory
     *
     * @param radarSummary radar summary
     * @return flight movement found
     */
    FlightMovement findFlightMovementFromRadarSummary(RadarSummary radarSummary) {
        if(radarSummary == null) {
            throw new IllegalArgumentException("Invalid RadarSummary ");
        }

        final String flightId = radarSummary.getFlightIdentifier();
        final String regNum = radarSummary.getRegistration();
        final String depTime = radarSummary.getDepartureTime();
        final String depAd = radarSummary.getDepartureAeroDrome();
        final String destAd = radarSummary.getDestinationAeroDrome();
        final String aircraftType = radarSummary.getAircraftType();
        LocalDateTime date = radarSummary.getDayOfFlight();
        if (date == null) {
            date = radarSummary.getDate();  // the date of contact if day of flight has not been calculated
        }

        LOG.debug("Finding Flight Movements from RadarSummary by  flightID: {}, depAd: {}, dateOfFlight: {}, depTime: {}",
            flightId,depAd,date,depTime);

        FlightMovement flightMovement = null;
        if (StringUtils.isNotEmpty(flightId) && date != null && StringUtils.isNotEmpty(depTime) && StringUtils.isNotEmpty(depAd)) {
            String eet = flightMovementBuilderUtility.guessEstimatedElapsedTime(depAd, destAd, flightId, regNum, aircraftType, date.toLocalDate());
            flightMovement = this.getFlightMovementByLogicalKey(flightId, date, depTime, depAd, eet != null ? eet : "0000");
        } else {
            LOG.warn("{} flightID: {}, depAd: {}, date: {}, depTime: {}", FlightMovementBuilderIssue.ORPHAN_ITEM,
                flightId, depAd, date, depTime);
        }
        return flightMovement;
    }

    /**
     * Werner, Helen and Carmine discuss on March 24 March 2017.
     * LogicalKey is
     * Tower.FlightID = FlightMovement.FlightID  - Mandatory
     * Tower.DepAd = FlightMovement.DepAd        - Mandatory
     * Tower.DestAd = FlightMovement.DestAd      - Mandatory
     * Tower.DayOfContact = FlightMovement.DayOfFlight ~ (and day before)  - Mandatory
     * Tower.DepTime = FlightMovement.DepTime - Mandatory (if is a Departure Tower)
     * Tower.DestTime ==> (DestTime - EET) FlightMovement.DepTime (if is a Destination Tower)
     *
     * @param towerMovementLog tower movement log
     * @return flight movement found
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    FlightMovement findFlightMovementFromTowerMovementLog(TowerMovementLog towerMovementLog) throws FlightMovementBuilderException {
        if (towerMovementLog == null){
            throw new IllegalArgumentException("Invalid TowerMovementLog ");
        }

        final String flightId = towerMovementLog.getFlightId();
        final String destTime = towerMovementLog.getDestinationContactTime();
        final String depAd = checkAerodromeForTower(towerMovementLog.getDepartureAerodrome(), true);
        final String destAd = checkAerodromeForTower(towerMovementLog.getDestinationAerodrome(), false);

        String depTime = towerMovementLog.getDepartureTime();
        if (depTime == null || depTime.isEmpty()) {
            depTime = towerMovementLog.getDepartureContactTime();
        }

        LocalDateTime date = towerMovementLog.getDayOfFlight();
        if (date == null) {
            date = towerMovementLog.getDateOfContact(); // the date of contact if day of flight has not been calculated
        }
        if (date == null) {
            throw new ErrorDTO.Builder("Date of contact is not specified")
                .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                .buildRejectedException();
        }

        LOG.debug("Finding Flight Movements from TowerMovementLog by  flightID: {}, depAd: {}, destAd: {}, dateOfFlight: {}, depTime: {}",
            flightId,depAd,destAd,date,depTime);

        FlightMovement flightMovement = null;
        if(StringUtils.isNotEmpty(flightId) && StringUtils.isNotEmpty(depAd) && StringUtils.isNotEmpty(destAd)
            && (StringUtils.isNotEmpty(depTime) || StringUtils.isNotEmpty(destTime))) {
            // We have depTime - probably is Tower Departure
            if(StringUtils.isNotEmpty(depTime)) {
                flightMovement = this.getFlightMovementByLogicalKey(flightId, date, depTime, depAd, "0000");
            } else {
                // We don't have DepTime - probably is Tower Arrival
                Sort sort = orderByDateOfFlightAndDepTime();
                final LocalDateTime dayBefore = date.minusDays(1L);
                final List<FlightMovement> flightMovementList = flightMovementRepository
                    .findAllByFlightIdAndDepAdAndDateOfFlight(sort, flightId, depAd, dayBefore, date);
                final List<FlightMovement> flightMovementsByArrivalTimeAndEET =
                    findLatestFlightMovementByArrivalTimeAndEET(flightMovementList, flightId, date, destTime, depAd);
                if (CollectionUtils.isNotEmpty(flightMovementsByArrivalTimeAndEET)) {
                    flightMovement = flightMovementsByArrivalTimeAndEET.get(0);
                }
            }
        } else {
            LOG.warn("{} flightID: {}, depAd: {}, destAd: {}, dateOfContact: {}, depTime: {}, destTime: {}",
                FlightMovementBuilderIssue.ORPHAN_ITEM, flightId, depAd, destAd, date, depTime, destTime);
        }
        return flightMovement;
    }

    /**
     * Returns first flight movement that exists with same flight id overlapping with date and time.
     */
    private FlightMovement findOverlapWithExistingFlightMovement(final TowerMovementLog towerMovementLog) {
        Preconditions.checkArgument(towerMovementLog != null);

        String identifier = towerMovementLog.getFlightId();
        String time = ObjectUtils.firstNonNull(
            StringUtils.stripToNull(towerMovementLog.getDepartureTime()),
            StringUtils.stripToNull(towerMovementLog.getDepartureContactTime()),
            StringUtils.stripToNull(towerMovementLog.getDestinationContactTime()));
        LocalDateTime date = ObjectUtils.firstNonNull(
            towerMovementLog.getDayOfFlight(),
            towerMovementLog.getDateOfContact());

        return findOverlapWithExistingFlightMovement(identifier, time, date);
    }

    /**
     * Returns first flight movement that exists with same flight id overlapping with date and time.
     */
    private FlightMovement findOverlapWithExistingFlightMovement(final String identifier, final String time, final LocalDateTime date) {
        if (StringUtils.isBlank(identifier) || StringUtils.isBlank(time) || date == null) return null;

        List<FlightMovement> overlaps = flightMovementRepository.findAllOverlapByFlightIdAndDepTimeAndDateOfFlight(
                identifier, time, date, date.minusDays(1));

        return overlaps == null || overlaps.isEmpty() ? null : overlaps.get(0);
    }

    private String checkAerodromeForTower(final String aerodrome, final boolean checkAfil) {
        String ad = flightMovementBuilderUtility.checkAerodrome(aerodrome, checkAfil);
        if (ad == null) {
            ad = ApplicationConstants.PLACEHOLDER_ZZZZ;
        }
        return ad;
    }

    /**
     * Werner, Helen and Carmine discuss on March 28 March 2017.
     * LogicalKey is
     * ATC.FlightID = FlightMovement.FlightID  - Mandatory
     * ATC.DepAd = FlightMovement.DepAd        - Mandatory
     * ATC.DestAd = FlightMovement.DestAd      - Mandatory
     * ATC.DayOfContact = FlightMovement.DayOfFlight (and day before)  - Mandatory
     * ATC.DepTime = FlightMovement.DepTime - Mandatory
     *
     *
     * @param atcMovementLog atc movement log
     * @return flight movement found
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    FlightMovement findFlightMovementFromAtcMovementLog(AtcMovementLog atcMovementLog) throws FlightMovementBuilderException {
        if (atcMovementLog == null) {
            throw new IllegalArgumentException("Invalid AtcMovementLog ");
        }

        final String flightId = atcMovementLog.getFlightId();
        final String depTime = atcMovementLog.getDepartureTime();
        final String depAd = checkAerodromeForTower(atcMovementLog.getDepartureAerodrome(), true);
        final String destAd= checkAerodromeForTower(atcMovementLog.getDestinationAerodrome(), false);

        LocalDateTime date = atcMovementLog.getDayOfFlight();
        if (date == null){
            date = atcMovementLog.getDateOfContact(); // the date of contact if day of flight has not been calculated
        }

        LOG.debug("Finding Flight Movements from AtcMovementLog by  flightID: {}, depAd: {}, destAd: {}, dateOfFlight: {}, depTime: {}",
            flightId,depAd,destAd,date,depTime);


        FlightMovement flightMovement = null;
        if (StringUtils.isNotEmpty(flightId) && StringUtils.isNotEmpty(depAd) && StringUtils.isNotEmpty(destAd)
            && date != null && (StringUtils.isNotEmpty(depTime))) {
            flightMovement = this.getFlightMovementByLogicalKey(flightId, date, depTime, depAd, "0000");
        } else {
            LOG.warn("{} flightID: {}, depAd: {}, destAd: {}, dateOfFlight: {}, depTime: {}",
                FlightMovementBuilderIssue.ORPHAN_ITEM, flightId, depAd, destAd, date, depTime);
        }
        return flightMovement;
    }

    public List<FlightMovement> findFlightMovementByPassengerServiceChargeReturn(PassengerServiceChargeReturn passengerServiceChargeReturn, boolean isUpload) {
        if(passengerServiceChargeReturn == null) {
            throw new IllegalArgumentException("Invalid PassengerServiceChargeReturn");
        }

        List<FlightMovement> flightMovements = null;
        final String flightId = passengerServiceChargeReturn.getFlightId();
        final String depTime = passengerServiceChargeReturn.getDepartureTime();
        final LocalDateTime dayOfFlight=passengerServiceChargeReturn.getDayOfFlight();

        LOG.debug("Finding Flight Movements from PassengerServiceChargeReturn by  flightID: {}, dayOfFlight: {}, depTime: {}",
            flightId, dayOfFlight, depTime);

        if(StringUtils.isNotEmpty(flightId) && dayOfFlight != null){
            if (depTime == null || depTime.equals("")) {
                if (isUpload) {
                    final Sort sort=orderByDateOfFlightAndDepTime();
                    final List<FlightmovementCategoryType> categoryTypes = new ArrayList<>();
                    categoryTypes.add(FlightmovementCategoryType.DOMESTIC);
                    categoryTypes.add(FlightmovementCategoryType.DEPARTURE);
                    flightMovements = flightMovementRepository.findAllByFlightIdAndDateOfFlightAndFlightCategoryTypeIn(sort,flightId,dayOfFlight,categoryTypes);
                } else {
                    flightMovements=flightMovementRepository.findAllByFlightIdAndDateOfFlight(flightId, dayOfFlight);
                }
            } else {
                flightMovements=flightMovementRepository.findAllByFlightIdAndDateOfFlightAndDepTime(flightId,dayOfFlight, depTime);
            }
        }else{
            LOG.warn("{} flightID: {}, dayOfFlight: {}, depTime: {}", FlightMovementBuilderIssue.ORPHAN_ITEM,
                flightId, dayOfFlight, depTime);
        }
        return flightMovements;
    }

    public long countAllFlightMovement() {
        return flightMovementRepository.count();
    }

    /**
     * This method create by FlightMovementBuilder an Object FlightMovement
     *
     * @param flightMovement flight movement
     * @return flight movement created
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    public FlightMovement createFlightMovementFromUI(FlightMovement flightMovement,Boolean forInvoice) throws FlightMovementBuilderException {
        LOG.debug("Create a Flight Movement from WebUI");
        checkValidValues(flightMovement);

        // validate that flight movement does not match existing by logical keys
        List<FlightMovement> existingFlightMovements = doFindAllByLogicalKey(
            flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime(),
            flightMovement.getDepAd(), flightMovement.getEstimatedElapsedTime());
        if (existingFlightMovements != null && !existingFlightMovements.isEmpty()) {
            LOG.warn("Cannot create Flight Movement from WebUI that matches existing records by logical key : {}.",
                flightMovement);

            String description = String.format("flightId[%s] depAd[%s] depTime[%s] dof[%s]", flightMovement.getFlightId(),
                    flightMovement.getDepAd(), flightMovement.getDepTime(),flightMovement.getDateOfFlight().format(DateTimeFormatter.ISO_DATE_TIME));

            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                    ErrorConstants.ERR_UNIQUENESS_VIOLATION_DESC, description,FlightMovement.class, FlightMovement.UNIQUE_KEY_COLUMN_NAMES);
        }

        flightMovement.setThruFlight(thruFlightPlanUtility.isThruFlight(flightMovement));
        if (flightMovement.getThruFlight()) {
        	this.deleteDuplicatedRadarFM(flightMovement);
        }

        FlightMovement flightMovementResult = doPersistFlightMovement(
            flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovement, forInvoice));

        // run methods after flight movement is created/updated
        postCreateUpdate(flightMovementResult, null);

        return flightMovementResult;
    }

    public FlightMovement updateFlightMovementFromUI(final Integer id, FlightMovement flightMovement) throws FlightMovementBuilderException {
        LOG.debug("Update a Flight Movement from WebUI");
        checkValidValues(flightMovement);

        // validate that flight movement does not match existing by logical keys
        List<FlightMovement> existingFlightMovements = doFindAllByLogicalKey(
            flightMovement.getFlightId(), flightMovement.getDateOfFlight(), flightMovement.getDepTime(),
            flightMovement.getDepAd(), flightMovement.getEstimatedElapsedTime());
        if (existingFlightMovements != null && existingFlightMovements.stream().anyMatch(fm -> !Objects.equals(fm.getId(), id))) {
            LOG.warn("Cannot update Flight Movement from WebUI that matches existing records by logical key : {}.",
                flightMovement);
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                ErrorConstants.ERR_UNIQUENESS_VIOLATION_DESC, FlightMovement.class, FlightMovement.UNIQUE_KEY_COLUMN_NAMES);
        }

        FlightMovement flightMovementResult = null;
        FlightMovement fmFromDb = null;

        if (id != null) {
            FlightMovement existingFlightMovement = flightMovementRepository.findOne(id);

            //make sure id and flightMovement.id is the same
            if(existingFlightMovement!=null && existingFlightMovement.getId().equals(flightMovement.getId())){

                // need to set needed data before updating for recalculating extended hours surcharge (used in EANA only)
                fmFromDb = setFlightMovementFieldsForComparison(new FlightMovement(), existingFlightMovement);

                flightMovementMerge.updateFlightMovementCreatedByUI(existingFlightMovement, flightMovement);
                FlightMovement flightMovementFromBuilder=flightMovementBuilder.createUpdateFlightMovementFromUI(flightMovement,false);

                // US101390: merge and ignore atc, radar, and tower tracks as they are not defined via UI and thus always null
                flightMovementMerge.mergeFlightMovementsFromUI(flightMovementFromBuilder, existingFlightMovement,
                    "id", "source", "atcLogTrack", "radarRoute", "towerLogTrack");

                flightMovementResult = doPersistFlightMovement(existingFlightMovement);

                // remove duplicated flight movements created from Radar summary in case of THRU flights
                flightMovement.setThruFlight(thruFlightPlanUtility.isThruFlight(flightMovement));
            	if (flightMovement.getThruFlight()) {
                	this.deleteDuplicatedRadarFM(flightMovement);
                    flightMovementResult = doPersistFlightMovement(existingFlightMovement);
                }
            }
        }

        // run methods after flight movement is created/updated
        postCreateUpdate(flightMovementResult, fmFromDb);

        return flightMovementResult;
    }

    public FlightMovement updateFlightMovementFromJobs(final Integer id) throws FlightMovementBuilderException {
        LOG.debug("Update a Flight Movement from Jobs");
        if(id == null) {
            throw new IllegalArgumentException("Invalid Flight Movement id");

        }

        FlightMovement flightMovementResult = null;

        final FlightMovement flightMovementExisting = flightMovementRepository.findOne(id);

        if (flightMovementExisting != null) {
            flightMovementRepositoryUtility.detach(flightMovementExisting);
            checkFlightMovementFromJobs(flightMovementExisting);
            FlightMovement flightMovement = flightMovementBuilder.createUpdateFlightMovementFromUI(
                flightMovementExisting, false);
            flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);
        }

        if (flightMovementResult != null) {

            // run methods after flight movement is created/updated
            postCreateUpdate(flightMovementResult, null);
        }

        return flightMovementResult;
    }

    /**
     * Return true if a flight's status allows updates. Otherwise returns false, or, if
     * "fromReject" is set, throws an exception. This is because we want to see this "error"
     * when fixing rejects interactively.
     */
    private boolean checkSpatiaUpdateAllowed (final FlightMovement x, boolean fromReject) {
        if (x == null) {
            return false;
        }
        if (x.getStatus() == FlightMovementStatus.INVOICED || x.getStatus() == FlightMovementStatus.PAID || x.getStatus() == FlightMovementStatus.DELETED) {
            LOG.warn ("Flight movement can't be updated because it's already INVOICED or PAID, or DELETED; ignoring incoming FPL object from Spatia/CRONOS: flight movement id={} {}] ",
                    x.getId(), x.getLogicalKey());
            if (fromReject) {
                if (x.getStatus() == FlightMovementStatus.DELETED) {
                    throw new ErrorDTO.Builder()
                        .setErrorMessage(FlightMovementBuilderIssue.FLIGHT_MOVEMENT_DELETED.name())
                        .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                        .buildInvalidDataException();

                } else {
                    throw new ErrorDTO.Builder()
                        .setErrorMessage(FlightMovementBuilderIssue.FLIGHT_MOVEMENT_INVOICED.name())
                        .addRejectedReason(RejectedReasons.VALIDATION_ERROR)
                        .buildInvalidDataException();
                }
            }
            return false;
        }
        return true;
    }


    /**
     * Find all flights that match the logical key and EET in the provided FPL object. May return multiple objects.
     */
    private List <FlightMovement> doFindAllByLogicalKey (final FplObjectDto fplObject) {
        Preconditions.checkNotNull (fplObject);
        return doFindAllByLogicalKey (
                fplObject.getFlightId(),
                fplObject.getDayOfFlight() != null ? fplObject.getDayOfFlight().atStartOfDay() : null,
                fplObject.getDepartureTime(),
                fplObject.getDepartureAd(),
                fplObject.getTotalEet());
    }

    /**
     * Set status of a flight to CANCELED
     */
    public void cancelFlight (final FlightMovement x) {
        if (x != null && x.getStatus() != FlightMovementStatus.CANCELED) {
            LOG.debug ("Setting flight movement id={} status to CANCELED: {}", x.getId(), x.getLogicalKey());
            x.setStatus(FlightMovementStatus.CANCELED);
        }
    }

    List <FlightMovement> findBySpatiaFplObjectIdOrderBySpatiaFplObjectIdDesc (final Long spatiaFplObjectId) {
        if (spatiaFplObjectId != null) {
            flightMovementRepository.findBySpatiaFplObjectIdOrderBySpatiaFplObjectIdDesc(spatiaFplObjectId);
        }
        return new ArrayList<>();
    }

    /**
     * Werner, Helen, and Jonathan discuss on August 30 2017.
     * fplObject should first look for a flight movement with catalogueFplObjectId
     * If a flight movement is found, update it.
     * If a flight movement isn't found, look for a flight movement using logical keys.
     * If a flight movement is found, update it.
     * It is safe to use the flight movement found by the fplObject because it must match the logical keys
     * and thus retain associations.
     * If a flight movement isn't found using the fplObjectId or logical keys, a new record is created.
     */
    public FlightMovement createUpdateFlightMovementFromSpatia(final FplObjectDto fplObject, boolean fromReject)  {

        Preconditions.checkNotNull (fplObject);

        LOG.debug("Create or update Flight Movement {} from Spatia", fplObject);

        FlightMovementValidator.validateFplObject(fplObject);

        FlightMovement existingFlightMovement;

        //
        // CASE 1: Incoming fplObject is an update of one we already imported from Spatia before
        //

        // Find flights with the same Spatia ID. Technically we don't have a unique constraint on
        // this field, so we might get multiple matches.
        final List <FlightMovement> flightsBySpatiaId =
                findBySpatiaFplObjectIdOrderBySpatiaFplObjectIdDesc(
                        fplObject.getCatalogueFplObjectId());
        if (flightsBySpatiaId != null && !flightsBySpatiaId.isEmpty()) {
            // Warn about multiple matches as this shouldn't normally happen
            if (flightsBySpatiaId.size() > 1 && LOG.isWarnEnabled()) {
                final String idList = formatIdList(flightsBySpatiaId);
                LOG.warn ("Found multiple flight movements [{}] that match the same Spatia ID {}, this should never happen",
                        idList, fplObject.getCatalogueFplObjectId());
            }
            // Make sure none of them are PAID/INVOICED/DELETED
            if (!flightsBySpatiaId.stream().allMatch(x->checkSpatiaUpdateAllowed (x, fromReject))) {
                return null;
            }
            // Use the first one for further updates
            final FlightMovement flightMovementBySpatiaId = flightsBySpatiaId.get(0);
            LOG.debug("Found flight movement by fpl_object_id: {}", flightMovementBySpatiaId);

            // The following can only happen if Spatia changed the logical key of a flight to
            // values that match another flight, e.g., by updating the departure aerodrome

            // Find any _other_ flight movements with a logical key similar to the incoming one. We
            // should get at most one result, unless departure time tolerance parameters have
            // been changed and flights that were previously considered separate are now considered
            // the same because they fall within the new tolerance values.
            final List <FlightMovement> flightsByNewLogicalKey = doFindAllByLogicalKey(fplObject).stream()
                    .filter(x->!x.equals (flightMovementBySpatiaId)) // except the one we already found by Spatia ID
                    .collect(Collectors.toList());
            // Make sure none of them are PAID or INVOICED, or DELETED
            if (!flightsByNewLogicalKey.stream().allMatch(x->checkSpatiaUpdateAllowed (x, fromReject))) {
                return null;
            }

            // One of these other records may match our new logical key exactly, which would prevent us
            // from updating "flightMovementBySpatiaId" with that logical key, because it would trigger
            // the DB unique constraint exception.
            existingFlightMovement = flightsByNewLogicalKey.stream()
                    // Try to find record that exactly matches incoming logical key
                    .filter(x->Objects.equals(x.getLogicalKey(), fplObject.getLogicalKey())).findFirst()
                    // Otherwise, there's no conflict, so use the record that matched Spatia ID
                    .orElse (flightMovementBySpatiaId);

            // Cancel all _other_ flights that matched our logical key
            flightsByNewLogicalKey.stream()
                .filter (x->!Objects.equals (x, existingFlightMovement)) // except the one we will be updating
                .forEach (this::cancelFlight);

            // Also cancel the record that matched Spatia ID, unless that's the record we are updating
            if (existingFlightMovement != flightMovementBySpatiaId) {
                cancelFlight (flightMovementBySpatiaId);
            }

        }

        //
        // CASE 2: The system tries to link a flight movement imported from Spatia with another one already existing
        // and inserted previously via UI/Surveillance logs, using the logical key to identify the record to update.
        //
        else if(!fplObject.getCataloguePrcStatus().equalsIgnoreCase("C")) { // Ignore incoming fplObjects in canceled state
            LOG.debug("Flight movement not found by fpl_object_id={}: searching by logical key {}",
                fplObject.getCatalogueFplObjectId(), fplObject.getLogicalKey());

            // Find all flights that approximately match the incoming logical key
            final List <FlightMovement> flightsByLogicalKey = doFindAllByLogicalKey (fplObject);

            // None found -- fall through
            if (flightsByLogicalKey == null || flightsByLogicalKey.isEmpty()) {
                existingFlightMovement = null;
            }
            // One or more found by logical key
            else {
                // Make sure none of them are INVOICED/PAID/DELETED
                if (!flightsByLogicalKey.stream().allMatch(x->checkSpatiaUpdateAllowed (x, fromReject))) {
                    return null;
                }
                // look for an exact logical key match first
                existingFlightMovement = flightsByLogicalKey.stream()
                        .filter (x->Objects.equals (x.getLogicalKey(), fplObject.getLogicalKey())).findFirst()
                        // Otherwise, use the first record (i.e., one with latest departure time).
                        .orElseGet (()->flightsByLogicalKey.get(0));
                // Found a record by logical key, that was created by a different Spatia ID than the incoming FPL object
                if (existingFlightMovement.getSpatiaFplObjectId() != null) {
                    LOG.warn ("Flight movement id={} {} matches incoming fplObject id={} {} by logical key, but was originally created by a different fplObject id={}",
                            existingFlightMovement.getId(),
                            existingFlightMovement.getLogicalKey(),
                            fplObject.getCatalogueFplObjectId(),
                            fplObject.getLogicalKey(),
                            existingFlightMovement.getSpatiaFplObjectId());
                }

                // 2019-09-25 Update the flight

            }

        } else {
            // Incoming flight is new but canceled, ignore it
            LOG.debug("Ignoring incoming canceled fplObject with id={} and logical key {}",
                    fplObject.getCatalogueFplObjectId(), fplObject.getLogicalKey());
            return null;
        }

        FlightMovement flightMovement;
        FlightMovement flightMovementResult;

        FlightMovement fmFromDb = null;

        // Existing record found either by fplObjectId (CASE 1) or by logical key (CASE 2): update it
        if (existingFlightMovement != null) {

            flightMovementRepositoryUtility.detach(existingFlightMovement);

            // need to set needed data before updating for recalculating extended hours surcharge (used in EANA only)
            fmFromDb = setFlightMovementFieldsForComparison(new FlightMovement(), existingFlightMovement);

            flightMovement = flightMovementBuilder.validateAndMergeFromFplObject(existingFlightMovement, fplObject);
            flightMovementResult = flightMovementRepositoryUtility.overwrite(flightMovement);

        } else {

            //
            // CASE 3: Because the flight movement imported from Spatia cannot be linked to an existing one, that flight
            // movement is a new record to create.
            //
            flightMovement = flightMovementBuilder.validateAndCreateFlightMovementFromFlightObject(fplObject);
            flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);
        }

        // EANA feature. If this flight was during closing hours, all other flights with the same reg
        // number that exist during the same hours should be recalculated (extended hours surcharge)
        try {
            updateFlightsChargesDuringAerodromeClosingHours(flightMovementResult, fmFromDb);
        } catch(Exception ex) {
         // Suppress all exceptions to avoid creating extra rejects
            LOG.error("Calculations for {} failed because {}", flightMovementResult, ex);
        }

        // Done - return
        return flightMovementResult;
    }

    public FlightMovement calculateFlightMovementFromFplObject(FlightMovement flightMovement) throws FlightMovementBuilderException {
        if (flightMovement == null) return null;

        flightMovementBuilder.calculateFlightMovementFromFlightObject(flightMovement);

        // Remove duplicated/partial flight movements created from Radar summary in case of THRU flights
        flightMovement.setThruFlight(thruFlightPlanUtility.isThruFlight(flightMovement));
        if(flightMovement.getThruFlight()) {
            this.deleteDuplicatedRadarFM(flightMovement);
        }

        // Save the changes
        FlightMovement flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);

        // run methods after flight movement is created/updated
        postCreateUpdate(flightMovementResult, null);

        return flightMovementResult;
    }

    public FlightMovement createUpdateFlightMovementFromRadarSummary(final RadarSummary radarSummary, final ItemLoaderObserver o) throws FlightMovementBuilderException {
        LOG.debug("Create or update Flight Movement from RadarSummary");

        FlightMovementValidator.validateRadarSummary(radarSummary);

        // clean-up flight id and registration
        if(StringUtils.isNotBlank(radarSummary.getFlightIdentifier())) {
         	final Pattern p = Pattern.compile ("[^a-zA-Z0-9]");
         	final String flightId =radarSummary.getFlightIdentifier();
         	final String reg = radarSummary.getRegistration();
          	if(StringUtils.isNotBlank(flightId)) {
          	    final String f = p.matcher (flightId).replaceAll("");
          		if (!Objects.equals (f, flightId)) {
          		    LOG.debug("flight id changed from {} to {}",radarSummary.getFlightIdentifier(),f);
          		    radarSummary.setFlightIdentifier(f);
          		}
          	}
          	if(StringUtils.isNotBlank(reg)) {
          	    final String r = p.matcher (reg).replaceAll("");
                if (!Objects.equals(r, reg)) {
              		LOG.debug("registration changed from {} to {}",radarSummary.getRegistration(),r);
              		radarSummary.setRegistration(r);
                }
          	}
        }

        ItemLoaderResult fplResult = null;
        FlightMovement flightMovementResult = null;
        FlightMovement fmFromDb = null;
        FlightMovement existFlightMovement = findFlightMovementFromRadarSummary(radarSummary);

        // THRU flights are not updated from Radar Summary.
        // New FM is not created from RS if there is THRU FM which can be matched to this RS
        if (isFlightMovementEditable(existFlightMovement)) {
            if (existFlightMovement != null && existFlightMovement.getId() != null) {

                // need to set needed data before updating for recalculating extended hours surcharge (used in EANA only)
                fmFromDb = setFlightMovementFieldsForComparison(new FlightMovement(), existFlightMovement);

                //validation of the flight level for TMA(Leonardo file input)
                if(systemConfigurationService.getBoolean(SystemConfigurationItemName.VALIDATE_FLIGHT_LEVEL_AIRSPACE)) {
                    // check if existing RS and the new RS have different entry point time
                    // convert entry time from radar summary into dateTime
                    // compare with the one from FM
                    LocalDateTime entry = null;
                    LocalDateTime exit = null;
                    LocalTime localEntryTime = JSR310DateConverters.convertStringToLocalTime(radarSummary.getFirEntryTime(),
                            JSR310DateConverters.DEFAULT_PATTERN_TIME);
                    LocalTime localExitTime = JSR310DateConverters.convertStringToLocalTime(radarSummary.getFirExitTime(),
                            JSR310DateConverters.DEFAULT_PATTERN_TIME);
                    if (radarSummary.getDayOfFlight() != null) {
                        LocalDate localDate = radarSummary.getDayOfFlight().toLocalDate();
                        if (localEntryTime != null) {
                            entry = LocalDateTime.of(localDate, localEntryTime);
                        }
                        if (localExitTime != null) {
                           exit = LocalDateTime.of(localDate, localExitTime);
                        }
                    }

                    // Recalculate entry and exit points for the route
                    List<RouteSegment> list = existFlightMovement.getRouteSegments();
                    if(list != null && !list.isEmpty()) {
                        for(RouteSegment rs: list) {
                            if(rs.getSegmentType() == SegmentType.RADAR &&
                                    StringUtils.isNotBlank(rs.getSegmentStartLabel()) &&
                                    StringUtils.isNotBlank(radarSummary.getFirEntryPoint()) &&
                                   !rs.getSegmentStartLabel().equalsIgnoreCase(radarSummary.getFirEntryPoint()) &&
                                   existFlightMovement.getEntryTime() != null &&
                                   entry != null &&
                                   !existFlightMovement.getEntryTime().isEqual(entry)) {
                                 // this is different segment

                                 if(entry.isBefore(existFlightMovement.getEntryTime())) {
                                    // if entry is earlier - set FM entry point from segment
                                    existFlightMovement.setBillableEntryPoint(radarSummary.getFirEntryPoint());
                                 }
                                 if(exit != null && existFlightMovement.getExitTime() != null &&
                                         exit.isAfter(existFlightMovement.getExitTime())) {
                                    // if exit is later - set FM exit point from segment
                                    existFlightMovement.setBillableExitPoint(radarSummary.getFirExitPoint());
                                 }
                            }
                        }
                    }


                }
                if (!thruFlightPlanUtility.isThruFlight(existFlightMovement)) {

                    // not Thru flight
                    flightMovementRepositoryUtility.detach(existFlightMovement);
                    FlightMovement flightMovement = flightMovementBuilder
                        .updateFlightMovementFromRadarSummary(existFlightMovement, radarSummary);
                    flightMovementResult = flightMovementRepositoryUtility.overwrite(flightMovement);

                    // need to clear the session after saving a FM to avoid an exception while doing bulk upload
                    // with files that attempt to update the same record two or more times
                    clearCurrentSession();

                    if (flightMovementResult != null) fplResult = ItemLoaderResult.UPDATED;
                }
            } else {

                // find possible THRU flight movement
                List<FlightMovement> listFM = this.findThruFlightMovementsFromRadarSummary(radarSummary);
                boolean existThruFlight = false;
                if (listFM != null && !listFM.isEmpty()) {
                    for (FlightMovement fm : listFM) {
                        if (fm != null && this.thruFlightPlanUtility.isThruFlight(fm)) {

                            // check if dep time for the radar summary is within the interval of the THRU flight route segments
                            List<ThruFlightPlanVO> map = thruFlightPlanUtility.parseThruPlanRoute(fm.getFplRoute(),
                                fm.getDepAd(), DateTimeUtils.addTimeToDate(fm.getDateOfFlight(), fm.getDepTime()));

                            LocalDateTime startDate = null;
                            LocalDateTime endDate = null;
                            if (map != null && !map.isEmpty()) {
                                if(map.get(0) != null)
                                    startDate = map.get(0).getDepTime();

                                int size = map.size();
                                if(map.get(size - 1) != null)
                                    endDate = map.get(size - 1).getArrivalTime();
                            }

                            LocalDateTime depRadar = DateTimeUtils.addTimeToDate(radarSummary.getDayOfFlight(), radarSummary.getDepartureTime());
                            if ((fm.getDepTime() != null && fm.getDepTime().equals(radarSummary.getDepartureTime())) || (
                                startDate != null && depRadar.isAfter(startDate) && endDate != null && depRadar.isBefore(endDate))
                            ) {
                                LOG.debug("Found existing THRU Flight Movement   flightID: {}, dateOfFlight: {}, depTime: {}",
                                        fm.getFlightId(),fm.getDateOfFlight(),fm.getDepTime());
                                existThruFlight =true;
                                break;
                            }
                        }
                    }
                }

                if (!existThruFlight) {
                    FlightMovement flightMovement = flightMovementBuilder
                        .createFlightMovementFromRadarSummary(radarSummary);
                    flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);
                    fplResult = ItemLoaderResult.CREATED;
                }
            }
        } else {

            // throw exception if not bulk loader process, used to displayed on user form
            handleBadStatus(existFlightMovement.getStatus(), existFlightMovement.getDisplayName());

            // return immediately if no exception thrown
            return null;
        }

        if (fplResult != null) {

            // observers are used on bulk upload to count fpl added versus updated
            this.notifyItemLoaderObserver(o, fplResult);

            // run methods after flight movement is created/updated
            postCreateUpdate(flightMovementResult, fmFromDb);
        }

        return flightMovementResult;
    }

    public FlightMovement createUpdateFlightMovementFromTowerMovementLog(TowerMovementLog towerMovementLog, ItemLoaderObserver o) throws FlightMovementBuilderException {
        LOG.debug("Create or update Flight Movement from TowerMovementLog ");

        FlightMovement flightMovement;
        FlightMovement flightMovementResult = null;

        ItemLoaderResult fplResult = null;
        FlightMovement fmFromDb = null;

        // STEP 1: look for existing flight movement by logic keys using tower movement log
        FlightMovement existFlightMovement = findFlightMovementFromTowerMovementLog(towerMovementLog);

        // STEP 2: look for an overlapping flight movement if none found by logical keys
        // US 104328: only want to create new flight movement for delta or thru if it does not overlap with
        // an existing flight movement
        if (existFlightMovement == null) {
            existFlightMovement = findOverlapWithExistingFlightMovement(towerMovementLog);
        }

        if (isFlightMovementEditable(existFlightMovement)) {

            // using tower movement log, update existing flight movement if defined or create new flight movement
            // should only create if their is a valid tower movement log departure time
            if (existFlightMovement != null) {

                flightMovementRepositoryUtility.detach(existFlightMovement);

                // need to set needed data before updating for recalculating extended hours surcharge (used in EANA only)
                fmFromDb = setFlightMovementFieldsForComparison(new FlightMovement(), existFlightMovement);

                flightMovement = flightMovementBuilder.updateFlightMovementFromTowerMovementLog(existFlightMovement, towerMovementLog);

                flightMovementResult = flightMovementRepositoryUtility.overwrite(flightMovement);

                // need to clear the session after saving a FM to avoid an exception while doing bulk upload
                // with files that attempt to update the same record two or more times
                clearCurrentSession();

                if (flightMovementResult != null) fplResult = ItemLoaderResult.UPDATED;

            } else if (
                StringUtils.isNotBlank(towerMovementLog.getDepartureTime()) ||
                StringUtils.isNotBlank(towerMovementLog.getDepartureContactTime())
            ) {
                flightMovement = flightMovementBuilder.createFlightMovementFromTowerMovementLog(towerMovementLog);

                flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);
                if (flightMovementResult != null) fplResult = ItemLoaderResult.CREATED;
            }

        } else {

            // throw exception if not bulk loader process, used to displayed on user form
            handleBadStatus(existFlightMovement.getStatus(), existFlightMovement.getDisplayName());

            // return immediately if no exception thrown
            return null;
        }

        if (fplResult != null) {

            // observables are used on bulk upload to count fpl added versus updated
            this.notifyItemLoaderObserver(o, fplResult);

            // run methods after flight movement is created/updated
            postCreateUpdate(flightMovementResult, fmFromDb);
        }

        return flightMovementResult;
    }

    public FlightMovement createUpdateFlightMovementFromAtcMovementLog(AtcMovementLog atcMovementLog, ItemLoaderObserver o) throws FlightMovementBuilderException {
        LOG.debug("Create or update Flight Movement from ATCMovementLog ");

        FlightMovement flightMovement;
        FlightMovement flightMovementResult;

        ItemLoaderResult fplResult = null;
        FlightMovement fmFromDb = null;

        // STEP 1: look for existing flight movement by logic keys using atc movement log
        FlightMovement existFlightMovement = findFlightMovementFromAtcMovementLog(atcMovementLog);

        if (isFlightMovementEditable(existFlightMovement)) {
            if (existFlightMovement != null) {

                flightMovementRepositoryUtility.detach(existFlightMovement);

                // need to set needed data before updating for recalculating extended hours surcharge (used in EANA only)
                fmFromDb = setFlightMovementFieldsForComparison(new FlightMovement(), existFlightMovement);

                flightMovement = flightMovementBuilder.updateFlightMovementFromAtcMovementLog(existFlightMovement, atcMovementLog);

                flightMovementResult = flightMovementRepositoryUtility.overwrite(flightMovement);

                // need to clear the session after saving a FM to avoid an exception while doing bulk upload
                // with files that attempt to update the same record two or more times
                clearCurrentSession();

                if (flightMovementResult != null) fplResult = ItemLoaderResult.UPDATED;

            } else {

                // Werner, Helen and Carmine discuss on March 27 March 2017 (by email).
                // We create a ATC that have a DepTime
                flightMovement = flightMovementBuilder.createFlightMovementFromAtcMovementLog(atcMovementLog);

                flightMovementResult = flightMovementRepositoryUtility.persist(flightMovement);
                if (flightMovementResult != null) fplResult = ItemLoaderResult.CREATED;
            }
        } else {

            // throw exception if not bulk loader process, used to displayed on user form
            handleBadStatus(existFlightMovement.getStatus(), existFlightMovement.getDisplayName());

            // return immediately if no exception thrown
            return null;
        }

        if (fplResult != null) {

            // observables are used on bulk upload to count fpl added versus updated
            this.notifyItemLoaderObserver(o, fplResult);

            // run methods after flight movement is created/updated
            postCreateUpdate(flightMovementResult, fmFromDb);
        }

        return flightMovementResult;
    }

    /**
     * Update flight movement from passenger service charge.
     *
     * @param passengerServiceChargeReturn passenger service charge return
     * @return flight movement updated
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    public FlightMovement updateFlightMovementFromPassengerServiceCharge(
        PassengerServiceChargeReturn passengerServiceChargeReturn, boolean isUpload, ItemLoaderObserver o
    ) throws FlightMovementBuilderException {
        LOG.debug("UpdateFlightMovementFromPassengerServiceCharge");

        FlightMovement flightMovementResult = null;

        ItemLoaderResult fplResult = null;

        if( passengerServiceChargeReturn != null) {
            List<FlightMovement> flightMovements = findFlightMovementByPassengerServiceChargeReturn(passengerServiceChargeReturn, isUpload);
            if (flightMovements != null && !flightMovements.isEmpty()) {
                FlightMovement existFlightMovement = flightMovements.get(0);
                if (isFlightMovementEditable(existFlightMovement)) {

                    flightMovementRepositoryUtility.detach(existFlightMovement);

                    FlightMovement flightMovement = flightMovementBuilder
                        .updateFlightMovementFromPassengerPassengerServiceReturn(existFlightMovement, passengerServiceChargeReturn);

                    flightMovementResult = flightMovementRepositoryUtility.overwrite(flightMovement);
                    if (flightMovementResult != null) fplResult = ItemLoaderResult.UPDATED;

                } else {

                    // throw exception if not bulk loader process, used to displayed on user form
                    handleBadStatus(existFlightMovement.getStatus(), existFlightMovement.getDisplayName());

                    // return immediately if no exception thrown
                    return null;
                }
            }
        } else {
            LOG.warn("PassengerServiceChargeReturn is NULL !!!");
            throw new FlightMovementBuilderException(FlightMovementBuilderIssue.REJECTED_ITEM);
        }

        if (fplResult != null) {

            // observables are used on bulk upload to count fpl added versus updated
            this.notifyItemLoaderObserver(o, fplResult);
        }

        return flightMovementResult;
    }

    @Transactional(readOnly = true)
    public List<FlightMovementValidationViewModel>  validateAllFlightMovementByParams(final LocalDateTime startDate,
                                                                                      final LocalDateTime endDateInclusive,
                                                                                      final boolean userBillingCenterOnly,
                                                                                      final List<Integer> accountIdList,
                                                                                      final boolean iata,
                                                                                      final BillingCenter userBC,
                                                                                      final Integer flightCategory){
        List<FlightMovement> flightMovementList;
        List<FlightMovementValidationViewModel> flightMovementViewListReturn = null;

        flightMovementList = this.findFlightMovementsForAviationBillingValidation(startDate, endDateInclusive, userBillingCenterOnly, accountIdList, iata, userBC, flightCategory);

        if(flightMovementList != null && !flightMovementList.isEmpty()){
            FlightMovementValidationViewModel view;
            for(FlightMovement fm : flightMovementList){
                if(fm != null){
                    view =getValidableFlightMovementModel(fm);
                    if(view != null && view.getStatus().equals(FlightMovementStatus.INCOMPLETE)){
                        if(flightMovementViewListReturn == null){
                            flightMovementViewListReturn = new ArrayList<>();
                        }
                        flightMovementViewListReturn.add(view);
                    }
                }
            }
        }

        return flightMovementViewListReturn;
    }

    /**
     * Recalculates all flight movements for the given month and year.
     *
     * @param startDate period start date
     * @param endDateInclusive period end date inclusive
     * @param userBillingCenterOnly user billing center only
     * @param accountIdList account id list
     * @param iata iata only
     * @param userBC user billing center
     * @return Number of flights affected
     */
    public ResultRecords  recalculateAllFlightMovementByParams(final LocalDateTime startDate,
                                                               final LocalDateTime endDateInclusive,
                                                               final boolean userBillingCenterOnly,
                                                               List<Integer> accountIdList,
                                                               boolean iata,
                                                               BillingCenter userBC){
        List<FlightMovement> flightMovementList = null;
        ResultRecords result = new ResultRecords();

        result.setTotalRecords(0);
        result.setChangedRecords(0);

        ZonedDateTime zdtStart = ZonedDateTime.of(startDate, ReportHelper.UTC_ZONE_ID);

        LocalDateTime ldtEnd = endDateInclusive.plusDays(1);
        ZonedDateTime zdtEnd = ZonedDateTime.of(ldtEnd, ReportHelper.UTC_ZONE_ID);

        int i = 0;
        flightMovementList = this.findFlightMovementsForAviationBilling(zdtStart, zdtEnd, userBillingCenterOnly, accountIdList, iata, userBC);

        if(flightMovementList != null && !flightMovementList.isEmpty()){
            boolean chargesSet;
            for(FlightMovement fm : flightMovementList){
                if(fm != null && fm.getId() != null){
                    chargesSet = this.calculateCharges(fm.getId());
                    if(chargesSet){
                        i = i + 1;
                    }
                }
            }
        }

        result.setTotalRecords(i);
        // TODO set changed records number

        return result;
    }

    /**
     * Reconciles all flight movements for the given period.
     *
     * @param startDate period start date
     * @param endDateInclusive period end date
     * @param userBillingCenterOnly user billing center only
     * @param accountIdList account id list
     * @param iata iata only
     * @param userBC user billing center
     * @return Number of flights affected
     */
    public ResultRecords reconcileAllFlightMovementByParams(final LocalDateTime startDate,
                                                            final LocalDateTime endDateInclusive,
                                                            final boolean userBillingCenterOnly,
                                                            List<Integer> accountIdList,
                                                            boolean iata,
                                                            BillingCenter userBC){
        List<FlightMovement> flightMovementList;
        ResultRecords result = new ResultRecords();

        result.setTotalRecords(0);
        result.setChangedRecords(0);
        int i = 0;

        ZonedDateTime zdtStart = ZonedDateTime.of(startDate, ReportHelper.UTC_ZONE_ID);

        LocalDateTime ldtEnd = endDateInclusive.plusDays(1);
        ZonedDateTime zdtEnd = ZonedDateTime.of(ldtEnd, ReportHelper.UTC_ZONE_ID);

        flightMovementList = this.findFlightMovementsForAviationBilling(zdtStart, zdtEnd, userBillingCenterOnly, accountIdList, iata, userBC);

        if(flightMovementList != null && !flightMovementList.isEmpty()){
            for(FlightMovement fm : flightMovementList){
                if(fm != null && fm.getId() != null){
                    FlightMovement updatedFm = null;
                    try {
                        updatedFm = updateFlightMovementFromUI(fm.getId(),fm);
                    } catch (FlightMovementBuilderException e) {
                        throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
                    }
                    if(updatedFm != null && updatedFm.getStatus() != null && updatedFm.getId() != null){
                        i = i + 1;
                    }
                }
            }
        }

        result.setTotalRecords(i);
        // TODO set changed records number

        return result;
    }
    /* Helper methods */

    /**
     * Wrapper for {@link FlightMovementRepositoryUtility#persist(FlightMovement)}. Please use
     * repository utility class from now on.
     */
    public FlightMovement doPersistFlightMovement(final FlightMovement flightMovement){
        return flightMovementRepositoryUtility.persist(flightMovement);
    }

    /**
     * This method check if the flight movement is editable.
     * Do nothing to existFlightMovement if it has been already INVOICED, PAID or DELETED.
     *
     * @param flightMovement flight movement to check
     * @return FALSE if FlightMovement has INVOICED or PAID, or DELETED status
     */
    private boolean isFlightMovementEditable(FlightMovement flightMovement){
        return flightMovement == null || flightMovement.getStatus() == null || (
            !flightMovement.getStatus().equals(FlightMovementStatus.INVOICED) &&
                !flightMovement.getStatus().equals(FlightMovementStatus.PAID) &&
                !flightMovement.getStatus().equals(FlightMovementStatus.DELETED));
    }

    /**
     * Helper method for to get departure time range values from the system configuration
     *
     * @return o or Integer value of SystemConfigurationItemName value
     */
    private Integer getDepTimeRangeConfig(String itemName) {
        int itemValue = 0;
        SystemConfiguration systemConfiguration = systemConfigurationService.getOneByItemName(itemName);
        if(systemConfiguration != null) {
            try {
                itemValue = Integer.parseInt(systemConfiguration.getCurrentValue());
            }catch(NumberFormatException e){
                LOG.error("Error on parse the value for the property : {}", itemName);
            }
        }
        return itemValue;
    }

    /**
     * This method is use when don't have depTime (from other source: Tower, ATC,...).
     * This method find all flight movement that have (arrivalTime - EET) ~ (depTime +/- DepTimeRange).
     *
     * @param flightMovements flight movements to check
     * @param flightId flight movement flight id
     * @param dateOfContact flight movement date of contact
     * @param arrivalTime flight movement arrival time
     * @param depAd flight movement departure aerodrome
     * @return list of flight movements found
     */
    private List<FlightMovement> findLatestFlightMovementByArrivalTimeAndEET(List<FlightMovement> flightMovements, String flightId, LocalDateTime dateOfContact, String arrivalTime, String depAd){

        List<FlightMovement> flightMovementResult =null;
        if(flightMovements!=null){
            flightMovementResult=new ArrayList<>();

            for(FlightMovement flightMovement: flightMovements) {

                if (StringUtils.isNotBlank(flightMovement.getEstimatedElapsedTime())) {
                    Integer minutesEstimatedElapsedTime = DateTimeUtils.getMinutes(flightMovement.getEstimatedElapsedTime());
                    LocalDateTime estimatedDateOfFlightAndDepTime = DateTimeUtils.minusMinute(dateOfContact, arrivalTime, minutesEstimatedElapsedTime);
                    if (estimatedDateOfFlightAndDepTime != null) {
                        String estimatedDepTime = DateTimeUtils.convertLocalDateTimeToTimeString(estimatedDateOfFlightAndDepTime);
                        LocalDateTime estimatedDateOfFlight = LocalDateTime.of(estimatedDateOfFlightAndDepTime.toLocalDate(), LocalTime.MIDNIGHT);
                        final FlightMovement tmp = this.getFlightMovementByLogicalKey(flightId, estimatedDateOfFlight, estimatedDepTime, depAd, "0000");
                        if (tmp != null) {
                            flightMovementResult.add(tmp);
                        }
                    }
                }
            }
        }
        return flightMovementResult;
    }

    /**
     * Helper method for Sort FlightMovement List by Descending order on dateOfFlight and depTime.
     *
     * @return Sort
     */
    private static Sort orderByDateOfFlightAndDepTime() {
        return new Sort(Sort.Direction.DESC, KEY_DATE_OF_FLIGHT, "depTime");
    }

    /**
     *
     * @param flightMovement flight movement to validate
     * @throws FlightMovementBuilderException flight movement builder exception
     */
    public void checkValidValues(final FlightMovement flightMovement) throws FlightMovementBuilderException {
        String depAd = flightMovementAerodromeService.checkAerodromeIdentifier(flightMovement.getDepAd(), true, true);
        String destAd = flightMovementAerodromeService.checkAerodromeIdentifier(flightMovement.getDestAd());

        if (StringUtils.isBlank(depAd)) {
            new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_AERODROME_VALIDATION + " " + flightMovement.getDepAd())
                .addInvalidField(flightMovement.getClass(), "depAd", ErrorConstants.ERR_AERODROME_VALIDATION, flightMovement.getDepAd())
                .throwInvalidDataException();
        }

        if (StringUtils.isBlank(destAd)) {
            new ErrorDTO.Builder()
                .setErrorMessage(ErrorConstants.ERR_AERODROME_VALIDATION + " " + flightMovement.getDestAd())
                .addInvalidField(flightMovement.getClass(), "destAd", ErrorConstants.ERR_AERODROME_VALIDATION, flightMovement.getDestAd())
                .throwInvalidDataException();
        }

        // Domestic and ARRIVAL flights must have Estimated elapsed Time
        // ZZZZ is reserved for domestic flights only
        if((flightMovement.getDestAd().equals(ApplicationConstants.PLACEHOLDER_ZZZZ) || flightMovementAerodromeService.isAerodromeDomestic(flightMovement.getDestAd()))
            && (StringUtils.isBlank(flightMovement.getEstimatedElapsedTime()))) {

            new ErrorDTO.Builder("Missing estimated elapsed time")
                .appendDetails("The estimated elapsed time is required when the arrival aerodrome is domestic")
                .addInvalidField(flightMovement.getClass(), "estimatedElapsedTime", ErrorConstants.ERR_REQUIRED)
                .throwInvalidDataException();
        }

        if (flightMovementAerodromeService.isCircularRoute(flightMovement.getDepAd(),flightMovement.getItem18Dep(),
                flightMovement.getDestAd(),flightMovement.getItem18Dest())) {

            boolean isEmptyEET = StringUtils.isBlank(flightMovement.getEstimatedElapsedTime());
            boolean isEmptyCSMN = StringUtils.isBlank(flightMovement.getCruisingSpeedOrMachNumber());

            if (isEmptyEET || isEmptyCSMN) {
                final ErrorDTO.Builder errorBuilder = new ErrorDTO.Builder(ErrorConstants.ERR_CIRCULAR_ROUTE);
                if (isEmptyEET) {
                    errorBuilder.addInvalidField(flightMovement.getClass(), "estimatedElapsedTime", "is empty");
                }
                if (isEmptyCSMN) {
                    errorBuilder.addInvalidField(flightMovement.getClass(), "cruisingSpeedOrMachNumber", "is empty");
                }
                errorBuilder.throwInvalidDataException();
            }
        }
    }

    public boolean isIcaoAerodrome(final String aerodromeId) {
        return flightMovementAerodromeService.isIcaoAerodrome(aerodromeId);
    }

    public boolean[] areValidIcaoAerodromes (final String ... aerodromes) {
        if (aerodromes == null || aerodromes.length <= 0) {
            throw new IllegalArgumentException("Invalid aerodrome list");
        }
        boolean[] validAerodromes = new boolean[aerodromes.length];
        for (int i=0; i<aerodromes.length; i++) {
            validAerodromes[i] = flightMovementAerodromeService.isIcaoAerodrome(aerodromes[i]);
        }
        return validAerodromes;
    }

    private List<FlightMovement> findFlightMovementsForAviationBilling(ZonedDateTime zdtStart, ZonedDateTime zdtEnd, final boolean userBillingCenterOnly,
                                                                       final List<Integer> accountIdList,
                                                                       final boolean iata, final BillingCenter bc) {
        assert (bc != null);
        List<FlightMovement> flightMovementList = null;
        if( zdtStart != null || zdtEnd != null){
            final Date dateStart = null;
            if(zdtStart != null)
                Date.from (zdtStart.toInstant());
            final Date dateEnd = null;
            if(zdtEnd != null)
                Date.from (zdtEnd.toInstant());
            // accountIdList and iataStatus fields are optional
            // the following logic is used because currently we don't create SQL statements dynamically
            if ((accountIdList == null || accountIdList.isEmpty())){
                if (userBillingCenterOnly) {
                    flightMovementList = flightMovementRepository.findForAviationBillingByMonthYearIatastatus(dateStart, dateEnd, iata, bc.getId());
                }
                else {
                    flightMovementList = flightMovementRepository.findForAviationBillingByMonthYearIatastatus(dateStart, dateEnd, iata);
                }
            } else if(!accountIdList.isEmpty()){
                if (userBillingCenterOnly) {
                    flightMovementList = flightMovementRepository.findForAviationBillingByMonthYearIatastatusAccountlist(dateStart, dateEnd, iata, accountIdList, bc.getId());
                }
                else {
                    flightMovementList = flightMovementRepository.findForAviationBillingByMonthYearIatastatusAccountlist(dateStart, dateEnd, iata, accountIdList);
                }
            }
        }
        return flightMovementList;
    }

    private List<FlightMovement> findFlightMovementsForAviationBillingValidation(final LocalDateTime startDate,
                                                                                 final LocalDateTime endDate,
                                                                                 final boolean userBillingCenterOnly,
                                                                                 final List<Integer> accountIdList,
                                                                                 final boolean iata,
                                                                                 final BillingCenter bc,
                                                                                 final Integer flightCategory) {
        Preconditions.checkArgument(bc != null);
        final boolean invoiceByDateOfFlight = systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT);

        return this.flightMovementRepository.findAll((root, query, criteriaBuilder) -> {
            final List <Predicate> andList = new ArrayList<>();

            andList.add(criteriaBuilder.or(criteriaBuilder.equal(root.get(KEY_STATUS), FlightMovementStatus.PENDING),
                criteriaBuilder.equal(root.get(KEY_STATUS), FlightMovementStatus.INCOMPLETE)));

            if (iata) {
                andList.add(criteriaBuilder.equal(root.join(KEY_ACCOUNT).get(IATA), iata));
            } else {
                andList.add(criteriaBuilder.or(criteriaBuilder.equal(root.join(KEY_ACCOUNT, JoinType.LEFT).get(IATA), iata),
                    criteriaBuilder.isNull(root.join(KEY_ACCOUNT, JoinType.LEFT))));
            }

            if (flightCategory != null) {
                andList.add(criteriaBuilder.equal(root.join(KEY_FLIGHT_MOVEMENT_CATEGORY).get(KEY_ID), flightCategory));
            } else {
                andList.add(criteriaBuilder.notEqual(root.join(KEY_FLIGHT_MOVEMENT_CATEGORY).get(KEY_NAME), "OTHER"));
            }

            if (startDate != null && endDate != null) {
                LocalDateTime startAt = startDate.with(LocalTime.MIN);
                LocalDateTime endAt = endDate.with(LocalTime.MAX);
                if (invoiceByDateOfFlight) {
                    andList.add(criteriaBuilder.between(root.get(KEY_DATE_OF_FLIGHT), startAt, endAt));
                } else {
                    andList.add(criteriaBuilder.between(root.get(KEY_BILLING_DATE), startAt, endAt));
                }
            }

            if (userBillingCenterOnly) {
                andList.add(criteriaBuilder.equal(root.join(KEY_BILLING_CENTER).get(KEY_ID), bc.getId()));
            }

            if (accountIdList != null && !accountIdList.isEmpty()) {
                andList.add(root.join(KEY_ACCOUNT).get(KEY_ID).in(accountIdList));
            }

            // Combine all predicates
            return criteriaBuilder.and(andList.toArray(new Predicate[0]));
        });
    }

    /**
     * Used as a shorthand for notifying a item loader observer
     * @param o observer to notify
     * @param result result to pass to observer
     */
    private void notifyItemLoaderObserver(ItemLoaderObserver o, ItemLoaderResult result) {
        if(o != null && result != null) {
            o.update(this, result);
        }
    }

    /**
     * This method is used to check and update subsequent departure flight movement charges.
     *
     * The main purpose of this method is for parking time, parking charge, and total charge amounts
     * of subsequent departure flight movements who's amounts mentioned are depending on a prior arrival.
     *
     * @param providedFlightMovement prior flight movement
     * @return subsequent flight movement
     */
    private FlightMovement updateSubsequentDepartureFlightMovementCharges(FlightMovement providedFlightMovement) {

        // assert that prior flight movement is not null
        if (providedFlightMovement == null)
            return null;

        // only apply to movement types DOMESTIC, INT_ARRIVAL, REG_ARRIVAL
        if(providedFlightMovement.getFlightCategoryType() != null &&
                (providedFlightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.DEPARTURE) ||
                    providedFlightMovement.getFlightCategoryType().equals(FlightmovementCategoryType.OVERFLIGHT) ||
                    providedFlightMovement.isOTHER())){
            return null;
        }

        // find subsequent departure flight movement
        FlightMovement subsequentFlightMovement = this.findSubsequentDeparture(providedFlightMovement);

        // assert subsequent flight movement is not null and not already INVOICED or PAID
        if (isNotValidSubsequentDeparture(subsequentFlightMovement))
            return null;

        // detach flight movement from context before continuing
        flightMovementRepositoryUtility.detach(subsequentFlightMovement);

        // update subsequent departure flight movement charges
        Boolean isRecalculated = flightMovementBuilder.recalculateCharges(subsequentFlightMovement);

        // if charges set, persist changes
        if (isRecalculated) {
            flightMovementBuilder.handleStatus(subsequentFlightMovement, false);
            return flightMovementRepositoryUtility.persist(subsequentFlightMovement);
        } else {
            return null;
        }
    }

    /**
     * Subsequent departures cannot be updated if the flight movement status is not INCOMPLETE,
     * PENDING, or partially applied to an invoice.
     */
    private boolean isNotValidSubsequentDeparture(final FlightMovement flightMovement) {

        boolean invalid;

        if (flightMovement == null)
            invalid = true;
        else if (flightMovement.getEnrouteInvoiceId() != null)
            invalid = true;
        else if (flightMovement.getOtherInvoiceId() != null)
            invalid = true;
        else if (flightMovement.getPassengerInvoiceId() != null)
            invalid = true;
        else
            invalid = flightMovement.getStatus() != FlightMovementStatus.INCOMPLETE &&
                flightMovement.getStatus() != FlightMovementStatus.PENDING;

        return invalid;
    }

    /**
     * Find subsequent denature for provided FlightMovement.
     *
     * @return first subsequent departure
     */
    private FlightMovement findSubsequentDeparture(FlightMovement providedFlightMovement) {

        // confirm necessary values are not null
        if (providedFlightMovement == null || providedFlightMovement.getDateOfFlight() == null
            || providedFlightMovement.getItem18RegNum() == null || providedFlightMovement.getArrivalAd() == null)
            return null;

        // convert dateOfFlight LocalDateTime to Date value
        Date dateOfFlight = JSR310DateConverters
            .convertLocalDateTimeToDate(providedFlightMovement.getDateOfFlight());

        // set item18RegNum and arrivalAd as provided flight movement values
        String item18RegNum = providedFlightMovement.getItem18RegNum();
        String arrivalAd = providedFlightMovement.getArrivalAd();

        // attempt to get arrival time by provided flight movement value or estimate
        String arrivalTime = providedFlightMovement.getArrivalTime();

        // if arrival time is null, attempt to set from departure time plus estimated elapsed time
        // this will also update the date of flight value if necessary
        if (StringUtils.isEmpty(arrivalTime) && StringUtils.isNotEmpty(providedFlightMovement.getDepTime())
            && StringUtils.isNotEmpty(providedFlightMovement.getEstimatedElapsedTime())) {

            // set departure time as LocalTime
            LocalTime depTime = DateTimeUtils.convertStringToLocalTime(providedFlightMovement.getDepTime());

            // set estimated elapsed time as minutes
            Integer estimatedElapsedTime = DateTimeUtils
                .getMinutes(providedFlightMovement.getEstimatedElapsedTime());

            // set date and time of flight from dateOfFlight and depTime plus estimatedElapsedTime
            // it is done this way so that dep time plus eet greater then 24 hours increases the date
            // value as well
            LocalDateTime dateTimeOfFlight = DateTimeUtils.addTimeToDate(providedFlightMovement.getDateOfFlight(),
                depTime.plusMinutes(estimatedElapsedTime));

            // update date of flight from local date time value
            dateOfFlight = JSR310DateConverters
                .convertLocalDateTimeToDate(dateTimeOfFlight.toLocalDate().atStartOfDay());

            // update arrival time from local date time value
            arrivalTime = DateTimeMapperUtils.parseSystemTime(dateTimeOfFlight.toLocalTime());
        }

        // confirm calculated values are not null
        if (dateOfFlight == null || StringUtils.isEmpty(item18RegNum) || StringUtils.isEmpty(arrivalAd)|| StringUtils.isEmpty(arrivalTime))
            return null;

        // get all matching subsequent flight movements
        List<FlightMovement> subsquentFlightMovements = flightMovementRepository.findSubsequentDepartures(
            dateOfFlight, item18RegNum, arrivalAd, arrivalTime);

        // return if no records found
        if (subsquentFlightMovements == null || subsquentFlightMovements.isEmpty())
            return null;

        /*
         * Find first subsequent departure that matches arrivalAd.
         *
         * This is accomplished by looping through each subsequent flight movement found and determining if either
         * depAd or item18Dep match arrivalAd.
         *
         * This is required as ZZZZ depAd need to parse item18Dep field in order to validate.
         */
        for (FlightMovement fm : subsquentFlightMovements) {

            // continue if no fm movement or dep ad
            if (fm == null || StringUtils.isEmpty(fm.getDepAd()))
                continue;

            // return flight movement if depAd matches arrivalAd
            if (fm.getDepAd().equalsIgnoreCase(arrivalAd))
                return fm;

            // continue if depAd does not equal ZZZZ or AFIL or item18Dep does not exists
            if (!(fm.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_ZZZZ) || fm.getDepAd().equalsIgnoreCase(ApplicationConstants.PLACEHOLDER_AFIL))
                || fm.getItem18Dep() == null)
                continue;

            // get item18Dep aerodrome identifier if possible
            String item18Dep = Item18Parser.getFirstAerodrome(fm.getItem18Dep());

            // return flight movement if item18Dep matches arrivalAd
            if (item18Dep != null && item18Dep.equalsIgnoreCase(arrivalAd))
                return fm;
        }

        // return null indicating no matches
        return null;
    }

    /**
     * Checks flight Movement fileds whwn is called from jobs
     *
     */
    private void checkFlightMovementFromJobs(FlightMovement aFlightMovement) {
        if (aFlightMovement.getBillingDate() == null) {
            aFlightMovement.setBillingDate(aFlightMovement.getDateOfFlight().toLocalDate().atStartOfDay());
        }
    }

    public List<Object> getDistinctRoutes() {
        return flightMovementRepository.findDistinctRoutes();
    }

    public List<Object> getDistinctFlightLevels() {
        return flightMovementRepository.findDistinctFlightLevels();
    }

    @Transactional(readOnly = true)
    public List<FlightMovement> findAllByFlightTypeIntervalDate(LocalDateTime startInterval, LocalDateTime endInterval) {
        List<FlightMovement> flightMovements = null;

        if (startInterval != null && endInterval != null && startInterval.isBefore(endInterval)) {
            Date startDate = JSR310DateConverters.convertLocalDateTimeToDate(startInterval);
            Date endDate = JSR310DateConverters.convertLocalDateTimeToDate(endInterval);

            flightMovements = flightMovementRepository.findAllByFlightTypeIntervalDate(startDate, endDate);
        } else {
            LOG.debug("Cannot get flight movements, startInterval={}, endInterval={}", startInterval, endInterval);
        }

        return flightMovements;
    }

    /**
     * THRU flights can be matched with radar sumamry as following
     * LogicalKey is
     * Radar.FlightID = FlightMovement.FlightID  - Mandatory
     * Radar.DayOfFlight = FlightMovement.DayOfFlight  - Mandatory
     *
     * @param radarSummary radar summary
     * @return flight movement found
     */
    @Transactional(readOnly = true)
    public List<FlightMovement> findThruFlightMovementsFromRadarSummary(RadarSummary radarSummary) {

        if(radarSummary == null) {
            throw new IllegalArgumentException("Invalid radar summary");
        }

        List<FlightMovement> list = null;
        final String flightId = radarSummary.getFlightIdentifier();
        LocalDateTime date = radarSummary.getDayOfFlight();
        if (date == null){
            date = radarSummary.getDate();  // the date of contact if day of flight has not been calculated
        }

        LOG.debug("Finding Flight Movements from RadarSummary by  flightID: {}, dateOfFlight: {}",
            flightId,date);

        if (StringUtils.isNotEmpty(flightId) && date!=null){

            list = flightMovementRepository.findAllByFlightIdAndDateOfFlight(flightId, date);
        } else {
            LOG.warn("{} flightID: {}, date: {}", FlightMovementBuilderIssue.ORPHAN_ITEM, flightId, date);
        }
        return list;
    }


    private void deleteDuplicatedRadarFM(FlightMovement flightMovement) {

        // find possible FM created from partial radar summaries and remove those
        // FM created from radar summaries should have the same fields: flightId,dayOfFlight, and
        // deptime should be >= FM.deptime and <= arrival time from the last segment
        if (flightMovement != null) {
            LOG.debug ("attempting to remove duplicate radar flight movements");
            LocalDateTime startDateFpl = DateTimeUtils.addTimeToDate(flightMovement.getDateOfFlight(), flightMovement.getDepTime());

            List<ThruFlightPlanVO> map = thruFlightPlanUtility.parseThruPlanRoute(flightMovement.getFplRoute(),
                flightMovement.getDepAd(), startDateFpl);

            // default start date is the time of departure, default end date is the time of departure + estimated elapsed time
            LocalDateTime startDate = startDateFpl;
            LocalDateTime endDate = DateTimeUtils.addTimeToDate(startDateFpl,flightMovement.getEstimatedElapsedTime());
            if (map != null && !map.isEmpty()) {
                if (map.get(0) != null)
                    startDate = map.get(0).getDepTime();

                int size = map.size();
                if (map.get(size - 1) != null)
                    endDate = map.get(size - 1).getArrivalTime();
            }

            List<FlightMovement> list = flightMovementRepository.findThruFlightsByTimeInterval(flightMovement.getFlightId(),
                flightMovement.getDateOfFlight(),FlightMovementSource.RADAR_SUMMARY.toValue() );
            if (list != null && !list.isEmpty()) {
                for (FlightMovement fm : list) {
                    LocalDateTime dep = DateTimeUtils.addTimeToDate(fm.getDateOfFlight(), fm.getDepTime());
                    if(
                            (
                                (fm.getDepTime() != null && fm.getDepTime().equals(flightMovement.getDepTime()))
                                    ||
                                    (dep.isAfter(startDate) && dep.isBefore(endDate))
                            )
                            &&
                            (flightMovement.getId() == null || !fm.getId().equals(flightMovement.getId()))
                    ) {
                        LOG.debug("Deleting Flight Movements from RadarSummary flightID: {}, dateOfFlight: {}, depTime: {}",
                                fm.getFlightId(), fm.getDateOfFlight(), fm.getDepTime());
                        deleteFlightMovement(fm.getId());
                    }
                }
            }
        }
    }

    public List<Object> getDistinctRegNum () {
        return flightMovementRepository.findDistinctRegNum();
    }

    private ResultRecords synchronizeCumulativeFlights(FlightMovement flightMovement) {
    	ResultRecords result = new ResultRecords();
        result.setTotalRecords(0);
        result.setChangedRecords(0);

    	if (flightMovement != null &&
    			flightMovement.getBillableCrossingDist() != null && flightMovement.getBillableCrossingDist() > 0.0 &&
    			flightMovement.getBillableCrossingDist() < FlightMovementConstants.EANA_MINIMUM_INTERNATIONAL_DISTANCE &&
        		systemConfigurationService.getBillingOrgCode() == BillingOrgCode.EANA &&
        		flightMovement.getFlightCategoryScope() != null &&	flightMovement.getFlightCategoryScope().equals(FlightmovementCategoryScope.INTERNATIONAL) &&
        				flightMovement.getAccount() != null && flightMovement.getAccount().getId() != null &&
        						StringUtils.isNotBlank(flightMovement.getWakeTurb())) {


    		// System configuration item INVOICE_BY_DAY_OF_FLIGHT is used to determine if the flights are grouped
    		// by billing_date or by date_of_flight.
    		// if INVOICE_BY_DAY_OF_FLIGHT == true, group by date_of_flight
    		// else group by billing_date.
       		List<FlightMovement> list = null;
    		if(systemConfigurationService.getBoolean(SystemConfigurationItemName.INVOICE_BY_DAY_OF_FLIGHT)){
    			list = flightMovementRepository.findAllCumulativeByAccountAndWakeTurbTypeDOF(flightMovement.getAccount().getId(),
						flightMovement.getWakeTurb(),flightMovement.getDateOfFlight());
    		} else {
    			list = flightMovement.getBillingDate() == null ? null
        				:flightMovementRepository.findAllCumulativeByAccountAndWakeTurbTypeBD(flightMovement.getAccount().getId(),
        						flightMovement.getWakeTurb(),flightMovement.getBillingDate());
    		}

    		int i=0;
    		if(list !=null && !list.isEmpty()) {
    			boolean chargesSet;
    			for(FlightMovement fm : list){
    				if(fm != null && fm.getId() != null){
    				    flightMovementRepositoryUtility.detach(fm);
    					chargesSet = flightMovementBuilder.recalculateCharges(fm);

    					if(chargesSet){
    						i = i + 1;
    						flightMovementBuilder.handleStatus(fm, false);
    			            flightMovementRepositoryUtility.persist(fm);
    					}
    				}
    			}
    		}
    		result.setTotalRecords(i);
    	}
    	return result;
    }

    /**
     * scheduled task will run at midnight every day
     * System configuration: After 30 days:
	 * Check for zero cost flights on a daily basis and if the day of flight is 30 or more days in the past,
	 * the flight will be marked as paid.
     */
    @Scheduled(cron = "00 00 00 * * *")
    public void markPaidOldZeroCostFlights(){

    	final SystemConfiguration sc = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MARK_ZERO_FLIGHT_COSTS_AS_PAID);
        if(sc != null && sc.getCurrentValue() != null && sc.getCurrentValue().equalsIgnoreCase("After 30 days")){
        	LOG.debug("Setting zero charge flights to PAID");
       		int paid = flightMovementRepository.setOldZeroCostFlightsPaid();
       		LOG.debug("Set  [ {} ] zero charge flights to PAID",paid);
        }
    }

    public ResultRecords setZeroCostFlightsPaid(List<Integer> flightIds){
    	ResultRecords result = new ResultRecords();

        result.setTotalRecords(0);
        result.setChangedRecords(0);
        int updated =0;
    	final SystemConfiguration sc = systemConfigurationService.getOneByItemName(SystemConfigurationItemName.MARK_ZERO_FLIGHT_COSTS_AS_PAID);
        if(sc != null && sc.getCurrentValue() != null && sc.getCurrentValue().equalsIgnoreCase("Manually") &&
        		flightIds !=null && !flightIds.isEmpty()){

        	List<Integer> list = new ArrayList<>();
           	for(Integer id:flightIds) {

            	FlightMovement fm= flightMovementRepository.getOne(id);
            	if(fm != null && fm.getStatus() != null && fm.getStatus().equals(FlightMovementStatus.PENDING) && fm.getTotalCharges() == 0.0){
            		list.add(fm.getId());
            	}
           	}
           	if(!list.isEmpty()) {
           		updated =flightMovementRepository.setZeroCostFlightsPaid(list);
           	}
        }
        result.setTotalRecords(updated);
        return result;
    }

    private static String formatIdList (final List <FlightMovement> list) {
        if (list != null && !list.isEmpty()) {
            return list.stream()
                    .map (x->String.format ("id=%s", x.getId()))
                    .collect (Collectors.joining(", "))
            ;
        }
        return "";
    }

    /**
     * This method will suppress a bad status exception if running from a bulk loader process. Otherwise,
     * it will throw an exception that will be displayed on the front-end.
     */
    @SuppressWarnings("squid:S1192")
    static void handleBadStatus(final FlightMovementStatus status, final String displayName) {
        LOG.info("FlightMovement can't be updated because status is {}", status);

        // US 106272: ignore existing invoiced flight movement if bulk loader context
        if (Boolean.TRUE.equals(BillingContext.get(BillingContextKey.BULK_LOADER))) {
            LOG.debug(MSG_IGNORE_SURVEILLANCE_LOG, displayName);
            return;
        }

        // throw exception indicating that the flight movement cannot be updated
        final String translatedStatusCode = status == null ? null
            : Translation.getLangByToken(status.toString());
        throw new ErrorDTO.Builder()
            .addRejectedReason (RejectedReasons.VALIDATION_ERROR)
            .setErrorMessage (ErrorConstants.ERR_FLIGHT_MOVEMENT_UPDATE_BAD_STATUS)
            .addErrorMessageVariable ("status", translatedStatusCode)
            .buildInvalidDataException();
    }

    private void postCreateUpdate(FlightMovement flightMovementResult, FlightMovement fmFromDb) {
        /*
         * Update subsequent departure flight movement charges.
         *
         * The main purpose of this method is for parking time, parking charge, and total charge amounts
         * of subsequent departure flight movements who's amounts mentioned are depending on a prior arrival.
         */
        this.updateSubsequentDepartureFlightMovementCharges(flightMovementResult);

        /*
         * EANA feature. Synchronize cumulative international flights with billable crossing distance < 200 km
         */
        this.synchronizeCumulativeFlights(flightMovementResult);

        /*
         * EANA feature. If this flight was during closing hours, all other flights with the same reg
         * number that exist during the same hours should be recalculated (extended hours surcharge)
         */
        this.updateFlightsChargesDuringAerodromeClosingHours(flightMovementResult, fmFromDb);
    }

    /*
     * EANA feature.
     * If this flight was during closing hours, all other flights with the same reg number that exist
     * during the same hours should be recalculated (extended hours surcharge)
     * The extended hours surcharge is applied to the departure flight only. Any further departures/arrivals
     * after the initial pair do not incur any additional surcharges
     */
    private void updateFlightsChargesDuringAerodromeClosingHours(final FlightMovement modifiedFlightMovement,
                                                                 final FlightMovement existingFlightMovement) {

        if (modifiedFlightMovement == null || systemConfigurationService.getBillingOrgCode() != BillingOrgCode.EANA ||
            Boolean.FALSE.equals(systemConfigurationService.getBoolean(SystemConfigurationItemName.EXTENDED_HOURS_SURCHARGE_SUPPORT)) ||
            modifiedFlightMovement.getItem18RegNum() == null && (existingFlightMovement == null || existingFlightMovement.getItem18RegNum() == null)) {
            return;
        }
        List<FlightMovement> list = aerodromeOperationalHoursService.getListFlightMovementsForRecalculation(modifiedFlightMovement, DEPARTURE);
        List<FlightMovement> finalList = new ArrayList<>(list);

        list = aerodromeOperationalHoursService.getListFlightMovementsForRecalculation(modifiedFlightMovement, ARRIVAL);
        finalList.addAll(list);

        if (existingFlightMovement != null) {
            if (flightMovementKeyDataChanged(modifiedFlightMovement, existingFlightMovement, DEPARTURE)) {
                list = aerodromeOperationalHoursService.getListFlightMovementsForRecalculation(existingFlightMovement, DEPARTURE);
                finalList.addAll(list);
            }

            if (flightMovementKeyDataChanged(modifiedFlightMovement, existingFlightMovement, ARRIVAL)) {
                list = aerodromeOperationalHoursService.getListFlightMovementsForRecalculation(existingFlightMovement, ARRIVAL);
                finalList.addAll(list);
            }
        }

        List<FlightMovement> listWithoutDuplicates = finalList.stream().filter(fm -> !Objects.equals(fm.getId(), modifiedFlightMovement.getId())).distinct()
            .sorted(Comparator.comparing(FlightMovement::getDateOfFlight)
            .thenComparing(FlightMovement::getDepTime)).collect(Collectors.toList());

        for (FlightMovement fm: listWithoutDuplicates) {
            if (fm.getStatus().equals(FlightMovementStatus.PENDING) || fm.getStatus().equals(FlightMovementStatus.INCOMPLETE)) {
                flightMovementRepositoryUtility.detach(fm);
                if (flightMovementBuilder.recalculateCharges(fm)){
                    flightMovementBuilder.handleStatus(fm, false);
                    flightMovementRepositoryUtility.persist(fm);
                }
            }
        }
    }

    private boolean flightMovementKeyDataChanged (FlightMovement modifiedFlightMovement, FlightMovement existingFlightMovement, String flightType) {
        LocalDateTime modifiedDateOfFlight = modifiedFlightMovement.getDateOfFlight();
        String modifiedRegNum = modifiedFlightMovement.getItem18RegNum();
        String modifiedDepAd = modifiedFlightMovement.getDepAd();
        String modifiedDestAd = modifiedFlightMovement.getDestAd();
        String modifiedDepTime = modifiedFlightMovement.getDepTime();
        String modifiedDestTime = modifiedFlightMovement.getArrivalTime();

        LocalDateTime existingDateOfFlight = existingFlightMovement.getDateOfFlight();
        String existingRegNum = existingFlightMovement.getItem18RegNum();
        String existingDepAd = existingFlightMovement.getDepAd();
        String existingDestAd = existingFlightMovement.getDestAd();
        String existingDepTime = existingFlightMovement.getDepTime();
        String existingDestTime = existingFlightMovement.getArrivalTime();

        if (modifiedDateOfFlight != existingDateOfFlight && !Objects.equals(modifiedRegNum, existingRegNum)) {
            return true;
        } else if (flightType.equals(DEPARTURE)) {
            return !modifiedDepAd.equals(existingDepAd) || !modifiedDepTime.equals(existingDepTime);
        } else {
            return !modifiedDestAd.equals(existingDestAd) || !Objects.equals(modifiedDestTime, existingDestTime);
        }
    }

    private FlightMovement setFlightMovementFieldsForComparison(FlightMovement fmFromDb, FlightMovement existingFlightMovement) {
        fmFromDb.setDateOfFlight(existingFlightMovement.getDateOfFlight());
        fmFromDb.setItem18RegNum(existingFlightMovement.getItem18RegNum());
        fmFromDb.setDepAd(existingFlightMovement.getDepAd());
        fmFromDb.setDestAd(existingFlightMovement.getDestAd());
        fmFromDb.setArrivalAd(existingFlightMovement.getArrivalAd());
        fmFromDb.setDepTime(existingFlightMovement.getDepTime());
        fmFromDb.setArrivalTime(existingFlightMovement.getArrivalTime());
        fmFromDb.setEstimatedElapsedTime(existingFlightMovement.getEstimatedElapsedTime());

        return fmFromDb;
    }

    public Boolean isDuplicate(FlightMovementViewModel flight,FlightMovementViewModel tempFlight) {
        if(flight != null && tempFlight != null) {

           if(flight.getId() != null && tempFlight.getId()!=null &&
                flight.getId().equals(tempFlight.getId())) {
               return  false;
           }

            if (flight.getFlightId().equals(tempFlight.getFlightId()) &&
                flight.getDepAd().equals(tempFlight.getDepAd()) &&
                dateTimeMatch(flight,tempFlight)) {
                return  true;
            }
        }
        return false;
    }

    private boolean dateTimeMatch(FlightMovementViewModel newflight,FlightMovementViewModel existFlight) {

        final Integer configPrctValue = systemConfigurationService.getInteger(SystemConfigurationItemName.DEP_TIME_RANGE_EET_PERCENTAGE, 0);
        final Integer configMinValue = systemConfigurationService.getInteger(SystemConfigurationItemName.DEP_TIME_RANGE_MIN, 0);

        // ensure both departure time and and estimated elapsed time are 4 digits long if defined
        // default estimated elapsed time to "0000" if not defined
        if(StringUtils.isBlank(newflight.getDepTime()) || StringUtils.isBlank(existFlight.getDepTime())) {
            return false;
        }
        String depTimeNew = newflight.getDepTime().length() == 4 ? newflight.getDepTime()
            : StringUtils.leftPad(newflight.getDepTime(), 4, '0');

        String depTimeExist = existFlight.getDepTime().length() == 4 ? existFlight.getDepTime()
                : StringUtils.leftPad(existFlight.getDepTime(), 4, '0');

        String eet = StringUtils.isBlank(existFlight.getEstimatedElapsedTime()) ? "0000"
            : StringUtils.leftPad(existFlight.getEstimatedElapsedTime(), 4, '0');

        //calculate range value in minutes from percentage of flight movement estimated elapsed time
        long timeRange = 0;
        if(StringUtils.isNotBlank(eet)) {
            timeRange = (Long.valueOf(eet.substring(0,2)) * 60  + Long.valueOf(eet.substring(2))) * configPrctValue/100;
        }

        //if calculated range is less then min, set range to min
        if (timeRange < configMinValue)
            timeRange =configMinValue;

        // convert lookup time value into numeric number of minutes

        long timeVal = Long.valueOf(depTimeExist.substring(0,2)) * 60  + Long.valueOf(depTimeExist.substring(2));

        // set min time value and max time value from _timeVal plus/minus _timeRange
        long timeMin = timeVal - timeRange;
        long timeMax = timeVal + timeRange;

        // if min time is less then 0, calculate time and end date in past days from lookup date
        // else, set start date to the lookup date
        LocalDateTime date = existFlight.getDateOfFlight();
        LocalDateTime dateStart;
        if (timeMin < 0) {
            dateStart = date.minusDays(1);
            timeMin = 1440 - Math.abs(timeMin);
        } else
            dateStart = date;


        //if max time is greater then 1440, calculate time and end date in future days from lookup date
        // else, set end date to the lookup date
        LocalDateTime dateEnd;
        if(timeMax > 1440) {
           dateEnd = date.plusDays(1);
           timeMax = timeMax - 1440;
        } else
            dateEnd = date;


        // if flight movement date of flight is outside calculated date range,
        // return false for no match
        if(newflight.getDateOfFlight().isBefore(dateStart) || newflight.getDateOfFlight().isAfter(dateEnd)){
            return false;
        }

        // convert flight movement dep time into numeric number of minutes
        long timeValNew = Long.valueOf(depTimeNew.substring(0,2)) * 60  + Long.valueOf(depTimeNew.substring(2));

        //if flight movement date of flight is equal to calculated min/max and dep time outside calcualted time range,
        //return false for no match
        if((newflight.getDateOfFlight().isEqual(dateStart) && timeValNew < timeMin) ||
                (newflight.getDateOfFlight().isEqual(dateEnd) && timeValNew > timeMax)){
            return false;
        } else {
            //else return true for a match within the date and time range
            return true;
        }
    }

    private void clearCurrentSession() {
        Session session = flightMovementRepository.getThisCurrentSession();
        if (session != null) {
            session.flush();
            session.clear();
        }
    }

    /**
     * Where the ANSP’s workflow requires mandatory prepayment for any flight
     * which is not associated with an account which has a credit facility with the ANSP,
     * the whitelisting process is used to ensure prepayment is used. If the flight is not prepaid,
     * the flight is declined and a notice will be sent to the originator.  If the flight has been prepaid,
     * the flight is accepted and a confirmation notice is sent to the originator.
     *
     * When a flight plan is received, three checks are done to determine if the whitelisting process is to be applied. These checks are for:
     * - flights have a zero-cost for any reason, including exemptions;
     * - flights which are associated with a credit account;
     * - flights which are associated with a non-credit account which has an unexpired prepaid balance greater than the cost of the flight
     *
     * If the flight does not meet any of these criteria,
     * it is marked as declined and a notice is sent to the originator and to the area ATCs (Area Control Centres)
     * @param flightMovement Flight movement received from Cronos
     */
    public void checkWhitelisting(FlightMovement flightMovement) {
        LOG.debug("Check if whitelisting is applied");
        LocalDate whitelistingStartDate = whitelistingUtils.getWhitelistingStartDate();

        if (whitelistingStartDate == null) {
            LOG.debug("Whitelisting is not applied");
            return;
        }

        LocalDate dateOfFlight = flightMovement.getDateOfFlight().toLocalDate();
        if (whitelistingStartDate.isAfter(dateOfFlight)) {
            LOG.debug("Whitelisting is not applied because start date is after date of flight");
            return;
        }

        checkFlightMovementCostForWhitelisting(flightMovement, whitelistingStartDate);
    }

    private boolean checkFlightMovementExceptionsForWhitelisting(FlightMovement flightMovement) {

        String flightType = nvl(flightMovement.getFlightType(), "");
        String item18Status = nvl(flightMovement.getItem18Status(), "");

        // If the flight is military (M) and item 18 contains STS/STATE AIRCRAFT – flight is handled as a state flight

        //noinspection ConstantConditions - these Strings can never be null. Suppression due to java limitation of the return value
        if (flightType.equalsIgnoreCase("M") && item18Status.contains(STATE)) {
            // State flight handling – remarks is set to STATE.
            updateItem18Remark(flightMovement, STATE);
            return true;
        }

        // If the flight type is general (G) and item 18 contains STS/MEDEVAC or STS/HOSP – flight is handled as an ambulance flight.
        // If the flight type is commercial (S|N) and item 18 contains STS/MEDEVAC or STS/HOSP – flight is handled as an ambulance flight.

        //noinspection ConstantConditions - these Strings can never be null. Suppression due to java limitation of the return value
        if ((flightType.equalsIgnoreCase("G") || flightType.equalsIgnoreCase("S")
            || flightType.equalsIgnoreCase("N"))
            && (item18Status.contains("MEDEVAC") || (item18Status.contains("HOSP")))) {
            // Ambulance flight handling – remarks is set to AMBULANCE
            updateItem18Remark(flightMovement, AMBULANCE);
            return true;
        }

        return false;
    }

    private void updateItem18Remark(FlightMovement flightMovement, String remark) {
        String item18Remark = nvl(flightMovement.getItem18Rmk(), "");

        //noinspection ConstantConditions
        if (!item18Remark.contains(remark)) {
            if (StringUtils.isBlank(item18Remark)) {
                item18Remark = item18Remark.concat(remark);
            } else {
                item18Remark = item18Remark.concat(" "+ remark);
            }
            flightMovement.setItem18Rmk(item18Remark);
        }
    }

    private void updateFlightNotes(FlightMovement flightMovement, String details) {
        String flightNotes = flightMovement.getFlightNotes();
        if (StringUtils.isBlank(flightMovement.getFlightNotes())) {
            flightMovement.setFlightNotes(details);
        } else {
            FlightNotesUtility.mergeFlightNotes(flightMovement, flightNotes, details);
        }
    }

    private void checkFlightMovementCostForWhitelisting(FlightMovement flightMovement, LocalDate whitelistingStartDate) {
        Account flightMovementAccount = flightMovement.getAccount();
        if (flightMovementAccount == null) {
            if (checkFlightMovementExceptionsForWhitelisting(flightMovement)) {
                LOG.debug("Flight plan {} is accepted because it's STATE or EMERGENCY flight", flightMovement);
                sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), false);
                return;
            } else {
                String details = "Flight plan not accepted due to unresolved account";
                LOG.debug("Flight plan {} not accepted due to unresolved account", flightMovement);
                flightMovement.setStatus(FlightMovementStatus.DECLINED);
                updateFlightNotes(flightMovement, details);
                flightMovementRepositoryUtility.overwrite(flightMovement);
                sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), true);
                return;
            }
        }

        if (!flightMovementAccount.getCashAccount()) {
            LOG.debug("Whitelisting is not applied because '{}' is a Credit account", flightMovementAccount.getName());
            sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), false);
            return;
        }

        final Currency usdCurrency = currencyUtils.getCurrencyUSD();
        double totalChargesAmountForNewFlight = whitelistingUtils.getTotalChargesAmountInUSD(flightMovement);
        double totalChargeAmountForExistingFlights = getTotalChargeAmountForExistingFlightsForWhitelisting(flightMovement.getId(),
            flightMovementAccount.getId(), whitelistingStartDate);

        final Transaction lastTransaction = transactionService.findTopByAccountIdAndCurrencyIdOrderByTransactionDateTimeDesc(flightMovement.getAccount().getId(), usdCurrency.getId());
        double lastBalance = 0d;
        if (lastTransaction != null) {
            lastBalance = lastTransaction.getBalance();
        }

        if (lastBalance == 0 && (totalChargesAmountForNewFlight + totalChargeAmountForExistingFlights) == 0) {
            LOG.debug("Whitelisting is not applied because flight movement charges are zero");
            sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), false);
            return;
        }

        if ((lastBalance + totalChargesAmountForNewFlight + totalChargeAmountForExistingFlights > 0) &&
            !checkFlightMovementExceptionsForWhitelisting(flightMovement)) {

            String details = "Flight plan not accepted due to prepayment requirement";
            LOG.debug("Flight plan {} not accepted due to prepayment requirement", flightMovement);
            flightMovement.setStatus(FlightMovementStatus.DECLINED);
            updateFlightNotes(flightMovement, details);
            flightMovementRepositoryUtility.overwrite(flightMovement);
            sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), true);
        } else {
            LOG.debug("Flight plan {} is accepted", flightMovement);
            sendAcceptedOrDeclinedMessageToCronos(flightMovement.getSpatiaFplObjectId(), false);
        }
    }

    private double getTotalChargeAmountForExistingFlightsForWhitelisting(final int newFlightMovementId,
                                                                         final int accountId,
                                                                         final LocalDate whitelistingStartDate) {
        LocalDateTime whitelistingStart = LocalDateTime.of(whitelistingStartDate, LocalTime.MIN);
        double totalChargeAmount = 0d;

        List<FlightMovement> flightMovements = flightMovementRepository.checkAllFlightMovementsByAccountForWhitelisting(newFlightMovementId,
            accountId, whitelistingStart);

        for (FlightMovement fm: flightMovements) {
            if (whitelistingUtils.ifWhitelistingFlightIsBillable(fm)) {
               flightMovementBuilder.recalculateCharges(fm);
               flightMovementBuilder.handleStatus(fm, false);
               flightMovementRepositoryUtility.persist(fm);
               totalChargeAmount += whitelistingUtils.getTotalChargesAmountInUSD(fm);
            }
        }

        return totalChargeAmount;
    }

    private void sendAcceptedOrDeclinedMessageToCronos(long fplObjectId, boolean decline) {
        if (!pluginService.isEnabled(PluginKey.CRONOS_2)) {
            LOG.debug("Accepted/Declined message cannot be sent to Cronos. Plugin Cronos 2 is disabled");
            return;
        }
        String cronosConnectionUrl = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_CRONOS_CONNECTION_URL);
        String cronosConnectionLogin = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_CRONOS_CONNECTION_LOGIN);
        String cronosConnectionPassword = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_CRONOS_CONNECTION_PASSWORD);

        if (StringUtils.isBlank(cronosConnectionUrl) || StringUtils.isBlank(cronosConnectionLogin) || StringUtils.isBlank(cronosConnectionPassword)) {
            LOG.debug("Accepted/Declined message cannot be sent to Cronos. Please check all needed system configurations for connection");
            return;
        }

        if (StringUtils.isBlank(jSessionId)) {
            loginToCronos(cronosConnectionUrl, cronosConnectionLogin, cronosConnectionPassword);
        }

        if (StringUtils.isNotBlank(jSessionId)) {
            String message = getAcceptedOrDeclinedMessageForCronos(decline);
            String messageType = decline ? "decline" : "accept";
            String url = String.format("%s/billing/flightobject/%s/%s", cronosConnectionUrl, fplObjectId, messageType);

            HttpResponse response = sendMessageToCronos(fplObjectId, message, url, messageType);

            // Full authentication is required to access Cronos - need to login and update JSESSIONID, then send a message again
            if (response != null && response.getStatusLine().getStatusCode() == 401) {
                loginToCronos(cronosConnectionUrl, cronosConnectionLogin, cronosConnectionPassword);
                if (StringUtils.isNotBlank(jSessionId)) {
                    response = sendMessageToCronos(fplObjectId, message, url, messageType);
                    checkResponseStatus(response, messageType, fplObjectId);
                }
            } else {
                checkResponseStatus(response, messageType, fplObjectId);
            }
        }
    }

    private void checkResponseStatus(HttpResponse response, String messageType, long fplObjectId) {
        if (response == null) {
            LOG.debug("Response from sending PUT request to Cronos in null");
            return;
        }

        if (response.getStatusLine().getStatusCode() == 200) {
            LOG.debug("{} message for fplObjectId: {} sent successfully to Cronos", messageType, fplObjectId);
            // Full authentication is required to access Cronos - need to login and update JSESSIONID, then send a message again
        } else {
            StatusLine statusLine = response.getStatusLine();
            LOG.debug("Was unable to send a {} message for fplObjectId: {} to Cronos: statusCode: {}, statusReasonPhrase: {}",
                messageType, fplObjectId, statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
    }

    private String getAcceptedOrDeclinedMessageForCronos(boolean decline) {
        if (decline) {
            String declineMessage = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_DECLINED_FLIGHT_NOTICE_TEXT);
            if (StringUtils.isBlank(declineMessage)) {
                declineMessage = "Flight plan not accepted due to prepayment requirement";
            }
            return declineMessage;
        } else {
            String acceptMessage = systemConfigurationService.getCurrentValue(SystemConfigurationItemName.WL_ACCEPTED_FLIGHT_NOTICE_TEXT);
            if (StringUtils.isBlank(acceptMessage)) {
                acceptMessage = "Flight plan is accepted";
            }
            return acceptMessage;
        }
    }

    private HttpResponse sendMessageToCronos(long fplObjectId, String message, String url, String messageType) {
        LOG.debug("Sending a {} message to Cronos for fplObjectId: {}", messageType, fplObjectId);
        HttpPut httpPut = new HttpPut(url);
        String body = String.format("{\"notice\": \"%s\"}", message);
        httpPut.addHeader("Content-type", "application/json");
        httpPut.addHeader("Cookie", String.format("JSESSIONID=%s", jSessionId));
        try {
            httpPut.setEntity(new StringEntity(body));
        } catch (UnsupportedEncodingException e) {
            LOG.debug(e.getLocalizedMessage());
        }

        // Execute and get the response
        HttpResponse response;
        try {
            response = httpclient.execute(httpPut);
        } catch (IOException e) {
            LOG.debug("Was unable to send PUT request from url -" + url, e);
            return null;
        }

        httpPut.releaseConnection();
        return response;
    }

    private void loginToCronos(String cronosConnectionUrl, String cronosConnectionLogin, String cronosConnectionPassword) {
        jSessionId = "";
        httpclient = HttpClients.createDefault();

        LOG.debug("Trying to login to Cronos");
        String url = String.format("%s/session/login", cronosConnectionUrl);
        HttpPost httpPost = new HttpPost(url);

        // Request parameters and other properties
        List<NameValuePair> params = new ArrayList<>(2);
        params.add(new BasicNameValuePair("userLogin", cronosConnectionLogin));
        params.add(new BasicNameValuePair("password", cronosConnectionPassword));
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOG.debug(e.getLocalizedMessage());
        }

        // Execute and get the response
        HttpResponse response;
        try {
            response = httpclient.execute(httpPost);
        } catch (IOException e) {
            LOG.debug("Was unable to send POST request (login) from url -" + url, e);
            return;
        }

        httpPost.releaseConnection();

        if (response == null) {
            LOG.debug("Response from login to Cronos in null");
            return;
        }

        // Extract the JSESSIONID cookie from the response
        if (response.getStatusLine().getReasonPhrase().equals("OK")) {
            Header[] headers = response.getHeaders("Set-Cookie");
            final Pattern pattern = Pattern.compile("JSESSIONID=(.*?);");

            for (Header header: headers) {
                final Matcher matcher = pattern.matcher(header.getValue());
                if (matcher.lookingAt()) {
                    jSessionId = matcher.group(1);
                    break;
                }
            }
        } else {
            StatusLine statusLine = response.getStatusLine();
            LOG.debug("Cannot connect to Cronos: statusCode: {}, statusReasonPhrase: {}",
                statusLine.getStatusCode(), statusLine.getReasonPhrase());
        }
    }

    public boolean checkIfUnifiedTaxFlight(FlightMovement flightMovement) {

    	boolean isUnifiedTaxAircraft = false;

    	String item18RegNum = flightMovementBuilderUtility.checkAircraftRegistrationNumber(flightMovement);

        if (item18RegNum != null) {

            Double aMtow = null;

            AircraftRegistration ar = aircraftRegistrationService.findAircraftRegistrationByRegNumber(item18RegNum);
            if (ar != null) {
            	// SMALL_AIRCRAFT_MAX_WEIGHT is expressed in KG
                Integer maxWeight = systemConfigurationService.getIntOrZero(SystemConfigurationItemName.SMALL_AIRCRAFT_MAX_WEIGHT);

                // MTOW stored in small tones in the DB ==> need to convert it to KG
            	aMtow = ar.getMtowOverride()* ReportHelper.TO_KG;

                if (aMtow <= maxWeight) {

                	boolean isDomesticOrLocal = (ar.getIsLocal()) ||
                		(flightMovement.getFlightCategoryNationality().equals(FlightmovementCategoryNationality.NATIONAL));

                	if (isDomesticOrLocal) {
                		isUnifiedTaxAircraft = true;
                	}
                }
            }
        }
        return isUnifiedTaxAircraft;
    }

}
