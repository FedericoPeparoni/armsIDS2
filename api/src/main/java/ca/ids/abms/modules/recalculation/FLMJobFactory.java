package ca.ids.abms.modules.recalculation;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.flightmovements.FlightMovementService;
import ca.ids.abms.modules.flightmovements.enumerate.FlightMovementStatus;
import ca.ids.abms.modules.jobs.*;
import ca.ids.abms.modules.jobs.impl.*;
import ca.ids.abms.modules.reports2.async.*;
import com.google.common.base.Preconditions;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;

@Component
public class FLMJobFactory implements JobFactory {

    /**
     * Flight movement query by ids.
     */
    private static final String QUERY_FLM_BY_ID;

    /**
     * Flight movements query by start and end dates.
     */
    private static final String QUERY_FLM_BY_START_END_DATE;

    /**
     * Flight movements sub query by account ids.
     */
    private static final String[] QUERY_FLM_BY_START_END_DATE_SUB_QUERY;

    /**
     * Flight movement query sort order.
     */
    private static final String QUERY_FLM_SORT;

    private final JobInstanceService jobInstanceService;
    private final EntityManagerFactory entityManagerFactory;
    private final FlightMovementService flightMovementService;
    private final AsyncInvoiceGeneratorProcessor asyncInvoiceGeneratorProcessor;
    private final AsyncIataInvoiceGeneratorProcessor asyncIataInvoiceGeneratorProcessor;

    public FLMJobFactory(
        final JobInstanceService jobInstanceService,
        final EntityManagerFactory entityManagerFactory,
        final FlightMovementService flightMovementService,
        final AsyncInvoiceGeneratorProcessor asyncInvoiceGeneratorProcessor,
        final AsyncIataInvoiceGeneratorProcessor asyncIataInvoiceGeneratorProcessor
    ){
        this.jobInstanceService = jobInstanceService;
        this.entityManagerFactory = entityManagerFactory;
        this.flightMovementService = flightMovementService;
        this.asyncInvoiceGeneratorProcessor = asyncInvoiceGeneratorProcessor;
        this.asyncIataInvoiceGeneratorProcessor = asyncIataInvoiceGeneratorProcessor;
    }

    @SuppressWarnings({"squid:S00112", "squid:S2259"})
    public Job buildJob(final JobType jobType, final Integer jobExecutionId, final JobParameters jobParameters) {
        Preconditions.checkArgument(jobType != null && jobExecutionId != null);

        final Job job;
        switch (jobType) {
            case BULK_RECALCULATION:
                job = buildBulkRecalculationJob(jobExecutionId);
                break;
            case RESTRICTED_RECALCULATION:
                job = buildRestrictedRecalculationJob(jobExecutionId);
                break;
            case BULK_RECONCILIATION:
                job = buildBulkReconciliationJob(jobExecutionId);
                break;
            case RESTRICTED_RECONCILIATION:
                job = buildRestrictedReconciliationJob(jobExecutionId);
                break;
            case AVIATION_INVOICES:
                job = buildAviationInvoiceJob(jobExecutionId, jobParameters);
                break;
            default:
                throw new RuntimeException("Job type" + " " + jobType + " " + "not implemented");
        }
        return job;
    }

    static void validateParameters(LocalDateTime ldtStart, LocalDateTime ldtEnd) {
        LocalDateTime today = LocalDateTime.now();
        if (ldtStart.isAfter(ldtEnd)) {
            throw new CustomParametrizedException("Invalid start date" + " " + ldtStart, "start date should be before the end date");
        }
        else if (ldtStart.isAfter(today)) {
            throw new CustomParametrizedException("Future start date is not allowed");
        }
    }

    private Job buildBulkRecalculationJob (final Integer jobExecutionId) {
        final Step<Integer> step = new SimpleStep<>(getBulkReader(), getRecalculationProcessor());
        return new SimpleJob<>(jobInstanceService, jobExecutionId, step);
    }

