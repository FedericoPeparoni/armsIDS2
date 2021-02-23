package ca.ids.abms.modules.selfcareportal.aircraftregistration;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.accounts.Account;
import ca.ids.abms.modules.aircraft.*;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
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
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/sc-aircraft-registration")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCAircraftRegistrationController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftRegistrationController.class);

    private final AircraftRegistrationService aircraftRegistrationService;
    private final CountryService countryService;
    private final UserService userService;
    private final AircraftRegistrationMapper aircraftRegistrationMapper;
    private final SystemConfigurationService systemConfigurationService;
    private final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService;
    private final ReportDocumentCreator reportDocumentCreator;

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String AIRCRAFT_REGISTRATION_LOADER = "aircraftRegistrationLoader";

    private static final String AR_NOT_FOUND = "Aircraft Registration not found";
    private static final String NO_ACCOUNT = "Account doesn't exist";
    private static final String AR_NOT_CREATED = "Error: Aircraft Registration not created";
    private static final String SC_USER_NO_ACCOUNT_PERMISSIONS = "Self-care user doesn't have permissions for this account";
    private static final String SC_USER_NO_AR_PERMISSIONS = "Self-care user doesn't have permissions for this aircraft registration";
    private static final String NO_APPROVAL_REQUEST = "There is no Approval Request for this AircraftRegistration. AircraftRegistration can't be updated";

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<AircraftRegistrationCsvViewModel> dataImportService;

    @Qualifier(AIRCRAFT_REGISTRATION_LOADER)
    private AircraftRegistrationLoader loaderService;

    @SuppressWarnings("squid:S00107")
    public SCAircraftRegistrationController(final AircraftRegistrationService aircraftRegistrationService,
                                            final CountryService countryService,
                                            final AircraftRegistrationLoader loaderService,
                                            final UserService userService,
                                            final AircraftRegistrationMapper aircraftRegistrationMapper,
                                            final SystemConfigurationService systemConfigurationService,
                                            final SelfCarePortalApprovalRequestService selfCarePortalApprovalRequestService,
                                            final ReportDocumentCreator reportDocumentCreator) {
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.countryService = countryService;
        this.loaderService = loaderService;
        this.userService = userService;
        this.aircraftRegistrationMapper = aircraftRegistrationMapper;
        this.systemConfigurationService = systemConfigurationService;
        this.selfCarePortalApprovalRequestService = selfCarePortalApprovalRequestService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PostMapping
    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    public ResponseEntity<AircraftRegistrationViewModel> createSCAircraftRegistration(
        @Valid @RequestBody AircraftRegistrationViewModel aircraftRegistration) throws IOException, URISyntaxException {

        LOG.debug("REST request to save SCAircraftRegistration : {}", aircraftRegistration);

        if (aircraftRegistration.getRegistrationNumber() == null || aircraftRegistration.getAccount().getId() == null) {
            LOG.debug("Bad request: Registration Number or Account is missing");
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception("Registration Number or Account is missing"));
        }

        aircraftRegistration.setCreatedBySelfCare(true);
        AircraftRegistration model = aircraftRegistrationMapper.toModel(aircraftRegistration);

        User us = getCurrentUser();
        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);

        AircraftRegistrationViewModel result;
        if (needApproval && us.getIsSelfcareUser()) {
            result = aircraftRegistrationMapper.toViewModel(selfCarePortalApprovalRequestService.createNewApprovalRequest(
                model, null, model.getAccount().getId(), AircraftRegistration.class,
                RequestDataset.AIRCRAFT_REGISTRATION, RequestType.CREATE));
        } else {
            result = aircraftRegistrationMapper.toViewModel(aircraftRegistrationService.save(model));
        }

        if (result == null) {
            LOG.debug(AR_NOT_CREATED);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(AR_NOT_CREATED));
        }

        return ResponseEntity.created(new URI("/api/aircraft-registrations/" + result.getRegistrationNumber()))
                .body(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    public ResponseEntity<Void> deleteAircraftRegistration(@PathVariable Integer id) throws IOException {
        LOG.debug("REST request to delete SCAircraftRegistration : {}", id);

        AircraftRegistration ar = aircraftRegistrationService.getOne(id);
        if (ar == null) {
            LOG.debug(AR_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(AR_NOT_FOUND));
        }

        Account ac = ar.getAccount();
        if (ac == null) {
        	// can't delete aircraft registration for not self-care account
            LOG.debug("Can't delete aircraft registration for not self-care account");
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception("Can't delete aircraft registration for not self-care account"));
        }
        User us = getCurrentUser();

        // self_care_access user can delete aircraft registration only for his account
        // self_care_admin can delete aircraft registration for any self-care account
        if (us.getIsSelfcareUser() && !ar.getAccount().containsAccountUser(us))  {
            LOG.debug(SC_USER_NO_AR_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(SC_USER_NO_AR_PERMISSIONS));
        }

        boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);
        if (needApproval && us.getIsSelfcareUser()) {
            selfCarePortalApprovalRequestService.createNewApprovalRequest(ar, id, ac.getId(), AircraftRegistration.class,
                RequestDataset.AIRCRAFT_REGISTRATION, RequestType.DELETE);
        } else {
            aircraftRegistrationService.delete(id);
        }

        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<AircraftRegistrationViewModel> getAircraftRegistration(@PathVariable Integer id) {
        LOG.debug("REST request to get AircraftRegistration : {}", id);

        AircraftRegistration ar = aircraftRegistrationService.getOne(id);
        if(ar == null || ar.getAccount() == null ) {
            LOG.debug(AR_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(AR_NOT_FOUND));
        }

        User us = getCurrentUser();

        // self_care_access user can get only aircraft registration for his own accounts
        // self_care_admin can get aircraft registration for any self-care account
        if(us.getIsSelfcareUser() && !ar.getAccount().containsAccountUser(us))  {
            LOG.debug(SC_USER_NO_ACCOUNT_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(SC_USER_NO_ACCOUNT_PERMISSIONS));
        }

        AircraftRegistrationViewModel aircraftRegistrationViewModel = aircraftRegistrationMapper.toViewModel(ar);

        return Optional.of(aircraftRegistrationViewModel).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @GetMapping
    public ResponseEntity<?> getSCAircraftRegistrations(@SortDefaults({
                                                        @SortDefault(sort = { "registrationNumber" },
                                                            direction = Sort.Direction.ASC),
                                                        @SortDefault(sort = { "registrationStartDate" },
                                                            direction = Sort.Direction.ASC) }) Pageable pageable,
                                                        @RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                                        @RequestParam(name = "csvExport", required = false) Boolean csvExport) throws IOException {

        LOG.debug("REST request to get all AircraftRegistrations");
        User us = getCurrentUser();

        // self_care_access user can get only aircraft registration for his own accounts
        // self_care_admin can get aircraft registration for any self-care account
        List<AircraftRegistrationViewModel> arList;
        long totalRecords;
        long requestListSize = 0;
        if (us.getIsSelfcareUser()) {
            LOG.debug("REST request to get all AircraftRegistrations for Self-Care user");

            boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);
            totalRecords = aircraftRegistrationService.countAllAircraftRegistrationsForSelfCareUser(us.getId());
            if (needApproval && us.getIsSelfcareUser()) {
                arList = aircraftRegistrationMapper.toViewModel(aircraftRegistrationService.findAircraftRegistrationForSelfCareUser(us.getId(), searchFilter));
                requestListSize = selfCarePortalApprovalRequestService.countAllinList(RequestStatus.OPEN.toValue(),
                    RequestDataset.AIRCRAFT_REGISTRATION.toValue(), us.getId());
                List<AircraftRegistrationViewModel> requestList = selfCarePortalApprovalRequestService.getAircraftRegistrationFromApprovalRequestList(
                    selfCarePortalApprovalRequestService.findAllinList(searchFilter, RequestStatus.OPEN.toValue(),
                        RequestDataset.AIRCRAFT_REGISTRATION.toValue(), null, null, null, us.getId()));

                for (AircraftRegistrationViewModel ar: requestList) {
                    arList.removeIf(p -> Objects.equals(p.getId(), ar.getId()));
                }

                arList.addAll(requestList);
                arList.sort(Comparator.comparing(AircraftRegistrationViewModel::getRegistrationNumber));

            } else {
                arList = aircraftRegistrationMapper.toViewModel(aircraftRegistrationService.findAircraftRegistrationForSelfCareUser(us.getId(), searchFilter));
            }
        } else {
            LOG.debug("REST request to get all AircraftRegistrations for Self-Care accounts");
        	arList = aircraftRegistrationMapper.toViewModel(aircraftRegistrationService.findAllAircraftRegistrationForSelfCareAccounts(searchFilter));
            totalRecords = aircraftRegistrationService.countAllForSelfCareAccounts();
        }

        int start = pageable.getOffset();
        int end = (start + pageable.getPageSize()) > arList.size() ? arList.size() : (start + pageable.getPageSize());

        if (csvExport != null && csvExport) {
            final List<AircraftRegistrationCsvExportModel> csvExportModel = aircraftRegistrationMapper.toCsvModelFromViewModel(arList);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aircraft_Registrations", csvExportModel, AircraftRegistrationCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AircraftRegistrationViewModel> resultPage = new PageImplCustom<>(arList.subList(start, end),
                pageable, arList.size(), totalRecords + requestListSize);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/prefix/{prefix}")
    public ResponseEntity<Country> getCountryByPrefix(@PathVariable String prefix) {
        LOG.debug("REST request to get Country by prefix: {}", prefix);

        Country country = countryService.findCountryByPrefix(prefix);

        return Optional.ofNullable(country).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AircraftRegistrationViewModel> updateSCAircraftRegistration(
        @RequestBody AircraftRegistrationViewModel aircraftRegistrationViewModel, @PathVariable Integer id
    ) throws IOException {

        LOG.debug("REST request to update AircraftRegistration : {}", aircraftRegistrationViewModel);
        LOG.debug("Code : {}", id);

        AircraftRegistration ar = aircraftRegistrationService.getOne(id);
        if (ar == null) {
            LOG.debug(AR_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(AR_NOT_FOUND));
        }

        Account ac = ar.getAccount();
        if (ac == null ) {
            LOG.debug(NO_ACCOUNT);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_ACCOUNT));
        }

        User us = getCurrentUser();

        // self_care_access user can update only aircraft registration for his own accounts
        // self_care_admin can update aircraft registration for any self-care account
        AircraftRegistrationViewModel result;
        if (!us.getIsSelfcareUser() || ac.containsAccountUser(us)) {

            boolean needApproval = systemConfigurationService.getBoolean(SystemConfigurationItemName.REQUIRE_ADMIN_APPROVAL_FOR_SC_AIRCRAFT_REGISTRATION);
            AircraftRegistration aircraftRegistration = aircraftRegistrationMapper.toModel(aircraftRegistrationViewModel);

            if (needApproval && us.getIsSelfcareUser()) {
                if (aircraftRegistrationViewModel.getScRequestId() != null && Objects.equals(aircraftRegistrationViewModel.getScRequestType(), "update")) {
                    result = checkSCApprovalRequest(aircraftRegistrationViewModel);
                } else {
                    result = aircraftRegistrationMapper.toViewModel(selfCarePortalApprovalRequestService.
                        createNewApprovalRequest(aircraftRegistration, id, ac.getId(), AircraftRegistration.class, RequestDataset.AIRCRAFT_REGISTRATION, RequestType.UPDATE));
                }
            } else {
                result = aircraftRegistrationMapper.toViewModel(aircraftRegistrationService.update(id, aircraftRegistration));
            }
        }  else {
            LOG.debug(SC_USER_NO_ACCOUNT_PERMISSIONS);
            throw new CustomParametrizedException(ErrorConstants.ERR_ACCESS_DENIED,
                new Exception(SC_USER_NO_ACCOUNT_PERMISSIONS));
        }

        return ResponseEntity.ok().body(result);
    }

    private AircraftRegistrationViewModel checkSCApprovalRequest(AircraftRegistrationViewModel dto) throws IOException {
        LOG.debug("REST request to update self-care Aircraft Registration that is not approved : {}", dto);

        if (dto.getScRequestId() == null) {
            LOG.debug(NO_APPROVAL_REQUEST);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_APPROVAL_REQUEST));
        }
        final Integer requestId = dto.getScRequestId();
        final AircraftRegistration aircraftRegistration = aircraftRegistrationMapper.toModel(dto);
        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        if (aircraftRegistration == null || us == null) {
            LOG.debug(AR_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(AR_NOT_FOUND));
        }

        return aircraftRegistrationMapper.toViewModel(
            selfCarePortalApprovalRequestService.updateUnprovenApprovalRequest(requestId, aircraftRegistration, AircraftRegistration.class));
    }

    @PreAuthorize("hasAnyAuthority('self_care_access')")
    @PutMapping
    public ResponseEntity<AircraftRegistrationViewModel> updateSCApprovalRequest(@RequestBody AircraftRegistrationViewModel airRegDto)
        throws IOException {
        LOG.debug("REST request to update self-care AircraftRegistration that is not approved : {}", airRegDto);

        if (airRegDto.getScRequestId() == null) {
            LOG.debug(NO_APPROVAL_REQUEST);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION,
                new Exception(NO_APPROVAL_REQUEST));
        }
        final Integer requestId = airRegDto.getScRequestId();
        final AircraftRegistration ac = aircraftRegistrationMapper.toModel(airRegDto);
        User us = getCurrentUser();
        if(ac == null || us == null) {
            LOG.debug(AR_NOT_FOUND);
            throw new CustomParametrizedException(ErrorConstants.ERR_VALIDATION, new Exception(AR_NOT_FOUND));
        }

        AircraftRegistrationViewModel result = aircraftRegistrationMapper.toViewModel(selfCarePortalApprovalRequestService.updateUnprovenApprovalRequest(requestId, ac, AircraftRegistration.class));

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<UploadReportParsedItems> upload(@RequestParam("file") final MultipartFile file) {
        LOG.debug("REST request to upload AircraftRegistration file");

        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<AircraftRegistrationCsvViewModel> scheduled;
        try {
        	loaderService.setIsSelfCare(true);

            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                    AircraftRegistrationCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }
        //noinspection unchecked
        final List<UploadReportViewModel> summary = loaderService.reportBulkUpload(scheduled, file);

        return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(scheduled, summary));
    }

    /**
	 * Return the currently logged-in user
	 */
	public User getCurrentUser() {
		return userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
	}
}
