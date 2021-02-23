package ca.ids.abms.modules.formulas.ldp;

import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import ca.ids.abms.spreadsheets.SSService;
import ca.ids.abms.spreadsheets.dto.WorkbookDto;
import org.apache.tomcat.util.http.fileupload.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/air-navigation-charges-schedules")
@SuppressWarnings({"unused", "squid:S1452"})
public class LdpBillingFormulaController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(LdpBillingFormulaController.class);

    private final LdpBillingFormulaService ldpBillingFormulaService;
    private final LdpBillingFormulaMapper ldpBillingFormulaMapper;
    private final SSService ssService;
    private final ReportDocumentCreator reportDocumentCreator;

    public LdpBillingFormulaController(final LdpBillingFormulaService ldpBillingFormulaService,
                                       final LdpBillingFormulaMapper ldpBillingFormulaMapper,
                                       final SSService ssService,
                                       final ReportDocumentCreator reportDocumentCreator) {
        this.ldpBillingFormulaService = ldpBillingFormulaService;
        this.ldpBillingFormulaMapper = ldpBillingFormulaMapper;
        this.ssService = ssService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> getAllCharges(@SortDefaults({@SortDefault(sort = { "id.chargesType" }, direction = Sort.Direction.ASC),
                                            @SortDefault(sort = { "aerodromeCategory" }, direction = Sort.Direction.ASC)}) Pageable pageable,
                                           @RequestParam(name = "textFilter", required = false) final String textSearch,
                                           @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get All LdpBillingFormulas by filters: textSearch: {}", textSearch);
        final Page<LdpBillingFormula> charges = ldpBillingFormulaService.getAllFormulasInfo(pageable, textSearch);

        if (csvExport != null && csvExport) {
            final List<LdpBillingFormula> list = charges.getContent();
            final List<LdpBillingFormulaCsvExportModel> csvExportModel = ldpBillingFormulaMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Air_Navigation_Charges_Schedules", csvExportModel,
                LdpBillingFormulaCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<LdpBillingFormulaViewModel> resultPage = new PageImplCustom<>(
                ldpBillingFormulaMapper.toViewModel(charges), pageable, charges.getTotalElements(), ldpBillingFormulaService.countAll());
            return Optional.of(resultPage)
                .map(result -> new ResponseEntity<>(result, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
        }
    }

    @GetMapping(value = "/{id}")
    public Collection<LdpBillingFormulaViewModel> getChargesByAerodromeCategory (@PathVariable final Integer id) {
        final Collection<LdpBillingFormula> formulas = ldpBillingFormulaService.getFormulaInfo(id);
        return ldpBillingFormulaMapper.toViewModel(formulas);
    }

    @PreAuthorize("hasAuthority('charges_schedule_modify')")
    @PutMapping(value = "/{id}/{chargeType}/upload",
            headers = ("content-type=multipart/*"),
            consumes = "multipart/form-data")
    public ResponseEntity<LdpBillingFormulaViewModel> uploadSpreadsheet (@PathVariable final Integer id,
                                                                         @PathVariable final String chargeType,
                                                                         @RequestParam final MultipartFile file)
        throws URISyntaxException, IOException {

        // validate charge type for uploading
        if (!ChargeTypes.isAllowed(chargeType)) {
            LOG.error("Spreadsheet validation failed, '{}' is not a valid charge type.", chargeType);
            throw ExceptionFactory.getInvalidDataException(
                String.format("'%s' is not a valid charge type", chargeType), "chargeType");
        }

        // validate and upload multipart file
        LdpBillingFormula formula;
        try {
            formula = ldpBillingFormulaService.uploadFormula(id, chargeType, file);
        } catch (IOException ex) {
            LOG.error("Cannot upload the spreadsheet for aerodrome category ID '{}' and charge type '{}' because {}",
                id, chargeType, ex);
            throw ex;
        }

        // return craeted endpoint with http status of Created (Code 201)
        return ResponseEntity.created(new URI("/api/air-navigation-charges-schedules/" + formula.getId()
            .getAerodromeCategoryId() + '/' + formula.getId().getChargesType()))
            .body(ldpBillingFormulaMapper.toViewModel(formula));
    }

    @GetMapping(value = "/{id}/{chargeType}/download")
    public void downloadSpreadsheet (@PathVariable final Integer id,
                                     @PathVariable final String chargeType, HttpServletResponse response) throws IOException {
        if (ChargeTypes.isAllowed(chargeType)) {
            final LdpBillingFormula formula = ldpBillingFormulaService.downloadFormula(id, chargeType);
            try (ByteArrayInputStream is = new ByteArrayInputStream(formula.getChargesSpreadsheet())) {
                response.addHeader("Content-disposition", "attachment;filename=" + formula.getSpreadsheetFileName());
                response.setContentType(formula.getSpreadsheetContentType());
                IOUtils.copy(is, response.getOutputStream());
                response.flushBuffer();
            } catch (IOException ioe) {
                LOG.error("Cannot download the spreadsheet for aerodrome category ID {} and charge type {}",
                    id, chargeType);
                throw ioe;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('charges_schedule_modify')")
    @DeleteMapping(value = "/{id}/{chargeType}")
    public ResponseEntity<Void> removeSpreadsheet (@PathVariable final Integer id,
                                                   @PathVariable final String chargeType) {
        LOG.debug("REST request to delete charge with id : ({},{})", id, chargeType);
        ldpBillingFormulaService.deleteFormula(id, chargeType);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{id}/{chargeType}/preview")
    public ResponseEntity<WorkbookDto> getPreview (@PathVariable final Integer id,
                                                   @PathVariable final String chargeType)
        throws IOException {
        WorkbookDto result;
        if (ChargeTypes.isAllowed(chargeType)) {
            final LdpBillingFormula formula = ldpBillingFormulaService.downloadFormula(id, chargeType);
            try (final ByteArrayInputStream is = new ByteArrayInputStream(formula.getChargesSpreadsheet())) {
                result = ChargeTypes.getModelSpreadsheet(chargeType, ssService, is);
            } catch (IOException ioe) {
                LOG.error("Cannot download the spreadsheet for aerodrome category ID {} and charge type {}",
                    id, chargeType);
                throw ioe;
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok().body(result);
    }
}
