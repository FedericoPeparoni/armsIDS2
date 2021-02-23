package ca.ids.abms.modules.unspecifiedaircraft;

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
@RequestMapping("/api/unspecified-aircraft-types")
@SuppressWarnings({"unused", "squid:S1452"})
public class UnspecifiedAircraftTypeController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UnspecifiedAircraftTypeController.class);

    private UnspecifiedAircraftTypeService unspecifiedAircraftTypeService;
    private UnspecifiedAircraftTypeMapper unspecifiedAircraftTypeMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public UnspecifiedAircraftTypeController(final UnspecifiedAircraftTypeService unspecifiedAircraftTypeService,
                                             final UnspecifiedAircraftTypeMapper unspecifiedAircraftTypeMapper,
                                             final ReportDocumentCreator reportDocumentCreator){
        this.unspecifiedAircraftTypeService=unspecifiedAircraftTypeService;
        this.unspecifiedAircraftTypeMapper=unspecifiedAircraftTypeMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('zzzz_aircraft_type_modify')")
    @PostMapping
    public ResponseEntity<UnspecifiedAircraftTypeViewModel> createUnspecifiedAircraftType(
        @Valid @RequestBody final UnspecifiedAircraftTypeViewModel unspecifiedAircraftTypeViewModel) throws URISyntaxException {
        LOG.debug("REST request to save UnspecifiedAircraftType : {}", unspecifiedAircraftTypeViewModel);

        if (unspecifiedAircraftTypeViewModel.getTextIdentifier() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        unspecifiedAircraftTypeViewModel.setStatus(UnspecifiedAircraftTypeStatus.MANUAL);

        UnspecifiedAircraftType unspecifiedAircraftType=unspecifiedAircraftTypeMapper.toModel(unspecifiedAircraftTypeViewModel);
        UnspecifiedAircraftType unspecifiedAircraftTypeCreate = unspecifiedAircraftTypeService.create(unspecifiedAircraftType);
        UnspecifiedAircraftTypeViewModel result=unspecifiedAircraftTypeMapper.toViewModel(unspecifiedAircraftTypeCreate);

        return ResponseEntity.created(new URI("/api/unspecified-aircraft-types/" + result.getId()))
            .body(result);
    }

    @PreAuthorize("hasAuthority('zzzz_aircraft_type_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UnspecifiedAircraftTypeViewModel> updateUnspecifiedAircraftType(
        @RequestBody final UnspecifiedAircraftTypeViewModel unspecifiedAircraftTypeViewModel,
        @PathVariable final Integer id) {
        LOG.debug("REST request to update UnspecifiedAircraftType : {}", unspecifiedAircraftTypeViewModel);

        unspecifiedAircraftTypeViewModel.setStatus(UnspecifiedAircraftTypeStatus.MANUAL);

        UnspecifiedAircraftType unspecifiedAircraftType=unspecifiedAircraftTypeMapper.toModel(unspecifiedAircraftTypeViewModel);
        UnspecifiedAircraftType unspecifiedAircraftTypeUpdate = unspecifiedAircraftTypeService.update(id, unspecifiedAircraftType);
        UnspecifiedAircraftTypeViewModel result=unspecifiedAircraftTypeMapper.toViewModel(unspecifiedAircraftTypeUpdate);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('zzzz_aircraft_type_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUnspecifiedAircraftType(@PathVariable final Integer id) {
        LOG.debug("REST request to delete UnspecifiedAircraftType : {}", id);
        unspecifiedAircraftTypeService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getAllUnspecifiedAircraftTypes(
        @RequestParam(name = "search", required = false) final String search,
        @SortDefault(sort = {"textIdentifier"}, direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all UnspecifiedAircraftType");
        final Page<UnspecifiedAircraftType> page = unspecifiedAircraftTypeService.findAll(search, pageable);
        long countAll = unspecifiedAircraftTypeService.countAllUnspecifiedAircraftTypes();

        if (csvExport != null && csvExport) {
            final List<UnspecifiedAircraftType> list = page.getContent();
            final List<UnspecifiedAircraftTypeCsvExportModel> csvExportModel = unspecifiedAircraftTypeMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Unspecified_Aircraft_Types", csvExportModel, UnspecifiedAircraftTypeCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UnspecifiedAircraftTypeViewModel> resultPage = new PageImplCustom<>(unspecifiedAircraftTypeMapper
                .toViewModel(page), pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<UnspecifiedAircraftTypeViewModel> getUnspecifiedAircraftType(@PathVariable final Integer id) {
        LOG.debug("REST request to get UnspecifiedAircraftType : {}", id);

        UnspecifiedAircraftType unspecifiedAircraftType = unspecifiedAircraftTypeService.findOne(id);

        return Optional.ofNullable(unspecifiedAircraftType)
            .map(result -> new ResponseEntity<>(unspecifiedAircraftTypeMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));


    }

}
