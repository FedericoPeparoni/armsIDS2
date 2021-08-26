package ca.ids.abms.modules.flightmovements;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.PartialParametersException;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.aircraft.AircraftRegistration;
import ca.ids.abms.modules.aircraft.AircraftRegistrationService;
import ca.ids.abms.modules.aircraft.AircraftType;
import ca.ids.abms.modules.billingcenters.BillingCenter;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.flightmovementsbuilder.FlightMovementBuilderException;
import ca.ids.abms.modules.flightmovementsbuilder.enumerate.FlightMovementValidatorIssue;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportHelper;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.util.StringUtils;

import org.apache.poi.util.SystemOutLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ca.ids.abms.modules.system.summary.SystemConfigurationItemName.DUPL_OR_MISS_FLIGHTS_EET_PERC;
import static ca.ids.abms.modules.system.summary.SystemConfigurationItemName.DUPL_OR_MISS_FLIGHTS_MIN_WIND;

@RestController
@RequestMapping("api/flightmovements")
@SuppressWarnings({"unused", "squid:S1452"})
public class FlightMovementController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(FlightMovementController.class);
    private final FlightMovementService flightMovementService;
    private final FlightMovementMapper flightMovementMapper;
    private final UserService userService;
    private final SystemConfigurationService systemConfigurationService;
    private final AccountService accountService;
    private final AircraftRegistrationService aircraftRegistrationService;
    private final ReportDocumentCreator reportDocumentCreator;

    public FlightMovementController(final FlightMovementService flightMovementService,
                                    final FlightMovementMapper flightMovementMapper,
                                    final UserService userService,
                                    final SystemConfigurationService systemConfigurationService,
                                    final AccountService accountService,
                                    final AircraftRegistrationService aircraftRegistrationService,
                                    final ReportDocumentCreator reportDocumentCreator) {

        this.flightMovementService = flightMovementService;
        this.flightMovementMapper = flightMovementMapper;
        this.userService = userService;
        this.systemConfigurationService = systemConfigurationService;
        this.accountService = accountService;
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<Page<FlightMovementViewModel>> getAllFlightMovement(
        @RequestParam(name = "search", required = false) final String textSearch,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"dateOfFlight"}, direction=Sort.Direction.DESC),
            @SortDefault(sort = {"depTime"}, direction = Sort.Direction.DESC)})  Pageable pageable,
        @RequestParam(name = "duplicatesOrMissing", required = false) Boolean duplicatesOrMissing,
        @RequestParam(name = "showActualDepDest", required = false) Boolean showActualDepDest,
        @RequestParam(name = "showAllFlights", required = false) Boolean showAllFlights) {

        LOG.debug("REST request to get All FlightMovement; textSearch: {}", textSearch);

        if (Boolean.TRUE.equals(duplicatesOrMissing)) {
            LOG.debug("Detecting if there are duplicates or missing");

            final Sort sortingOpts = new Sort(
                new Sort.Order(Sort.Direction.ASC, "item18RegNum"),
                new Sort.Order(Sort.Direction.ASC, "dateOfFlight"),
                new Sort.Order(Sort.Direction.ASC, "depTime"));

            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sortingOpts);
        }
        final Page<FlightMovement> page = flightMovementService.getAllFlightMovement(pageable, textSearch);
        final Page<FlightMovementViewModel> resultPage = new PageImplCustom<>(flightMovementMapper.toViewModel(page), pageable, page.getTotalElements(), page.getTotalElements());
        if (Boolean.TRUE.equals(duplicatesOrMissing)) {
            DuplicateOrMissingFlightsDetector.analyze(resultPage.getContent(),
                systemConfigurationService.getIntOrZero(DUPL_OR_MISS_FLIGHTS_MIN_WIND),
                systemConfigurationService.getIntOrZero(DUPL_OR_MISS_FLIGHTS_EET_PERC),
                Boolean.TRUE.equals(showAllFlights), Boolean.TRUE.equals(showActualDepDest));
        }
        return ResponseEntity.ok().body(resultPage);
    }



    @GetMapping(value = "/filters")
    @SuppressWarnings("squid:S00107")
    public ResponseEntity<?> finAllFlightMovementByFilter(
        @RequestParam(name = "search", required = false) final String textSearch,
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"dateOfFlight"}, direction=Sort.Direction.DESC),
            @SortDefault(sort = {"depTime"}, direction = Sort.Direction.DESC)})  Pageable pageable,
        @RequestParam(name = "status", required = false) String status,
        @RequestParam(name = "type", required = false) Integer flightMovementCategoryId,
        @RequestParam(name = "iata", required = false) Boolean iata,
        @RequestParam(required = false) String issue,
        @RequestParam(required = false) String invoice,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate start,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate end,
        @RequestParam(name = "duplicatesOrMissing", required = false) Boolean duplicatesOrMissing,
        @RequestParam(name = "showActualDepDest", required = false) Boolean showActualDepDest,
        @RequestParam(name = "showAllFlights", required = false) Boolean showAllFlights,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport
    ) throws IOException {

        LOG.debug("REST request to get All FlightMovement; textSearch: {} status: {} movementType: {} issue: {} invoice: {} iata: {} startDate: {} endDate: {}",
            textSearch, status, flightMovementCategoryId, issue, invoice, iata, start, end);

        if (Boolean.TRUE.equals(duplicatesOrMissing)) {
            LOG.debug("Detecting if there are duplicates or missing");

            final Sort sortingOpts = new Sort(
                new Sort.Order(Sort.Direction.ASC, "item18RegNum"),
                new Sort.Order(Sort.Direction.ASC, "dateOfFlight"),
                new Sort.Order(Sort.Direction.ASC, "depTime"));
            pageable = new PageRequest(pageable.getPageNumber(), pageable.getPageSize(), sortingOpts);
        }

        if (csvExport != null && csvExport) {
            int pageSize = 10000;
            int currentPage = 0;

            ReportDocument doc = null;
            //TODO: use For
            while(true) {
                pageable = new PageRequest(currentPage, pageSize);
                LOG.debug("findAllFlightMovementByFilter  page");
                final Page<FlightMovement> pageFlightMovement = flightMovementService.findAllFlightMovementByFilterForDownload(pageable, textSearch,
                    flightMovementCategoryId, status, iata, issue, invoice, start, end, duplicatesOrMissing);
                if(!pageFlightMovement.hasContent()) {
                    break;
                }

                //Convert to List
				final List<FlightMovement> flightMovementList = new ArrayList();
                flightMovementList.addAll(pageFlightMovement.getContent());
                LOG.debug("grandezza lista " + flightMovementList.size());
                LOG.debug("Page: " + (currentPage));

                currentPage = currentPage + 1;
            	if(doc == null) {
            		doc = reportDocumentCreator.createCsvDocument("Flight_Movement", flightMovementMapper.toCsvModel(flightMovementList), FlightMovementCsvExportModel.class, true);
            	}else {
					reportDocumentCreator.appendToCsvDocument(doc, flightMovementMapper.toCsvModel(flightMovementList),FlightMovementCsvExportModel.class, true, false);
            	}


            }
            //Flush
            doc.getOutputStream().flush();
            doc.getOutputStream().close();
            return doCreateResource(doc);
            //return doCreateStreamingResponse(doc);

        } else {

			final List<FlightMovement> list = new ArrayList();
            LOG.debug("findAllFlightMovementByFilter No page");
            final Page<FlightMovement> page = flightMovementService.findAllFlightMovementByFilter(pageable, textSearch,
                flightMovementCategoryId, status, iata, issue, invoice, start, end, duplicatesOrMissing);

            final long countAllFlightMovement = flightMovementService.countAllFlightMovement();
            final Page<FlightMovementViewModel> resultPage = new PageImplCustom<>(flightMovementMapper.toViewModel(page),
					pageable, page.getTotalElements(), countAllFlightMovement);

			if (Boolean.TRUE.equals(duplicatesOrMissing)) {
                DuplicateOrMissingFlightsDetector.analyze(resultPage.getContent(),
                    systemConfigurationService.getIntOrZero(DUPL_OR_MISS_FLIGHTS_MIN_WIND),
                    systemConfigurationService.getIntOrZero(DUPL_OR_MISS_FLIGHTS_EET_PERC),
                    Boolean.TRUE.equals(showAllFlights), Boolean.TRUE.equals(showActualDepDest));
            }
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/issue/{issue}")
    public ResponseEntity<Page<FlightMovementViewModel>> findAllFlightMovementByIssue(Pageable pageable, @PathVariable FlightMovementValidatorIssue issue) {
        LOG.debug("REST request to get All FlightMovement By issue: '{}'", issue);
        final Page<FlightMovement> page = flightMovementService.findAllFlightMovementByIssue(pageable, issue.toValue());
        final Page<FlightMovementViewModel> resultPage = new PageImplCustom<>(flightMovementMapper.toViewModel(page), pageable, page.getTotalElements(), page.getTotalElements());
        return ResponseEntity.ok().body(resultPage);
    }


    @GetMapping(value = "/{id}")
    public ResponseEntity<FlightMovementViewModel> findFlightMovementById(@PathVariable Integer id) {
        LOG.debug("REST request to get FlightMovement By ID: {}", id);
        FlightMovement flightMovement = flightMovementService.findFlightMovementById(id);
        FlightMovementViewModel dto = flightMovementMapper.toViewModel(flightMovement);
        return ResponseEntity.ok().body(dto);
    }

    @GetMapping(value = "/validations")
    public ResponseEntity<List<FlightMovementValidationViewModel>> validationFlightMovementsById(@RequestBody List<Integer> fligtMovementIds) {
        LOG.debug("REST request to validation FlightMovement By ID: '{}'", fligtMovementIds);
        List<FlightMovementValidationViewModel> flightMovementValidations = new ArrayList<>();
        if (fligtMovementIds != null && !fligtMovementIds.isEmpty()) {
            for (Integer id : fligtMovementIds) {
                FlightMovementValidationViewModel flightMovementValidation = flightMovementService.validateFlightMovementByID(id);
                flightMovementValidations.add(flightMovementValidation);
            }
            return ResponseEntity.ok().body(flightMovementValidations);
        } else {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
    }

    @PreAuthorize("hasAuthority('aviation_invoice_validate')")
    @PostMapping(value = "/validations/byParams")
    public ResponseEntity<List<FlightMovementValidationViewModel>> validationFlightMovementsByParams(
        @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
        @RequestParam(value = "endDateInclusive", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
        @RequestParam(value = "userBillingCenterOnly", required = false, defaultValue = "true") final boolean userBillingCenterOnly,
        @RequestParam(value = "flightCategory", required = false) final Integer flightCategory,
        @RequestParam(value = "iata") final boolean iata,
        @RequestBody(required = false) final List<Integer> accountIdList
    ) {
        LOG.debug("REST request to validation FlightMovement By userBillingCenter/accounts/iataStatus/flightCategory from '{}' to '{}'",
            startDate, endDateInclusive);

        BillingCenter userBC = this.getBillingCenterOfCurrentUser();
        List<FlightMovementValidationViewModel> failedList = flightMovementService
            .validateAllFlightMovementByParams(startDate, endDateInclusive, userBillingCenterOnly, accountIdList, iata, userBC, flightCategory);

        if (failedList != null && !failedList.isEmpty()) {
            return ResponseEntity.ok().body(failedList);
        } else {
            return ResponseEntity.ok().body(new ArrayList<>());
        }
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/reconcile/byParams")
    public ResponseEntity<ResultRecords> reconcileFlightMovementsByParams(@RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
            @RequestParam(value = "endDateInclusive", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
            @RequestParam(value = "userBillingCenterOnly", required = false, defaultValue = "true") final boolean userBillingCenterOnly,
            @RequestParam(value = "accountIdList", required=false) final List<Integer> accountIdList,
            @RequestParam(value = "iata") final boolean iata) {
        LOG.debug("REST request to reconcile FlightMovement By userBillingCenter/accounts/iataStatus from '{}' to '{}'",
            startDate, endDateInclusive);

        BillingCenter userBC = this.getBillingCenterOfCurrentUser();
        ResultRecords flightNumber =  flightMovementService.reconcileAllFlightMovementByParams(startDate, endDateInclusive, userBillingCenterOnly, accountIdList, iata, userBC);

        if (flightNumber != null) {
            return ResponseEntity.ok().body(flightNumber);
        } else {
            return ResponseEntity.ok().body(null);
        }
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/recalculate/byParams")
    public ResponseEntity<ResultRecords> recalculateFlightMovementsByParams(
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
            @RequestParam(value = "endDateInclusive", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
            @RequestParam(value = "userBillingCenterOnly", required = false, defaultValue = "true") final boolean userBillingCenterOnly,
            @RequestParam(value = "accountIdList", required=false) final List<Integer> accountIdList,
            @RequestParam(value = "iata") final boolean iata) {
        LOG.debug("REST request to recalculate FlightMovement By userBillingCenter/accounts/iataStatus from '{}' to '{}'",
            startDate, endDateInclusive);

        BillingCenter userBC = this.getBillingCenterOfCurrentUser();
        ResultRecords flightNumber  = flightMovementService.recalculateAllFlightMovementByParams(startDate, endDateInclusive, userBillingCenterOnly, accountIdList, iata, userBC);
        if (flightNumber != null ) {
            return ResponseEntity.ok().body(flightNumber);
        } else {
            return ResponseEntity.ok().body(null);
        }
    }

    /**
     * When a user deletes a flight movement, the record is not physically deleting but modifying the
     * flight movement status.
     */
    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteFlightMovement(@PathVariable Integer id, @RequestBody String notes) {
        LOG.debug("REST request to delete FlightMovement By ID: {}", id);
        flightMovementService.deleteFlightMovementFromUI(id, notes, false);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FlightMovementViewModel> updateFlightMovement(@PathVariable Integer id, @RequestBody FlightMovementViewModel flightMovementDto) {
        LOG.debug("REST request to update FlightMovement By ID: {}", id);
        final FlightMovement flightMovement = flightMovementMapper.toModel(flightMovementDto);
        FlightMovement result = null;
        try {
            result = flightMovementService.updateFlightMovementFromUI(id, flightMovement);
        } catch (FlightMovementBuilderException e) {
            if(!StringUtils.isStringIfNotNull(flightMovement.getEstimatedElapsedTime())){
                throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class);
            }

            if(!StringUtils.isStringIfNotNull(flightMovement.getCruisingSpeedOrMachNumber())){
                throw ExceptionFactory.getInvalidDataException(ErrorConstants.ERR_CIRCULAR_ROUTE, FlightMovement.class);
            }

            throw new CustomParametrizedException(e.getFlightMovementBuilderIssue().toValue(), new Exception(e.getLocalizedMessage()));
        }
        final FlightMovementViewModel flightMovementViewModel = flightMovementMapper.toViewModel(result);
        return ResponseEntity.ok().body(flightMovementViewModel);
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PostMapping
    public ResponseEntity<FlightMovementViewModel> createFlightMovement(@Valid @RequestBody FlightMovementViewModel flightMovementDto) throws URISyntaxException {
        LOG.debug("REST request to create a new FlightMovement");
        if (flightMovementDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final FlightMovement flightMovement = flightMovementMapper.toModel(flightMovementDto);
        final FlightMovement result;
        try {
            result = flightMovementService.createFlightMovementFromUI(flightMovement,false);
        } catch (FlightMovementBuilderException e) {
            throw new CustomParametrizedException(e.getFlightMovementBuilderIssue().toValue(), new Exception(e.getLocalizedMessage()));
        }
        final FlightMovementViewModel resultDto = flightMovementMapper.toViewModel(result);
        return ResponseEntity.created(new URI("/api/flightmovements/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/recalculate")
    public ResponseEntity<FlightMovementListChargesCalculationResult> recalculateChargesByIds(@RequestBody List<Integer> fligtMovementIds) {

        LOG.debug("REST request to recalculate FlightMovement charges By Ids: {}", fligtMovementIds);
        if (fligtMovementIds == null || fligtMovementIds.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // find each flight movement to recalculate by id and log ids that do not exist
        List<FlightMovement> flightMovements = flightMovementService.findFlightMovementsByIds(fligtMovementIds);
        for (Integer id : fligtMovementIds) {
            if(flightMovements.stream().filter(f -> f.getId().equals(id)).count() < 1) {
                LOG.debug("Flight movement id: {} doesn't exist in DB", id);
            }
        }

        // attempt to recalculate each flight movement found
        FlightMovementListChargesCalculationResult flightMovementListChargesCalculationResults = new FlightMovementListChargesCalculationResult();
        for (FlightMovement flightMovement : flightMovements) {
            Boolean recalculateCharge= flightMovementService.calculateCharges(flightMovement.getId());
            if (recalculateCharge) {
                flightMovementListChargesCalculationResults.addSuccessfullyCalculated(flightMovement.getId());
            } else {
                flightMovementListChargesCalculationResults.addUnSuccessfullyCalculated(flightMovement.getId());
            }
        }

        return ResponseEntity.ok().body(flightMovementListChargesCalculationResults);
    }

    @PreAuthorize("hasAuthority('flight_movement_modify')")
    @PutMapping(value = "/reconcile")
    public ResponseEntity<Void> reconcilesByIds(@RequestBody List<Integer> fligtMovementIds) {

        LOG.debug("REST request to reconcile FlightMovement charges By Ids: {}", fligtMovementIds);
        if (fligtMovementIds == null || fligtMovementIds.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        // find each flight movement to recalculate by id and log ids that do not exist
        List<FlightMovement> flightMovements = flightMovementService.findFlightMovementsByIds(fligtMovementIds);
        for (Integer id : fligtMovementIds) {
            if (flightMovements.stream().filter(f -> f.getId().equals(id)).count() < 1) {
                LOG.debug("Flight movement id: {} doesn't exist in DB", id);
            }
        }

        // attempt to reconcile each flight movement found
        for (FlightMovement flightMovement : flightMovements) {
            try {
                flightMovementService.updateFlightMovementFromUI(flightMovement.getId(), flightMovement);
            } catch (FlightMovementBuilderException e) {
                throw new PartialParametersException(e.getFlightMovementBuilderIssue().toValue(), e.getLocalizedMessage());
            }
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/account/{accountId}")
    public ResponseEntity<Page<FlightMovementViewModel>> findAllFlightMovementByAccount(Pageable pageable, @PathVariable Integer accountId) {
        LOG.debug("REST request to getAllFlightMovementByAccountID: {}", accountId);

        List<FlightMovement> flightMovementsResult = flightMovementService.findAllFlightMovementByAccount(accountId);
        final Page<FlightMovementViewModel> resultPage = new PageImpl<>(flightMovementMapper.toViewModel(flightMovementsResult), pageable, flightMovementsResult.size());
        return ResponseEntity.ok().body(resultPage);
    }

    // returns a list of flight movements that are related to a billing ledger (invoice)
    @GetMapping(value = "/invoices/{invoiceId}")
    public ResponseEntity<?> findAllFlightMovementsByAssociatedBillingLedgerId (
        Pageable pageable,
        @PathVariable final Integer invoiceId,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get flight movements that are linked to invoice id: {}", invoiceId);

        if (invoiceId == null) {
            return ResponseEntity.badRequest().body(null);
        }

        Page<FlightMovement> flightMovements = flightMovementService.findAllByAssociatedBillingLedgerId(invoiceId, pageable);

        if (csvExport != null && csvExport) {
            final List<FlightMovement> list = flightMovements.getContent();
            final List<FlightMovementCsvExportModel> csvExportModel = flightMovementMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Flight_Movement", csvExportModel, FlightMovementCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            Page<FlightMovementViewModel> resultPage = new PageImpl<>(flightMovementMapper.toViewModel(flightMovements), pageable, flightMovements.getTotalElements());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/list/invoices/{invoiceId}")
    public ResponseEntity<List<FlightMovementViewModel>> findAllFlightMovementsByAssociatedBillingLedgerId (@PathVariable final Integer invoiceId) {
        LOG.debug("REST request to get flight movements that are linked to invoice id: {}", invoiceId);

        List<FlightMovementViewModel> resultPage = null;
        if (invoiceId != null) {
            resultPage = flightMovementMapper.toViewModel(flightMovementService.findAllByAssociatedBillingLedgerId(invoiceId));
        }
        return ResponseEntity.ok().body(resultPage);
    }

    // Returns Aircraft Type from the latest Registration Number
    @GetMapping(value = "/aircraftTypeByLatestRegistrationNumber/{registrationNumber}")
    public ResponseEntity<AircraftType> getAircraftTypeByLatestRegistrationNumber(@PathVariable String registrationNumber) {
        LOG.debug("REST request to get Aircraft Type from Registration Number: {}", registrationNumber);
        AircraftType aircraftTypeEntity = flightMovementService.getAircraftTypeByLatestRegistrationNumber(registrationNumber);
        return ResponseEntity.ok().body(aircraftTypeEntity);
    }

    // Returns distinct dep_ad and dest_ad
    @GetMapping(value = "/distinctRoutes")
    public ResponseEntity<List<Object>> getDistinctRoutes() {
        LOG.debug("REST request to get distinct departure and destination aerodromes");
        List<Object> result = flightMovementService.getDistinctRoutes();
        return ResponseEntity.ok().body(result);
    }

    // Returns distinct flight levels
    @GetMapping(value = "/distinctFlightLevels")
    public ResponseEntity<List<Object>> getDistinctFlightLevels() {
        LOG.debug("REST request to get distinct flight levels");
        List<Object> result = flightMovementService.getDistinctFlightLevels();
        return ResponseEntity.ok().body(result);
    }

    // Returns distinct registration numbers
    @GetMapping(value = "/distinctRegNumbers")
    public ResponseEntity<List<Object>> getRegNumbers() {
        LOG.debug("REST request to get distinct registration numbers");
        List<Object> result = flightMovementService.getDistinctRegNum();
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/account/{accountId}/{status}/{month}/{year}")
    public ResponseEntity<List<FlightMovementViewModel>> findAllFlightMovementByAccount(@PathVariable Integer accountId,@PathVariable String status,@PathVariable Integer month, @PathVariable Integer year) {
        LOG.debug("REST request to getAllFlightMovementByAccountID: {}", accountId);
        List<FlightMovementViewModel> flightMovementViewModels=null;
        if(accountId!=null && status!=null && month != null && year != null) {
            LocalDateTime startDate = LocalDateTime.of(year, month, 1,0,0);
            LocalDateTime endDate = startDate.withDayOfMonth(startDate.toLocalDate().lengthOfMonth());
            List<FlightMovement>  flightMovements = flightMovementService.findAllFlightMovementByAccountAndDate(accountId,status,startDate,endDate);
            flightMovementViewModels = flightMovementMapper.toViewModel(flightMovements);
        }
        return ResponseEntity.ok().body(flightMovementViewModels);
    }

    @GetMapping(value = "/accountByIdentifier/{identifier}")
    public ResponseEntity<Account> getAccountByIdentifier(@PathVariable String identifier) {
        LOG.debug("REST request to get account by identifier: {}", identifier);

        Account account = accountService.findAccountByIcaoOrIataCode(identifier);

        return Optional.ofNullable(account).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/accountByOperator/{operator}")
    public ResponseEntity<Account> getAccountByOperator(@PathVariable String operator) {
        LOG.debug("REST request to get account by operator: {}", operator);

        Account account = accountService.findAccountByOprIdentifier(operator);

        return Optional.ofNullable(account).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/aircraftRegistrationByRegNumber/{regNumber}/{dateOfFlight}")
    public ResponseEntity<AircraftRegistration> getAircraftRegistrationByIdentifier(@PathVariable String regNumber,
                                                                                    @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                                                        final LocalDateTime dateOfFlight) {
        LOG.debug("REST request to get aircraft registration by Registration Number: {} and date of flight: {}", regNumber, dateOfFlight);

        AircraftRegistration aircraftRegistration = aircraftRegistrationService.findAircraftRegistrationByRegistrationNumber(regNumber, dateOfFlight);

        return Optional.ofNullable(aircraftRegistration).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
      * Find billing center ID of the current user
     */
     public BillingCenter getBillingCenterOfCurrentUser() {
        final User currentUser = getCurrentUser();
            if (currentUser.getBillingCenter() == null) {
                throw ExceptionFactory.getMissingBillingCenterOfCurrentUserException (ReportHelper.class, currentUser.getId().toString(), currentUser.getLogin());
            }
        return currentUser.getBillingCenter();
     }

	/**
	 * Return the currently logged-in user
	 */
	public User getCurrentUser() {
		return userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
	}

	/**
	 * Set PENDING zero total cost flight movements as PAID
	 */
    @PostMapping(value = "/mark-as-paid")
    @PreAuthorize("hasAuthority('flight_movement_modify')")
	public ResponseEntity<ResultRecords> setZeroCostFlightsPaid(@RequestBody List<Integer> flightIds){
		LOG.debug("REST request to Set PENDING zero total cost flight movements as PAID");
		ResultRecords updated  = flightMovementService.setZeroCostFlightsPaid(flightIds);
        if (updated != null ) {
            return ResponseEntity.ok().body(updated);
        } else {
            return ResponseEntity.ok().body(null);
        }
	}
}
