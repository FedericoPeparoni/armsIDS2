package ca.ids.abms.plugins.common.modules;

import ca.ids.abms.config.error.CustomParametrizedException;
import ca.ids.abms.modules.transactions.Transaction;
import ca.ids.abms.modules.transactions.TransactionPayment;
import ca.ids.abms.plugins.common.utilities.PluginExceptionHelper;
import ca.ids.spring.cache.annotations.CacheableOnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPluginTransactionCacheable {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractPluginTransactionCacheable.class);

    private final AbstractPluginTransactionService pluginTransactionService;

    public AbstractPluginTransactionCacheable(
        final AbstractPluginTransactionService pluginTransactionService
    ) {
        this.pluginTransactionService = pluginTransactionService;
    }

    /**
     * Cacheable Transaction wrapper for `createCreditNote` service method. This catches
     * all exceptions and throws as CacheableRuntimeException with necessary sql statements
     * if possible.
     *
     * @param transaction credit note transaction to create
     */
    @CacheableOnException(retry = true, exceptions = { CustomParametrizedException.class }, exclude = true)
    public void createCreditNote(final Transaction transaction) {
        try {
            // create necessary external records and throw exception if failed
            // else update transaction export status if successful
            if (pluginTransactionService.createCreditNoteCacheable(transaction))
                pluginTransactionService.exported(transaction);
        } catch (CustomParametrizedException ex) {
            throw ex; // throw custom parametrized exceptions back up the stack
        } catch (Exception ex) {
            // handle all other exceptions as cacheable runtime exception
            PluginExceptionHelper.logExportException(LOG, ex, transaction);
            throw PluginExceptionHelper.handleExportException(ex, pluginTransactionService
                .createCreditNoteStatement(transaction));
        }
    }

    /**
     * Cacheable Transaction wrapper for `createCreditPayment` service method. This catches
     * all exceptions and throws as CacheableRuntimeException with necessary sql statements
     * if possible.
     *
     * @param transactionPayment credit payment transaction to create
     */
    @CacheableOnException(retry = true, exceptions = { CustomParametrizedException.class }, exclude = true)
    public void createCreditPayment(final TransactionPayment transactionPayment) {
        try {
            // create necessary external records and throw exception if failed
            // else update transaction export status if successful
            if (pluginTransactionService.createCreditPaymentCacheable(transactionPayment))
                pluginTransactionService.exported(transactionPayment);
        } catch (CustomParametrizedException ex) {
            throw ex; // throw custom parametrized exceptions back up the stack
        } catch (Exception ex) {
            // handle all other exceptions as cacheable runtime exception
            PluginExceptionHelper.logExportException(LOG, ex, transactionPayment);
            throw PluginExceptionHelper.handleExportException(ex, pluginTransactionService
                .createCreditPaymentStatement(transactionPayment));
        }
    }
}
