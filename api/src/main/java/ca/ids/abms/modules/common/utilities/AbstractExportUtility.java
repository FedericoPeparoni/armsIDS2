package ca.ids.abms.modules.common.utilities;

import ca.ids.abms.config.error.ErrorDTO;
import ca.ids.abms.config.error.ExceptionFactory;
import ca.ids.abms.config.error.FieldErrorDTO;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller utility to export all or select entities by id or object when
 * appropriate error messages for response if necessary.
 *
 * @param <E> exportable entity
 */
public abstract class AbstractExportUtility<E> {

    private final PlatformTransactionManager transactionManager;

    private final TransactionDefinition transactionDefinition;

    protected AbstractExportUtility(final PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.transactionDefinition = defaultTransactionDefinition();
    }

    /**
     * Export all exportable entities.
     *
     * @return error messages if any encountered
     */
    @Transactional
    public ErrorDTO exportAll() {

        // find all and filter out only exportable entities
        List<E> entities = findAllExportable();

        Integer errorCount = 0;
        for (E entity : entities) {

            // export entry and update error count if result not null
            FieldErrorDTO result = doExport(entity);
            if (result != null)
                errorCount++;
        }

        if (errorCount > 0)
            return new ErrorDTO
                .Builder(String.format("Failed to export %s out of %s exportable documents.",
                errorCount, entities.size()))
                .build();
        else
            return null;
    }

    /**
     * Export list of entities by primary identifier.
     *
     * @param ids select ids to export
     * @return error messages if any encountered
     */
    @Transactional
    public ErrorDTO exportList(final List<Integer> ids) {

        // assert that ids is not null or empty
        if (ids == null || ids.isEmpty())
            return null;

        // loop through each id and export
        ArrayList<FieldErrorDTO> errors = new ArrayList<>();
        for (Integer id : ids) {

            // export entry and add to errors if result not null
            FieldErrorDTO result = doExport(id);
            if (result != null)
                errors.add(result);
        }

        // if errors found, return new ErrorDTO with details
        if (errors.isEmpty())
            return null;
        else
            return new ErrorDTO
                .Builder("Could not export the following.")
                .addInvalidFields(errors)
                .build();
    }

    /**
     * Export entity to external system.
     *
     * @param entity exportable entity of type `E`
     */
    protected abstract void exportOne(E entity);

    /**
     * Return list of exportable entities.
     *
     * @return list of entities of type `E`
     */
    protected abstract List<E> findAllExportable();

    /**
     * Find entity by primary identifier value.
     *
     * @param id primary identifier
     * @return found entity of type `E`
     */
    protected abstract E findOneById(Integer id);

    /**
     * Class of exportable entity.
     *
     * @return class of type `E`
     */
    protected abstract Class<E> getEntityClass();

    /**
     * Human readable reference number used for identifying entities.
     *
     * @param entity exportable entity of type `E`
     * @return reference number
     */
    protected abstract String getReferenceNumber(E entity);

    private DefaultTransactionDefinition defaultTransactionDefinition() {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return defaultTransactionDefinition;
    }

    private FieldErrorDTO doExport(final Integer id) {

        // look for entity
        E entity = findOneById(id);

        // add not found error if it doesn't exists
        if (entity == null)
            return new FieldErrorDTO(getEntityClass().getSimpleName(), "Not Found",
                String.format("No document found with id '%s'.", id));

        // export found entity
        return doExport(entity);
    }

    private FieldErrorDTO doExport(final E entity) {

        // attempt to export entity
        // return root cause of any exception thrown
        try {
            doExportOne(entity);
        } catch (Exception ex) {
            return new FieldErrorDTO(getEntityClass().getSimpleName(), getReferenceNumber(entity),
                ExceptionFactory.resolveMainCause(ex).getLocalizedMessage());
        }

        // return null if successful as no error message to add
        return null;
    }

    private void doExportOne(final E entity) {

        // start a transaction definition that does not propagate upstream
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);

        try {

            // attempt to export entity
            exportOne(entity);

        } catch (Exception ex) {

            // rollback transaction on any exception before throwing
            transactionStatus.setRollbackOnly();
            throw ex;

        } finally {

            // commit transaction definition before continuing
            transactionManager.commit(transactionStatus);
        }
    }
}
