package ca.ids.abms.modules.reports2.invoices.iata;

import ca.ids.abms.modules.reports2.invoices.aviation.BillingInterval;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import com.codahale.metrics.annotation.Timed;

import ca.ids.abms.modules.reports2.common.ReportControllerBase;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.util.MiscUtils;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = {"api/reports2/iata-invoice", "api/reports/iata-invoice"})
public class IataInvoiceController extends ReportControllerBase {

    private final IataInvoiceService iataInvoiceService;
    private final UserEventLogService userEventLogService;

    public IataInvoiceController(final IataInvoiceService iataInvoiceService,
                                 final UserEventLogService userEventLogService) {
        this.iataInvoiceService = iataInvoiceService;
        this.userEventLogService = userEventLogService;
    }

    /**
     * Generate IATA aviation invoice for the given billing period.
     * <p>
     * <H2>Usage</h2>
     * <p>
     * <code><b>
     * <pre>{GET,POST} /api/reports/iata-invoice?param=value...</pre>
     * </b></code>
     *
     * <b>WARNING</b>: the GET method is deprecated, please use POST
     *
     * @param billingInterval           - billing period monthly/weekly; optional
     * @param startDate                 - start date for billing period; optional
     * @param endDateInclusive          - end date (inclusive) for billing period; optional
     * @param format                    - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to xlsx.
     * @param preview                   - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                                  <ul>
     *                                      <li>the report will contain a "fake" invoice number (XXXXXX)
     *                                      <li>all database changes will be rolled back at the end
     *                                      <li>output will include records only from accounts that match accountIdList (see below)
     *                                  </ul>
     * @param iataInvoicePayload        - list of account IDs to be included in the report. This parameter is used <b>only</b> when
     *                                    <code>preview</code> is set to 1, and will be ignored otherwise, i.e., in the normal (non-preview)
     *                                    mode the report will include all relevant records.
     * @param userBillingCenterOnly     - flights by user's billing centre or all flights (true/false)? optional
     * @param sort                      - sort order for line items; must be "account,dateTime" or "dateTime,account"
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     * (i.e., the suggested file name) headers set as necessary.
     */
    @Timed
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional
    public ResponseEntity<?> createIataInvoice(
        @RequestParam(required = false) BillingInterval billingInterval,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
        @RequestParam(required = false) final ReportFormat format,
        @RequestParam(required = false) final Boolean preview,
        @RequestParam(required = false, defaultValue = "false") final boolean userBillingCenterOnly,
        @RequestParam(required = false, defaultValue = "account,dateTime") final String sort,
        @RequestBody(required = false) final IataInvoicePayload iataInvoicePayload,
        HttpServletRequest request
    ) {
        final IataInvoiceItemOrder order = MiscUtils.nvl(IataInvoiceItemOrder.tryParse(sort), IataInvoiceItemOrder.ACCOUNT_DATETIME);
        final String ipAddress = userEventLogService.getIpAddressFromRequest(request);
        final List<Integer> accountIdList = iataInvoicePayload != null
            ? iataInvoicePayload.getAccountIdList() : null;

        return doCreateBinaryResponse(preview, isPreview -> iataInvoiceService
            .createInvoiceForCurrentUser(billingInterval, startDate, endDateInclusive, format, accountIdList,
                isPreview, order, userBillingCenterOnly, ipAddress));
    }
}
