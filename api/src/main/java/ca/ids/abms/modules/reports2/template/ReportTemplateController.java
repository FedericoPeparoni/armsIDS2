package ca.ids.abms.modules.reports2.template;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.util.models.PageImplCustom;
import org.apache.poi.util.IOUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/report-templates")
@SuppressWarnings({"unused", "squid:S1452"})
public class ReportTemplateController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(ReportTemplateController.class);

    private ReportTemplateService reportTemplateService;
    private ReportTemplateMapper reportTemplateMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public ReportTemplateController(final ReportTemplateService reportTemplateService,
                                    final ReportTemplateMapper reportTemplateMapper,
                                    final ReportDocumentCreator reportDocumentCreator) {
        this.reportTemplateService = reportTemplateService;
        this.reportTemplateMapper = reportTemplateMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @PreAuthorize("hasAuthority('report_template_modify')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        LOG.debug("REST request to remove the ReportTemplate with id {}", id);
        reportTemplateService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<?> findAll(@SortDefault(sort = {"reportTemplateName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) {
        LOG.debug("REST request to get all ReportTemplates");
        final Page<ReportTemplate> page = reportTemplateService.findAll(pageable);

        if (csvExport != null && csvExport) {
            final List<ReportTemplate> list = page.getContent();
            final List<ReportTemplateCsvExportModel> csvExportModel = reportTemplateMapper.toCsvModel(list);
            ReportDocument report = reportDocumentCreator.createCsvDocument("Report_Templates", csvExportModel,
                ReportTemplateCsvExportModel.class, true);
            return doCreateBinaryResponse(report);
        } else {
            final Page<ReportTemplateViewModel> resultPage = new PageImplCustom<>(
                reportTemplateMapper.toViewModel(page), pageable, page.getTotalElements(), reportTemplateService.countAll());
            return ResponseEntity.ok().body(resultPage);
        }
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ReportTemplateViewModel> getOne(@PathVariable Integer id) {
        LOG.debug("REST request to get the ReportTemplate with id {}", id);
        final ReportTemplate reportTemplate = reportTemplateService.getOne(id);
        final ReportTemplateViewModel reportTemplateDto = reportTemplateMapper.toViewModel(reportTemplate);
        return Optional.ofNullable(reportTemplateDto)
                .map(result -> new ResponseEntity<>(result,HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{id}/download")
    public void download (@PathVariable final Integer id, HttpServletResponse response) throws IOException {
        final ReportTemplate template = reportTemplateService.getOne(id);
        try (ByteArrayInputStream is = new ByteArrayInputStream(template.getTemplateDocument())) {
            response.addHeader("Content-disposition", "attachment;filename=\"" + template.getReportTemplateName() + '\"');
            response.setContentType(template.getMimeType());
            IOUtils.copy(is, response.getOutputStream());
            response.flushBuffer();
        } catch (IOException ioe) {
            LOG.error("Cannot download the template with ID {}", id);
            throw ioe;
        }
    }

    @PreAuthorize("hasAuthority('report_template_modify')")
    @PostMapping(value = "/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<ReportTemplateViewModel> create (@RequestParam("report_template_name") final String name,
                                                           @RequestParam("sql_query") final String sqlQuery,
                                                           @RequestParam("parameters") final String parameters,
                                                           @RequestParam("file") final MultipartFile file) throws URISyntaxException, IOException {
        LOG.debug("REST request to upload the ReportTemplate with file {}", file.getName());
        ResponseEntity<ReportTemplateViewModel> response;
        ReportTemplate result;
        try {
            result = reportTemplateService.uploadAndCreate(file, name, sqlQuery, parameters);
        } catch (IOException ioe) {
            LOG.error("Cannot upload the template {}", file.getName());
            throw ioe;
        }
        response = ResponseEntity.created(new URI("/api/report-templates/" + result.getId()))
            .body(reportTemplateMapper.toViewModel(result));
        return response;
    }

    @PreAuthorize("hasAuthority('report_template_modify')")
    @PutMapping(value = "/{id}/upload", headers = ("content-type=multipart/*"), consumes = "multipart/form-data")
    public ResponseEntity<ReportTemplateViewModel> update (@PathVariable final Integer id,
                                                           @RequestParam("report_template_name") final String name,
                                                           @RequestParam("sql_query") final String sqlQuery,
                                                           @RequestParam("parameters") final String parameters,
                                                           @RequestParam(value="file", required = false) final MultipartFile file) throws URISyntaxException, IOException {
        LOG.debug("REST request to update the ReportTemplate with ID {}", id);
        ResponseEntity<ReportTemplateViewModel> response;
        ReportTemplate result;
        try {
            result = reportTemplateService.uploadAndUpdate(id, file, name, sqlQuery, parameters);
        } catch (IOException ioe) {
            LOG.error("Cannot upload the ReportTemplate with ID {}", id);
            throw ioe;
        }
        response = ResponseEntity.created(new URI("/api/report-templates/" + result.getId()))
            .body(reportTemplateMapper.toViewModel(result));
        return response;
    }
}
