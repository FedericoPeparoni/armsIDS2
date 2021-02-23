package ca.ids.abms.plugins.prototype.modules.transactions;

import ca.ids.abms.util.jdbc.PluginSqlStatement;
import ca.ids.abms.modules.system.SystemConfigurationService;
import ca.ids.abms.plugins.prototype.modules.system.PrototypeConfigurationItemName;
import ca.ids.abms.plugins.prototype.modules.transactionlog.PrototypeTransactionLog;
import ca.ids.abms.plugins.prototype.modules.transactionlog.PrototypeTransactionLogUtility;
import ca.ids.spring.cache.exceptions.CacheableRuntimeException;
import org.hibernate.PessimisticLockException;
import org.hibernate.QueryTimeoutException;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.LockAcquisitionException;
import org.hibernate.exception.LockTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ca.ids.abms.modules.translation.Translation;

import java.sql.SQLException;

@Service
public class PrototypeTransactionService {

    private static final Logger LOG = LoggerFactory.getLogger(PrototypeTransactionService.class);

    private SystemConfigurationService systemConfigurationService;
    private PrototypeTransactionUtility prototypeTransactionUtility;
    private PrototypeTransactionLogUtility prototypeTransactionLogUtility;

    public PrototypeTransactionService(PrototypeTransactionUtility prototypeTransactionUtility,
                                       PrototypeTransactionLogUtility prototypeTransactionLogUtility,
                                       SystemConfigurationService systemConfigurationService) {
        this.prototypeTransactionUtility = prototypeTransactionUtility;
        this.prototypeTransactionLogUtility = prototypeTransactionLogUtility;
        this.systemConfigurationService = systemConfigurationService;
    }

    /**
     * Attempt to mimic poor database connection for saving transactions.
     *
     * @param prototypeTransaction prototype transaction to save
     */
    @Transactional
    public void save(PrototypeTransaction prototypeTransaction) {
        LOG.debug("Attempting to 'save' with prototype transaction {} for mock plugin.", prototypeTransaction);

        // attempt to get severity from plugin configuration, default to 0.5
        double severity = 0.5d;
        try {
            severity = Double.parseDouble(systemConfigurationService
                .getValue(PrototypeConfigurationItemName.MOCK_FATAL_ERROR_OCCURRENCE_FREQUENCY)) / 100;
        } catch (NumberFormatException ignored) {
            LOG.warn("No {} configuration item set for prototype plugin. Using default value of {} percent.",
                PrototypeConfigurationItemName.MOCK_FATAL_ERROR_OCCURRENCE_FREQUENCY,
                severity * 100);
        }

        try {

            // here we would run necessary sql statements
            //PrototypeTransaction result = prototypeTransactionRepository.insert(prototypeTransaction);
            //prototypeTransactionLogService.save(new PrototypeTransactionLog(result.getId(), "abms"));

            // instead mock poor connection with configured severity level
            this.mockPoorConnection(severity);

        } catch (Exception ex) {
            Throwable cause = ex;
            // handle cacheable runtime exception as save expected to run two statements
            if (ex instanceof CacheableRuntimeException && ex.getCause() != null)
                cause = ex.getCause();
            throw new CacheableRuntimeException(PluginSqlStatement.sqlToMetadata(
                prototypeTransactionUtility.insertStatement(prototypeTransaction),
                prototypeTransactionLogUtility.insertStatement(
                    new PrototypeTransactionLog(-1, "abms"))), cause);
        }

        LOG.debug("Successfully completed 'save' with prototype transaction {} for mock plugin.", prototypeTransaction);
    }


    /**
     * `TransactionalCacheable` wrapper for `save` service method.
     *
     * @param prototypeTransaction prototype transaction to save
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveCacheable(PrototypeTransaction prototypeTransaction) {
        this.save(prototypeTransaction);
    }

    /**
     * Mimic poor database connection, severity is a number between 0 and 1. 0 being
     * no issues, and 1 being issues 100% of the time.
     *
     * @param severity number between 0 and 1.
     */
    private void mockPoorConnection(double severity) {
        severity = severity > 1 ? 1 : severity;
        severity = severity < 0 ? 0 : severity;
        double increment = severity / 5;
        double random = Math.random();
        if (random <=  increment)
            throw new JDBCConnectionException(Translation.getLangByToken("JDBC Connection Exception Example."), new SQLException());
        else if (random <= increment * 2)
            throw new LockAcquisitionException(Translation.getLangByToken("Lock Acquisition Exception Example."), new SQLException());
        else if (random <= increment * 3)
            throw new LockTimeoutException(Translation.getLangByToken("Lock Timeout Exception Example."), new SQLException());
        else if (random <= increment * 4)
            throw new PessimisticLockException(Translation.getLangByToken("Pessimistic Lock Exception Example."), new SQLException(), "SELECT 1");
        else if (random <= increment * 5)
            throw new QueryTimeoutException(Translation.getLangByToken("Query Timeout Exception Example."), new SQLException(), "SELECT 1");
    }

}
