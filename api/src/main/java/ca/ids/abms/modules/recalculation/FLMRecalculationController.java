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
import ca.ids.abms.util.MiscUtils;

@RestController
@RequestMapping("/api/flm-job/recalculation")
@SuppressWarnings("unused")
public class FLMRecalculationController {

    private static final Logger LOG = LoggerFactory.getLogger(FLMRecalculationController.class);

    private final JobAsyncLauncher jobAsyncLauncher;

    @GetMapping(value = "/item")
    public ResponseEntity<JobSummary> getStatusRestrictedRecalculation () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        LOG.debug("Get FLM Recalculation Job status (single recalculation) with parameters {}", jobParameters);
        final JobSummary jobSummary = jobAsyncLauncher.getStatus(JobType.RESTRICTED_RECALCULATION, jobParameters);

        return ResponseEntity.ok().body(jobSummary);
    }

    @GetMapping
    public ResponseEntity<JobSummary> getStatusBulkRecalculation () {
        final JobParameters jobParameters = new JobParametersBuilder()
            .toJobParameters();
        LOG.debug("Get FLM Recalculation Job status (bulk recalculation): {}", jobParameters);

        final JobSummary jobSummary = jobAsyncLauncher.getStatus(JobType.BULK_RECALCULATION, jobParameters);

        return ResponseEntity.ok().body(jobSummary);
    }

    @PostMapping(value = "/item/{flightIdArray}")
    public ResponseEntity<Void> executeRestrictedRecalculation (@PathVariable final List<Integer> flightIdArray) {
        LOG.debug("Request to run the FLM Recalculation Job status for the FLM ID {}", flightIdArray);

        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .addParameter("flightId", flightIdArray, false)
            .toJobParameters();

        HttpStatus httpStatus;
        if (jobAsyncLauncher.isAlreadyRunning(JobType.RESTRICTED_RECALCULATION, jobParameters)) {
            httpStatus = HttpStatus.OK;
        } else {
            try {
                // create job before running async launch to ensure it is created before returning
                // http status of ACCEPTED (HttpStatusCode:202) to any clients
                jobAsyncLauncher.create(JobType.RESTRICTED_RECALCULATION, jobParameters);
                jobAsyncLauncher.launch(JobType.RESTRICTED_RECALCULATION, jobParameters);
                httpStatus = HttpStatus.ACCEPTED;
            } catch (JobAlreadyRunningException jar) {
                httpStatus = HttpStatus.OK;
            }
        }
        return ResponseEntity.status(httpStatus).build();
    }

    @PostMapping(value = "/{startDate}/{endDateInclusive}")
    public ResponseEntity<Void> executeBulkRecalculation  (@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
                                                           @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateInclusive,
                                                           @RequestBody(required = false) final List <Integer> accountIdList) {
        LOG.debug("Request to run the FLM Recalculation Job status for the period from {} to {}", startDate, endDateInclusive);

        LocalDateTime endDate = endDateInclusive.plusDays(1);

        FLMJobFactory.validateParameters(startDate, endDate);
        JobParameters jobParameters;
        if (accountIdList != null && !accountIdList.isEmpty()) {
            jobParameters = new JobParametersBuilder()
                .addParameter("startDate", startDate, false)
                .addParameter("endDate", endDate, false)
                .addOptionalParameter("accountIdList", accountIdList, false)
                .toJobParameters();
        } else {
            jobParameters = new JobParametersBuilder()
                .addParameter("startDate", startDate, false)
                .addParameter("endDate", endDate, false)
                .toJobParameters();
        }

        HttpStatus httpStatus;
        if (jobAsyncLauncher.isAlreadyRunning(JobType.BULK_RECALCULATION, jobParameters)) {
            httpStatus = HttpStatus.OK;
        } else {
            try {
                // create job before running async launch to ensure it is created before returning
                // http status of ACCEPTED (HttpStatusCode:202) to any clients
                jobAsyncLauncher.create(JobType.BULK_RECALCULATION, jobParameters);
                jobAsyncLauncher.launch(JobType.BULK_RECALCULATION, jobParameters);
                httpStatus = HttpStatus.ACCEPTED;
            } catch (JobAlreadyRunningException jar) {
                httpStatus = HttpStatus.OK;
            }
        }
        return ResponseEntity.status(httpStatus).build();
    }

    @DeleteMapping(value = "/item")
    public ResponseEntity<Void> cancelRestrictedRecalculation() {
        final JobParameters jobParameters = new JobParametersBuilder()
            .addParameter("user", MiscUtils.nvl(SecurityUtils.getCurrentUserLogin(), FLMJobConstant.DEFAULT_USER_NAME), true)
            .toJobParameters();
        LOG.debug("CANCEL an FLM Recalculation Job status (single recalculation) with parameters {}", jobParameters);

        jobAsyncLauncher.abort(JobType.RESTRICTED_RECALCULATION, jobParameters);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> cancelBulkRecalculation () {
        LOG.debug("CANCEL an FLM Recalculation Job status (bulk recalculation)");
        final JobParameters jobParameters = new JobParametersBuilder()
            .toJobParameters();
        jobAsyncLauncher.abort(JobType.BULK_RECALCULATION, jobParameters);

        return ResponseEntity.ok().build();
    }

    public FLMRecalculationController(final JobAsyncLauncher jobAsyncLauncher) {
        this.jobAsyncLauncher = jobAsyncLauncher;
    }
}
