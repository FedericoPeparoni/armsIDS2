package ca.ids.abms.modules.jobs.impl;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.EntityManagerFactoryUtils;

import ca.ids.abms.modules.jobs.ItemWriter;

public class JPAItemWriter<T> implements ItemWriter<T> {

    private final EntityManagerFactory entityManagerFactory;

    private EntityManager entityManager;

    private boolean isOpened = false;

    public JPAItemWriter(final EntityManagerFactory entityManagerFactory){
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void open() {
        if (!isOpened) {
            this.isOpened = true;
            this.entityManager = EntityManagerFactoryUtils.getTransactionalEntityManager(this.entityManagerFactory);
        }
    }

    @Override
    public void write(T item) {
        if (isOpened) {
            if (!entityManager.contains(item)) {
                entityManager.merge(item);
            }
        }
    }

    @Override
    public void close() {
        if (isOpened) {
            this.entityManager.flush();
            this.entityManager = null;
            this.isOpened = false;
        }
    }
}
