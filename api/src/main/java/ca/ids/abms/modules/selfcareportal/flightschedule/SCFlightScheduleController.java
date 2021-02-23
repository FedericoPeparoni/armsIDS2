package ca.ids.abms.modules.selfcareportal.flightschedule;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.accounts.AccountService;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.flight.*;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.selfcareportal.approvalrequests.SelfCarePortalApprovalRequestService;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestDataset;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestStatus;
import ca.ids.abms.modules.selfcareportal.approvalrequests.enumerate.RequestType;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.modules.system.summary.SystemConfigurationItemName;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;

import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/sc-flight-schedules")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCFlightScheduleController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCFlightScheduleController.class);

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String FLIGHT_SCHEDULE_LOADER = "flightScheduleLoader";

    private static final String FS_NOT_FOUND = "Flight Schedule not found";
    private static final String SC_FS_NOT_FOUND = "Self-Care account Flight Schedule not found";
    private static final String NO_FLIGHT_SCHEDULE = "Can't get flight schedule for non self-care account";
    private static final String NO_PERMISSIONS = "Self-care user doesn't have permissions for this account";
    private static final String NO_ACCOUNT = "Account doesn't exist";

    private final FlightScheduleService flightScheduleService;
    private final FlightScheduleMapper flightScheduleMapper;
    private final UserService userService;
    private final AccountService accountService;
    private final SystemConfigurationService systemConfigurationService;
    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;

    private final ReportDocumentCreator reportDocumentCreator;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<FlightScheduleCsvViewModel> dataImportService;

    @Qualifier(FLIGHT_SCHEDULE_LOADER)
    private FlightScheduleLoader loaderService;

    @SuppressWarnings("squid:S00107") // Methods should not have too many parameters
    public SCFlightScheduleController(final FlightScheduleService flightScheduleService,
                                      final FlightScheduleMapper flightScheduleMapper,
                                      final FlightScheduleLoader loaderService,
                                      final UserService userService,
                                      final AccountService accountService,
                                      final SystemConfigurationService systemConfigurationService,
                                      final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
                                      final ReportDocumentCreator reportDocumentCreator) {
        this.flightScheduleService = flightScheduleService;
        this.flightScheduleMapper = flightScheduleMapper;
        this.loaderService = loaderService;
        this.userService = userService;
        this.accountService = accountService;
        this.systemConfigurationService = systemConfigurationService;
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PostMapping
    public ResponseEntity<FlightScheduleViewModel> createFlightScheduleFromSC(@Valid @RequestBody FlightScheduleViewModel flightScheduleViewModel)
        throws URISyntaxException, IOException {

        LOG.debug("REST request to save FlightSchedule from self-care portal : {}", flightScheduleViewModel);

        if (flightScheduleViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        FlightSchedule flightSchedule = flightScheduleMapper.toModel(flightScheduleViewModel);
        flightSchedule.setSelfCare(Boolean.TRUE);
        FlightScheduleViewModel result;

        User us =  userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);

        if (needApproval && us.getIsSelfcareUser()) {
            result = flightScheduleMapper.toViewModel(selfCarePortalApprovalRequestService.createNewApprovalRequest(
                flightSchedule, null, flightSchedule.getAccount().getId(), FlightSchedule.class,
                RequestDataset.FLIGHT_SCHEDULE, RequestType.CREATE));
        } else {
            result = flightScheduleMapper.toViewModel(flightScheduleService.create(flightSchedule));
        }

        return ResponseEntity.created(new URI("/api/sc-flight-schedules/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteFlightScheduleFromSC (@PathVariable Integer id) throws IOException {
        LOG.debug("REST request to delete FlightSchedule from self-care portal : {}", id);

        // self_care_access user can delete flight schedule only for his account
        // self_care_admin can delete flight schedule for any self-care account
        FlightSchedule fs = flightScheduleService.findOne(id);
        if (fs == null) {
            LOG.debug(FS_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(FS_NOT_FOUND));
        }
        Account ac = fs.getAccount();
        if (ac == null ) {
        	// can't delete flight schedule for not self-care account
            LOG.debug("No account found");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("No account found"));
        }
        User us =  userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        if (us.getIsSelfcareUser() && !ac.containsAccountUser(us))  {
            LOG.debug("Self-care user doesn't have permissions for this Flight Schedule");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Self-care user doesn't have permissions for this Flight Schedule"));
        }

        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);
        if (needApproval && us.getIsSelfcareUser()) {
            selfCarePortalApprovalRequestService.createNewApprovalRequest(fs, id, ac.getId(), FlightSchedule.class, RequestDataset.FLIGHT_SCHEDULE, RequestType.DELETE);
        } else {
            flightScheduleService.delete(id);
        }

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping
    @SuppressWarnings("squid:S3776")
    public ResponseEntity<?> getAllFlightScheduleFromSC(
        @RequestParam(name = "search", required = false) final String textSearch,
        @RequestParam(name = "accountId", required = false) final Integer accountId,
        @SortDefault.SortDefaults({
            @SortDefault(sort = { "account" }, direction = Sort.Direction.ASC),
            @SortDefault(sort = { "flightServiceNumber" }, direction = Sort.Direction.ASC)}) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) throws IOException {

        LOG.debug("REST request to get all FlightSchedule from self-care portal");

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can get only FlightSchedule for his own accounts
        // self_care_admin can get FlightSchedule for any self-care account

        Account ac = accountService.getOne(accountId);
        if (ac == null) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_ACCOUNT));
        }
        if (!ac.hasAccountUsers()) {
            LOG.debug(NO_FLIGHT_SCHEDULE);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(NO_FLIGHT_SCHEDULE));
        }

        Page<FlightScheduleViewModel> resultPage = null;
        List<FlightScheduleViewModel> flightScheduleList = null;
        long totalRecords = 0;
        long requestListSize = 0;

        if (!us.getIsSelfcareUser() || ac.containsAccountUser(us)) {
            flightScheduleList = flightScheduleMapper.toViewModel(flightScheduleService.findAll(accountId, textSearch));
            totalRecords = flightScheduleService.countAllSelfCareFlightSchedules(us.getIsSelfcareUser() ? us.getId() : null);
            boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);

            if (needApproval && us.getIsSelfcareUser()) {
                requestListSize = selfCarePortalApprovalRequestService.countAllinList(RequestStatus.OPEN.toValue(),
                    RequestDataset.FLIGHT_SCHEDULE.toValue(), us.getId());

                List<FlightScheduleViewModel> requestList = selfCarePortalApprovalRequestService.getFlightScheduleFromApprovalRequestList(
                    selfCarePortalApprovalRequestService.findAllinList(textSearch, RequestStatus.OPEN.toValue(),
                        RequestDataset.FLIGHT_SCHEDULE.toValue(), accountId, null, null, us.getId()));

                for (FlightScheduleViewModel schedule: requestList) {
                    flightScheduleList.removeIf(p -> Objects.equals(p.getId(), schedule.getId()));
                }

                flightScheduleList.addAll(requestList);
                flightScheduleList.sort(Comparator.comparing(FlightScheduleViewModel::getFlightServiceNumber));
            }
        }
        if (flightScheduleList != null) {
            if (csvExport != null && csvExport) {
                final List<FlightScheduleCsvExportModel> csvExportModel = flightScheduleMapper.toCsvModelFromViewModel(flightScheduleList);
                ReportDocument report = reportDocumentCreator.createCsvDocument("Flight_Schedules", csvExportModel, FlightScheduleCsvExportModel.class, true);
                return doCreateBinaryResponse(report);
            } else {
                flightScheduleService.addMissingAndUnexpectedFlights(flightScheduleList);
                int start = pageable.getOffset();
                int end = (start + pageable.getPageSize()) > flightScheduleList.size() ? flightScheduleList.size() : (start + pageable.getPageSize());
                resultPage = new PageImplCustom<>(flightScheduleList.subList(start, end), pageable, flightScheduleList.size(), totalRecords + requestListSize);
            }
        }
        return ResponseEntity.ok().body(resultPage);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<FlightScheduleViewModel> getFlightScheduleFromSC(@PathVariable Integer id) {
        LOG.debug("REST request to get FlightSchedule from self-care portal : {}", id);

        FlightSchedule fs = flightScheduleService.findOne(id);

        if (fs == null || fs.getAccount() == null || !fs.getAccount().hasAccountUsers()) {
            LOG.debug(SC_FS_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(SC_FS_NOT_FOUND));
        }

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can get only FlightSchedule for his own accounts
        // self_care_admin can get FlightSchedule for any self-care account

        if (us.getIsSelfcareUser() && !fs.getAccount().containsAccountUser(us))  {
            LOG.debug(NO_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(NO_PERMISSIONS));
        }
        return Optional.of(fs)
            .map(result -> new ResponseEntity<>(flightScheduleMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/pre-upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<List<UploadReportViewModel>> preUpload(
        @RequestParam("file") final MultipartFile file,
        @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate
    ) {

        LOG.debug("REST request to pre upload FlightSchedule file from self-care portal");

        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<FlightScheduleCsvViewModel> scheduled;
        try {
            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                FlightScheduleCsvViewModel.class);
            scheduled.forEach(x -> x.setStartDate(startDate));
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can preUpload only FlightSchedule for his own accounts
        // self_care_admin can preUpload FlightSchedule for any self-care account
       Integer acId = flightScheduleService.getAccountId(scheduled);
       if(acId == null) {
           LOG.debug("Flight schedule file don't have account information");
           throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
               new Exception("Flight schedule file don't have account information"));

       }
       Account ac = accountService.getOne(acId);
       if (ac == null || !ac.hasAccountUsers()) {
           LOG.debug("Account for the Flight schedule not found");
           throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
               new Exception("Account for the Flight schedule not found"));
       }
       if (us.getIsSelfcareUser() && !ac.containsAccountUser(us))  {
           LOG.debug(NO_PERMISSIONS);
           throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
               new Exception(NO_PERMISSIONS));
       }

        // noinspection unchecked
        final List<UploadReportViewModel> summary = loaderService.checkBulkUpload(scheduled);
        return ResponseEntity.ok().body(summary);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<FlightScheduleViewModel> updateFlightScheduleFromSC(
        @RequestBody FlightScheduleViewModel flightScheduleViewModel,
        @PathVariable Integer id
    ) throws IOException {

        LOG.debug("REST request to update FlightSchedule from self-care portal : {}", flightScheduleViewModel);
        FlightSchedule fs = flightScheduleService.findOne(id);

        if (fs == null || fs.getAccount() == null || !fs.getAccount().hasAccountUsers()) {
            LOG.debug(SC_FS_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(SC_FS_NOT_FOUND));
        }

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can update only FlightSchedule for his own accounts
        // self_care_admin can update FlightSchedule for any self-care account
        if (us.getIsSelfcareUser() && !fs.getAccount().containsAccountUser(us))  {
            LOG.debug(NO_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(NO_PERMISSIONS));
        }

        FlightScheduleViewModel result;
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_FLIGHT_SCHEDULES);

        FlightSchedule flightSchedule = flightScheduleMapper.toModel(flightScheduleViewModel);

        if (needApproval && us.getIsSelfcareUser()) {
            if (flightScheduleViewModel.getScRequestId() != null && Objects.equals(flightScheduleViewModel.getScRequestType(), "update")) {
                result = checkSCApprovalRequest(flightScheduleViewModel);
            } else {
                result = flightScheduleMapper.toViewModel(selfCarePortalApprovalRequestService.createNewApprovalRequest(
                    flightSchedule, id, flightSchedule.getAccount().getId(), FlightSchedule.class,
                    RequestDataset.FLIGHT_SCHEDULE, RequestType.UPDATE));
            }
        } else {
            FlightSchedule flightScheduleUpdate = flightScheduleService.update(id, flightSchedule);
            result = flightScheduleMapper.toViewModel(flightScheduleUpdate);
        }
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_access')")
    @PutMapping
    public ResponseEntity<FlightScheduleViewModel> updateSCApprovalRequest(
        @RequestBody FlightScheduleViewModel flightScheduleDto
    ) throws IOException {

        FlightScheduleViewModel result = checkSCApprovalRequest(flightScheduleDto);

        return ResponseEntity.ok().body(result);
    }

    private FlightScheduleViewModel checkSCApprovalRequest(FlightScheduleViewModel flightScheduleDto ) throws IOException {
        LOG.debug("REST request to update self-care Flight Schedule that is not approved : {}", flightScheduleDto);

        if (flightScheduleDto.getScRequestId() == null) {
            LOG.debug("Bad request: there is no Approval Request for this FlightSchedule");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("There is no Approval Request for this FlightSchedule. FlightSchedule can't be updated"));
        }
        final Integer requestId = flightScheduleDto.getScRequestId();
        final FlightSchedule flightSchedule = flightScheduleMapper.toModel(flightScheduleDto);
        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        if (flightSchedule == null || us == null) {
            LOG.debug(FS_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(FS_NOT_FOUND));
        }

        return flightScheduleMapper.toViewModel(
            selfCarePortalApprovalRequestService.updateUnprovenApprovalRequest(requestId, flightSchedule, FlightSchedule.class));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<UploadReportParsedItems> upload(@RequestParam("file") final MultipartFile file,
                                                          @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate) {

        LOG.debug("REST request to upload FlightSchedule file from self-care portal");

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        List<FlightScheduleCsvViewModel> scheduled;
        try {
            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                FlightScheduleCsvViewModel.class);
            scheduled.forEach(x -> x.setStartDate(startDate));
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        // self_care_access user can Upload only FlightSchedule for his own accounts
        // self_care_admin can Upload FlightSchedule for any self-care account
       Integer acId = flightScheduleService.getAccountId(scheduled);
       if (acId == null) {
           LOG.debug("Flight schedule file don't have account information. Access denied");
           throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
               new Exception("Flight schedule file don't have account information. Access denied"));
       }

       Account ac = accountService.getOne(acId);
       if (ac == null || !ac.hasAccountUsers()) {
           LOG.debug("Account for the Flight schedule not found. Access denied");
           throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
               new Exception("Account for the Flight schedule not found. Access denied"));
       }

       if (us.getIsSelfcareUser() && !ac.containsAccountUser(us)) {
            LOG.debug(NO_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(NO_PERMISSIONS));
       }

        // noinspection unchecked
       final List<UploadReportViewModel> summary = loaderService.reportBulkUpload(scheduled, file);

       return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(scheduled, summary));
    }

}
