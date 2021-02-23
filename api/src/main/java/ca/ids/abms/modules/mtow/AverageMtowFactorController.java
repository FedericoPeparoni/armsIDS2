package ca.ids.abms.modules.mtow;

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
@RequestMapping("/api/average-mtow-factors")
@SuppressWarnings({"unused", "squid:S1452"})
public class AverageMtowFactorController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AverageMtowFactorController.class);
    private final AverageMtowFactorService averageMtowFactorService;
    private final AverageMtowFactorMapper averageMtowFactorMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public AverageMtowFactorController(final AverageMtowFactorService anAverageMtowFactorService,
                                       final AverageMtowFactorMapper anAverageMtowFactorMapper,
                                       final ReportDocumentCreator reportDocumentCreator) {
        averageMtowFactorService = anAverageMtowFactorService;
        averageMtowFactorMapper = anAverageMtowFactorMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllAverageMtowFactors(@RequestParam(required = false) final FactorClass filter,
                                                      @SortDefault(sort = {"upperLimit"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all average mtow factors with factor class: {}", filter);
        final Page<AverageMtowFactor> page = averageMtowFactorService.findAll(filter, pageable);

        if (csvExport != null && csvExport) {
            final List<AverageMtowFactor> list = page.getContent();
            final List<AverageMtowFactorCsvExportModel> csvExportModel = averageMtowFactorMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Average_Mtow_Factors", csvExportModel,
                AverageMtowFactorCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AverageMtowFactorViewModel> resultPage = new PageImplCustom<>(averageMtowFactorMapper.toViewModel(page),
                pageable, page.getTotalElements(), averageMtowFactorService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AverageMtowFactorViewModel> getAverageMtowFactor(@PathVariable final Integer id) {
        LOG.debug("REST request to get average mtow factor : {}", id);

        AverageMtowFactor averageMtowFactor = averageMtowFactorService.getOne(id);

        return Optional.ofNullable(averageMtowFactor)
                .map(result -> new ResponseEntity<>(averageMtowFactorMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/upper-limit/{upperLimit}")
    public ResponseEntity<AverageMtowFactorViewModel> getAverageMtowFactor(@PathVariable final Double upperLimit) {
        LOG.debug("REST request to get best mtow factor by upperLimit: {}", upperLimit);

        AverageMtowFactor averageMtowFactor = averageMtowFactorService.getOneByUpperLimit(upperLimit);

        return Optional.ofNullable(averageMtowFactor)
                .map(result -> new ResponseEntity<>(averageMtowFactorMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('avg_mtow_factor_modify')")
    @PostMapping
    public ResponseEntity<AverageMtowFactorViewModel> create(@Valid @RequestBody final AverageMtowFactorViewModel averageMtowFactor)
        throws URISyntaxException {
        LOG.debug("REST request to save average mtow factor : {}", averageMtowFactor);

        if (averageMtowFactor.getAverageMtowFactor() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        AverageMtowFactor result = averageMtowFactorService.save(averageMtowFactorMapper.toModel(averageMtowFactor));
        AverageMtowFactorViewModel viewModel = averageMtowFactorMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/average-mtow-factors/" + result.getAverageMtowFactor())).body(viewModel);
    }

    @PreAuthorize("hasAuthority('avg_mtow_factor_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<AverageMtowFactorViewModel> updateAverageMtowFactor(@RequestBody final AverageMtowFactorViewModel averageMtowFactor,
                                                                              @PathVariable final Integer id) {
        LOG.debug("REST request to update average mtow factor : {}", averageMtowFactor);

        AverageMtowFactor result = averageMtowFactorService.update(id, averageMtowFactorMapper.toModel(averageMtowFactor));
        AverageMtowFactorViewModel viewModel = averageMtowFactorMapper.toViewModel(result);

        return ResponseEntity.ok().body(viewModel);
    }

    @PreAuthorize("hasAuthority('avg_mtow_factor_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteAverageMtowFactor(@PathVariable final Integer id) {
        LOG.debug("REST request to delete average mtow factor : {}", id);

        averageMtowFactorService.delete(id);
        return ResponseEntity.ok().build();
    }
}
