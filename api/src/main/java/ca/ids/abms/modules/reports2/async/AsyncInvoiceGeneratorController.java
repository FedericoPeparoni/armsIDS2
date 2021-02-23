package ca.ids.abms.modules.reports2.async;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.jobs.JobExecutionStatus;
import ca.ids.abms.modules.jobs.JobType;
import ca.ids.abms.modules.jobs.impl.JobAsyncLauncher;
import ca.ids.abms.modules.jobs.impl.JobParameters;
import ca.ids.abms.modules.jobs.impl.JobParametersBuilder;
import ca.ids.abms.modules.jobs.impl.JobSummary;
import ca.ids.abms.modules.reports2.common.ReportFormat;
import ca.ids.abms.modules.reports2.invoices.aviation.BillingInterval;
import ca.ids.abms.modules.reports2.invoices.iata.IataInvoiceItemOrder;
import ca.ids.abms.modules.usereventlogs.UserEventLogService;
import ca.ids.abms.modules.users.User;
import ca.ids.abms.modules.users.UserService;
import ca.ids.abms.util.MiscUtils;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/async-reports/aviation-invoice")
public class AsyncInvoiceGeneratorController {

    private static final Logger log = LoggerFactory.getLogger(AsyncInvoiceGeneratorController.class);

    private final JobAsyncLauncher jobAsyncLauncher;
    private final UserService userService;
    private final UserEventLogService userEventLogService;

    public AsyncInvoiceGeneratorController(final JobAsyncLauncher jobAsyncLauncher,
                                           final UserService userService,
                                           final UserEventLogService userEventLogService) {
        this.jobAsyncLauncher = jobAsyncLauncher;
        this.userService = userService;
        this.userEventLogService = userEventLogService;
    }

