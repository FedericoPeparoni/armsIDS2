package ca.ids.abms.modules.revenueprojection;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ca.ids.abms.modules.reports2.InvoiceTemplateCategory;
import ca.ids.abms.modules.reports2.common.ReportControllerBase;
import ca.ids.abms.modules.reports2.common.ReportDocumentCreator;
import ca.ids.abms.modules.reports2.common.ReportFormat;

@RestController
@RequestMapping("/api/stats/revenue-projection")
public class RevenueProjectionController extends ReportControllerBase {

    private final RevenueProjectionService revenueProjectionService;
    private final ReportDocumentCreator reportDocumentCreator;

    public RevenueProjectionController(final RevenueProjectionService revenueProjectionService, final ReportDocumentCreator reportDocumentCreator) {
        this.revenueProjectionService = revenueProjectionService;
        this.reportDocumentCreator = reportDocumentCreator;
    }

    @Transactional
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createRevenueProjection(@Validated @RequestBody RevenueProjection revenueProjection) {
        RevenueProjectionReport data = revenueProjectionService.createRevenueProjection(revenueProjection);
        ReportFormat format;

        switch(revenueProjection.getFormat()) {
            case "PDF":
                format = ReportFormat.pdf;
                break;
            case "XLSX":
                format = ReportFormat.xlsx;
                break;
            default:
                format = ReportFormat.pdf;
                break;
        }

        return doCreateBinaryResponse(
            false,
            isPreview -> {
                return reportDocumentCreator.createXmlBasedInvoiceDocument("REVENUE_PROJECTION", data, format, InvoiceTemplateCategory.REVENUE_PROJECTION);
            }
        );
   }

}
