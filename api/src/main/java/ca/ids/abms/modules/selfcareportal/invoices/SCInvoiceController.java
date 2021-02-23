package ca.ids.abms.modules.selfcareportal.invoices;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.billings.*;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.mappers.DateTimeMapperUtils;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/sc-billing-ledgers")
@SuppressWarnings({"unused", "squid:S1452"})
public class SCInvoiceController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(SCInvoiceController.class);
    private final BillingLedgerService billingLedgerService;
    private final BillingLedgerMapper billingLedgerMapper;
    private final UserService userService;
    private final ReportDocumentCreator reportDocumentCreator;

    public SCInvoiceController(final BillingLedgerService billingLedgerService,
                               final BillingLedgerMapper billingLedgerMapper,
                               final UserService userService,
                               final ReportDocumentCreator reportDocumentCreator) {
        this.billingLedgerService = billingLedgerService;
        this.billingLedgerMapper = billingLedgerMapper;
        this.userService = userService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('self_care_admin','self_care_access')")
    @SuppressWarnings("squid:S00107")
    public ResponseEntity<?> getAllBillingLedgers(@RequestParam(name = "status", required = false) String filter,
                                                  @RequestParam(name = "search", required = false) final String textSearch,
                                                  @RequestParam(name = "account", required = false) Integer accountId,
                                                  @RequestParam(name = "startDate", required = false) String startDate,
                                                  @RequestParam(name = "endDate", required = false) String endDate,
                                                  @RequestParam(name = "flightIdOrRegistration", required = false) final String flightIdOrRegistration,
                                                  @RequestParam(name = "createdByUserId", required = false) final Integer createdByUserId,
                                                  @SortDefault(sort = {"invoiceNumber"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                  @RequestParam(name = "csvExport", required = false) Boolean csvExport,
                                                  @RequestParam(name = "billingCentre", required = false) Integer billingCentreId) {

        User us = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());
        final Page<BillingLedger> page;
        final long totalRecords;

        LocalDateTime startDateFilter = null;
        LocalDateTime endDateFilter = null;

        if (startDate != null && endDate != null) {
            startDateFilter = DateTimeMapperUtils.parseISODate(startDate).atStartOfDay();
            endDateFilter = DateTimeMapperUtils.parseISODate(endDate).atTime(LocalTime.MAX);
        }

        // self_care_access user can get invoices only for his own accounts
        // self_care_admin can get invoices for any self-care account
        if (us.getIsSelfcareUser()) {
            LOG.debug("REST request to get all billing ledgers for Self-Care user {} ", us.getName());
            page = billingLedgerService.findAll(filter, textSearch, pageable, null, us.getId(), null,
                accountId, startDateFilter, endDateFilter, null, flightIdOrRegistration, createdByUserId, billingCentreId);
            totalRecords = billingLedgerService.countAllForSelfCareUser(us.getId());
        } else {
            LOG.debug("REST request to get all AircraftRegistrations for Self-Care accounts");
            page = billingLedgerService.findAll(filter, textSearch, pageable, true, null, null,
                accountId, startDateFilter, endDateFilter, null, flightIdOrRegistration, createdByUserId, billingCentreId);
            totalRecords = billingLedgerService.countAllForSelfCareAccounts();
        }

        if (csvExport != null && csvExport) {
            final List<BillingLedger> list = page.getContent();
            final List<BillingLedgerCsvExportModel> csvExportModel = billingLedgerMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Invoices", csvExportModel,
                BillingLedgerCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<BillingLedgerViewModel> resultPage = new PageImplCustom<>(billingLedgerMapper.toViewModel(page),
                pageable, page.getTotalElements(), totalRecords);
            return ResponseEntity.ok().body(resultPage);
        }
    }
}
