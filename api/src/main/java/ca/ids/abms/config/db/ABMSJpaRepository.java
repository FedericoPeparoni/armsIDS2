package ca.ids.abms.config.db;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.google.common.collect.Iterables;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import ca.ids.abms.config.db.validators.UniqueKey;
import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.config.error.ErrorConstants;

@Transactional(readOnly = true)
public class ABMSJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements ABMSRepository<T, ID> {

    private final JpaEntityInformation<T, ID> entityInformation;
    private final EntityManager entityManager;

    public ABMSJpaRepository(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    public void refresh(T entity, LockModeType lockMode) {
        entityManager.refresh(entity, lockMode);
    }

    public void detach(T entity) {
        entityManager.detach(entity);
    }

    /**
     * This should only be used if concurrent updates are allowed and it is understood that the latest will over that a specific version wins the last one to persist
     * is saved as any modified data will be overwritten or ignored depending on the {@param replicationMode} outside
     * the context of the provided entity.
     */
    @Override
    @Transactional
    public T overwrite(T entity) {

        // much check for uniqueness constraint first
        checkTheUniqueness(entity);

        Session session = getCurrentSession();
        if (session != null && entityInformation.getId(entity) != null) {

            // must call pre update to support version and audited fields
            // that hibernate replicate method does not support
            preUpdate(entity);

            // use hibernate's replicate method to overwrite any existing changes
            session.replicate(entity, ReplicationMode.OVERWRITE);
        }

        // ensure entity is persisted in session context
        return super.save(entity);
    }

    @Override
    public Session getThisCurrentSession() {
        return getCurrentSession();
    }

    @Override
    @Transactional
    public <S extends T> S save(S entity) {
        checkTheUniqueness(entity);
        return super.save(entity);
    }

    @Override
    @Transactional
    public <S extends T> S saveAndFlush(S entity) {
        checkTheUniqueness(entity);
        return super.saveAndFlush(entity);
    }

    @Override
    public Page<T> findAll (final Pageable pageable, final String textToSearch, final FiltersList filters) {
        /* Standard findAll method */
        if (textToSearch == null && filters == null) {
            return super.findAll(pageable);
        }
        /* Enhanced findAll method */
        return super.findAll(new FiltersSpecification.Builder(textToSearch, filters).build(), pageable);
    }

    private <S extends T> void checkTheUniqueness(S entity) {
        final Class entityOriginalClass = super.getDomainClass();
        if (entityOriginalClass.isAnnotationPresent(UniqueKey.class)) {
            final UniqueKey uniqueKey = (UniqueKey) entityOriginalClass.getAnnotation(UniqueKey.class);
            final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
            final CriteriaQuery<Object> criteriaQuery = criteriaBuilder.createQuery();
            final Root<?> root = criteriaQuery.from(entityOriginalClass);
            final List<Predicate> predicates = new ArrayList<>(uniqueKey.columnNames().length);
            try {
                for (int i = 0; i < uniqueKey.columnNames().length; i++) {
                    final String propertyName = uniqueKey.columnNames()[i];
                    final PropertyDescriptor desc = new PropertyDescriptor(propertyName, entityOriginalClass);
                    final Method readMethod = desc.getReadMethod();
                    final Object propertyValue = readMethod.invoke(entity);
                    if (!uniqueKey.checkSeparately() || propertyValue != null) {
                        final Predicate predicate = criteriaBuilder.equal(root.get(propertyName), propertyValue);
                        predicates.add(predicate);
                    }
                }
            } catch (IntrospectionException | IllegalAccessException | InvocationTargetException ie) {
                throw new IllegalStateException("invalid use of the @UniqueKey annotation", ie);
            }
            Predicate whereCondition = null;
            if (uniqueKey.checkSeparately()) {
                whereCondition = criteriaBuilder.or(predicates.toArray(new Predicate[0]));
            } else {
                whereCondition = criteriaBuilder.and(predicates.toArray(new Predicate[0]));
            }
            criteriaQuery.select(root).where(whereCondition);
            final TypedQuery<Object> typedQuery = entityManager.createQuery(criteriaQuery);
            final FlushModeType currentFlushMode = entityManager.getFlushMode();
            entityManager.setFlushMode(FlushModeType.COMMIT);
            final List<Object> resultSet = typedQuery.getResultList();
            final Object currentId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
            entityManager.setFlushMode(currentFlushMode);
            if (currentId != null) {
                /* Updating an existing entity */
                final List<S> entities = (List<S>) resultSet;
                for (S existingEntity : entities) {
                    final Object existingId = entityManager.getEntityManagerFactory().getPersistenceUnitUtil()
                            .getIdentifier(existingEntity);
                    if (!currentId.equals(existingId)) {
                        throw new CustomParametrizedException(
                            ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                            ErrorConstants.ERR_UNIQUENESS_VIOLATION_DESC,
                            entityOriginalClass,
                            notNullUniqueKeys(entity, uniqueKey)
                        );
                    }
                }
            } else {
                /* Creating a new entity */
                if (!resultSet.isEmpty()) {
                    throw new CustomParametrizedException(
                        ErrorConstants.ERR_UNIQUENESS_VIOLATION,
                        ErrorConstants.ERR_UNIQUENESS_VIOLATION_DESC,
                        entityOriginalClass,
                        notNullUniqueKeys(entity, uniqueKey)
                    );
                }
            }
        }
    }

    /**
     * Iterates over an entities unique keys and returns all keys that have a value
     * The client uses the unique key field name to highlight the corresponding field
     * Null fields should not be highlighted when checking for uniqueness
     *
     * @param entity;
     * @param uniqueKey;
     * @return notNullUniqueKeys;
     */
    private <S extends T> String[] notNullUniqueKeys(S entity, UniqueKey uniqueKey) {
        ArrayList<String> notNullUniqueKeys = new ArrayList<>();
        Field field;

        for (String columnName : uniqueKey.columnNames()) {

            try {
                field = entity.getClass().getDeclaredField(columnName);

                // set private field accessible so it can be read
                field.setAccessible(true);

                if (field.get(entity) != null) {
                    // if the value is not null add the column name to the list of names to return
                    notNullUniqueKeys.add(columnName);
                }

            } catch (IllegalAccessException | NoSuchFieldException ignored) {
                // ignored
            }

        }

        return notNullUniqueKeys.toArray(new String[0]);
    }

    /**
     * Returns the current hibernate session.
     */
    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    /**
     * Call any method annotated with {@link PreUpdate} for the provided entity and bumps
     * any {@link Version} fields.
     *
     * Note: This should only be used within the {@link #overwrite(Object)} method
     * since it does not handle pre update and versioning logic.
     *
     * Limitation: Only supports fields annotated with {@link Version} and not methods annotated
     * with {@link Version}.
     */
    private void preUpdate(T entity) {

        // loop through class and all super classes
        Class<?> clazz = entity.getClass();
        do {
            preUpdate(entity, clazz);
        } while((clazz = clazz.getSuperclass()) != null);
    }

    /**
     * Call any method annotated with {@link PreUpdate} for the provided entity and bumps
     * any {@link Version} fields.
     *
     * Note: This should only be used within the {@link #overwrite(Object)} method
     * since it does not handle pre update and versioning logic.
     *
     * Limitation: Only supports fields annotated with {@link Version} and not methods annotated
     * with {@link Version}.
     */
    private void preUpdate(T entity, Class clazz) {

        // bump entity '@Version' property by one
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(Version.class)) {
                preUpdateVersion(entity, field);
            }
        }

