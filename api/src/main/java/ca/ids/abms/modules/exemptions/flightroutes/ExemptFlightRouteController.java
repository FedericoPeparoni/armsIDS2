package ca.ids.abms.modules.exemptions.flightroutes;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
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
@RequestMapping("/api/exempt-flight-routes")
@SuppressWarnings({"unused", "squid:S1452"})
public class ExemptFlightRouteController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ExemptFlightRouteController.class);

    private final ExemptFlightRouteService exemptFlightRouteService;
    private final ExemptFlightRouteMapper exemptFlightRoutetMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public ExemptFlightRouteController(final ExemptFlightRouteService exemptFlightRouteService,
                                       final ExemptFlightRouteMapper exemptFlightRoutetMapper,
                                       final ReportDocumentCreator reportDocumentCreator) {
        this.exemptFlightRouteService = exemptFlightRouteService;
        this.exemptFlightRoutetMapper = exemptFlightRoutetMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll (@RequestParam(name = "searchFilter", required = false) final String textSearch,
                                      @SortDefault.SortDefaults({
                                          @SortDefault(sort = {"departureAerodrome"}, direction = Sort.Direction.ASC),
                                          @SortDefault(sort = {"destinationAerodrome"}, direction = Sort.Direction.ASC)
                                      }) final Pageable pageable,
                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all exempt flight routes");
        final Page<ExemptFlightRoute> page = exemptFlightRouteService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<ExemptFlightRoute> list = page.getContent();
            final List<ExemptFlightRouteCsvExportModel> csvExportModel = exemptFlightRoutetMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Exempt_Flight_Routes", csvExportModel,
                ExemptFlightRouteCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<ExemptFlightRouteViewModel> resultPage = new PageImplCustom<>(
                exemptFlightRoutetMapper.toViewModel(page), pageable, page.getTotalElements(), exemptFlightRouteService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ExemptFlightRouteViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get an exempt flight route with id: {}", id);
        final ExemptFlightRoute item = exemptFlightRouteService.getOne(id);
        final ExemptFlightRouteViewModel dto = exemptFlightRoutetMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('exempt_flight_route_modify')")
    @PostMapping
    public ResponseEntity<ExemptFlightRouteViewModel> create(@Valid @RequestBody final ExemptFlightRouteViewModel dto) throws URISyntaxException {
        LOG.debug("REST request to create an exempt flight route: {}", dto);
        if (dto.getId() != null) {
            throw new CustomParametrizedException(ErrorConstants.ERR_ID_MAY_BE_NULL, "id");
        }
        final ExemptFlightRoute itemToCreate = exemptFlightRoutetMapper.toModel(dto);
        final ExemptFlightRoute createdItem = exemptFlightRouteService.create(itemToCreate);
        final ExemptFlightRouteViewModel resultDto = exemptFlightRoutetMapper.toViewModel(createdItem);
        return ResponseEntity.created(new URI("/api/exempt-flight-routes/" + createdItem.getId()))
            .body(resultDto);
    }

    @PreAuthorize("hasAuthority('exempt_flight_route_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<ExemptFlightRouteViewModel> update(@PathVariable final Integer id,
                                                             @Valid @RequestBody final ExemptFlightRouteViewModel dto){
        LOG.debug("REST request to update an exempt flight route with id: {}", id);
        final ExemptFlightRoute itemToUpdate = exemptFlightRoutetMapper.toModel(dto);
        final ExemptFlightRoute updatedItem = exemptFlightRouteService.update(id, itemToUpdate);
        final ExemptFlightRouteViewModel updatedDto = exemptFlightRoutetMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('exempt_flight_route_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to delete an exempt flight route with id: {}", id);
        exemptFlightRouteService.delete(id);
        return ResponseEntity.ok().build();
    }
}
