package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.flightmovements.FlightMovement;
import ca.ids.abms.modules.jobs.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleJobTest {

    @Mock
    private ItemReader<FlightMovement> itemReader;

    @Mock
    private  ItemProcessor<FlightMovement> itemProcessor;

    @Mock
    private  ItemWriter<FlightMovement> itemWriter;

    private JobInstanceService jobInstanceServiceMocked;

    private Job job;

    private Step singleStep;

    private List<FlightMovement> results;

    @Mock
    private EntityManager entityManager;

    @Mock
    private JobInstanceRepository jobInstanceRepository;

    @Mock
    private JobExecutionRepository jobExecutionRepository;

    private JobParameters jobParameters = new JobParametersBuilder()
        .addParameter("year", "2017", false)
        .addParameter("month", "03", false)
        .toJobParameters();

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        FlightMovement firstMovement = new FlightMovement();
        firstMovement.setId(1);

        FlightMovement secondMovement = new FlightMovement();
        secondMovement.setId(2);


        results = new ArrayList<>();
        results.add(firstMovement);
        results.add(secondMovement);

        doNothing().when(itemReader).open(jobParameters);
        doNothing().when(itemReader).close();
        when(itemReader.read()).thenReturn(results);

        when(itemProcessor.processItem(firstMovement)).thenReturn(firstMovement);
        when(itemProcessor.processItem(secondMovement)).thenReturn(secondMovement);

        doNothing().when(itemWriter).open();
        doNothing().when(itemWriter).close();
        doNothing().when(itemWriter).write(any(FlightMovement.class));

        jobInstanceServiceMocked = new JobInstanceService(entityManager, jobInstanceRepository, jobExecutionRepository, 1) {

            @Override
            public void updateJobExecution(Integer jobExecutionId, StepSummary stepSummary) {
                assertThat(jobExecutionId).isEqualTo(1);
                assertThat(stepSummary).isNotNull();
                assertThat(stepSummary.getTotalItems()).isEqualTo(2);
            }
        };
    }

    @Test
    public void testExecutingJob() {
        singleStep = new SimpleStep(itemReader, itemProcessor, itemWriter);
        job = new SimpleJob(jobInstanceServiceMocked, 1, singleStep);
        job.executeSteps(jobParameters);
    }
}
