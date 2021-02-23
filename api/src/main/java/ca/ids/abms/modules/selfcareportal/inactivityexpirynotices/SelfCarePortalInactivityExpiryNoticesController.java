package ca.ids.abms.modules.selfcareportal.inactivityexpirynotices;

import ca.ids.abms.modules.common.controllers.AbmsCrudController;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;

@RestController
@RequestMapping(SelfCarePortalInactivityExpiryNoticesController.ENDPOINT)
@SuppressWarnings({"unused", "squid:S1452"})
public class SelfCarePortalInactivityExpiryNoticesController extends AbmsCrudController<SelfCarePortalInactivityExpiryNotice,
    SelfCarePortalInactivityExpiryNoticesViewModel, SelfCarePortalInactivityExpiryNoticesCsvExportModel, Integer> {

    static final String ENDPOINT = "/api/self-care-portal-inactivity-expiry-notices";

    private static final Logger LOG = LoggerFactory.getLogger(SelfCarePortalInactivityExpiryNoticesController.class);

    public SelfCarePortalInactivityExpiryNoticesController(final SelfCarePortalInactivityExpiryNoticesMapper selfCarePortalInactivityExpiryNoticesMapper,
                                                           final SelfCarePortalInactivityExpiryNoticesService selfCarePortalInactivityExpiryNoticesService,
                                                           final ReportDocumentCreator reportDocumentCreator) {
        super(ENDPOINT, selfCarePortalInactivityExpiryNoticesMapper, selfCarePortalInactivityExpiryNoticesService,
            reportDocumentCreator, "Inactivity_and_Expiry_Notices", SelfCarePortalInactivityExpiryNoticesCsvExportModel.class);
    }

    @Override
    @GetMapping
    @PreAuthorize("hasAuthority('self_care_admin')")
    public ResponseEntity<?> getPage(@RequestParam(required = false) final String search,
                                     @SortDefault.SortDefaults({@SortDefault(sort = { "account" })}) final Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Self Care Portal inactivity/expiry notices with search '{}' for page '{}'", search, pageable);
        return super.doGetPage(search, pageable, csvExport);
    }

    @Override
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('self_care_admin')")
    public ResponseEntity<SelfCarePortalInactivityExpiryNoticesViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get Self Care Portal inactivity/expiry notice with id '{}'", id);
        return super.doGetOne(id);
    }

    @Override
    public ResponseEntity<SelfCarePortalInactivityExpiryNoticesViewModel> create(SelfCarePortalInactivityExpiryNoticesViewModel viewModel) throws URISyntaxException {
        return ResponseEntity.badRequest().body(new SelfCarePortalInactivityExpiryNoticesViewModel());
    }
    @Override
    public ResponseEntity<SelfCarePortalInactivityExpiryNoticesViewModel> update(Integer id, SelfCarePortalInactivityExpiryNoticesViewModel viewModel) {
        return ResponseEntity.badRequest().body(new SelfCarePortalInactivityExpiryNoticesViewModel());
    }
    @Override
    public ResponseEntity delete(Integer id) {
        return ResponseEntity.badRequest().build();
    }
}
