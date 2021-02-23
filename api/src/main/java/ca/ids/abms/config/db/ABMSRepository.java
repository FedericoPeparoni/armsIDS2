package ca.ids.abms.config.db;

import java.io.Serializable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import org.hibernate.Session;
import javax.persistence.LockModeType;

@NoRepositoryBean
public interface ABMSRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    void refresh(T entity);

    void refresh(T entity, LockModeType lockMode);

    void detach(T entity);

    T overwrite(T entity);

    Session getThisCurrentSession();

    Page<T> findAll (final Pageable pageable, final String textToSearch, final FiltersList filters);

    default Page<T> findAll (final Pageable pageable, final String textToSearch, final Filter<?> filter) {
        return findAll(pageable, textToSearch, new FiltersList.Builder(filter).build());
    }

    default Page<T> findAll (final Pageable pageable, final FiltersList filters) {
        return findAll(pageable, null, filters);
    }

    default Page<T> findAll (final Pageable pageable, final Filter<?> filter) {
        return findAll(pageable, null, filter);
    }

    default Page<T> findAll (final Pageable pageable, final String textToSearch) {
        return findAll(pageable, textToSearch, (FiltersList) null);
    }
}
