package ca.ids.abms.modules.selfcareportal.flightsearch;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/sc-flight-search")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCFlightSearchController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCFlightSearchController.class);
    private final UserService userService;
    private final SCFlightSearchMapper sCFlightSearchMapper;
    private final SCFlightSearchService sCFlightSearchService;
    private final ReportDocumentCreator reportDocumentCreator;

    public SCFlightSearchController(final UserService userService,
                                    final SCFlightSearchMapper sCFlightSearchMapper,
                                    final SCFlightSearchService sCFlightSearchService,
                                    final ReportDocumentCreator reportDocumentCreator) {
        this.userService = userService;
        this.sCFlightSearchMapper = sCFlightSearchMapper;
        this.sCFlightSearchService = sCFlightSearchService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllFlightMovementsForSelfCareAccounts(
        @SortDefault.SortDefaults({
            @SortDefault(sort = {"accountName"}, direction=Sort.Direction.ASC),
            @SortDefault(sort = {"dateOfFlight"}, direction=Sort.Direction.DESC),
            @SortDefault(sort = {"depTime"}, direction = Sort.Direction.ASC)})  Pageable pageable,
        @RequestParam(name = "account", required = false) Integer accountId,
        @RequestParam(name = "flightId", required = false) String flightId,
        @RequestParam(name = "icaoCode", required = false) String icaoCode,
        @RequestParam(name = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate startDate,
        @RequestParam(name = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) final LocalDate endDate,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all flight movements for self-Care accounts");

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        // self_care_access user can get only flight movements for his own accounts
        // self_care_admin can get flight movements for any self-care account
        Page<FlightMovement> page;
        long totalRecords;

        if (!us.getIsSelfcareUser()) {
            LOG.debug("REST request to get all flight movements by filter for Self-Care accounts");
            page = sCFlightSearchService.findAllFlightMovementByFilter(pageable, accountId, flightId, icaoCode, startDate, endDate, null);
            totalRecords = sCFlightSearchService.countAllForSelfCareAccounts();
        } else {
            int userId = us.getId();
            LOG.debug("REST request to get all flight movements by filter for Self-Care user: {}", userId);
            page = sCFlightSearchService.findAllFlightMovementByFilter(pageable, accountId, flightId, icaoCode, startDate, endDate, userId);
            totalRecords = sCFlightSearchService.countAllForSelfCareUser(userId);
        }

        if (csvExport != null && csvExport) {
            final List<FlightMovement> list = page.getContent();
            final List<SCFlightMovementCsvExportModel> csvExportModel = sCFlightSearchMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Flight_Search_Result", csvExportModel,
                SCFlightMovementCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            return ResponseEntity.ok().body(new PageImplCustom<>(sCFlightSearchMapper.toViewModel(page), pageable, page.getTotalElements(), totalRecords));
        }
    }
}
