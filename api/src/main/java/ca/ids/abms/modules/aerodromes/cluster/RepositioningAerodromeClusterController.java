package ca.ids.abms.modules.aerodromes.cluster;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/repositioning-aerodrome-clusters")
@SuppressWarnings({"unused", "squid:S1452"})
public class RepositioningAerodromeClusterController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(RepositioningAerodromeClusterController.class);
    private final RepositioningAerodromeClusterService repositioningAerodromeClusterService;
    private final RepositioningAerodromeClusterMapper repositioningAerodromeClusterMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public RepositioningAerodromeClusterController(final RepositioningAerodromeClusterService aRepositioningAerodromeClusterService,
                                                   final RepositioningAerodromeClusterMapper aRepositioningAerodromeClusterMapper,
                                                   final ReportDocumentCreator reportDocumentCreator) {
        repositioningAerodromeClusterService = aRepositioningAerodromeClusterService;
        repositioningAerodromeClusterMapper = aRepositioningAerodromeClusterMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @PostMapping
    public ResponseEntity<RepositioningAerodromeClusterViewModel> createRepositioningAerodromeCluster(
            @Valid @RequestBody final RepositioningAerodromeClusterViewModel repositioningAerodromeClusterViewModel)
            throws URISyntaxException {
        LOG.debug("REST request to save RepositioningAerodromeCluster : {}", repositioningAerodromeClusterViewModel);

        if (repositioningAerodromeClusterViewModel.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<RepositioningAssignedAerodromeCluster> assignedClusters = getAssignedCluster(
                repositioningAerodromeClusterViewModel);
        RepositioningAerodromeCluster repositioningAerodromeCluster = repositioningAerodromeClusterMapper
                .toModel(repositioningAerodromeClusterViewModel);
        RepositioningAerodromeCluster result = repositioningAerodromeClusterService.save(repositioningAerodromeCluster,
                assignedClusters);

        return ResponseEntity.created(new URI("/api/repositioning-aerodrome-clusters/" + result.getId()))
                .body(repositioningAerodromeClusterMapper.toViewModel(result));
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteRepositioningAerodromeCluster(@PathVariable final Integer id) {
        LOG.debug("REST request to delete RepositioningAerodromeCluster : {}", id);
        repositioningAerodromeClusterService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllRepositioningAerodromeClusters(@RequestParam(name = "searchFilter", required = false) final String textSearch,
                                                                  @SortDefault(sort = {"repositioningAerodromeClusterName"},
                                                                      direction = Sort.Direction.ASC) final Pageable pageable,
                                                                  @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all RepositioningAerodromeClusters");
        final Page<RepositioningAerodromeCluster> page = repositioningAerodromeClusterService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<RepositioningAerodromeCluster> list = page.getContent();
            final List<RepositioningAerodromeClusterCsvExportModel> csvExportModel = repositioningAerodromeClusterMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Repositioning_Aerodrome_Clusters", csvExportModel,
                RepositioningAerodromeClusterCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<RepositioningAerodromeClusterViewModel> resultPage = new PageImplCustom<>(
                repositioningAerodromeClusterMapper.toViewModel(page), pageable, page.getTotalElements(), repositioningAerodromeClusterService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<RepositioningAerodromeClusterViewModel> getRepositioningAerodromeCluster(@PathVariable final Integer id) {
        LOG.debug("REST request to get RepositioningAerodromeCluster : {}", id);

        RepositioningAerodromeCluster repositioningAerodromeCluster = repositioningAerodromeClusterService.getOne(id);

        return Optional.ofNullable(repositioningAerodromeCluster).map(
                result -> new ResponseEntity<>(repositioningAerodromeClusterMapper.toViewModel(result), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('aerodrome_category_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<RepositioningAerodromeClusterViewModel> updateRepositioningAerodromeCluster(
            @RequestBody final RepositioningAerodromeClusterViewModel repositioningAerodromeClusterViewModel,
            @PathVariable final Integer id) {
        LOG.debug("REST request to update RepositioningAerodromeCluster : {}", repositioningAerodromeClusterViewModel);

        List<RepositioningAssignedAerodromeCluster> assignedClusters = getAssignedCluster(
                repositioningAerodromeClusterViewModel);
        RepositioningAerodromeCluster repositioningAerodromeCluster = repositioningAerodromeClusterMapper
                .toModel(repositioningAerodromeClusterViewModel);
        repositioningAerodromeClusterService.update(id, repositioningAerodromeCluster, assignedClusters);

        RepositioningAerodromeCluster result = repositioningAerodromeClusterService.getOne(id);

        return ResponseEntity.ok().body(repositioningAerodromeClusterMapper.toViewModel(result));
    }

    private List<RepositioningAssignedAerodromeCluster> getAssignedCluster(
            RepositioningAerodromeClusterViewModel aRepositioningAerodromeCluster) {
        List<RepositioningAssignedAerodromeCluster> clusters = new ArrayList<>();
        Collection<String> identifiers = aRepositioningAerodromeCluster.getAerodromeIdentifiers();
        if (identifiers != null) {
            for (String identifier : identifiers) {
                RepositioningAssignedAerodromeCluster c = new RepositioningAssignedAerodromeCluster();
                c.setAerodromeIdentifier(identifier);
                clusters.add(c);
            }
        }
        return clusters;
    }
}
