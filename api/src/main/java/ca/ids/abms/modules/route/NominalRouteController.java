package ca.ids.abms.modules.route;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import ca.ids.abms.modules.util.models.PageImplCustom;

@RestController
@RequestMapping("/api/nominal-routes")
@SuppressWarnings({"unused", "squid:S1452"})
public class NominalRouteController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(NominalRouteController.class);

    private final NominalRouteService nominalRouteService;
    private final NominalRouteMapper nominalRouteMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public NominalRouteController(final NominalRouteService nominalRouteService,
                                  final NominalRouteMapper nominalRouteMapper,
                                  final ReportDocumentCreator reportDocumentCreator) {
        this.nominalRouteService = nominalRouteService;
        this.nominalRouteMapper = nominalRouteMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('nominal_route_modify')")
    @PostMapping
    public ResponseEntity<NominalRouteViewModel> create(@Valid @RequestBody final NominalRouteViewModel nominalRouteDto) throws URISyntaxException {
        LOG.debug("REST request to create a new NominalRoute");
        if (nominalRouteDto.getId() != null) {
            return ResponseEntity.badRequest().body(null);
        }
        final NominalRoute nominalRoute = nominalRouteMapper.toModel(nominalRouteDto);
        final NominalRoute result = nominalRouteService.create(nominalRoute, true);
        final NominalRouteViewModel resultDto = nominalRouteMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/nominal-routes/" + result.getId())).body(resultDto);
    }

    @PreAuthorize("hasAuthority('nominal_route_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to remove the NominalRoute with id {}", id);
        nominalRouteService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                     @SortDefaults({@SortDefault(sort = { "pointa" }, direction = Sort.Direction.ASC),
                                         @SortDefault(sort = { "pointb" }, direction = Sort.Direction.ASC)})
                                     final Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all NominalRoutes");
        final Page<NominalRoute> page = nominalRouteService.findAll(pageable, searchFilter);
        long countAll = nominalRouteService.countAllNominalRoutes();

        if (csvExport != null && csvExport) {
            final List<NominalRoute> list = page.getContent();
            final List<NominalRouteCsvExportModel> csvExportModel = nominalRouteMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Nominal_Routes", csvExportModel, NominalRouteCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<NominalRouteViewModel> resultPage = new PageImplCustom<>(nominalRouteMapper.toViewModel(page), pageable,
                page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NominalRouteViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get the NominalRoute with id {}", id);
        final NominalRoute nominalRoute = nominalRouteService.getOne(id);
        final NominalRouteViewModel nominalRouteDto = nominalRouteMapper.toViewModel(nominalRoute);
        return Optional.ofNullable(nominalRouteDto).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('nominal_route_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<NominalRouteViewModel> update(@PathVariable final Integer id,
                                                        @RequestBody final NominalRouteViewModel nominalRouteDto) {
        LOG.debug("REST request to update a NominalRoute with id {}", id);
        final NominalRoute nominalRoute = nominalRouteMapper.toModel(nominalRouteDto);
        NominalRoute result = nominalRouteService.update(id, nominalRoute, true);
        final NominalRouteViewModel resultDto = nominalRouteMapper.toViewModel(result);
        return ResponseEntity.ok().body(resultDto);
    }
}
