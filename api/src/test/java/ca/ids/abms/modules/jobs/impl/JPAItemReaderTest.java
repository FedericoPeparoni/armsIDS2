package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.ItemReader;
import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class JPAItemReaderTest {


    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query queryObject;

    private ItemReader<Integer> itemReader;

    private String query = "fake query";
    
    private String[] subQuery = {"fake subquery", "fake subquery_2"};

    private List<Integer> queryResults;

    private List<Integer> results;



    private JobParameters jobParameters = new JobParametersBuilder()
        .addParameter("year", "2017", false)
        .addParameter("month", "03", false)
        .toJobParameters();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        queryResults = new ArrayList<>();
        queryResults.add(1);
        queryResults.add(2);

        itemReader = new JPAItemReader<>(entityManagerFactory, query, subQuery, null, false);

        when(entityManagerFactory.createEntityManager())
            .thenReturn(entityManager);

        when(entityManager.createQuery(anyString())).thenReturn(queryObject);

        when(queryObject.getResultList()).thenReturn(queryResults);

    }

    @Test
    public void testReader() {
        when(entityManager.isOpen()).thenReturn(false);
        itemReader.open(jobParameters);
        when(entityManager.isOpen()).thenReturn(true);
        verify(entityManagerFactory).createEntityManager();
        verify(entityManager).createQuery(query);

        Iterable<Integer> results = itemReader.read();
        assertThat(results).isNotNull();
        assertThat(results).hasSize(2);

        itemReader.close();
        verify(entityManager).close();

    }
}
