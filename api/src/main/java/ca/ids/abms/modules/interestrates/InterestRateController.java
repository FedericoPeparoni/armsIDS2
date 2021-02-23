package ca.ids.abms.modules.interestrates;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/interest-rate")
@SuppressWarnings({"unused", "squid:S1452"})
public class InterestRateController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(InterestRateController.class);

    private final InterestRateService interestRateService;
    private final InterestRateMapper interestRateMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public InterestRateController(final InterestRateService interestRateService,
                                  final InterestRateMapper interestRateMapper,
                                  final ReportDocumentCreator reportDocumentCreator) {
        this.interestRateService = interestRateService;
        this.interestRateMapper = interestRateMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('interest_rate_modify')")
    @PostMapping
    public ResponseEntity<InterestRateViewModel> createInterestRate(@Valid @RequestBody final InterestRateViewModel interestRateViewModel) throws URISyntaxException {
        LOG.debug("REST request to save Interest Rate : {}", interestRateViewModel);

        if (interestRateViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }

        final InterestRateViewModel result = interestRateMapper.toViewModel(interestRateService.save(interestRateMapper.toModel(interestRateViewModel)));
        return ResponseEntity.created(new URI("/api/interest-rate/" + result.getId())).body(result);
    }

    @PreAuthorize("hasAuthority('interest_rate_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<InterestRateViewModel> updateInterestRate(@Valid @RequestBody final InterestRateViewModel interestRateViewModel,
                                                                    @PathVariable final Integer id) {

        LOG.debug("REST request to update Interest Rate : {}", interestRateViewModel);

        final InterestRateViewModel result = interestRateMapper.toViewModel(interestRateService.update(id, interestRateMapper.toModel(interestRateViewModel)));
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('interest_rate_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteInterestRate(@PathVariable final Integer id) {
        LOG.debug("REST request to delete Interest Rate : {}", id);

        interestRateService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAuthority('interest_rate_view')")
    @GetMapping(value = "/{id}")
    public ResponseEntity<InterestRateViewModel> getInterestRate(@PathVariable final Integer id) {
        LOG.debug("REST request to get Interest Rate : {}", id);

        InterestRate interestRate = interestRateService.getOne(id);

        return Optional.ofNullable(interestRate)
            .map(result -> new ResponseEntity<>(interestRateMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('interest_rate_view')")
    @GetMapping
    public ResponseEntity<?> getAllInterestRates(@SortDefault(sort = {"startDate"}, direction = Sort.Direction.DESC) Pageable pageable,
                                                 @RequestParam(name = "search", required = false) final String textSearch,
                                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get Interest Rate that contain the text: {}", textSearch);

        final Page<InterestRate> page = interestRateService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<InterestRate> list = page.getContent();
            final List<InterestRateCsvExportModel> csvExportModel = interestRateMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Interest_Rates", csvExportModel,
                InterestRateCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<InterestRateViewModel> resultPage = new PageImplCustom<>(interestRateMapper.toViewModel(page),
                pageable, page.getTotalElements(), interestRateService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }
}
