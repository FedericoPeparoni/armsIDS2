package ca.ids.abms.modules.invoices;

import ca.ids.abms.modules.common.controllers.MediaDocumentComponent;
import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportDocument;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoice-templates")
@SuppressWarnings({"unused", "squid:S1452"})
public class InvoiceTemplateController extends MediaDocumentComponent {

    private static final Logger LOG = LoggerFactory.getLogger(InvoiceTemplateController.class);

    private InvoiceTemplateService invoiceTemplateService;
    private InvoiceTemplateMapper invoiceTemplateMapper;
    private final ReportDocumentCreator reportDocumentCreator;

    public InvoiceTemplateController(final InvoiceTemplateService invoiceTemplateService,
                                     final InvoiceTemplateMapper invoiceTemplateMapper,
                                     final ReportDocumentCreator reportDocumentCreator) {
        this.invoiceTemplateService = invoiceTemplateService;
        this.invoiceTemplateMapper = invoiceTemplateMapper;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @GetMapping
    public ResponseEntity<?> findAll(@SortDefault(sort = {"invoiceTemplateName"}, direction = Sort.Direction.ASC) Pageable pageable,
                                     @RequestParam(name = "searchFilter", required = false) final String searchFilter,
                                     @RequestParam(name = "csvExport", required = false) Boolean csvExport) throws Exception {
        LOG.debug("REST request to get all InvoiceTemplates");

        if (csvExport != null && csvExport) {
            return findAllCSV(searchFilter, pageable);
        } else {
            Page<InvoiceTemplateViewModel> page = invoiceTemplateService.findAll(pageable, searchFilter);
            return ResponseEntity.ok().body(page);
        }
    }


    @GetMapping(value = "/{invoiceTemplateCategory}")
    public ResponseEntity<InvoiceTemplateViewModel> getOne(@PathVariable InvoiceTemplateCategory invoiceTemplateCategory) {
        LOG.debug("REST request to get the InvoiceTemplate with category {}", invoiceTemplateCategory);

        final InvoiceTemplateViewModel invoiceTemplateDto = invoiceTemplateService.getOne(invoiceTemplateCategory);

        return Optional.ofNullable(invoiceTemplateDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PreAuthorize("hasAuthority('invoice_template_modify')")
    @GetMapping(value = "/{invoiceTemplateCategory}/reset")
    public ResponseEntity<InvoiceTemplateViewModel> resetReportTemplateToDefault(@PathVariable InvoiceTemplateCategory invoiceTemplateCategory) {
        LOG.debug("REST request to get the InvoiceTemplate with category {}", invoiceTemplateCategory);

        final InvoiceTemplateViewModel invoiceTemplateDto = invoiceTemplateService.resetReportTemplateToDefault(invoiceTemplateCategory);

        return Optional.ofNullable(invoiceTemplateDto)
                .map(result -> new ResponseEntity<>(
                        result,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping(value = "/{invoiceTemplateCategory}/download")
    public void download (@PathVariable final InvoiceTemplateCategory invoiceTemplateCategory, HttpServletResponse response) throws IOException {
    	invoiceTemplateService.downloadInvoiceTemplate(invoiceTemplateCategory, response);
    }

    @GetMapping(value = "/{invoiceTemplateCategory}/downloadTemplateExampleXml")
    public void downloadTemplateExampleXml (@PathVariable final InvoiceTemplateCategory invoiceTemplateCategory, HttpServletResponse response) throws IOException {
    	invoiceTemplateService.downloadInvoiceTemplateExampleXml(invoiceTemplateCategory, response);
    }

    @PreAuthorize("hasAuthority('invoice_template_modify')")
    @PostMapping(value = "/{invoiceTemplateCategory}/upload",
        headers = ("content-type=multipart/*"),
        consumes = "multipart/form-data")
    public ResponseEntity<InvoiceTemplateViewModel> update (
        @PathVariable final InvoiceTemplateCategory invoiceTemplateCategory,
        @RequestParam("invoice_template_name") final String name,
        @RequestParam(value="file", required = false) final MultipartFile file)
        throws URISyntaxException, IOException {
        LOG.debug("REST request to update the InvoiceTemplate with category {}", invoiceTemplateCategory);
        ResponseEntity<InvoiceTemplateViewModel> response;
        InvoiceTemplate result;
        try {
            result = invoiceTemplateService.uploadAndCreateOrUpdate(invoiceTemplateCategory, file, name);
        } catch (IOException ioe) {
            LOG.error("Cannot upload the InvoiceTemplate with category {}", invoiceTemplateCategory);
            throw ioe;
        }
        response = ResponseEntity.created(new URI("/api/invoice-templates/" + result.getId()))
            .body(invoiceTemplateMapper.toViewModel(result));
        return response;
    }


;
    //CSV Export
    private ResponseEntity<?> findAllCSV(final String searchFilter,
                                                             Pageable pageable) throws Exception {
        LOG.debug("REST request to get all InvoiceTemplates CSV");

        int pageSize = 10000;
        int currentPage = 0;

        ReportDocument doc = null;
        //TODO: use For
        while(true) {
            pageable = new PageRequest(currentPage, pageSize);
            final Page<InvoiceTemplateViewModel> pageInvoice = invoiceTemplateService.findAllForDownload(pageable, searchFilter);
            if(!pageInvoice.hasContent()) {
                break;
            }

            //Convert to List
            final List<InvoiceTemplateViewModel> InvoiceList = new ArrayList();
            InvoiceList.addAll(pageInvoice.getContent());
            LOG.debug("grandezza lista " + InvoiceList.size());
            LOG.debug("Page: " + (currentPage));
            final List<InvoiceTemplateCsvExportModel> csvExportModel = invoiceTemplateMapper.toCsvModel(InvoiceList);

            currentPage = currentPage + 1;
            if(doc == null) {
                doc =  reportDocumentCreator.createCsvDocument("Invoice_Templates", csvExportModel,
                    InvoiceTemplateCsvExportModel.class, true);
            }else {
                reportDocumentCreator.appendToCsvDocument(doc, csvExportModel,InvoiceTemplateCsvExportModel.class, true, false);
            }
        }
        //Flush
        doc.getOutputStream().flush();
        doc.getOutputStream().close();
        return doCreateResource(doc);
        //return doCreateStreamingResponse(doc);

    }
}
