package ca.ids.abms.modules.aircraft;

import java.net.URI;
import java.time.LocalDateTime;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
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
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.countries.Country;
import ca.ids.abms.modules.countries.CountryService;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.util.models.PageImplCustom;

@RestController
@RequestMapping("/api/aircraft-registrations")
@SuppressWarnings({"unused", "squid:S1452"})
public class AircraftRegistrationController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(AircraftRegistrationController.class);

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String AIRCRAFT_REGISTRATION_LOADER = "aircraftRegistrationLoader";

    private final AircraftRegistrationService aircraftRegistrationService;
    private final AircraftRegistrationMapper aircraftRegistrationMapper;
    private final CountryService countryService;
    private final FlightMovementService flightMovementService;
    private final ReportDocumentCreator reportDocumentCreator;

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<AircraftRegistrationCsvViewModel> dataImportService;

    @Qualifier(AIRCRAFT_REGISTRATION_LOADER)
    private AircraftRegistrationLoader loaderService;

    public AircraftRegistrationController(final AircraftRegistrationService aircraftRegistrationService,
                                          final AircraftRegistrationMapper aircraftRegistrationMapper,
                                          final CountryService countryService,
                                          final FlightMovementService flightMovementService,
                                          final ReportDocumentCreator reportDocumentCreator,
                                          final AircraftRegistrationLoader loaderService) {
        this.aircraftRegistrationService = aircraftRegistrationService;
        this.aircraftRegistrationMapper = aircraftRegistrationMapper;
        this.countryService = countryService;
        this.reportDocumentCreator = reportDocumentCreator;
        this.loaderService = loaderService;
        this.flightMovementService = flightMovementService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('aircraft_registration_modify')")
    public ResponseEntity<AircraftRegistrationViewModel> createAircraftRegistration(@Valid @RequestBody final AircraftRegistrationViewModel aircraftRegistration)
        throws URISyntaxException {
        LOG.debug("REST request to save AircraftRegistration : {}", aircraftRegistration);

        if (aircraftRegistration.getRegistrationNumber() == null) {
            return ResponseEntity.badRequest().body(null);
        }

        AircraftRegistration createdAircraftRegistration = aircraftRegistrationService.save(aircraftRegistrationMapper.toModel(aircraftRegistration));

        if (createdAircraftRegistration == null) {
            return ResponseEntity.badRequest().body(null);
        }

        AircraftRegistrationViewModel result = aircraftRegistrationMapper.toViewModel(createdAircraftRegistration);

        return ResponseEntity.created(new URI("/api/aircraft-registrations/" + result.getRegistrationNumber())).body(result);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('aircraft_registration_modify')")
    public ResponseEntity<Void> deleteAircraftRegistration(@PathVariable Integer id) {
        LOG.debug("REST request to delete AircraftRegistration : {}", id);

        aircraftRegistrationService.delete(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<AircraftRegistration> getAircraftRegistration(@PathVariable Integer id) {
        LOG.debug("REST request to get AircraftRegistration : {}", id);

        AircraftRegistration aircraftRegistration = aircraftRegistrationService.getOne(id);

        return Optional.ofNullable(aircraftRegistration).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<?> getAllAircraftRegistrations(@RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                                                  @SortDefaults({
                                                                      @SortDefault(sort = { "registrationNumber" }, direction = Sort.Direction.ASC),
                                                                      @SortDefault(sort = { "registrationStartDate" }, direction = Sort.Direction.ASC) })
                                                                      Pageable pageable,
                                                         @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all AircraftRegistrations");
        final Page<AircraftRegistration> page = aircraftRegistrationService.findAll(pageable, searchFilter);
        long countAll = aircraftRegistrationService.countAllAircraftRegistrations();

        if (csvExport != null && csvExport) {
            final List<AircraftRegistration> list = page.getContent();
            final List<AircraftRegistrationCsvExportModel> csvExportModel = aircraftRegistrationMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Aircraft_Registrations", csvExportModel, AircraftRegistrationCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<AircraftRegistrationViewModel> resultPage = new PageImplCustom<>(aircraftRegistrationMapper.toViewModel(page),
                pageable, page.getTotalElements(), countAll);
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/prefix/{prefix}")
    public ResponseEntity<Country> getCountryByPrefix(@PathVariable String prefix) {
        LOG.debug("REST request to get Country by prefix: {}", prefix);

        Country country = countryService.findCountryByPrefix(prefix);

        return Optional.ofNullable(country).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('aircraft_registration_modify')")
    public ResponseEntity<AircraftRegistrationViewModel> updateAircraftRegistration(@Valid @RequestBody final AircraftRegistrationViewModel aircraftRegistration,
                                                                                    @PathVariable final Integer id) {
        LOG.debug("REST request to update AircraftRegistration : {}", aircraftRegistration);
        LOG.debug("Code : {}", id);

        AircraftRegistration updatedAircraftRegistration = aircraftRegistrationService.update(id, aircraftRegistrationMapper.toModel(aircraftRegistration));
        AircraftRegistrationViewModel result = aircraftRegistrationMapper.toViewModel(updatedAircraftRegistration);

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('flight_schedule_modify')")
    @PutMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<UploadReportParsedItems> upload(@RequestParam("file") final MultipartFile file) {

        LOG.debug("REST request to upload AircraftRegistration file");
        if (file.getContentType() == null) {
            return ResponseEntity.badRequest().body(null);
        }
        List<AircraftRegistrationCsvViewModel> scheduled;
        try {
            scheduled = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                    AircraftRegistrationCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage(), ex);
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        loaderService.setIsSelfCare(false);
        //noinspection unchecked
        final List<UploadReportViewModel> summary = loaderService.reportBulkUpload(scheduled, file);

        return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(scheduled, summary));
    }

    @GetMapping(value = "/aircraft-type/{regNumber}/{dateOfFlight}")
    public ResponseEntity<AircraftType> getAircraftTypeByIdentifier(
        @PathVariable String regNumber, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime dateOfFlight
    ) {
        LOG.debug("REST request to get aircraft registration by Registration Number: {} and date of flight: {}", regNumber, dateOfFlight);

        // attempt to get aircraft registration number by aircraft registration or fallback to latest flight movement
        AircraftRegistration aircraftRegistration = aircraftRegistrationService
            .findAircraftRegistrationByRegistrationNumber(regNumber, dateOfFlight);
        AircraftType aircraftType = aircraftRegistration != null ? aircraftRegistration.getAircraftType()
            : flightMovementService.getAircraftTypeByLatestRegistrationNumber(regNumber);

        return Optional.ofNullable(aircraftType).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}

