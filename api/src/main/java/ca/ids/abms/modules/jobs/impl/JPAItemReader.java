package ca.ids.abms.modules.jobs.impl;

import ca.ids.abms.modules.jobs.ItemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;


public class JPAItemReader<T> implements ItemReader<T> {

    private static final Logger LOG = LoggerFactory.getLogger(JPAItemReader.class);

    private final EntityManagerFactory entityManagerFactory;

    private static final ThreadLocal<EntityManager> threadLocalEntityManager;

    private final String query;

    private final String[] subQueries;

    private final String sortQuery;

    private JobParameters jobParameters;

    private Query queryObject;

    private boolean isOpen = false;

    private boolean detachableEntities;

    static {
        threadLocalEntityManager = new ThreadLocal<>();
    }

    public JPAItemReader(
        final EntityManagerFactory entityManagerFactory, final String query, final String[] subQueries,
        final String sortQuery, final boolean detachableEntities
    ) {
        this.query = query;
        this.subQueries = subQueries;
        this.sortQuery = sortQuery;
        this.entityManagerFactory = entityManagerFactory;
        this.detachableEntities = detachableEntities;
    }

    private EntityManager getEntityManager() {
        EntityManager entityManager = threadLocalEntityManager.get();
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
            threadLocalEntityManager.set(entityManager);
        }
        return entityManager;
    }

    private void closeEntityManager() {
        EntityManager em = threadLocalEntityManager.get();
        if (em != null) {
            em.close();
            threadLocalEntityManager.set(null);
        }
    }

    @Override
    public void open(final JobParameters jobParameters) {
        if (!isOpen) {
            LOG.debug("Open the reader; {}", jobParameters);

            this.isOpen = true;
            this.jobParameters = jobParameters;

            StringJoiner jobQuery = new StringJoiner(" ");

            jobQuery.add(query);

            if (jobParameters.hasOptionalParameters())
                jobQuery.add(getSubQuery());

            if (sortQuery != null)
                jobQuery.add(sortQuery);

            this.queryObject = this.getEntityManager().createQuery(jobQuery.toString());

            jobParameters.getParameters().forEach(
                jobParameter -> {
                    if (!jobParameter.getIdentifier()) {
                        queryObject.setParameter(jobParameter.getName(), jobParameter.getValue());
                    }
                }
            );

            jobParameters.getOptionalParameters().forEach(
                    jobParameter -> {
                        if (!jobParameter.getIdentifier()) {
                            queryObject.setParameter(jobParameter.getName(), jobParameter.getValue());
                        }
                    }
                );

        } else {
            LOG.warn("Reader already opened; {}", jobParameters);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterable<T> read() {
        if (isOpen) {
            LOG.debug("Reader started; {}", jobParameters);

            final List<T> results = new ArrayList<>();
            final List<T> queryResult = queryObject.getResultList();
            for (final T entity : queryResult) {
                if (detachableEntities && !(entity instanceof Number)) {
                    this.getEntityManager().detach(entity);
                }
                results.add(entity);
            }
            return results;
        } else {
            LOG.warn("Reader not opened yet; {}", jobParameters);
            return null;
        }
    }

    @Override
    public void close() {
        if (isOpen) {
            LOG.debug("Close the reader; {}", jobParameters);
            this.isOpen = false;
            this.queryObject = null;
            this.jobParameters = null;

            this.closeEntityManager();
        } else {
            LOG.warn("Reader already closed; {}", jobParameters);
        }
    }

    private String getSubQuery() {
        Collection<JobParameter> optionalJobParameters = jobParameters.getOptionalParameters();
        StringJoiner result = new StringJoiner(" ");
        for (JobParameter optionalJobParameter : optionalJobParameters) {
            for (String subQueryCurrent : subQueries) {
                if (subQueryCurrent.contains(optionalJobParameter.getName())) {          
                    result.add(subQueryCurrent);
                    break;
                }
            }
        }
        return result.toString();
    }
}
