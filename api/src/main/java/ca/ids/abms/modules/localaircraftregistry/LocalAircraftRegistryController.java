package ca.ids.abms.modules.localaircraftregistry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.util.models.PageImplCustom;

@RestController
@RequestMapping("/api/local-aircraft-registries")
@SuppressWarnings({"unused", "squid:S1452"})
public class LocalAircraftRegistryController  extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LocalAircraftRegistryController.class);
    private final LocalAircraftRegistryService localAircraftRegistryService;
    private final LocalAircraftRegistryMapper localAircraftRegistryMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String LOCAL_AIRCRAFT_REGISTRY_LOADER = "localAircraftRegistryLoader";

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<LocalAircraftRegistryCsvViewModel> dataImportService;

    @Qualifier(LOCAL_AIRCRAFT_REGISTRY_LOADER)
    private LocalAircraftRegistryLoader loaderService;

    public LocalAircraftRegistryController(final LocalAircraftRegistryService localAircraftRegistryService,
                                           final LocalAircraftRegistryMapper localAircraftRegistryMapper,
                                           final ReportDocumentCreator reportDocumentCreator,
                                           final LocalAircraftRegistryLoader loaderService) {

        this.localAircraftRegistryService = localAircraftRegistryService;
        this.localAircraftRegistryMapper = localAircraftRegistryMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.loaderService = loaderService;
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<LocalAircraftRegistryViewModel> getLocalAircraftRegistry(@PathVariable final Integer id) {
        LOG.debug("REST request to get local aircraft registry : {}", id);

        LocalAircraftRegistry localAircraftRegistry = localAircraftRegistryService.getOne(id);

        return Optional.ofNullable(localAircraftRegistry)
            .map(result -> new ResponseEntity<>(localAircraftRegistryMapper.toViewModel(result), HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('local_acreg_modify')")
    @PostMapping
    public ResponseEntity<LocalAircraftRegistryViewModel> createLocalAircraftRegistry(@Valid @RequestBody final LocalAircraftRegistryViewModel localAircraftRegistry)
        throws URISyntaxException {
        LOG.debug("REST request to save local aircraft registry : {}", localAircraftRegistry);

        if (localAircraftRegistry.getRegistrationNumber() == null || localAircraftRegistry.getOwnerName() == null ||
            localAircraftRegistry.getAnalysisType() == null || localAircraftRegistry.getMtowWeight() == null ||
            localAircraftRegistry.getCoaDateOfRenewal() == null || localAircraftRegistry.getCoaDateOfExpiry() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        final LocalAircraftRegistry result = localAircraftRegistryService.save(localAircraftRegistryMapper.toModel(localAircraftRegistry));
        final LocalAircraftRegistryViewModel viewModel = localAircraftRegistryMapper.toViewModel(result);

        return ResponseEntity.created(new URI("/api/local-aircraft-registries/" + result.getRegistrationNumber()))
            .body(viewModel);
    }

    @PreAuthorize("hasAuthority('local_acreg_modify')")
    @PutMapping(value = "/{id}")
    public ResponseEntity<LocalAircraftRegistryViewModel> updateLocalAircraftRegistry(@Valid @RequestBody final LocalAircraftRegistryViewModel localAircraftRegistry,
                                                                                      @PathVariable final Integer id) {
        LOG.debug("REST request to update local aircraft registry : {}", localAircraftRegistry);

        LocalAircraftRegistry result = localAircraftRegistryService.update(id, localAircraftRegistryMapper.toModel(localAircraftRegistry));
        final LocalAircraftRegistryViewModel viewModel = localAircraftRegistryMapper.toViewModel(result);

        return ResponseEntity.ok().body(viewModel);
    }

    @PreAuthorize("hasAuthority('local_acreg_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteLocalAircraftRegistry(@PathVariable final Integer id) {
        LOG.debug("REST request to delete local aircraft registry : {}", id);

        localAircraftRegistryService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> getLocalAircraftRegistryByFilter(@RequestParam(name = "search", required = false) final String textSearch,
                                                                                 @RequestParam(required = false) String filter,
                                                                                 @SortDefault(sort = {"registrationNumber"}, direction = Sort.Direction.ASC)
                                                                                         Pageable pageable,
                                                                                 @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all Local Aircraft Registries by registration status filter {}, text filter {}", filter, textSearch);

        final Page<LocalAircraftRegistry> page = localAircraftRegistryService.findAllLocalAircraftRegistriesByFilter(filter, textSearch, pageable);
        long countAll = localAircraftRegistryService.countAllLocalAircraftRegistries();

        if (csvExport != null && csvExport) {
            final List<LocalAircraftRegistry> list = page.getContent();
            final List<LocalAircraftRegistryCsvExportModel> csvExportModel = localAircraftRegistryMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Local_Aircraft_Registry", csvExportModel, LocalAircraftRegistryCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<LocalAircraftRegistryViewModel> resultPage = new PageImplCustom<>(localAircraftRegistryMapper.toViewModel(page), pageable,
                page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    /**
     * Upload of a CSV file.
     */
    @PreAuthorize("hasAuthority('local_acreg_modify')")
    @PutMapping(value="/upload", consumes = { "multipart/form-data" })
    @SuppressWarnings(value = "unchecked")
    public ResponseEntity<UploadReportParsedItems> upload (@RequestParam("file") MultipartFile file) {
        LOG.info ("Local AC registry upload started, file \"{}\"", file.getOriginalFilename());
        List<LocalAircraftRegistryCsvViewModel> mappingResult;

        try {
            mappingResult = dataImportService.parseFromMultipartFile(
                file, DataImportService.STRATEGY.BIND_BY_POSITION, LocalAircraftRegistryCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw CustomParametrizedException.createFileException("Invalid file", ex, file.getOriginalFilename());
        }
        if (mappingResult.isEmpty()) {
            LOG.error("The data is not valid, file may be empty {}", file.getOriginalFilename());
            throw new CustomParametrizedException("The data are not valid, please check the file and upload again", file.getOriginalFilename());
        }

        final List<UploadReportViewModel> rejectedItemList = loaderService.reportBulkUpload(mappingResult, file);

        LOG.info ("Local AC registry upload finished: processed={}, rejected={}", mappingResult.size(), rejectedItemList.size());
        return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(mappingResult, rejectedItemList));
    }
}
