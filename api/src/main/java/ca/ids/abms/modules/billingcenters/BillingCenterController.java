package ca.ids.abms.modules.billingcenters;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/billing-centers")
@SuppressWarnings("squid:S1452")
public class BillingCenterController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(BillingCenterController.class);

    private final BillingCenterService billingCenterService;
    private final BillingCenterMapper billingCenterMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public BillingCenterController(final BillingCenterService billingCenterService,
                                   final BillingCenterMapper billingCenterMapper,
                                   final ReportDocumentCreator reportDocumentCreator){
        this.billingCenterService = billingCenterService;
        this.billingCenterMapper = billingCenterMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('billing_center_modify')")
    @PostMapping
    public ResponseEntity<BillingCenterViewModel> createBillingCenter(@Valid @RequestBody BillingCenterViewModel billingCenterViewModel) throws URISyntaxException {
        LOG.debug("REST request to save BillingCenter : {}", billingCenterViewModel);

        BillingCenter billingCenter = billingCenterMapper.toModel(billingCenterViewModel);
        BillingCenter billingCenterCreate = billingCenterService.create(billingCenter);
        BillingCenterViewModel result = billingCenterMapper.toViewModel(billingCenterCreate);

        return ResponseEntity.created(new URI("/api/billing-centers/" + result.getId()))
            .body(result);
    }

    @PreAuthorize("hasAuthority('billing_center_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<BillingCenterViewModel> updateBillingCenter(@RequestBody BillingCenterViewModel billingCenterViewModel, @PathVariable Integer id) {
        LOG.debug("REST request to update BillingCenter : {}", billingCenterViewModel);

        BillingCenter billingCenter = billingCenterMapper.toModel(billingCenterViewModel);
        BillingCenter billingCenterUpdate = billingCenterService.update(id, billingCenter);
        BillingCenterViewModel result = billingCenterMapper.toViewModel(billingCenterUpdate);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('billing_center_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteBillingCenter(@PathVariable Integer id) {
        LOG.debug("REST request to delete BillingCenter : {}", id);
        billingCenterService.delete(id);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<?> getAllBillingCenter(@RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                                 @SortDefault(sort = {"name"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Billing Centers");
        final Page<BillingCenter> page = billingCenterService.findAll(pageable, searchFilter);
        if (csvExport != null && csvExport) {
            final List<BillingCenter> list = page.getContent();
            final List<BillingCenterCsvExportModel> csvExportModel = billingCenterMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Billing_Centers", csvExportModel, BillingCenterCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<BillingCenterViewModel> resultPage = new PageImplCustom<>(billingCenterMapper.toViewModel(page), pageable, page.getTotalElements(),
                billingCenterService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<BillingCenterViewModel> getBillingCenter(@PathVariable Integer id) {
        LOG.debug("REST request to get BillingCenter : {}", id);

        BillingCenter billingCenter = billingCenterService.findOne(id);

        return Optional.ofNullable(billingCenter)
            .map(result -> new ResponseEntity<>(billingCenterMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/allMinimal")
    public Collection<BillingCenterComboViewModel> findAllMinimal() {
        LOG.debug("REST request to get all billing centres, with minimal return data");
        return billingCenterMapper.toComboViewModel(billingCenterService.findAll());
    }

    @GetMapping(value = "/{depAd}/{destAd}")
    public ResponseEntity<BillingCenterViewModel> getBillingCenterByAerodromes(@PathVariable String depAd, @PathVariable String destAd) {
        LOG.debug("REST request to get a billing centre by aerodromes {} and {}", depAd, destAd);

        FlightMovement fm = this.billingCenterService.getFlightMovementWithBillingCenter(depAd, destAd);

        BillingCenterViewModel bcvm = this.billingCenterMapper.toViewModel(fm.getBillingCenter());

        return ResponseEntity.ok().body(bcvm);
    }

}