    /**
     *
     * Create aviation (non-IATA) invoice(s) for the given billing period.
     *
     * <H2>Usage</h2>
     *
     * <code><b>
     *      <pre>{GET,POST} /api/reports/aviation-invoice?param=value...</pre>
     * </b></code>
     *
     * <b>WARNING</b>: the GET method is deprecated, please use POST
     *
     * <p>
     * @param userBillingCenterOnly  - flights by user's billing centre or all flights (true/false)? optional
     * @param format                 - output format: pdf, xlsx, docx, csv, txt, xml, json; optional; defaults to pdf.
     * @param preview                - preview or commit (0/1)? optional; defaults to 0 (false). When preview is set:
     *                                   <ul>
     *                                      <li>the report will contain a "fake" invoice number (XXXXXX)
     *                                      <li>all database changes will be rolled back at the end
     *                                      <li>output will include records only from accounts that match accountIdList (see below)
     *                                   </ul>
     * @param accountIdList          - list of account IDs to be included in the report. This parameter is used <b>only</b> when
     *                                    <code>preview</code> is set to 1, and will be ignored otherwise, i.e., in the normal (non-preview)
     *                                    mode the report will include all relevant records.
     * @param billingInterval        - billing period monthly/weekly; optional
     * @param startDate              - start date for billing period; optional
     * @param endDateInclusive       - end date (inclusive) for billing period; optional
     *
     * @return the invoice document in the requested format. The response entity will have "Content-Type" and "Content-Disposition"
     *         (i.e., the suggested file name) headers set as necessary. The returned document may contain multiple invoices on separate
     *         pages (one invoice per account).
     */
    @PostMapping
    public ResponseEntity<Void> createAviationInvoice (
        @RequestParam final Boolean userBillingCenterOnly,
        @RequestParam final ReportFormat format,
        @RequestParam final Boolean preview,
        @RequestBody(required = false) final List<Integer> accountIdList,
        @RequestParam(required = false) final Integer flightCategory,
        @RequestParam BillingInterval billingInterval,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) final LocalDateTime endDateInclusive,
        @RequestParam(required = false, defaultValue = "false") Boolean iataInvoice,
        @RequestParam(required = false, defaultValue = "account,dateTime") final String sort,
        HttpServletRequest request
    ) {

        log.debug("Requested the creation of aviation {} invoices through the async job", iataInvoice ? "IATA" : "NON-IATA");

        final IataInvoiceItemOrder order = MiscUtils.nvl(IataInvoiceItemOrder.tryParse(sort), IataInvoiceItemOrder.ACCOUNT_DATETIME);

        final String ipAddress = userEventLogService.getIpAddressFromRequest(request);
        final User currentUser = userService.getUserByLogin (SecurityUtils.getCurrentUserLogin());

        final AsyncInvoiceGeneratorScope scope = new AsyncInvoiceGeneratorScope(userBillingCenterOnly,
            resolveReportFormat(preview, iataInvoice, format), preview, accountIdList, flightCategory,
            billingInterval, startDate, endDateInclusive, currentUser, ipAddress, order);

        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system"), true)
            .addParameter("scope", scope, false)
            .addParameter("iataInvoice", iataInvoice, false)
            .toJobParameters();

        HttpStatus httpStatus;
        if (jobAsyncLauncher.isAlreadyRunning(JobType.AVIATION_INVOICES, jobParameters)) {
            httpStatus = HttpStatus.OK;
        } else {
            try {
                // create job before running async launch to ensure it is created before returning
                // http status of ACCEPTED (HttpStatusCode:202) to any clients
                jobAsyncLauncher.create(JobType.AVIATION_INVOICES, jobParameters);
                jobAsyncLauncher.launch(JobType.AVIATION_INVOICES, jobParameters);
                httpStatus = HttpStatus.ACCEPTED;
            } catch (JobAlreadyRunningException jar) {
                httpStatus = HttpStatus.OK;
            }
        }
        return ResponseEntity.status(httpStatus).build();
    }

    @GetMapping
    public ResponseEntity<JobSummary> getStatus () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system"), true)
            .toJobParameters();
        log.debug("Get aviation invoice async status {}", jobParameters);

        final JobSummary resultSummary = jobAsyncLauncher.getStatus(JobType.AVIATION_INVOICES, jobParameters);

        return ResponseEntity.ok().body(resultSummary);
    }

    @SuppressWarnings("squid:S1452")
    @RequestMapping(value = "/download", method = RequestMethod.GET)
    public ResponseEntity<?> download (
        @RequestParam final ReportFormat format,
        @RequestParam final Boolean preview,
        @RequestParam final Boolean iata
    ) {
        final String currentUser = MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system");
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", currentUser, true)
            .toJobParameters();
        final JobSummary resultSummary = jobAsyncLauncher.getStatus(JobType.AVIATION_INVOICES, jobParameters);
        if (resultSummary.getJobExecutionStatus().equals(JobExecutionStatus.COMPLETED)) {

            // iata non-preview invoice generation is xlsx
            ReportFormat scopeFormat = resolveReportFormat(preview, iata, format);

            final Path filePath = AsyncInvoiceGeneratorPreviewWriter.getTempFile(currentUser, scopeFormat);

            if (filePath != null) {
                final File file = new File(filePath.toString());
                if (file.length() > 0) {
                    return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(scopeFormat.contentType()))
                        .contentLength(file.length())
                        .header("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"")
                        .body(new FileSystemResource(file));
                }
            }
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelCurrentJob () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(),"system"), true)
            .toJobParameters();
        log.debug("CANCEL a job with parameters {}", jobParameters);

        jobAsyncLauncher.abort(JobType.AVIATION_INVOICES, jobParameters);

        return ResponseEntity.ok().build();
    }

    /**
     * IATA invoice generation is forced in XLSX format ONLY.
     */
    private ReportFormat resolveReportFormat(final Boolean preview, final Boolean iata, final ReportFormat format) {
        return !preview && iata ? ReportFormat.xlsx : format;
    }
}
