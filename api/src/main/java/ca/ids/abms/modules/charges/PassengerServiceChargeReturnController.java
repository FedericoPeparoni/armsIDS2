package ca.ids.abms.modules.charges;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.PartialParametersException;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.common.dto.UploadReportParsedItems;
import ca.ids.abms.modules.common.dto.UploadReportViewModel;
import ca.ids.abms.modules.dataimport.CsvImportServiceImp;
import ca.ids.abms.modules.dataimport.DataImportService;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.apache.poi.util.IOUtils;
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

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/passenger-service-charge-return")
@SuppressWarnings("squid:S1452")
public class PassengerServiceChargeReturnController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(PassengerServiceChargeReturnController.class);
    private static final String IMAGE_RESOURCE = "/{id}/image";

    private final PassengerServiceChargeReturnService passengerServiceChargeReturnService;
    private final PassengerServiceChargeReturnMapper passengerServiceChargeReturnMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    private static final String DATA_IMPORT_SERVICE = "recordableRowCsvImporter";
    private static final String PASSENGER_CHARGE_RETURN_LOADER = "passengerServiceChargeReturnLoader";

    @Autowired
    @Qualifier(DATA_IMPORT_SERVICE)
    private CsvImportServiceImp<PassengerServiceChargeReturnCsvViewModel> dataImportService;

    @Qualifier(PASSENGER_CHARGE_RETURN_LOADER)
    private PassengerServiceChargeReturnLoader loaderService;

    public PassengerServiceChargeReturnController(final PassengerServiceChargeReturnService passengerServiceChargeReturnService,
                                                  final PassengerServiceChargeReturnMapper passengerServiceChargeReturnMapper,
                                                  final ReportDocumentCreator reportDocumentCreator,
                                                  final PassengerServiceChargeReturnLoader loaderService) {
        this.passengerServiceChargeReturnService = passengerServiceChargeReturnService;
        this.passengerServiceChargeReturnMapper = passengerServiceChargeReturnMapper;
        this.reportDocumentCreator = reportDocumentCreator;
        this.loaderService = loaderService;
    }

    @GetMapping
    public ResponseEntity<?> findAll(
        @SortDefault(sort = {"dayOfFlight", "departureTime"}, direction = Sort.Direction.ASC) Pageable pageable,
        @RequestParam(value = OrphanPassengerServiceChargeReturnFilter.QUERY_STRING_ATTRIBUTE, required = false) final String value,
        @RequestParam(value ="search", required = false) String textSearch,
        @RequestParam(name = "csvExport", required = false) Boolean csvExport) {

        LOG.debug("REST request to get all charge returns");

        final Page<PassengerServiceChargeReturn> page = passengerServiceChargeReturnService.findAll(pageable, Boolean.parseBoolean(value), textSearch, null);

        if (csvExport != null && csvExport) {
            final List<PassengerServiceChargeReturn> list = page.getContent();
            final List<PassengerServiceChargeReturnCsvExportModel> csvExportModel = passengerServiceChargeReturnMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Passenger_Service_Charge_Returns", csvExportModel, PassengerServiceChargeReturnCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<PassengerServiceChargeReturnViewModel> resultPage = new PageImplCustom<>(
                passengerServiceChargeReturnMapper.toViewModel(page), pageable, page.getTotalElements(), passengerServiceChargeReturnService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PassengerServiceChargeReturnViewModel> getOne(@PathVariable final Integer id) {
        LOG.debug("REST request to get the charge return with id {}", id);
        final PassengerServiceChargeReturn formula = passengerServiceChargeReturnService.getOne(id);
        final PassengerServiceChargeReturnViewModel formulaDto = passengerServiceChargeReturnMapper.toViewModel(formula);
        return Optional.ofNullable(formulaDto)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "new/upload", headers = ("content-type=multipart/*"))
    public ResponseEntity<PassengerServiceChargeReturn> uploadNew(@RequestParam(required = false, name = "file") final MultipartFile file,
                                                                  @RequestParam(required = false) final Map<String, String> params) {
        LOG.debug("REST request to create a new passenger service charge return");

        PassengerServiceChargeReturn pscr;

        try {
            pscr = passengerServiceChargeReturnService.uploadPscrWithImage(file, params);

        } catch (Exception e) {
            throw new PartialParametersException(e.getLocalizedMessage());
        }

        return ResponseEntity.ok().body(pscr);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value = "{id}/upload", headers = ("content-type=multipart/*"))
    public ResponseEntity<PassengerServiceChargeReturn> update(@RequestParam(required = false, name = "file") final MultipartFile file,
                                                               @RequestParam(required = false) final Map<String, String> params,
                                                               @PathVariable final Integer id) {
        LOG.debug("REST request to update a charge return with id {}", params.get("id"));

        PassengerServiceChargeReturn result;

        try {
            result = passengerServiceChargeReturnService.update(id, params, file);
        } catch (Exception e) {
            throw new PartialParametersException(e.getLocalizedMessage());
        }

        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAuthority('flight_log_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable final Integer id) {
        LOG.debug("REST request to remove the charge return with id {}", id);
        passengerServiceChargeReturnService.delete(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Upload of a CSV file of multiple PSCR
     * @param file file
     * @return BulkLoaderSummary
     */
    @PreAuthorize("hasAuthority('flight_log_modify')")
    @PutMapping(value="/upload", consumes = { "multipart/form-data" })
    @SuppressWarnings(value = "unchecked")
    public ResponseEntity<UploadReportParsedItems> upload (@RequestParam("file") final MultipartFile file) {
        List<PassengerServiceChargeReturnCsvViewModel> mappingResult;
        try {
            mappingResult = dataImportService.parseFromMultipartFile(file, DataImportService.STRATEGY.BIND_BY_POSITION,
                PassengerServiceChargeReturnCsvViewModel.class);
        } catch (Exception ex) {
            LOG.error("Cannot read the file {} because: {}", file.getOriginalFilename(), ex.getMessage());
            throw new CustomParametrizedException("Invalid file", ex, file.getOriginalFilename());
        }

        if (mappingResult.isEmpty()) {
            LOG.error("The data is not valid, file may be empty {}", file.getOriginalFilename());
            throw new CustomParametrizedException("The data are not valid, please check the file and upload again", file.getOriginalFilename());
        }
        final List<UploadReportViewModel> rejectedItemList = loaderService.reportBulkUpload(mappingResult, file);

        LOG.info ("Passenger Service Charge Return upload finished: processed={}, rejected={}", mappingResult.size(), rejectedItemList.size());
        return ResponseEntity.ok().body(UploadReportParsedItems.createInstance(mappingResult, rejectedItemList));
    }

    @GetMapping(value = IMAGE_RESOURCE)
    public void downloadImage (@PathVariable final Integer id, HttpServletResponse response) throws IOException {
        final PassengerServiceChargeReturn pscr = passengerServiceChargeReturnService.getOne(id);
        try (final ByteArrayInputStream is = new ByteArrayInputStream(pscr.getDocumentContents())) {
            response.addHeader("Content-disposition", "attachment;filename=" + pscr.getDocumentFilename());
            response.setContentType(pscr.getDocumentMimeType());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ioe) {
            LOG.error("Cannot download the passenger manifest with ID {}", id);
            throw ioe;
        }
    }

    /**
     * Reconcile passenger service charges with flight movements
     * @param start_date, end_date, itl_pay, dom_pay
     * @return PassengerJobSummary[Object]
     */
    @PreAuthorize("hasAuthority('passenger_revenue_reconcile')")
    @PostMapping(value="/reconciliation")
    public ResponseEntity <PassengerChargeReconciliationJob> reconcile (@RequestParam final String start_date,
                                                                        @RequestParam final String end_date,
                                                                        @RequestParam final Float itl_pay,
                                                                        @RequestParam final Float dom_pay) {
        PassengerChargeReconciliationJob job = new PassengerChargeReconciliationJob();

        job.setCountFM(passengerServiceChargeReturnService.getMovementsWithoutPassengerCharges(start_date, end_date));
        job.setCountPSCR(passengerServiceChargeReturnService.getPassengerChargesWithoutMovements(start_date, end_date));
        job.setTotalDomFees(passengerServiceChargeReturnService.totalDomesticFees(start_date, end_date));
        job.setTotalItlFees(passengerServiceChargeReturnService.totalInternationalFees(start_date, end_date));

        if (job.getTotalDomFees() != null) {
            job.setTotalDomCollected(dom_pay / job.getTotalDomFees() * 100);
        }
        if (job.getTotalItlFees() != null) {
            job.setTotalItlCollected(itl_pay / job.getTotalItlFees() * 100);
        }

        return ResponseEntity.ok().body(job);
    }

}
