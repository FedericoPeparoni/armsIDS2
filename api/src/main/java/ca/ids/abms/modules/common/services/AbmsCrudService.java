package ca.ids.abms.modules.common.services;

import ca.ids.abms.config.db.ABMSRepository;
import ca.ids.abms.config.db.FiltersSpecification;
import ca.ids.abms.config.error.ErrorConstants;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.modules.common.entities.AbmsCrudEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.io.Serializable;

public abstract class AbmsCrudService<T extends AbmsCrudEntity<I>, I extends Serializable> {

    private final ABMSRepository<T, I> repository;

    public AbmsCrudService(final ABMSRepository<T, I> repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<T> findAll(final String search, final Pageable pageable) {
        return repository.findAll(new FiltersSpecification.Builder(search).build(), pageable);
    }

    @Transactional(readOnly = true)
    public T findOne(final I id) {
        return repository.findOne(id);
    }

    @Transactional
    public T create(final T entity) {

        // ensure object id is null
        entity.setId(null);

        // persist and return created object
        return repository.save(entity);
    }

    @Transactional
    public T update(final I id, final T entity) {

        // validate that item exists before updating repository
        if (repository.getOne(id) == null) {
            throw ExceptionFactory.persistenceDataManagement(new EntityNotFoundException(),
                ErrorConstants.ERR_UPDATE_NO_LONGER_EXISTS);
        }

        // ensure object id matches
        entity.setId(id);

        // update entity in repository and return result
        return repository.save(entity);
    }

    @Transactional
    public void remove(final I id) {

        // validate that item exists before deleting
        if (repository.getOne(id) == null) {
            throw ExceptionFactory.persistenceDataManagement(new EntityNotFoundException(),
                ErrorConstants.ERR_DELETE_NO_LONGER_EXISTS);
        }

        // delete entity from repository
        repository.delete(id);
    }

    public long countAll(){
        return repository.count();
    }
}