    private Job buildBulkReconciliationJob (final Integer jobExecutionId) {
        final Step<Integer> step = new SimpleStep<>(getBulkReader(), getReconciliationProcessor());
        return new SimpleJob<>(jobInstanceService, jobExecutionId, step);
    }

    private Job buildRestrictedRecalculationJob (final Integer jobExecutionId) {
        final Step<Integer> step = new SimpleStep<>(getRestrictedReader(), getRecalculationProcessor());
        return new SimpleJob<>(jobInstanceService, jobExecutionId, step);
    }

    private Job buildRestrictedReconciliationJob (final Integer jobExecutionId) {
        final Step<Integer> step = new SimpleStep<>(getRestrictedReader(), getReconciliationProcessor());
        return new SimpleJob<>(jobInstanceService, jobExecutionId, step);
    }

    private Job buildAviationInvoiceJob (final Integer jobExecutionId, final JobParameters jobParameters) {
        Step<AsyncInvoiceGeneratorScope> step;
        final JobParameter param = jobParameters.getParameter("iataInvoice");
        if (param != null && Boolean.TRUE.equals(param.getValue())) {
            step = new AsyncInvoiceGeneratorStep(asyncIataInvoiceGeneratorProcessor, new AsyncInvoiceGeneratorPreviewWriter());
        } else {
            step = new AsyncInvoiceGeneratorStep(asyncInvoiceGeneratorProcessor, new AsyncInvoiceGeneratorPreviewWriter());
        }
        return new SimpleJob<>(jobInstanceService, jobExecutionId, step);
    }

    private ItemReader<Integer> getBulkReader() {
        return new JPAItemReader<>(entityManagerFactory, QUERY_FLM_BY_START_END_DATE, QUERY_FLM_BY_START_END_DATE_SUB_QUERY, QUERY_FLM_SORT, true);
    }

    private ItemReader<Integer> getRestrictedReader() {
        return new JPAItemReader<>(entityManagerFactory, QUERY_FLM_BY_ID, null, QUERY_FLM_SORT, true);
    }

    private ItemProcessor<Integer> getRecalculationProcessor() {
        return new FLMRecalculationProcessor(flightMovementService);
    }

    private ItemProcessor<Integer> getReconciliationProcessor() {
        return new FLMReconciliationProcessor(flightMovementService);
    }

    /* Define static queries by excluded flight movement status */
    static {

        // base query select statement
        String selectFrom = "SELECT fm.id FROM FlightMovement fm";

        // base queries for restricted and bulk jobs
        String whereIds = "WHERE fm.id IN :flightId";
        String whereStartEndDate = "WHERE fm.billingDate >= :startDate AND fm.billingDate < :endDate";

        // add additional restrictions by flight movement status
        if (!FLMJobConstant.INCLUDED_FLM_STATUS.isEmpty()) {
            StringBuilder queryBuilder = new StringBuilder("fm.status IN (");
            for (FlightMovementStatus status : FLMJobConstant.INCLUDED_FLM_STATUS) {
                if (status != FLMJobConstant.INCLUDED_FLM_STATUS.get(0)) {
                    queryBuilder.append(",");
                }
                queryBuilder.append("'");
                queryBuilder.append(status.toValue());
                queryBuilder.append("'");
            }
            queryBuilder.append(")");
            whereIds += " AND " + queryBuilder.toString();
            whereStartEndDate += " AND " + queryBuilder.toString();
        }

        // define query for flight movements by ids
        QUERY_FLM_BY_ID = selectFrom + " " + whereIds;

        // define query for flight movements by start and end dates
        QUERY_FLM_BY_START_END_DATE = selectFrom + " " + whereStartEndDate;

        // define conditional query accounts and flight movement category
        QUERY_FLM_BY_START_END_DATE_SUB_QUERY = new String[]{
            "AND fm.account.id IN (:accountIdList)",
            "AND fm.flightmovementCategory.id = :flightMovementCategoryId"
        };

        // define sort query for logical order
        QUERY_FLM_SORT = "ORDER BY fm.dateOfFlight, fm.depTime, fm.flightId, fm.id";
    }
}
