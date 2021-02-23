package ca.ids.abms.modules.recalculation;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.ids.abms.config.security.SecurityUtils;
import ca.ids.abms.modules.jobs.JobAlreadyRunningException;
import ca.ids.abms.modules.jobs.JobType;
import ca.ids.abms.modules.jobs.impl.JobAsyncLauncher;
import ca.ids.abms.modules.jobs.impl.JobParameters;
import ca.ids.abms.modules.jobs.impl.JobParametersBuilder;
import ca.ids.abms.modules.jobs.impl.JobSummary;
import ca.ids.abms.modules.jobs.impl.JobSummaryMapper;
import ca.ids.abms.modules.jobs.impl.JobSummaryViewModel;
import ca.ids.abms.util.MiscUtils;

@RestController
@RequestMapping("/api/flm-job/reconciliation")
@SuppressWarnings("unused")
public class FLMReconciliationController {

    private static final Logger LOG = LoggerFactory.getLogger(FLMReconciliationController.class);

    private final JobAsyncLauncher jobAsyncLauncher;

    private final JobSummaryMapper jobSummaryMapper;

    /***************************
     * NB!
     * Adding the current user to a job's parameters allows multiple users to run a job of that type
     * Not adding a user to a job's parameters makes a job of that type UNIQUE accross the application
     * unless other parameters are added
     */

    @GetMapping(value = "/item")
    public ResponseEntity<JobSummaryViewModel> getStatusRestrictedReconciliation () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        LOG.debug("Get FLM Reconciliation Job status (single reconciliation) with parameters {}", jobParameters);
        final JobSummary jobSummary = jobAsyncLauncher.getStatus(JobType.RESTRICTED_RECONCILIATION, jobParameters);

        return ResponseEntity.ok().body(jobSummaryMapper.toViewModel(jobSummary));
    }

    @GetMapping
    public ResponseEntity<JobSummaryViewModel> getStatusBulkReconciliation () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        LOG.debug("Get FLM Reconciliation Job status (bulk reconciliation)");

        final JobSummary jobSummary = jobAsyncLauncher.getStatus(JobType.BULK_RECONCILIATION, jobParameters);

        return ResponseEntity.ok().body(jobSummaryMapper.toViewModel(jobSummary));
    }

    @PostMapping(value = "/item/{flightIdArray}")
    public ResponseEntity<Void> executeRestrictedReconciliation (@PathVariable final List<Integer> flightIdArray) {
        LOG.debug("Request to run the FLM Reconciliation Job status for the FLM IDs {}", flightIdArray);

        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .addParameter("flightId", flightIdArray, false)
            .toJobParameters();

        HttpStatus httpStatus;
        if (jobAsyncLauncher.isAlreadyRunning(JobType.RESTRICTED_RECONCILIATION, jobParameters)) {
            httpStatus = HttpStatus.OK;
        } else {
            try {
                // create job before running async launch to ensure it is created before returning
                // http status of ACCEPTED (HttpStatusCode:202) to any clients
                jobAsyncLauncher.create(JobType.RESTRICTED_RECONCILIATION, jobParameters);
                jobAsyncLauncher.launch(JobType.RESTRICTED_RECONCILIATION, jobParameters);
                httpStatus = HttpStatus.ACCEPTED;
            } catch (JobAlreadyRunningException jar) {
                httpStatus = HttpStatus.OK;
            }
        }
        return ResponseEntity.status(httpStatus).build();
    }

    @PostMapping(value = "/{startDate}/{endDateInclusive}/")
    public ResponseEntity<Void> executeBulkReconciliation  (@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateInclusive,
                                                            @RequestBody(required = false) final List <Integer> accountIdList,
                                                            @RequestParam(required = false) final Integer flightMovementCategoryId) {
        LOG.debug("Request to run the FLM Reconciliation Job status for the period from {} to {}", startDate, endDateInclusive);

        LocalDateTime endDate = endDateInclusive.plusDays(1);

        FLMJobFactory.validateParameters(startDate, endDate);
        JobParametersBuilder builder =  new JobParametersBuilder();
        builder.addParameter("startDate", startDate, false);
        builder.addParameter("endDate", endDate, false);
        if (accountIdList != null && !accountIdList.isEmpty()) {
            builder.addOptionalParameter("accountIdList", accountIdList, false);
        }
        if (flightMovementCategoryId != null) {
            builder.addOptionalParameter("flightMovementCategoryId", flightMovementCategoryId, false);
        }
        builder.addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true);
        JobParameters jobParameters = builder.toJobParameters();

        HttpStatus httpStatus;
        if (jobAsyncLauncher.isAlreadyRunning(JobType.BULK_RECONCILIATION, jobParameters)) {
            httpStatus = HttpStatus.OK;
        } else {
            try {
                // create job before running async launch to ensure it is created before returning
                // http status of ACCEPTED (HttpStatusCode:202) to any clients
                jobAsyncLauncher.create(JobType.BULK_RECONCILIATION, jobParameters);
                jobAsyncLauncher.launch(JobType.BULK_RECONCILIATION, jobParameters);
                httpStatus = HttpStatus.ACCEPTED;
            } catch (JobAlreadyRunningException jar) {
                httpStatus = HttpStatus.OK;
            }
        }
        return ResponseEntity.status(httpStatus).build();
    }

    @DeleteMapping(value = "/item")
    public ResponseEntity<Void> cancelRestrictedReconciliation() {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        LOG.debug("CANCEL an FLM Recalculation Job status (single reconciliation) with parameters {}", jobParameters);

        jobAsyncLauncher.abort(JobType.RESTRICTED_RECONCILIATION, jobParameters);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelBulkReconciliation () {
        LOG.debug("CANCEL an FLM Recalculation Job status (bulk reconciliation)");
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        jobAsyncLauncher.abort(JobType.BULK_RECONCILIATION, jobParameters);

        return ResponseEntity.ok().build();
    }

    public FLMReconciliationController(final JobAsyncLauncher jobAsyncLauncher, JobSummaryMapper jobSummaryMapper) {
        this.jobAsyncLauncher = jobAsyncLauncher;
        this.jobSummaryMapper = jobSummaryMapper;
    }
}