        // run any '@PreUpdate' logic defined in entity
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.isAnnotationPresent(PreUpdate.class)) {
                preUpdateMethod(entity, method);
            }
        }
    }

    /**
     * Called from within {@link #preUpdate} when an entity's method is found to be annotated
     * with {@link PreUpdate}. This will imply invoice the method without any parameters.
     *
     * Any reflection access exceptions will be suppressed.
     */
    private void preUpdateMethod(T entity, Method method) {
        try {
            method.invoke(entity);
        } catch (IllegalAccessException | InvocationTargetException ignored) {
            // ignored, suppress any reflection access exceptions
        }
    }

    /**
     * Called from within {@link #preUpdate} when an entity's field is found to be annotated
     * with {@link Version}. This will incriminate the entity version field.
     *
     * Any reflection access exceptions will be suppressed.
     */
    private void preUpdateVersion(T entity, Field field) {
        try {
            Class<?> type = field.getType();
            if (type == Integer.class) {
                field.set(entity, preUpdateVersionMax(entity, field, Integer.class) + 1);
            } else if (type == Short.class) {
                field.set(entity, preUpdateVersionMax(entity, field, Short.class) + 1);
            } else if (type == Long.class) {
                field.set(entity, preUpdateVersionMax(entity, field, Long.class) + 1);
            } else if (type == Timestamp.class) {
                field.set(entity, Timestamp.valueOf(LocalDateTime.now()));
            } else {
                throw new IllegalStateException("Entity field annotated with @Version must be of type int, " +
                    "short, long, or timestamp");
            }
        } catch (IllegalAccessException ignored) {
            // ignored, suppress any reflection access exceptions
        }
    }

    /**
     * Return the max version for the provided entity's identifier value.
     */
    private <V extends Number> V preUpdateVersionMax(T entity, Field field, Class<V> clazz) throws IllegalAccessException {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<V> query = builder.createQuery(clazz);
        Root<T> root = query.from(getDomainClass());

        // only supports single id fields, composite keys have not been used
        String idName = Iterables.getFirst(entityInformation.getIdAttributeNames(), "id");
        ID idValue = entityInformation.getId(entity);

        // define query as the version column where identifier is equal to id field entity value
        query.select(builder.max(root.get(field.getName())))
            .where(builder.equal(root.get(idName), idValue));

        // query max version and return first result or fallback to entity value or default to 0
        List<V> results = entityManager.createQuery(query).getResultList();
        if (results == null || results.isEmpty()) {
            Object fieldValue = field.get(entity);
            return clazz.isInstance(fieldValue) ? clazz.cast(fieldValue) : clazz.cast(0);
        } else {
            return results.get(0);
        }
    }
}
