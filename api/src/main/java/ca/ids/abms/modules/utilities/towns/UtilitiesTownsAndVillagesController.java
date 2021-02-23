package ca.ids.abms.modules.utilities.towns;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilities-towns-and-villages")
@SuppressWarnings({"unused", "squid:S1452"})
public class UtilitiesTownsAndVillagesController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(UtilitiesTownsAndVillagesController.class);

    private UtilitiesTownsAndVillageService utilitiesTownsAndVillageService;
    private UtilitiesTownsAndVillageMapper utilitiesTownsAndVillageMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public UtilitiesTownsAndVillagesController(final UtilitiesTownsAndVillageService utilitiesTownsAndVillageService,
                                               final UtilitiesTownsAndVillageMapper utilitiesTownsAndVillageMapper,
                                               final ReportDocumentCreator reportDocumentCreator) {
        this.utilitiesTownsAndVillageService = utilitiesTownsAndVillageService;
        this.utilitiesTownsAndVillageMapper = utilitiesTownsAndVillageMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll (@SortDefault(sort = {"townOrVillageName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                      @RequestParam(name = "textFilter", required = false) final String textSearch,
                                      @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all towns and villages");
        final Page<UtilitiesTownsAndVillage> page = utilitiesTownsAndVillageService.findAll(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<UtilitiesTownsAndVillage> list = page.getContent();
            final List<UtilitiesTownsAndVillageCsvExportModel> csvExportModel = utilitiesTownsAndVillageMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Utilities_Towns_And_Villages", csvExportModel,
                UtilitiesTownsAndVillageCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<UtilitiesTownsAndVillageViewModel> resultPage = new PageImplCustom<>(
                utilitiesTownsAndVillageMapper.toViewModel(page), pageable, page.getTotalElements(), utilitiesTownsAndVillageService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }
    @GetMapping(value = "/{id}")
    public ResponseEntity<UtilitiesTownsAndVillageViewModel> getOne(@PathVariable Integer id) {
        final UtilitiesTownsAndVillage item = utilitiesTownsAndVillageService.getOne(id);
        final UtilitiesTownsAndVillageViewModel dto = utilitiesTownsAndVillageMapper.toViewModel(item);
        return Optional.ofNullable(dto)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('utilities_towns_modify')")
    @PostMapping
    public ResponseEntity<UtilitiesTownsAndVillageViewModel> create(
        @RequestBody UtilitiesTownsAndVillageViewModel dto) throws URISyntaxException {
        if (dto.getResidentialElectricityUtilitySchedule() == null || dto.getResidentialElectricityUtilitySchedule().getScheduleId() == null
            || dto.getCommercialElectricityUtilitySchedule() == null || dto.getCommercialElectricityUtilitySchedule().getScheduleId() == null
            || dto.getWaterUtilitySchedule() == null || dto.getWaterUtilitySchedule().getScheduleId() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        final UtilitiesTownsAndVillage itemToCreate = utilitiesTownsAndVillageMapper.toModel(dto);

        try {
            final UtilitiesTownsAndVillage createdItem = utilitiesTownsAndVillageService.create(itemToCreate);
            final UtilitiesTownsAndVillageViewModel resultDto = utilitiesTownsAndVillageMapper.toViewModel(createdItem);
            return ResponseEntity.created(new URI("/api/utilities-towns-and-villages/" + createdItem.getId()))
                .body(resultDto);
        } catch (DataIntegrityViolationException ex) {
            throw new CustomParametrizedException(ErrorConstants.ERR_UNIQUENESS_VIOLATION, "townOrVillageName");
        }
    }

    @PreAuthorize("hasAuthority('utilities_towns_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<UtilitiesTownsAndVillageViewModel> update(@PathVariable Integer id,
                                                                    @RequestBody UtilitiesTownsAndVillageViewModel dto){
        final UtilitiesTownsAndVillage itemToUpdate = utilitiesTownsAndVillageMapper.toModel(dto);
        final UtilitiesTownsAndVillage updatedItem = utilitiesTownsAndVillageService.update(id, itemToUpdate);
        final UtilitiesTownsAndVillageViewModel updatedDto = utilitiesTownsAndVillageMapper.toViewModel(updatedItem);
        return ResponseEntity.ok().body(updatedDto);
    }

    @PreAuthorize("hasAuthority('utilities_towns_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        utilitiesTownsAndVillageService.delete(id);
        return ResponseEntity.ok().build();
    }
}
